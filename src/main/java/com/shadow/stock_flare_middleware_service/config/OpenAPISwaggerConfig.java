package com.shadow.stock_flare_middleware_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import static com.shadow.stock_flare_middleware_service.util.FileSystemUtil.read;

@Configuration
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class OpenAPISwaggerConfig {

    @Value("${app.version.path}")
    private String appVersionPath;

    @Bean
    public OpenAPI springShopOpenAPI(@Value("${spring.application.name}") String applicationName,
                                     @Value("${spring.application.description}") String applicationDescription) {
        final String securitySchemeName = "Authorization";
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("basic")
                                )
                )
                .security(Collections.singletonList(new SecurityRequirement().addList(securitySchemeName)))
                .info(new Info().title(applicationName)
                        .description(applicationDescription)
                        .version(read(appVersionPath))
                );
    }
}