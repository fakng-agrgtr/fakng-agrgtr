package com.fakng.fakngagrgtr.dto;

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
