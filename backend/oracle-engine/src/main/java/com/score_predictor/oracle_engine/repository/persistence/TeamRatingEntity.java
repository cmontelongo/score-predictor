package com.score_predictor.oracle_engine.repository.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("team_ratings")
@Data
@Builder
public class TeamRatingEntity {
    @Id
    private Long id;
    private String teamName;
    private Integer eloRating;
    private Double attackStrength;
    private Double defenseStrength;
    public TeamRatingEntity(Long id, String teamName, Integer eloRating, Double attackStrength, Double defenseStrength) {
        this.id = id;
        this.teamName = teamName;
        this.eloRating = eloRating;
        this.attackStrength = attackStrength;
        this.defenseStrength = defenseStrength;

    }
}
