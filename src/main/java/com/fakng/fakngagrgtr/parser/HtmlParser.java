package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.company.CompanyRepository;
import com.fakng.fakngagrgtr.location.LocationRepository;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import com.fakng.fakngagrgtr.vacancy.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public abstract class HtmlParser extends Parser {

    private final WebClient htmlWebClient;

    public HtmlParser(WebClient htmlWebClient, LocationCache locationCache,
                      CompanyRepository companyRepository, LocationRepository locationRepository) {
        super(locationCache, companyRepository, locationRepository);
        this.htmlWebClient = htmlWebClient;
    }

    @Override
    public List<Vacancy> getAllVacancies() {
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
