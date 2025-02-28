package com.alom.dorundorunbe.domain.ranking.repository;

import com.alom.dorundorunbe.global.enums.Tier;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface RankingCacheRepository {

    // 사용자 점수를 Redis Sorted Set에 저장
    void saveUserRanking(Tier tier, Long userId, Double avgPoint);

    // 특정 티어의 랭킹 정보를 조회 (내림차순 정렬)
    Set<ZSetOperations.TypedTuple<Object>> getTierRanking(Tier tier);



    // 특정 티어의 모든 랭킹 데이터 삭제
    void deleteTierRanking(Tier tier);
}