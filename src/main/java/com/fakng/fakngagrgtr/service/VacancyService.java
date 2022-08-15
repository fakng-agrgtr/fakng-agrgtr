package com.fakng.fakngagrgtr.service;

import com.fakng.fakngagrgtr.model.Vacancy;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VacancyService {

    Page<Vacancy> findAll(List<Long> companyIds, List<Long> locationIds, int page, int pageSize);

}
