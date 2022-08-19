package com.fakng.fakngagrgtr.parser.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacancyDTO {
    private String id;
    private String title;
    private String applyUrl;
    private String responsibilities;
    private String qualifications;
    private List<LocationDTO> locations;
    private String description;
    private String publishDate;
    private String additionalInstructions;
    private String summary;
    private Boolean hasRemote;
}
