package com.fakng.fakngagrgtr.parser.apple;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppleParser extends HtmlParser {

    private static final String URL_FOR_VACANCY = "https://jobs.apple.com/en-us/details/";
    private static final String XPATH_TO_PAGE_COUNT = "//*[@id='frmPagination']/span[2]/text()";
    private static final String XPATH_TO_FIELD_WITH_JSON_BODY = "/html/body/script[1]/text()";
    private static final String JOB_SUMMARY_XPATH = "//div[@id='jd-job-summary']/span";
    private static final String KEY_QUALIFICATION_XPATH = "//div[@id='jd-key-qualifications']//span";
    private static final String DESCRIPTION_XPATH = "//div[@id='jd-description']/span";
    private static final String EXPERIENCE_XPATH = "//div[@id='jd-education-experience']/span";
    private static final String ADDITIONAL_REQ_XPATH = "//div[@id='jd-additional-requirements']//span";
    private final ObjectMapper mapper;

    public AppleParser(WebClient htmlWebClient,
                       CompanyRepository companyRepository,
                       LocationProcessor locationProcessor,
                       @Value("${url.apple}") String url,
                       ObjectMapper mapper) {
        super(htmlWebClient, companyRepository, locationProcessor);
        this.mapper = mapper;
        this.url = url;
    }

    @Override
    protected String getCompanyName() {
        return "Apple";
    }

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    protected List<Vacancy> getAllVacancies() throws Exception {
        HtmlPage htmlPage = getPage(0);
        int countPages = getCountPages(htmlPage);
        List<Vacancy> allVacancies = new ArrayList<>(getVacanciesFromHtml(htmlPage));
        for (int i = 1; i < countPages; i++) {
            htmlPage = getPage(i);
            allVacancies.addAll(getVacanciesFromHtml(htmlPage));
        }
        return allVacancies;
    }

    @Override
    protected Iterable<Vacancy> enrichVacanciesWithDetails(Iterable<Vacancy> vacancies) throws IOException {
        for (Vacancy vacancy : vacancies) {
            if (!vacancy.isFullyConstructed()) {
                HtmlPage vacancyPage = downloadPage(vacancy.getUrl());
                vacancy.setDescription(buildDescription(vacancyPage));
                vacancy.setFullyConstructed(true);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return vacancies;
    }

    private String buildDescription(HtmlPage vacancyPage) {
        String summary = getElementText(vacancyPage, JOB_SUMMARY_XPATH);
        String keyQualifications = buildFromParts(vacancyPage, KEY_QUALIFICATION_XPATH);
        String description = getElementText(vacancyPage, DESCRIPTION_XPATH);
        String educationAndExperience = getElementText(vacancyPage, EXPERIENCE_XPATH);
        String additionalReqs = buildFromParts(vacancyPage, ADDITIONAL_REQ_XPATH);
        return buildDescription(summary, keyQualifications, description, educationAndExperience, additionalReqs);
    }

    private String buildFromParts(HtmlPage vacancyPage, String partXpath) {
        StringBuilder builder = new StringBuilder();
        List<DomElement> qualificationElements = vacancyPage.getByXPath(partXpath);
        qualificationElements.forEach(qualification -> builder.append(qualification.getTextContent()).append("\n"));
        return builder.toString();
    }

    private String getElementText(HtmlPage vacancyPage, String xpath) {
        DomElement element = vacancyPage.getFirstByXPath(xpath);
        if (element != null) {
            return element.getTextContent();
        } else {
            return null;
        }
    }

    private HtmlPage getPage(int page) throws IOException {
        return downloadPage(String.format(url, page));
    }

    private int getCountPages(HtmlPage htmlPage) {
        return Integer.parseInt(htmlPage.getByXPath(XPATH_TO_PAGE_COUNT).get(0).toString());
    }

    private List<Vacancy> getVacanciesFromHtml(HtmlPage htmlpage) throws JsonProcessingException {
        String jsonBody = getJsonBody(htmlpage);
        return getVacanciesFromJsonBody(jsonBody);
    }

    private String getJsonBody(HtmlPage htmlPage) {
        String textContent = ((DomText) htmlPage.getByXPath(XPATH_TO_FIELD_WITH_JSON_BODY).get(0)).getData();
        return textContent.substring(textContent.indexOf('{'), textContent.lastIndexOf('}') + 1);
    }

    private List<Vacancy> getVacanciesFromJsonBody(String json) throws JsonProcessingException {
        ResponseDTO responseDTO = mapper.readValue(json, ResponseDTO.class);
        return responseDTO.getSearchResults().stream()
                .map(this::parseVacancy)
                .toList();
    }

    private Vacancy parseVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(dto.getPostingTitle());
        vacancy.setDescription(buildDescription(dto.getPostingTitle(), dto.getTeam().getTeamName(), dto.getJobSummary()));
        vacancy.setJobId(dto.getPositionId());
        vacancy.setUrl(URL_FOR_VACANCY + dto.getPositionId());
        vacancy.setPublishedDate(parseLocalDateTime(dto.getPostDateInGMT()));
        vacancy.setCompany(company);
        vacancy.setLocations(parseLocations(dto.getLocations()));
        return vacancy;
    }

    private LocalDateTime parseLocalDateTime(String postDateInGMT) {
        return LocalDateTime.parse(postDateInGMT.replace("Z", ""));
    }

    private List<Location> parseLocations(List<LocationDTO> locationDTOs) {
        return locationDTOs.stream()
                .map(locationDTO -> locationProcessor.processLocation(
                        company,
                        locationDTO.getCity(),
                        parseCountry(locationDTO.getCountryID())))
                .toList();
    }

    private String parseCountry(String country) {
        return country.substring(country.indexOf('-') + 1);
    }
}
