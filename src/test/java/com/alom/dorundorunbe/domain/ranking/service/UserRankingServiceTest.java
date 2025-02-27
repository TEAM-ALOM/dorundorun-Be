package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.*;
import com.alom.dorundorunbe.domain.ranking.dto.RankingResponseDto;
import com.alom.dorundorunbe.domain.ranking.dto.RankingSocketDto;
import com.alom.dorundorunbe.domain.ranking.dto.RankingSocketUserDto;
import com.alom.dorundorunbe.domain.ranking.repository.RankingCacheRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.ranking.util.RankingCacheKeyUtil;
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

    @Test
    @DisplayName("포인트가 1개에서 2개일 경우, 업데이트 없이 리턴한다")
    void updateUserRankingPointAndNotify_ShouldNotUpdate_WhenPointCountRemainsBelowThree() {

        User user = new User();
        user.setId(101L);
        user.setTier(Tier.AMATEUR);
        UserRanking ur = UserRanking.create(user);
        ur.confirmRanking(amateurRanking);
        // 1개 포인트만 추가
        ur.addPoint(15.0);
        when(userRankingRepository.findByUserId(101L)).thenReturn(Optional.of(ur));

        // When: 포인트 업데이트 호출 (총 포인트 = 2개 -> avgPoint 계산 하면 안됨)
        userRankingService.updateUserRankingPointAndNotify(101L, 12.0);


        assertThat(ur.getPoints().size()).isEqualTo(2);
        assertThat(ur.getAveragePoint()).isNull();

        verify(rankingCacheRepository, never()).saveUserRanking(any(), anyLong(), anyDouble());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(RankingSocketDto.class));
        Double averagePoint = ur.getAveragePoint();
        assertThat(averagePoint).isNull();
    }

    @Test
    @DisplayName("포인트가 2개에서 3개가 될 경우, 평균 변화 발생 시 업데이트 및 웹소켓 전송이 발생한다")
    void updateUserRankingPointAndNotify_ShouldUpdateAndSendWebSocket_WhenThirdPointIsAdded() {

        User user = new User();
        user.setId(102L);
        user.setNickname("TestUser");
        user.setTier(Tier.AMATEUR);
        UserRanking ur = UserRanking.create(user);
        ur.confirmRanking(amateurRanking);
        // 2개 포인트만 추가
        ur.addPoint(15.0);
        ur.addPoint(20.0);

        when(userRankingRepository.findByUserId(user.getId())).thenReturn(Optional.of(ur));



        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(user.getId());

        when(tuple.getScore()).thenReturn(20.0);

        when(rankingCacheRepository.getTierRanking(Tier.AMATEUR)).thenReturn(Set.of(tuple));

        // When: 3번째 포인트 추가 → 총 3개가 되어 평균 점수가 계산되어야 함
        userRankingService.updateUserRankingPointAndNotify(user.getId(), 25.0);


        assertThat(ur.getPoints().size()).isEqualTo(3);
        assertThat(ur.getAveragePoint()).isNotNull();
        assertThat(ur.getAveragePoint()).isEqualTo(20.0);
        // 캐시 업데이트가 1회 호출되어야 함
        verify(rankingCacheRepository, times(1))
                .saveUserRanking(eq(Tier.AMATEUR), eq(user.getId()), anyDouble());


        ArgumentCaptor<RankingSocketDto> captor = ArgumentCaptor.forClass(RankingSocketDto.class);
        verify(messagingTemplate, times(1))
                .convertAndSend(anyString(), captor.capture());

        RankingSocketDto sentDto = captor.getValue();
        // 전달된 DTO에 포함된 사용자 목록에서, 해당 사용자의 평균 점수가 20.0인지 확인
        boolean found = sentDto.getParticipants().stream()
                .anyMatch(dto -> dto.getUserId().equals(user.getId()) && dto.getAveragePoint().equals(20.0));
        assertThat(found).isTrue();

        verify(userRankingRepository, never()).findByRankingId(anyLong());
    }


    @Test
    @DisplayName("포인트가 3개 이상인 상태에서 새 포인트가 기존 3개보다 작아 평균에 변화가 없으면 업데이트 없이 리턴한다")
    void updateUserRankingPointAndNotify_ShouldNotUpdate_WhenAverageRemainsUnchanged() {

        User user = new User();
        user.setId(103L);
        user.setNickname("TestUser");
        user.setTier(Tier.AMATEUR);
        UserRanking ur = UserRanking.create(user);
        ur.confirmRanking(amateurRanking);
        ur.addPoint(10.0);
        ur.addPoint(20.0);
        ur.addPoint(30.0);
        ur.updateAveragePoint();
        when(userRankingRepository.findByUserId(user.getId())).thenReturn(Optional.of(ur));


        userRankingService.updateUserRankingPointAndNotify(user.getId(), 5.0);

        // 5는 기존 3개보다 작은 포인트이므로 그대로
        Double avgBefore = 20.0; // (10+20+30)/3 = 20
        assertThat(ur.getAveragePoint()).isEqualTo(avgBefore);
        // 업데이트(캐시 저장, 웹소켓 전송) 발생하지 않아야 함
        verify(rankingCacheRepository, never()).saveUserRanking(any(), anyLong(), anyDouble());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(RankingSocketDto.class));
    }

    @Test
    @DisplayName("포인트가 3개 이상인 상태에서 새 포인트 추가로 평균이 변화하면 업데이트가 발생한다")
    void updateUserRankingPointAndNotify_ShouldUpdate_WhenAverageChanges() {

        User user = new User();
        user.setId(103L);
        user.setNickname("TestUser");
        user.setTier(Tier.AMATEUR);
        UserRanking ur = UserRanking.create(user);
        ur.confirmRanking(amateurRanking);
        ur.addPoint(10.0);
        ur.addPoint(20.0);
        ur.addPoint(30.0);
        // 평균 계산: 20.0
        ur.updateAveragePoint();
        when(userRankingRepository.findByUserId(user.getId())).thenReturn(Optional.of(ur));



        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(user.getId());
        when(tuple.getScore()).thenReturn(20.0);
        when(rankingCacheRepository.getTierRanking(Tier.AMATEUR)).thenReturn(Set.of(tuple));

        // When: 새 포인트 40.0이 추가되면, 내부적으로 [10,20,30,40]이 되고, 상위 3개는 [40,30,20]이 되어 평균은 30.0으로 변경됨
        userRankingService.updateUserRankingPointAndNotify(user.getId(), 40.0);

        // Then:
        // 포인트가 4개가 추가되지만 내부적으로 최고 성적 3개만 유지되어야 하므로 개수는 3개
        assertThat(ur.getPoints().size()).isEqualTo(3);
        // 평균이 30.0으로 업데이트되어야 함
        assertThat(ur.getAveragePoint()).isEqualTo(30.0);
        // 캐시 업데이트(saveUserRanking)가 1회 호출되어야 함
        verify(rankingCacheRepository, times(1))
                .saveUserRanking(eq(Tier.AMATEUR), eq(user.getId()), anyDouble());
        // 웹소켓 전송(convertAndSend)도 1회 호출되어야 함
        verify(messagingTemplate, times(1))
                .convertAndSend(anyString(), any(RankingSocketDto.class));

        verify(userRankingRepository, never()).findByRankingId(anyLong());
    }

    @Test
    @DisplayName("grade가 -1인 참가자가 null로 처리되는지 검증")
    void updateTierRankingAndNotify_ShouldConvertMinusOneToNull() {
        // Given: 10명의 사용자가 존재하며 초기 점수가 -1.0
        Ranking ranking = Ranking.create(Tier.AMATEUR);
        ReflectionTestUtils.setField(ranking, "id", 1L);

        Set<ZSetOperations.TypedTuple<Object>> rankingSet = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            rankingSet.add(createTuple((long) i, -1.0)); // 모든 점수를 -1로 저장(zset정렬 위함)
        }

        when(rankingCacheRepository.getTierRanking(Tier.AMATEUR)).thenReturn(rankingSet);

        // When: updateTierRankingAndNotify 호출
        userRankingService.updateTierRankingAndNotify(1L, Tier.AMATEUR);


        ArgumentCaptor<RankingSocketDto> captor = ArgumentCaptor.forClass(RankingSocketDto.class);
        verify(messagingTemplate).convertAndSend(anyString(), captor.capture());

        List<RankingSocketUserDto> participants = captor.getValue().getParticipants();
        for (RankingSocketUserDto dto : participants) {
            assertThat(dto.getAveragePoint()).isNull();
        }
    }

    @Test
    @DisplayName("사용자 10명 중 특정 사용자의 점수가 급등하여 1등이 되었을 때 전체 순위가 올바르게 업데이트되는지 검증")
    void testRankingUpdateWhenUserScoreIncreases() {

        when(rankingCacheRepository.getTierRanking(Tier.AMATEUR)).thenReturn(mockRankingSet);
        when(userRankingRepository.findByUserId(5L)).thenReturn(Optional.of(userRankings.get(4))); // 5번 사용자

        // 기존 1등 확인
        Long previousTopUserId = mockRankingSet.stream()
                .max(Comparator.comparingDouble(ZSetOperations.TypedTuple::getScore))
                .map(ZSetOperations.TypedTuple::getValue)
                .map(val -> (Long) val)
                .orElseThrow();


        userRankingService.updateUserRankingPointAndNotify(5L, 200.0);


        assertThat(rankingCacheRepository.getTierRanking(Tier.AMATEUR))
                .isNotEmpty()
                .anyMatch(tuple -> (Long) tuple.getValue() == 5L && tuple.getScore() > previousTopUserId);


        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/sub/ranking/" + RankingCacheKeyUtil.getTierRankingKey(Tier.AMATEUR)),
                any(RankingSocketDto.class)
        );
    }




}