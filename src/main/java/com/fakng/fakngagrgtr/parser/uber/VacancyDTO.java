package com.fakng.fakngagrgtr.parser.uber;

import lombok.Data;

import java.util.List;

@Data
class VacancyDTO {
    private String id;
    private String title;
    private String description;
    private String updatedDate;
    private List<LocationDTO> allLocations;
}
