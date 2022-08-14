package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.company.CompanyRepository;
import com.fakng.fakngagrgtr.location.LocationRepository;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public abstract class ApiParser extends Parser {

    protected final WebClient webClient;

    public ApiParser(WebClient webClient, LocationCache locationCache,
                     CompanyRepository companyRepository, LocationRepository locationRepository) {
        super(locationCache, companyRepository, locationRepository);
        this.webClient = webClient;
    }
}
