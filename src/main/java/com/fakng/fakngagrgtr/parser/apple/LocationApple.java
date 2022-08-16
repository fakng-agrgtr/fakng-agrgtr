package com.fakng.fakngagrgtr.parser.apple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationApple {
    @JsonProperty("city")
    public String city;

    @JsonProperty("level")
    public Integer level;

    @JsonProperty("metro")
    public String metro;

    @JsonProperty("name")
    public String name;

    @JsonProperty("stateProvince")
    public String stateProvince;

    @JsonProperty("countryName")
    public String countryName;

    @JsonProperty("region")
    public String region;

    @JsonProperty("countryID")
    public String countryID;

}
