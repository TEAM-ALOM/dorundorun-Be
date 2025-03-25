package com.alom.dorundorunbe.domain.runningrecord.service;

import com.alom.dorundorunbe.domain.item.domain.Item;
import com.alom.dorundorunbe.domain.item.domain.ItemCategory;
import com.alom.dorundorunbe.domain.item.dto.EquippedItemResponseDto;
import com.alom.dorundorunbe.domain.item.service.ItemService;
import com.alom.dorundorunbe.domain.runningrecord.domain.GpsCoordinate;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.dto.*;
import com.alom.dorundorunbe.domain.runningrecord.mapper.GpsCoordinateMapper;
import com.alom.dorundorunbe.domain.runningrecord.mapper.RunningRecordMapper;
import com.alom.dorundorunbe.domain.runningrecord.repository.GpsCoordinateRepository;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordItemRepository;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
import com.alom.dorundorunbe.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.alom.dorundorunbe.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RunningRecordServiceTest {

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private RunningRecordMapper runningRecordMapper;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private RunningRecordItemRepository runningRecordItemRepository;

    @Mock
    private GpsCoordinateMapper gpsCoordinateMapper;

    @Mock
    private GpsCoordinateRepository gpsCoordinateRepository;

    @InjectMocks
    private RunningRecordService runningRecordService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveRunningRecord : RunningRecord 생성")
    void saveRunningRecord() {
        // given
        Long userId = 1L;
        RunningRecordStartDto startDto = new RunningRecordStartDto();
        startDto.setUserId(userId);

        User user = new User();
        user.setRunning(false);
        RunningRecord runningRecord = new RunningRecord();
        RunningRecordResponseDto responseDto = new RunningRecordResponseDto();

        when(userService.findById(userId)).thenReturn(user);
        when(runningRecordMapper.toEntityFromStartDto(startDto)).thenReturn(runningRecord);
        when(runningRecordMapper.toResponseDto(runningRecord)).thenReturn(responseDto);

        // when
        RunningRecordResponseDto result = runningRecordService.saveRunningRecord(startDto);

        // then
        verify(runningRecordRepository).save(runningRecord);
        assertEquals(responseDto, result);
        assertTrue(user.isRunning());
    }

    @Test
    @DisplayName("updateRunningRecord : RunningRecord 업데이트")
    void updateRunningRecord() {
        // given
        RunningRecordEndDto endDto = new RunningRecordEndDto();
        endDto.setId(1L);
        GpsCoordinateDto gpsDto = new GpsCoordinateDto(37.7749, -122.4194, "2025-01-01T08:00:00Z");
        endDto.setGpsCoordinates(List.of(gpsDto));

        RunningRecord runningRecord = new RunningRecord();
        User user = new User();
        user.setRunning(true);
        runningRecord.setUser(user);
        RunningRecordResponseDto responseDto = new RunningRecordResponseDto();

        GpsCoordinate gpsCoordinate = new GpsCoordinate();
        when(gpsCoordinateMapper.toEntity(gpsDto)).thenReturn(gpsCoordinate);

        when(runningRecordRepository.findById(1L)).thenReturn(Optional.of(runningRecord));
        when(runningRecordMapper.toResponseDto(runningRecord)).thenReturn(responseDto);

        // when
        RunningRecordResponseDto result = runningRecordService.updateRunningRecord(endDto);

        // then
        verify(runningRecordRepository).save(runningRecord);
        verify(runningRecordItemRepository).saveAll(anyList());
        verify(gpsCoordinateRepository).saveAll(anyList());
        assertEquals(responseDto, result);
        assertFalse(user.isRunning());
    }

    @Test
    @DisplayName("setEquippedItems : 착용한 아이템 정보 설정")
    void setEquippedItems() {
        // given
        RunningRecord runningRecord = new RunningRecord();
        runningRecord.setUser(User.builder().id(1L).build());

        EquippedItemResponseDto itemDto = new EquippedItemResponseDto(1L, "Item1", ItemCategory.ACCESSORY);
        Item item = new Item();

        when(itemService.findEquippedItemList(1L)).thenReturn(List.of(itemDto));
        when(itemService.findItemById(1L)).thenReturn(item);

        // when
        runningRecordService.setEquippedItems(runningRecord);

        // then
        verify(itemService).findEquippedItemList(1L);
        verify(itemService).findItemById(1L);
        verify(runningRecordItemRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("setGpsCoordinate : gps 정보 설정")
    void setGpsCoordinate() {
        // given
        RunningRecord runningRecord = new RunningRecord();
        GpsCoordinateDto gpsCoordinateDto = new GpsCoordinateDto(37.7749, -122.4194, "2025-01-01T08:00:00Z");
        GpsCoordinate gpsCoordinate = new GpsCoordinate();

        when(gpsCoordinateMapper.toEntity(gpsCoordinateDto)).thenReturn(gpsCoordinate);

        // when
        runningRecordService.setGpsCoordinate(runningRecord, List.of(gpsCoordinateDto));

        // then
        verify(gpsCoordinateRepository).saveAll(anyList());
        assertEquals(1, runningRecord.getGpsCoordinates().size());
    }

    @Test
    @DisplayName("findRunningRecords : RunningRecord 유저별 조회")
    void findRunningRecords(){
        // given
        Long userId = 1L;
        Pageable pageable = mock(Pageable.class);
        User user = new User();

        RunningRecord runningRecord = new RunningRecord();
        Page<RunningRecord> records = new PageImpl<>(Collections.singletonList(runningRecord));
        RunningRecordResponseDto responseDto = new RunningRecordResponseDto();
        responseDto.setItems(List.of(new EquippedItemResponseDto(1L, "Item1", ItemCategory.ACCESSORY)));

        when(userService.findById(userId)).thenReturn(user);
        when(runningRecordRepository.findByUser(user, pageable)).thenReturn(records);
        when(runningRecordMapper.toResponseDto(runningRecord)).thenReturn(responseDto);

        // when
        Page<RunningRecordResponseDto> result = runningRecordService.findRunningRecords(userId, pageable);

        // then
        verify(userService).findById(userId);
        verify(runningRecordRepository).findByUser(user, pageable);
        assertEquals(1,result.getTotalElements());
        assertEquals(responseDto, result.getContent().get(0));
        assertEquals(1, result.getContent().get(0).getItems().size());
        assertEquals(1L, result.getContent().get(0).getItems().get(0).id());
    }

    @Test
    @DisplayName("findRunningRecord : RunningRecord 단일 조회")
    void findRunningRecord(){
        // given
        Long id = 1L;
        RunningRecord runningRecord = new RunningRecord();
        RunningRecordResponseDto responseDto = new RunningRecordResponseDto();
        responseDto.setItems(List.of(new EquippedItemResponseDto(1L, "Item1", ItemCategory.ACCESSORY))); // 리스트 초기화

        when(runningRecordRepository.findById(id)).thenReturn(Optional.of(runningRecord));
        when(runningRecordMapper.toResponseDto(runningRecord)).thenReturn(responseDto);

        // when
        RunningRecordResponseDto result = runningRecordService.findRunningRecord(id);

        // then
        verify(runningRecordRepository).findById(id);
        assertEquals(result, responseDto);
        assertEquals(1, result.getItems().size());
        assertEquals(1L, result.getItems().get(0).id());
    }

    @Test
    @DisplayName("findRunningRecord_NotFound : RunningRecord Not Found")
    void findRunningRecord_NotFound(){
        // given
        Long id = 1L;
        when(runningRecordRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThrows(IllegalStateException.class,() -> runningRecordService.findRunningRecord(id));
        verify(runningRecordRepository).findById(id);
    }
}
