package com.fakng.fakngagrgtr.parser.spotify;

import com.fakng.fakngagrgtr.parser.spotify.ResponseDTO;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SpotifyParser extends ApiParser {

    private static final String JOB_URL = "https://www.lifeatspotify.com/jobs/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, u", Locale.ENGLISH);


    public SpotifyParser(WebClient webClient,
                         CompanyRepository companyRepository,
                         LocationProcessor locationProcessor,
                         @Value("${url.spotify}") String url) {
        super(webClient, companyRepository, locationProcessor);
        this.url = url;
    }

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    protected String getCompanyName() {
        return "Spotify";
    }

    @Override
    protected List<Vacancy> getAllVacancies() {
        ResponseDTO page = getPage();
        return new ArrayList<>(processPageResponse(page));
    }

    private List<Vacancy> processPageResponse(ResponseDTO response) {
        return response.getResult().stream()
                .map(this::createVacancy)
                .toList();
    }

    private Vacancy createVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setUrl(JOB_URL+dto.getId());
        vacancy.setTitle(dto.getText());
        return vacancy;
    }


    private ResponseDTO getPage() {
        ResponseSpec response = getRequest(url);
        return response.bodyToMono(ResponseDTO.class).block();
    }
}
