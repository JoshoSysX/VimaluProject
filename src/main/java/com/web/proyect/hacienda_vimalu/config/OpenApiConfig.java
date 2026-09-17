package com.web.proyect.hacienda_vimalu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Api Ecommerce Hacienda Vimalu")
                        .version("1.0")
                        .description("Documentación de servicios para el proyecto de Hacienda Vimalu")
                );
    }
}
