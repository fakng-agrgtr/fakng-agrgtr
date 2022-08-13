package com.fakng.fakngagrgtr;

import com.fakng.fakngagrgtr.parser.impl.GoogleParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class FakngAgrgtrApplication implements CommandLineRunner {

    private final GoogleParser googleParser;

    public static void main(String[] args) {
        SpringApplication.run(FakngAgrgtrApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        googleParser.parse();
    }
}
