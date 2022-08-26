package com.fakng.fakngagrgtr.parser.stripe;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class VacancyDTO {
    private String jobId;
    private String url;
    private String title;
    private Set<LocationDTO> location = new HashSet<>();
    private String description;
    private String team;
    private String jobType;
}
