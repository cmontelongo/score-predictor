package com.score_predictor.oracle_engine.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.score_predictor.oracle_engine.repository.etl.DataIngestionService;
import com.score_predictor.oracle_engine.service.ModelTrainingService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DataIngestionService ingestionService;
    private final ModelTrainingService trainingService;

    @PostMapping(value = "/import-history", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadHistory(@RequestPart("file") FilePart file) {
        return ingestionService.importCsv(file)
            .then(Mono.just("Upload successfully processed."));
    }

    @PostMapping("/retrain")
    public Mono<String> retrainModel() {
        return trainingService.retrainModel()
            .then(Mono.just("Model retrained and ratings updated."));
    }
}
