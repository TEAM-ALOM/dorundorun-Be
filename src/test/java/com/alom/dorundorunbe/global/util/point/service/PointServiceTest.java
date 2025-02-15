package com.alom.dorundorunbe.global.util.point.service;

import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.user.domain.User;

import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.point.reward.RankingReward;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserRankingRepository userRankingRepository;


    private User user1, user2, user3, user4, user5, user6;
    private UserRanking ranking1, ranking2, ranking3, ranking4, ranking5, rankingNull;
    private final Long rankingId = 1L;

    @BeforeEach
    void setUp() {

        user1 = User.builder().id(1L).nickname("User1").tier(Tier.AMATEUR).cash(0L).lp(0.0).isRankingParticipated(true).build();
        user2 = User.builder().id(2L).nickname("User2").tier(Tier.AMATEUR).cash(0L).lp(0.0).isRankingParticipated(true).build();
        user3 = User.builder().id(3L).nickname("User3").tier(Tier.AMATEUR).cash(0L).lp(0.0).isRankingParticipated(true).build();
        user4 = User.builder().id(4L).nickname("User4").tier(Tier.AMATEUR).cash(0L).lp(0.0).isRankingParticipated(true).build();
        user5 = User.builder().id(5L).nickname("User5").tier(Tier.AMATEUR).cash(0L).lp(0.0).isRankingParticipated(true).build();
        user6 = User.builder().id(6L).nickname("User6").tier(Tier.AMATEUR).cash(0L).lp(0.0).isRankingParticipated(true).build();

        ranking1 = UserRanking.builder().id(1L).user(user1).grade(1L).build(); // 1등
        ranking2 = UserRanking.builder().id(2L).user(user2).grade(2L).build(); // 2등
        ranking3 = UserRanking.builder().id(3L).user(user3).grade(3L).build(); // 3등
        ranking4 = UserRanking.builder().id(4L).user(user4).grade(4L).build(); // 4등
        ranking5 = UserRanking.builder().id(5L).user(user5).grade(5L).build(); // 5등
        rankingNull = UserRanking.builder().id(6L).user(user6).grade(null).build(); //등수 null

        List<UserRanking> userRankings = List.of(ranking1, ranking2, ranking3, ranking4, ranking5, rankingNull);


        when(userRankingRepository.findWithUserByRankingId(rankingId)).thenReturn(userRankings);
    }

    @Test
    @DisplayName("6명의 사용자에게 등수에 맞는 보상이 지급되며, null(6번째 사용자) 등수는 보상을 받지 않음")
    void testGiveRankingRewardToUsersByRanking() {
        // When (랭킹 보상 지급 메서드 실행)
        pointService.giveRankingRewardToUsersByRanking(rankingId);

        // Then (검증)
        assertReward(user1, 1L, (long) (1000 * user1.getTier().getCashMultiplier()), 30 * user1.getTier().getLpMultiplier());  // 1등
        assertReward(user2, 2L, (long) (500 * user2.getTier().getCashMultiplier()), 20 * user2.getTier().getLpMultiplier());   // 2등
        assertReward(user3, 3L, (long) (300 * user3.getTier().getCashMultiplier()), 10 * user3.getTier().getLpMultiplier());   // 3등
        assertReward(user4, 4L, (long) (100 * user4.getTier().getCashMultiplier()), 5 * user4.getTier().getLpMultiplier());    // 4등
        assertReward(user5, 5L, (long) (50 * user5.getTier().getCashMultiplier()), 2 * user5.getTier().getLpMultiplier());     // 5등
        assertReward(user6, null, 0, 0);    // null 등 : 보상 없음
    }

    private void assertReward(User user, Long grade, long expectedCash, double expectedLp) {
        RankingReward reward = RankingReward.getRewardByGrade(grade);
        long calculatedCash = reward.calculateCash(user.getTier());
        double calculatedLp = reward.calculateLp(user.getTier());

        // 기대 값과 실제 계산된 값 비교
        assertThat(calculatedCash).isEqualTo(expectedCash);
        assertThat(calculatedLp).isEqualTo(expectedLp);


        // 사용자의 보상 상태 확인
        assertThat(user.getCash()).isEqualTo(expectedCash);
        assertThat(user.getLp()).isEqualTo(expectedLp);
    }
}