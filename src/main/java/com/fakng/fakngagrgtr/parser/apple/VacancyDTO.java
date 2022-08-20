package com.fakng.fakngagrgtr.parser.apple;

import lombok.Data;

import java.util.List;

@Data
public class VacancyDTO {

    private String jobSummary;

    private List<LocationDTO> locations;

    private String positionId;

    private String postingTitle;

    private String postDateInGMT;

    private TeamDTO team;
}
