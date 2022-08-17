package com.fakng.fakngagrgtr.parser.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties({"localeInfo"})
public class VacancyDTO {

    private String id;

    private String jobSummary;

    private List<LocationDTO> locations;

    private String positionId;

    private String postingDate;

    private String postingTitle;

    private String postDateInGMT;

    private String transformedPostingTitle;

    private String reqId;

    private Boolean managedPipelineRole;

    private Integer standardWeeklyHours;

    private TeamDTO team;

    private Boolean homeOffice;

}
