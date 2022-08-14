package com.fakng.fakngagrgtr.parser.impl.google;

import com.fakng.fakngagrgtr.entity.Company;
import com.fakng.fakngagrgtr.entity.Vacancy;
import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.parser.impl.google.dto.ResponseDTO;
import com.fakng.fakngagrgtr.parser.impl.google.dto.VacancyDTO;
import com.fakng.fakngagrgtr.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleParser extends ApiParser {

    private static final String GOOGLE_NAME = "Google";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private final Company google;

    public GoogleParser(WebClient webClient, CompanyRepository companyRepository, @Value("${url.google}") String url) {
        super(webClient);
        this.url = url;
        this.google = companyRepository.findByTitle(GOOGLE_NAME).orElse(null);
    }

    @Override
    protected List<Vacancy> getAllVacancies() throws Exception {
        ResponseDTO firstPage = getPage(1);
        int lastPage = firstPage.getCount() / firstPage.getPageSize() + 1;
        List<Vacancy> vacancies = new ArrayList<>();
        for (int index = 2; index <= lastPage; index += 1) {
            List<VacancyDTO> jobs = getPage(index).getJobs();
            vacancies.addAll(jobs.stream().map(this::createVacancy).toList());
        }
        return vacancies;
    }

    private Vacancy createVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(parseVacancyId(dto.getId()));
        vacancy.setTitle(dto.getTitle());
        vacancy.setUrl(dto.getApplyUrl());
        vacancy.setCompany(google);
        if (dto.getPublishDate() != null) {
            vacancy.setAddDate(parseLocalDateTime(dto.getPublishDate()));
        }
        vacancy.setLocation(null); // dto.locations?
        vacancy.setDescription(generateFullDescription(dto));
        return vacancy;
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

    private LocalDateTime parseLocalDateTime(String datetime) {
        return LocalDateTime.parse(datetime, DATE_TIME_FORMATTER);
    }

    private ResponseDTO getPage(int index) throws IOException {
        ResponseSpec response = sendRequest(url + "&page=" + index);
        return response.bodyToMono(ResponseDTO.class).block();
    }

    private ResponseSpec sendRequest(String url) throws IOException {
        return webClient
                .get()
                .uri(url)
                .retrieve();
    }
}
