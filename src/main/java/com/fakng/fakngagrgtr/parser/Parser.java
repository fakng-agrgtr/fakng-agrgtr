package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.company.Company;
import com.fakng.fakngagrgtr.company.CompanyRepository;
import com.fakng.fakngagrgtr.vacancy.Vacancy;
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

    protected void initBase() {
        company = companyRepository.findByTitle(getCompanyName())
                .orElseThrow(() -> new IllegalStateException(String.format("There is no %s company present in DB", getCompanyName())));
        locationProcessor.fillLocationCache(company);
    }

    public List<Vacancy> parse() {
        try {
            return getAllVacancies();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract List<Vacancy> getAllVacancies() throws Exception;

    protected abstract String getCompanyName();
}
