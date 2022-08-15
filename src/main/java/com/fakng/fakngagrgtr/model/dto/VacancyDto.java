package com.fakng.fakngagrgtr.model.dto;

import com.fakng.fakngagrgtr.model.dto.CompanyDto;
import com.fakng.fakngagrgtr.model.dto.LocationDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VacancyDto {
    private long id;
    private String title;
    private String description;
    private String url;
    private LocalDateTime addDate;
    private CompanyDto company;
    private LocationDto location;
}
