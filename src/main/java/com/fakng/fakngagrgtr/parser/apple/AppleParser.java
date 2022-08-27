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
    private static final String KEY_QUALIFICATIONS_XPATH = "//div[@id='jd-key-qualifications']//span";
    private static final String BASIC_DESCRIPTION_XPATH = "//div[@id='jd-description']/span";
    private static final String ADDITIONAL_REQS_XPATH = "//div[@id='jd-additional-requirements']//span";
    private static final String EDUCATION_XPATH = "//div[@id='jd-education-experience']/span";
    private final ObjectMapper mapper;

    public AppleParser(WebClient htmlWebClient,
                       CompanyRepository companyRepository,
                       LocationProcessor locationProcessor,
                       @Value("${apple.url}") String url,
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
    public List<Vacancy> getAllVacancies() throws Exception {
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
    public void enrichWithDetails(Vacancy vacancy) throws IOException {
        HtmlPage page = downloadPage(vacancy.getUrl());
        String description = parseDescription(page);
        vacancy.setDescription(description);
    }

    private String parseDescription(HtmlPage page) {
        String summary = parseDescriptionPart(page, JOB_SUMMARY_XPATH);
        String keyQualifications = parseMultipleDescriptionParts(page, KEY_QUALIFICATIONS_XPATH);
        String description = parseDescriptionPart(page, BASIC_DESCRIPTION_XPATH);
        String additionalReqs = parseMultipleDescriptionParts(page, ADDITIONAL_REQS_XPATH);
        String education = parseDescriptionPart(page, EDUCATION_XPATH);
        return buildDescription(summary, keyQualifications, description, additionalReqs, education);
    }

    private String parseDescriptionPart(HtmlPage page, String xPath) {
        DomElement element = page.getFirstByXPath(xPath);
        return element.getNodeValue();
    }

    private String parseMultipleDescriptionParts(HtmlPage page, String xPath) {
        List<DomElement> elements = page.getByXPath(xPath);
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.getNodeValue()));
        return builder.toString();
    }

    private HtmlPage getPage(int numberOfPage) throws IOException {
        return downloadPage(String.format(url, numberOfPage));
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

    private Vacancy parseVacancy(VacancyDTO vacancyDTO) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(vacancyDTO.getPostingTitle());
        vacancy.setDescription(generateFullDescription(vacancyDTO));
        vacancy.setJobId(vacancyDTO.getPositionId());
        vacancy.setUrl(URL_FOR_VACANCY + vacancyDTO.getPositionId());
        vacancy.setPublishedDate(parseLocalDateTime(vacancyDTO.getPostDateInGMT()));
        vacancy.setCompany(company);
        vacancy.setLocations(parseLocations(vacancyDTO.getLocations()));
        return vacancy;
    }

    private String generateFullDescription(VacancyDTO vacancyDTO) {
        return vacancyDTO.getTeam().getTeamName() + "\n" +
                vacancyDTO.getPostingTitle() + "\n" +
                vacancyDTO.getJobSummary();
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
