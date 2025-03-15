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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDoodleService {

    private final UserDoodleRepository userDoodleRepository;
    private final UserRepository userRepository;
    private final DoodleRepository doodleRepository;
    private final RunningRecordRepository runningRecordRepository;

    // 참가자 리스트가 null일 경우 초기화하는 메서드
    private void initializeParticipantsList(Doodle doodle) {
        if (doodle.getParticipants() == null) {
            doodle.setParticipants(new ArrayList<>());
        }
    }

    public UserDoodle createUserDoodle(Long doodleId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        Doodle doodle = doodleRepository.findById(doodleId).orElseThrow(() -> new IllegalArgumentException("DOODLE NOT FOUND"));

        initializeParticipantsList(doodle);

        UserDoodle userDoodle = new UserDoodle();
        userDoodle.setStatus(UserDoodleStatus.PARTICIPATING);
        userDoodle.setRole(UserDoodleRole.CREATOR);
        userDoodle.setJoinDate(LocalDate.now());
        userDoodle.setUser(user);
        userDoodle.setDoodle(doodle);

        doodle.getParticipants().add(userDoodle);

        userDoodleRepository.save(userDoodle);
        doodleRepository.save(doodle);

        return userDoodle;
    }

    public UserDoodle addParticipantsToUserDoodle(Long doodleId, Long userId) {
        //참가자가 방에 들어올때마다 호출됨
        User user = userRepository.findById(doodleId).orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        Doodle doodle = doodleRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("DOODLE NOT FOUND"));

        initializeParticipantsList(doodle);

        UserDoodle userDoodle = new UserDoodle();
        userDoodle.setStatus(UserDoodleStatus.PARTICIPATING);
        userDoodle.setRole(UserDoodleRole.PARTICIPANT); //참가자역할로 추가
        userDoodle.setJoinDate(LocalDate.now());
        userDoodle.setUser(user);
        userDoodle.setDoodle(doodle);

        doodle.getParticipants().add(userDoodle);

        userDoodleRepository.save(userDoodle);
        doodleRepository.save(doodle);

        return userDoodle;
    }

    public double getWeeklyTotalDistance(List<RunningRecord> runningRecords) {
        //주간 목표 거리 달성 여부
        double weeklyTotalDistance = runningRecords.stream()
                .mapToDouble(RunningRecord::getDistance)
                .sum();
        return weeklyTotalDistance;
    }

    public int getWeeklyGoalCount(List<RunningRecord> runningRecords) {
        return (runningRecords.size());
    }

    public double getWeeklyAveragePace(List<RunningRecord> runningRecords) {
        double averagePace = runningRecords.stream()
                .mapToDouble(RunningRecord::getPace)
                .average()
                .orElse(0.0);
        return averagePace;
    }

    public double getWeeklyAverageCadence(List<RunningRecord> runningRecords) {
        double averageCadence = runningRecords.stream()
                .mapToDouble(RunningRecord::getCadence)
                .average()
                .orElse(0.0);
        return averageCadence;
    }

    public double getWeeklyAverageHeartRate(List<RunningRecord> runningRecords) {
        double averageHeartRate = runningRecords.stream()
                .mapToDouble(RunningRecord::getHeartRate)
                .average()
                .orElse(0.0);
        //runningRecord에서 받아온 heartRate를 heartRateZone으로 변환하는 메서드 필요
        return averageHeartRate;
    }

    //    public boolean checkDoodleRunningLocation(Doodle doodle, GpsCoordinate gpsCoordinate){ //위치 확인
//
//
//    }

    //목표 전체 달성 여부 확인
    public UserDoodleStatus isGoalAchieved(Long userId, Long doodleId) {
        Doodle doodle = doodleRepository.findById(doodleId).orElseThrow(() -> new RuntimeException("Doodle not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserDoodle userDoodle = userDoodleRepository.findByDoodleAndUser(doodle, user).orElseThrow(() -> new RuntimeException("UserDoodle not found"));
        List<RunningRecord> runningRecords = runningRecordRepository.findAllByUser(user);
        boolean isAchieved = ( //심박존, 위치 확인 추가 필요
                (getWeeklyTotalDistance(runningRecords) >= doodle.getWeeklyGoalDistance()) &&
                        (getWeeklyGoalCount(runningRecords) >= doodle.getWeeklyGoalCount()) &&
                        (getWeeklyAveragePace(runningRecords) >= doodle.getWeeklyGoalPace()) &&
                        (getWeeklyAverageCadence(runningRecords) >= doodle.getWeeklyGoalCadence()));
//                (getWeeklyAverageHeartRate(runningRecords) == doodle.getWeeklyGoalHeartRateZone()); //이부분 수정필요
        if (isAchieved) {
            userDoodle.setStatus(UserDoodleStatus.COMPLETED);
            userDoodleRepository.save(userDoodle);
        }
        return userDoodle.getStatus();
    }

    //유저가 참여한 두들런 중 포인트 상위 10개 방 반환
    public List<Doodle> getTop10DoodlePointsForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(0, 10);
//        return topDoodles.stream()
//                .map(Doodle::getDoodlePoint)
//                .collect(Collectors.toList());
        return userDoodleRepository.findTop10ByUserOrderByDoodlePointDesc(user, pageable);
    }
}
