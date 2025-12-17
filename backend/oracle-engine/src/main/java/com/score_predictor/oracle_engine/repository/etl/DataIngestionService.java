package com.score_predictor.oracle_engine.repository.etl;

import java.io.InputStreamReader;
import java.time.LocalDate;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.score_predictor.oracle_engine.repository.persistence.HistoricalMatchEntity;
import com.score_predictor.oracle_engine.repository.persistence.HistoricalMatchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataIngestionService {

    private final HistoricalMatchRepository repository;

    public Mono<Void> importCsv(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
            .flatMap(dataBuffer -> {
                try {
                    return parseAndSave(dataBuffer);
                } catch (Exception e) {
                    return Mono.error(e);
                } finally {
                    DataBufferUtils.release(dataBuffer);
                }
            });
    }

    @SuppressWarnings("null")
    private Mono<Void> parseAndSave(DataBuffer dataBuffer) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(dataBuffer.asInputStream()))) {
            reader.readNext(); 
            
            var matches = Flux.<HistoricalMatchEntity>create(sink -> {
                try {
                    String[] record;
                    while ((record = reader.readNext()) != null) {
                        // CSV columns: date, home_team, away_team, home_score, away_score, tournament, city, country, neutral
                        
                        String tournament = record[5];

                        HistoricalMatchEntity entity = new HistoricalMatchEntity(
                            null,
                            LocalDate.parse(record[0]), // Date format YYYY-MM-DD
                            record[1], // Home Team
                            record[2], // Away Team
                            Integer.parseInt(record[3]), // Home Score
                            Integer.parseInt(record[4]), // Away Score
                            tournament,
                            calculateImportance(tournament) // K-Factor
                        );
                        sink.next(entity);
                    }
                    sink.complete();
                } catch (Exception e) {
                    sink.error(e);
                }
            });

            return repository.saveAll(matches).then();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private int calculateImportance(String tournament) {
        if (tournament.contains("World Cup")) return 60;
        if (tournament.contains("Copa America") || tournament.contains("Euro")) return 50;
        if (tournament.contains("Qualification")) return 40;
        return 20; // Default for friendlies and others
    }
}
