package com.alom.dorundorunbe.domain.runningrecord.controller;

import com.alom.dorundorunbe.domain.item.domain.ItemCategory;
import com.alom.dorundorunbe.domain.item.dto.EquippedItemResponseDto;
import com.alom.dorundorunbe.domain.runningrecord.dto.*;
import com.alom.dorundorunbe.domain.runningrecord.service.RunningRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RunningRecordController.class)
@WithMockUser
public class RunningRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RunningRecordService runningRecordService;

    @Test
    @DisplayName("Post /records : RunningRecord 생성")
    public void testCreateRunningRecord() throws Exception {
        RunningRecordResponseDto responseDto = RunningRecordResponseDto.builder()
                .id(1L)
                .startTime("2024-10-30T08:00:00Z")
                .date("2024-10-30")
                .isRunning(true)
                .build();

        when(runningRecordService.saveRunningRecord(any(RunningRecordStartDto.class))).thenReturn(responseDto);

        String requestBody = objectMapper.writeValueAsString(
                RunningRecordStartDto.builder()
                        .userId(1L)
                        .startTime("2024-10-30T08:00:00Z")
                        .date("2024-10-30")
                        .build()
        );

        mockMvc.perform(post("/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.startTime").value("2024-10-30T08:00:00Z"))
                .andExpect(jsonPath("$.date").value("2024-10-30"))
                .andExpect(jsonPath("$.running").value(true));
    }

    @Test
    @DisplayName("Put /records : RunningRecord 업데이트")
    public void testUpdateRunningRecord() throws Exception {
        RunningRecordResponseDto responseDto = RunningRecordResponseDto.builder()
                .id(1L)
                .distance(5.02)
                .cadence(150)
                .elapsedTime(2038)
                .averageSpeed(8.86)
                .pace(6766.2682)
                .heartRate(150)
                .startTime("2024-10-30T08:00:00Z")
                .endTime("2024-10-30T08:33:58Z")
                .date("2024-10-30")
                .isRunning(false)
                .items(List.of(new EquippedItemResponseDto(1L, "Item1", ItemCategory.ACCESSORY)))
                .gpsCoordinates(List.of(new GpsCoordinateDto(37.7749, -122.4194, "2024-10-30T08:05:00Z")))
                .build();

        when(runningRecordService.updateRunningRecord(any(RunningRecordEndDto.class))).thenReturn(responseDto);

        String requestBody = objectMapper.writeValueAsString(
                RunningRecordEndDto.builder()
                        .id(1L)
                        .userId(1L)
                        .distance(5.02)
                        .cadence(150)
                        .elapsedTime(2038)
                        .endTime("2024-10-30T08:33:58Z")
                        .averageSpeed(8.86)
                        .heartRate(150)
                        .gpsCoordinates(List.of(new GpsCoordinateDto(37.7749, -122.4194, "2024-10-30T08:05:00Z")))
                        .build()
        );

        mockMvc.perform(put("/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.startTime").value("2024-10-30T08:00:00Z"))
                .andExpect(jsonPath("$.endTime").value("2024-10-30T08:33:58Z"))
                .andExpect(jsonPath("$.date").value("2024-10-30"))
                .andExpect(jsonPath("$.distance").value(5.02))
                .andExpect(jsonPath("$.cadence").value(150))
                .andExpect(jsonPath("$.elapsedTime").value(2038))
                .andExpect(jsonPath("$.averageSpeed").value(8.86))
                .andExpect(jsonPath("$.pace").value(6766.2682))
                .andExpect(jsonPath("$.heartRate").value(150))
                .andExpect(jsonPath("$.running").value(false))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("Item1"))
                .andExpect(jsonPath("$.items[0].itemCategory").value("ACCESSORY"))
                .andExpect(jsonPath("$.gpsCoordinates[0].latitude").value(37.7749))
                .andExpect(jsonPath("$.gpsCoordinates[0].longitude").value(-122.4194))
                .andExpect(jsonPath("$.gpsCoordinates[0].timestamp").value("2024-10-30T08:05:00Z"));
    }

    @Test
    @DisplayName("Get /records/{id} : RunningRecord 단일 조회")
    public void testFetchRunningRecord() throws Exception {
        RunningRecordResponseDto responseDto = RunningRecordResponseDto.builder()
                .id(1L)
                .distance(5.02)
                .cadence(150)
                .elapsedTime(2038)
                .averageSpeed(8.86)
                .pace(6766.2682)
                .heartRate(150)
                .startTime("2024-10-30T08:00:00Z")
                .endTime("2024-10-30T08:33:58Z")
                .date("2024-10-30")
                .isRunning(true)
                .items(List.of(new EquippedItemResponseDto(1L, "Item1", ItemCategory.ACCESSORY)))
                .gpsCoordinates(List.of(new GpsCoordinateDto(37.7749, -122.4194, "2024-10-30T08:05:00Z")))
                .build();
        when(runningRecordService.findRunningRecord(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.startTime").value("2024-10-30T08:00:00Z"))
                .andExpect(jsonPath("$.endTime").value("2024-10-30T08:33:58Z"))
                .andExpect(jsonPath("$.date").value("2024-10-30"))
                .andExpect(jsonPath("$.distance").value(5.02))
                .andExpect(jsonPath("$.cadence").value(150))
                .andExpect(jsonPath("$.elapsedTime").value(2038))
                .andExpect(jsonPath("$.averageSpeed").value(8.86))
                .andExpect(jsonPath("$.heartRate").value(150))
                .andExpect(jsonPath("$.running").value(true))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("Item1"))
                .andExpect(jsonPath("$.items[0].itemCategory").value("ACCESSORY"))
                .andExpect(jsonPath("$.gpsCoordinates[0].latitude").value(37.7749))
                .andExpect(jsonPath("$.gpsCoordinates[0].longitude").value(-122.4194))
                .andExpect(jsonPath("$.gpsCoordinates[0].timestamp").value("2024-10-30T08:05:00Z"));
    }

    @Test
    @DisplayName("Get /records/user/{userId} : RunningRecord 유저별 조회")
    public void testFetchRunningRecords() throws Exception {
        RunningRecordResponseDto responseDto = RunningRecordResponseDto.builder()
                .id(1L)
                .distance(5.02)
                .cadence(150)
                .elapsedTime(2038)
                .averageSpeed(8.86)
                .pace(6766.2682)
                .heartRate(150)
                .startTime("2024-10-30T08:00:00Z")
                .endTime("2024-10-30T08:33:58Z")
                .date("2024-10-30")
                .isRunning(true)
                .items(List.of(new EquippedItemResponseDto(1L, "Item1", ItemCategory.ACCESSORY)))
                .gpsCoordinates(List.of(new GpsCoordinateDto(37.7749, -122.4194, "2024-10-30T08:05:00Z")))
                .build();
        Page<RunningRecordResponseDto> responsePage = new PageImpl<>(Collections.singletonList(responseDto));
        when(runningRecordService.findRunningRecords(eq(1L), any(Pageable.class))).thenReturn(responsePage);

        mockMvc.perform(get("/records/user/1")
                .param("page", "0")
                .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].startTime").value("2024-10-30T08:00:00Z"))
                .andExpect(jsonPath("$.content[0].endTime").value("2024-10-30T08:33:58Z"))
                .andExpect(jsonPath("$.content[0].date").value("2024-10-30"))
                .andExpect(jsonPath("$.content[0].distance").value(5.02))
                .andExpect(jsonPath("$.content[0].cadence").value(150))
                .andExpect(jsonPath("$.content[0].elapsedTime").value(2038))
                .andExpect(jsonPath("$.content[0].averageSpeed").value(8.86))
                .andExpect(jsonPath("$.content[0].heartRate").value(150))
                .andExpect(jsonPath("$.content[0].running").value(true))
                .andExpect(jsonPath("$.content[0].items[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].items[0].name").value("Item1"))
                .andExpect(jsonPath("$.content[0].items[0].itemCategory").value("ACCESSORY"))
                .andExpect(jsonPath("$.content[0].gpsCoordinates[0].latitude").value(37.7749))
                .andExpect(jsonPath("$.content[0].gpsCoordinates[0].longitude").value(-122.4194))
                .andExpect(jsonPath("$.content[0].gpsCoordinates[0].timestamp").value("2024-10-30T08:05:00Z"));
    }

}
