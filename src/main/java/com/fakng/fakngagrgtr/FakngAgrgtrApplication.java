package com.fakng.fakngagrgtr;

import com.fakng.fakngagrgtr.parser.apple.AppleParser;
import com.fakng.fakngagrgtr.persistent.vacancy.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class FakngAgrgtrApplication implements CommandLineRunner {

    private final AppleParser appleParser;
    private final VacancyRepository vacancyRepository;
    public static void main(String[] args) {
        SpringApplication.run(FakngAgrgtrApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CompletableFuture
                .supplyAsync(appleParser::parse)
                .thenApply(vacancyRepository::saveAll)
                .thenCompose(vacancies -> CompletableFuture.supplyAsync(() -> appleParser.parseSecondary(vacancies)))
                .thenApply(vacancyRepository::saveAll);
    }
}
