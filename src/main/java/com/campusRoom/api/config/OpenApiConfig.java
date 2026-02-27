package com.campusRoom.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI CampusRoomOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusRoom API")
                        .description("Documentation interactive de l’API Campus Room. "
                                + "Utilisez cette interface pour tester et explorer les endpoints REST.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support de l'API Campus Room.")
                                .url("mailto:noreplydevback@gmail.com"))

                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
