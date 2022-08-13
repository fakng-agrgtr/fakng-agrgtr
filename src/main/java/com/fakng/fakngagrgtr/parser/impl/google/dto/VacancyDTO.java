package com.fakng.fakngagrgtr.parser.impl.google.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VacancyDTO {

    private String id;
    private String title;
    private List<String> categories;
    private String applyUrl;
    private String responsibilities;
    private String qualifications;
    private String companyId;
    private String companyName;
    private String language_code;
    private List<Map<String, Object>> locations;
    private String description;
    private List<String> educationLevels;
    private String created;
    private String modified;
    private String publishDate;
    private String applicationInstruction; // ?
    private String eeo;
    private Integer locationsCount;
    private String additionalInstructions;
    private String summary;
    private List<Map<String, Float>> buildingPins;
    private Boolean hasRemote;
}
