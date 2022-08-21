package com.fakng.fakngagrgtr.parser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Initializer {

    private List<Parser> parsers;

    public void initialize() {
        parsers.forEach(Parser::initialize);
        Map<Parser, LocalDateTime> delays = new HashMap<>();
        while (!allParsersFinished()) {
            for (Parser parser : parsers) {
                if (isParserReady(parser)) {
                    parser.requestNewVacanciesDetails(true);
                }
            }
        }
    }

    private boolean allParsersFinished() {
        for (Parser parser : parsers) {
            if (parser.getRequestingNewVacanciesDelay() != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isParserReady(Parser parser) {
        return parser.getRequestingNewVacanciesDelay()
                .isBefore(LocalDateTime.now());
    }
}
