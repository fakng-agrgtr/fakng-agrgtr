package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.parser.uber.UberParser;
import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
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
public class UberParserTest extends AbstractParserTest {

    private static final String URL = "Uber";

    private UberParser uberParser;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private LocationProcessor locationProcessor;

    @BeforeEach
    public void init() throws IOException {
        String json = getTestData("uber-data.json");
        uberParser = new UberParser(mockWebClient(json), companyRepository, locationProcessor, URL);
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
        uberParser.init();
        List<Vacancy> expectedVacancies = prepareVacancies(company);

        List<Vacancy> actualVacancies = uberParser.parse();

        assertEquals(expectedVacancies.size(), actualVacancies.size());
        for (int i = 0; i < expectedVacancies.size(); i++) {
            assertVacancy(expectedVacancies.get(i), actualVacancies.get(i));
        }
    }

    private List<Vacancy> prepareVacancies(Company company) {
        List<Vacancy> vacancies = new ArrayList<>();

        Vacancy first = new Vacancy();
        first.setTitle("title_1");
        first.setUrl("https://www.uber.com/global/en/careers/list/1/");
        first.setJobId("1");
        first.setCompany(company);
        first.setDescription("desc_1");
        first.setLocations(company.getLocations());
        first.setPublishedDate(LocalDateTime.parse("2022-08-14T00:00:00.001"));
        vacancies.add(first);

        Vacancy second = new Vacancy();
        second.setTitle("title_2");
        second.setUrl("https://www.uber.com/global/en/careers/list/2/");
        second.setJobId("2");
        second.setCompany(company);
        second.setDescription("desc_2");
        second.setLocations(company.getLocations().isEmpty() ? new ArrayList<>() : company.getLocations().subList(0, 1));
        second.setPublishedDate(LocalDateTime.parse("2022-08-13T00:00:00.002"));
        vacancies.add(second);

        return vacancies;
    }
}
