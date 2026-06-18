package com.dexcode.taskmasterai;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TaskmasteraiApplication {

    public static void main(String[] args) {

        //** Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        try {
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            log.error("Error loading .env file: {}", e.getMessage());
        }

        SpringApplication.run(TaskmasteraiApplication.class, args);
    }

}
