package com.score_predictor.tournament_core.service;

import org.springframework.stereotype.Service;

import com.score_predictor.tournament_core.entity.MatchEntity;
import com.score_predictor.tournament_core.infraestructure.client.OracleEngineClient;
import com.score_predictor.tournament_core.model.enums.MatchStatus;
import com.score_predictor.tournament_core.repository.MatchRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final MatchRepository matchRepository;
    private final OracleEngineClient oracleClient;

    @SuppressWarnings("null")
    public Mono<MatchEntity> finalizeMatch(Long matchId, Integer homeScore, Integer awayScore) {
        return matchRepository.findById(matchId)
            .flatMap(match -> {
                MatchEntity updatedMatch = match.toBuilder()
                    .homeScore(homeScore)
                    .awayScore(awayScore)
                    .status(MatchStatus.FINISHED)
                    .build();

                return matchRepository.save(updatedMatch);
            })
            .flatMap(savedMatch -> 
                oracleClient.notifyMatchFinished(
                    savedMatch.homeTeam(), 
                    savedMatch.awayTeam(), 
                    savedMatch.homeScore(), 
                    savedMatch.awayScore()
                ).thenReturn(savedMatch)
            );
    }
}
