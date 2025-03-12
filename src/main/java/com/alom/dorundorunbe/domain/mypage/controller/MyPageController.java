package com.alom.dorundorunbe.domain.mypage.controller;


import com.alom.dorundorunbe.domain.mypage.dto.*;
import com.alom.dorundorunbe.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
@Tag(name = "마이페이지 API")
public class MyPageController implements MyPageControllerSwaggerDocs{

    private final MyPageService myPageService;

    @GetMapping("/{userId}")
    public ResponseEntity<MyPageResponseDto> myPage(@PathVariable Long userId){
        return ResponseEntity.status(HttpStatus.CREATED).body(myPageService.getMyPage(userId));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateDto userDTO, @PathVariable Long userId){
        return myPageService.updateByEmail(userDTO, userId);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return myPageService.deleteUser(userId);
    }

}
