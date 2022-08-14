package com.fakng.fakngagrgtr.location;

import com.fakng.fakngagrgtr.vacancy.Vacancy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "location")
@Data
@EqualsAndHashCode(of = "id")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "title", length = 32, nullable = false)
    private String title;

    @JsonIgnore
    @OneToMany(mappedBy = "location")
    private List<Vacancy> vacancies;
}
