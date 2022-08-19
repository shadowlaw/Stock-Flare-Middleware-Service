package com.shadow.jse_notification_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.shadow.jse_notification_service.util.FileSystemUtil.read;

@Configuration
public class OpenAPISwaggerConfig {

    @Value("${app.version.path}")
    private String appVersionPath;

    @Bean
    public OpenAPI springShopOpenAPI(@Value("${spring.application.name}") String applicationName,
                                     @Value("${spring.application.description}") String applicationDescription) {
        return new OpenAPI()
                .info(new Info().title(applicationName)
                        .description(applicationDescription)
                        .version(read(appVersionPath))
                );
    }
}