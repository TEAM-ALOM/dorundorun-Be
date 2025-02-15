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

    @Test
    @DisplayName("배치 상태에서 5km 기록이 3회 미만이면 예외 발생")
    void handleRankingParticipation_ShouldThrowException_IfRecordCountIsLessThanThree() {
        LocalDateTime beforeStart = LocalDateTime.now(); // 실행 직전 시간 저장
        user.startRankingParticipation(); // 랭킹 참여 시작 (현재 시간으로 설정됨)
        LocalDateTime afterStart = LocalDateTime.now();  // 실행 직후 시간 저장
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(runningRecordRepository.countByUserAndDistanceAndCreatedAtAfter(
                user, 5.0, user.getRankingParticipationDate())).thenReturn(2L); // 기록 부족

        // When & Then
        assertThatThrownBy(() -> rankingService.handleRankingParticipation(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.RANKING_MINIMUM_RECORDS_NOT_MET.getMessage());

        verify(runningRecordRepository, times(1))
                .countByUserAndDistanceAndCreatedAtAfter(user, 5.0, user.getRankingParticipationDate());


        assertThat(user.getRankingParticipationDate()).isNotNull();


        assertThat(user.getRankingParticipationDate())
                .isAfterOrEqualTo(beforeStart)
                .isBeforeOrEqualTo(afterStart.plusSeconds(1)); // 1초 이내 차이가 나도록 설정
    }
}