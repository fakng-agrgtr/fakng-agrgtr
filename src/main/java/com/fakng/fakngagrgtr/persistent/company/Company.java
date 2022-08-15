package com.fakng.fakngagrgtr.persistent.company;

import com.fakng.fakngagrgtr.persistent.location.Location;
import com.fakng.fakngagrgtr.persistent.vacancy.Vacancy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
@Data
@EqualsAndHashCode(of = "id")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "title", length = 32, nullable = false)
    private String title;

    @Column(name = "logo_url")
    private String logoUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "company")
    private List<Vacancy> vacancies;

    @JsonIgnore
    @ManyToMany(mappedBy = "companies")
    private List<Location> locations = new ArrayList<>();

    public void addLocation(Location location) {
        locations.add(location);
    }
}
