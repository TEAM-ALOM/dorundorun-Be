package com.alom.dorundorunbe.domain.doodle.service;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.dto.*;
import com.alom.dorundorunbe.domain.doodle.repository.UserDoodleRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodle;
import com.alom.dorundorunbe.domain.doodle.domain.Doodle;
import com.alom.dorundorunbe.domain.doodle.repository.DoodleRepository;
import com.alom.dorundorunbe.global.config.RedisConfig;
import com.alom.dorundorunbe.global.util.CustomRandomUtil;
import com.alom.dorundorunbe.global.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoodleService {

    private final DoodleRepository doodleRepository;
    private final UserRepository userRepository;
    private final UserDoodleRepository userDoodleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDoodleService userDoodleService;
    private final RedisConfig redisConfig;
    private final RedisUtil redisUtil;

    private static final String INVITE_LINK_PREFIX = "doodleId=%d";

    @Transactional
    public DoodleResponseDto createDoodle(DoodleRequestDto doodleRequestDto) { //doodle 생성 기능
        //Doodle 생성
        Doodle doodle = Doodle.builder()
                .name(doodleRequestDto.getName())
                .maxParticipant(doodleRequestDto.getMaxParticipant())
                .participants(new ArrayList<>())
                .isRunning(doodleRequestDto.isRunning())
                .isPublic(doodleRequestDto.isPublic())
                .isGoalActive(doodleRequestDto.isGoalActive())
                .doodlePoint(0)
                .build();

        if (doodle.isGoalActive() && doodle.isRunning()){ //주간 목표 활성화, 활성화 모드가 달리기일 경우
          doodle.setWeeklyGoalDistance(doodleRequestDto.getWeeklyGoalDistance());
          doodle.setWeeklyGoalCount(doodleRequestDto.getWeeklyGoalCount());
          doodle.setWeeklyGoalCadence(doodleRequestDto.getWeeklyGoalCadence());
          doodle.setWeeklyGoalPace(doodleRequestDto.getWeeklyGoalPace());
          doodle.setWeeklyGoalHeartRateZone(doodleRequestDto.getWeeklyGoalHeartRateZone());
          doodle.setGoalParticipationCount(doodleRequestDto.getGoalParticipationCount());
          doodle.setRequiredTier(doodleRequestDto.getRequiredTier());
          //위치 추가 필요
        }
        else if (doodle.isGoalActive() && !doodle.isRunning()){ //주간 목표 활성화, 활성화 모드가 걷기일 경우
            doodle.setWeeklyGoalDistance(doodleRequestDto.getWeeklyGoalDistance());
            doodle.setWeeklyGoalCount(doodleRequestDto.getWeeklyGoalCount());
            //위치 추가 필요
            doodle.setWeeklyGoalCadence(null);
            doodle.setWeeklyGoalPace(null);
            doodle.setWeeklyGoalHeartRateZone(null);
            doodle.setGoalParticipationCount(null);
            doodle.setRequiredTier(null);
        }

        else{ //주간 목표 비활성화
            doodle.setWeeklyGoalDistance(null);
            doodle.setWeeklyGoalCount(null);
            doodle.setWeeklyGoalCadence(null);
            doodle.setWeeklyGoalPace(null);
            doodle.setWeeklyGoalHeartRateZone(null);
            doodle.setGoalParticipationCount(null);
            doodle.setRequiredTier(null);
        }

        Doodle savedDoodle = doodleRepository.save(doodle);
        UserDoodle userDoodle = userDoodleService.createUserDoodle(savedDoodle.getId(), doodleRequestDto.getUserId());

        savedDoodle.getParticipants().add(userDoodle);

        return DoodleResponseDto.from(savedDoodle);
    }

    @Transactional
    public List<DoodleResponseDto> getAllDoodles() { //doodle 전체 조회
        List<Doodle> doodles = doodleRepository.findAll();
        return doodles.stream()
                //map연산자로 각 Doodle 객체를 DoodleResponseDto로 변환
                .map(DoodleResponseDto::from)
                .collect(Collectors.toList());
    }

    public DoodleResponseDto getDoodleById(Long doodleId) { //doodle 상세 조회
        Optional<Doodle> doodle = doodleRepository.findById(doodleId);
        return doodle.map(DoodleResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("NOT FOUND"));
    }

    public void deleteDoodle(Long doodleId) {
        Doodle doodle = doodleRepository.findById(doodleId)
                .orElseThrow(() -> new IllegalArgumentException("NOT FOUND"));

        doodleRepository.delete(doodle);
    }

    @Transactional
    public DoodleResponseDto updateDoodle(Long doodleId, DoodleRequestDto doodleRequestDto) {
        Doodle doodle = doodleRepository.findById(doodleId)
                .orElseThrow(() -> new IllegalArgumentException("NOT FOUND"));
        doodle.setName(doodleRequestDto.getName());
        doodle.setMaxParticipant(doodleRequestDto.getMaxParticipant());
        doodle.setRunning(doodleRequestDto.isRunning());
        doodle.setPublic(doodleRequestDto.isPublic());
        doodle.setGoalActive(doodleRequestDto.isGoalActive());

        if (doodle.isGoalActive() && doodle.isRunning()) {
            doodle.setWeeklyGoalDistance(doodleRequestDto.getWeeklyGoalDistance());
            doodle.setWeeklyGoalCount(doodleRequestDto.getWeeklyGoalCount());
            doodle.setWeeklyGoalCadence(doodleRequestDto.getWeeklyGoalCadence());
            doodle.setWeeklyGoalPace(doodleRequestDto.getWeeklyGoalPace());
            doodle.setWeeklyGoalHeartRateZone(doodleRequestDto.getWeeklyGoalHeartRateZone());
            doodle.setGoalParticipationCount(doodleRequestDto.getGoalParticipationCount());
            doodle.setRequiredTier(doodleRequestDto.getRequiredTier());
            //위치 추가 필요
        }
        else if (doodle.isGoalActive() && !doodle.isRunning()){
            doodle.setWeeklyGoalDistance(doodleRequestDto.getWeeklyGoalDistance());
            doodle.setWeeklyGoalCount(doodleRequestDto.getWeeklyGoalCount());
            //위치 추가 필요
            doodle.setWeeklyGoalCadence(null);
            doodle.setWeeklyGoalPace(null);
            doodle.setWeeklyGoalHeartRateZone(null);
            doodle.setGoalParticipationCount(null);
            doodle.setRequiredTier(null);
        }
        else{
            doodle.setWeeklyGoalDistance(null);
            doodle.setWeeklyGoalCount(null);
            doodle.setWeeklyGoalCadence(null);
            doodle.setWeeklyGoalPace(null);
            doodle.setWeeklyGoalHeartRateZone(null);
            doodle.setGoalParticipationCount(null);
            doodle.setRequiredTier(null);
        }

        Doodle updatedDoodle = doodleRepository.save(doodle);
        return DoodleResponseDto.from(updatedDoodle);
    }

    @Transactional
    public DoodleResponseDto addParticipantToDoodle(Long doodleId, Long userId) { //참가자 추가
        Doodle doodle = doodleRepository.findById(doodleId).orElseThrow(() -> new RuntimeException("Doodle not found"));
        //비밀번호 검증
//        if (!passwordEncoder.matches(password, doodle.getPassword())) {
//            throw new IllegalArgumentException("Wrong password");
//        }

        //참가자 중복 체크
        if (doodle.IsDuplicatedParticipant(doodle, userId)) {
            throw new IllegalArgumentException("Duplicated Participant");
        }

        //참가자 인원 제한 수 체크
        if (!doodle.checkCanAddParticipant(doodle.getParticipants().size())) {
            throw new IllegalArgumentException("Full participants");
        }

        userDoodleService.addParticipantsToUserDoodle(doodleId, userId);
        return DoodleResponseDto.from(doodle);
    }

    @Transactional
    public DoodleResponseDto deleteParticipant(Long doodleId, Long userId) {
        Doodle doodle = doodleRepository.findById(doodleId).
                orElseThrow(() -> new RuntimeException("NOT FOUND"));
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("NOT FOUND"));
        UserDoodle userDoodle = userDoodleRepository.findByDoodleAndUser(doodle, user).
                orElseThrow(() -> new RuntimeException("유저가 해당 Doodle에 존재하지 않습니다."));


        userDoodleRepository.delete(userDoodle);
        doodle.getParticipants().remove(userDoodle);
        doodleRepository.save(doodle);
        return DoodleResponseDto.from(doodle);
    }

    public List<UserDoodleDto> getParticipants(Long doodleId) {
        Doodle doodle = doodleRepository.findById(doodleId)
                .orElseThrow(() -> new IllegalArgumentException("NOT FOUND"));

        return doodle.getParticipants().stream()
                .map(UserDoodleDto::from)
                .collect(Collectors.toList());
    }

    //참가자 doodle 완료 상태 업데이트 로직 구현
    @Transactional
    public UserDoodleDto updateParticipantStatus(Long doodleId, Long userId, UserDoodleStatus status) {
        Doodle doodle = doodleRepository.findById(doodleId).
                orElseThrow(() -> new RuntimeException("NOT FOUND"));
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("NOT FOUND"));
        UserDoodle userDoodle = userDoodleRepository.findByDoodleAndUser(doodle, user)
                .orElseThrow(() -> new IllegalArgumentException("NOT FOUND"));
        userDoodle.setStatus(status);
        userDoodleRepository.save(userDoodle);
        return UserDoodleDto.from(userDoodle);
    }

