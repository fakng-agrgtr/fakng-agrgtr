package com.fakng.fakngagrgtr.parser.impl.google;

import com.fakng.fakngagrgtr.entity.Vacancy;
import com.fakng.fakngagrgtr.parser.ApiParser;
import com.fakng.fakngagrgtr.parser.impl.google.dto.ResponseDTO;
import com.fakng.fakngagrgtr.parser.impl.google.dto.VacancyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleParser extends ApiParser {

    public GoogleParser(@Autowired WebClient webClient, @Value("${url.google}") String url) {
        super(webClient);
        this.url = url;
    }

    @Override
    protected List<Vacancy> process() throws Exception {
        ResponseDTO firstPage = getPage(1);
        List<VacancyDTO> googleVacancies = new ArrayList<>(firstPage.getJobs());
        for (int i = 2; i <= firstPage.getCount() / firstPage.getPageSize() + 1; i += 1) {
            System.out.println("Page " + i);
            ResponseDTO page = getPage(i);
            googleVacancies.addAll(page.getJobs());
        }
        return googleVacancies.stream().map(this::createVacancy).toList();
    }

    private Vacancy createVacancy(VacancyDTO dto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(Long.parseLong(dto.getId().split("/")[1]));
        vacancy.setTitle(dto.getTitle());
        vacancy.setUrl(dto.getApplyUrl());
        vacancy.setCompany(null); // Google? dto.companyName? dto.companyId? It may be YouTube or anything else.
        if (dto.getPublishDate() != null) {
            vacancy.setAddDate(LocalDateTime.parse(dto.getPublishDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")));
        }
        vacancy.setLocation(null); // dto.locations?
        vacancy.setDescription(
                dto.getDescription() + "\n" +
                dto.getSummary() + "\n" +
                dto.getQualifications() + "\n" +
                dto.getResponsibilities() + "\n" +
                dto.getAdditionalInstructions()
        );
        return vacancy;
    }

    private ResponseDTO getPage(int index) throws IOException {
        String body = sendRequest(url + "&page=" + index);
        return new Gson().fromJson(body, ResponseDTO.class);
    }

    private String sendRequest(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        conn.getResponseCode(); // send request
        StringBuilder responseContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        reader.close();
        return responseContent.toString();
    }
}
