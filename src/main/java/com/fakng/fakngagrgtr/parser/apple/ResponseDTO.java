package com.fakng.fakngagrgtr.parser.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties({
        "cmsPacketData",
        "translations",
        "formats",
        "refData",
        "sortModifier",
        "page",
        "queryParams",
        "filters",
        "modifiedQuery",
        "totalRecords",
        "localeList",
        "countryCode",
        "fullUrl",
        "origin",
        "serverLocation",
        "metaData",
        "hasActiveSession",
        "isExternal",
        "isInternal",
        "isFileMaker",
        "searchPageUrl",
        "defaultSearchLink",
        "statusCode",
        "showLocalHeader"
})
public class ResponseDTO {
    private List<VacancyDTO> searchResults;
}
