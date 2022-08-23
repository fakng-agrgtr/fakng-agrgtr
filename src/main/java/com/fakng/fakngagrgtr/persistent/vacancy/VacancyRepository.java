package com.fakng.fakngagrgtr.persistent.vacancy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VacancyRepository extends CrudRepository<Vacancy, Long> {

    @Query(value = "select v from Vacancy v " +
            "join fetch v.company c " +
            "join fetch v.locations l " +
            "where c.id in :companyIds and l.id in :locationIds and lower(v.title) like lower(CONCAT('%',:title,'%'))",
            countQuery = "select count(v) from Vacancy v " +
            "join v.company c " +
            "join v.locations l " +
            "where c.id in :companyIds and l.id in :locationIds and lower(v.title) like lower(CONCAT('%',:title,'%'))")
    Page<Vacancy> findAll(@Param("companyIds") List<Integer> companyIds,
                          @Param("locationIds") List<Integer> locationIds,
                          @Param("title") String title,
                          Pageable pageable);

    @Modifying
    @Query(value = "UPDATE vacancy SET active = false WHERE company_id = :companyId AND job_id NOT IN (:jobIds)", nativeQuery = true)
    void markNotPresentAsInactive(@Param("companyId") Integer companyId, @Param("jobIds") List<String> jobIds);

    @Modifying
    void deleteByLastUpdatedBefore(LocalDateTime date);
}
