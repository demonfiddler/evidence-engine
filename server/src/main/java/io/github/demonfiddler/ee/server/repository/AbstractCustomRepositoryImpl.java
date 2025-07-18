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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.ProfileUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * An abstract base implementation for a custom repository that uses native queries.
 */
abstract class AbstractCustomRepositoryImpl {

    /** Platform-dependent line separator. */
    static final String NL = System.lineSeparator();

    @PersistenceContext
    EntityManager em;
    @Resource
    EntityUtils entityUtils;
    @Resource
    ProfileUtils profileUtils;
    /**
     * Keeps track of which named queries have been registered in JPA. N.B. Requires external synchronisation: access is
     * typically synchronized in the findByXxx() methods of subclasses.
     */
    final Set<String> queryNames = new HashSet<>();

    AbstractCustomRepositoryImpl() {
    }

    abstract Logger getLogger();

    /**
     * Appends a string to the specified {@code StringBuilder}s.
     * @param s The string to append.
     * @param buffers The {@code StringBuilder}s to which {@code s} should be appended.
     */
    final void append(String s, StringBuilder... buffers) {
        for (StringBuilder buffer : buffers)
            buffer.append(s);
    }

    /**
     * Defines a JPA named native query. N.B. This method requires external synchronisation.
     * @param queryName The query name.
     * @param sql The native SQL.
     * @param resultClass The class that the query returns.
     * @return The prepared query.
     */
    Query defineNamedQuery(String queryName, String sql, Class<?> resultClass) {
        Query query = em.createNativeQuery(sql, resultClass);
        em.getEntityManagerFactory().addNamedQuery(queryName, query);
        queryNames.add(queryName);

        if (getLogger().isTraceEnabled())
            getLogger().trace("Defined query '{}' as:\n{}", queryName, sql);
        else
            getLogger().debug("Defined query '{}'", queryName);

        return query;
    }

}
