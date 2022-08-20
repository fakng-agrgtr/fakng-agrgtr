package com.fakng.fakngagrgtr.scheduler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import com.fakng.fakngagrgtr.parser.Parser;
import com.fakng.fakngagrgtr.persistent.vacancy.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FakngScheduler {

    private final List<Parser> parsers;
    private final VacancyRepository vacancyRepository;
    @Value("${agrgtr.pool-size.primary:1}")
    private int primaryPoolSize;

    @Value("${agrgtr.pool-size.secondary:1}")
    private int secondaryPoolSize;
    private ExecutorService primaryExecutor;
    private ExecutorService secondaryExecutor;

    @PostConstruct
    public void init() {
        primaryExecutor = Executors.newFixedThreadPool(primaryPoolSize);
        secondaryExecutor = Executors.newFixedThreadPool(secondaryPoolSize);
    }

    @Scheduled(cron = "${agrgtr.cron:0 */12 * * * *}")
    public void scheduleAggregation() {
        parsers.forEach(parser -> CompletableFuture
                .supplyAsync(parser::parse, primaryExecutor)
                .thenApply(vacancyRepository::saveAll)
                .thenCompose(vacancies -> CompletableFuture.supplyAsync(() -> parser.parseSecondary(vacancies), secondaryExecutor))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }));
    }
}
