package com.fakng.fakngagrgtr.parser.cache;

import com.fakng.fakngagrgtr.location.Location;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LocationCache {

    private final Map<String, Location> cache = new HashMap<>();

    public String getLocationKey(String city, String country) {
        return (city == null ? "NO_CITY" : city) + "#" + country;
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public void putIfAbsent(String key, Location value) {
        cache.putIfAbsent(key, value);
    }

    public void putIfAbsent(String city, String country, Location value) {
        String key = getLocationKey(city, country);
        putIfAbsent(key, value);
    }

    public Location get(String key) {
        return cache.get(key);
    }
}
