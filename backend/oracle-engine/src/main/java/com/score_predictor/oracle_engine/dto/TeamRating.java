package com.score_predictor.oracle_engine.dto;

public record TeamRating(
    String teamName, 
    int eloRating, 
    double attackStrength, 
    double defenseStrength
) {
    public TeamRating {
        if (eloRating < 0) throw new IllegalArgumentException("Elo rating cannot be negative");
    }
}
