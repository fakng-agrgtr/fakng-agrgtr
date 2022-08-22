package com.fakng.fakngagrgtr.parser.apple;

import lombok.Data;

import java.util.List;

@Data
class ResponseDTO {
    private List<VacancyDTO> searchResults;
}
