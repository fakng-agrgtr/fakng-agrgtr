package com.fakng.fakngagrgtr.persistent.location;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Data
@EqualsAndHashCode(of = "id")
public class Location {

    @Id
    @GeneratedValue(generator = "location-seq-generator")
    @GenericGenerator(name = "location-seq-generator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "location_seq"))
    private Integer id;

    @Column(name = "city", length = 32)
    private String city;

    @Column(name = "country", length = 32, nullable = false)
    private String country;

    @JsonIgnore
    @ManyToMany(mappedBy = "locations")
    private List<Vacancy> vacancies;

    @ManyToMany
    @JoinTable(name = "company_location", joinColumns = @JoinColumn(name = "location_id"), inverseJoinColumns = @JoinColumn(name = "company_id"))
    private List<Company> companies = new ArrayList<>();

    public void addCompany(Company company) {
        companies.add(company);
    }

    public void addVacancy(Vacancy vacancy) {
        vacancies.add(vacancy);
    }
}
