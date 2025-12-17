package com.score_predictor.oracle_engine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.score_predictor.oracle_engine.dto.MatchPrediction;
import com.score_predictor.oracle_engine.dto.MatchResultDTO;
import com.score_predictor.oracle_engine.service.PredictionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/predict")
    public Flux<MatchPrediction> predictNextPhase() {
        return predictionService.predictNextRound();
    }

    @PostMapping("/internal/update-result")
    public Mono<Void> receiveMatchResult(@RequestBody MatchResultDTO result) {
        return predictionService.processNewResult(result);
    }

}
