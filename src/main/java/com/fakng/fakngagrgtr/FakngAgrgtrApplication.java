package com.fakng.fakngagrgtr;

import com.fakng.fakngagrgtr.parser.uber.UberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class FakngAgrgtrApplication implements CommandLineRunner {

    private final UberParser uberParser;

    public static void main(String[] args) {
        SpringApplication.run(FakngAgrgtrApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        uberParser.parse();
    }
}
