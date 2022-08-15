package com.fakng.fakngagrgtr.persistent.company;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

    @Query("select c from Company c left join fetch c.locations where c.title = :title")
    Optional<Company> findByTitle(@Param("title") String title);
}
