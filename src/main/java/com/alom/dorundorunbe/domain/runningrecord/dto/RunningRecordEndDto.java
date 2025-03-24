package com.alom.dorundorunbe.domain.runningrecord.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "RunningRecord 러닝 종료 요청 DTO")
public class RunningRecordEndDto {
    @Schema(description = "RunningRecord id", example = "1")
    private Long id;

    @Schema(description = "User id", example = "1")
    private Long userId;

    @Schema(description = "러닝 종료 시간", example = "2025-01-01T08:30:00Z")
    private String endTime;

    @Schema(description = "러닝 총 거리", example = "5000.0")
    private Double distance;

    @Schema(description = "케이던스", example = "80")
    private Integer cadence;

    @Schema(description = "러닝 총 시간", example = "1800")
    private Integer elapsedTime;

    @Schema(description = "평균 속도", example = "2.78")
    private Double averageSpeed;

    @Schema(description = "평균 심장박동수", example = "120")
    private Integer heartRate;

    @Schema(description = "gps 정보")
    private List<GpsCoordinateDto> gpsCoordinates = new ArrayList<>();
}
