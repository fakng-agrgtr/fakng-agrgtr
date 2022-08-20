package com.fakng.fakngagrgtr.parser;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Initializer {

    private List<Parser> parsers;

    public void initialize() {
        parsers.forEach(Parser::initialize);
        while (!allParsersFinished()) {
            for (Parser parser : parsers) {
                if (isParserReady(parser)) {
                    parser.requestNewVacanciesDetails();
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
