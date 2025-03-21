package com.alom.dorundorunbe.domain.mypage.controller;

import com.alom.dorundorunbe.domain.mypage.dto.MyPageResponseDto;
import com.alom.dorundorunbe.domain.mypage.dto.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface MyPageControllerSwaggerDocs {
    @Operation(
            summary = "마이페이지 조회하기",
            description = """
                **마이페이지 조회하기**
                
                마이페이지를 조회합니다.
                
                **입력 파라미터:**
                
                - 'Long id' : 유저 id
                
                **반환 값:**
                
                - 'MyPageResponseDto' : 마이페이지 dto
                
                """
    )
    public ResponseEntity<MyPageResponseDto> myPage(@PathVariable Long id);

    @Operation(
            summary = "유저 정보 업데이트",
            description = """
                **유저 정보 업데이트**
                
                유저 정보를 업데이트 합니다
                
                **입력 파라미터:**
                
                - 'String nickname' : 바뀐 닉네임
                
                **반환 값:**
                
                - 'String response body' : 유저 정보 변경 성공 여부
                
                """
    )
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateDto userUpdateDTO, @PathVariable Long id);

    @Operation(
            summary = "유저 삭제",
            description = """
                **유저 삭제**
                
                유저를 삭제합니다
                
                **반환 값:**
                
                - 'String response body' : 유저 삭제 성공 여부
                
                """
    )
    public ResponseEntity<String> deleteUser(@PathVariable Long id);


}
