package com.fakng.fakngagrgtr.parser.stripe;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VacancyDTO {
    private String link;
    private String roles;
    private String team;
    private List<String> officeLocation;
    private List<String> remoteLocation; // for remote vacancies
    private String description;
    private Boolean hasRemote = false;
    private String jobType;
}
