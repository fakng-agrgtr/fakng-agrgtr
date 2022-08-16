package com.fakng.fakngagrgtr;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.fakng.fakngagrgtr.parser.Parser;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
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
    @Value("${agrgtr.pool-size:1}")
    private int poolSize;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    @Scheduled(cron = "${agrgtr.cron}:0 */12 * * * *")
    public void scheduleAggregation() {
        parsers.forEach(parser -> CompletableFuture
                .supplyAsync(parser::parse)
                .thenAccept(this::updateCompanyVacancies)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                }));
    }

    private void updateCompanyVacancies(List<Vacancy> actualVacancies) {
        if (actualVacancies.isEmpty()) {
            return;
        }
        List<Vacancy> dbVacancies = vacancyRepository.findByCompany(
                actualVacancies.get(0).getCompany());
        removeOutdatedVacancies(actualVacancies, dbVacancies);
        addNewVacancies(actualVacancies, dbVacancies);
    }

    private void removeOutdatedVacancies(List<Vacancy> actualVacancies, List<Vacancy> dbVacancies) {
        List<Vacancy> outdatedVacancies = dbVacancies.stream()
                .filter(vacancy -> !actualVacancies.contains(vacancy))
                .toList();
        vacancyRepository.deleteAll(outdatedVacancies);
    }

    private void addNewVacancies(List<Vacancy> actualVacancies, List<Vacancy> dbVacancies) {
        List<Vacancy> newVacancies = actualVacancies.stream()
                .filter(vacancy -> !dbVacancies.contains(vacancy))
                .toList();
        vacancyRepository.saveAll(newVacancies);
    }
}
