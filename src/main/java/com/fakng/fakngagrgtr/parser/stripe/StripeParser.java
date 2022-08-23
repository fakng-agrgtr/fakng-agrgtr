package com.fakng.fakngagrgtr.parser.stripe;

import com.fakng.fakngagrgtr.parser.HtmlParser;
import com.fakng.fakngagrgtr.parser.LocationProcessor;
import com.fakng.fakngagrgtr.persistent.company.CompanyRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StripeParser extends HtmlParser {

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
        int offset = 0;
        HtmlPage page;
        DomNodeList<DomNode> links;

        List<VacancyDTO> vacancyDTOList = new ArrayList<>();
        List<Vacancy> vacancyList = new ArrayList<>();

        while (true) {
            page = downloadPage(String.format(url, offset));
            links = page.querySelectorAll(".JobsListings__link");

            if (links.size() == 0) {
                break;
            }

            links.forEach(
                    link -> vacancyDTOList.add(fillVacancyDTO(link.getAttributes().getNamedItem("href").getNodeValue().substring(13)))
            );

            vacancyDTOList.forEach(
                    vacancyDTO -> vacancyList.add(fillVacancy(vacancyDTO))
            );
            offset++;
        }

        return vacancyList;
    }

    @SneakyThrows
    private VacancyDTO fillVacancyDTO(String link) {
        VacancyDTO vacancyDTO = new VacancyDTO();

        HtmlPage page = downloadPage("https://stripe.com/jobs/listing" + link);
        DomNodeList<DomNode> description = page.querySelectorAll(".ArticleMarkdown");
        DomNodeList<DomNode> jobElements = page.querySelectorAll(".JobDetailCardProperty__title");
        DomNode title = page.querySelectorAll(".Copy__title").get(0);

        vacancyDTO.setDescription("");

        description.get(0).getChildren().forEach(
                iter -> vacancyDTO.setDescription(vacancyDTO.getDescription() + iter.asNormalizedText())
        );

        vacancyDTO.setLink(link);
        vacancyDTO.setOfficeLocation(List.of(jobElements.get(0).getTextContent().replace(", or ", ", ").split(", ")));
        vacancyDTO.setJobType(jobElements.get(jobElements.size() - 1).getTextContent());
        vacancyDTO.setTeam(jobElements.get(jobElements.size() - 2).getTextContent());
        vacancyDTO.setRoles(title.getTextContent());

        if (jobElements.size() == 4) {
            vacancyDTO.setHasRemote(true);
            vacancyDTO.setRemoteLocation(List.of(jobElements.get(1).getTextContent().replace(", or ", ", ").split(", ")));
        }

        return vacancyDTO;
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
