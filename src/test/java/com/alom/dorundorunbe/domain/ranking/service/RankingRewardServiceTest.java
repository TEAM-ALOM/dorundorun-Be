package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.ranking.repository.RankingRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.util.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingRewardServiceTest {

    @InjectMocks
    private RankingRewardService rankingRewardService;

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private UserRankingRepository userRankingRepository;

    @Mock
    private PointService pointService;

    private Ranking ranking1, ranking2;
    private User user1, user2, user3, user4;
    private UserRanking userRanking1, userRanking2, userRanking3, userRanking4;
    private final Long rankingId1 = 1L;
    private final Long rankingId2 = 2L;

    @BeforeEach
    void setUp() {

        ranking1 = Ranking.builder().id(rankingId1).tier(Tier.BEGINNER).build();
        ranking2 = Ranking.builder().id(rankingId2).tier(Tier.AMATEUR).build();


        user1 = createUser(1L, "User1", Tier.BEGINNER);
        user2 = createUser(2L, "User2", Tier.BEGINNER);
        user3 = createUser(3L, "User3", Tier.AMATEUR);
        user4 = createUser(4L, "User4", Tier.AMATEUR);


        userRanking1 = createUserRanking(1L, user1, ranking1, 1L);
        userRanking2 = createUserRanking(2L, user2, ranking1, 2L);
        userRanking3 = createUserRanking(3L, user3, ranking2, 1L);
        userRanking4 = createUserRanking(4L, user4, ranking2, 2L);

        lenient().when(rankingRepository.findAll()).thenReturn(List.of(ranking1, ranking2));

        lenient().when(rankingRepository.existsById(rankingId1)).thenReturn(true);
        lenient().when(rankingRepository.existsById(rankingId2)).thenReturn(true);
        lenient().when(userRankingRepository.findWithUserByRankingId(rankingId1)).thenReturn(List.of(userRanking1, userRanking2));
        lenient().when(userRankingRepository.findWithUserByRankingId(rankingId2)).thenReturn(List.of(userRanking3, userRanking4));
    }

    private User createUser(Long id, String nickname, Tier tier) {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .tier(tier)
                .cash(0L)
                .lp(0.0)
                .isRankingParticipated(true)
                .build();
    }

    private UserRanking createUserRanking(Long id, User user, Ranking ranking, Long grade) {
        UserRanking build = UserRanking.builder()
                .id(id)
                .user(user)
                .grade(grade)
                .build();
        build.confirmRanking(ranking);
        return build;

    }
}
