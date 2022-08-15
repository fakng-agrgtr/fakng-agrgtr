package com.fakng.fakngagrgtr.parser;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class HtmlParser extends Parser {

    private final WebClient htmlWebClient;

    private HtmlPage downloadPage(String url) throws IOException {
        return htmlWebClient.getPage(url);
    }
}
