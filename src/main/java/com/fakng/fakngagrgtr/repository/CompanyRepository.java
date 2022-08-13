package com.fakng.fakngagrgtr.repository;

import com.fakng.fakngagrgtr.entity.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Optional<Company> findByTitle(String title);
}
