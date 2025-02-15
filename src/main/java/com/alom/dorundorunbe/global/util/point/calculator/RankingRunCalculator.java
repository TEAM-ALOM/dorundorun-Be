package com.alom.dorundorunbe.global.util.point.calculator;

import com.alom.dorundorunbe.domain.runningrecord.domain.RunningRecord;
import org.springframework.stereotype.Component;

@Component("rankingRunCalculator")
public class RankingRunCalculator implements PointCalculator{
    @Override
    public double calculatePoints(RunningRecord record) {
        return Math.pow(record.getDistance(), 1.3) / record.getElapsedTime();
    }
}

