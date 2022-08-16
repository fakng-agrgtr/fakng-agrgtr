package com.fakng.fakngagrgtr.parser.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties({"localeInfo"})
public class VacancyApple {

    @JsonProperty("id")
    private String id;

    @JsonProperty("jobSummary")
    private String jobSummary;

    @JsonProperty("locations")
    private List<LocationApple> locations;

    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("postingDate")
    private String postingDate;

    @JsonProperty("postingTitle")
    private String postingTitle;

    @JsonProperty("postDateInGMT")
    private String postDateInGMT;

    @JsonProperty("transformedPostingTitle")
    private String transformedPostingTitle;

    @JsonProperty("reqId")
    private String reqId;

    @JsonProperty("managedPipelineRole")
    private Boolean managedPipelineRole;

    @JsonProperty("standardWeeklyHours")
    private Integer standardWeeklyHours;

    @JsonProperty("team")
    private TeamApple team;

    @JsonProperty("homeOffice")
    private Boolean homeOffice;
}
