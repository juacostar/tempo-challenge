package com.tenpo.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Users Service API")
                        .version("1.0")
                        .description("Documentación de API para tempo-challenge")
                        .contact(new Contact()
                                .name("Tu Nombre")
                                .email("tu@email.com")
                        ));
    }
}
