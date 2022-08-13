package com.fakng.fakngagrgtr.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ParserConfig {

    @Bean
    public com.gargoylesoftware.htmlunit.WebClient htmlWebClient() {
        com.gargoylesoftware.htmlunit.WebClient webClient = new com.gargoylesoftware.htmlunit.WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false); // probably need to enable it later
        return webClient;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
