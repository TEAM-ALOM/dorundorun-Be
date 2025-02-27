package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.*;
import com.alom.dorundorunbe.domain.ranking.dto.RankingResponseDto;
import com.alom.dorundorunbe.domain.ranking.repository.RankingCacheRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRankingServiceTest {
    @Mock
    private UserRankingRepository userRankingRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private RankingCacheRepository rankingCacheRepository;

    @InjectMocks
    private UserRankingService userRankingService;

    private Ranking amateurRanking;
    private List<UserRanking> userRankings;

    private Set<ZSetOperations.TypedTuple<Object>> mockRankingSet;
    @BeforeEach
    void setUp() {
        // Ranking 생성 (Tier.AMATEUR)
        amateurRanking = Ranking.create(Tier.AMATEUR);
        ReflectionTestUtils.setField(amateurRanking, "id", 1L);

        userRankings = new ArrayList<>();
        mockRankingSet = new HashSet<>();

        // 10명의 UserRanking 객체 생성
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .nickname("User" + i)
                    .tier(Tier.AMATEUR)
                    .isRankingParticipated(true)
                    .build();

            ReflectionTestUtils.setField(user, "id", (long) i);

            UserRanking ur = UserRanking.create(user);
            ur.confirmRanking(amateurRanking);
            ReflectionTestUtils.setField(ur, "id", (long) i);

            // 각 사용자에게 3개의 포인트 추가 후 평균 계산
            ur.addPoint(10.0 + i);
            ur.addPoint(12.0 + i);
            ur.addPoint(14.0 + i);
            ur.updateAveragePoint();

            userRankings.add(ur);


            mockRankingSet.add(new DefaultTypedTuple<>(ur.getUser().getId(), ur.getAveragePoint()));
        }



    }



    private ZSetOperations.TypedTuple<Object> createTuple(Long userId, Double score) {
        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(userId);
        when(tuple.getScore()).thenReturn(score);
        return tuple;
    }

    @Test
    @DisplayName("새로운 사용자가 랭킹에 정상적으로 참가할 수 있다.")
    void createUserRanking_success() {

        User newUser = new User();
        ReflectionTestUtils.setField(newUser, "id", 11L);
        ReflectionTestUtils.setField(newUser, "tier", Tier.AMATEUR);

        // 초기 상태 검증: isRankingParticipated가 false여야 함
        assertThat(newUser.isRankingParticipated()).isFalse();


        userRankingService.createUserRanking(newUser, amateurRanking);


        ArgumentCaptor<UserRanking> captor = ArgumentCaptor.forClass(UserRanking.class);
        verify(userRankingRepository, times(1)).save(captor.capture());

        UserRanking savedRanking = captor.getValue();
        assertThat(savedRanking.getUser()).isEqualTo(newUser);
        assertThat(savedRanking.getRanking()).isEqualTo(amateurRanking);

        // isRankingParticipated가 true로 변경되었는지 검증
        assertThat(newUser.isRankingParticipated()).isTrue();

        // Redis에도 정상적으로 저장되었는지 검증
        verify(rankingCacheRepository, times(1)).saveUserRanking(eq(Tier.AMATEUR), eq(11L), eq(-1.0));
    }

    @Test
    @DisplayName("이미 랭킹에 참가한 사용자는 예외를 던진다.")
    void createUserRanking_alreadyParticipated() {
        // Given
        User existingUser = userRankings.get(0).getUser(); // 이미 참가한 사용자 선택



        assertThrows(BusinessException.class, () -> userRankingService.createUserRanking(existingUser, amateurRanking));

        verify(userRankingRepository, never()).save(any());
        verify(rankingCacheRepository, never()).saveUserRanking(any(), anyLong(), anyDouble());
    }


}