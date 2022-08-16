package com.fakng.fakngagrgtr.parser.apple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeamApple {
    @JsonProperty("teamName")
    public String teamName;

    @JsonProperty("teamID")
    public String teamID;

    @JsonProperty("teamCode")
    public String teamCode;
}
