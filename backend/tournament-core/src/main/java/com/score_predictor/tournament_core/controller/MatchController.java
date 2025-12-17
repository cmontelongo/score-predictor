package com.score_predictor.tournament_core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.score_predictor.tournament_core.entity.MatchEntity;
import com.score_predictor.tournament_core.model.dto.ScheduleMatchRequest;
import com.score_predictor.tournament_core.model.enums.MatchStatus;
import com.score_predictor.tournament_core.repository.MatchRepository;
import com.score_predictor.tournament_core.service.TournamentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MatchController {
    private final MatchRepository matchRepository;
    private final TournamentService tournamentService;

    @GetMapping
    public Flux<MatchEntity> getAllMatches() {
        return matchRepository.findAll();
    }

    @SuppressWarnings("null")
    @PostMapping
    public Mono<MatchEntity> createMatch(@RequestBody MatchEntity match) {
        return matchRepository.save(match);
    }

    @PostMapping("/schedule")
    public Mono<MatchEntity> scheduleMatch(@RequestBody ScheduleMatchRequest request) {
        MatchEntity newMatch = new MatchEntity(
                null,
                request.homeTeam(),
                request.awayTeam(),
                null,
                null,
                request.matchDate(),
                request.stadium(),
                request.stage(),
                MatchStatus.SCHEDULED);
        return matchRepository.save(newMatch);
    }

    @GetMapping("/scheduled")
    public Flux<MatchEntity> getUpcomingMatches() {
        return matchRepository.findByStatus(MatchStatus.SCHEDULED);
    }

    record FinishMatchRequest(Integer homeScore, Integer awayScore) {
    }

    @PutMapping("/{id}/finish")
    public Mono<MatchEntity> finishMatch(
            @PathVariable Long id,
            @RequestBody FinishMatchRequest request) {
        return tournamentService.finalizeMatch(id, request.homeScore(), request.awayScore());
    }

    @SuppressWarnings("null")
    @PostMapping("/schedule/batch")
    public Flux<MatchEntity> scheduleBatchMatches(@RequestBody List<ScheduleMatchRequest> requests) {
        Flux<MatchEntity> entitiesFlux = Flux.fromIterable(requests)
                .map(req -> MatchEntity.builder()
                        .homeTeam(req.homeTeam())
                        .awayTeam(req.awayTeam())
                        .matchDate(req.matchDate())
                        .stadium(req.stadium())
                        .stage(req.stage())
                        .status(MatchStatus.SCHEDULED)
                        .build());

        return matchRepository.saveAll(entitiesFlux);
    }

}
