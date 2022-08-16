package com.fakng.fakngagrgtr.parser.apple;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppleParser extends HtmlParser {

    private static final String URL_FOR_VACANCY = "https://jobs.apple.com/en-us/details/";
    private static final String ELEMENT_NAME_WITH_PAGE_COUNT = "frmPagination";
    private static final String ELEMENT_TAG_WITH_PAGE_COUNT = "span";
    private static final String PATH_TO_VACANCIES = "searchResults";
    private static final String PATH_TO_FIELD_WITH_JSON_BODY = ".//script";
    private static final JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private static final ObjectMapper mapper = new ObjectMapper();


    public AppleParser(WebClient htmlWebClient,
                       CompanyRepository companyRepository,
                       LocationProcessor locationProcessor,
                       @Value("${url.apple}") String url) {
        super(htmlWebClient, companyRepository, locationProcessor);
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
        HtmlPage htmlPage = getResponseByPage(0);
        int countPages = getCountPages(htmlPage);
        List<Vacancy> allVacancies = new ArrayList<>(getVacanciesFromDoc(htmlPage));
        for (int i = 1; i < countPages; i++) {
            htmlPage = getResponseByPage(i);
            allVacancies.addAll(getVacanciesFromDoc(htmlPage));
        }
        return allVacancies;
    }

    private HtmlPage getResponseByPage(int numberOfPage) throws IOException {
        return downloadPage(String.format(url, numberOfPage));
    }

    private int getCountPages(HtmlPage htmlPage) {
        return Integer.parseInt(htmlPage.getElementsByIdAndOrName(ELEMENT_NAME_WITH_PAGE_COUNT).get(0).getElementsByTagName(ELEMENT_TAG_WITH_PAGE_COUNT).get(1).getVisibleText());
    }

    private List<Vacancy> getVacanciesFromDoc(HtmlPage htmlpage) throws ParseException, JsonProcessingException {
        String jsonBody = getJsonBody(htmlpage);
        return getVacanciesFromJsonBody(jsonBody);
    }

    private String getJsonBody(HtmlPage htmlPage) {
        String textContent = ((HtmlScript) htmlPage.getByXPath(PATH_TO_FIELD_WITH_JSON_BODY).get(1)).getTextContent();
        return textContent.substring(textContent.indexOf('{'), textContent.lastIndexOf('}') + 1);
    }

    private List<Vacancy> getVacanciesFromJsonBody(String json) throws ParseException, JsonProcessingException {
        JSONObject jsonBody = parse(json);
        JSONArray searchResults = (JSONArray) jsonBody.get(PATH_TO_VACANCIES);
        List<Vacancy> vacancies = new ArrayList<>();
        for (Object vacancyJson : searchResults) {
            VacancyApple vacancyApple = toVacancyApple(vacancyJson);
            vacancies.add(toVacancy(vacancyApple));
        }
        return vacancies;
    }

    private JSONObject parse(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json);
    }

    private VacancyApple toVacancyApple(Object vacancyJson) throws JsonProcessingException {
        return mapper.readValue(vacancyJson.toString(), VacancyApple.class);
    }

    private Vacancy toVacancy(VacancyApple vacancyApple) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(vacancyApple.getPostingTitle());
        vacancy.setDescription(generateFullDescription(vacancyApple));
        vacancy.setUrl(URL_FOR_VACANCY + vacancyApple.getPositionId());
        vacancy.setPublishedDate(vacancyApple.getPostDateTime());
        vacancy.setCompany(company);
        vacancy.setLocations(toLocations(vacancyApple.getLocations()));
        return vacancy;
    }

    private String generateFullDescription(VacancyApple vacancyApple) {
        return vacancyApple.getTeam().getTeamName() + "\n" +
                vacancyApple.getPostingTitle() + "\n" +
                vacancyApple.getJobSummary();
    }

    private List<Location> toLocations(List<LocationApple> locationsApple) {
        return locationsApple.stream()
                .map(location -> locationProcessor.processLocation(company, location.getCity(), parseCountry(location.getCountryID())))
                .collect(Collectors.toList());
    }

    private String parseCountry(String country) {
        return country.substring(country.indexOf('-') + 1);
    }
}
