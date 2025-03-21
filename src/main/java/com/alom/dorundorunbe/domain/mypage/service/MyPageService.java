package com.alom.dorundorunbe.domain.mypage.service;

import com.alom.dorundorunbe.domain.achievement.repository.UserAchievementRepository;
import com.alom.dorundorunbe.domain.achievement.service.AchievementService;
import com.alom.dorundorunbe.domain.item.service.ItemService;
import com.alom.dorundorunbe.domain.mypage.dto.MyPageResponseDto;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
import com.alom.dorundorunbe.domain.runningrecord.service.RunningRecordService;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.mypage.dto.UserUpdateDto;
import com.alom.dorundorunbe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserService userService;
    private final RunningRecordRepository runningRecordRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ItemService itemService;
    private final RunningRecordService runningRecordService;
    private final AchievementService achievementService;

    public MyPageResponseDto getMyPage(Long userId){
        User user = userService.findById(userId);
        Pageable runningPage = PageRequest.of(0, 10, Sort.by("endTime").descending());
        Pageable achievementPage = PageRequest.of(0, 10, Sort.by("id").descending());
        MyPageResponseDto myPageResponseDto = MyPageResponseDto.builder()
                .email(user.getEmail())
                .rank(user.getRanking())
                .nickname(user.getNickname())
                .achievements(achievementService.findUserAchievement(userId, achievementPage))
                .runningRecords(runningRecordService.findRunningRecords(userId, runningPage))
                .equippedItems(itemService.findEquippedItemList(userId))
                .build();

        return myPageResponseDto;
    }


    public boolean checkNickNameDuplicate(String nickName) {
        return userService.existsByNickname(nickName);
    }

    public ResponseEntity<String> updateByEmail(UserUpdateDto userDTO, Long userId) {
        User user = userService.findById(userId);
        if(userDTO.getNickname() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nickname is required");
        if(checkNickNameDuplicate(userDTO.getNickname()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nickname already exists");
        user.setNickname(userDTO.getNickname());
        userService.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    // soft delete -> refresh 토큰 삭제 로직 추가 필요
    public ResponseEntity<String> deleteUser(Long userId) {
        User user = userService.findById(userId);
        userService.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }
}