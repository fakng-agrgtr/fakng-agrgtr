package com.fakng.fakngagrgtr;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fakng.fakngagrgtr.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class FakngAgrgtrApplication implements CommandLineRunner {

    private final VacancyService vacancyService;
    private final CompanyRepository companyRepository;

    public static void main(String[] args) {
        SpringApplication.run(FakngAgrgtrApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Company company = companyRepository.findByTitle("Google").get();
        List<Vacancy> vacancies = new ArrayList<>();
        Vacancy first = new Vacancy();
        first.setTitle("title_1");
        first.setDescription("desc_1");
        first.setUrl("url_1");
        first.setPublishedDate(LocalDateTime.now().minusDays(10));
        first.setCompany(company);
        first.setJobId("job_1");

        Vacancy second = new Vacancy();
        second.setTitle("title_2");
        second.setDescription("desc_2");
        second.setUrl("url_2");
        second.setPublishedDate(LocalDateTime.now().minusDays(5));
        second.setCompany(company);
        second.setJobId("job_2");

        vacancies.add(first);
        vacancies.add(second);

        vacancyService.updateOrMarkInactive(vacancies);
    }
}
