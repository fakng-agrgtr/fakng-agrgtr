package com.fakng.fakngagrgtr.scheduler;

import com.fakng.fakngagrgtr.parser.VacancyProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FakngScheduler {

    private final List<VacancyProcessor> processors;
    @Value("${agrgtr.pool-size:1}")
    private int poolSize;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    @Scheduled(cron = "${agrgtr.cron:0 */12 * * * *}")
    public void scheduleAggregation() {
        processors.forEach(processor -> CompletableFuture
                .runAsync(processor::parse, executor)
                .thenComposeAsync((v) -> processor.parsePages())
                .exceptionally(e -> {
                    log.error("Error while parsing vacancies", e);
                    return null;
                }));
    }
}
