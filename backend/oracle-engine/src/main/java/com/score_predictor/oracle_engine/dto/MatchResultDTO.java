package com.score_predictor.oracle_engine.dto;

public record MatchResultDTO(
    String homeTeam,
    String awayTeam,
    Integer homeScore,
    Integer awayScore
) {
    public MatchResultDTO {
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Goals scored cannot be negative");
        }
    }
}
