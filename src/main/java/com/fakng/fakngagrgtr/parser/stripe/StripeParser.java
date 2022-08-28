package com.fakng.fakngagrgtr.parser.stripe;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@Component
public class StripeParser extends HtmlParser {

    private static final int PAGE_SIZE = 100;
    private static final String STRIPE_URL = "https://stripe.com";
    private static final String JOBS_PAGINATION_LIST_XPATH = "//a[text()='Last']";
    private static final String VACANCY_TITLE_XPATH = "//h1[@class='Copy__title']";
    private static final String VACANCIES_URLS_XPATH = "//a[@data-analytics-label='jobs_listings_title_link']";
    private static final String VACANCY_DESCRIPTION_BLOCK_XPATH = "//div[@class='ArticleMarkdown']//*";
    private static final String VACANCY_CITIES_XPATH = "//span[@class='JobsListings__locationDisplayName']";
    private static final String VACANCY_JOB_TYPE_XPATH = "//p[text()='Job type']";
    private static final String VACANCY_TEAM_XPATH = "//p[text()='Team']";

    public StripeParser(WebClient htmlWebClient,
                        CompanyRepository companyRepository,
                        LocationProcessor locationProcessor,
                        @Value("${url.stripe}") String url) {
        super(htmlWebClient, companyRepository, locationProcessor);
        this.url = url;
    }

    @PostConstruct
    public void init() {
        initBase();
    }

    @Override
    protected String getCompanyName() {
        return "Stripe";
    }
    @Override
    public List<Vacancy> getAllVacancies() throws IOException {
        HtmlPage page = downloadPage(String.format(url, 0));
        int pageCount = getPageCount(page);
        List<Vacancy> vacancyList = new ArrayList<>(fillVacancies(
                page.getByXPath(VACANCIES_URLS_XPATH), page.getByXPath(VACANCY_CITIES_XPATH)
        ));

        for (int i = 1; i <= pageCount; i++) {
            page = downloadPage(String.format(url, i));
            vacancyList.addAll(fillVacancies(
                    page.getByXPath(VACANCIES_URLS_XPATH), page.getByXPath(VACANCY_CITIES_XPATH)
            ));
        }

        return vacancyList;
    }

    private int getPageCount(HtmlPage page) {
        String url = ((DomElement) page.getFirstByXPath(JOBS_PAGINATION_LIST_XPATH)).getAttribute("href");
        return Integer.parseInt(url.replace("?skip=", "")) / PAGE_SIZE;
    }

    private List<Vacancy> fillVacancies(List<DomNode> urls, List<DomElement> cities) throws IOException {
        List<Vacancy> vacancies = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            String jobId = urls.get(i).getAttributes().getNamedItem("href").getNodeValue();

            // Skip repeating vacancies and parse locations for them
            List<LocationDTO> locationDTOs = new ArrayList<>();
            while (jobId.equals(urls.get(i).getAttributes().getNamedItem("href").getNodeValue())) {
                locationDTOs.add(createLocationDto(cities.get(i)));
                i++;
                if (i >= urls.size()) break;
            }

            // Variable i stopped in incorrect position
            i--;
            vacancies.add(createVacancy(jobId, locationDTOs));
        }

        return vacancies;
    }

    private Vacancy createVacancy(String jobId, List<LocationDTO> locationDTOS) throws IOException {
        Vacancy vacancy = new Vacancy();

        HtmlPage page = downloadPage(STRIPE_URL + jobId);
        DomElement title = page.getFirstByXPath(VACANCY_TITLE_XPATH);
        DomElement jobType = ((DomElement) page.getFirstByXPath(VACANCY_JOB_TYPE_XPATH)).getNextElementSibling();
        DomElement team = ((DomElement) page.getFirstByXPath(VACANCY_TEAM_XPATH)).getNextElementSibling();

        vacancy.setTitle(title.getTextContent());
        vacancy.setDescription(createFullDescription(
                createDescription(page), jobType.getTextContent(), team.getTextContent())
        );
        vacancy.setUrl(STRIPE_URL + jobId);
        vacancy.setJobId(jobId.replaceAll("/.*/", ""));
        vacancy.setCompany(company);
        vacancy.setLocations(processLocations(locationDTOS));

        return vacancy;
    }

    private List<Location> processLocations(List<LocationDTO> locationDTOS) {
        List<Location> locations = new ArrayList<>();
        locationDTOS.forEach(locationDTO -> locations.add(locationProcessor.processLocation(
                    company, locationDTO.getCity(), locationDTO.getCountryCode()
            ))
        );

        return locations;
    }

    private String createDescription(HtmlPage page) {
        List<DomNode> description = page.getByXPath(VACANCY_DESCRIPTION_BLOCK_XPATH);

        StringBuilder stringBuilder = new StringBuilder();
        description.forEach(
                tag -> stringBuilder.append(tag.asNormalizedText()).append("\n")
        );

        return stringBuilder.toString();
    }

    private String createFullDescription(String description, String jobType, String team) {
        return description +
                "Job Type: " + jobType + "\n" +
                "Team: " + team;
    }

    private LocationDTO createLocationDto(DomElement city) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCity(city.getTextContent());
        // Get country code from alt attribute from the image next to city name
        locationDTO.setCountryCode(city.getPreviousElementSibling().getAttribute("alt"));

        return locationDTO;
    }
}
