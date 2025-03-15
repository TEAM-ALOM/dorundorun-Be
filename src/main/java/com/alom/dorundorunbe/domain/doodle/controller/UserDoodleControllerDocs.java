package com.alom.dorundorunbe.domain.doodle.controller;

import com.alom.dorundorunbe.domain.doodle.domain.Doodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.dto.UserDoodleDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserDoodleControllerDocs {

    @Operation(
            summary = "포인트 상위 10개 두들런 반환",
            description = """
                    **포인트 상위 10개 두들런 반환**
                    특정 유저가 참여한 두들런 중 포인트 합계 상위 10개의 두들런을 반환합니다.
                    
                    **입력  파라미터**
                    - `userId` : 특정 유저 ID
                    
                    **반환값**
                    - `List<Doodle>` : 두들런 리스트
                    """
    )
    ResponseEntity<List<Doodle>> getTop10DoodlePointsForUser(@PathVariable("userId") Long userId);

    @Operation(
            summary = "유저 주간 목표 전체 달성 여부 확인",
            description = """
                    **유저 주간 목표 전체 달성 여부 확인**
                    유저가 주간 묵표를 모두 달성했는지 확인합니다.
                    
                    **입력 파라미터**
                    - `userId` : 특정 유저 ID
                    - `doodleId` : 특정 두들런 ID
                    
                    **반환값**
                    - `UserDoodleStatus` : 유저의 완료 상태
                    """
    )
    ResponseEntity<UserDoodleStatus> isGoalAchieved(@PathVariable("userId") Long userId, @PathVariable("doodleId") Long doodleId);
}
