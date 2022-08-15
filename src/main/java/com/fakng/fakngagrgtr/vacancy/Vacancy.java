package com.fakng.fakngagrgtr.vacancy;

import com.fakng.fakngagrgtr.company.Company;
import com.fakng.fakngagrgtr.location.Location;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vacancy")
@Data
@EqualsAndHashCode(of = "id")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "title", length = 128, nullable = false)
    private String title;

    @Column(name = "description", length = 4096, nullable = false)
    private String description;

    @Column(name = "url", length = 512, nullable = false)
    private String url;

    @Column(name = "add_date")
    @CreationTimestamp
    private LocalDateTime addDate;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
}
