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

import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.ProfileUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class CustomTopicRefRepositoryImpl implements CustomTopicRefRepository {

    /** Describes the elements of a query. */
    static record QueryMetaData(String countQueryName, String selectQueryName, boolean hasTopicId, String entityName,
        boolean hasEntityKind, boolean hasEntityId, boolean hasText, boolean hasTextH2, boolean hasTextMariaDB,
        boolean isAdvanced, boolean isPaged, boolean isSorted) {
    }

    static Logger logger = LoggerFactory.getLogger(CustomTopicRefRepositoryImpl.class);

    @PersistenceContext
    EntityManager em;
    @Resource
    EntityUtils entityUtils;
    @Resource
    ProfileUtils profileUtils;

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    private QueryMetaData getQueryMetaData(@NonNull TopicRefQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasTopicId = filter.getTopicId() != null;
        String entityName = entityUtils.getEntityName(filter.getEntityKind());
        boolean hasEntityKind = filter.getEntityKind() != null;
        boolean hasEntityId = filter.getEntityId() != null;
        boolean hasText = filter.getText() != null;
        boolean hasTextH2 = hasText && profileUtils.isIntegrationTesting();
        boolean hasTextMariaDB = hasText && !profileUtils.isIntegrationTesting();
        boolean isAdvanced = hasText && filter.getAdvancedSearch();
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        String entityNameTC = StringUtils.firstToUpper(entityName);
        countQueryName.append("topicRef.count").append(entityNameTC).append("By");
        selectQueryName.append("topicRef.find").append(entityNameTC).append("By");
        if (hasTopicId) {
            countQueryName.append("Topic");
            selectQueryName.append("Topic");
        }
        if (hasEntityKind) {
            // NOTE: at present EntityKind is mandatory so the query names don't really need
            // to include it. However, once we support entity inheritance/polymorphism,
            // EntityKind will become an optional query filter field and will be required in
            // the query names.
            countQueryName.append("EntityKind");
            selectQueryName.append("EntityKind");
        }
        if (hasEntityId) {
            countQueryName.append("EntityId");
            selectQueryName.append("EntityId");
        }
        if (hasText) {
            countQueryName.append("Text");
            selectQueryName.append("Text");
            if (isAdvanced) {
                countQueryName.append("Advanced");
                selectQueryName.append("Advanced");
            }
        }
        if (isSorted) {
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);
        }

        return new QueryMetaData(countQueryName.toString(), selectQueryName.toString(), hasTopicId, entityName,
            hasEntityKind, hasEntityId, hasText, hasTextH2, hasTextMariaDB, isAdvanced, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(TopicRefQueryFilter filter, Pageable pageable, QueryMetaData m) {
        /*
         * SELECT tr."id", tr."topic_id", '${entityKind}' AS "entity_kind", tr."${entityName}_id", tr."locations"
         * FROM "topic_${entityName}_ref" tr
         * JOIN
         *     FT_SEARCH_DATA(:text, 0, 0) ft
         * ON
         *     ft."TABLE" = 'topic_${entityName}_ref'
         *     AND tr."id" = ft."KEYS"[1]
         * WHERE
         *     tr."topic_id" = :topicId
         *     AND tr."${entityName}_id" = :entityId
         *     AND MATCH(...) AGAINST (:text)
         * ${orderByClause}
         */

        String selectFields = "tr.\"id\", tr.\"topic_id\", '" + filter.getEntityKind() + "' AS \"entity_kind\", tr.\""
            + m.entityName + "_id\" AS \"entity_id\", tr.\"locations\"";
        boolean needsAnd = false;
        String tableName = "topic_" + m.entityName + "_ref";
        StringBuilder ftJoinTable = new StringBuilder();
        StringBuilder ftJoinCondition = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();
        StringBuilder orderByClause = new StringBuilder();
        if (m.hasTopicId || m.hasEntityId || m.hasText) {
            whereClause.append("WHERE");
            if (m.hasTopicId) {
                whereClause.append(NL) //
                    .append("    tr.\"topic_id\" = :topicId");
                needsAnd = true;
            }
            // if (m.hasEntityKind) {
            // whereClause.append(NL) //
            // .append("    ");
            // if (needsAnd)
            // whereClause.append("AND ");
            // whereClause.append("\"entity_kind\" = :entityKind");
            // needsAnd = true;
            // }
            if (m.hasEntityId) {
                whereClause.append(NL) //
                    .append("    ");
                if (needsAnd)
                    whereClause.append("AND ");
                whereClause.append("tr.\"").append(m.entityName).append("_id\" = :entityId");
                needsAnd = true;
            }
            if (m.hasText) {
                if (m.hasTextH2) {
                    ftJoinTable.append(NL) //
                        .append("JOIN").append(NL) //
                        .append("    FT_SEARCH_DATA(:text, 0, 0) ft");
                    ftJoinCondition.append(NL) //
                        .append("ON").append(NL) //
                        .append("    ft.\"TABLE\" = '").append(tableName).append("'").append(NL) //
                        .append("    AND tr.\"id\" = ft.\"KEYS\"[1]");
                } else if (m.hasTextMariaDB) {
                    whereClause.append(NL) //
                        .append("    ");
                    if (needsAnd)
                        whereClause.append("AND ");
                    whereClause.append("MATCH(\"locations\") AGAINST (:text");
                    if (m.isAdvanced)
                        whereClause.append(" IN BOOLEAN MODE");
                    whereClause.append(')');
                }
            }
        }
        if (m.isSorted)
            entityUtils.appendOrderByClause(orderByClause, pageable, "", true);

        String template = """
            SELECT %s
            FROM "%s" tr
            %s%s%s%s;
            """;

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT
        // query with different ORDER BY clauses will result in the registration of multiple identical COUNT queries,
        // each of which will simply overwrite the previous definition. This is not a problem, but it is somewhat
        // inefficient.
        String countSql = String.format(template, "COUNT(*)", tableName, ftJoinTable, ftJoinCondition, whereClause, "");
        Query countQuery = em.createNativeQuery(countSql, Long.class);
        em.getEntityManagerFactory().addNamedQuery(m.countQueryName, countQuery);
        logger.debug("Defined query '{}' as:\n{}", m.countQueryName, countSql);

        String selectSql = String.format(template, selectFields, tableName, ftJoinTable, ftJoinCondition, whereClause,
            orderByClause);
        Query selectQuery = em.createNativeQuery(selectSql, TopicRef.class);
        em.getEntityManagerFactory().addNamedQuery(m.selectQueryName, selectQuery);
        logger.debug("Defined query '{}' as:\n{}", m.selectQueryName, selectSql);

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    // @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Page<TopicRef> findByFilter(TopicRefQueryFilter filter, Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries;
        try {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, TopicRef.class);
            queries = new QueryPair(countQuery, selectQuery);
        } catch (IllegalArgumentException e) {
            queries = defineNamedQueries(filter, pageable, m);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasTopicId)
            params.put("topicId", filter.getTopicId());
        // if (m.hasEntityKind)
        // params.put("entityKind", filter.getEntityKind());
        if (m.hasEntityId)
            params.put("entityId", filter.getEntityId());
        if (m.hasText)
            params.put("text", filter.getText());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<TopicRef> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, pageable, total);
    }

}
