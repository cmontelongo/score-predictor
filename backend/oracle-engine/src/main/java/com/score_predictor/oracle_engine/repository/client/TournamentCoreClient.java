package com.score_predictor.oracle_engine.repository.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.score_predictor.oracle_engine.dto.MatchDTO;

import reactor.core.publisher.Flux;

@Component
public class TournamentCoreClient {

    private final WebClient webClient;

@SuppressWarnings("null")
public TournamentCoreClient(WebClient.Builder builder, @Value("${CORE_SERVICE_URL}") String coreUrl) {
        this.webClient = builder.baseUrl(coreUrl).build();
    }

    // Método existente
    public Flux<MatchDTO> getAllMatches() {
        return webClient.get()
                .uri("/matches")
                .retrieve()
                .bodyToFlux(MatchDTO.class);
    }

    public Flux<MatchDTO> getScheduledMatches() {
        return webClient.get()
                .uri("/matches/scheduled")
                .retrieve()
                .bodyToFlux(MatchDTO.class);
    }

}
