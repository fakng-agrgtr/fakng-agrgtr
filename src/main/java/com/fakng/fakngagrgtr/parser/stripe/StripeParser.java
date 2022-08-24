package com.fakng.fakngagrgtr.parser.stripe;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StripeParser extends HtmlParser {

    private final static String VACANCY_URL = "https://stripe.com/jobs/listing";
    private final static String JOBS_PAGINATION_LIST_XPATH = "/html/body/div/section[4]/div/div[2]/div/div/nav/ul/li[7]/a";
    private final static String VACANCY_TITLE_XPATH = "/html/body/section[1]/div/div[2]/div/div/section/header/h1";

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
            page = downloadPage(String.format(url, pageCount));
            vacancyList.addAll(fillVacancy2(getLinksFromPage(page)));
        }

        return vacancyList;
    }

    private int getPageCount(HtmlPage page) {
        return Integer.parseInt(page.getByXPath(JOBS_PAGINATION_LIST_XPATH).get(0).toString().replaceAll("\\D+", "")) / 100;
    }

    private DomNodeList<DomNode> getLinksFromPage(HtmlPage page) {
        return page.querySelectorAll(".JobsListings__link");
    }

    private List<Vacancy> fillVacancy2(DomNodeList<DomNode> links) {
        List<Vacancy> vacancies = new ArrayList<>();
        links.forEach(link ->
                vacancies.add(fillVacancyDTO(link.getAttributes().getNamedItem("href").getNodeValue().substring(13)))
        );

        return vacancies;
    }

    @SneakyThrows
    private Vacancy fillVacancyDTO(String link) {
        VacancyDTO vacancyDTO = new VacancyDTO();

        HtmlPage page = downloadPage(VACANCY_URL + link);
        DomNodeList<DomNode> description = page.querySelectorAll(".ArticleMarkdown");
        DomNodeList<DomNode> jobElements = page.querySelectorAll(".JobDetailCardProperty__title");
        HtmlHeading1 title = (HtmlHeading1) page.getByXPath(VACANCY_TITLE_XPATH).get(0);

        vacancyDTO.setDescription("");

        description.get(0).getChildren().forEach(
                iter -> vacancyDTO.setDescription(vacancyDTO.getDescription() + iter.asNormalizedText())
        );

        vacancyDTO.setLink(link);
        vacancyDTO.setOfficeLocation(List.of(jobElements.get(0).getNextElementSibling().getTextContent().replace(" or ", "").split(", ")));
        vacancyDTO.setJobType(jobElements.get(jobElements.size() - 1).getNextElementSibling().getTextContent());
        vacancyDTO.setTeam(jobElements.get(jobElements.size() - 2).getNextElementSibling().getTextContent());
        vacancyDTO.setRoles(title.getTextContent());

        if (jobElements.size() == 4) {
            vacancyDTO.setHasRemote(true);
            vacancyDTO.setRemoteLocation(List.of(jobElements.get(1).getNextElementSibling().getTextContent().replace(" or ", "").split(", ")));
        }

        System.out.println(vacancyDTO);

        return fillVacancy(vacancyDTO);
    }

    private Vacancy fillVacancy(VacancyDTO vacancyDTO) {
        Vacancy vacancy = new Vacancy();

        List<String> locations = new ArrayList<>();
        String description = vacancyDTO.getDescription() +
                (vacancyDTO.getHasRemote() ? "Has Remote: true" : "Has Remote: false");

        if (vacancyDTO.getRemoteLocation() != null) {
            locations.addAll(vacancyDTO.getRemoteLocation());
        }
        locations.addAll(vacancyDTO.getOfficeLocation());

        vacancy.setTitle(vacancyDTO.getRoles());
        vacancy.setUrl("https://stripe.com/jobs/listing" + vacancyDTO.getLink());
        vacancy.setDescription(description);
        vacancy.setCompany(company);
        processLocations(vacancy, locations);

        return vacancy;
    }

    private void processLocations(Vacancy vacancy, List<String> locations) {
        locations.forEach(location -> {
            vacancy.addLocation(locationProcessor.processLocation(
                    company, location, location
            ));
        });
    }
}
