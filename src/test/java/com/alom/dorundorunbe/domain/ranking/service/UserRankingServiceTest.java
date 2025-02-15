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
}