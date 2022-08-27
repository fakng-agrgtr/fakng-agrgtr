package com.fakng.fakngagrgtr.configuration;

import com.fakng.fakngagrgtr.parser.VacancyProcessor;
import com.fakng.fakngagrgtr.parser.google.GoogleParser;
import com.fakng.fakngagrgtr.service.VacancyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class GoogleConfiguration {
    @Value("${google.pool-size}")
    private int poolSize;

    @Value("${google.batch-size}")
    private int batchSize;

    @Value("${google.minutes-per-processing}")
    private int minutesPerProcessing;

    @Bean
    public ExecutorService googleExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public VacancyProcessor googleProcessor(GoogleParser parser, VacancyService vacancyService) {
        return new VacancyProcessor(googleExecutor(), parser, vacancyService,
                poolSize, batchSize, minutesPerProcessing, false);
    }
}
