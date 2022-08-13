package com.fakng.fakngagrgtr.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class ApiParser extends Parser {

    protected final WebClient webClient;
}
