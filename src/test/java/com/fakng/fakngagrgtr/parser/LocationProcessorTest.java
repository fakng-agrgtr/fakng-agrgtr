package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.location.LocationRepository;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LocationProcessorTest extends AbstractParserTest {

    private LocationProcessor locationProcessor;

    @Mock
    private LocationRepository locationRepository;

    private final LocationCache locationCache = new LocationCache();

    @BeforeEach
    public void init() {
        locationProcessor = new LocationProcessor(locationCache, locationRepository);
    }

    @Test
    public void testFillCache() {
        Company company = prepareCompany();
        String expectedKey1 = "city_1#country_1";
        String expectedKey2 = "city_2#country_2";

        locationProcessor.fillLocationCache(company);

        assertEquals(company.getLocations().get(0), locationCache.get(expectedKey1));
        assertEquals(company.getLocations().get(1), locationCache.get(expectedKey2));
    }

    @Test
    public void testProcessCachedLocation() {
        Company company = prepareCompany();
        Location expectedLocation = company.getLocations().get(0);
        locationCache.putIfAbsent(expectedLocation.getCity(), expectedLocation.getCountry(), expectedLocation);

        Location actualLocation = locationProcessor.processLocation(company,
                expectedLocation.getCity(), expectedLocation.getCountry());

        assertLocation(expectedLocation, actualLocation);
    }

    @Test
    public void testProcessNotCachedLocation() {
        Company company = prepareCompany();
        Location expectedLocation = company.getLocations().get(0);
        Mockito.when(locationRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0, Location.class));

        Location actualLocation = locationProcessor.processLocation(company,
                expectedLocation.getCity(), expectedLocation.getCountry());

        String key = locationCache.getLocationKey(actualLocation.getCity(), actualLocation.getCountry());
        assertEquals(expectedLocation, locationCache.get(key));
        assertLocation(expectedLocation, actualLocation);
    }
}
