package com.fakng.fakngagrgtr.parser.amazon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacancyDTO {
    private String basicQualifications;
    private String city;
    private String countryCode;
    private String description;
    private String idIcims;
    private String jobPath;
    private String postedDate;
    private String preferredQualifications;
    private String title;
}
