package com.alom.dorundorunbe.domain.ranking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "웹소켓을 통해 전달되는 랭킹 정보 DTO")
public class RankingSocketDto {
    @Schema(description = "Ranking 방 ID", example = "1")
    private Long rankingId;

    @Schema(description = "랭킹 방의 Tier (예: 아마추어, 프로 등)", example = "스타터")
    private String tier;

    @Schema(description = "랭킹 방에 참가한 사용자 목록(등수, 평균점수)")
    private List<RankingSocketUserDto> participants;

    public RankingSocketDto(Long rankingId, String tier, List<RankingSocketUserDto> users) {
        this.rankingId = rankingId;
        this.tier = tier;
        this.participants = users;
    }
}

