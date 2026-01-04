/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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

package io.github.demonfiddler.ee.client.util;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import io.github.demonfiddler.ee.client.AuthPayload;
import io.github.demonfiddler.ee.client.User;

/**
 * Provides authentication services.
 */
@Service
@Scope(SCOPE_SINGLETON)
public class Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Authenticator.class);

    private static final String JWT_RESPONSE_SPEC = """
        {
            token
            user {
                id
                username
                firstName
                lastName
                authorities(aggregation:ALL, format:SHORT)
            }
        }
        """;

    @Autowired
    @Qualifier("webClient")
    private WebClient webClient;
    @Value(value = "${graphql.endpoint.url}")
    private String _url;
    @Value("${spring.security.user.name}")
    private String _username;
    @Value("${spring.security.user.password}")
    private String _password;
    @Autowired
    private QueryExecutor queryExecutor;
    @Autowired
    private QueryReactiveExecutor queryReactiveExecutor;
    @Autowired
    private MutationExecutor mutationExecutor;
    @Autowired
    private MutationReactiveExecutor mutationReactiveExecutor;

    private String url;
    private String username;
    private String jwtToken;
    private User user;

    /** Returns the logged-in user. */
    public User getUser() {
        return user;
    }

    /**
     * <p>
     * Signs into the server using the credentials supplied by Spring configuration properties. Remembers the
     * authentication token so that subsequent GraphQL calls will be authenticated.
     * </p>
     * Configuration properties are:
     * <ul>
     * <li>endpoint: {@code graphql.endpoint.url}</li>
     * <li>username: {@code spring.security.user.name}</li>
     * <li>password: {@code spring.security.user.password}</li>
     * </ul>
     * @return {@code true} on login success.
     */
    public boolean login() {
        return login(_url, _username, _password);
    }

    /**
     * <p>
     * Signs into the server using the specified credentials. Remembers the authentication token so
     * that subsequent GraphQL calls will be authenticated.
     * </p>
     * @param url The GraphQL endpoint URL. Can be {@code null}.
     * @param username The username.
     * @param password The password.
     * @return {@code true} on login success.
     */
    public boolean login(String url, String username, String password) {
        Objects.requireNonNull(username, "username cannot be null");
        Objects.requireNonNull(password, "password cannot be null");

        if (url == null)
            url = _url;
        if (isAuthenticated()) {
            if (!url.equals(this.url)) {
                throw new IllegalStateException(
                    "Cannot login to '" + url + "'', as already logged into '" + this.url + '\'');
            }
            if (!username.equals(this.username)) {
                throw new IllegalStateException(
                    "Cannot login as '" + username + "'', as already logged in as '" + this.username + '\'');
            }
            LOGGER.debug("Already logged in as '{}'", username);
            return true;
        }

        this.url = url;
        this.username = username;

        try {
            AuthPayload authPayload = mutationExecutor.login(JWT_RESPONSE_SPEC, username, password);
            jwtToken = authPayload.getToken();
            user = authPayload.getUser();
        } catch (GraphQLRequestPreparationException e) {
            throw new IllegalStateException(e);
        } catch (GraphQLRequestExecutionException e) {
            throw new IllegalArgumentException("Failed to authenticate user: " + username, e);
        }

        if (isAuthenticated()) {
            webClient = webClient.mutate() //
                .defaultHeader(AUTHORIZATION, "Bearer " + jwtToken) //
                .build();
            updateExecutors();
        }

        return isAuthenticated();
    }

    /**
     * Forgets authentication data.
     */
    public void logout() {
        if (!isAuthenticated()) {
            LOGGER.debug("Cannot log out, as not currently logged in");
            return;
        }

        webClient = webClient.mutate().defaultHeaders(m -> m.remove(AUTHORIZATION)).build();
        updateExecutors();
        url = username = jwtToken = null;
        user = null;

        LOGGER.debug("Logged out");
    }

    /**
     * Indicates whether the user is signed in.
     * @return {@code true} if authenticated, otherwise {@code false}.
     */
    public boolean isAuthenticated() {
        return jwtToken != null;
    }

    private void updateExecutors() {
        HttpGraphQlClient httpGraphQlClient = HttpGraphQlClient.builder(webClient).build();
        queryExecutor.graphQlClient = //
            queryReactiveExecutor.graphQlClient = //
                mutationExecutor.graphQlClient = //
                    mutationReactiveExecutor.graphQlClient = httpGraphQlClient;
    }

}
