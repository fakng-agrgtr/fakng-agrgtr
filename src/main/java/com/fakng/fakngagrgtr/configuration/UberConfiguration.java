package com.fakng.fakngagrgtr.configuration;

import com.fakng.fakngagrgtr.parser.VacancyProcessor;
import com.fakng.fakngagrgtr.parser.uber.UberParser;
import com.fakng.fakngagrgtr.service.VacancyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class UberConfiguration {
    @Value("${uber.pool-size}")
    private int poolSize;

    @Value("${uber.batch-size}")
    private int batchSize;

    @Value("${uber.minutes-per-processing}")
    private int minutesPerProcessing;

    @Bean
    public ExecutorService uberExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public VacancyProcessor uberProcessor(UberParser parser, VacancyService vacancyService) {
        return new VacancyProcessor(uberExecutor(), parser, vacancyService,
                poolSize, batchSize, minutesPerProcessing, true);
    }
}
