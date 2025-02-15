package com.alom.dorundorunbe.global.util.point.service;

import com.alom.dorundorunbe.domain.ranking.domain.UserRanking;
import com.alom.dorundorunbe.domain.ranking.repository.UserRankingRepository;
import com.alom.dorundorunbe.domain.ranking.service.UserRankingService;
import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import com.alom.dorundorunbe.domain.user.domain.User;
import com.alom.dorundorunbe.domain.user.repository.UserRepository;
import com.alom.dorundorunbe.global.util.point.reward.RankingReward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {
    private final PointCalculationService pointCalculationService;
    private final UserRepository userRepository;
    private final UserRankingRepository userRankingRepository;
    private final UserRankingService userRankingService;



    public void processRunningPoints(RunningRecord record) {
        User user = record.getUser();

        //if(user.isDoodleParticipated()){
        //double doodlePoints = pointCalculationService.calculatePoints("doodleRunCalculator", record);
        //doodleService.updateUserRankingPointAndNotify(user.getId(), doodlePoints);

        //}

        if (user.isRankingParticipated()) {
            double rankingPoints = pointCalculationService.calculatePoints("rankingRunCalculator", record);
            userRankingService.updateUserRankingPointAndNotify(user.getId(), rankingPoints);
        }

    }

//    public void giveDoodleRewardToUser(Long userId, Long point){
//
//    }


    @Transactional//벌크성 처리
    public void giveRankingRewardToUsersByRanking(Long rankingId) {

        List<UserRanking> userRankings = userRankingRepository.findWithUserByRankingId(rankingId);



        for (UserRanking userRanking : userRankings) {
            User user = userRanking.getUser();
            RankingReward reward = RankingReward.getRewardByGrade(userRanking.getGrade());

            long rankCash = reward.calculateCash(user.getTier());
            double rankLp = reward.calculateLp(user.getTier());

            user.addCash(rankCash);
            user.addLp(rankLp);
            user.resetRankingParticipated();


        }




    }



}
