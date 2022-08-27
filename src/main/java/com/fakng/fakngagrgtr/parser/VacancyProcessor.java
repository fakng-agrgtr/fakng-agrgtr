package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.vacancy.ProcessingStatus;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fakng.fakngagrgtr.service.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Slf4j
public class VacancyProcessor {
    private final ExecutorService executor;
    private final Parser parser;
    private final VacancyService vacancyService;
    private final int poolSize;
    private final int batchSize;
    private final int minutesPerProcessing;
    private final boolean processDetails;

    public void parse() {
        try {
            List<Vacancy> vacancies = parser.getAllVacancies();
            vacancyService.updateOrMarkInactive(vacancies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> parsePages() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        if (processDetails) {
            for (int i = 0; i < poolSize; i++) {
                futures.add(CompletableFuture.runAsync(this::processVacancyDetailsBatches, executor)
                        .exceptionally(e -> {
                            log.error("Error while parsing vacancies' details", e);
                            return null;
                        })
                );
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void processVacancyDetailsBatches() {
        List<Vacancy> notReady = vacancyService.findNotReadyAndPutInProgress(minutesPerProcessing, batchSize);
        while (!notReady.isEmpty()) {
            for (Vacancy vacancy : notReady) {
                parser.enrichWithDetails(vacancy);
                vacancy.setStatus(ProcessingStatus.READY);
            }
            vacancyService.saveAll(notReady);
            notReady = vacancyService.findNotReadyAndPutInProgress(minutesPerProcessing, batchSize);
        }
    }
}
