package com.fakng.fakngagrgtr.parser.apple;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO {
    private List<VacancyDTO> searchResults;
}
