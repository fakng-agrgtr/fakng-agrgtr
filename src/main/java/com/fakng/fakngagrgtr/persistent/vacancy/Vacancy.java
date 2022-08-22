package com.fakng.fakngagrgtr.persistent.vacancy;

import com.fakng.fakngagrgtr.persistent.company.Company;
import com.fakng.fakngagrgtr.persistent.location.Location;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "job_id", length = 32, nullable = false)
    private String jobId;

    @Column(name = "add_date")
    @CreationTimestamp
    private LocalDateTime addDate;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "vacancy_location", joinColumns = @JoinColumn(name = "vacancy_id"), inverseJoinColumns = @JoinColumn(name = "location_id"))
    private List<Location> locations = new ArrayList<>();

    public void addLocation(Location location) {
        locations.add(location);
    }
}
