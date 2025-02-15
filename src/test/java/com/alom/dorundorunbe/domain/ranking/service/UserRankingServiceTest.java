package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.*;
import com.alom.dorundorunbe.domain.ranking.dto.RankingResponseDto;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.enums.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRankingServiceTest {

    @Mock
    private UserRankingRepository userRankingRepository;


    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private UserRankingService userRankingService;

    private Ranking amateurRanking;
    private List<UserRanking> userRankings = new ArrayList<>();
    private User testUser;

    @BeforeEach
    void setUp() {
        amateurRanking = Ranking.create(Tier.AMATEUR);

        //  10명의 유저 생성
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .id((long) i)
                    .nickname("User" + i)
                    .tier(Tier.AMATEUR)
                    .build();

            UserRanking userRanking = UserRanking.create(user);
            userRanking.confirmRanking(amateurRanking);


            // 각 사용자에게 3개의 포인트 추가
            userRanking.addPoint(10.0 + i);
            userRanking.addPoint(12.0 + i);
            userRanking.addPoint(14.0 + i);
            userRanking.updateAveragePoint();

            userRankings.add(userRanking);
        }


        testUser = userRankings.get(4).getUser();


        when(userRankingRepository.findByRankingId(amateurRanking.getId())).thenReturn(userRankings);


        userRankingService.updateGrades(amateurRanking.getId());
    }
    @Test
    @DisplayName("사용자 포인트 변경 후 등수 업데이트")
    void updateUserRankingPointAndNotify_ShouldUpdateRankingWhenUserPointChanges() {
        //기존 등수 확인
        userRankingService.updateGrades(amateurRanking.getId());

        UserRanking testUserRanking = userRankings.stream()
                .filter(ur -> ur.getUser().equals(testUser))
                .findFirst()
                .orElseThrow();

        Long initialRank = testUserRanking.getGrade();


        //기존 등수가 null이면 테스트 중단
        assertThat(initialRank).isNotNull();

        //User5의 포인트를 대폭 증가시켜 1등 만듦
        double newPoint = 30.0;
        testUserRanking.addPoint(newPoint);
        testUserRanking.updateAveragePoint();


        when(userRankingRepository.findByRankingId(amateurRanking.getId())).thenReturn(userRankings);


        userRankingService.updateGrades(amateurRanking.getId());


        List<UserRanking> updatedRankings = userRankingRepository.findByRankingId(amateurRanking.getId());
        UserRanking updatedUserRanking = updatedRankings.stream()
                .filter(ur -> ur.getUser().equals(testUser))
                .findFirst()
                .orElseThrow();

        Long updatedRank = updatedUserRanking.getGrade();


        //등수가 올라갔는지 확인
        assertThat(updatedRank).isNotNull();
        assertThat(updatedRank).isEqualTo(1); //1등

    }

    @Test
    @DisplayName("사용자 포인트 변경 후 등수 업데이트 및 웹소켓 전송 확인")
    void updateUserRankingPointAndNotify_ShouldUpdateRankingAndSendWebSocketMessage() {

        userRankingService.updateGrades(amateurRanking.getId());

        UserRanking testUserRanking = userRankings.stream()
                .filter(ur -> ur.getUser().equals(testUser))
                .findFirst()
                .orElseThrow();

        Long initialRank = testUserRanking.getGrade();

        assertThat(initialRank).isNotNull();


        double newPoint = 30.0;
        testUserRanking.addPoint(newPoint);
        testUserRanking.updateAveragePoint();


        when(userRankingRepository.findByRankingId(amateurRanking.getId())).thenReturn(userRankings);
        when(userRankingRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testUserRanking));


        userRankingService.updateUserRankingPointAndNotify(testUser.getId(), newPoint);


        userRankingService.updateGrades(amateurRanking.getId());


        List<UserRanking> updatedRankings = userRankingRepository.findByRankingId(amateurRanking.getId());
        UserRanking updatedUserRanking = updatedRankings.stream()
                .filter(ur -> ur.getUser().equals(testUser))
                .findFirst()
                .orElseThrow();

        Long updatedRank = updatedUserRanking.getGrade();


        assertThat(updatedRank).isNotNull();
        assertThat(updatedRank).isLessThan(initialRank);


        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/sub/ranking/" + amateurRanking.getId()), any(RankingResponseDto.class));
    }

}