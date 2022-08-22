package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractParserTest {

    protected String getTestData(String fileName) throws IOException {
        return IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream(fileName)), StandardCharsets.UTF_8);
    }

    protected WebClient mockWebClient(String body) {
        return WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body(body)
                                .build()))
                .build();
    }

    protected void mockHtmlClient(Consumer<com.gargoylesoftware.htmlunit.WebClient> trueClientConsumer) throws Exception {
        try (com.gargoylesoftware.htmlunit.WebClient trueClient = new com.gargoylesoftware.htmlunit.WebClient()) {
            trueClientConsumer.accept(trueClient);
        }
    }

    protected void assertLocation(Location expected, Location actual) {
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getCompanies().get(0), actual.getCompanies().get(0));
    }

    protected Company prepareCompany() {
        Company company = new Company();
        Location first = prepareLocation("city_1", "country_1");
        first.addCompany(company);
        Location second = prepareLocation("city_2", "country_2");
        second.addCompany(company);
        company.addLocation(first);
        company.addLocation(second);
        return company;
    }

    protected Location prepareLocation(String city, String country) {
        Location location = new Location();
        location.setCity(city);
        location.setCountry(country);
        return location;
    }

    protected void assertVacancy(Vacancy expected, Vacancy actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getJobId(), actual.getJobId());
        assertEquals(expected.getPublishedDate(), actual.getPublishedDate());
        assertEquals(expected.getCompany().getId(), actual.getCompany().getId());
        assertEquals(expected.getLocations().size(), actual.getLocations().size());
        for (int i = 0; i < expected.getLocations().size(); i++) {
            assertLocation(expected.getLocations().get(i), actual.getLocations().get(i));
        }
    }

    @FunctionalInterface
    protected interface Consumer<T> {
        void accept(T arg) throws Exception;
    }
}
