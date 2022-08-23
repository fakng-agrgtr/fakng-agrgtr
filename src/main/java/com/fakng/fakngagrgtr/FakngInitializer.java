package com.fakng.fakngagrgtr;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import com.fakng.fakngagrgtr.parser.Parser;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fakng.fakngagrgtr.persistent.vacancy.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FakngInitializer {

    private final List<Parser> parsers;
    private final VacancyRepository vacancyRepository;

    private final Map<Parser, LocalDateTime> delays = new ConcurrentHashMap<>();
    private final Map<Parser, List<Vacancy>> newVacancies = new ConcurrentHashMap<>();

    @Value("${initializer.pool-size:1}")
    private int poolSize;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    public CompletableFuture<Void> initialize() {
        CompletableFuture<?>[] futures = parsers.stream()
                .map(this::requestVacanciesList)
                .toList()
                .toArray(new CompletableFuture[parsers.size()]);
        CompletableFuture.anyOf(futures)
                .join();

    }

    private void requestVacanciesDetails() {
        while (!isAllFinished()) {

        }
    }

    private boolean isAllFinished() {
        for (Parser parser : newVacancies.keySet()) {
            if (newVacancies.get(parser) == null || newVacancies.get(parser).size() > 0) {
                return false;
            }
        }
        return true;
    }

    private CompletableFuture<?> requestVacanciesList(Parser parser) {
        return CompletableFuture
                .supplyAsync(parser::parse, executor)
                .thenAccept(vacancies -> {
                    newVacancies.put(parser, vacancies);
                    delays.put(parser, LocalDateTime.now());
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}
