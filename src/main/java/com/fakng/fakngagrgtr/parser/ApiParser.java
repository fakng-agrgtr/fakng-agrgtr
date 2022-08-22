package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public abstract class ApiParser extends Parser {

    protected final WebClient webClient;

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

    protected ResponseSpec postRequest(String url, Object body, Class<?> bodyType, Map<String, String> headers) {
        WebClient.RequestBodySpec request = webClient
                .post()
                .uri(url);
        headers.forEach(request::header);
        return request.body(Mono.just(body), bodyType)
                .retrieve();
    }
}
