package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.repository.RankingRepository;

import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.runningrecord.repository.RunningRecordRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.exception.BusinessException;
import com.alom.dorundorunbe.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @Mock
    private RankingRepository rankingRepository;


    @Mock
    private UserRepository userRepository;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private UserRankingService userRankingService;

    @InjectMocks
    private RankingService rankingService;

    private User user;
    private Ranking ranking;

    @BeforeEach
    void setUp() {


        user = User.builder()
                .id(1L)
                .nickname("runner123")
                .email("example@example.com")
                .cash(1000L)
                .isRankingParticipated(false)
                .tier(null) // 배치고사 필요 상태
                .rankingParticipationDate(null) // 처음 참가, null 값
                .build();
        ranking = Ranking.builder()
                .id(1L)
                .tier(Tier.AMATEUR)
                .build();
    }
    private RunningRecord mockRunningRecord(int elapsedTime) {
        return RunningRecord.builder()
                .user(user)
                .distance(5.0)
                .elapsedTime(elapsedTime)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .endTime(LocalDateTime.now().minusMinutes(5))
                .build();
    }

    @Test
    @DisplayName("랭킹 참가 시 배치고사가 필요한 경우, 랭킹 참여 날짜가 설정된다")
    void handleRankingParticipation_ShouldStartPlacementTest_IfRankingParticipationDateIsNull() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        rankingService.handleRankingParticipation(1L);

        // Then
        assertThat(user.getRankingParticipationDate()).isNotNull(); // 배치고사 시작되었는지 확인
        verify(userRepository, times(1)).findById(1L); // 사용자 조회 실행 검증
    }

    @Test
    @DisplayName("이미 랭킹에 참가한 사용자는 예외가 발생해야 한다")
    void handleRankingParticipation_ShouldThrowException_IfUserAlreadyParticipated() {
        // Given
        user.setRankingParticipated();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> rankingService.handleRankingParticipation(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.RANKING_ALREADY_PARTICIPATED.getMessage());
    }
}