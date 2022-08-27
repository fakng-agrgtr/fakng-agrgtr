package com.fakng.fakngagrgtr.configuration;

import com.fakng.fakngagrgtr.parser.VacancyProcessor;
import com.fakng.fakngagrgtr.parser.amazon.AmazonParser;
import com.fakng.fakngagrgtr.service.VacancyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AmazonConfiguration {

    @Value("${amazon.pool-size}")
    private int poolSize;

    @Value("${amazon.batch-size}")
    private int batchSize;

    @Value("${amaon.minutes-per-processing}")
    private int minutesPerProcessing;

    @Bean
    public ExecutorService amazonExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean
    public VacancyProcessor amazonProcessor(AmazonParser parser, VacancyService vacancyService) {
        return new VacancyProcessor(amazonExecutor(), parser, vacancyService,
                poolSize, batchSize, minutesPerProcessing, false);
    }
}
