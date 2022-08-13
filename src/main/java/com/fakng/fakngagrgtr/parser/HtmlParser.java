package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.entity.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class HtmlParser extends Parser {

    private WebClient htmlWebClient;

    @Override
    public List<Vacancy> process() {
        try {
            HtmlPage page = downloadPage();
            return process(page);
        } catch (Exception e) {
            log.error("Error while parsing page under url: {}", url, e);
        }
        return null; //хз
    }

    protected abstract List<Vacancy> process(HtmlPage page) throws Exception;

    private HtmlPage downloadPage() throws IOException {
        return htmlWebClient.getPage(url);
    }
}
