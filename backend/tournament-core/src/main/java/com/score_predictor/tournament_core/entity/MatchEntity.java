package com.score_predictor.tournament_core.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.score_predictor.tournament_core.model.enums.MatchStage;
import com.score_predictor.tournament_core.model.enums.MatchStatus;

import lombok.Builder;

@Table("matches")
@Builder(toBuilder = true)
public record MatchEntity(
    @Id Long id,
    String homeTeam,
    String awayTeam,
    Integer homeScore,
    Integer awayScore,
    LocalDateTime matchDate,
    String stadium,
    MatchStage stage,
    MatchStatus status
) {}