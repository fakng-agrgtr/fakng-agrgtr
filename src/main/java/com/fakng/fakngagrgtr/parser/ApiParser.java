package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public abstract class ApiParser extends Parser {

    protected final WebClient webClient;

    public ApiParser(WebClient webClient, CompanyRepository companyRepository, LocationProcessor locationProcessor) {
        super(companyRepository, locationProcessor);
        this.webClient = webClient;
    }
}
