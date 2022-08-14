package com.fakng.fakngagrgtr.vacancy;

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
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    @Transactional(readOnly = true)
    public Page<Vacancy> findAll(List<Long> companyIds, List<Long> locationIds, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return vacancyRepository.findAll(companyIds, locationIds, pageable);
    }
}
