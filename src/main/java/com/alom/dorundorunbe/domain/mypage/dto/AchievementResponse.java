package com.alom.dorundorunbe.domain.mypage.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "마이페이지 내에 띄울 업적")
public class AchievementResponse {
    @Schema(description = "업적 id")
    private Long achievementId;

    @Schema(description = "업적 이름")
    private String achievementName;

    //private String achievementDescription;
}
