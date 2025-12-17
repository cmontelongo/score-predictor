package com.score_predictor.tournament_core.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.score_predictor.tournament_core.entity.MatchEntity;
import com.score_predictor.tournament_core.model.enums.MatchStatus;

import reactor.core.publisher.Flux;

public interface MatchRepository extends R2dbcRepository<MatchEntity, Long> {

    Flux<MatchEntity> findByStatus(MatchStatus status);
    
}
