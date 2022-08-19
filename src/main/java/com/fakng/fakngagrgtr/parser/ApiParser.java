package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public abstract class ApiParser extends Parser {

    protected final WebClient webClient;

    protected static WebClient createWebClient(String url) {
        if (url.isEmpty()) throw new RuntimeException();
        return WebClient.create(url);
    }

    public ApiParser(WebClient webClient, CompanyRepository companyRepository, LocationProcessor locationProcessor) {
        super(companyRepository, locationProcessor);
        this.webClient = webClient;
    }

    protected ResponseSpec getRequest(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve();
    }

    protected ResponseSpec postRequest(String url, Object body, Class<?> bodyType) {
        return webClient
                .post()
                .uri(url)
                .body(Mono.just(body), bodyType)
                .retrieve();
    }
}
