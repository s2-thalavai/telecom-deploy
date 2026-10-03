package com.telecom.plans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TelecomPlansApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelecomPlansApplication.class, args);
    }
}
