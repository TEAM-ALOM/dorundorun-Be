package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.ranking.repository.RankingCacheRepository;
import com.alom.dorundorunbe.domain.ranking.repository.RankingRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingRewardServiceTest {

    @InjectMocks
    private RankingRewardService rankingRewardService;

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private UserRankingRepository userRankingRepository;

    @Mock
    private PointService pointService;

    @Mock
    private RankingCacheRepository rankingCacheRepository;

    private final Long rankingId1 = 1L;
    private final Long rankingId2 = 2L;

    private Ranking ranking1, ranking2;

    @BeforeEach
    void setUp() {
        ranking1 = Ranking.builder().id(rankingId1).tier(Tier.BEGINNER).build();
        ranking2 = Ranking.builder().id(rankingId2).tier(Tier.AMATEUR).build();
    }

    @Test
    @DisplayName("주간 보상 지급이 정상적으로 이루어지는지 검증")
    void testProcessWeeklyRewards_Success() {

        when(rankingRepository.findAll()).thenReturn(List.of(ranking1, ranking2));
        when(rankingRepository.existsById(rankingId1)).thenReturn(true);
        when(rankingRepository.existsById(rankingId2)).thenReturn(true);


        rankingRewardService.processWeeklyRewards();


        verify(pointService, times(1)).giveRankingRewardToUsersByRanking(rankingId1);
        verify(pointService, times(1)).giveRankingRewardToUsersByRanking(rankingId2);


        verify(userRankingRepository, times(1)).deleteByRankingId(rankingId1);
        verify(userRankingRepository, times(1)).deleteByRankingId(rankingId2);


        verify(rankingCacheRepository, times(1)).deleteTierRanking(Tier.BEGINNER);
        verify(rankingCacheRepository, times(1)).deleteTierRanking(Tier.AMATEUR);
    }

}
