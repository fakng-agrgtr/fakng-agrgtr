package com.fakng.fakngagrgtr.parser.spotify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VacancyDTO {

    private String id;
    private String text;
    private Object main_category;
    private Object sub_category;
    private Object location;
    private Object job_type;
    private boolean is_remote;
    private boolean remote_name;
}
