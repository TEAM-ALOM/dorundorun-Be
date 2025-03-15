//package com.alom.dorundorunbe.domain.mypage.service;
//
//import com.alom.dorundorunbe.domain.mypage.dto.MyPageRunningRecordResponse;
//import com.alom.dorundorunbe.domain.mypage.dto.UserUpdateDto;
//import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
//import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
//import com.alom.dorundorunbe.domain.user.domain.User;
//import com.alom.dorundorunbe.domain.user.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MyPageServiceTest {
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private RunningRecordRepository runningRecordRepository;
//
//    @InjectMocks
//    private MyPageService myPageService;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        testUser = User.builder().id(1L).email("user@test.com").isDeleted(false).nickname("test").build();
//    }
//
//    @Test
//    @DisplayName("사용자 정보 수정 성공")
//    void updateUserSuccess(){
//        UserUpdateDto userUpdateDTO = new UserUpdateDto();
//        userUpdateDTO.setNickname("newNickName");
//
//        Mockito.when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
//        Mockito.when(userService.existsByNickname(userUpdateDTO.getNickname())).thenReturn(false);
//
//        ResponseEntity<String> response = myPageService.updateByEmail(userUpdateDTO, testUser.getEmail());
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("User updated successfully", response.getBody());
//
//        Mockito.verify(userService, Mockito.times(1)).findByEmail(testUser.getEmail());
//        Mockito.verify(userService, Mockito.times(1)).save(testUser);
//    }
//
//    @Test
//    @DisplayName("사용자 닉네임 수정 실패: 중복")
//    void updateUserNickNameDuplicate() {
//        UserUpdateDto userUpdateDTO = new UserUpdateDto();
//        userUpdateDTO.setNickname("newNickName");
//
//        Mockito.when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
//        Mockito.when(userService.existsByNickname(userUpdateDTO.getNickname())).thenReturn(true);
//
//        ResponseEntity<String> response = myPageService.updateByEmail(userUpdateDTO, testUser.getEmail());
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("Nickname already exists", response.getBody());
//    }
//
//    @Test
//    @DisplayName("러닝 기록 조회 성공")
//    void getRunningRecordsSuccess() {
//        // Given: 테스트 데이터 준비
//
//        RunningRecord record1 = RunningRecord.builder().id(1L).user(testUser).date(LocalDate.now()).build();
//        RunningRecord record2 = RunningRecord.builder().id(2L).user(testUser).date(LocalDate.now()).build();
//
//        // 수정 가능한 리스트로 변경
//        List<RunningRecord> runningRecords = new ArrayList<>(List.of(record1, record2));
//
//        // Mock 설정
//        Mockito.when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
//        Mockito.when(runningRecordRepository.findAllByUser(testUser)).thenReturn(runningRecords);
//
//        // When: 테스트 실행
//        List<MyPageRunningRecordResponse> result = myPageService.getRunningRecords(testUser.getEmail());
//
//        // Then: 결과 검증
//        assertEquals(2, result.size());
//        assertEquals(record1.getId(), result.get(0).getDate());
//        assertEquals(record2.getId(), result.get(1).getDate());
//
//        // Mock 호출 검증
//        Mockito.verify(userService, Mockito.times(1)).findByEmail(testUser.getEmail());
//        Mockito.verify(runningRecordRepository, Mockito.times(1)).findAllByUser(testUser);
//    }
//}