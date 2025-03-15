package com.alom.dorundorunbe.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "user 정보 업데이트 dto")
public class UserUpdateDto {
    @Schema(description = "바뀐 닉네임")
    private String nickname;
}
