package org.example.sampleproblemspringweb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sample Problem Spring Web API")
                .version("1.0.0")
                .description("A sample REST API demonstrating problem-spring-web for standardized error handling")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@example.com")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .packagesToScan("org.example.sampleproblemspringweb.controller")
            .build();
    }
}

