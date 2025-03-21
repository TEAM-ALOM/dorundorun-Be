package com.alom.dorundorunbe.domain.mypage.dto;

import com.alom.dorundorunbe.domain.achievement.dto.query.UserAchievementDto;
import com.alom.dorundorunbe.domain.item.dto.EquippedItemResponseDto;
import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "마이페이지 response dto")
public class MyPageResponseDto {
    @Schema(description = "email")
    private String email;

    @Schema(description = "nickname")
    private String nickname;

    @Schema(description = "업적 리스트")
    private Slice<UserAchievementDto> achievements;

    @Schema(description = "사용자 랭크")
    private Ranking rank;

    @Schema(description = "최근 런닝 기록들 리스트")
    private Page<RunningRecordResponseDto> runningRecords;

    @Schema(description = "현재 입고 있는 템들 리스트")
    private List<EquippedItemResponseDto> equippedItems;


}
