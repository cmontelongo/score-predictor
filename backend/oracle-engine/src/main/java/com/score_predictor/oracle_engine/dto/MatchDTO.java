package com.score_predictor.oracle_engine.dto;

import java.time.LocalDateTime;

public record MatchDTO(
    Long id,
    String homeTeam,
    String awayTeam,
    Integer homeScore,
    Integer awayScore,
    LocalDateTime matchDate,
    String stadium,
    String stage,
    String status 
) {}
