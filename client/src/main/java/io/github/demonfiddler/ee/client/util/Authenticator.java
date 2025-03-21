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

package io.github.demonfiddler.ee.client.util;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XHTML_XML;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.Forbidden;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import io.github.demonfiddler.ee.client.User;

/**
 * Provides authentication services.
 */
@Service
@Scope(SCOPE_SINGLETON)
public class Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Authenticator.class);

    private static final String REQUEST_SPEC = """
        {
            id
            username
            firstName
            lastName
        }
        """;
    private static final String LOGIN_BODY = "username=%s&password=%s&remember-me=on&_csrf=%s";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String REMEMBER_ME = "remember-me";

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
    private String jSessionId;
    private String rememberMe;
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
     * Signs into the server using the credentials supplied by Spring configuration properties. Remembers the
     * authentication token so that subsequent GraphQL calls will be authenticated.
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
        URI baseUri = getUri(null, null, null);
        URI loginUri = getUri("/login", null, null);

        // First {@code GET} the login form in order to extract the _csrf token.
        ResponseEntity<String> loginResponse = webClient //
            .get() //
            .uri(loginUri) //
            .accept(TEXT_HTML, APPLICATION_XHTML_XML, APPLICATION_XML) //
            .header("Origin", baseUri.toASCIIString()) //
            .retrieve() //
            .toEntity(String.class) //
            .block();
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Retrieved login page from {}", loginUri.toASCIIString());
        else
            LOGGER.debug("Retrieved login page");

        extractCookies(loginResponse);
        String body = loginResponse.getBody();
        Document loginDoc = Jsoup.parse(body);
        String _csrf = loginDoc.selectXpath("/html/body/div/form/input[@name='_csrf']").attr("value");
        LOGGER.trace("Found cookie JSESSIONID={}; _csrf={}", jSessionId, _csrf);

        // Now we can POST the authentication request.
        String authBody = String.format(LOGIN_BODY, username, password, _csrf);
        ResponseEntity<Void> authResponse = webClient //
            .post() //
            .uri(loginUri) //
            .accept(TEXT_HTML, APPLICATION_XHTML_XML, APPLICATION_XML) //
            .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE) //
            .header(CONTENT_LENGTH, String.valueOf(authBody.length())) //
            .header("Origin", baseUri.toASCIIString()) //
            .header("Cookie", JSESSIONID + '=' + jSessionId) //
            .bodyValue(authBody) //
            .retrieve() //
            .toBodilessEntity() //
            .block();
        extractCookies(authResponse);
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Received authentication cookies JSESSIONID={}, remember-me={}", jSessionId, rememberMe);
        else
            LOGGER.debug("Received authentication response");

        if (isAuthenticated()) {
            webClient = webClient.mutate() //
                .defaultCookie(JSESSIONID, jSessionId) //
                .defaultCookie(REMEMBER_ME, rememberMe) //
                .build();
            updateExecutors();
        }

        try {
            user = queryExecutor.userByUsername(REQUEST_SPEC, username);
        } catch (GraphQLRequestPreparationException | GraphQLRequestExecutionException e) {
            jSessionId = rememberMe = null;
            throw new IllegalArgumentException("Failed to retrieve user: " + username, e);
        }

        return isAuthenticated();
    }

    /**
     * Signs out of the server and forgets authentication data.
     */
    public void logout() {
        if (!isAuthenticated()) {
            LOGGER.debug("Cannot log out, as not currently logged in");
            return;
        }

        URI logoutUri = getUri("/logout", null, null);
        try {
            webClient //
                .post() //
                .uri(logoutUri) //
                .accept(TEXT_HTML, APPLICATION_XHTML_XML, APPLICATION_XML) //
                .retrieve() //
                .toBodilessEntity() //
                .block();
        } catch (Forbidden e) {
            // Expected on logout.
        }
        webClient = webClient.mutate() //
            .defaultCookies(m -> {
                m.remove(JSESSIONID);
                m.remove(REMEMBER_ME);
            }) //
            .build();
        updateExecutors();
        url = username = jSessionId = rememberMe = null;
        user = null;

        LOGGER.debug("Logged out");
    }

    /**
     * Indicates whether the user is signed in.
     * @return {@code true} if authenticated, otherwise {@code false}.
     */
    public boolean isAuthenticated() {
        return jSessionId != null && rememberMe != null;
    }

    private URI getUri(String path, String query, String fragment) {
        URI uri = URI.create(url);
        String scheme = uri.getScheme();
        String userInfo = uri.getUserInfo();
        String host = uri.getHost();
        int port = uri.getPort();
        try {
            return new URI(scheme, userInfo, host, port, path, query, fragment);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to construct URI", e);
        }
    }

    private void extractCookies(ResponseEntity<?> response) {
        jSessionId = rememberMe = null;
        List<String> rawCookies = response.getHeaders().get(SET_COOKIE);
        if (rawCookies != null) {
            for (String rawCookie : rawCookies) {
                int equals = rawCookie.indexOf("=");
                int semi = rawCookie.indexOf(";", equals);
                String name = rawCookie.substring(0, equals);
                String value = rawCookie.substring(equals + 1, semi);
                switch (name) {
                    case JSESSIONID:
                        jSessionId = value;
                        break;
                    case REMEMBER_ME:
                        rememberMe = value;
                        break;
                }
            }
        }
    }

    private void updateExecutors() {
        HttpGraphQlClient httpGraphQlClient = HttpGraphQlClient.builder(webClient).build();
        queryExecutor.graphQlClient = //
            queryReactiveExecutor.graphQlClient = //
                mutationExecutor.graphQlClient = //
                    mutationReactiveExecutor.graphQlClient = httpGraphQlClient;
    }

}