//    //doodle 비밀번호 변경
//    @Transactional
//    public DoodleResponseDto updateDoodlePassword(Long doodleId, Long userId, String newPassword) {
//        Doodle doodle = doodleRepository.findById(doodleId).orElseThrow(() -> new RuntimeException(("Doodle not found")));
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        UserDoodle userDoodle = userDoodleRepository.findByDoodleAndUser(doodle, user).orElseThrow(() -> new RuntimeException("UserDoodle not found"));
//        if (userDoodle.getRole() != UserDoodleRole.CREATOR) {
//            throw new IllegalArgumentException("비밀번호를 변경할 자격이 없습니다.");
//        }
//        String encodedPassword = passwordEncoder.encode(newPassword);
//        Doodle updatedDoodle = doodleRepository.save(doodle);
//        return DoodleResponseDto.from(updatedDoodle);
//    }

    //doodle 방에 포인트 지급
    @Transactional
    public void addPointsToDoodle(Long doodleId, Long userId, double doodlePoints){
        Doodle doodle = doodleRepository.findById(doodleId).orElseThrow(()->new RuntimeException("Doodle not found"));
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        doodle.setDoodlePoint(doodlePoints);
        doodleRepository.save(doodle);
    }

    //Doodle 포인트 상위 10개 방의 포인트 반환
    public List<Double> getTop10PointsForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(0, 10);
        List<Doodle> topDoodles = userDoodleRepository.findTop10ByUserOrderByDoodlePointDesc(user, pageable);

        return topDoodles.stream()
                .map(Doodle::getDoodlePoint)
                .collect(Collectors.toList());
    }

    //Doodle방 초대 코드 생성 기능
    public DoodleInviteCodeResponse generateDoodleInviteCode(Long doodleId){
        Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(doodleId), String.class);
        if (link.isEmpty()){
            String randomCode = CustomRandomUtil.generateRandomCode(10);
            redisUtil.setData(INVITE_LINK_PREFIX.formatted(doodleId), randomCode);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(doodleId), randomCode, RedisUtil.toTomorrow());
            return new DoodleInviteCodeResponse(randomCode);
        }
        return new DoodleInviteCodeResponse(link.get());
    }

    //초대코드로 Doodle에 유저를 초대
    public void joinDoodleByInviteCode(Long doodleId, Long userId, DoodleInviteCodeRequest request){
        Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(doodleId), String.class);

        if (link.isPresent()){
            if(!link.equals(request.code())) throw new IllegalArgumentException("NOT_MATCH_LINK");
            addParticipantToDoodle(doodleId, userId);
        }
        else{
            throw new IllegalArgumentException("EXPIRED_LINK");
        }
    }


}
