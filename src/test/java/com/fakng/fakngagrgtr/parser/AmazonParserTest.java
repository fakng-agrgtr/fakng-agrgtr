package com.fakng.fakngagrgtr.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fakng.fakngagrgtr.parser.amazon.AmazonParser;
import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class AmazonParserTest extends AbstractParserTest {

    private static final String URL = "Amazon";
    private AmazonParser amazonParser;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private LocationProcessor locationProcessor;

    @BeforeEach
    public void init() throws IOException {
        String json = IOUtils.toString(Objects.requireNonNull(this.getClass()
                .getResourceAsStream("amazon-data.json")), StandardCharsets.UTF_8);
        WebClient webClient = WebClient.builder()
                .exchangeFunction(clientRequest -> Mono.just(
                        ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body(json)
                                .build()
                )).build();
        amazonParser = new AmazonParser(webClient, companyRepository, locationProcessor, URL);
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
        amazonParser.init();
        List<Vacancy> expectedVacancies = prepareVacancies(company);

        List<Vacancy> actualVacancies = amazonParser.getAllVacancies();

        assertEquals(expectedVacancies.size(), actualVacancies.size());
        for (int i = 0; i < expectedVacancies.size(); i++) {
            assertVacancy(expectedVacancies.get(i), actualVacancies.get(i));
        }
    }

    private List<Vacancy> prepareVacancies(Company company) {
        List<Vacancy> vacancies = new ArrayList<>();

        Vacancy first = new Vacancy();
        first.setJobId("994952");
        first.setTitle("title_1");
        first.setUrl("https://www.amazon.jobs" + "/url_1");
        first.setCompany(company);
        first.setDescription("description 1\nbasic_qualifications 1\npreferred_qualifications 1");
        first.addLocation(company.getLocations().get(0));
        first.setPublishedDate(LocalDateTime.of(2019, 11, 18, 0, 0));
        vacancies.add(first);

        Vacancy second = new Vacancy();
        second.setJobId("954155");
        second.setTitle("title_2");
        second.setUrl("https://www.amazon.jobs" + "/url_2");
        second.setCompany(company);
        second.setDescription("description 2\nbasic_qualifications 2\npreferred_qualifications 2");
        second.addLocation(company.getLocations().get(1));
        second.setPublishedDate(LocalDateTime.of(2019, 11, 18, 0, 0));
        vacancies.add(second);

        return vacancies;
    }
}
