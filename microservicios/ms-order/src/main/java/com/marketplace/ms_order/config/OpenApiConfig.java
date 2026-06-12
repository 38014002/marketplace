package com.marketplace.ms_order.config; // Cambia el paquete según el microservicio

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Microservicio de Órdenes (ms-order)") // Customizar por servicio
                        .version("1.0.0")
                        .description("Documentación de los endpoints de ms-order.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("marketplace")
                .pathsToMatch("/api/**") // Captura todos tus controladores
                .build();
    }
}