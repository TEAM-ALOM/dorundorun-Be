package com.alom.dorundorunbe.domain.runningrecord.controller;

import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordEndDto;
import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordResponseDto;
import com.alom.dorundorunbe.domain.runningrecord.dto.RunningRecordStartDto;
import com.alom.dorundorunbe.domain.runningrecord.service.RunningRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/records")
@Tag(name = "러닝 기록 관리 API")
public class RunningRecordController implements RunningRecordControllerDocs {
    private final RunningRecordService runningRecordService;

    @PostMapping
    public ResponseEntity<RunningRecordResponseDto> createRunningRecord(@RequestBody RunningRecordStartDto startDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(runningRecordService.saveRunningRecord(startDto));
    }

    @PutMapping
    public ResponseEntity<RunningRecordResponseDto> updateRunningRecord(@RequestBody RunningRecordEndDto endDto){
        return ResponseEntity.status(HttpStatus.OK).body(runningRecordService.updateRunningRecord(endDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RunningRecordResponseDto> fetchRunningRecord(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(runningRecordService.findRunningRecord(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<RunningRecordResponseDto>> fetchRunningRecords(@PathVariable(name = "userId") Long userId,
                                                                                     @RequestParam(defaultValue = "0", value = "page") int page,
                                                                                     @RequestParam(defaultValue = "5", value = "size") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(runningRecordService.findRunningRecords(userId, pageable));
    }


}
