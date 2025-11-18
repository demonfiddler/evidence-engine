/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

package io.github.demonfiddler.ee.server.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

        @Bean(name = "io.github.demonfiddler.ee.server.rest.config.SpringDocConfiguration.apiInfo")
        OpenAPI apiInfo() {
                return new OpenAPI()
                        .info(new Info().title("Evidence Engine REST Interface - OpenAPI 3.1.1").description(
                                "This is a REST API for exporting lists of scientists, publications, etc., based on the OpenAPI 3.1.1 specification. Some useful links: - [The Evidence Engine page](https://campaign-resources.org/evidence-engine.html)")
                                .termsOfService("https://campaign-resources.org/evidence-engine.html")
                                .contact(new Contact().email("admin@campaign-resources.org"))
                                .license(new License().name("GNU Affero General Public License v3.0")
                                        .url("https://www.gnu.org/licenses/agpl-3.0.en.html"))
                                .version("0.0.1"))
                        .components(new Components().addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
        }

}
