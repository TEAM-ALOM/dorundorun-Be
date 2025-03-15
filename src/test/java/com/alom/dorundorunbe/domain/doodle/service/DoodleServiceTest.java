package com.alom.dorundorunbe.domain.doodle.service;

import com.alom.dorundorunbe.domain.doodle.domain.Doodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.dto.*;
import com.alom.dorundorunbe.domain.doodle.repository.DoodleRepository;
import com.alom.dorundorunbe.domain.doodle.repository.UserDoodleRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoodleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDoodleRepository userDoodleRepository;

    @Mock
    private DoodleRepository doodleRepository;

    @Mock
    private UserDoodleService userDoodleService;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private DoodleService doodleService;

    private static User user;
    private static User user2;
    private static DoodleRequestDto doodleRequestDto;
    private static Doodle doodle1;
    private static Doodle doodle2;
    private static UserDoodleDto userDoodleDto;
    private static UserDoodle userDoodle;
    private static UserDoodle CreatorUserDoodle;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .nickname("runner123")
                .email("example@example.com")
                .cash(1000L)
                .tier(Tier.AMATEUR)
                .build();

        user2 = User.builder()
                .id(2L)
                .nickname("runner456")
                .email("example2@example.com")
                .cash(1000L)
                .tier(Tier.AMATEUR)
                .build();

        doodle1 = Doodle.builder()
                .id(1L)
                .name("Doodle 1")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(50.0)
                .weeklyGoalPace(5.0)
                .weeklyGoalHeartRateZone(3)
                .goalParticipationCount(10)
                .maxParticipant(5)
                .participants(new ArrayList<>())  // 리스트 비워놓음
                .isGoalActive(true)
                .isRunning(true)
                .doodlePoint(100)
                .requiredTier(Tier.STARTER)
                .build();

        doodle2 = Doodle.builder()
                .id(2L)
                .name("Doodle 2")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(60.0)
                .weeklyGoalPace(6.0)
                .weeklyGoalHeartRateZone(3)
                .goalParticipationCount(20)
                .maxParticipant(10)
                .participants(new ArrayList<>())  // 리스트 비워놓음
                .isGoalActive(true)
                .isRunning(true)
                .doodlePoint(50)
                .requiredTier(Tier.AMATEUR)
                .build();

        userDoodle = UserDoodle.builder()
                .id(1L)
                .user(user)
                .doodle(doodle1)
                .status(UserDoodleStatus.PARTICIPATING)
                .role(UserDoodleRole.PARTICIPANT)
                .joinDate(LocalDate.now())
                .build();

        CreatorUserDoodle = UserDoodle.builder()
                .id(2L)
                .user(user)
                .doodle(doodle1)
                .status(UserDoodleStatus.PARTICIPATING)
                .role(UserDoodleRole.CREATOR)
                .joinDate(LocalDate.now())
                .build();

        doodleRequestDto = DoodleRequestDto.builder()
                .name("Test Doodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .weeklyGoalHeartRateZone(3)
                .maxParticipant(10)
                .userId(1L)
                .isGoalActive(true)
                .isPublic(true)
                .isRunning(true)
                .requiredTier(Tier.STARTER)
                .build();
    }

    @Test
    @DisplayName("createDoodle : Doodle 생성에 성공한다.")
    public void createDoodle() {
        // mock doodleRepository
        Doodle savedDoodle = Doodle.builder()
                .id(1L)
                .name("Test Doodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .goalParticipationCount(10)
                .weeklyGoalHeartRateZone(3)
                .isPublic(true)
                .isGoalActive(true)
                .maxParticipant(10)
                .participants(new ArrayList<>())
                .isRunning(true)
                .requiredTier(Tier.STARTER)
                .build();
        when(doodleRepository.save(any(Doodle.class))).thenReturn(savedDoodle);

        // mock userDoodleService
        UserDoodle userDoodle = new UserDoodle();
        userDoodle.setRole(UserDoodleRole.CREATOR);
        userDoodle.setUser(user);
        userDoodle.setDoodle(savedDoodle);
        when(userDoodleService.createUserDoodle(1L, 1L)).thenReturn(userDoodle);

        // service call
        DoodleResponseDto responseDto = doodleService.createDoodle(doodleRequestDto);

        // Assertions
        assertNotNull(responseDto);
        assertEquals("Test Doodle", responseDto.getName());

        // userDoodleService가 정확히 호출되었는지 확인
        verify(userDoodleService, times(1)).createUserDoodle(1L, 1L);

        // doodleRepository가 제대로 저장되었는지 확인
        verify(doodleRepository, times(1)).save(any(Doodle.class));

        // 생성된 doodle 정보 확인
        assertEquals(Tier.STARTER, responseDto.getRequiredTier());
        assertEquals(2.0, responseDto.getWeeklyGoalCadence());
        assertTrue(responseDto.isRunning());
    }

    @Test
    @DisplayName("getAllDoodles : Doodle 전체 조회에 성공한다.")
    public void getAllDoodles() {
        //given : DoodleRepository의 findAll()이 호출되었을 때 정의된 값 반환
        when(doodleRepository.findAll()).thenReturn(Arrays.asList(doodle1, doodle2));
        //when : getAllDoodles call
        List<DoodleResponseDto> doodleResponseDtos = doodleService.getAllDoodles();
        //then
        verify(doodleRepository, times(1)).findAll(); //findAll이 1번 호출되었는지
        assert doodleResponseDtos.size() == 2;
        assert doodleResponseDtos.get(0).getName().equals("Doodle 1");
        assert doodleResponseDtos.get(1).getName().equals("Doodle 2");
    }

    @Test
    @DisplayName("getDoodleById : Doodle 조회에 성공한다.")
    public void getDoodleById() {
        //given
        when(doodleRepository.findById(anyLong())).thenReturn(Optional.of(doodle1));
        //when
        DoodleResponseDto doodleResponseDto = doodleService.getDoodleById(1L);
        //then
        assertNotNull(doodleResponseDto);
        assertEquals("Doodle 1", doodleResponseDto.getName());
        assertEquals(1L, doodleResponseDto.getId());

        verify(doodleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteDoodle : Doodle 삭제에 성공한다.")
    public void deleteDoodle() {
        Doodle doodle = Doodle.builder()
                .id(1L)
                .name("Delete Test Doodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .goalParticipationCount(10)
                .maxParticipant(10)
                .participants(new ArrayList<>())
                .build();

        when(doodleRepository.findById(1L)).thenReturn(Optional.of(doodle));

        doNothing().when(doodleRepository).delete(doodle);

        doodleService.deleteDoodle(1L);

        verify(doodleRepository, times(1)).findById(1L);
        verify(doodleRepository, times(1)).delete(doodle);
    }

    @Test
    @DisplayName("updateDoodle : Doodle 수정에 성공한다.")
    public void updateDoodle() {
        Doodle oldDoodle = Doodle.builder()
                .id(1L)
                .name("Test Doodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .weeklyGoalHeartRateZone(3)
                .goalParticipationCount(10)
                .maxParticipant(10)
                .participants(new ArrayList<>())
                .isGoalActive(true)
                .build();

        DoodleRequestDto updatedDoodleRequestDto = DoodleRequestDto.builder()
                .name("Updated Test Doodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(3.0)
                .weeklyGoalPace(4.0)
                .weeklyGoalHeartRateZone(3)
                .maxParticipant(20)
                .isGoalActive(true)
                .isRunning(true)
                .build();

        when(doodleRepository.findById(1L)).thenReturn(Optional.of(oldDoodle));
        when(doodleRepository.save(any(Doodle.class))).thenReturn(oldDoodle);

        DoodleResponseDto responseDto = doodleService.updateDoodle(1L, updatedDoodleRequestDto);

        assertNotNull(responseDto);
        assertEquals("Updated Test Doodle", responseDto.getName());
        assertEquals(1L, responseDto.getId());

        verify(doodleRepository, times(1)).findById(1L);
        verify(doodleRepository, times(1)).save(oldDoodle);
    }

    @Test
    @DisplayName("addParticipantToDoodle : addParticipantToUserDoodle 호출에 성공한다.") //UserDoodleService에서 기능 테스트 필요
    public void addParticipantToDoodle() {
        // When: Mockito Mock 설정
        when(doodleRepository.findById(anyLong())).thenReturn(Optional.of(doodle1)); // doodleRepository Mock 설정
//        when(passwordEncoder.matches(eq("testPassword"), eq(doodle1.getPassword()))).thenReturn(true); // 비밀번호 체크 Mock 설정

        // Service 호출: 참가자 추가
        DoodleResponseDto doodleResponseDto = doodleService.addParticipantToDoodle(doodle1.getId(), user.getId());

        // Then: 결과 검증
        assertNotNull(doodleResponseDto);  // DoodleResponseDto가 null이 아니어야 함
        verify(userDoodleService, times(1)).addParticipantsToUserDoodle(doodle1.getId(), user.getId());
    }


    @Test
    @DisplayName("deleteParticipant : Doodle 참가자 삭제에 성공한다.")
    public void deleteParticipant() {
        when(doodleRepository.findById(anyLong())).thenReturn(Optional.of(doodle1));
        when(userDoodleRepository.findByDoodleAndUser(doodle1, user))
                .thenReturn(Optional.of(userDoodle));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        doodle1.getParticipants().add(userDoodle);

        DoodleResponseDto doodleResponseDto = doodleService.deleteParticipant(doodle1.getId(), user.getId());

        assertNotNull(doodleResponseDto);
        assertEquals(doodle1.getId(), doodleResponseDto.getId());
        assertEquals(0, doodleResponseDto.getParticipants().size());

        verify(userDoodleRepository, times(1)).delete(userDoodle);
        verify(doodleRepository, times(1)).save(doodle1);
    }

    @Test
    @DisplayName("getParticipants : Doodle 참가자 전체 조회에 성공한다.")
    public void getParticipants() {
        List<UserDoodle> userDoodleList = new ArrayList<>();
        userDoodleList.add(userDoodle);
        doodle1.setParticipants(userDoodleList);

        when(doodleRepository.findById(1L)).thenReturn(Optional.of(doodle1));

        List<UserDoodleDto> participants = doodleService.getParticipants(1L);

        assertNotNull(participants);
        assertEquals(1, participants.size());
        assertEquals(user.getId(), participants.get(0).getUserId());

        verify(doodleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateParticipantStatus : Doodle 참가자 완료 상태 수정에 성공한다.")
    public void updateParticipantStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(doodleRepository.findById(anyLong())).thenReturn(Optional.of(doodle1));
        when(userDoodleRepository.findByDoodleAndUser(any(Doodle.class), any(User.class))).thenReturn(Optional.of(userDoodle));
        when(userDoodleRepository.save(any(UserDoodle.class))).thenReturn(userDoodle);

        userDoodleDto = doodleService.updateParticipantStatus(doodle1.getId(), user.getId(), UserDoodleStatus.COMPLETED);

        assertNotNull(userDoodleDto);
        assertEquals(UserDoodleStatus.COMPLETED, userDoodleDto.getStatus());

        verify(userDoodleRepository, times(1)).save(any(UserDoodle.class));
        verify(userDoodleRepository, times(1)).findByDoodleAndUser(any(Doodle.class), any(User.class));
    }

//    @Test
//    @DisplayName("updateDoodlePassword : Doodle 비밀번호 변경에 성공한다.")
//    public void updateDoodlePassword(){
//        String newPassword = "newSecurePassword";
//        String encodedPassword = "encodedPassword";
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
//        when(doodleRepository.findById(anyLong())).thenReturn(Optional.of(doodle1));
//        when(userDoodleRepository.findByDoodleAndUser(any(Doodle.class), any(User.class))).thenReturn(Optional.of(CreatorUserDoodle));
//        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
//        when(doodleRepository.save(any(Doodle.class))).thenReturn(doodle1);
//
//        DoodleResponseDto doodleResponseDto = doodleService.updateDoodlePassword(doodle1.getId(), user2.getId(), newPassword);
//        assertNotNull(doodleResponseDto);
//        assertEquals(doodle1.getId(), doodleResponseDto.getId());
//        verify(passwordEncoder, times(1)).encode(newPassword);
//        verify(doodleRepository, times(1)).save(doodle1);
//    }

    @Test
    @DisplayName("generateDoodleInviteCode : Doodle방 초대 코드 생성에 성공한다.")
    public void generateDoodleInviteCode() {
        when(redisUtil.getData(anyString(), eq(String.class))).thenReturn(Optional.of("testInviteCode"));
        DoodleInviteCodeResponse doodleInviteCodeReponse = doodleService.generateDoodleInviteCode(doodle1.getId());

        Optional<String> data = redisUtil.getData("doodleId=%d".formatted(doodle1.getId()), String.class);
        assertNotNull(data);
        assertTrue(data.isPresent());
        assertThat(data.get()).isEqualTo(doodleInviteCodeReponse.code());
    }

}