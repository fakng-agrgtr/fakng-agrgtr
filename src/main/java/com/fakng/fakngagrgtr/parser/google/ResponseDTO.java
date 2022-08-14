package com.fakng.fakngagrgtr.parser.google;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseDTO {
    private Integer count;
    private Integer nextPage;
    private Integer pageSize;
    private List<VacancyDto> jobs;
}