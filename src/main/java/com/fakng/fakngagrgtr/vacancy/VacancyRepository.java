package com.fakng.fakngagrgtr.vacancy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends CrudRepository<Vacancy, Long> {

    @Query("select v from Vacancy v where v.company.id in :companyIds and v.location.id in :locationIds")
    Page<Vacancy> findAll(@Param("companyIds") List<Long> companyIds,
                          @Param("locationIds") List<Long> locationIds,
                          Pageable pageable);
}
