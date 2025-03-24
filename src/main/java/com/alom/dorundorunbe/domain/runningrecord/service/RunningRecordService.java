package com.alom.dorundorunbe.domain.runningrecord.service;

import com.alom.dorundorunbe.domain.item.domain.Item;
import com.alom.dorundorunbe.domain.item.dto.EquippedItemResponseDto;
import com.alom.dorundorunbe.domain.item.service.ItemService;
import com.alom.dorundorunbe.domain.runningrecord.domain.GpsCoordinate;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecordItem;
import com.alom.dorundorunbe.domain.runningrecord.dto.*;
import com.alom.dorundorunbe.domain.runningrecord.mapper.GpsCoordinateMapper;
import com.alom.dorundorunbe.domain.runningrecord.mapper.RunningRecordMapper;
import com.alom.dorundorunbe.domain.runningrecord.repository.GpsCoordinateRepository;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordItemRepository;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RunningRecordService {
    private final RunningRecordRepository runningRecordRepository;
    private final RunningRecordMapper runningRecordMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final RunningRecordItemRepository runningRecordItemRepository;
    private final GpsCoordinateMapper gpsCoordinateMapper;
    private final GpsCoordinateRepository gpsCoordinateRepository;

    @Transactional
    public void setEquippedItems(RunningRecord runningRecord){
        List<EquippedItemResponseDto> equippedItems = itemService.findEquippedItemList(runningRecord.getUser().getId());
        List<RunningRecordItem> runningRecordItems = new ArrayList<>();
        equippedItems.forEach(itemDto -> {
            Item item = itemService.findItemById(itemDto.id());
            RunningRecordItem runningRecordItem = RunningRecordItem.builder()
                    .item(item)
                    .runningRecord(runningRecord)
                    .build();
            runningRecordItems.add(runningRecordItem);
        });
        runningRecordItemRepository.saveAll(runningRecordItems);
    }

    @Transactional
    public void setGpsCoordinate(RunningRecord runningRecord, List<GpsCoordinateDto> gpsCoordinateDtos){
        List<GpsCoordinate> gpsCoordinates = gpsCoordinateDtos.stream()
                .map(gpsCoordinateMapper::toEntity)
                .collect(Collectors.toList());
        gpsCoordinates.forEach(runningRecord::addGpsCoordinate);
        gpsCoordinateRepository.saveAll(gpsCoordinates);
    }

    @Transactional
    public RunningRecordResponseDto saveRunningRecord(RunningRecordStartDto startDto){
        User user = userService.findById(startDto.getUserId());
        RunningRecord runningRecord = runningRecordMapper.toEntityFromStartDto(startDto);
        runningRecord.setUser(user);
        runningRecord.setRunning(true);
        runningRecordRepository.save(runningRecord);
        return runningRecordMapper.toResponseDto(runningRecord);
    }

    @Transactional
    public RunningRecordResponseDto updateRunningRecord(RunningRecordEndDto endDto){
        RunningRecord runningRecord = runningRecordRepository.findById(endDto.getId())
                .orElseThrow(() -> new IllegalStateException("Running record with id "+endDto.getId()+" does not exist"));
        runningRecordMapper.updateFromEndDto(endDto, runningRecord);
        runningRecord.calculatePace();
        runningRecord.setRunning(false);
        runningRecordRepository.save(runningRecord);
        setEquippedItems(runningRecord);
        setGpsCoordinate(runningRecord, endDto.getGpsCoordinates());
        return runningRecordMapper.toResponseDto(runningRecord);
    }

    // user별 최신순 특정 개수의 기록 조회
    public Page<RunningRecordResponseDto> findRunningRecords(Long userId, Pageable pageable){
        User user = userService.findById(userId);
        Page<RunningRecord> records = runningRecordRepository.findByUser(user, pageable);
        return records.map(runningRecordMapper::toResponseDto);
    }

    // 특정 기록의 상세 조회
    public RunningRecordResponseDto findRunningRecord(Long id){
        return runningRecordMapper.toResponseDto(runningRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Running record with id "+id+" does not exist")));
    }
}
