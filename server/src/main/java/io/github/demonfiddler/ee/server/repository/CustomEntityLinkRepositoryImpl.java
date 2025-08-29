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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.demonfiddler.ee.server.model.EntityLink;
import io.github.demonfiddler.ee.server.model.EntityLinkQueryFilter;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

/**
 * Provides database access for cross-entity references. Such links are entity:entity references, implemented via the
 * "entity_link" association table.
 */
@Repository
@Transactional
public class CustomEntityLinkRepositoryImpl extends AbstractCustomRepositoryImpl implements CustomEntityLinkRepository {

    /** Describes the elements of a findByFilter() query. */
    private static record QueryMetaData(EntityLinkQueryFilter filter, Pageable pageable, String countQueryName,
        String selectQueryName, boolean hasFromEntityKind, boolean hasFromEntityId, boolean hasToEntityKind,
        boolean hasToEntityId, boolean hasStatus, boolean hasText, boolean hasTextH2, boolean hasTextMariaDB,
        boolean isAdvanced, boolean isPaged, boolean isSorted) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomEntityLinkRepositoryImpl.class);

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    @SuppressWarnings("null")
    private QueryMetaData getQueryMetaData(@NonNull EntityLinkQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasFilter = filter != null;
        boolean hasFromEntityKind = hasFilter && filter.getFromEntityKind() != null;
        boolean hasFromEntityId = hasFilter && filter.getFromEntityId() != null;
        boolean hasToEntityKind = hasFilter && filter.getToEntityKind() != null;
        boolean hasToEntityId = hasFilter && filter.getToEntityId() != null;
        boolean hasStatus = hasFilter && filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean hasText = hasFilter && filter.getText() != null;
        boolean hasTextH2 = hasText && profileUtils.isIntegrationTesting();
        boolean hasTextMariaDB = hasText && !profileUtils.isIntegrationTesting();
        boolean isAdvanced = hasText && filter.getAdvancedSearch();
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();

        // For paged queries involving an H2 full text filter, we need to ensure that the sort order includes "id"
        // because otherwise the join with FT_SEARCH_DATA can result in records duplicated across successive pages.
        if (/*hasTextH2 && */isPaged && pageable.getSort().filter(o -> o.getProperty().equals("id")).isEmpty()) {
            Sort sort = pageable.getSort().and(Sort.by("id"));
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            isSorted = true;
        }

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        StringBuilder[] queryNames = { countQueryName, selectQueryName };
        append("entityLink.", queryNames);
        countQueryName.append("countBy");
        selectQueryName.append("findBy");
        if (hasFromEntityKind || hasFromEntityId) {
            append("FromEntity", queryNames);
            if (hasFromEntityKind)
                append("Kind", queryNames);
            if (hasFromEntityId)
                append("Id", queryNames);
        }
        if (hasToEntityKind || hasToEntityId) {
            append("ToEntity", queryNames);
            if (hasToEntityKind)
                append("Kind", queryNames);
            if (hasToEntityId)
                append("Id", queryNames);
        }
        if (hasStatus)
            append("Status", queryNames);
        if (hasText) {
            append("Text", queryNames);
            if (isAdvanced)
                append("Advanced", queryNames);
        }
        if (isSorted)
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);

        return new QueryMetaData(filter, pageable, countQueryName.toString(), selectQueryName.toString(),
            hasFromEntityKind, hasFromEntityId, hasToEntityKind, hasToEntityId, hasStatus, hasText, hasTextH2,
            hasTextMariaDB, isAdvanced, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(QueryMetaData m) {
        /*
        A new and secondary use for this query is to fetch all records, of either a specific kind or all kinds, which
        are linked from or to a specific record. This is used by the filtered ILinkableEntity.fromEntityLinks and
        ILinkableEntity.toEntityLinks fields. In these cases, the LinkableEntityQueryFilter passed would specify
        fromEntityId or toEntityId respectively. For homogeneous results, the filter would specify toEntityKind or
        fromEntityKind respectively.
        
        SELECT e."dtype", e."status", e."created", e."created_by_user_id", e."updated", e."updated_by_user_id", el.*
        FROM "entity_link" el
        JOIN "entity" e
        ON e."id" = el."id"
        -- if (m.hasFromEntityKind) {
        JOIN "entity" fe
        ON fe."id" = el."from_entity_id"
        -- }
        -- if (m.hasToEntityKind) {
        JOIN "entity" te
        ON te."id" = el."to_entity_id"
        -- }
        -- if (m.hasTextH2) {
        JOIN
            FT_SEARCH_DATA(:text, 0, 0) ft
        ON
            ft."TABLE" = 'entity_link'
            AND ft."KEYS"[1] = el."id"
        -- }
        WHERE
            -- if (m.hasFromEntityKind) {
            fe."dtype" = :fromEntityKind
            -- }
            -- if (m.hasFromEntityId) {
            AND el."from_entity_id" = :fromEntityId
            --}
            -- if (m.hasToEntityKind) {
            AND te."dtype" = :toEntityKind
            -- }
            -- if (m.hasToEntityId) {
            AND el."to_entity_id" = :toEntityId
            -- }
            -- if (m.hasStatus) {
            AND se."status" IN (:status)
            -- }
            -- if (m.hasTextMariaDB) {
            AND MATCH("from_entity_locations", "to_entity_locations" IN BOOLEAN MODE) AGAINST (:text);
            -- }
         */

        StringBuilder feJoinClause = new StringBuilder();
        StringBuilder teJoinClause = new StringBuilder();
        StringBuilder seJoinClause = new StringBuilder();
        StringBuilder ftJoinClause = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder orderByClause = new StringBuilder();
        if (m.hasFromEntityKind) {
            feJoinClause.append(NL) //
                .append("JOIN \"entity\" fe").append(NL) //
                .append("ON fe.\"id\" = el.\"from_entity_id\"");
        }
        if (m.hasToEntityKind) {
            teJoinClause.append(NL) //
                .append("JOIN \"entity\" te").append(NL) //
                .append("ON te.\"id\" = el.\"to_entity_id\"");
        }
        if (m.hasTextH2) {
            ftJoinClause.append(NL) //
                .append("JOIN").append(NL) //
                .append("    FT_SEARCH_DATA(:text, 0, 0) ft").append(NL) //
                .append("ON").append(NL) //
                .append("    ft.\"TABLE\" = 'entity_link'").append(NL) //
                .append("    AND ft.\"KEYS\"[1] = el.\"id\"");
        }
        if (m.hasFromEntityKind || m.hasFromEntityId || m.hasToEntityKind || m.hasToEntityId() || m.hasStatus
            || m.hasTextMariaDB) {

            boolean needsAnd = false;
            whereClause.append(NL) //
                .append("WHERE");
            if (m.hasFromEntityKind) {
                whereClause.append(NL) //
                    .append("    fe.\"dtype\" = :fromEntityKind");
                needsAnd = true;
            }
            if (m.hasFromEntityId) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("el.\"from_entity_id\" = :fromEntityId");
                needsAnd = true;
            }
            if (m.hasToEntityKind) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("te.\"dtype\" = :toEntityKind");
                needsAnd = true;
            }
            if (m.hasToEntityId) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("el.\"to_entity_id\" = :toEntityId");
                needsAnd = true;
            }
            if (m.hasStatus) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("e.\"status\" IN (:status)");
                needsAnd = true;
            }
            if (m.hasTextMariaDB) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("MATCH(\"from_entity_locations\", \"to_entity_locations\") AGAINST (:text");
                if (m.isAdvanced)
                    whereClause.append(" IN BOOLEAN MODE");
                whereClause.append(')');
                needsAnd = true;
            }
        }
        if (m.isSorted)
            entityUtils.appendOrderByClause(orderByClause, m.pageable, "e.", "el.", "cbu.", "ubu.", true);

        String template = """
            SELECT %s
            FROM "entity_link" el
            JOIN "entity" e
            ON e."id" = el."id"%s%s%s%s%s%s;
            """;

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT
        // query with different ORDER BY clauses will result in the registration of multiple identical COUNT queries,
        // each of which will simply overwrite the previous definition. This is not a problem, but it is somewhat
        // inefficient.
        String countSql = String.format(template, "COUNT(*)", feJoinClause, teJoinClause, seJoinClause, ftJoinClause,
            whereClause, "");
        Query countQuery = defineNamedQuery(m.countQueryName, countSql, Long.class);

        String selectFields =
            "e.\"dtype\", e.\"status\", e.\"created\", e.\"created_by_user_id\", e.\"updated\", e.\"updated_by_user_id\", el.*";
        String selectSql = String.format(template, selectFields, feJoinClause, teJoinClause, seJoinClause, ftJoinClause,
            whereClause, orderByClause);
        Query selectQuery = defineNamedQuery(m.selectQueryName, selectSql, EntityLink.class);

        return new QueryPair(countQuery, selectQuery);
    }

    private Query defineFindByEntityIdQuery(String queryName) {
        String sql = """
            SELECT e."dtype", e."status", e."created", e."created_by_user_id", e."updated", e."updated_by_user_id", el.*
            FROM "entity_link" el
            JOIN "entity" e
            ON e."id" = el."id"
            WHERE
                el."from_entity_id" = :fromEntityId
                AND el."to_entity_id" = :toEntityId;
            """;

        return defineNamedQuery(queryName, sql, EntityLink.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<EntityLink> findByFilter(EntityLinkQueryFilter filter, Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.countQueryName) || !queryNames.contains(m.selectQueryName))
                queries = defineNamedQueries(m);
        }
        if (queries == null) {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, EntityLink.class);
            queries = new QueryPair(countQuery, selectQuery);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasFromEntityKind)
            params.put("fromEntityKind", m.filter.getFromEntityKind().name());
        if (m.hasFromEntityId)
            params.put("fromEntityId", m.filter.getFromEntityId());
        if (m.hasToEntityKind)
            params.put("toEntityKind", m.filter.getToEntityKind().name());
        if (m.hasToEntityId)
            params.put("toEntityId", m.filter.getToEntityId());
        if (m.hasStatus)
            params.put("status", m.filter.getStatus().stream().map(s -> s.name()).toList());
        if (m.hasText)
            params.put("text", m.filter.getText());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), m.pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<EntityLink> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, m.pageable, total);
    }

    @Override
    public Optional<EntityLink> findByEntityIds(Long fromEntityId, Long toEntityId) {
        String queryName = "entityLink.findByEntityIds";

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName))
                query = defineFindByEntityIdQuery(queryName);
        }
        if (query == null)
            query = em.createNamedQuery(queryName, EntityLink.class);
        query.setParameter("fromEntityId", fromEntityId) //
            .setParameter("toEntityId", toEntityId);
        try {
            return Optional.of((EntityLink)query.getSingleResult());
        } catch (NoResultException _) {
            return Optional.empty();
        }
    }

}
