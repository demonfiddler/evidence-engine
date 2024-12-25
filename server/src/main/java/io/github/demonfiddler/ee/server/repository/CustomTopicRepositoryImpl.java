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

import static io.github.demonfiddler.ee.server.util.EntityUtils.NL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicQueryFilter;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class CustomTopicRepositoryImpl implements CustomTopicRepository {

    /** Describes the elements of a query. */
    static record QueryMetaData(String countQueryName, String selectQueryName, boolean hasParentId, boolean hasText,
        boolean isAdvanced, boolean hasStatus, boolean isRecursive, boolean isPaged, boolean isSorted) {
    }

    private static Logger logger = LoggerFactory.getLogger(CustomTopicRepositoryImpl.class);

    @PersistenceContext
    EntityManager em;
    @Resource
    protected EntityUtils util;

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    private QueryMetaData getQueryMetaData(@NonNull TopicQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasParentId = filter.getParentId() != null;
        boolean hasText = filter.getText() != null;
        boolean isAdvanced = hasText && filter.getAdvancedSearch() != null && filter.getAdvancedSearch();
        boolean hasStatus = filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean isRecursive = filter.getRecursive() != null && filter.getRecursive();
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        countQueryName.append("topic.countBy");
        selectQueryName.append("topic.findBy");
        if (hasParentId) {
            countQueryName.append("ParentId");
            selectQueryName.append("ParentId");
        }
        if (hasStatus) {
            countQueryName.append("Status");
            selectQueryName.append("Status");
        }
        if (hasText) {
            countQueryName.append("Text");
            selectQueryName.append("Text");
            if (isAdvanced) {
                countQueryName.append("Advanced");
                selectQueryName.append("Advanced");
            }
        }
        if (isRecursive) {
            countQueryName.append("Recursive");
            selectQueryName.append("Recursive");
        }
        if (isSorted) {
            util.appendOrderByToQueryName(selectQueryName, pageable);
        }

        return new QueryMetaData(countQueryName.toString(), selectQueryName.toString(), hasParentId, hasText,
            isAdvanced, hasStatus, isRecursive, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(TopicQueryFilter filter, Pageable pageable, QueryMetaData m) {
        // NOTE: status predicate needs to be applied to both topics and entities but
        // parentId predicate is applied to the recursive predicate when recursive or
        // the main select when non-recursive.
        StringBuilder whereClause = new StringBuilder();
        if (!m.isRecursive || m.hasText || m.hasStatus) {
            boolean needsAnd = false;
            whereClause.append(NL).append("WHERE");
            if (!m.isRecursive) {
                whereClause.append(NL).append("    ").append("\"parent_id\" ");
                if (m.hasParentId)
                    whereClause.append("= :parentId");
                else
                    whereClause.append("IS NULL");
                needsAnd = true;
            }
            if (m.hasText || m.hasStatus) {
                if (m.hasText) {
                    whereClause.append(NL).append("    ");
                    if (needsAnd)
                        whereClause.append("AND ");
                    whereClause.append("MATCH (\"label\", \"description\", \"status\") AGAINST (:text");
                    if (m.isAdvanced)
                        whereClause.append(" IN BOOLEAN MODE");
                    whereClause.append(')');
                    needsAnd = true;
                }
                if (m.hasStatus) {
                    whereClause.append(NL).append("    ");
                    if (needsAnd)
                        whereClause.append("AND ");
                    whereClause.append("\"status\" IN (:status)");
                }
            }
        }
        StringBuilder orderByClause = new StringBuilder();
        if (m.isSorted)
            util.appendOrderByClause(orderByClause, pageable, "", true);

        // TODO: convert to use JEP 459: String Templates once released
        String countSql;
        String selectSql;
        if (m.isRecursive) {
            StringBuilder recursiveWhereClause1 = new StringBuilder();
            StringBuilder recursiveWhereClause2 = new StringBuilder();
            if (m.hasStatus) {
                recursiveWhereClause1.append(" AND t.\"status\" IN (:status)");
                recursiveWhereClause2.append(NL).append("    WHERE t.\"status\" IN (:status)");
            }
            // At first sight one might think that it would be more efficient to select the
            // topic records from the sub_topics temporary table, but this doesn't work
            // because that table uses the MEMORY storage engine, which doesn't support full
            // text indexes. Hence the need to select ids only and use them in the final join.
            // NOTE: H2 requires
            // WITH RECURSIVE sub_topic("id, "parent_id")
            // AS (
            // nonRecursiveSelect
            // UNION ALL
            // recursiveSelect
            // )
            // SELECT ...
            // --------
            String template = """
                            WITH RECURSIVE sub_topic
                            AS (
                                SELECT t.\"id\", t.\"parent_id\"
                                FROM topic t
                                WHERE t.\"parent_id\" = :parentId%s
                                UNION ALL
                                SELECT t.\"id\", t.\"parent_id\"
                                FROM topic t
                                INNER JOIN
                                    sub_topic st ON t.\"parent_id\" = st.\"id\"%s
                            )
                            SELECT %s
                            FROM topic t
                            JOIN sub_topic st
                            ON t.\"id\" = st.\"id\"%s%s;
                            """;
            countSql =
                String.format(template, recursiveWhereClause1, recursiveWhereClause2, "COUNT(*)", whereClause, "");
            selectSql = String.format(template, recursiveWhereClause1, recursiveWhereClause2, "DISTINCT t.*",
                whereClause, orderByClause);
        } else {
            String template = """
                            SELECT %s
                            FROM topic t%s%s;
                            """;
            countSql = String.format(template, "COUNT(*)", whereClause, "");
            selectSql = String.format(template, "*", whereClause, orderByClause);
        }

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple
        // executions of the same SELECT query with different ORDER BY clauses will
        // result in the registration of multiple identical COUNT queries, each of
        // which will simply overwrite the previous definition. This is not a problem,
        // but it is somewhat inefficient.
        Query countQuery = em.createNativeQuery(countSql, Long.class);
        em.getEntityManagerFactory().addNamedQuery(m.countQueryName, countQuery);
        logger.debug("Defined query '{}' as:\n{}", m.countQueryName, countSql);

        Query selectQuery = em.createNativeQuery(selectSql, Topic.class);
        em.getEntityManagerFactory().addNamedQuery(m.selectQueryName, selectQuery);
        logger.debug("Defined query '{}' as:\n{}", m.selectQueryName, selectSql);

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    // @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Page<Topic> findByFilter(@NonNull TopicQueryFilter filter, @NonNull Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries;
        try {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, Topic.class);
            queries = new QueryPair(countQuery, selectQuery);
        } catch (IllegalArgumentException e) {
            queries = defineNamedQueries(filter, pageable, m);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasParentId)
            params.put("parentId", filter.getParentId());
        if (m.hasText)
            params.put("text", filter.getText());
        if (m.hasStatus)
            params.put("status", filter.getStatus().stream().map(s -> s.name()).toList());
        util.setQueryParameters(queries, params);
        if (m.isPaged)
            util.setQueryPagination(queries.selectQuery(), pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<Topic> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, pageable, total);
    }

}
