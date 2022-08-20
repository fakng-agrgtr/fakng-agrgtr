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

    private boolean requestsRestriction = false;
    private Queue<Vacancy> newVacancies;
    private LocalDateTime requestingNewVacanciesDelay;

    public void initialize() {
        try {
            newVacancies = new LinkedList<>(getAllVacancies());
            if (newVacancies.size() > 0) {
                requestingNewVacanciesDelay = LocalDateTime.now();
                enableRequestsRestriction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Vacancy> parse() {
        try {
            List<Vacancy> allVacancies = getAllVacancies();
            newVacancies = new LinkedList<>(findNewVacancies(allVacancies));
            if (newVacancies.size() > 0) {
                requestingNewVacanciesDelay = LocalDateTime.now();
            }
            return allVacancies;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enableRequestsRestriction() {
        requestsRestriction = true;
    }

    public void disableRequestsRestriction() {
        requestsRestriction = false;
    }

    public LocalDateTime getRequestingNewVacanciesDelay() {
        return requestingNewVacanciesDelay;
    }

    public void requestNewVacanciesDetails() {
        int requestsAmount = 0;
        while (newVacancies.size() > 0) {
            if (requestsRestriction && requestsAmount >= REQUESTS_LIMIT) {
                requestingNewVacanciesDelay = LocalDateTime.now().plusHours(REQUESTING_DELAY);
                return;
            }
            Vacancy vacancy = newVacancies.remove();
            if (saveNewVacancyDetails(vacancy.getUrl())) {
                requestsAmount += 1;
            }
        }
        requestingNewVacanciesDelay = null;
    }

    protected void initBase() {
        company = companyRepository.findByTitle(getCompanyName())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("There is no %s company present in DB", getCompanyName())));
        locationProcessor.fillLocationCache(company);
    }

    protected abstract List<Vacancy> getAllVacancies() throws Exception;

    protected Vacancy parseVacancyPage(String url) {
        return null;
    }

    protected abstract String getCompanyName();

    private List<Vacancy> findNewVacancies(List<Vacancy> allVacancies) {
        return allVacancies; // TODO
    }

    private boolean saveNewVacancyDetails(String url) {
        Vacancy vacancy = parseVacancyPage(url);
        if (vacancy != null) {
            vacancyRepository.save(vacancy);
            return true;
        } else return false;
    }
}
