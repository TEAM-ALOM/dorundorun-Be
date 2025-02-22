package com.alom.dorundorunbe.domain.doodle.domain;

import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity @Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "doodle")
public class Doodle extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column//주간 목표
    private Double weeklyGoalDistance;

    @Column(nullable = false)
    private Integer weeklyGoalCount;

    @Column
    private Double weeklyGoalCadence;

    @Column
    private Double weeklyGoalPace;

    @Column
    private Integer weeklyGoalHeartRateZone;

    @Column
    private Integer goalParticipationCount;

    @Column(nullable = false)
    private int maxParticipant;

    @Column(nullable = false)
    private boolean isRunning;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private double doodlePoint;

    @Column(nullable = false)
    private boolean isGoalActive;

    @Column
    private Tier requiredTier;

    //추가할 것 - 목표로 설정한 위치

    @OneToMany(mappedBy = "doodle", cascade = CascadeType.ALL)
    private List<UserDoodle> participants;

    //방 생성 시 참가자 수를 검증
    public boolean checkCanAddParticipant(int currentParticipants){
        return currentParticipants < maxParticipant;
    }

    //참가자 중복 검증
    public boolean IsDuplicatedParticipant(Doodle doodle, Long userId){
        return doodle.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(userId));
    }


}
