package com.fakng.fakngagrgtr.vacancy;

import com.fakng.fakngagrgtr.company.CompanyDTO;
import com.fakng.fakngagrgtr.location.LocationDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VacancyDTO {
    private long id;
    private String title;
    private String description;
    private String url;
    private LocalDateTime addDate;
    private CompanyDTO company;
    private List<LocationDTO> location;
}
