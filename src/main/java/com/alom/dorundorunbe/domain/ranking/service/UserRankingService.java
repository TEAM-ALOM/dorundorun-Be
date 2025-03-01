package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.domain.ranking.domain.Ranking;
import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.ranking.dto.RankingResponseDto;
import com.alom.dorundorunbe.domain.ranking.dto.RankingSocketDto;
import com.alom.dorundorunbe.domain.ranking.dto.RankingSocketUserDto;
import com.alom.dorundorunbe.domain.ranking.dto.UserRankingDto;
import com.alom.dorundorunbe.domain.ranking.repository.RankingCacheRepository;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.ranking.util.RankingCacheKeyUtil;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.global.enums.Tier;
import com.alom.dorundorunbe.global.exception.BusinessException;
import com.alom.dorundorunbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserRankingService {
    private final UserRankingRepository userRankingRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RankingCacheRepository rankingCacheRepository;


    @Transactional
    public void createUserRanking(User user, Ranking ranking) {

        if (user.isRankingParticipated()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_IN_RANKING);
        }



        UserRanking userRanking = UserRanking.create(user);
        userRanking.confirmRanking(ranking);
        userRankingRepository.save(userRanking);
        user.setRankingParticipated();

        rankingCacheRepository.saveUserRanking(ranking.getTier(), user.getId(), -1.0);


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
        Ranking ranking = userRanking.getRanking();
        Long rankingId = ranking.getId();
        Tier rankingTier = ranking.getTier();


        // Redis Sorted Set에 사용자의 최신 평균 점수를 업데이트
        rankingCacheRepository.saveUserRanking(rankingTier, userId, newAvgPoint);

        // 전체 방의 최신 순위를 계산하고 웹 소켓으로 전송
        updateTierRankingAndNotify(rankingId, rankingTier);

    }
    public void updateTierRankingAndNotify(Long rankingId, Tier tier) {



        Set<ZSetOperations.TypedTuple<Object>> rankingSet = rankingCacheRepository.getTierRanking(tier);



        rankingSet = refreshCacheFromDbIfEmpty(rankingId, tier, rankingSet);//cache 유실 시 db값 불러옴
        if (rankingSet == null) return; // DB에도 데이터가 없으면 종료


        List<RankingSocketUserDto> userList = new ArrayList<>();
        long counter = 0;
        Long rank = null;
        Double previousScore = null;



        for (ZSetOperations.TypedTuple<Object> tuple : rankingSet) {
            counter++;
            Long userId = (Long) tuple.getValue();
            Double avgScore = tuple.getScore();


            avgScore = (avgScore != null) ? avgScore : -1.0;


            if (avgScore == -1.0) {
                rank = null;
            } else {

                if (previousScore == null || !avgScore.equals(previousScore)) {
                    rank = counter;
                }
            }

            Long finalRank = (avgScore == -1.0) ? null : rank;

            userList.add(new RankingSocketUserDto(userId, (avgScore == -1.0 ? null : avgScore), finalRank));
            previousScore = avgScore;
        }
        String tierRankingKey = RankingCacheKeyUtil.getTierRankingKey(tier);
        messagingTemplate.convertAndSend("/sub/ranking/" + tierRankingKey,
                new RankingSocketDto(rankingId, tierRankingKey, userList));
    }
    private Set<ZSetOperations.TypedTuple<Object>> refreshCacheFromDbIfEmpty(Long rankingId, Tier tier, Set<ZSetOperations.TypedTuple<Object>> rankingSet) {
        if (rankingSet == null || rankingSet.isEmpty()) {
            // DB에서 해당 랭킹 방의 사용자 랭킹 정보를 조회 (티어별로 조회)
            List<UserRanking> userRankings = userRankingRepository.findByRankingId(rankingId);

            if (userRankings.isEmpty()) {
                return null;
            }


            for (UserRanking userRanking : userRankings) {
                Double avgPoint = (userRanking.getAveragePoint() != null) ? userRanking.getAveragePoint() : -1.0;
                rankingCacheRepository.saveUserRanking(tier, userRanking.getUser().getId(), avgPoint);
            }

            rankingSet = rankingCacheRepository.getTierRanking(tier);
            if (rankingSet == null || rankingSet.isEmpty()) {
                return null;
            }
        }
        return rankingSet;
    }


}