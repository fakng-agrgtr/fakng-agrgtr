package com.fakng.fakngagrgtr.parser.impl.google;

import java.util.List;
import java.util.Map;

public class VacancyDTO {

    public String id;
    public String title;
    public List<String> categories;
    public String applyUrl;
    public String responsibilities;
    public String qualifications;
    public String companyId;
    public String companyName;
    public String language_code;
    public List<Map<String, Object>> locations;
    public String description;
    public List<String> educationLevels;
    public String created;
    public String modified;
    public String publishDate;
    public String applicationInstruction; // ?
    public String eeo;
    public Integer locationsCount;
    public String additionalInstructions;
    public String summary;
    public List<Map<String, Float>> buildingPins;
    public Boolean hasRemote;
}
