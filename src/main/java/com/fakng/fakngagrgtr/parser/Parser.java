package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class Parser {
    protected final CompanyRepository companyRepository;
    protected final LocationProcessor locationProcessor;
    protected String url;
    protected Company company;

    public List<Vacancy> parse() {
        try {
            return getAllVacancies();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<Vacancy> parseSecondary(Iterable<Vacancy> vacancies) {
        try {
            return enrichVacanciesWithDetails(vacancies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void initBase() {
        company = companyRepository.findByTitle(getCompanyName())
                .orElseThrow(() -> new IllegalStateException(String.format("There is no %s company present in DB", getCompanyName())));
        locationProcessor.fillLocationCache(company);
    }

    protected abstract List<Vacancy> getAllVacancies() throws Exception;

    protected abstract String getCompanyName();

    protected abstract Iterable<Vacancy> enrichVacanciesWithDetails(Iterable<Vacancy> vacancies) throws Exception;

    protected String buildDescription(String... parts) {
        StringBuilder descriptionBuilder = new StringBuilder();
        for (String part : parts) {
            if (part != null) {
                descriptionBuilder.append(part).append("\n");
            }
        }
        descriptionBuilder.deleteCharAt(descriptionBuilder.length()-1);
        return descriptionBuilder.toString();
    }
}
