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

package io.github.demonfiddler.ee.server.security.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.UserRepository;

@Component
public class JwtUtils {

    @Value("${jwt.server-url}")
    private String serverUrl;
    @Value("${jwt.header}")
    private String headerString;
    @Value("${jwt.prefix}")
    private String tokenPrefix;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.duration-ms}")
    private long expirationTime;
    @Value("${jwt.authority-claim}")
    private String authorityClaim;
    @Autowired
    private UserRepository userRepository;

    public String getHeaderString() {
        return headerString;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getSecret() {
        return secret;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getAuthorityClaim() {
        return authorityClaim;
    }

    /**
     * Generates a JWT authentication token for a specified user.
     * @param user The user for which the token is to be generated. The user object must contain the aggregate of all
     * granted authorities.
     * @return An HMAC256-signed JSON Web Token.
     */
    public String generateToken(User user) {
        Instant now = Instant.now();
        List<String> authorities =
            userRepository.findAllUserAuthorities(user.getId()).stream().map(authority -> authority.name()).toList();
        return JWT.create() //
            .withAudience(serverUrl) //
            .withIssuer(serverUrl) //
            .withIssuedAt(Date.from(now)) //
            .withExpiresAt(Date.from(now.plusMillis(getExpirationTime()))) //
            .withClaim(authorityClaim, authorities) //
            .withSubject(user.getUsername()) //
            .sign(Algorithm.HMAC256(secret));
    }

    /**
     * Validates a JWT token.
     * @param token The JWT token.
     * @return The decoded token.
     */
    public DecodedJWT validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret)) //
            .withIssuer(serverUrl) //
            .withAudience(serverUrl) //
            .build() //
            .verify(token);
    }

}
