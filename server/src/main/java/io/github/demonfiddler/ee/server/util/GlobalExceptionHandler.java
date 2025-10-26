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

package io.github.demonfiddler.ee.server.util;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final ErrorClassification UNAUTHORIZED = ErrorClassification.errorClassification("UNAUTHORIZED");
    private static final ErrorClassification NOT_FOUND = ErrorClassification.errorClassification("NOT_FOUND");
    private static final ErrorClassification CONSTRAINT_VIOLATION =
        ErrorClassification.errorClassification("CONSTRAINT_VIOLATION");
    private static final ErrorClassification DUPLICATE_KEY = ErrorClassification.errorClassification("DUPLICATE_KEY");

    private static Throwable getRootCause(Throwable t) {
        Throwable rootCause = t;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
            rootCause = rootCause.getCause();
        return rootCause;
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(GraphqlErrorBuilder<?> builder, AccessDeniedException e) {
        return builder //
            .errorType(UNAUTHORIZED) //
            .message(
                "You are not authorised to perform the operation. Speak to the system administrator. Root cause: %s",
                getRootCause(e).getMessage()) //
            .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(GraphqlErrorBuilder<?> builder, PermissionDeniedDataAccessException e) {
        return builder //
            .errorType(UNAUTHORIZED) //
            .message(
                "You are not authorised to perform the operation. Speak to the system administrator. Root cause: %s",
                e.getRootCause().getMessage()) //
            .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(GraphqlErrorBuilder<?> builder, DataIntegrityViolationException e) {
        return builder //
            .errorType(CONSTRAINT_VIOLATION) //
            .message(
                "You tried to create or update a record in a way that violates a relational integrity constraint in the database. Root cause: %s",
                e.getRootCause().getMessage()) //
            .build();
    }

    // It looks as if the preceding handler is overriding this one.
    @GraphQlExceptionHandler
    public GraphQLError handle(GraphqlErrorBuilder<?> builder, DuplicateKeyException e) {
        return builder //
            .errorType(DUPLICATE_KEY) //
            .message(
                "You tried to create or update a record using a unique key value that already exists in the database. Root cause: %s",
                e.getRootCause().getMessage()) //
            .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(GraphqlErrorBuilder<?> builder, ObjectRetrievalFailureException e) {
        return builder //
            .errorType(NOT_FOUND) //
            .message(
                "You tried to create or update a %s record using a key value '%s' that does not exist in the database. Root cause: %s",
                e.getPersistentClass().getSimpleName(), e.getIdentifier(), e.getRootCause().getMessage()) //
            .build();
    }

}
