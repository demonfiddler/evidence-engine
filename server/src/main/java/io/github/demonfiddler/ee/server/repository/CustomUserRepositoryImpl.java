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

package io.github.demonfiddler.ee.server.repository;

import java.util.List;
import io.github.demonfiddler.ee.server.model.AuthorityKind;
import io.github.demonfiddler.ee.server.model.User;
import jakarta.persistence.Query;

public class CustomUserRepositoryImpl extends CustomTrackedEntityRepositoryImpl<User> implements CustomUserRepository {

    private static final String QUERY_SELECT_ALL_AUTHORITIES = "user.selectAllAuthorities";
    private static final String SQL_SELECT_ALL_AUTHORITIES = """
        SELECT "authority"
        FROM "user_authority" ua
        WHERE ua."user_id" = :userId
        UNION
        SELECT "authority"
        FROM "group_authority" ga
        JOIN "group_user" gu
        ON gu."group_id" = ga."group_id"
        JOIN "user" u
        ON u."username" = gu."username" AND u."id" = :userId 
    """;

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected String getFulltextColumns() {
        return "\"username\", \"first_name\", \"last_name\", \"email\", \"notes\"";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AuthorityKind> findAllUserAuthorities(Long userId) {
        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(QUERY_SELECT_ALL_AUTHORITIES))
                query = defineNamedQuery(QUERY_SELECT_ALL_AUTHORITIES, SQL_SELECT_ALL_AUTHORITIES, AuthorityKind.class);
        }
        if (query == null)
            query = em.createNamedQuery(QUERY_SELECT_ALL_AUTHORITIES, AuthorityKind.class);
        query.setParameter("userId", userId);
        return (List<AuthorityKind>)query.getResultList();
    }

}
