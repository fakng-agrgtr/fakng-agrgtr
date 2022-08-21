package com.fakng.fakngagrgtr.scheduler;

import com.fakng.fakngagrgtr.parser.Parser;
import com.fakng.fakngagrgtr.service.VacancyService;
import lombok.RequiredArgsConstructor;
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
public class FakngScheduler {

    private final List<Parser> parsers;
    private final VacancyService vacancyService;
    @Value("${agrgtr.pool-size:1}")
    private int poolSize;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    @Scheduled(cron = "${agrgtr.cron:0 */12 * * * *}")
    public void scheduleAggregation() {
        parsers.forEach(parser -> CompletableFuture
                .supplyAsync(parser::parse, executor)
                .thenAccept(vacancyService::updateOrMarkInactive)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }));
    }
}
