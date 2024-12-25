
/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024 Adrian Price. All rights reserved.
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

package io.github.demonfiddler.ee.server;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import io.github.demonfiddler.ee.server.util.ProfileUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    ProfileUtils profileUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // For now, only expose the actuator endpoints during integration testing.
        if (profileUtils.isIntegrationTesting()) {
            http.csrf(customizer -> customizer //
                .ignoringRequestMatchers("/actuator/**")) //
                .authorizeHttpRequests(customizer -> customizer //
                    .requestMatchers(GET, "/actuator/**").permitAll() //
                    .requestMatchers(POST, "/actuator/**").permitAll() //
                );
        }

        // Authorise application endpoints.
        http.csrf(customizer -> customizer //
            .ignoringRequestMatchers("/graphql")) //
            .authorizeHttpRequests(customizer -> {
                customizer //
                    .requestMatchers("/graphiql").authenticated() //
                    .requestMatchers(HttpMethod.POST, "/graphql").permitAll() //
                    .anyRequest().authenticated();
            }) //
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

}
