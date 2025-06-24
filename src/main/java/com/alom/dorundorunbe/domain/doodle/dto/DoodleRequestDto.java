package com.alom.dorundorunbe.domain.doodle.dto;

import com.alom.dorundorunbe.global.enums.Tier;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoodleRequestDto {
    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @Positive(message = "유효하지 않은 값입니다.")
    private Double weeklyGoalDistance;

    @Positive(message = "유효하지 않은 값입니다.")
    private Integer weeklyGoalCount;

    @Positive(message = "유효하지 않은 값입니다.")
    private Double weeklyGoalCadence;

    @Positive(message = "유효하지 않은 값입니다.")
    private Double weeklyGoalPace;

    @NotNull(message = "최대 인원 수를 입력해주세요.")
    @Positive(message = "유효하지 않은 값입니다.")
    private int maxParticipant;

    @NotNull(message = "러닝 모드를 선택해주세요.(걷기 또는 달리기)")
    private boolean isRunning;

    @Min(value = 1, message = "심박존은 최소 1이어야 합니다.")
    @Max(value = 5, message = "심박존은 최대 5이어야 합니다.")
    private Integer weeklyGoalHeartRateZone;

    @NotNull(message =  "방의 공개 여부를 선택해주세요.(공개 또는 비공개)")
    private boolean isPublic;

    @NotNull(message = "주간 목표 활성화 여부를 선택해주세요.(활성화 또는 비활성화)")
    private boolean isGoalActive;

    private Tier requiredTier;
}
