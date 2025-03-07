package com.alom.dorundorunbe.domain.mypage.controller;


import com.alom.dorundorunbe.domain.mypage.dto.*;
import com.alom.dorundorunbe.domain.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myPage")
public class MyPageController implements MyPageControllerSwaggerDocs{

    private final MyPageService myPageService;

    @GetMapping("/")
    public ResponseEntity<MyPageResponseDto> myPage(){
        // 사용자 식별 - email 반환
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<AchievementResponse> achievementResponses = myPageService.getAchievements(username);
        String rank = myPageService.getUserRank(username);
        List<MyPageRunningRecordResponse> runningRecords = myPageService.getRunningRecords(username);
        String nickname = myPageService.getUserNickname(username);

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(username, nickname, achievementResponses, rank, runningRecords);
        return new ResponseEntity<>(myPageResponseDto, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateDto userDTO){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return myPageService.updateByEmail(userDTO, username);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return myPageService.deleteUser(username);
    }

}
