package com.alom.dorundorunbe.domain.mypage.service;

import com.alom.dorundorunbe.domain.achievement.repository.UserAchievementRepository;
import com.alom.dorundorunbe.domain.item.service.ItemService;
import com.alom.dorundorunbe.domain.mypage.dto.MyPageResponseDto;
import com.alom.dorundorunbe.domain.mypage.dto.MyPageRunningRecordResponse;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
import com.alom.dorundorunbe.domain.achievement.domain.UserAchievement;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.mypage.dto.AchievementResponse;
import com.alom.dorundorunbe.domain.mypage.dto.UserUpdateDto;
import com.alom.dorundorunbe.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserService userService;
    private final RunningRecordRepository runningRecordRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ItemService itemService;

    public MyPageResponseDto getMyPage(Long userId){
        User user = userService.findById(userId);
        MyPageResponseDto myPageResponseDto = MyPageResponseDto.builder()
                .email(user.getEmail())
                .rank(user.getRanking().toString())
                .nickname(user.getNickname())
                .achievements(getAchievements(userId))
                .runningRecords(getRunningRecords(userId))
                .equippedItems(itemService.findEquippedItemList(userId))
                .build();

        return myPageResponseDto;
    }

    public List<MyPageRunningRecordResponse> getRunningRecords(Long userId) {
        User user = userService.findById(userId);
        List<RunningRecord> runningRecords = runningRecordRepository.findAllByUser(user);
        runningRecords.sort(Comparator.comparing(RunningRecord::getDate).reversed());

        return runningRecords.stream()
                .map(MyPageRunningRecordResponse::new)
                .collect(Collectors.toList());
    }

    public List<AchievementResponse> getAchievements(Long userId) {
        User user = userService.findById(userId);
        List<UserAchievement> userAchievements = userAchievementRepository.findAllByUser(user);
        return userAchievements.stream()
                .map(ua->new AchievementResponse(
                        ua.getAchievement().getId(),
                        ua.getAchievement().getName()
                ))
                .collect(Collectors.toList());
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