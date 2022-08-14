package com.fakng.fakngagrgtr.parser.google;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationDTO {
    private String display;
    private double lat;
    private double lon;
    private List<String> addressLines;
    private String city;
    private String postCode;
    private String country;
    private String countryCode;
    private boolean isRemote;
}
