package com.alom.dorundorunbe.domain.ranking.domain;


import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;



@Entity @Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ranking")
public class Ranking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "ranking", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    @Builder.Default
    private List<UserRanking> participants = new ArrayList<>(); // 랭킹 참가자 목록

    @Enumerated(EnumType.STRING)
    private Tier tier;



    public void addParticipant(UserRanking userRanking){
        participants.add(userRanking);
    }

    public static Ranking create(Tier tier) {
        return Ranking.builder()
                .tier(tier)
                .build();
    }



}
