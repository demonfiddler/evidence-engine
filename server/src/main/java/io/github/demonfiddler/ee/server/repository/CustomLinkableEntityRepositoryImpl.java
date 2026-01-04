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

package io.github.demonfiddler.ee.server.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import io.github.demonfiddler.ee.server.model.ILinkableEntity;
import io.github.demonfiddler.ee.server.model.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.StatusKind;
import jakarta.persistence.Query;

/**
 * An abstract base implementation of the {@code CustomRepository} interface.
 * @param <T> The type handled by the implementation.
 */
public abstract class CustomLinkableEntityRepositoryImpl<T extends ILinkableEntity> extends AbstractCustomRepositoryImpl
    implements CustomRepository<T, LinkableEntityQueryFilter> {

    /** Describes the elements of a query. */
    private static record QueryMetaData(LinkableEntityQueryFilter filter, Pageable pageable, String entityName,
        String countQueryName, String selectQueryName, boolean hasRecordId, boolean hasTopic, boolean isRecursive,
        boolean hasFromEntityKind, boolean hasFromEntityId, String fromEntityName, boolean hasToEntityKind,
        boolean hasToEntityId, String toEntityName, boolean hasStatus, boolean hasText, boolean hasTextH2,
        boolean hasTextMariaDB, boolean isAdvanced, boolean isPaged, boolean isSorted,
        boolean isSortedOnCreatedByUsername, boolean isSortedOnUpdatedByUsername) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLinkableEntityRepositoryImpl.class);

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns the runtime entity class corresponding to the {@literal <T>} type parameter.
     * @return The runtime entity class.
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Returns the database columns for which there exists a full text index.
     * @return A comma-separated list of back-quoted database column names.
     */
    protected abstract String getFulltextColumns();

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    @SuppressWarnings("null")
    private QueryMetaData getQueryMetaData(@Nullable LinkableEntityQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasFilter = filter != null;
        boolean hasRecordId = hasFilter && filter.getRecordId() != null;
        boolean hasTopic = hasFilter && !hasRecordId && filter.getTopicId() != null;
        boolean isRecursive = hasTopic && filter.getRecursive() != null && filter.getRecursive();
        boolean hasFromEntityKind = hasFilter && !hasRecordId && filter.getFromEntityKind() != null;
        boolean hasFromEntityId = hasFilter && !hasRecordId && filter.getFromEntityId() != null;
        boolean hasToEntityKind = hasFilter && !hasRecordId && filter.getToEntityKind() != null;
        boolean hasToEntityId = hasFilter && !hasRecordId && filter.getToEntityId() != null;
        boolean hasStatus = hasFilter && !hasRecordId && filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean hasText = hasFilter && !hasRecordId && filter.getText() != null && !filter.getText().isBlank();
        boolean hasTextH2 = hasText && profileUtils.isIntegrationTesting();
        boolean hasTextMariaDB = hasText && !profileUtils.isIntegrationTesting();
        boolean isAdvanced = hasText && filter.getAdvancedSearch() != null && filter.getAdvancedSearch();
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();
        boolean isSortedOnCreatedByUsername = false;
        boolean isSortedOnUpdatedByUsername = false;
        String entityName = entityUtils.getEntityName(getEntityClass());
        String fromEntityName = hasFromEntityKind ? entityUtils.getEntityName(filter.getFromEntityKind()) : "";
        String toEntityName = hasToEntityKind ? entityUtils.getEntityName(filter.getToEntityKind()) : "";

        // Unauthenticated queries should only return published results.
        if (securityUtils.getCurrentUsername().equals("anonymousUser")) {
            if (filter == null)
                filter = new LinkableEntityQueryFilter();
            filter.setStatus(List.of(StatusKind.PUB));
            hasFilter = hasStatus = true;
        }

        // For paged queries, we need to ensure that the sort order includes "id"
        // because otherwise the join with H2's
        // FT_SEARCH_DATA can result in records duplicated across successive pages, OR
        // result sets involving JOINs can
        // be returned in a nondeterministic order.
        if (isPaged && pageable.getSort().filter(o -> o.getProperty().equals("id")).isEmpty()) {
            Sort sort = pageable.getSort().and(Sort.by("id"));
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            isSorted = true;
        }
        if (isSorted) {
            isSortedOnCreatedByUsername =
                !pageable.getSort().filter(o -> o.getProperty().equals("createdByUsername")).isEmpty();
            isSortedOnUpdatedByUsername =
                !pageable.getSort().filter(o -> o.getProperty().equals("updatedByUsername")).isEmpty();
        }

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        StringBuilder[] queryNames = { countQueryName, selectQueryName };
        append(entityName, queryNames);
        countQueryName.append(".countBy");
        selectQueryName.append(".findBy");
        if (hasRecordId) {
            append("Id", queryNames);
        }
        if (hasTopic) {
            if (isRecursive)
                append("Recursive", queryNames);
            append("Topic", queryNames);
        }
        if (hasFromEntityKind || hasFromEntityId) {
            append("FromEntity", queryNames);
            if (hasFromEntityKind)
                append("Kind", queryNames);
            if (hasFromEntityId)
                append("Id", queryNames);
        }
        if (hasToEntityKind || hasToEntityId) {
            append("ToEntity", queryNames);
            // Unnecessary, since the query name starts with toEntityName?
            // Actually, it depends on whether the query's entity kind is linked as the
            // fromEntity or the toEntity!
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
        if (isSorted) {
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);
        }

        return new QueryMetaData(filter, pageable, entityName, countQueryName.toString(), selectQueryName.toString(),
            hasRecordId, hasTopic, isRecursive, hasFromEntityKind, hasFromEntityId, fromEntityName, hasToEntityKind,
            hasToEntityId, toEntityName, hasStatus, hasText, hasTextH2, hasTextMariaDB, isAdvanced, isPaged, isSorted,
            isSortedOnCreatedByUsername, isSortedOnUpdatedByUsername);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(QueryMetaData m) {
        // Question: do we need the recursive topic filter if a master entity is
        // provided?
        // One might be tempted to think that we would only need to apply a topic filter
        // when retrieving the master
        // entity list, and that retrieval of associated entities could be done simply
        // via the entity_link
        // association table.
        //
        // However, a master entity person might be associated with multiple topics and
        // multiple publications or
        // declarations, each of which may or may not pertain to the specified topic,
        // so if a topic filter is specified, we would not want to include publications
        // or declarations that do not
        // pertain to that topic.
        //
        // Additionally, if NO topic was selected, we would expect to see ALL associated
        // publications and declarations,
        // whereas when a topic IS selected, we would expect to see ONLY those
        // publications and declarations associated
        // with that topic.
        //
        // In other words, if a topicId is supplied, it must be used in the query
        // regardless of whether a master entity
        // is supplied.
        /*
         * The primary use for this query is to fetch all records of a particular entity
         * kind, which match a set of
         * criteria including topic and master entity links, status and text matches.
         * This is used by the filtered
         * claims, declarations, persons, publications and quotations top-level query
         * fields.
         * A possible future use of this query might be to fetch all records of any
         * entity kind, which match the same set
         * of criteria, with a view to implementing queries such as
         * ILinkableEntity.fromEntities: [ILinkableEntity] and
         * ILinkableEntity.toEntities: [ILinkableEntity]. Such potentially heterogeneous
         * results would require a native
         * query that joins the base "entity" table with all other tables "claim",
         * "declaration", "person", "publication",
         * "quotation" and "topic". In this case we would have to deal the possibility
         * of column name/type clashes.
         * 
         * -- This is what the query template looks like conceptually when ALL filter
         * fields are provided.
         * -- It includes all combinations of recursive & non-recursive, count & select
         * queries, H2 & MariaDB databases.
         * 
         * -- if (m.isRecursive) { commonTableExpr =
         * WITH RECURSIVE "sub_topic" ("id", "parent_id")
         * AS (
         * SELECT t."id", t."parent_id"
         * FROM "topic" t
         * -- if (m.hasStatus) {
         * JOIN "entity" e
         * ON e."id" = t."id"
         * -- }
         * WHERE t."id" = :topicId
         * -- if (m.hasStatus) {
         * AND e."status" IN (:status)
         * -- }
         * UNION ALL
         * SELECT t."id", t."parent_id"
         * FROM "topic" t
         * JOIN "sub_topic" st
         * ON st."id" = t."parent_id"
         * -- if (m.hasStatus) {
         * JOIN "entity" e
         * ON e."id" = t."id"
         * WHERE e."status" IN (:status)
         * -- }
         * )
         * -- }
         * 
         * SELECT COUNT(*) | DISTINCT e."dtype", e."status", e."rating", e."created",
         * e."created_by_user_id", e."updated", e."updated_by_user_id", ee.*
         * FROM "entity" e
         * 
         * -- if (m.hasTopic) { topJoinClause =
         * JOIN "entity_link" topic_el
         * ON
         * -- if (!m.isRecursive) {
         * topic_el."from_entity_id" = :topicId
         * -- }
         * AND topic_el."to_entity_id" = e."id"
         * -- if (m.isRecursive) {
         * JOIN "sub_topic" st
         * ON st."id" = topic_el."from_entity_id"
         * -- } else {
         * JOIN "entity" topic_e
         * ON
         * topic_e."id" = topic_el."from_entity_id"
         * AND topic_e."dtype" = 'TOP'
         * -- if (m.hasStatus) {
         * AND topic_e."status" IN (:status)
         * -- }
         * -- }
         * -- }
         * 
         * -- if (m.hasFromEntityKind || m.hasFromEntityId) { meJoinClause =
         * JOIN "entity_link" master_el
         * ON
         * -- if (m.hasFromEntityId) {
         * master_el."from_entity_id" = :fromEntityId
         * -- }
         * AND master_el."to_entity_id" = e."id"
         * -- if (m.hasFromEntityKind) {
         * JOIN "entity" master_e
         * ON
         * master_e."id" = master_el."from_entity_id"
         * AND master_e."dtype" = :fromEntityKind
         * -- }
         * -- }
         * 
         * -- if (m.hasToEntityKind || m.hasToEntityId) { meJoinClause =
         * JOIN "entity_link" master_el
         * ON
         * -- if (m.hasToEntityId) {
         * master_el."to_entity_id" = :toEntityId
         * -- }
         * AND master_el."from_entity_id" = e."id"
         * -- if (m.hasToEntityKind) {
         * JOIN "entity" master_e
         * ON
         * master_e."id" = master_el."to_entity_id"
         * AND master_e."dtype" = :toEntityKind
         * -- }
         * -- }
         * 
         * -- if (m.hasTextH2) { ftJoinClause =
         * JOIN FT_SEARCH_DATA(:text, 0, 0) ft
         * ON
         * ft."TABLE" = '${m.toEntityName}'
         * AND ft."KEYS"[1] = e."id"
         * -- }
         * 
         * JOIN "${m.entityName}" ee
         * ON ee."id" = e."id"
         *
         * -- if (m.isSortedOnCreatedByUsername) { cbuJoinClause =
         * JOIN "user" cbu
         * ON cbu."id" = e."created_by_user_id"
         * --}
         *
         * -- if (m.isSortedOnUpdatedByUsername) { ubuJoinClause =
         * JOIN "user" ubu
         * ON ubu."id" = e."updated_by_user_id"
         * --}
         *
         * -- if (m.hasRecordId || m.hasStatus || m.hasTextMariaDB) {
         * WHERE
         * -- if (m.hasRecordId) {
         *   e."id" = :recordId
         * -- }
         *
         * -- if (m.hasStatus) {
         *   AND e."status" IN (:status)
         * -- }
         * 
         * -- if (m.hasTextMariaDB) {
         *   AND MATCH (${fulltextEntityColumns}) AGAINST (:text IN BOOLEAN MODE)
         * -- }
         * -- }
         * 
         * -- if (m.isSorted)
         * ORDER BY
         *   "${sortField}" ${sortOrder}, ...
         * -- }
         * ;
         */

        String commonTableExpr;
        if (m.isRecursive) {
            // This is the recursive common table expression that collects a specified topic and its sub-topics.
            String cteTemplate = """
                WITH RECURSIVE "sub_topic" ("id", "parent_id")
                AS (
                    SELECT t."id", t."parent_id"
                    FROM "topic" t%s
                    WHERE t."id" = :topicId%s
                    UNION ALL
                    SELECT t."id", t."parent_id"
                    FROM "topic" t%s
                    JOIN "sub_topic" st
                    ON st."id" = t."parent_id"%s
                )
                """;
            StringBuilder cteJoinClause = new StringBuilder();
            StringBuilder cteStatusClause1 = new StringBuilder();
            StringBuilder cteStatusClause2 = new StringBuilder();
            if (m.hasStatus) {
                cteJoinClause.append(NL) //
                    .append("    JOIN \"entity\" e").append(NL) //
                    .append("    ON e.\"id\" = t.\"id\"");
                cteStatusClause1.append(NL) //
                    .append("        AND e.\"status\" IN (:status)");
                cteStatusClause2.append(NL) //
                    .append("    WHERE e.\"status\" IN (:status)");
            }
            commonTableExpr =
                String.format(cteTemplate, cteJoinClause, cteStatusClause1, cteJoinClause, cteStatusClause2);
        } else {
            commonTableExpr = "";
        }

        boolean needsAnd = false;
        StringBuilder topicJoinClause = new StringBuilder();
        if (m.hasTopic) {
            topicJoinClause.append(NL) //
                .append("JOIN \"entity_link\" topic_el").append(NL) //
                .append("ON");
            if (!m.isRecursive) {
                topicJoinClause.append(NL) //
                    .append("    topic_el.\"from_entity_id\" = :topicId");
                needsAnd = true;
            }
            topicJoinClause.append(NL) //
                .append("    ");
            if (needsAnd)
                topicJoinClause.append("AND ");
            topicJoinClause.append("topic_el.\"to_entity_id\" = e.\"id\"");

            if (m.isRecursive) {
                topicJoinClause.append(NL) //
                    .append("JOIN \"sub_topic\" st").append(NL) //
                    .append("ON st.\"id\" = topic_el.\"from_entity_id\"");
            } else {
                topicJoinClause.append(NL) //
                    .append("JOIN \"entity\" topic_e").append(NL) //
                    .append("ON").append(NL) //
                    .append("    topic_e.\"id\" = topic_el.\"from_entity_id\"").append(NL) //
                    .append("    AND topic_e.\"dtype\" = 'TOP'");
                if (m.hasStatus) {
                    topicJoinClause.append(NL) //
                        .append("    AND topic_e.\"status\" IN (:status)");
                }
            }
        }

        // N.B. This code assumes that fromEntityKind/fromEntityId and toEntityKind/toEntityId are mutually exclusive.
        StringBuilder meJoinClause = new StringBuilder();
        if (m.hasFromEntityKind || m.hasFromEntityId) {
            meJoinClause.append(NL) //
                .append("JOIN \"entity_link\" master_el").append(NL) //
                .append("ON");
            needsAnd = false;
            if (m.hasFromEntityId) {
                meJoinClause.append(NL) //
                    .append("    master_el.\"from_entity_id\" = :fromEntityId");
                needsAnd = true;
            }
            meJoinClause.append(NL) //
                .append("    ");
            if (needsAnd)
                meJoinClause.append("AND ");
            meJoinClause.append("master_el.\"to_entity_id\" = e.\"id\"");
            if (m.hasFromEntityKind) {
                meJoinClause.append(NL) //
                    .append("JOIN \"entity\" master_e").append(NL) //
                    .append("ON").append(NL) //
                    .append("    master_e.\"id\" = master_el.\"from_entity_id\"").append(NL) //
                    .append("    AND master_e.\"dtype\" = :fromEntityKind");
            }
        } else if (m.hasToEntityKind || m.hasToEntityId) {
            meJoinClause.append(NL) //
                .append("JOIN \"entity_link\" master_el").append(NL) //
                .append("ON");
            needsAnd = false;
            if (m.hasToEntityId) {
                meJoinClause.append(NL) //
                    .append("    master_el.\"to_entity_id\" = :toEntityId");
                needsAnd = true;
            }
            meJoinClause.append(NL) //
                .append("    ");
            if (needsAnd)
                meJoinClause.append("AND ");
            meJoinClause.append("master_el.\"from_entity_id\" = e.\"id\"");
            if (m.hasToEntityKind) {
                meJoinClause.append(NL) //
                    .append("JOIN \"entity\" master_e").append(NL) //
                    .append("ON").append(NL) //
                    .append("    master_e.\"id\" = master_el.\"to_entity_id\"").append(NL) //
                    .append("    AND master_e.\"dtype\" = :toEntityKind");
            }
        }

        StringBuilder ftJoinClause = new StringBuilder();
        if (m.hasTextH2) {
            ftJoinClause.append(NL) //
                .append("JOIN FT_SEARCH_DATA(:text, 0, 0) ft").append(NL) //
                .append("ON").append(NL) //
                .append("    ft.\"TABLE\" = '").append(m.toEntityName).append('\'').append(NL) //
                .append("    AND ft.\"KEYS\"[1] = e.\"id\"");
        }

        StringBuilder eeJoinClause = new StringBuilder();
        eeJoinClause.append(NL) //
            .append("JOIN \"").append(m.entityName).append("\" ee").append(NL).append("ON ee.\"id\" = e.\"id\"");

        StringBuilder cbuJoinClause = new StringBuilder();
        if (m.isSortedOnCreatedByUsername) {
            cbuJoinClause.append(NL) //
                .append("JOIN \"user\" cbu").append(NL) //
                .append("ON cbu.\"id\" = e.\"created_by_user_id\"");
        }
        StringBuilder ubuJoinClause = new StringBuilder();
        if (m.isSortedOnUpdatedByUsername) {
            ubuJoinClause.append(NL) //
                .append("JOIN \"user\" ubu").append(NL) //
                .append("ON ubu.\"id\" = e.\"updated_by_user_id\"");
        }

        StringBuilder whereClause = new StringBuilder();
        if (m.hasRecordId || m.hasStatus || m.hasTextMariaDB) {
            whereClause.append(NL) //
                .append("WHERE");
            needsAnd = false;
            if (m.hasRecordId) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("e.\"id\" = :recordId");
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
                whereClause.append("MATCH (").append(getFulltextColumns()).append(") AGAINST (:text");
                if (m.isAdvanced)
                    whereClause.append(" IN BOOLEAN MODE");
                whereClause.append(')');
                needsAnd = true;
            }
        }

        StringBuilder orderByClause = new StringBuilder();
        if (m.isSorted)
            entityUtils.appendOrderByClause(orderByClause, m.pageable, "e.", "ee.", "cbu.", "ubu.", true);

        String template = """
            %sSELECT %s
            FROM "entity" e%s%s%s%s%s%s%s%s;
            """;

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT query
        // with different ORDER BY clauses will result in the registration of multiple identical COUNT queries, each of
        // which will simply overwrite the previous definition. This is not a problem, but it is somewhat inefficient.
        String countSql = String.format(template, commonTableExpr, "COUNT(*)", topicJoinClause, meJoinClause,
            ftJoinClause, eeJoinClause, "", "", whereClause, "");
        Query countQuery = defineNamedQuery(m.countQueryName, countSql, Long.class);

        String columns =
            "DISTINCT e.\"dtype\", e.\"status\", e.\"rating\", e.\"created\", e.\"created_by_user_id\", e.\"updated\", e.\"updated_by_user_id\", ee.*";
        String selectSql = String.format(template, commonTableExpr, columns, topicJoinClause, meJoinClause,
            ftJoinClause, eeJoinClause, cbuJoinClause, ubuJoinClause, whereClause, orderByClause);
        Query selectQuery = defineNamedQuery(m.selectQueryName, selectSql, getEntityClass());

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<T> findByFilter(@Nullable LinkableEntityQueryFilter filter, @NonNull Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.countQueryName) || !queryNames.contains(m.selectQueryName))
                queries = defineNamedQueries(m);
        }
        if (queries == null) {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, getEntityClass());
            queries = new QueryPair(countQuery, selectQuery);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasRecordId)
            params.put("recordId", m.filter.getRecordId());
        if (m.hasTopic)
            params.put("topicId", m.filter.getTopicId());
        if (m.hasFromEntityKind)
            params.put("fromEntityKind", m.filter.getFromEntityKind().name());
        if (m.hasFromEntityId)
            params.put("fromEntityId", m.filter.getFromEntityId());
        if (m.hasToEntityKind)
            params.put("toEntityKind", m.filter.getToEntityKind().name());
        if (m.hasToEntityId)
            params.put("toEntityId", m.filter.getToEntityId());
        if (m.hasStatus)
            params.put("status", m.filter.getStatus().stream().map(e -> e.name()).toList());
        if (m.hasText)
            params.put("text", m.filter.getText());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), m.pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<T> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, m.pageable, total);
    }

}
