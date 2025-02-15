package com.alom.dorundorunbe.domain.ranking.domain;

import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_ranking")
public class UserRanking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long grade; // 사용자 순위


    private Double averagePoint;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_ranking_id")
    @Builder.Default
    private List<RankingPoint> points = new ArrayList<>();

    public static UserRanking create(User user) {

        return UserRanking.builder()
                .user(user)
                .grade(null)
                .averagePoint(null)
                .build();
    }

    public void confirmRanking(Ranking ranking){
        this.ranking = ranking;
        ranking.addParticipant(this);
    }

    public void addPoint(double point){
        points.add(RankingPoint.of(point));
        points.sort(Comparator.comparingDouble(RankingPoint::getPoint).reversed());
        while (points.size() > 3) {
            points.remove(points.size() - 1);
        }


    }
    public Double updateAveragePoint() {
        this.averagePoint = points.stream()
                .mapToDouble(RankingPoint::getPoint)
                .average()
                .orElse(0.0);
        return averagePoint;
    }

    public void updateGrade(Long grade){
        this.grade = grade;
    }
}
