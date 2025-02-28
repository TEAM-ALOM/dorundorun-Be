package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.repository.RankingCacheRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.global.enums.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingSyncService {//캐쉬 db 동기화


    private final RankingCacheRepository rankingCacheRepository;
    private final UserRankingRepository userRankingRepository;


    @Transactional
    public void syncRankingForTier(Tier tier) {
        Set<ZSetOperations.TypedTuple<Object>> rankingSet = rankingCacheRepository.getTierRanking(tier);
        if (rankingSet == null || rankingSet.isEmpty()) { // 동기화 할 캐쉬가 없다면 리턴
            return;
        }

        long counter = 0;
        Long rank = null;
        Double previousScore = null;


        for (ZSetOperations.TypedTuple<Object> tuple : rankingSet) {
            counter++;
            Long userId = (Long) tuple.getValue();
            Double avgScore = tuple.getScore();


            avgScore = (avgScore != null) ? avgScore : -1.0;


            if (avgScore == -1.0) {
                rank = null;
            } else {

                if (previousScore == null || !avgScore.equals(previousScore)) {
                    rank = counter;
                }
            }


            Long finalRank = (avgScore == -1.0) ? null : rank;
            userRankingRepository.findByUserId(userId).ifPresent(userRanking -> {
                if (userRanking.getGrade() == null || !userRanking.getGrade().equals(finalRank)) {
                    userRanking.updateGrade(finalRank);
                }
            });
            previousScore = avgScore;
        }
    }


}
