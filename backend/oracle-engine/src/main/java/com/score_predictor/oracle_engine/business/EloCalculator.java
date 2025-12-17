package com.score_predictor.oracle_engine.business;

import org.springframework.stereotype.Component;

import com.score_predictor.oracle_engine.dto.TeamRating;

@Component
public class EloCalculator {

    private static final double DIVISOR = 400.0;
    private static final double BASE = 10.0;

    /**
     * P(A) = 1 / (1 + 10 ^ ((Rb - Ra) / 400))
     */
    public double calculateWinProbability(TeamRating teamA, TeamRating teamB) {
        double ratingDiff = teamB.eloRating() - teamA.eloRating();
        return 1.0 / (1.0 + Math.pow(BASE, ratingDiff / DIVISOR));
    }

    public int calculateNewRating(int currentRating, double expectedScore, double actualScore, int kFactor) {
        return (int) (currentRating + kFactor * (actualScore - expectedScore));
    }
}
