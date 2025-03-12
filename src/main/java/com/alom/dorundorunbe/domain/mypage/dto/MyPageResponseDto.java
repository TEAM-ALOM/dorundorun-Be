package com.alom.dorundorunbe.domain.mypage.dto;

import com.alom.dorundorunbe.domain.item.dto.EquippedItemResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
    private List<AchievementResponse> achievements;

    @Schema(description = "사용자 랭크")
    private String rank;

    @Schema(description = "최근 런닝 기록들 리스트")
    private List<MyPageRunningRecordResponse> runningRecords;

    @Schema(description = "현재 입고 있는 템들 리스트")
    private List<EquippedItemResponseDto> equippedItems;


}
