package com.fakng.fakngagrgtr.parser.impl.google.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO {

    private Integer count;
    private Integer nextPage;
    private Integer pageSize;
    private List<VacancyDTO> jobs;
}