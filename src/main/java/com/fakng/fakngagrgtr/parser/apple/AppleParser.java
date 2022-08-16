package com.fakng.fakngagrgtr.parser.apple;

import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppleParser extends ApiParser {

    private static final int TIMEOUT_REQUEST = 5000;
    private static final String URL_FOR_VACANCY = "https://jobs.apple.com/en-us/details/";
    private static final String COUNT_PAGES_ADDRESS = "span[class=pageNumber]";
    private static final String JSON_BODY_ADDRESS = "script[type=text/javascript]";


    public AppleParser(WebClient webClient,
                       CompanyRepository companyRepository,
                       LocationProcessor locationProcessor,
                       @Value("${url.apple}") String url) {
        super(webClient, companyRepository, locationProcessor);
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
        Document doc = getResponseByPage(0);
        int countPages = takeCountPages(doc);
        List<Vacancy> allVacancies = new ArrayList<>(takeVacanciesFromDoc(doc));
        for (int i = 1; i < 2; i++) {
            doc = getResponseByPage(i);
            allVacancies.addAll(takeVacanciesFromDoc(doc));
        }
        return allVacancies;
    }

    private Document getResponseByPage(int x) throws IOException {
        return Jsoup.parse(new URL(String.format(url, x)), TIMEOUT_REQUEST);
    }

    private int takeCountPages(Document doc) {
        return Integer.parseInt(doc.select(COUNT_PAGES_ADDRESS).get(1).text());
    }

    private List<Vacancy> takeVacanciesFromDoc(Document doc) throws ParseException {
        String jsonBody = takeJsonBody(doc);
        return takeVacanciesFromJsonBody(jsonBody);
    }

    private String takeJsonBody(Document doc) {
        return doc.select(JSON_BODY_ADDRESS)
                .first()
                .childNode(0).toString()
                .replace(";", "")
                .trim()
                .substring(19);
    }

    private List<Vacancy> takeVacanciesFromJsonBody(String json) throws ParseException {
        JSONObject jsonBody = parse(json);
        JSONArray searchResults = (JSONArray) jsonBody.get("searchResults");
        List<Vacancy> vacancies = new ArrayList<>();
        for (Object vacancyJson : searchResults) {
            try {
                VacancyApple vacancyApple = toVacancyApple(vacancyJson);
                vacancies.add(toVacancy(vacancyApple));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return vacancies;
    }

    private JSONObject parse(String json) throws ParseException {
        JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        return  (JSONObject) jsonParser.parse(json);
    }

    private VacancyApple toVacancyApple(Object vacancyJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(vacancyJson.toString(), VacancyApple.class);
    }

    private Vacancy toVacancy(VacancyApple vacancyApple) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(vacancyApple.getPostingTitle());
        vacancy.setDescription(generateFullDescription(vacancyApple));
        vacancy.setUrl(URL_FOR_VACANCY + vacancyApple.getPositionId());
        vacancy.setAddDate(LocalDateTime.now());
        vacancy.setPublishedDate(parseVacancyAppleDateTime(vacancyApple.getPostDateInGMT()));
        vacancy.setCompany(company);
        vacancy.setLocations(toLocations(vacancyApple.getLocations()));
        return vacancy;
    }

    private String generateFullDescription(VacancyApple vacancyApple) {
        return vacancyApple.getTeam().getTeamName() + "\n" +
                vacancyApple.getPostingTitle() + "\n" +
                vacancyApple.getJobSummary();
    }

    private LocalDateTime parseVacancyAppleDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime.replace("Z", ""));
    }

    private List<Location> toLocations(List<LocationApple> locationsApple) {
        return locationsApple.stream()
                .map(location -> locationProcessor.processLocation(company, location.getCity(), parseCountry(location.getCountryID())))
                .collect(Collectors.toList());
    }

    private String parseCountry(String country) {
        return country.split("-")[1];
    }
}
