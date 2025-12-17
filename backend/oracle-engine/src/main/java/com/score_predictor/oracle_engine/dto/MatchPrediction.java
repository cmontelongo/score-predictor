package com.score_predictor.oracle_engine.dto;

public record MatchPrediction(
    String homeTeam, 
    String awayTeam, 
    double homeWinProb, 
    double drawProb,
    double awayWinProb
) {
    public MatchPrediction {
        double totalProb = homeWinProb + drawProb + awayWinProb;
        if (Math.abs(totalProb - 1.0) > 0.0001) {
            throw new IllegalArgumentException("The sum of probabilities must be 1.0");
        }
    }
}
