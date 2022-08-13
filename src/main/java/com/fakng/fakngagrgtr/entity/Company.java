package com.fakng.fakngagrgtr.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "company")
    private List<Vacancy> vacancies;
}
