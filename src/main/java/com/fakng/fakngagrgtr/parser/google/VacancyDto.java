package com.fakng.fakngagrgtr.parser.google;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VacancyDto {
    private String id;
    private String title;
    private List<String> categories;
    private String applyUrl;
    private String responsibilities;
    private String qualifications;
    private String companyId;
    private String companyName;
    private String language_code;
    private List<LocationDTO> locations;
    private String description;
    private List<String> educationLevels;
    private String created;
    private String modified;
    private String publishDate;
    private String applicationInstruction;
    private String eeo;
    private Integer locationsCount;
    private String additionalInstructions;
    private String summary;
    private List<BuildingPinDTO> buildingPins;
    private Boolean hasRemote;
}
