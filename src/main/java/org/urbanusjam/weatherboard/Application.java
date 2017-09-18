package org.urbanusjam.weatherboard;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableWebSecurity
@EnableScheduling
@ComponentScan({"org.urbanusjam.weatherboard"})
public class Application {

    public static void main(String[] args) {
        run(Application.class, args);
    }
}