package com.fakng.fakngagrgtr.parser;

import com.fakng.fakngagrgtr.entity.Vacancy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class Parser {

    protected String url;

    public List<Vacancy> parse() {
        try {
            return process();
        } catch (Exception e) {
            log.error("Error while parsing page under url: {}", url, e);
        }
        return null; //хз
    }

    protected abstract List<Vacancy> process() throws Exception;
}