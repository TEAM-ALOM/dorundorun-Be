package com.alom.dorundorunbe.domain.doodle.controller;

import com.alom.dorundorunbe.domain.doodle.domain.Doodle;
import com.alom.dorundorunbe.domain.doodle.domain.UserDoodleStatus;
import com.alom.dorundorunbe.domain.doodle.dto.DoodleRequestDto;
import com.alom.dorundorunbe.domain.doodle.dto.DoodleResponseDto;
import com.alom.dorundorunbe.domain.doodle.dto.UserDoodleDto;
import com.alom.dorundorunbe.domain.doodle.dto.UserDoodleRole;
import com.alom.dorundorunbe.domain.doodle.service.DoodleService;
import com.alom.dorundorunbe.domain.doodle.service.UserDoodleService;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import com.alom.dorundorunbe.global.enums.Tier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoodleController.class)
public class DoodleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoodleService doodleService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDoodleService userDoodleService;

    static private Doodle doodle;

    static private User user;

    static private DoodleRequestDto doodleRequestDto;

    static private DoodleResponseDto doodleResponseDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .nickname("runner123")
                .email("example@example.com")
                .cash(1000L)
                .tier(Tier.AMATEUR)
                .build();

        doodle = Doodle.builder()
                .id(1L)
                .name("testDoodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .weeklyGoalHeartRateZone(3)
                .goalParticipationCount(10)
                .maxParticipant(20)
                .participants(new ArrayList<>())
                .build();

        doodleRequestDto = DoodleRequestDto.builder()
                .name("testDoodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .weeklyGoalHeartRateZone(3)
                .maxParticipant(20)
                .build();

        doodleResponseDto = DoodleResponseDto.builder()
                .id(1L)
                .name("testDoodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(2.0)
                .weeklyGoalPace(3.0)
                .weeklyGoalHeartRateZone(3)
                .goalParticipationCount(10)
                .maxParticipant(20)
                .build();
    }

    @Test
    @DisplayName("Post /create/{userId} : Doodle을 생성한다.")
    @WithMockUser(username = "runner123", roles = {"USER"})
    public void createDoodle() throws Exception {
        // given
        when(doodleService.createDoodle(eq(1L), any(DoodleRequestDto.class)))
                .thenReturn(doodleResponseDto);

        // when & then
        mockMvc.perform(post("/doodle/create/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
                        .content(new ObjectMapper().writeValueAsString(doodleRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // 응답 타입 검증
                .andExpect(jsonPath("$.name").value("testDoodle"))
                .andExpect(jsonPath("$.weeklyGoalDistance").value(1.0));

        // verify
        verify(doodleService, times(1))
                .createDoodle(eq(1L), any(DoodleRequestDto.class));
    }

    @Test
    @DisplayName("Get /{doodleId} : 특정 Doodle을 조회한다.")
    @WithMockUser(username = "runner123", roles = {"USER"})
    public void getDoodleById() throws Exception{
        when(doodleService.getDoodleById(1L)).thenReturn(DoodleResponseDto.from(doodle));

        mockMvc.perform(get("/doodle/{doodleId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.weeklyGoalDistance").value(1.0))
                .andExpect(jsonPath("$.weeklyGoalCount").value(1))
                .andExpect(jsonPath("$.weeklyGoalCadence").value(2.0))
                .andExpect(jsonPath("$.weeklyGoalPace").value(3.0))
                .andExpect(jsonPath("$.goalParticipationCount").value(10))
                .andExpect(jsonPath("$.maxParticipant").value(20)
        );
    }

    @Test
    @DisplayName("Get /doodle : 모든 Doodle을 조회한다.")
    @WithMockUser(username = "runner123", roles = {"USER"})
    public void getAllDoodles() throws Exception{
       List<DoodleResponseDto> doodleResponseDtos = Arrays.asList(
               DoodleResponseDto.builder()
                       .id(1L)
                       .name("testDoodle1")
                       .weeklyGoalDistance(5.0)
                       .weeklyGoalCount(1)
                       .weeklyGoalCadence(3.0)
                       .weeklyGoalPace(2.0)
                       .goalParticipationCount(10)
                       .maxParticipant(20)
                       .build(),

               DoodleResponseDto.builder()
                       .id(2L)
                       .name("testDoodle2")
                       .weeklyGoalDistance(10.0)
                       .weeklyGoalCount(1)
                       .weeklyGoalCadence(4.0)
                       .weeklyGoalPace(3.0)
                       .goalParticipationCount(15)
                       .maxParticipant(30)
                       .build()
       );

       when(doodleService.getAllDoodles()).thenReturn(doodleResponseDtos);

       mockMvc.perform(get("/doodle")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("testDoodle1"))
               .andExpect(jsonPath("$[1].name").value("testDoodle2"));

       verify(doodleService, times(1)).getAllDoodles();

    }

    @Test
    @DisplayName("Delete /{doodleId} : 특정 Doodle을 삭제한다.")
    @WithMockUser(username = "runner123", roles = {"ADMIN"})
    public void deleteDoodle() throws Exception {
       doNothing().when(doodleService).deleteDoodle(1L);

       mockMvc.perform(delete("/doodle/{doodleId}", 1L)
                       .with(csrf()))
               .andExpect(status().isNoContent());

       verify(doodleService, times(1)).deleteDoodle(1L);
    }

    @Test
    @DisplayName("Put /doodle/{doodleId} : 특정 Doodle을 수정한다.")
    @WithMockUser(username = "runner123", roles = {"ADMIN"})
    public void updatedDoodle() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        DoodleRequestDto updateRequest = DoodleRequestDto.builder()
                .name("Updated Doodle Name")
                .weeklyGoalDistance(2.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(3.0)
                .weeklyGoalPace(4.0)
                .maxParticipant(25)
                .build();

        DoodleResponseDto updatedDoodleResponse = DoodleResponseDto.builder()
                .id(1L)
                .name("Updated Doodle Name")
                .weeklyGoalDistance(2.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(3.0)
                .weeklyGoalPace(4.0)
                .goalParticipationCount(15)
                .maxParticipant(25)
                .build();

        // mockService 메소드가 updateDoodle을 호출하는지 검증
        when(doodleService.updateDoodle(eq(1L), any(DoodleRequestDto.class))).thenReturn(updatedDoodleResponse);
        // MockMvc 요청 및 응답 검증
        mockMvc.perform(put("/doodle/{doodleId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON) // Content-Type 설정
                        .with(csrf()) // CSRF 설정
                        .content(objectMapper.writeValueAsString(updateRequest))) // requestBody로 updateRequest 전달
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 응답 content type 확인
                .andExpect(jsonPath("$.name").value("Updated Doodle Name")) // name 값 확인
                .andExpect(jsonPath("$.weeklyGoalDistance").value(2.0)) // goalDistance 값 확인
                .andExpect(jsonPath("$.weeklyGoalCount").value(1))
                .andExpect(jsonPath("$.weeklyGoalCadence").value(3.0)) // goalCadence 값 확인
                .andExpect(jsonPath("$.weeklyGoalPace").value(4.0)) // goalPace 값 확인
                .andExpect(jsonPath("$.goalParticipationCount").value(15)) // goalParticipationCount 값 확인
                .andExpect(jsonPath("$.maxParticipant").value(25)); // maxParticipant 값 확인

        // updateDoodle 메소드가 한 번 호출되었는지 확인
        verify(doodleService, times(1)).updateDoodle(eq(1L), any(DoodleRequestDto.class));
    }


    @Test
    @DisplayName("Post /{doodleId}/User/{userId} : 특정 Doodle에 User를 추가한다.")
    @WithMockUser(username = "runner123", roles = {"USER"})
    public void addParticipantToDoodle() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        when(doodleService.addParticipantToDoodle(eq(1L), eq(1L)))
                .thenReturn(doodleResponseDto);

        mockMvc.perform(post("/doodle/{doodleId}/User/{userId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doodleRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testDoodle"));

        verify(doodleService, times(1)).addParticipantToDoodle(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("Delete /{doodleId}/User/{userId} : 특정 Doodle의 User를 삭제한다.")
    @WithMockUser(username = "runner123", roles = {"ADMIN"})
    public void deleteParticipant() throws Exception{
        DoodleResponseDto updatedDoodleResponse = DoodleResponseDto.builder()
                .id(1L)
                .name("Test Doodle")
                .weeklyGoalDistance(1.0)
                .weeklyGoalCount(1)
                .weeklyGoalCadence(3.0)
                .weeklyGoalPace(4.0)
                .goalParticipationCount(10)
                .maxParticipant(20)
                .participants(List.of()) // 참가자 제거 후 비어 있는 상태
                .build();

        when(doodleService.deleteParticipant(eq(1L), eq(1L))).thenReturn(updatedDoodleResponse);

        mockMvc.perform(delete("/doodle/{doodleId}/User/{userId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants").isEmpty());

        verify(doodleService, times(1)).deleteParticipant(1L,1L);
    }

    @Test
    @DisplayName("Get /{doodleId}/participants : 특정 Doodle의 모든 참가자를 조회한다.")
    @WithMockUser(username = "runner123", roles = {"USER"})
    public void getParticipants() throws Exception {
        List<UserDoodleDto> participants = List.of(
                UserDoodleDto.builder()
                        .id(1L)
                        .userId(1L)
                        .userName("user1")
                        .doodleId(1L)
                        .joinDate(LocalDate.now())
                        .role(UserDoodleRole.CREATOR)
                        .status(UserDoodleStatus.PARTICIPATING)
                        .build(),
                UserDoodleDto.builder()
                        .id(2L)
                        .userId(2L)
                        .userName("user2")
                        .doodleId(1L)
                        .joinDate(LocalDate.now())
                        .role(UserDoodleRole.PARTICIPANT)
                        .status(UserDoodleStatus.PARTICIPATING)
                        .build()
        );

        when(doodleService.getParticipants(eq(1L))).thenReturn(participants);

        mockMvc.perform(get("/doodle/{doodleId}/participants", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[1].userId").value(2L));

        verify(doodleService, times(1)).getParticipants(eq(1L));
    }

    @Test
    @DisplayName("Put /{doodleId}/participants/{userId} : 특정 Doodle의 User 완료 상태를 변경한다.")
    @WithMockUser(username = "runner123", roles = {"USER"})
    public void updateParticipantStatus() throws Exception{
        when(doodleService.getDoodleById(eq(1L))).thenReturn(DoodleResponseDto.from(doodle));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        UserDoodleStatus updatedStatus = UserDoodleStatus.COMPLETED;

        UserDoodleDto userDoodleDto = UserDoodleDto.builder()
                .id(1L)
                .status(updatedStatus)
                .userName("user1")
                .userId(1L)
                .role(UserDoodleRole.CREATOR)
                .joinDate(LocalDate.now())
                .doodleId(1L)
                .build();

        when(doodleService.updateParticipantStatus(eq(1L), eq(1L), eq(updatedStatus))).thenReturn(userDoodleDto);

        mockMvc.perform(put("/doodle/{doodleId}/participants/{userId}", 1L, 1L)
                        .param("status", updatedStatus.name())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(jsonPath("$.status").value(updatedStatus.name()))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.doodleId").value(1L));

        verify(doodleService, times(1)).updateParticipantStatus(1L,1L, updatedStatus);
    }

//    @Test
//    @DisplayName("Put /{doodleId}/password} : 특정 Doodle의 비밀번호를 변경한다.")
//    @WithMockUser(username = "runner123", roles = {"USER"})
//    public void updateDoodlePassword() throws Exception {
//        String newPassword = "newSecurePassword";
//
//        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
//        when(doodleService.updateDoodlePassword(eq(1L), eq(1L), eq(newPassword)))
//                .thenReturn(doodleResponseDto);
//        mockMvc.perform(put("/doodle/{doodleId}/password", 1L)
//                .param("userId", String.valueOf(user.getId()))
//                .param("newPassword", newPassword)
//                .contentType(MediaType.APPLICATION_JSON)
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(doodleResponseDto.getId()))
//                .andExpect(jsonPath("$.name").value(doodleResponseDto.getName()));
//    }
}
