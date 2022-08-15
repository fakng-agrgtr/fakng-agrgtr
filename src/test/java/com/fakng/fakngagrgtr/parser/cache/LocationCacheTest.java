package com.fakng.fakngagrgtr.parser.cache;

import com.fakng.fakngagrgtr.persistent.location.Location;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationCacheTest {

    private final LocationCache cache = new LocationCache();

    @Test
    public void testCityAndCountryKeyGenerated() {
        testKeyGeneration("city");
    }

    @Test
    public void testNullCityAndCountryKeyGenerated() {
        testKeyGeneration(null);
    }

    @Test
    public void testContainsFindsKey() {
        String key = "key";
        Location location = Mockito.mock(Location.class);
        cache.putIfAbsent(key, location);
        assertTrue(cache.contains(key));
    }

    @Test
    public void testContainsCannotFindKey() {
        String key = "key";
        Location location = Mockito.mock(Location.class);
        cache.putIfAbsent(key, location);
        assertFalse(cache.contains("wrong_key"));
    }

    @Test
    public void testGetReturnsCorrectValue() {
        String key1 = "key1";
        String key2 = "key2";
        Location location1 = Mockito.mock(Location.class);
        Location location2 = Mockito.mock(Location.class);
        cache.putIfAbsent(key1, location1);
        cache.putIfAbsent(key2, location2);
        assertEquals(location1, cache.get(key1));
    }

    @Test
    public void testPutGeneratesKeyAndAddsValue() {
        String city = "city";
        String country = "country";
        String expectedKey = "city#country";
        Location location = Mockito.mock(Location.class);
        cache.putIfAbsent(city, country, location);
        assertEquals(location, cache.get(expectedKey));
    }

    private void testKeyGeneration(String city) {
        String country = "country";
        String expectedKey = city == null ? "NO_CITY#country" : city + "#country";
        String actualKey = cache.getLocationKey(city, country);
        assertEquals(expectedKey, actualKey);
    }
}
