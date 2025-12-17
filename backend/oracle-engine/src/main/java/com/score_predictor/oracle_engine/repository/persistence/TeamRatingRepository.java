package com.score_predictor.oracle_engine.repository.persistence;

import java.util.Collection;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeamRatingRepository extends R2dbcRepository<TeamRatingEntity, Long> {
    Mono<TeamRatingEntity> findByTeamName(String teamName);

    Flux<TeamRatingEntity> findAllByTeamNameIn(Collection<String> teamNames);
}
