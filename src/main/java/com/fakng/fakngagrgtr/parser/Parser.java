package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fakng.fakngagrgtr.persistent.vacancy.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class Parser {

    private static final int REQUESTS_LIMIT = 100;
    private static final int REQUESTING_DELAY = 6; // 6 hours

    protected final CompanyRepository companyRepository;
    protected final LocationProcessor locationProcessor;
    protected final VacancyRepository vacancyRepository;
    protected String url;
    protected Company company;

    public List<Vacancy> parse() {
        try {
            return getAllVacancies();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Vacancy> requestNewVacanciesDetails(List<Vacancy> vacancies) {
        return vacancies.stream()
                .map(this::getVacancyDetails)
                .toList();
    }

    protected void initBase() {
        company = companyRepository.findByTitle(getCompanyName())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("There is no %s company present in DB", getCompanyName())));
        locationProcessor.fillLocationCache(company);
    }

    protected abstract List<Vacancy> getAllVacancies() throws Exception;

    protected Vacancy parseVacancyPage(Vacancy vacancy) throws Exception {
        return vacancy;
    }

    protected abstract String getCompanyName();

    private Vacancy getVacancyDetails(Vacancy vacancy) {
        try {
            return parseVacancyPage(vacancy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
