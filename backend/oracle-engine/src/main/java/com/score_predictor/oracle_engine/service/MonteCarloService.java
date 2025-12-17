package com.score_predictor.oracle_engine.service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.score_predictor.oracle_engine.business.EloCalculator;
import com.score_predictor.oracle_engine.dto.MatchPrediction;
import com.score_predictor.oracle_engine.dto.TeamRating;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonteCarloService {

    private final EloCalculator eloCalculator;

    private static final int SIMULATION_CYCLES = 10_000;

    @SuppressWarnings("null")
    public MatchPrediction simulateMatch(TeamRating home, TeamRating away) {
        double eloWinProb = eloCalculator.calculateWinProbability(home, away);

        var stats = IntStream.range(0, SIMULATION_CYCLES).parallel()
                .mapToObj(i -> runSingleSimulation(eloWinProb))
                .reduce(new SimulationResult(0, 0, 0), SimulationResult::accumulate, SimulationResult::combine);

        return new MatchPrediction(
                home.teamName(),
                away.teamName(),
                (double) stats.homeWins / SIMULATION_CYCLES,
                (double) stats.draws / SIMULATION_CYCLES,
                (double) stats.awayWins / SIMULATION_CYCLES);
    }

    private int runSingleSimulation(double homeWinProbability) {
        double drawAdjustment = 0.28;
        double halfAdj = drawAdjustment / 2.0;

        double thresholdHome = homeWinProbability - halfAdj;
        double thresholdDraw = homeWinProbability + halfAdj;

        double randomValue = ThreadLocalRandom.current().nextDouble();

        return (randomValue < thresholdHome) ? 1
                : (randomValue < thresholdDraw) ? 0
                : -1;
    }


    private record SimulationResult(int homeWins, int draws, int awayWins) {
        public SimulationResult accumulate(int result) {
            return new SimulationResult(
                    homeWins + (result == 1 ? 1 : 0),
                    draws + (result == 0 ? 1 : 0),
                    awayWins + (result == -1 ? 1 : 0));
        }

        public SimulationResult combine(SimulationResult other) {
            return new SimulationResult(
                    homeWins + other.homeWins,
                    draws + other.draws,
                    awayWins + other.awayWins);
        }
    }
}
