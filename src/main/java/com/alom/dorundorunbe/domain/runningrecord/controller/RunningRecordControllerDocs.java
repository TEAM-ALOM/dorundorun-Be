package com.alom.dorundorunbe.domain.runningrecord.controller;

import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordEndDto;
import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordResponseDto;
import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordStartDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface RunningRecordControllerDocs {
    @Operation(
            summary = "러닝 기록 생성",
            description = """
                **러닝 기록 생성**
                
                새로운 러닝 기록을 생성합니다.
                
                **입력 파라미터:**
                
                - `Long userId` : User id
                - `String startTime` : 러닝 시작 시간 (형식 예시 : "2025-01-01T08:00:00Z")
                - `String date` : 러닝 일자 (형식 예시 : "2025-01-01")
                    
                
                
                **반환값:**
                
                - `RunningRecordResponseDto` : 생성된 러닝 기록 정보              
                """
    )
    public ResponseEntity<RunningRecordResponseDto> createRunningRecord(@RequestBody RunningRecordStartDto startDto);

    // 수정하기
    @Operation(
            summary = "러닝 기록 업데이트",
            description = """
                **러닝 기록 업데이트**
                
                러닝이 종료되어 기록을 업데이트합니다.
                
                **입력 파라미터:**
                
                - `Long id` : RunningRecord id
                - `Long userId` : User id
                - `String endTime` : 러닝 종료 시간 (형식 예시 : "2025-01-01T08:00:00Z")
                - `Double distance` : 러닝 총 거리 (단위 : 미터)
                - `Integer cadence` : 케이던스
                - `Integer elapsedTime` : 러닝 총 시간 (단위 : 초)
                - `Double averageSpeed` : 평균 속도 (단위 : m/s)
                - `Integer heartRate` : 평균 심장박동수
                - `List<GpsCoordinateDto> gpsCoordinates` : gps 정보 목록
                    - `Double latitude` : 위도
                    - `Double longitude` : 경도
                    - `String timestamp` : 좌표 측정 시간 (형식 예시 : "2025-01-01T08:00:00Z")
                    
                
                
                **반환값:**
                
                - `RunningRecordResponseDto` : 수정된 러닝 기록 정보              
                """
    )
    public ResponseEntity<RunningRecordResponseDto> updateRunningRecord(@RequestBody RunningRecordEndDto endDto);

    @Operation(
            summary = "러닝 기록 상세 조회",
            description = """
                **러닝 기록 상세 조회**
                
                특정 id의 러닝 기록을 조회합니다.
                
                **입력 파라미터:**
                
                - `Long id` : RunningRecord id
                    
                **반환값:**
                
                - `RunningRecordResponseDto` : id에 해당하는 러닝 기록 정보           
                """
    )
    public ResponseEntity<RunningRecordResponseDto> fetchRunningRecord(@PathVariable(name = "id") Long id);

    @Operation(
            summary = "러닝 기록 user별 조회",
            description = """
                **러닝 기록 user별 조회**
                
                특정 user의 러닝 기록을 조회합니다. 페이징 처리가 지원됩니다.
                
                **입력 파라미터:**
                
                - `Long userId` : 조회할 User id
                - `int page` : 조회할 페이지 번호 (기본값: 0)
                - `int size` : 한 페이지에 표시할 데이터 수 (기본값: 5)
                    
                **반환값:**
                
                - `Page<RunningRecordResponseDto>` : userId에 해당하는 유저의 러닝 기록 정보
                - `content` : 러닝 기록 데이터 목록
                - `totalPages` : 전체 페이지 수
                - `totalElements` : 전체 데이터 수
                - `size` : 요청된 페이지 크기
                - `number` : 현재 페이지 번호
                - `sort` : 정렬 정보
                """
    )
    public ResponseEntity<Page<RunningRecordResponseDto>> fetchRunningRecords(@PathVariable(name = "userId") Long userId,
                                                                              @RequestParam(defaultValue = "0", value = "page") int page,
                                                                              @RequestParam(defaultValue = "5", value = "size") int size);
}
