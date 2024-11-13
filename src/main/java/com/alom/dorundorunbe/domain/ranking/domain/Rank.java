package com.alom.dorundorunbe.domain.ranking.domain;

public enum Rank {
    STARTER("스타터"),
    BEGINNER("비기너"),
    AMATEUR("아마추어"),
    PRO("프로");

    private final String rank;

    Rank(String rank) {
        this.rank = rank;
    }
}
