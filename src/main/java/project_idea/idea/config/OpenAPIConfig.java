package project_idea.idea.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class OpenAPIConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Idea API")
                        .description("REST API for Project Idea Platform")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Project Idea Team")
                                .email("support@mail.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    private Object createSwaggerUIConfig() {
        return new SwaggerUIConfig();
    }

    private static class SwaggerUIConfig {
        public final String persistAuthorization = "true";
        public final String tryItOutEnabled = "true";
        public final String filter = "true";
        public final String syntaxHighlight = "true";
    }
}
