package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.company.CompanyRepository;
import com.fakng.fakngagrgtr.location.LocationRepository;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import com.fakng.fakngagrgtr.vacancy.Vacancy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class Parser {

    protected final LocationCache locationCache;
    protected final CompanyRepository companyRepository;
    protected final LocationRepository locationRepository;
    protected String url;

    public List<Vacancy> parse() {
        try {
            return getAllVacancies();
        } catch (Exception e) {
            log.error("Error while parsing page under url: {}", url, e);
        }
        return null; //хз
    }

    protected abstract List<Vacancy> getAllVacancies() throws Exception;
}
