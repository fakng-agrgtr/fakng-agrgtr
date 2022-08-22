package com.fakng.fakngagrgtr.parser.amazon;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class ResponseDTO {
    private int hits;
    private List<VacancyDTO> jobs;
}
