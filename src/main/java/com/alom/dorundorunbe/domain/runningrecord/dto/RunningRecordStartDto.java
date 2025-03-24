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
@Schema(description = "RunningRecord 러닝 시작 요청 DTO")
public class RunningRecordStartDto {
    @Schema(description = "User id", example = "1")
    private Long userId;

    @Schema(description = "러닝 시작 시간", example = "2025-01-01T08:00:00Z")
    private String startTime;

    @Schema(description = "러닝 일자", example = "2025-01-01")
    private String date;
}
