package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.company.CompanyRepository;
import com.fakng.fakngagrgtr.location.LocationRepository;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public abstract class HtmlParser extends Parser {

    private final WebClient htmlWebClient;

    public HtmlParser(WebClient htmlWebClient, LocationCache locationCache,
                      CompanyRepository companyRepository, LocationRepository locationRepository) {
        super(locationCache, companyRepository, locationRepository);
        this.htmlWebClient = htmlWebClient;
    }

    private HtmlPage downloadPage(String url) throws IOException {
        return htmlWebClient.getPage(url);
    }
}
