package com.fakng.fakngagrgtr.parser.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties({"localeInfo"})
public class VacancyApple {

    private String id;

    private String jobSummary;

    private List<LocationApple> locations;

    private String positionId;

    private String postingDate;

    private String postingTitle;

    private String postDateInGMT;

    private String transformedPostingTitle;

    private String reqId;

    private Boolean managedPipelineRole;

    private Integer standardWeeklyHours;

    private TeamApple team;

    private Boolean homeOffice;

    public LocalDateTime getPostDateTime(){
        return LocalDateTime.parse(postDateInGMT.replace("Z", ""));
    }
}
