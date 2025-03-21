package com.alom.dorundorunbe.domain.mypage.service;

import com.alom.dorundorunbe.domain.achievement.service.AchievementService;
import com.alom.dorundorunbe.domain.item.service.ItemService;
import com.alom.dorundorunbe.domain.mypage.dto.MyPageResponseDto;
import com.alom.dorundorunbe.domain.runningrecord.service.RunningRecordService;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.mypage.dto.UserUpdateDto;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import com.alom.dorundorunbe.domain.user.service.UserService;
import com.alom.dorundorunbe.global.exception.BusinessException;
import com.alom.dorundorunbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserService userService;
    private final ItemService itemService;
    private final RunningRecordService runningRecordService;
    private final AchievementService achievementService;
    private final UserRepository userRepository;

    @Transactional
    public MyPageResponseDto getMyPage(Long userId){
        User user = userService.findById(userId);
        Pageable runningPage = PageRequest.of(0, 5, Sort.by("endTime").descending());
        Pageable achievementPage = PageRequest.of(0, 5, Sort.by("id").descending());
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


    @Transactional
    public ResponseEntity<String> updateByEmail(UserUpdateDto userDTO, Long userId) {
        User user = userService.findById(userId);
        if(userDTO.getNickname() == null)
            throw new BusinessException(ErrorCode.EMPTY_NICKNAME);
        if (userRepository.existsByNickname(userDTO.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATE);
        }

        user.setNickname(userDTO.getNickname());
        userService.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
    }

    // soft delete -> refresh 토큰 삭제 로직 추가 필요
    @Transactional
    public ResponseEntity<String> deleteUser(Long userId) {
        User user = userService.findById(userId);
        userService.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }
}