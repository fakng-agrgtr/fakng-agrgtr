package com.fakng.fakngagrgtr.parser.spotify;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fakng.fakngagrgtr.vacancy.Vacancy;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fakng.fakngagrgtr.parser.ApiParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;


@Component
public class SpotifyParser extends ApiParser {

    private static final String SPOTIFY_NAME = "Spotify";
    //    private final Company spotify;
    private static final String URL = "https://api-dot-new-spotifyjobs-com.nw.r.appspot.com/wp-json/animal/v1/job/search?q=&l=&c=backend%2Cclient-c%2Cdata%2Cengineering-leadership%2Cmachine-learning%2Cmobile%2Cnetwork-engineering-it%2Csecurity%2Ctech-research%2Cweb&j=";
    private static final String jobURL = "https://www.lifeatspotify.com/jobs";

    public SpotifyParser(WebClient webClient) {
        super(webClient);
//        this.spotify = companyRepository.findByTitle(SPOTIFY_NAME).orElse(null);
    }

//    public static void main(String[] args) {
//        WebClient webClient1 = WebClient.create();
//        SpotifyParser simpleParser = new SpotifyParser(webClient1);
//        try {
//            for (Vacancy vacancy : simpleParser.getAllVacancies()) {
//                System.out.println(vacancy);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    protected List<Vacancy> getAllVacancies() throws Exception {
        List<Vacancy> vacancies = new ArrayList<>();
        JSONObject jsonFromUrl = readJsonFromUrl(URL);
        JSONArray array = (JSONArray) jsonFromUrl.get("result");

        for (Object obj : array) {
            Vacancy vacancy = new Vacancy();
            JSONObject jo = (JSONObject) obj;
            vacancy.setId(null);
            vacancy.setTitle((String) jo.get("text"));
            vacancy.setDescription(null); // Спарсить What You'll Do со страницы с вакансии
            vacancy.setUrl(jobURL + "/" + jo.get("id"));
            vacancy.setAddDate(null);
            vacancy.setCompany(null);
            vacancy.setLocation(null);
            vacancies.add(vacancy);
        }
        return vacancies;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return (JSONObject) new JSONParser().parse(jsonText);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
