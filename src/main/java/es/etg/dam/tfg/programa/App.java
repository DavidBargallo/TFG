package es.etg.dam.tfg.programa;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class App {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = new SpringApplicationBuilder(App.class).headless(false).run(args);
        Application.launch(JavaFXApp.class, args); 
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}