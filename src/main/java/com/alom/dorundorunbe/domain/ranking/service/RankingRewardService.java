package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.repository.RankingCacheRepository;
import com.alom.dorundorunbe.domain.ranking.repository.RankingRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.global.exception.BusinessException;
import com.alom.dorundorunbe.global.exception.ErrorCode;
import com.alom.dorundorunbe.global.util.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingRewardService {//랭킹 보상 지급 로직
    private final RankingRepository rankingRepository;
    private final PointService pointService;
    private final UserRankingRepository userRankingRepository;
    private final RankingCacheRepository rankingCacheRepository;

    @Transactional 
    public void processWeeklyRewards() {
        List<Ranking> rankings = rankingRepository.findAll();
        for (Ranking ranking : rankings) {
            pointService.giveRankingRewardToUsersByRanking(ranking.getId());
            deleteRankingRecords(ranking.getId());
            rankingCacheRepository.deleteTierRanking(ranking.getTier());
        }
    }

    public void deleteRankingRecords(Long rankingId) {
        if (!rankingRepository.existsById(rankingId)) {
            throw new BusinessException(ErrorCode.RANKING_NOT_FOUND);
        }
        userRankingRepository.deleteByRankingId(rankingId);
    }
}
