package com.alom.dorundorunbe.domain.ranking.repository;

import com.alom.dorundorunbe.domain.ranking.util.RankingCacheKeyUtil;
import com.alom.dorundorunbe.global.enums.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RankingCacheRepositoryImpl implements RankingCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveUserRanking(Tier tier, Long userId, Double avgPoint) {
        String tierKey = RankingCacheKeyUtil.getTierRankingKey(tier);
        redisTemplate.opsForZSet().add(tierKey, userId, avgPoint);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getTierRanking(Tier tier) {
        String tierKey = RankingCacheKeyUtil.getTierRankingKey(tier);
        return redisTemplate.opsForZSet().reverseRangeWithScores(tierKey, 0, -1);
    }



    @Override
    public void deleteTierRanking(Tier tier) {
        String tierKey = RankingCacheKeyUtil.getTierRankingKey(tier);
        redisTemplate.delete(tierKey);
    }
}
