package com.fakng.fakngagrgtr.parser.spotify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseDTO {

    private List<CategoriesDTO> main_categories;
    private List<VacancyDTO> result;
}
