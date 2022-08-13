package com.fakng.fakngagrgtr.entity;

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

    @OneToMany(mappedBy = "location")
    private List<Vacancy> vacancies;
}