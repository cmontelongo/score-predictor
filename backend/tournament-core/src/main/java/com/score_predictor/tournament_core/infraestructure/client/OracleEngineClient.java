package com.score_predictor.tournament_core.infraestructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

record MatchResultPayload(String homeTeam, String awayTeam, Integer homeScore, Integer awayScore) {
}

@Component
public class OracleEngineClient {

    private final WebClient webClient;

    @SuppressWarnings("null")
    public OracleEngineClient(WebClient.Builder builder, @Value("${ORACLE_SERVICE_URL}") String oracleUrl) {
        this.webClient = builder.baseUrl(oracleUrl).build();
    }

    public Mono<Void> notifyMatchFinished(String home, String away, int hScore, int aScore) {
        var payload = new MatchResultPayload(home, away, hScore, aScore);

        return webClient.post()
                .uri("/internal/update-result")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    System.err.println("Error processing the Oracle: " + e.getMessage());
                    return Mono.empty();
                });
    }
}
