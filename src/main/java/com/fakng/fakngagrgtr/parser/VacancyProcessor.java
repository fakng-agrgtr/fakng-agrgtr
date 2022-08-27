package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.vacancy.ProcessingStatus;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fakng.fakngagrgtr.service.VacancyService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Builder
public class VacancyProcessor {
    private final ExecutorService executor;
    private final Parser parser;
    private final VacancyService vacancyService;
    private final int poolSize;
    private final int batchSize;
    private final int minutesPerProcessing;
    private final boolean detailsProcessingRequired;
    private final AtomicInteger failedAttempts = new AtomicInteger(0);
    private final int maxFailedAttemptsPerVacancy;
    private final int maxFailedAttemptsPerEnriching;

    public void parse() {
        try {
            List<Vacancy> vacancies = parser.getAllVacancies();
            vacancyService.updateOrMarkInactive(vacancies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> parsePages() {
        failedAttempts.set(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        if (detailsProcessingRequired) {
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
        while (thereAreAttempts() && !notReady.isEmpty()) {
            for (Vacancy vacancy : notReady) {
                boolean enrichingResult = tryEnriching(vacancy);
                handleEnrichingResult(vacancy, enrichingResult);
                if (!thereAreAttempts()) break;
            }
            vacancyService.saveAll(notReady);
            if (thereAreAttempts()) {
                notReady = vacancyService.findNotReadyAndPutInProgress(minutesPerProcessing, batchSize);
            }
        }
    }

    private void handleEnrichingResult(Vacancy vacancy, boolean enrichingResult) {
        if (enrichingResult) {
            vacancy.setStatus(ProcessingStatus.READY);
        } else {
            failedAttempts.incrementAndGet();
        }
    }

    private boolean thereAreAttempts() {
        return failedAttempts.get() < maxFailedAttemptsPerEnriching;
    }

    private boolean tryEnriching(Vacancy vacancy) {
        int failedAttempts = 0;
        boolean success = false;
        while (failedAttempts < maxFailedAttemptsPerVacancy && !success) {
            try {
                parser.enrichWithDetails(vacancy);
                success = true;
            } catch (Exception e) {
                failedAttempts++;
                if (failedAttempts == maxFailedAttemptsPerVacancy) {
                    log.info("Reached max amount of attempts updating vacancy {} for company {} with error",
                            vacancy.getJobId(), vacancy.getCompany().getTitle());
                }
            }
        }
        return success;
    }
}
