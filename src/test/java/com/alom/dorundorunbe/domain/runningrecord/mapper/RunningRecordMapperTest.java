package com.alom.dorundorunbe.domain.runningrecord.mapper;

import com.alom.dorundorunbe.domain.item.domain.Item;
import com.alom.dorundorunbe.domain.item.domain.ItemCategory;
import com.alom.dorundorunbe.domain.item.dto.EquippedItemResponseDto;
import com.alom.dorundorunbe.domain.runningrecord.domain.GpsCoordinate;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecordItem;
import com.alom.dorundorunbe.domain.runningrecord.dto.*;
import com.alom.dorundorunbe.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RunningRecordMapperTest {

    private final RunningRecordMapper mapper = new RunningRecordMapperImpl();

    @Test
    @DisplayName("toResponseDto : Entity to ResponseDto")
    void testToResponseDto(){
        // given
        User user = new User();
        RunningRecord runningRecord = RunningRecord.builder()
                .id(1L)
                .distance(5.02)
                .cadence(150)
                .elapsedTime(2038)
                .averageSpeed(8.86)
                .pace(6766.2682)
                .heartRate(150)
                .isRunning(false)
                .date(LocalDate.of(2024, 10, 30))
                .startTime(LocalDateTime.of(2024, 10, 30, 8, 0, 0))
                .endTime(LocalDateTime.of(2024, 10, 30, 8, 33, 58))
                .items(List.of(
                        RunningRecordItem.builder()
                                .item(Item.builder().id(1L).name("Item1").itemCategory(ItemCategory.ACCESSORY).build())
                                .build()
                ))
                .gpsCoordinates(List.of(
                        GpsCoordinate.builder()
                                .latitude(37.7749)
                                .longitude(-122.4194)
                                .timestamp(OffsetDateTime.parse("2024-10-30T08:05:00Z").toLocalDateTime())
                                .build()))
                .user(user)
                .build();

        // when
        RunningRecordResponseDto responseDto = mapper.toResponseDto(runningRecord);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getDistance()).isEqualTo(5.02);
        assertThat(responseDto.getCadence()).isEqualTo(150);
        assertThat(responseDto.getElapsedTime()).isEqualTo(2038);
        assertThat(responseDto.getAverageSpeed()).isEqualTo(8.86);
        assertThat(responseDto.getPace()).isEqualTo(6766.2682);
        assertThat(responseDto.getHeartRate()).isEqualTo(150);
        assertThat(responseDto.isRunning()).isEqualTo(false);
        assertThat(responseDto.getDate()).isEqualTo("2024-10-30");
        assertThat(responseDto.getStartTime()).isEqualTo("2024-10-30T08:00:00Z");
        assertThat(responseDto.getEndTime()).isEqualTo("2024-10-30T08:33:58Z");
        assertThat(responseDto.getItems()).hasSize(1);
        EquippedItemResponseDto itemDto = responseDto.getItems().get(0);
        assertThat(itemDto.id()).isEqualTo(1L);
        assertThat(itemDto.name()).isEqualTo("Item1");
        assertThat(itemDto.itemCategory()).isEqualTo(ItemCategory.ACCESSORY);
        assertThat(responseDto.getGpsCoordinates()).hasSize(1);
        GpsCoordinateDto gpsDto = responseDto.getGpsCoordinates().get(0);
        assertThat(gpsDto.getLatitude()).isEqualTo(37.7749);
        assertThat(gpsDto.getLongitude()).isEqualTo(-122.4194);
        assertThat(gpsDto.getTimestamp()).isEqualTo("2024-10-30T08:05:00Z");

    }

    @Test
    @DisplayName("toEntityFromStartDto : startDto to Entity")
    void testToEntityFromStartDto(){
        // given
        RunningRecordStartDto startDto = RunningRecordStartDto.builder()
                .userId(1L)
                .startTime("2024-10-30T08:00:00Z")
                .date("2024-10-30")
                .build();

        // when
        RunningRecord runningRecord = mapper.toEntityFromStartDto(startDto);

        // then
        assertThat(runningRecord.getDate()).isEqualTo(LocalDate.parse("2024-10-30"));
        assertThat(runningRecord.getStartTime()).isEqualTo(OffsetDateTime.parse("2024-10-30T08:00:00Z").toLocalDateTime());
    }

    @Test
    @DisplayName("updateFromEndDto : update Entity with RequestDto")
    void testUpdateFromEndDto(){
        // given
        RunningRecordEndDto endDto = RunningRecordEndDto.builder()
                .id(1L)
                .userId(1L)
                .distance(5.02)
                .cadence(150)
                .elapsedTime(2038)
                .endTime("2024-10-30T08:33:58Z")
                .averageSpeed(8.86)
                .heartRate(150)
                .build();

        // when
        RunningRecord runningRecord = new RunningRecord();
        mapper.updateFromEndDto(endDto, runningRecord);

        // then
        assertThat(runningRecord.getDistance()).isEqualTo(5.02);
        assertThat(runningRecord.getCadence()).isEqualTo(150);
        assertThat(runningRecord.getElapsedTime()).isEqualTo(2038);
        assertThat(runningRecord.getAverageSpeed()).isEqualTo(8.86);
        assertThat(runningRecord.getHeartRate()).isEqualTo(150);
        assertThat(runningRecord.getEndTime()).isEqualTo(OffsetDateTime.parse("2024-10-30T08:33:58Z").toLocalDateTime());
    }

}
