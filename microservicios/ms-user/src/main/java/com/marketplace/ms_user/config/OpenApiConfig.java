package com.marketplace.ms_user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Microservicio de Usuarios (ms-user)")
                        .version("1.0.0")
                        .description(
                                "Documentación interactiva de los endpoints de gestión de usuarios, autenticación local y sincronización interna del Marketplace."))
                // Agrega el requisito de seguridad global para que aparezca el candado en los
                // endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Configura el botón Authorize para que use el formato 'Bearer <JWT>' requerido
                // por la guía
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Ingresa directamente tu accessToken (sin escribir la palabra Bearer delante).")));
    }
}