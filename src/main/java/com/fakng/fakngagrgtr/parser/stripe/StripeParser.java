package com.fakng.fakngagrgtr.parser.stripe;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Component
public class StripeParser extends HtmlParser {

    private static final int pageSize = 100;
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
        List<Vacancy> vacancyList = new ArrayList<>();

        for (int i = 0; i <= pageCount; i++) {
            page = downloadPage(String.format(url, i));
            vacancyList.addAll(fillVacancy(
                    page.getByXPath(VACANCIES_URLS_XPATH), page.getByXPath(VACANCY_CITIES_XPATH)
            ));
        }

        return vacancyList;
    }

    private int getPageCount(HtmlPage page) {
        String url = ((DomElement) page.getByXPath(JOBS_PAGINATION_LIST_XPATH).get(0)).getAttribute("href");
        return Integer.parseInt(url.replaceAll("\\D+", "")) / pageSize;
    }

    private List<Vacancy> fillVacancy(List<DomNode> urls, List<DomElement> cities) {
        List<Vacancy> vacancies = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            String jobId = urls.get(i).getAttributes().getNamedItem("href").getNodeValue();

            // Skip repeating vacancies and parse locations for them
            Set<LocationDTO> locationDTOs = new HashSet<>();
            while (jobId.equals(urls.get(i).getAttributes().getNamedItem("href").getNodeValue())) {
                locationDTOs.add(createLocationDto(cities.get(i)));
                i++;
            }

            // Variable i stopped in incorrect position
            i--;
            vacancies.add(createVacancy(createVacancyDTO(jobId, locationDTOs)));
        }

        return vacancies;
    }

    @SneakyThrows
    private VacancyDTO createVacancyDTO(String jobUrl, Set<LocationDTO> locationDTOS) {
        VacancyDTO vacancyDTO = new VacancyDTO();

        HtmlPage page = downloadPage(STRIPE_URL + jobUrl);
        DomElement title = (DomElement) page.getByXPath(VACANCY_TITLE_XPATH).get(0);
        DomElement jobType = ((DomElement) page.getByXPath(VACANCY_JOB_TYPE_XPATH).get(0)).getNextElementSibling();
        DomElement team = ((DomElement) page.getByXPath(VACANCY_TEAM_XPATH).get(0)).getNextElementSibling();

        vacancyDTO.setDescription(createDescription(page));
        vacancyDTO.setUrl(STRIPE_URL + jobUrl);
        vacancyDTO.setJobId(jobUrl.replaceAll("\\D+", ""));
        vacancyDTO.setTeam(team.getTextContent());
        vacancyDTO.setLocation(locationDTOS);
        vacancyDTO.setJobType(jobType.getTextContent());
        vacancyDTO.setTitle(title.getTextContent());

        return vacancyDTO;
    }

    private Vacancy createVacancy(VacancyDTO vacancyDTO) {
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(vacancyDTO.getTitle());
        vacancy.setDescription(createFullDescription(vacancyDTO));
        vacancy.setUrl(vacancyDTO.getUrl());
        vacancy.setJobId(vacancyDTO.getJobId());
        vacancy.setCompany(company);
        processLocations(vacancy, vacancyDTO.getLocation());

        return vacancy;
    }

    private void processLocations(Vacancy vacancy, Set<LocationDTO> locations) {
        locations.forEach(location -> vacancy.addLocation(locationProcessor.processLocation(
                    company, location.getCity(), location.getCountryCode()
            ))
        );
    }

    private String createDescription(HtmlPage page) {
        List<DomNode> description = page.getByXPath(VACANCY_DESCRIPTION_BLOCK_XPATH);

        StringBuilder stringBuilder = new StringBuilder();
        description.forEach(
                tag -> stringBuilder.append(tag.asNormalizedText()).append("\n")
        );

        return stringBuilder.toString();
    }

    private String createFullDescription(VacancyDTO vacancyDTO) {
        return vacancyDTO.getDescription() + "\n" +
                "Job Type: " + vacancyDTO.getJobType() + "\n" +
                "Team: " + vacancyDTO.getTeam() + "\n";
    }

    private LocationDTO createLocationDto(DomElement city) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCity(city.getTextContent());
        // Get country code from alt attribute from the image next to city name
        locationDTO.setCountryCode(city.getPreviousElementSibling().getAttribute("alt"));

        return locationDTO;
    }
}
