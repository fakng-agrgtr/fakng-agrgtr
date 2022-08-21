package com.fakng.fakngagrgtr.persistent.vacancy;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VacancyBatchRepository {

    private static final String VACANCY_UPSERT = """
    INSERT INTO vacancy (title, description, url, published_date, company_id, job_id)
    VALUES (:title, :description, :url, :publishedDate, :companyId, :jobId)
    ON CONFLICT (company_id, job_id) DO
        UPDATE SET title = :title, description = :description, url = :url, published_date = :publishedDate, last_updated = now()
    """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void upsertAll(List<Vacancy> vacancies) {
        jdbcTemplate.batchUpdate(VACANCY_UPSERT, buildUpsertBatchParams(vacancies));
    }

    private MapSqlParameterSource[] buildUpsertBatchParams(List<Vacancy> vacancies) {
        MapSqlParameterSource[] params = new MapSqlParameterSource[vacancies.size()];
        for (int i = 0; i < vacancies.size(); i++) {
            Vacancy vacancy = vacancies.get(i);
            params[i] = new MapSqlParameterSource();
            params[i].addValue("title", vacancy.getTitle());
            params[i].addValue("description", vacancy.getDescription());
            params[i].addValue("url", vacancy.getUrl());
            params[i].addValue("publishedDate", vacancy.getPublishedDate());
            params[i].addValue("companyId", vacancy.getCompany().getId());
            params[i].addValue("jobId", vacancy.getJobId());
        }
        return params;
    }
}
