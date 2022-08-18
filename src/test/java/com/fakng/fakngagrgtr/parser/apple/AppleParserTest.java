package com.fakng.fakngagrgtr.parser.apple;

import com.fakng.fakngagrgtr.configuration.ParserConfig;
import com.fakng.fakngagrgtr.parser.AbstractParserTest;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AppleParserTest extends AbstractParserTest {

    private static final String URL = "Apple";
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
                new ParserConfig().objectMapper()
        );
    }

    @Test
    void getCompanyName() {
        assertEquals(appleParser.getCompanyName(), "Apple");
    }

    @Test
    public void testParseCreatesVacanciesFromJsonWithCachedLocations() throws IOException {
        Company company = prepareCompany();
        Mockito.when(companyRepository.findByTitle(URL))
                .thenReturn(Optional.of(company));
        Mockito.when(locationProcessor.processLocation(company, "city_1", "country_1"))
                .thenReturn(company.getLocations().get(0));
        Mockito.when(locationProcessor.processLocation(company, "city_2", "country_2"))
                .thenReturn(company.getLocations().get(1));
        Mockito.when(htmlWebClient.getPage(URL))
                .thenReturn(new WebClient().loadHtmlCodeIntoCurrentWindow(Files.readString(Paths.get("src/test/resources/com/fakng/fakngagrgtr/parser/apple-data.html"))));

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
        first.setUrl("https://jobs.apple.com/en-us/details/apple_1");
        first.setCompany(company);
        first.setDescription("team_1\ntitle_1\ndescription_1");
        first.setLocations(company.getLocations());
        vacancies.add(first);

        Vacancy second = new Vacancy();
        second.setTitle("title_2");
        second.setUrl("https://jobs.apple.com/en-us/details/apple_2");
        second.setCompany(company);
        second.setDescription("team_2\ntitle_2\ndescription_2");
        second.setLocations(company.getLocations().subList(0, 1));
        vacancies.add(second);

        return vacancies;
    }

    private void assertVacancy(Vacancy expected, Vacancy actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getCompany().getId(), actual.getCompany().getId());
        assertEquals(expected.getLocations().size(), actual.getLocations().size());
        for (int i = 0; i < expected.getLocations().size(); i++) {
            assertLocation(expected.getLocations().get(i), actual.getLocations().get(i));
        }
    }
}