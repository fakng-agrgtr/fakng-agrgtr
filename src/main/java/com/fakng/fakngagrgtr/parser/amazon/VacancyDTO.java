package com.fakng.fakngagrgtr.parser.amazon;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VacancyDTO {

    private String basicQualifications;
    private String businessCategory;
    private String city;
    private String companyName;
    private String countryCode;
    private String description;
    private String departmentCostCenter; // null
    private String descriptionShort;
    private String displayDistance; // null
    private String id;
    private String idIcims;
    private String isIntern; // null
    private String isManager; // null
    private String jobCategory;
    private String jobFamily;
    private String jobFunctionId; // null
    private String jobPath;
    private String jobScheduleType;
    private String location;
    private String normalizedLocation;
    private List<String> optionalSearchLabels;
    private String postedDate;
    private String preferredQualifications;
    private String primarySearchLabel;
    private String sourceSystem;
    private String state;
    private String title;
    private String universityJob; // null
    private String updatedTime;
    private String urlNextStep;
    private Map<String, String> team;
}
