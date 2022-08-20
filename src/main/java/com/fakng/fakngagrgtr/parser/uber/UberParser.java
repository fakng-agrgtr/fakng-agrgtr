package com.fakng.fakngagrgtr.parser.uber;

import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class UberParser extends ApiParser {

    private static final String VACANCY_FORMAT = "https://www.uber.com/global/en/careers/list/%s/";

    public UberParser(WebClient webClient, CompanyRepository companyRepository, LocationProcessor locationProcessor,
                      @Value("${url.uber}") String url) {
        super(webClient, companyRepository, locationProcessor);
        this.url = url;
    }

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    protected List<Vacancy> getAllVacancies() {
        return requestVacancies().stream()
                .map(this::createVacancy)
                .toList();
    }

    private Vacancy createVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setJobId(dto.getId());
        vacancy.setTitle(dto.getTitle());
        vacancy.setDescription(dto.getDescription());
        vacancy.setUrl(String.format(VACANCY_FORMAT, vacancy.getJobId()));
        vacancy.setCompany(company);
        vacancy.setPublishedDate(parseDate(dto.getUpdatedDate()));
        dto.getAllLocations().forEach(location -> vacancy.addLocation(
                locationProcessor.processLocation(company, location.getCity(), location.getCountry())
        ));
        return vacancy;
    }

    private LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date.replace("Z", ""));
    }

    private List<VacancyDTO> requestVacancies() {
        RequestDTO request = new RequestDTO();
        request.addParam("query", "software engineer");
        ResponseDTO response = postRequest(url, request, RequestDTO.class, Map.of("X-Csrf-Token", "''"))
                .bodyToMono(ResponseDTO.class)
                .block();
        if (response != null) {
            return response.getData().getResults();
        } else {
            throw new IllegalStateException("No Uber vacancies found");
        }
    }

    @Override
    protected String getCompanyName() {
        return "Uber";
    }
}
