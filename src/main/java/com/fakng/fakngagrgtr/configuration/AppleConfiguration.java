package com.fakng.fakngagrgtr.configuration;

import com.fakng.fakngagrgtr.parser.VacancyProcessor;
import com.fakng.fakngagrgtr.parser.apple.AppleParser;
import com.fakng.fakngagrgtr.service.VacancyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppleConfiguration {

    @Value("${apple.pool-size}")
    private int poolSize;

    @Value("${apple.batch-size}")
    private int batchSize;

    @Value("${apple.minutes-per-processing}")
    private int minutesPerProcessing;

    @Bean
    public ExecutorService appleExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public VacancyProcessor appleProcessor(AppleParser parser, VacancyService vacancyService) {
        return new VacancyProcessor(appleExecutor(), parser, vacancyService,
                poolSize, batchSize, minutesPerProcessing, true);
    }
}
