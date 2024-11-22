package org.example.schedulerspringtgbot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SchedulerSpringTgBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerSpringTgBotApplication.class, args);
    }

}
