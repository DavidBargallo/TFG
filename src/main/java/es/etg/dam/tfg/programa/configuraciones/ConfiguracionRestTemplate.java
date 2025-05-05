package es.etg.dam.tfg.programa.configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfiguracionRestTemplate {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

