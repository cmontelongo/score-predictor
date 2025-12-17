package com.score_predictor.oracle_engine.repository.persistence;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("historical_matches")
public record HistoricalMatchEntity(
    @Id Long id,
    LocalDate matchDate,
    String homeTeam,
    String awayTeam,
    Integer homeScore,
    Integer awayScore,
    String tournamentName,
    Integer importanceFactor
) {
    public HistoricalMatchEntity {
        if (importanceFactor == null) {
            importanceFactor = 1; // Default importance factor
        }
    }
}
