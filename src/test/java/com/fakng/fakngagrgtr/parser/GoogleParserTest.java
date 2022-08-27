package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.parser.google.GoogleParser;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoogleParserTest extends AbstractParserTest {

    private static final String URL = "Google";
    private GoogleParser googleParser;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private LocationProcessor locationProcessor;

    @BeforeEach
    public void init() throws IOException {
        String json = getTestData("google-data.json");
        googleParser = new GoogleParser(mockWebClient(json), companyRepository, locationProcessor, URL);
    }

    @Test
    public void testParseCreatesVacanciesFromJsonWithCachedLocations() {
        Company company = prepareCompany();
        Mockito.when(companyRepository.findByTitle(URL))
                .thenReturn(Optional.of(company));
        Mockito.when(locationProcessor.processLocation(company, "city_1", "country_1"))
                        .thenReturn(company.getLocations().get(0));
        Mockito.when(locationProcessor.processLocation(company, "city_2", "country_2"))
                .thenReturn(company.getLocations().get(1));
        googleParser.init();
        List<Vacancy> expectedVacancies = prepareVacancies(company);

        List<Vacancy> actualVacancies = googleParser.getAllVacancies();

        assertEquals(expectedVacancies.size(), actualVacancies.size());
        for (int i = 0; i < expectedVacancies.size(); i++) {
            assertVacancy(expectedVacancies.get(i), actualVacancies.get(i));
        }
    }

    private List<Vacancy> prepareVacancies(Company company) {
        List<Vacancy> vacancies = new ArrayList<>();

        Vacancy first = new Vacancy();
        first.setTitle("title_1");
        first.setUrl("apply_url_1");
        first.setJobId("1");
        first.setCompany(company);
        first.setDescription("description_1\nsummary_1\nqualifications_1\nresponsibilities_1\ninstructions_1\nHas remote: true");
        first.setLocations(company.getLocations());
        first.setPublishedDate(LocalDateTime.parse("2022-08-13T00:00:00.001"));
        vacancies.add(first);

        Vacancy second = new Vacancy();
        second.setTitle("title_2");
        second.setUrl("apply_url_2");
        second.setJobId("2");
        second.setCompany(company);
        second.setDescription("description_2\nsummary_2\nqualifications_2\nresponsibilities_2\ninstructions_2\nHas remote: false");
        second.setLocations(company.getLocations().isEmpty() ? new ArrayList<>() : company.getLocations().subList(0, 1));
        second.setPublishedDate(LocalDateTime.parse("2022-08-13T00:00:00.002"));
        vacancies.add(second);

        return vacancies;
    }
}
