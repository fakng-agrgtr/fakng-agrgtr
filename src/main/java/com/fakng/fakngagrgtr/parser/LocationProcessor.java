package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import com.fakng.fakngagrgtr.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LocationProcessor {

    protected final LocationCache locationCache;
    private final LocationService locationService;

    public void fillLocationCache(Company company) {
        company.getLocations()
                .forEach(location -> locationCache.putIfAbsent(location.getCity(), location.getCountry(), location));
    }

    public Location processLocation(Company company, String city, String country) {
        String locationKey = locationCache.getLocationKey(city, country);
        if (!locationCache.contains(locationKey)) {
            saveInDbAndCache(company, locationKey, city, country);
        }
        return locationCache.get(locationKey);
    }

    private void saveInDbAndCache(Company company, String locationKey, String city, String country) {
        locationService.findByCity(city)
                .or(() -> {
                    Location brandNew = new Location();
                    brandNew.setCity(city);
                    brandNew.setCountry(country);
                    brandNew.addCompany(company);
                    company.addLocation(brandNew);
                    return Optional.of(locationService.save(brandNew));
                }).ifPresent(fresh -> locationCache.putIfAbsent(locationKey, fresh));
    }
}
