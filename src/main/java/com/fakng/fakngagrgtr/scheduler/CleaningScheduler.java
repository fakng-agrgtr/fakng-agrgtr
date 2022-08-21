package com.fakng.fakngagrgtr.scheduler;

import com.fakng.fakngagrgtr.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleaningScheduler {

    @Value("${cleaner.months-life-time}")
    private int monthsLifeTime;

    private final VacancyService vacancyService;

    @Scheduled(cron = "${cleaner.cron}")
    public void cleanOutdatedVacancies() {
        vacancyService.deleteVacancies(LocalDateTime.now().minusMonths(monthsLifeTime));
    }
}
