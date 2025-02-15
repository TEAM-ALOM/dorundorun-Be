package com.alom.dorundorunbe.domain.user.domain;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity @Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 로그인 시 받아올 정보 -> 앱 내에서 사용자 식별에 사용
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean isDeleted;

    // 앱 내에서 사용할 닉네임 (ㅇㅇ두)
    @Column(nullable = false, unique = true, length = 32)
    private String nickname;

    // 현재 참가 중인 랭킹
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    @JsonIgnore
    private Ranking ranking;

    // 사용자가 소유한 재화
    @Column(nullable = false)
    private Long cash;

    // BACKGROUND 업적일 때 사용 (유저의 티어와 매칭)
    @Enumerated(EnumType.STRING)
    private Tier tier;

    // BACKGROUND 업적일 때 보상 배경
    @Column(length = 64)
    private String background;

    // 랭킹 포인트
    private double lp;


    @Column(nullable = false)
    private boolean isRankingParticipated;

    @Column
    private LocalDateTime rankingParticipationDate; // 랭킹 참가 시작 날짜

    public void startRankingParticipation() {
        this.rankingParticipationDate = LocalDateTime.now();
    }


    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;

    }

    public void updateCash(Long cash) {
        this.cash = cash;
    }

    public void updateBackground(String newBackground) {
        this.background = newBackground;
    }

    public void addCash(Long rewardValue){
        cash += rewardValue;
    }

    public void addLp(double lpPoints) {
        this.lp += lpPoints;
    }

    public void setRankingParticipated(){
        this.isRankingParticipated = true;
    }
    public void resetRankingParticipated(){
        this.isRankingParticipated = false;
    }


}