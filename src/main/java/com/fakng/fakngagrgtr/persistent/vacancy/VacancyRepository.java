package com.fakng.fakngagrgtr.persistent.vacancy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends CrudRepository<Vacancy, Long> {

    @Query(value = "select v from Vacancy v " +
            "join fetch v.company c " +
            "join fetch v.locations l " +
            "where c.id in :companyIds and l.id in :locationIds",
            countQuery = "select count(v) from Vacancy v " +
            "join v.company c " +
            "join v.locations l " +
            "where c.id in :companyIds and l.id in :locationIds")
    Page<Vacancy> findAll(@Param("companyIds") List<Long> companyIds,
                          @Param("locationIds") List<Long> locationIds,
                          Pageable pageable);
}
