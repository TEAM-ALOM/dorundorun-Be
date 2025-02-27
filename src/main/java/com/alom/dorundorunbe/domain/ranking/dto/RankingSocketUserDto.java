package com.alom.dorundorunbe.domain.ranking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "웹소켓을 통해 전달되는 랭킹 참가자 정보 DTO")
public class RankingSocketUserDto{
    @Schema(description = "사용자 ID", example = "101")
    private Long userId;

    @Schema(description = "사용자의 평균 점수", example = "85.0")
    private Double averagePoint;

    @Schema(description = "사용자의 랭킹 등수 (예: 1, 2, 3 등)", example = "1")
    private Long grade;

    public RankingSocketUserDto(Long userId, Double averagePoint, Long grade) {
        this.userId = userId;
        this.averagePoint = averagePoint;
        this.grade = grade;
    }
}
