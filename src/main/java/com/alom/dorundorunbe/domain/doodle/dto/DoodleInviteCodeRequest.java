package com.alom.dorundorunbe.domain.doodle.dto;

import jakarta.validation.constraints.NotBlank;

public record DoodleInviteCodeRequest (
    @NotBlank(message = "초대코드를 입력해주세요.")
    String code){
}
