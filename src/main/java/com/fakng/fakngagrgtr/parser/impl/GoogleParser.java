package com.fakng.fakngagrgtr.parser.impl;

import com.fakng.fakngagrgtr.entity.Vacancy;
import com.fakng.fakngagrgtr.parser.ApiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class GoogleParser extends ApiParser {

    public GoogleParser(@Autowired WebClient webClient, @Value("${url.google}") String url) {
        super(webClient);
        this.url = url;
    }

    @Override
    protected List<Vacancy> process() throws Exception {
        return null;
    }
}
