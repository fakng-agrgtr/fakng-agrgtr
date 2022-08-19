package com.fakng.fakngagrgtr.parser.uber;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
class RequestDTO {
    private Map<String, String> params = new HashMap<>();

    void addParam(String key, String val) {
        params.put(key, val);
    }
}
