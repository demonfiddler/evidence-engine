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

package io.github.demonfiddler.ee.server.repository;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.model.LinkEntitiesInput;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.ProfileUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

/**
 * Provides database access for cross-entity references that don't have a corresponding JPA entity class. Such links
 * are entity:entity references, implemented via association tables specific to the entity type pairs involved.
 */
@Repository
@Transactional
public class LinkRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkRepository.class);

    @Resource
    private EntityManager em;
    @Resource
    private EntityUtils entityUtils;
    @Resource
    private ProfileUtils profileUtils;
    /** Keeps track of which named queries have been registered in JPA. */
    private final Set<String> queryNames = new HashSet<>();

    /**
     * Creates an association between two entities. Note that the 'from' and 'to' entities must be chosen to reflect the
     * name of the association table for that entity pair. Association tables are named
     * {@literal <from-entity-name>_<to-entity-name>}.
     * @param linkInput Details of 'from' and 'to' entities to link.
     * @return The number of association records inserted.
     */
    public int linkEntities(LinkEntitiesInput linkInput) {
        String fromEntityName = entityUtils.getEntityName(linkInput.getFromEntityKind());
        String toEntityName = entityUtils.getEntityName(linkInput.getToEntityKind());
        String queryName = "link." + fromEntityName + "To" + StringUtils.firstToUpper(toEntityName);

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName)) {
                String ignore = profileUtils.isIntegrationTesting() ? "" : " IGNORE";
                String template = """
                                INSERT%s INTO \"%s_%s\"
                                    (\"%s_id\", \"%s_id\")
                                VALUES
                                    (:fromEntityId, :toEntityId);
                                """;
                String sql =
                    String.format(template, ignore, fromEntityName, toEntityName, fromEntityName, toEntityName);
                query = em.createNativeQuery(sql, Integer.class);
                em.getEntityManagerFactory().addNamedQuery(queryName, query);
                queryNames.add(queryName);
                LOGGER.debug("Defined query '{}' as:\n{}", queryName, sql);
            }
        }
        if (query == null)
            query = em.createNamedQuery(queryName, Integer.class);
        query.setParameter("fromEntityId", linkInput.getFromEntityId()) //
            .setParameter("toEntityId", linkInput.getToEntityId());
        try {
            return query.executeUpdate();
        } catch (PersistenceException e) {
            // This doesn't achieve the desired effect, as the exception has already resulted in the transaction being
            // marked rollback-only, which will ultimately result in a Spring UnexpectedRollbackException being thrown.
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException)
                return 0;
            throw e;
        }
    }

    /**
     * Removes an association between two entities. Note that the 'from' and 'to' entities must be chosen to reflect the
     * name of the association table for that entity pair. Association tables are named
     * {@literal <from-entity-name>_<to-entity-name>}.
     * @param linkInput Details of 'from' and 'to' entities to unlink.
     * @return The number of association records deleted.
     */
    public int unlinkEntities(LinkEntitiesInput linkInput) {
        String fromEntityName = entityUtils.getEntityName(linkInput.getFromEntityKind());
        String toEntityName = entityUtils.getEntityName(linkInput.getToEntityKind());
        String queryName = "unlink." + fromEntityName + "From" + StringUtils.firstToUpper(toEntityName);

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName)) {
                String template = """
                    DELETE FROM \"%s_%s\"
                    WHERE
                    \"%s_id\" = :fromEntityId
                    AND \"%s_id\" = :toEntityId;
                    """;
                String sql = String.format(template, fromEntityName, toEntityName, fromEntityName, toEntityName);
                query = em.createNativeQuery(sql, Integer.class);
                em.getEntityManagerFactory().addNamedQuery(queryName, query);
                queryNames.add(queryName);
                LOGGER.debug("Defined query '{}' as:\n{}", queryName, sql);
            }
        }
        if (query == null)
            query = em.createNamedQuery(queryName, Integer.class);
        query.setParameter("fromEntityId", linkInput.getFromEntityId()) //
            .setParameter("toEntityId", linkInput.getToEntityId());
        return query.executeUpdate();
    }

}
