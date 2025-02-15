package com.alom.dorundorunbe.domain.ranking.dto;

import com.alom.dorundorunbe.domain.ranking.domain.RankingPoint;
import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "User Ranking 응답 DTO")
public class UserRankingDto {
    @Schema(description = "사용자 랭킹 ID", example = "10")
    private Long userRankingId;

    @Schema(description = "등수", example = "1")
    private Long grade;

    @Schema(description = "평균 포인트", example = "150.5")
    private Double averagePoint;

    @Schema(description = "포인트 리스트(상위 3개)")
    private List<Double> points;

    @Schema(description = "생성 일자", example = "2025-01-01T00:00:00")
    private String createdAt;

    @Schema(description = "사용자 정보 DTO")
    private UserInfoDto userInfoDto;

    public static UserRankingDto of(UserRanking userRanking) {
        return UserRankingDto.builder()
                .userRankingId(userRanking.getId())
                .grade(userRanking.getGrade())
                .averagePoint(userRanking.getAveragePoint())
                .points(userRanking.getPoints().stream()
                        .map(RankingPoint::getPoint)
                        .toList())
                .createdAt(userRanking.getCreatedAt() != null ? userRanking.getCreatedAt().toString() : "N/A")
                .userInfoDto(UserInfoDto.of(userRanking.getUser()))
                .build();
    }
}
