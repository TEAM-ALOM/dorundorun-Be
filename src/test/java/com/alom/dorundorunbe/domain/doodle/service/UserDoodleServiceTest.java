package com.alom.dorundorunbe.domain.doodle.service;

import com.alom.dorundorunbe.domain.doodle.domain.Doodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.dto.UserDoodleDto;
import com.alom.dorundorunbe.domain.doodle.dto.UserDoodleRole;
import com.alom.dorundorunbe.domain.doodle.repository.DoodleRepository;
import com.alom.dorundorunbe.domain.doodle.repository.UserDoodleRepository;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDoodleServiceTest {

    @Mock
    private UserDoodleRepository userDoodleRepository;

    @Mock
    private DoodleRepository doodleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @InjectMocks
    private UserDoodleService userDoodleService;

    static private User user;
    static private Doodle doodle;
    static private RunningRecord runningRecord;
    static private UserDoodle userDoodle;

    @BeforeEach
    public void setUp(){
        user = User.builder()
                .id(1L)
                .nickname("testUser")
                .build();

        doodle = Doodle.builder()
                .id(1L)
                .name("testDoodle")
                .weeklyGoalDistance(3.0)
                .weeklyGoalCount(1)
                .weeklyGoalPace(12.0)
                .weeklyGoalCadence(3.0)
                .participants(new ArrayList<>())
                .isGoalActive(true)
                .doodlePoint(0)
                .isRunning(true)
                .weeklyGoalHeartRateZone(3)
                .isPublic(true)
                .maxParticipant(10)
                .build();

        runningRecord = RunningRecord.builder()
                .id(1L)
                .user(user)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now())
                .distance(3.0)
                .cadence(3)
                .elapsedTime(3600)
                .averageSpeed(5.0)
                .pace(12.0)
                .heartRate(120)
                .isRunning(false)
                .build();

        userDoodle = UserDoodle.builder()
                .id(1L)
                .user(user)
                .doodle(doodle)
                .status(UserDoodleStatus.PARTICIPATING)
                .role(UserDoodleRole.PARTICIPANT)
                .joinDate(LocalDate.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("createUserDoodle : Doodle 생성 시 참가자 초기화에 성공한다")
    public void createUserDoodle(){
        Long userId = 1L;
        Long doodleId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(doodleRepository.findById(doodleId)).thenReturn(Optional.ofNullable(doodle));

        UserDoodle result = userDoodleService.createUserDoodle(doodleId, userId);

        assertNotNull(result);
        assertThat(result.getRole()).isEqualTo(UserDoodleRole.CREATOR);
        assertThat(doodle.getParticipants().size()).isEqualTo(1);

        verify(userDoodleRepository, times(1)).save(any(UserDoodle.class));
        verify(doodleRepository, times(1)).save(any(Doodle.class));
    }

    @Test
    @DisplayName("addParticipantsToUserDoodle : Doodle방에 참가자를 업데이트 하는데 성공한다")
    public void addParticipantsToUserDoodle(){
        Long userId = 1L;
        Long doodleId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(doodleRepository.findById(doodleId)).thenReturn(Optional.ofNullable(doodle));

        //when
        UserDoodle result = userDoodleService.addParticipantsToUserDoodle(doodleId, userId);

        //then
        assertNotNull(result);
        assertThat(doodle.getParticipants().size()).isEqualTo(1);
        assertThat(result.getRole()).isEqualTo(UserDoodleRole.PARTICIPANT);

        verify(userDoodleRepository, times(1)).save(result);
        verify(doodleRepository, times(1)).save(doodle);
    }

    @Test
    @DisplayName("isGoalAchieved : 유저의 Doodle 주간 목표 전체 달성 여부를 확인한다")
    public void isGoalAchieved(){
        List<RunningRecord> runningRecordList = new ArrayList<>();

        runningRecordList.add(runningRecord);

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(doodleRepository.findById(1L)).thenReturn(Optional.ofNullable(doodle));
        when(userDoodleRepository.findByDoodleAndUser(any(Doodle.class), any(User.class))).thenReturn(Optional.ofNullable(userDoodle));
        when(runningRecordRepository.findAllByUser(any(User.class))).thenReturn(runningRecordList);

        UserDoodleDto resultDto = userDoodleService.isGoalAchieved(1L, 1L);

        double totalDistance = userDoodleService.getWeeklyTotalDistance(runningRecordList);
        int totalCount = userDoodleService.getWeeklyGoalCount(runningRecordList);
        double totalAveragePace = userDoodleService.getWeeklyAveragePace(runningRecordList);
        double totalAverageCadence = userDoodleService.getWeeklyAverageCadence(runningRecordList);
//        double totalAverageHeartRate = userDoodleService.getWeeklyAverageHeartRate(runningRecordList);

        assertNotNull(resultDto);
        assertThat(resultDto.getStatus()).isEqualTo(userDoodle.getStatus());
        assertThat(resultDto.getDoodleId()).isEqualTo(userDoodle.getDoodle().getId());
        assertThat(resultDto.getUserId()).isEqualTo(userDoodle.getUser().getId());

        assertThat(totalDistance).isEqualTo(3.0);
        assertThat(totalCount).isEqualTo(1);
        assertThat(totalAveragePace).isEqualTo(12.0);
        assertThat(totalAverageCadence).isEqualTo(3);
//        assertThat(totalAverageHeartRate).isEqualTo(120.0);

        verify(userRepository, times(1)).findById(1L);
        verify(doodleRepository, times(1)).findById(1L);
        verify(userDoodleRepository,times(1)).findByDoodleAndUser(doodle, user);
        verify(userDoodleRepository, times(1)).save(any(UserDoodle.class));
        verify(runningRecordRepository, times(1)).findAllByUser(any(User.class));
    }
}
