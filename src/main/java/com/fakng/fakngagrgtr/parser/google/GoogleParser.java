package com.fakng.fakngagrgtr.parser.google;

import com.fakng.fakngagrgtr.company.Company;
import com.fakng.fakngagrgtr.location.Location;
import com.fakng.fakngagrgtr.location.LocationRepository;
import com.fakng.fakngagrgtr.parser.cache.LocationCache;
import com.fakng.fakngagrgtr.vacancy.Vacancy;
import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.company.CompanyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GoogleParser extends ApiParser {

    private static final String GOOGLE_NAME = "Google";
    private Company google;

    public GoogleParser(WebClient webClient, LocationCache locationCache, CompanyRepository companyRepository,
                        LocationRepository locationRepository, @Value("${url.google}") String url) {
        super(webClient, locationCache, companyRepository, locationRepository);
        this.url = url;
    }

    @PostConstruct
    public void init() {
        google = companyRepository.findByTitle(GOOGLE_NAME)
                .orElseThrow(() -> new IllegalStateException(String.format("There is no %s company present in DB", GOOGLE_NAME)));
        google.getLocations()
                .forEach(location -> locationCache.putIfAbsent(location.getCity(), location.getCountry(), location));
    }

    @Override
    protected List<Vacancy> getAllVacancies() throws Exception {
        ResponseDTO firstPage = getPage(1);
        int lastPage = firstPage.getCount() / firstPage.getPageSize() + 1;
        List<Vacancy> allVacancies = new ArrayList<>(processPageResponse(firstPage));
        for (int index = 2; index <= lastPage; index++) {
            allVacancies.addAll(processPageResponse(getPage(index)));
        }
        return allVacancies;
    }

    private List<Vacancy> processPageResponse(ResponseDTO response) {
        return response.getJobs().stream()
                .map(this::createVacancy)
                .toList();
    }

    private Vacancy createVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(parseVacancyId(dto.getId()));
        vacancy.setTitle(dto.getTitle());
        vacancy.setUrl(dto.getApplyUrl());
        vacancy.setCompany(google);
        processLocations(vacancy, dto.getLocations());
        vacancy.setDescription(generateFullDescription(dto));
        return vacancy;
    }

    private void processLocations(Vacancy vacancy, List<LocationDTO> locations) {
        locations.forEach(location -> {
            String locationKey = locationCache.getLocationKey(location.getCity(), location.getCountryCode());
            if (!locationCache.contains(locationKey)) {
                saveInDbAndCache(locationKey, location);
            }
            vacancy.addLocation(locationCache.get(locationKey));
        });
    }

    private void saveInDbAndCache(String locationKey, LocationDTO location) {
        locationRepository.findByCity(location.getCity())
                .or(() -> {
                    Location brandNew = new Location();
                    brandNew.setCity(location.getCity());
                    brandNew.setCountry(location.getCountryCode());
                    brandNew.addCompany(google);
                    google.addLocation(brandNew);
                    return Optional.of(locationRepository.save(brandNew));
                }).ifPresent(fresh -> locationCache.putIfAbsent(locationKey, fresh));
    }

    private Long parseVacancyId(String dtoId) {
        return Long.parseLong(dtoId.split("/")[1]);
    }

    private String generateFullDescription(VacancyDTO dto) {
        return dto.getDescription() + "\n" +
                dto.getSummary() + "\n" +
                dto.getQualifications() + "\n" +
                dto.getResponsibilities() + "\n" +
                dto.getAdditionalInstructions() + "\n" +
                "Has remote: " + dto.getHasRemote();
    }

    private ResponseDTO getPage(int page) {
        ResponseSpec response = sendRequest(String.format(url, page));
        return response.bodyToMono(ResponseDTO.class).block();
    }

    private ResponseSpec sendRequest(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve();
    }
}
