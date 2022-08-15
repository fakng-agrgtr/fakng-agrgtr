package com.fakng.fakngagrgtr.service.impl;

import com.fakng.fakngagrgtr.model.Vacancy;
import com.fakng.fakngagrgtr.repository.VacancyRepository;
import com.fakng.fakngagrgtr.service.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Vacancy> findAll(List<Long> companyIds, List<Long> locationIds, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return vacancyRepository.findAll(companyIds, locationIds, pageable);
    }

}
