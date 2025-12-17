package com.score_predictor.tournament_core.model.dto;

import java.time.LocalDateTime;

import com.score_predictor.tournament_core.model.enums.MatchStage;

public record ScheduleMatchRequest(
    String homeTeam, 
    String awayTeam, 
    LocalDateTime matchDate, 
    String stadium, 
    MatchStage stage
) {}
