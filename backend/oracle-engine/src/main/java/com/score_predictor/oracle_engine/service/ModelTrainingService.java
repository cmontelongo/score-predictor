package com.score_predictor.oracle_engine.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.score_predictor.oracle_engine.business.EloCalculator;
import com.score_predictor.oracle_engine.repository.persistence.HistoricalMatchRepository;
import com.score_predictor.oracle_engine.repository.persistence.TeamRatingEntity;
import com.score_predictor.oracle_engine.repository.persistence.TeamRatingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelTrainingService {

    private final HistoricalMatchRepository historyRepo;
    private final TeamRatingRepository ratingRepo;
    private final EloCalculator eloCalculator;

    public Mono<Void> retrainModel() {
        Map<String, Integer> currentElos = new HashMap<>();

        return historyRepo.findAllByOrderByMatchDateAsc()
                .doOnNext(match -> {
                    int homeElo = currentElos.getOrDefault(match.homeTeam(), 1500);
                    int awayElo = currentElos.getOrDefault(match.awayTeam(), 1500);

                    double expectedHome = calculateExpectedScore(homeElo, awayElo);

                    double actualHome = getActualScore(match.homeScore(), match.awayScore());

                    int kFactor = match.importanceFactor() != null ? match.importanceFactor() : 40;

                    int newHomeElo = eloCalculator.calculateNewRating(homeElo, expectedHome, actualHome, kFactor);
                    int newAwayElo = eloCalculator.calculateNewRating(awayElo, 1.0 - expectedHome, 1.0 - actualHome,
                            kFactor);

                    currentElos.put(match.homeTeam(), newHomeElo);
                    currentElos.put(match.awayTeam(), newAwayElo);
                })
                .then(Mono.defer(() -> saveAllRatings(currentElos)));
    }

    private Mono<Void> saveAllRatings(Map<String, Integer> calculatedElos) {
        Set<String> teamNames = calculatedElos.keySet();

        return ratingRepo.findAllByTeamNameIn(teamNames)
                .collectMap(TeamRatingEntity::getTeamName)
                .flatMapMany(existingRatingsMap -> {
                    return Flux.fromIterable(calculatedElos.entrySet())
                            .map(entry -> {
                                String teamName = entry.getKey();
                                Integer newElo = entry.getValue();
                                TeamRatingEntity entity = existingRatingsMap.get(teamName);

                                if (entity != null) {
                                    entity.setEloRating(newElo);
                                    return entity;
                                } else {
                                    log.warn("New team detected: {}", teamName);
                                    return TeamRatingEntity.builder()
                                            .teamName(teamName)
                                            .eloRating(newElo)
                                            .attackStrength(1.0)
                                            .defenseStrength(1.0)
                                            .build();
                                }
                            });
                })
                .collectList()
                .flatMapMany(ratingRepo::saveAll)
                .then()
                .doOnSuccess(v -> log.info("Ratings saved successfully."));
    }

    private double calculateExpectedScore(int ratingA, int ratingB) {
        return 1.0 / (1.0 + Math.pow(10.0, (ratingB - ratingA) / 400.0));
    }

    private double getActualScore(int goalsA, int goalsB) {
        return (Integer.compare(goalsA, goalsB) + 1.0) / 2.0;
    }
}
