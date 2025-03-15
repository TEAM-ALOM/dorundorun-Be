package com.alom.dorundorunbe.domain.mypage.dto;


import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.dto.GpsCoordinateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "마이페이지에 띄울 running record")
public class MyPageRunningRecordResponse {
    @Schema(description = "날짜")
    private LocalDate date;

    @Schema(description = "거리")
    private Double distance;

    @Schema(description = "페이스")
    private Double pace;

    @Schema(description = "시간")
    private Integer elapsedTime;

    @Schema(description = "케이던스")
    private Integer cadence;

    @Schema(description = "gps 정보")
    private List<GpsCoordinateDto> gpsCoordinates = new ArrayList<>();

    public MyPageRunningRecordResponse(RunningRecord record) {
        this.date = record.getDate();
        this.distance = record.getDistance();
        this.pace = record.getPace();
        this.elapsedTime = record.getElapsedTime();
        this.cadence = record.getCadence();

        this.gpsCoordinates = record.getGpsCoordinates().stream()
                .map(gps -> {
                    GpsCoordinateDto dto = new GpsCoordinateDto();
                    dto.setLatitude(gps.getLatitude());
                    dto.setLongitude(gps.getLongitude());
                    dto.setTimestamp(gps.getTimestamp().toString()); // LocalDateTime → String 변환
                    return dto;
                })
                .toList();
    }
}
