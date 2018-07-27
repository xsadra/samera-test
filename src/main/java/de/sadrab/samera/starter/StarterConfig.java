package de.sadrab.samera.starter;

import de.sadrab.samera.controller.MainController;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StarterConfig {
    @Bean
    public ApplicationRunner start(MainController mainController) {
        return args -> {
            mainController.run();
        };
    }
}
