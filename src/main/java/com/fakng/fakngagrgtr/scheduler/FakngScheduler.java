package com.fakng.fakngagrgtr.scheduler;

import com.fakng.fakngagrgtr.parser.Parser;
import com.fakng.fakngagrgtr.vacancy.VacancyRepository;
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

    @Value("${agrgtr.pool-size}")
    private int poolSize;
    private ExecutorService executor;
    private final List<Parser> parsers;
    private final VacancyRepository vacancyRepository;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    @Scheduled(cron = "${agrgtr.cron}")
    public void scheduleAggregation() {
        parsers.forEach(parser -> CompletableFuture.supplyAsync(() -> vacancyRepository.saveAll(parser.parse()), executor));
    }
}
