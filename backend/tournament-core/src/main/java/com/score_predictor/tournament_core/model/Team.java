package com.score_predictor.tournament_core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("teams")
public class Team {
    @Id
    private Long id;
    private String name;
    private String isoCode;
    private String groupName;
}
