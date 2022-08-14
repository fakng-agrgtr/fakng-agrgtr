package com.fakng.fakngagrgtr.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RequestDto {
    private List<Long> companyIds;
    private List<Long> locationIds;
    private Map<String, String> sortings;
    private int page;
    private int pageSize;
}
