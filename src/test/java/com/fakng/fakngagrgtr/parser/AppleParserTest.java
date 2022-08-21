package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.parser.apple.AppleParser;
import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AppleParserTest extends AbstractParserTest {

    private static final String URL = "Apple";
    private static final String DATA_PATH = "apple-data.html";
    private AppleParser appleParser;
    @Mock
    private WebClient htmlWebClient;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private LocationProcessor locationProcessor;

    @BeforeEach
    public void init() {
        appleParser = new AppleParser(
                htmlWebClient,
                companyRepository,
                locationProcessor,
                URL,
                getObjectMapper()
        );
    }

    private ObjectMapper getObjectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .build()
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Test
    public void testParseCreatesVacanciesFromJsonWithCachedLocations() throws Exception {
        Company company = prepareCompany();
        Mockito.when(companyRepository.findByTitle(URL))
                .thenReturn(Optional.of(company));
        Mockito.when(locationProcessor.processLocation(company, "city_1", "country_1"))
                .thenReturn(company.getLocations().get(0));
        Mockito.when(locationProcessor.processLocation(company, "city_2", "country_2"))
                .thenReturn(company.getLocations().get(1));
        mockHtmlClient(trueClient -> Mockito.when(htmlWebClient.getPage(URL))
                .thenReturn(trueClient.loadHtmlCodeIntoCurrentWindow(getTestData(DATA_PATH))));

        appleParser.init();

        List<Vacancy> expectedVacancies = prepareVacancies(company);
        List<Vacancy> actualVacancies = appleParser.parse();

        assertEquals(expectedVacancies.size(), actualVacancies.size());
        for (int i = 0; i < expectedVacancies.size(); i++) {
            assertVacancy(expectedVacancies.get(i), actualVacancies.get(i));
        }
    }

    private List<Vacancy> prepareVacancies(Company company) {
        List<Vacancy> vacancies = new ArrayList<>();

        Vacancy first = new Vacancy();
        first.setTitle("title_1");
        first.setJobId("apple_1");
        first.setUrl("https://jobs.apple.com/en-us/details/" + first.getJobId());
        first.setCompany(company);
        first.setDescription("team_1\ntitle_1\ndescription_1");
        first.setLocations(company.getLocations());
        first.setPublishedDate(LocalDateTime.parse("2022-08-13T00:00:00.000000001"));
        vacancies.add(first);

        Vacancy second = new Vacancy();
        second.setTitle("title_2");
        second.setJobId("apple_2");
        second.setUrl("https://jobs.apple.com/en-us/details/" + second.getJobId());
        second.setCompany(company);
        second.setDescription("team_2\ntitle_2\ndescription_2");
        second.setLocations(company.getLocations().subList(0, 1));
        second.setPublishedDate(LocalDateTime.parse("2022-08-13T00:00:00.000000002"));
        vacancies.add(second);

        return vacancies;
    }
}