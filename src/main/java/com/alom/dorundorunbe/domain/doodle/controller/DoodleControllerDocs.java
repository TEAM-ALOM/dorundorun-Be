package com.alom.dorundorunbe.domain.doodle.controller;

import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.dto.DoodleInviteCodeRequest;
import com.alom.dorundorunbe.domain.doodle.dto.DoodleRequestDto;
import com.alom.dorundorunbe.domain.doodle.dto.DoodleResponseDto;
import com.alom.dorundorunbe.domain.doodle.dto.UserDoodleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DoodleControllerDocs {
    @Operation(
            summary = "두들런 생성",
            description = """
                    **두들런 생성**
                    새로운 두들런을 생성합니다.
                    
                    **입력 파라미터**
                    - `userId` : 유저 ID
                    - `name` : 두들런 이름
                    - `weeklyGoalDistance` : 주간 목표 러닝 거리
                    - `weeklyGoalCount` : 주간 목표 러닝 횟수
                    - `weeklyGoalCadence` : 주간 목표 러닝 케이던스
                    - `weeklyGoalPace` : 주간 목표 러닝 페이스
                    - `maxParticipant` : 두들런 최대 인원
                    - `weeklyGoalHeartRateZone` : 주간 목표 심박존
                    - `requiredTier` : 입장에 필요한 최소 티어 설정
                    - `isRunning` : 달리기 / 걷기 모드 설정 (달리기일 경우 true)
                    - `public` : 공공 방 / 비공개 방 설정 (공공 방이면 true)
                    - `goalActive` : 주간 목표 활성화 여부 (활성화 하면 ture)
                    
                    **반환값**
                    - `DoodleResponseDto` : 생성된 두들런 정보
                    """
    )
    ResponseEntity<DoodleResponseDto> createDoodle(@RequestBody DoodleRequestDto requestDto);

    @Operation(
            summary = "두들런 전체 조회",
            description = """
                    **두들런 전체 조회**
                    모든 두들런을 조회합니다
                    
                    **입력 파라미터**
                    
                    **반환값**
                    - `List<DoodleResponseDto>` : 모든 두들런 리스트
                    """
    )
    ResponseEntity<List<DoodleResponseDto>> getAllDoodles();

    @Operation(
            summary = "특정 두들런 조회",
            description = """
                    **특정 두들런 조회**
                    특정 두들런을 조회합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 조회할 두들런 ID
                    
                    **반환값**
                    - `DoodleResponseDto` : 두들런 정보
                    """
    )
    ResponseEntity<DoodleResponseDto> getDoodleById(@PathVariable("doodleId") Long doodleId);

    @Operation(
            summary = "두들런 수정",
            description = """
                    **두들런 수정**
                    특정 두들런을 수정합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 수정할 두들런 ID
                    - `name` : 두들런 이름
                    - `weeklyGoalDistance` : 주간 목표 러닝 거리
                    - `weeklyGoalCount` : 주간 목표 러닝 횟수
                    - `weeklyGoalCadence` : 주간 목표 러닝 케이던스
                    - `weeklyGoalPace` : 주간 목표 러닝 페이스
                    - `maxParticipant` : 두들런 최대 인원
                    - `weeklyGoalHeartRateZone` : 주간 목표 심박존
                    - `requiredTier` : 입장에 필요한 최소 티어 설정
                    - `isRunning` : 달리기 / 걷기 모드 설정 (달리기일 경우 true)
                    - `public` : 공공 방 / 비공개 방 설정 (공공 방이면 true)
                    - `goalActive` : 주간 목표 활성화 여부 (활성화 하면 ture)
                    
                    **반환값**
                    - `DoodleResponseDto` : 수정된 두들런 정보
                    """
    )
    ResponseEntity<DoodleResponseDto> updateDoodle(@PathVariable("doodleId") Long doodleId, @org.springframework.web.bind.annotation.RequestBody DoodleRequestDto doodleRequestDto);

    @Operation(
            summary = "두들런 삭제",
            description = """
                    **두들런 삭제**
                    두들런을 삭제합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 삭제할 두들런 ID
                   
                    **반환값**
                    """
    )
    ResponseEntity<Void> deleteDoodle(@PathVariable("doodleId") Long doodleId);

    @Operation(
            summary = "특정 두들런에 유저 추가",
            description = """
                    **특정 두들런에 유저 추가**
                    특정 두들런에 유저를 추가합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 유저를 추가할 두들런 ID
                    - `userId` : 추가할 유저 ID
                    
                    **반환값**
                    - `DoodleResponseDto` : 유저가 추가된 두들런 정보
                    """
    )
    ResponseEntity<DoodleResponseDto> addParticipantToDoodle(@PathVariable("doodleId") Long doodleId, @PathVariable("userId") Long userId);

    @Operation(
            summary = "특정 두들런의 유저 삭제",
            description = """
                    **특정 두들런의 유저 삭제**
                    특정 두들런의 특정 유저를 삭제합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 유저를 삭제할 두들런 ID
                    - `userId` : 삭제할 유저 ID
                    
                    **반환값**
                    - `DoodleResponseDto` : 유저가 삭제된 두들런 정보
                    """
    )
    ResponseEntity<DoodleResponseDto> deleteParticipant(@PathVariable("doodleId") Long doodleId, @PathVariable("userId") Long userId);

    @Operation(
            summary = "특정 두들런의 모든 참가자 조회",
            description = """
                    **특정 두들런의 모든 참가자 조회**
                    특정 두들런의 모든 참가자를 조회합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 조회할 두들런 ID
                    
                    **반환값**
                    - `List<UserDoodleDto>` : 조회한 두들런의 모든 유저 정보
                    """
    )
    ResponseEntity<List<UserDoodleDto>> getParticipants(@PathVariable("doodleId") Long doodleId);

    @Operation(
            summary = "특정 유저의 목표 달성 상태 변경",
            description = """
                    **특정 유저의 목표 달성 상태 변경**
                    특정 유저의 목표 달성 상태를 변경합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 유저가 참가한 두들런 ID
                    - `userId` : 목표 달성 상태를 변경할 유저 ID
                    
                    **반환값**
                    - `UserDoodleDto` : 유저 정보
                    """
    )
    ResponseEntity<UserDoodleDto> updateParticipantStatus(@PathVariable("doodleId") Long doodleId,
                                                          @PathVariable("userId") Long userId,
                                                          @RequestParam("status") UserDoodleStatus status);


    @Operation(
            summary = "특정 두들런에 특정 유저 초대",
            description = """
                    **특정 두들런에 특정 유저 초대**
                    특정 두들런에 특정 유저를 초대합니다
                    
                    **입력 파라미터**
                    - `doodleId` : 초대할 두들런 ID
                    - `userId` : 초대할 유저 ID
                    - `code` : 초대코드
                    
                    **반환값**
                    - `DoodleResponseDto` : 두들런 정보
                    """
    )
    ResponseEntity<DoodleResponseDto> joinDoodle(@PathVariable("doodleId") Long doodleId, @PathVariable("userId") Long userId, @Valid @org.springframework.web.bind.annotation.RequestBody final DoodleInviteCodeRequest request);
}
