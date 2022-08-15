package com.fakng.fakngagrgtr.parser.amazon;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseDTO {

    private String error;
    private int hits;
    private Map<String, String> facets;
    private Object content;
    private List<VacancyDTO> jobs;
}
