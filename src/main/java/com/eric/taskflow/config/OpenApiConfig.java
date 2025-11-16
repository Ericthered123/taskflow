package com.eric.taskflow.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Genera documentación Swagger/OpenAPI automáticamente y permite ver y probar los endpoints en /swagger-ui/index.html.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskflowApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow API")
                        .description("API REST para gestión de tareas y usuarios con JWT")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Eric")
                                .email("eric6doyle@gmail.com")
                                .url("https://github.com/Ericthered123"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // habilita seguridad global
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación extendida")
                        .url("https://springdoc.org/"));
    }
}
