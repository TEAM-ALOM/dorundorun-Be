//package com.alom.dorundorunbe.domain.mypage.controller;
//
//import com.alom.dorundorunbe.domain.mypage.dto.MyPageRunningRecordResponse;
//import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
//import com.alom.dorundorunbe.domain.mypage.dto.AchievementResponse;
//import com.alom.dorundorunbe.domain.mypage.dto.UserUpdateDto;
//import com.alom.dorundorunbe.domain.mypage.service.MyPageService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest(MyPageController.class)
//@AutoConfigureMockMvc
//class MyPageControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MyPageService myPageService;
//
//    @Test
//    @DisplayName("마이페이지 조회")
//    @WithMockUser(username = "testUser@test.com")
//    void myPageForReturnUserInfo() throws Exception {
//        String email = "testUser@test.com";
//
//        Mockito.when(myPageService.getAchievements(email))
//                .thenReturn(List.of(new AchievementResponse(1L, "testAchievement")));
//        Mockito.when(myPageService.getUserRank(email)).thenReturn("TestRank");
//        Mockito.when(myPageService.getRunningRecords(email))
//                .thenReturn(List.of(new MyPageRunningRecordResponse())); //mock data 채워야 함.
//        Mockito.when(myPageService.getUserNickname(email)).thenReturn("TestNickname");
//
//        mockMvc.perform(get("/mypage/"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value(email))
//                .andExpect(jsonPath("$.rank").value("TestRank"))
//                .andExpect(jsonPath("$.nickname").value("TestNickname"));
//    }
//
//    @Test
//    @DisplayName("유저 정보 수정")
//    @WithMockUser(username = "testUser@test.com")
//    void updateUserUpdateSuccess() throws Exception {
//        String username = "testUser@test.com";
//
//        Mockito.when(myPageService.updateByEmail(any(UserUpdateDto.class), eq(username)))
//                .thenReturn(ResponseEntity.ok("User Update Success"));
//
//        mockMvc.perform(put("/mypage/update")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf())
//                        .content("""
//                        {
//                          "nickname": "newTest"
//                        }
//                        """))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User Update Success"));
//    }
//
//    @Test
//    @DisplayName("회원 탈퇴")
//    @WithMockUser(username = "testUser@test.com")
//    void deleteUserSuccess() throws Exception {
//        String username = "testUser@test.com";
//
//        Mockito.when(myPageService.deleteUser(eq(username)))
//                .thenReturn(ResponseEntity.ok("User Deleted successfully"));
//
//        mockMvc.perform(delete("/mypage/delete")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User Deleted successfully"));
//    }
//}