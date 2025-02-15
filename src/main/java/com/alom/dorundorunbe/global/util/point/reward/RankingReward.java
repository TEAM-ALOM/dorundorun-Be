package com.alom.dorundorunbe.global.util.point.reward;

import com.alom.dorundorunbe.global.enums.Tier;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RankingReward {
    FIRST_PLACE(1L, 1000, 30),
    SECOND_PLACE(2L, 500, 20),
    THIRD_PLACE(3L, 300, 10),
    FOURTH_PLACE(4L, 100, 5),
    FIFTH_PLACE(5L, 50, 2),
    NO_REWARD(null, 0, 0);

    private final Long grade;
    private final long baseCash;
    private final double baseLp;

    RankingReward(Long grade, long baseCash, double baseLp) {
        this.grade = grade;
        this.baseCash = baseCash;
        this.baseLp = baseLp;
    }

    public static RankingReward getRewardByGrade(Long grade) {
        return Arrays.stream(values())
                .filter(reward -> reward.grade == grade)
                .findFirst()
                .orElse(NO_REWARD);
    }

    public long calculateCash(Tier tier) {
        return Math.round(baseCash * tier.getCashMultiplier());
    }

    public double calculateLp(Tier tier) {
        return baseLp * tier.getLpMultiplier();
    }
}

