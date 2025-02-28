package com.alom.dorundorunbe.domain.ranking.util;

import com.alom.dorundorunbe.global.enums.Tier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingCacheKeyUtil {

    private static final String RANKING_ZSET_PREFIX = "ranking:zset:";

    public static String getTierRankingKey(Tier tier) {
        return RANKING_ZSET_PREFIX + tier.name().toLowerCase();
    }
}
