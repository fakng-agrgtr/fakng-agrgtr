package com.fakng.fakngagrgtr.company;

import com.fakng.fakngagrgtr.company.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Optional<Company> findByTitle(String title);
}
