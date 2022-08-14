package com.fakng.fakngagrgtr.vacancy;

import com.fakng.fakngagrgtr.company.CompanyDto;
import com.fakng.fakngagrgtr.location.LocationDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VacancyDto {
    private long id;
    private String title;
    private String description;
    private String url;
    private LocalDateTime addDate;
    private CompanyDto company;
    private List<LocationDto> location;
}
