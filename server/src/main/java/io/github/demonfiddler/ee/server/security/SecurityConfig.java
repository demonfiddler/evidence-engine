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

package io.github.demonfiddler.ee.server.security;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.github.demonfiddler.ee.server.repository.JdbcTokenRepositoryImplEx;
import io.github.demonfiddler.ee.server.repository.JdbcUserDetailsManagerEx;
import io.github.demonfiddler.ee.server.security.jwt.JwtAuthenticationFilter;
import io.github.demonfiddler.ee.server.security.jwt.JwtUtils;
import io.github.demonfiddler.ee.server.util.ProfileUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String REMEMBER_ME_KEY = "auth";

    @Autowired
    private ProfileUtils profileUtils;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private DataSource dataSource;
    private JdbcUserDetailsManager userDetailsManager;
    private ProviderManager authenticationManager;
    private DaoAuthenticationProvider daoAuthenticationProvider;
    private RememberMeAuthenticationProvider rememberMeAuthenticationProvider;
    private JdbcTokenRepositoryImplEx tokenRepository;
    private PersistentTokenBasedRememberMeServices rememberMeServices;
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private RememberMeAuthenticationFilter rememberMeFilter;

    private void init() {
        if (userDetailsManager == null) {
            userDetailsManager = new JdbcUserDetailsManagerEx(dataSource);
            daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsManager);
            rememberMeAuthenticationProvider = new RememberMeAuthenticationProvider(REMEMBER_ME_KEY);
            authenticationManager = new ProviderManager(rememberMeAuthenticationProvider, daoAuthenticationProvider);
            userDetailsManager.setAuthenticationManager(authenticationManager);
            tokenRepository = new JdbcTokenRepositoryImplEx();
            tokenRepository.setDataSource(dataSource);
            rememberMeServices =
                new PersistentTokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsManager, tokenRepository);
            rememberMeFilter = new RememberMeAuthenticationFilter(authenticationManager, rememberMeServices);
            usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();
            usernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
            usernamePasswordAuthenticationFilter.setRememberMeServices(rememberMeServices);
            jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        }
    }

    @Bean
    AuthenticationManager authenticationManager() {
        init();
        return authenticationManager;
    }

    @Bean
    UserDetailsManager userDetailsManager() {
        init();
        return userDetailsManager;
    }

    @Bean
    RememberMeServices rememberMeServices() {
        init();
        return rememberMeServices;
    }

    @Bean
    RememberMeAuthenticationFilter rememberMeFilter() {
        init();
        return rememberMeFilter;
    }

    @Bean
    UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
        init();
        return usernamePasswordAuthenticationFilter;
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        init();
        return jwtAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://editor.swagger.io/"));
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        config.setAllowCredentials(true); // optional, depending on your needs

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        init();

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
        http //
            .cors(Customizer.withDefaults()) // enables CORS support
            .csrf(csrf -> csrf.disable()) // disables CSRF (safe for stateless APIs)
            .authenticationManager(authenticationManager) //
            .authenticationProvider(daoAuthenticationProvider) //
            .addFilterBefore(usernamePasswordAuthenticationFilter, AnonymousAuthenticationFilter.class) //
            .addFilterBefore(rememberMeFilter, UsernamePasswordAuthenticationFilter.class) //
            .addFilterBefore(jwtAuthenticationFilter, RememberMeAuthenticationFilter.class) //
            .authorizeHttpRequests(customizer -> {
                customizer //
                    .requestMatchers("/graphiql").authenticated() //
                    .requestMatchers("/rest/**").permitAll() //.authenticated() //
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow preflight
                    .requestMatchers(HttpMethod.POST, "/graphql").permitAll().anyRequest().authenticated();
            }) //
            .rememberMe(customizer -> {
                customizer.rememberMeServices(rememberMeServices);
            }) //
            .formLogin(Customizer.withDefaults()); //

        return http.build();
    }

}
