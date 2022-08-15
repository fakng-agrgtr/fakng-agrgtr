package com.fakng.fakngagrgtr.service;

import java.util.Optional;

import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Optional<Location> findByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }
}
