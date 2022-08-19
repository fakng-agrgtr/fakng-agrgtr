package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

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

    protected ResponseSpec getRequest(Function<UriBuilder, URI> uriFunction) {
        return webClient
                .get()
                .uri(uriFunction)
                .retrieve();
    }

    protected ResponseSpec postRequest(Function<UriBuilder, URI> uriFunction, Object body, Class<?> bodyType) {
        return webClient
                .post()
                .uri(uriFunction)
                .body(Mono.just(body), bodyType)
                .retrieve();
    }
}
