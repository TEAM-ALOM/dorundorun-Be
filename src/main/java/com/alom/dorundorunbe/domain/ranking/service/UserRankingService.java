package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.ranking.dto.RankingResponseDto;
import com.alom.dorundorunbe.domain.ranking.dto.UserRankingDto;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.exception.BusinessException;
import com.alom.dorundorunbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserRankingService {
    private final UserRankingRepository userRankingRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public void createUserRanking(User user, Ranking ranking) {

        if (user.isRankingParticipated()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_RANKING);
        }



        UserRanking userRanking = UserRanking.create(user);
        userRanking.confirmRanking(ranking);
        userRankingRepository.save(userRanking);
        user.setRankingParticipated();


    }

    @Transactional(readOnly = true)
    public UserRankingDto findUserRanking(Long userId) {
        UserRanking userRanking = userRankingRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_RANKING_NOT_FOUND));

        return UserRankingDto.of(userRanking);


    }

    @Transactional//랭킹 참가자 기록 업데이트->소켓
    public void updateUserRankingPointAndNotify(Long userId, double point) {
        UserRanking userRanking = userRankingRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RANKING_NOT_FOUND));

        userRanking.addPoint(point);
        if(userRanking.getPoints().size() < 3){
            return;
        }

        Double averagePoint = userRanking.getAveragePoint();
        Double newAvgPoint = userRanking.updateAveragePoint();

        if (Objects.equals(averagePoint, newAvgPoint)) {
            return;
        }
        Long rankingId = userRanking.getRanking().getId();


        updateGrades(rankingId);
        //추후 redis 캐쉬 적용 필요해보임
        RankingResponseDto rankingDto = new RankingResponseDto(userRanking.getRanking());

        messagingTemplate.convertAndSend("/sub/ranking/" + rankingId, rankingDto);
    }

    public void updateGrades(Long rankingId) {

        List<UserRanking> participants = userRankingRepository.findByRankingId(rankingId);

        List<UserRanking> validParticipants = participants.stream()
                .filter(userRanking -> userRanking.getAveragePoint() != null)
                .sorted(Comparator.comparingDouble(UserRanking::getAveragePoint).reversed())
                .toList();

        Double previousPoint = null;
        long rank = 0;

        for (int i = 0; i < validParticipants.size(); i++) {
            UserRanking userRanking = validParticipants.get(i);

            if (i == 0 || !Objects.equals(userRanking.getAveragePoint(), previousPoint)) {
                rank = i + 1;
            }

            if (userRanking.getGrade() == null || !userRanking.getGrade().equals(rank)) {
                userRanking.updateGrade(rank);
            }

            previousPoint = userRanking.getAveragePoint();
        }
    }
}