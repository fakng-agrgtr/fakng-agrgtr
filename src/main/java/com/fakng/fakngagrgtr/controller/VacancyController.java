package com.fakng.fakngagrgtr.controller;

import com.fakng.fakngagrgtr.model.dto.VacancyDto;
import com.fakng.fakngagrgtr.service.impl.VacancyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyServiceImpl vacancyServiceImpl;
    private final ObjectMapper objectMapper;

    @GetMapping("/vacancy")
    public Page<VacancyDto> getVacancies(@RequestParam List<Long> companyIds, @RequestParam List<Long> locationIds,
                                         @RequestParam int page, @RequestParam int pageSize) {
        return vacancyServiceImpl.findAll(companyIds, locationIds, page, pageSize)
                .map(vacancy -> objectMapper.convertValue(vacancy, VacancyDto.class));
    }
}
