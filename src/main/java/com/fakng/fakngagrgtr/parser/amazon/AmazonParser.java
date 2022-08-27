package com.fakng.fakngagrgtr.parser.amazon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

@Component
public class AmazonParser extends ApiParser {

    // Amazon doesn't allow to get more than 10000 vacancies.
    private static final int MAX_VACANCIES_COUNT = 10000;
    private static final String JOB_URL_FORMAT = "https://www.amazon.jobs%s";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, u", Locale.ENGLISH);

    public AmazonParser(WebClient webClient,
            CompanyRepository companyRepository,
            LocationProcessor locationProcessor,
            @Value("${amazon.url}") String url) {
        super(webClient, companyRepository, locationProcessor);
        this.url = url;
    }

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    protected String getCompanyName() {
        return "Amazon";
    }

    @Override
    public List<Vacancy> getAllVacancies() {
        int offset = 0;
        ResponseDTO firstPage = getPage(offset);
        List<Vacancy> vacancies = new ArrayList<>(processPageResponse(firstPage));
        while (vacancies.size() < firstPage.getHits() && vacancies.size() < MAX_VACANCIES_COUNT) {
            ResponseDTO page = getPage(vacancies.size());
            vacancies.addAll(processPageResponse(page));
        }
        return vacancies;
    }

    @Override
    public void enrichWithDetails(Vacancy vacancy) {

    }

    private List<Vacancy> processPageResponse(ResponseDTO response) {
        return Optional.of(response.getJobs())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::createVacancy)
                .toList();
    }

    private Vacancy createVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(dto.getTitle());
        vacancy.setUrl(parseUrl(dto));
        vacancy.setJobId(dto.getIdIcims());
        vacancy.setPublishedDate(parseLocalDateTime(dto));
        vacancy.setCompany(company);
        vacancy.addLocation(parseLocation(dto));
        vacancy.setDescription(generateFullDescription(dto));
        return vacancy;
    }

    private String parseUrl(VacancyDTO dto) {
        return String.format(JOB_URL_FORMAT, dto.getJobPath());
    }

    private LocalDateTime parseLocalDateTime(VacancyDTO dto) {
        String date = dto.getPostedDate();
        date = date.replace("  ", " ");
        LocalDate parsedDate = LocalDate.parse(date, DATE_TIME_FORMATTER);
        return parsedDate.atStartOfDay();
    }

    private Location parseLocation(VacancyDTO dto) {
        return locationProcessor.processLocation(
                company, dto.getCity(), dto.getCountryCode());
    }

    private String generateFullDescription(VacancyDTO dto) {
        return dto.getDescription() + "\n"
                + dto.getBasicQualifications() + "\n"
                + dto.getPreferredQualifications();
    }

    private ResponseDTO getPage(int offset) {
        ResponseSpec request = getRequest(String.format(url, offset));
        return request.bodyToMono(ResponseDTO.class).block();
    }
}
