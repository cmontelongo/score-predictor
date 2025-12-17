package com.score_predictor.tournament_core.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("matches")
public class Match {
    @Id
    private Long id;
    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private Integer homeScore;
    private Integer awayScore;
    private LocalDateTime matchDate;
    private String stage;
}
