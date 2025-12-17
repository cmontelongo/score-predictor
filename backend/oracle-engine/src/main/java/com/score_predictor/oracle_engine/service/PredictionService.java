package com.score_predictor.oracle_engine.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.score_predictor.oracle_engine.business.EloCalculator;
import com.score_predictor.oracle_engine.dto.MatchPrediction;
import com.score_predictor.oracle_engine.dto.MatchResultDTO;
import com.score_predictor.oracle_engine.dto.TeamRating;
import com.score_predictor.oracle_engine.repository.client.TournamentCoreClient;
import com.score_predictor.oracle_engine.repository.persistence.HistoricalMatchEntity;
import com.score_predictor.oracle_engine.repository.persistence.HistoricalMatchRepository;
import com.score_predictor.oracle_engine.repository.persistence.TeamRatingEntity;
import com.score_predictor.oracle_engine.repository.persistence.TeamRatingRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PredictionService {

        private final TournamentCoreClient coreClient;
        private final MonteCarloService monteCarloService;
        private final TeamRatingRepository ratingRepository;
        private final HistoricalMatchRepository historyRepo;
        private final EloCalculator eloCalculator;

        private static final int WORLD_CUP_K_FACTOR = 60;

        public Flux<MatchPrediction> predictNextRound() {
                return coreClient.getAllMatches()
                                .flatMap(matchDto -> {
                                        Mono<TeamRating> homeStats = findStatsOrDefault(matchDto.homeTeam());
                                        Mono<TeamRating> awayStats = findStatsOrDefault(matchDto.awayTeam());

                                        return Mono.zip(homeStats, awayStats)
                                                        .map(tuple -> monteCarloService.simulateMatch(tuple.getT1(),
                                                                        tuple.getT2()));
                                });
        }

        private Mono<TeamRating> findStatsOrDefault(String teamName) {
                return ratingRepository.findByTeamName(teamName)
                                .map(entity -> new TeamRating(
                                                entity.getTeamName(),
                                                entity.getEloRating(),
                                                entity.getAttackStrength(),
                                                entity.getDefenseStrength()))
                                .switchIfEmpty(Mono.just(new TeamRating(teamName, 1500, 1.0, 1.0))); // Default
        }

        @SuppressWarnings("null")
        public Mono<Void> updateRatingsAfterMatch(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
                Mono<TeamRatingEntity> homeMono = ratingRepository.findByTeamName(homeTeam)
                                .defaultIfEmpty(new TeamRatingEntity(null, homeTeam, 1500, 1.0, 1.0));

                Mono<TeamRatingEntity> awayMono = ratingRepository.findByTeamName(awayTeam)
                                .defaultIfEmpty(new TeamRatingEntity(null, awayTeam, 1500, 1.0, 1.0));

                return Mono.zip(homeMono, awayMono).flatMap(tuple -> {
                        TeamRatingEntity home = tuple.getT1();
                        TeamRatingEntity away = tuple.getT2();

                        double expectedHome = calculateExpectedScore(home.getEloRating(), away.getEloRating());
                        double expectedAway = 1.0 - expectedHome;

                        double actualHome = getActualScore(homeGoals, awayGoals);
                        double actualAway = 1.0 - actualHome;

                        int newHomeElo = eloCalculator.calculateNewRating(home.getEloRating(), expectedHome, actualHome,
                                        WORLD_CUP_K_FACTOR);
                        int newAwayElo = eloCalculator.calculateNewRating(away.getEloRating(), expectedAway, actualAway,
                                        WORLD_CUP_K_FACTOR);

                        TeamRatingEntity updatedHome = new TeamRatingEntity(
                                        home.getId(), home.getTeamName(), newHomeElo, home.getAttackStrength(),
                                        home.getDefenseStrength());

                        TeamRatingEntity updatedAway = new TeamRatingEntity(
                                        away.getId(), away.getTeamName(), newAwayElo, away.getAttackStrength(),
                                        away.getDefenseStrength());

                        return ratingRepository.saveAll(List.of(updatedHome, updatedAway)).then();
                });
        }

        public Mono<Void> processNewResult(MatchResultDTO result) {
                HistoricalMatchEntity hMatch = new HistoricalMatchEntity(
                                null, LocalDate.now(), result.homeTeam(), result.awayTeam(),
                                result.homeScore(), result.awayScore(), "World Cup 2026", 60);

                return historyRepo.save(hMatch)
                                .flatMap(savedMatch -> {
                                        return updateRatingsAfterMatch(result.homeTeam(), result.awayTeam(),
                                                        result.homeScore(), result.awayScore());
                                });
        }

        public Flux<MatchPrediction> predictFullSchedule() {
                return coreClient.getScheduledMatches()
                                .flatMap(match -> {
                                        Mono<TeamRating> home = findStatsOrDefault(match.homeTeam());
                                        Mono<TeamRating> away = findStatsOrDefault(match.awayTeam());

                                        return Mono.zip(home, away)
                                                        .map(tuple -> {
                                                                var prediction = monteCarloService.simulateMatch(
                                                                                tuple.getT1(), tuple.getT2());
                                                                return prediction;
                                                        });
                                });
        }

        private double calculateExpectedScore(int ratingA, int ratingB) {
                return 1.0 / (1.0 + Math.pow(10.0, (ratingB - ratingA) / 400.0));
        }

        private double getActualScore(int goalsA, int goalsB) {
                return (Integer.compare(goalsA, goalsB) + 1.0) / 2.0;
        }

}
