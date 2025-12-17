package com.score_predictor.oracle_engine.repository.persistence;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface HistoricalMatchRepository extends R2dbcRepository<HistoricalMatchEntity, Long> {

    Flux<HistoricalMatchEntity> findAllByOrderByMatchDateAsc();

    Flux<HistoricalMatchEntity> findByHomeTeamOrAwayTeamOrderByMatchDateDesc(String homeTeam, String awayTeam);
}
