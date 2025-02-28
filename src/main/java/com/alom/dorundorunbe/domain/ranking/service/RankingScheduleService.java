package com.alom.dorundorunbe.domain.ranking.service;

import com.alom.dorundorunbe.global.enums.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class RankingScheduleService {//트랜잭션 처리 스케줄러내에서 프록시로 인해 동시 처리 안된다 하여 다른 외부 서비스에 구현(reward, sync service)
    private final RankingRewardService rankingRewardService;
    private final RankingSyncService rankingSyncService;
    @Scheduled(cron = "0 0 0 * * MON",zone = "Asia/Seoul") // 매주 월요일 00:00 실행
    public void distributeWeeklyRewardsAndClearRankings() {

        Arrays.stream(Tier.values()).forEach(rankingSyncService::syncRankingForTier);
        rankingRewardService.processWeeklyRewards();


    }
    //동기화 작업(write down방식 선택) cache에 일단 업데이트 쳐서 실시간 순위 및 평균 포인트 제공하고 30분단위로 db에 업데이트 치는 방식
    @Scheduled(fixedDelay = 1800000L) // 30분마다 실행
    public void syncRankingFromCacheToDb() {
        Arrays.stream(Tier.values()).forEach(rankingSyncService::syncRankingForTier);
    }
}
