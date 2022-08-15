package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.location.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractParserTest {

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
}
