package com.stellarjets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StellarJetsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StellarJetsApplication.class, args);
    }
}
