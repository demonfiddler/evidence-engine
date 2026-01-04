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
import org.springframework.transaction.annotation.Transactional;

import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicQueryFilter;
import jakarta.persistence.Query;

@Transactional
public class CustomTopicRepositoryImpl extends AbstractCustomRepositoryImpl implements CustomTopicRepository {

    /** Describes the elements of a query. */
    static record QueryMetaData(@Nullable TopicQueryFilter filter, @NonNull Pageable pageable, String countQueryName,
        String selectQueryName, boolean hasParentId, boolean hasText, boolean hasTextH2, boolean hasTextMariaDB,
        boolean isAdvanced, boolean hasStatus, boolean hasRecordId, boolean isRecursive, boolean isPaged, boolean isSorted) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTopicRepositoryImpl.class);

    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    @SuppressWarnings("null")
    private QueryMetaData getQueryMetaData(@Nullable TopicQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasFilter = filter != null;
        boolean hasParentId = hasFilter && filter.getParentId() != null;
        boolean hasText = hasFilter && filter.getText() != null;
        boolean hasTextH2 = hasText && profileUtils.isIntegrationTesting();
        boolean hasTextMariaDB = hasText && !profileUtils.isIntegrationTesting();
        boolean isAdvanced = hasText && filter.getAdvancedSearch() != null && filter.getAdvancedSearch();
        boolean hasStatus = hasFilter && filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean hasRecordId = hasFilter && filter.getRecordId() != null;
        boolean isRecursive = hasFilter && filter.getRecursive() != null && filter.getRecursive();
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();

        // If filter supplies a recordId, ignore all other criteria.
        if (hasRecordId) {
            hasParentId = hasText = hasTextH2 = hasTextMariaDB = isAdvanced = hasStatus = isRecursive = false;
        }

        // Unauthenticated queries should only return published results.
        if (securityUtils.getCurrentUsername().equals("anonymousUser")) {
            if (filter == null)
                filter = new TopicQueryFilter();
            filter.setStatus(List.of(StatusKind.PUB));
            hasFilter = hasStatus = true;
        }

        // For paged queries involving an H2 full text filter, we need to ensure that the sort order includes "id"
        // because otherwise the join with FT_SEARCH_DATA can result in records duplicated across successive pages.
        if (hasTextH2 && isPaged && pageable.getSort().filter(o -> o.getProperty().equals("id")).isEmpty()) {
            Sort sort = pageable.getSort().and(Sort.by("id"));
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            isSorted = true;
        }

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        StringBuilder[] queryNames = { countQueryName, selectQueryName };
        append("topic.", queryNames);
        countQueryName.append("countBy");
        selectQueryName.append("findBy");
        if (hasRecordId)
            append("Id", queryNames);
        if (hasParentId) {
            append("Parent", queryNames);
            if (isRecursive)
                append("Recursive", queryNames);
        }
        if (hasStatus) {
            append("Status", queryNames);
        }
        if (hasText) {
            append("Text", queryNames);
            if (isAdvanced)
                append("Advanced", queryNames);
        }
        if (isSorted) {
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);
        }

        return new QueryMetaData(filter, pageable, countQueryName.toString(), selectQueryName.toString(), hasParentId,
            hasText, hasTextH2, hasTextMariaDB, isAdvanced, hasStatus, hasRecordId, isRecursive, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(QueryMetaData m) {
        String template;
        String selectFields;
        StringBuilder cteJoinClause = new StringBuilder();
        StringBuilder cteParentIdClause = new StringBuilder();
        StringBuilder cteStatusClause1 = new StringBuilder();
        StringBuilder cteStatusClause2 = new StringBuilder();
        StringBuilder ftJoinClause = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder orderByClause = new StringBuilder();

        // parentId/status predicates applied to recursive predicate when recursive or main select when non-recursive;
        // text predicate is only ever applied to the main select.
        if (m.isRecursive) {
            if (m.filter.getParentId() == -1)
                cteParentIdClause.append("IS NULL");
            else
                cteParentIdClause.append("= :parentId");
            if (m.hasStatus) {
                cteJoinClause.append(NL) //
                    .append("    JOIN \"entity\" e").append(NL) //
                    .append("    ON e.\"id\" = t.\"id\"");
                cteStatusClause1.append(NL) //
                    .append("        AND e.\"status\" IN (:status)");
                cteStatusClause2.append(NL) //
                    .append("    WHERE e.\"status\" IN (:status)");
            }
            // At first sight one might think that it would be more efficient to select the topic records from the
            // sub_topics temporary table, but this doesn't work because in MariaDB that table uses the MEMORY storage
            // engine, which doesn't support full text indexes. Hence the need to select ids only then use them in the
            // final join.
            template = """
                WITH RECURSIVE "sub_topic" ("id", "parent_id")
                AS (
                    SELECT t."id", t."parent_id"
                    FROM "topic" t%s
                    WHERE t."parent_id" %s%s
                    UNION ALL
                    SELECT t."id", t."parent_id"
                    FROM "topic" t%s
                    JOIN "sub_topic" st
                    ON st."id" = t."parent_id"%s
                )
                SELECT %s
                FROM "topic" t
                JOIN "entity" e
                ON e."id" = t."id"
                JOIN "sub_topic" st
                ON st."id" = t."id"%s%s%s;
                """;
            selectFields =
                "DISTINCT e.\"dtype\", e.\"status\", e.\"rating\", e.\"created\", e.\"created_by_user_id\", e.\"updated\", e.\"updated_by_user_id\", t.*";
        } else {
            template = """
                %s%s%s%s%sSELECT %s
                FROM "topic" t
                JOIN "entity" e
                ON e."id" = t."id"%s%s%s;
                """;
            selectFields =
                "e.\"dtype\", e.\"status\", e.\"rating\", e.\"created\", e.\"created_by_user_id\", e.\"updated\", e.\"updated_by_user_id\", t.*";
        }
        if ((m.hasParentId || m.hasStatus || m.hasRecordId) && !m.isRecursive || m.hasText) {
            boolean needsAnd = false;
            if ((m.hasParentId || m.hasStatus || m.hasRecordId) && !m.isRecursive || m.hasTextMariaDB) {
                whereClause.append(NL) //
                    .append("WHERE");
            }
            if (m.hasRecordId) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("e.\"id\" = :recordId");
                needsAnd = true;
            }
            if (m.hasParentId && !m.isRecursive) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("t.\"parent_id\" ");
                if (m.filter.getParentId() == -1)
                    whereClause.append("IS NULL");
                else
                    whereClause.append("= :parentId");
                needsAnd = true;
            }
            if (m.hasStatus && !m.isRecursive) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("e.\"status\" IN (:status)");
                needsAnd = true;
            }
            if (m.hasTextH2) {
                ftJoinClause.append(NL) //
                    .append("JOIN FT_SEARCH_DATA(:text, 0, 0) ft").append(NL) //
                    .append("ON ft.\"TABLE\" = 'topic'").append(NL) //
                    .append("    AND ft.\"KEYS\"[1] = t.\"id\"");
            } else if (m.hasTextMariaDB) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("MATCH (\"label\", \"description\") AGAINST (:text");
                if (m.isAdvanced)
                    whereClause.append(" IN BOOLEAN MODE");
                whereClause.append(')');
                needsAnd = true;
            }
        }
        if (m.isSorted)
            entityUtils.appendOrderByClause(orderByClause, m.pageable, "e.", "t.", "cbu.", "ubu.", true);

        String countSql = String.format(template, cteJoinClause, cteParentIdClause, cteStatusClause1, cteJoinClause,
            cteStatusClause2, "COUNT(*)", ftJoinClause, whereClause, "");
        String selectSql = String.format(template, cteJoinClause, cteParentIdClause, cteStatusClause1, cteJoinClause,
            cteStatusClause2, selectFields, ftJoinClause, whereClause, orderByClause);

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT query
        // with different ORDER BY clauses will result in the registration of multiple identical COUNT queries, each of
        // which will simply overwrite the previous definition. This is not a problem, but it is somewhat inefficient.
        Query countQuery = defineNamedQuery(m.countQueryName, countSql, Long.class);
        Query selectQuery = defineNamedQuery(m.selectQueryName, selectSql, Topic.class);

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<Topic> findByFilter(@NonNull TopicQueryFilter filter, @NonNull Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.countQueryName) || !queryNames.contains(m.selectQueryName))
                queries = defineNamedQueries(m);
        }
        if (queries == null) {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, Topic.class);
            queries = new QueryPair(countQuery, selectQuery);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasParentId && m.filter.getParentId() != -1)
            params.put("parentId", m.filter.getParentId());
        if (m.hasRecordId)
            params.put("recordId", m.filter.getRecordId());
        if (m.hasText)
            params.put("text", m.filter.getText());
        if (m.hasStatus)
            params.put("status", m.filter.getStatus().stream().map(s -> s.name()).toList());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<Topic> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, pageable, total);
    }

}
