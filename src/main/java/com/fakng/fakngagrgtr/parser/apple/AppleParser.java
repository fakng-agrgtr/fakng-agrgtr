package com.fakng.fakngagrgtr.parser.apple;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppleParser extends HtmlParser {

    private static final int TIMEOUT_REQUEST = 5000;
    private static final String URL_FOR_VACANCY = "https://jobs.apple.com/en-us/details/";
    private static final String COUNT_PAGES_ADDRESS = "span[class=pageNumber]";
    private static final String JSON_BODY_ADDRESS = "script[type=text/javascript]";

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
        Document doc = getResponseByPage(0);
        int countPages = getCountPages(doc);
        List<Vacancy> allVacancies = new ArrayList<>(getVacanciesFromDoc(doc));
        for (int i = 1; i < countPages; i++) {
            doc = getResponseByPage(i);
            allVacancies.addAll(getVacanciesFromDoc(doc));
        }
        return allVacancies;
    }

    private Document getResponseByPage(int numberOfPage) throws IOException {
        return Jsoup.parse(new URL(String.format(url, numberOfPage)), TIMEOUT_REQUEST);
    }

    private int getCountPages(Document doc) {
        return Integer.parseInt(doc.select(COUNT_PAGES_ADDRESS).get(1).text());
    }

    private List<Vacancy> getVacanciesFromDoc(Document doc) throws ParseException, JsonProcessingException {
        String jsonBody = getJsonBody(doc);
        return getVacanciesFromJsonBody(jsonBody);
    }

    private String getJsonBody(Document doc) {
        return doc.select(JSON_BODY_ADDRESS)
                .first()
                .childNode(0).toString()
                .replace(";", "")
                .trim()
                .substring(19);
    }

    private List<Vacancy> getVacanciesFromJsonBody(String json) throws ParseException, JsonProcessingException {
        JSONObject jsonBody = parse(json);
        JSONArray searchResults = (JSONArray) jsonBody.get("searchResults");
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
        return country.substring(country.indexOf('-'));
    }
}
