package com.fakng.fakngagrgtr.service;

import com.fakng.fakngagrgtr.persistent.vacancy.ProcessingStatus;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fakng.fakngagrgtr.persistent.vacancy.VacancyBatchRepository;
import com.fakng.fakngagrgtr.persistent.vacancy.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyBatchRepository vacancyBatchRepository;

    @Transactional(readOnly = true)
    public Page<Vacancy> findAll(List<Integer> companyIds, List<Integer> locationIds, String title, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return vacancyRepository.findAll(companyIds, locationIds, title, pageable);
    }

    @Transactional
    public List<Vacancy> findNotReadyAndPutInProgress(int minutesPerProcessing, int batchSize) {
        Pageable pageable = Pageable.ofSize(batchSize);
        LocalDateTime beforeDate = LocalDateTime.now().minusMinutes(minutesPerProcessing);
        List<Vacancy> vacancies = vacancyRepository.findAllNotReady(beforeDate, pageable);
        vacancies.forEach(vacancy -> vacancy.setStatus(ProcessingStatus.IN_PROGRESS));
        saveAll(vacancies);
        return vacancies;
    }

    @Transactional
    public void saveAll(List<Vacancy> vacancies) {
        vacancyRepository.saveAll(vacancies);
    }

    @Transactional
    public void updateOrMarkInactive(List<Vacancy> vacancies) {
        vacancyBatchRepository.upsertAll(vacancies);
        Integer companyId = vacancies.get(0).getCompany().getId();
        vacancyRepository.markNotPresentAsInactive(companyId, mapToJobIds(vacancies));
    }

    @Transactional
    public void deleteVacancies(LocalDateTime olderThan) {
        vacancyRepository.deleteByLastUpdatedBefore(olderThan);
    }

    private List<String> mapToJobIds(List<Vacancy> vacancies) {
        return vacancies.stream().map(Vacancy::getJobId).toList();
    }
}
