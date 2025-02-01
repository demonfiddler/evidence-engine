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

import static io.github.demonfiddler.ee.server.util.EntityUtils.NL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.ProfileUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional
public class TopicRefRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicRefRepository.class);

    /** Describes the elements of a findByFilter() query. */
    private static record FindQueryMetaData(String countQueryName, String selectQueryName, boolean hasTopicId, String entityName,
        boolean hasEntityKind, boolean hasEntityId, boolean hasText, boolean hasTextH2, boolean hasTextMariaDB,
        boolean isAdvanced, boolean isPaged, boolean isSorted) {}

    @PersistenceContext
    private EntityManager em;
    @Resource
    private EntityUtils entityUtils;
    @Resource
    private ProfileUtils profileUtils;
    /** Keeps track of which named queries have been registered in JPA. */
    private final Set<String> queryNames = new HashSet<>();

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    private FindQueryMetaData getFindQueryMetaData(@NonNull TopicRefQueryFilter filter, @NonNull Pageable pageable) {
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
            // NOTE: at present EntityKind is mandatory so the query names don't really need to include it. However,
            // once we support entity inheritance/polymorphism, EntityKind will become an optional query filter field
            // and will be required in the query names.
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

        return new FindQueryMetaData(countQueryName.toString(), selectQueryName.toString(), hasTopicId, entityName,
            hasEntityKind, hasEntityId, hasText, hasTextH2, hasTextMariaDB, isAdvanced, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(TopicRefQueryFilter filter, Pageable pageable, FindQueryMetaData m) {
        /*
         * SELECT tr."id", tr."topic_id", '${entityKind}' AS "entity_kind", tr."${entityName}_id", tr."locations"
         * FROM "topic_${entityName}_ref" tr
         * JOIN
         *     FT_SEARCH_DATA(:text, 0, 0) ft
         * ON
         *     ft."TABLE" = 'topic_${entityName}_ref'
         *     AND ft."KEYS"[1] = tr."id"
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
            if (m.hasTopicId || m.hasEntityId || m.hasTextMariaDB)
                whereClause.append(NL) //
                    .append("WHERE");
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
                        .append("    AND ft.\"KEYS\"[1] = tr.\"id\"");
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
            FROM "%s" tr%s%s%s%s;
            """;

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT
        // query with different ORDER BY clauses will result in the registration of multiple identical COUNT queries,
        // each of which will simply overwrite the previous definition. This is not a problem, but it is somewhat
        // inefficient.
        String countSql = String.format(template, "COUNT(*)", tableName, ftJoinTable, ftJoinCondition, whereClause, "");
        Query countQuery = em.createNativeQuery(countSql, Long.class);
        em.getEntityManagerFactory().addNamedQuery(m.countQueryName, countQuery);
        queryNames.add(m.countQueryName);
        LOGGER.debug("Defined query '{}' as:\n{}", m.countQueryName, countSql);

        String selectSql = String.format(template, selectFields, tableName, ftJoinTable, ftJoinCondition, whereClause,
            orderByClause);
        Query selectQuery = em.createNativeQuery(selectSql, TopicRef.class);
        em.getEntityManagerFactory().addNamedQuery(m.selectQueryName, selectQuery);
        queryNames.add(m.selectQueryName);
        LOGGER.debug("Defined query '{}' as:\n{}", m.selectQueryName, selectSql);

        return new QueryPair(countQuery, selectQuery);
    }

    private String getAddUpdateQueryName(TopicRef topicRef) {
        String entityKind = EntityKind.valueOf(topicRef.getEntityKind()).label();
        return "topicRef." + (topicRef.getId() == null ? "add" : "update") + entityKind + "Ref";
    }

    private String getRemoveQueryName(TopicRef topicRef) {
        String entityKind = EntityKind.valueOf(topicRef.getEntityKind()).label();
        return "topicRef.remove" + entityKind + "RefBy" + (topicRef.getId() == null ? "EntityId" : "TopicRefId");
    }

    private Query defineAddQuery(String queryName, TopicRef topicRef) {
        String ignore = profileUtils.isIntegrationTesting() ? "" : " IGNORE";
        String template = """
            INSERT%s INTO "topic_%s_ref"
                ("topic_id", "%s_id", "locations")
            VALUES
                (:topicId, :entityId, :locations);
            """;
        EntityKind entityKind = EntityKind.valueOf(topicRef.getEntityKind());
        String entityName = entityUtils.getEntityName(entityKind);
        String sql = String.format(template, ignore, entityName, entityName);

        return defineNamedQuery(queryName, sql, Integer.class);
    }

    private Query defineUpdateQuery(String queryName, TopicRef topicRef) {
        String template = """
            UPDATE "topic_%s_ref"
            SET
                "topic_id" = :topicId,
                "%s_id" = :entityId,
                "locations" = :locations
            WHERE
                "id" = :id;
            """;
        EntityKind entityKind = EntityKind.valueOf(topicRef.getEntityKind());
        String entityName = entityUtils.getEntityName(entityKind);
        String sql = String.format(template, entityName, entityName);

        return defineNamedQuery(queryName, sql, Integer.class);
    }

    private Query defineRemoveQuery(String queryName, TopicRef topicRef) {
        String template;
        if (topicRef.getId() == null) {
            template = """
                DELETE FROM "topic_%s_ref"
                WHERE
                    "topic_id" = :topicId
                    AND "%s_id" = :entityId;
                """;
        } else {
            template = """
                DELETE FROM "topic_%s_ref"
                WHERE "id" = :id;
                """;
        }
        EntityKind entityKind = EntityKind.valueOf(topicRef.getEntityKind());
        String entityName = entityUtils.getEntityName(entityKind);
        String sql = String.format(template, entityName, entityName);
                
        return defineNamedQuery(queryName, sql, Integer.class);
    }

    private /*TypedQuery<TopicRef>*/void defineFindByTopicRefIdQuery(String queryName, EntityKind entityKind) {
        String template = """
            SELECT "id", "topic_id", "%s_id" AS "entity_id", '%s' AS "entity_kind", "locations"
            FROM "topic_%s_ref"
            WHERE "id" = :id;
            """;
        String entityName = entityUtils.getEntityName(entityKind);
        String sql = String.format(template, entityName, entityKind.name(), entityName);

        defineNamedQuery(queryName, sql, TopicRef.class);
    }

    private /*TypedQuery<TopicRef>*/void defineFindByEntityIdQuery(String queryName, EntityKind entityKind) {
        String template = """
            SELECT "id", "topic_id", "%s_id" AS "entity_id", '%s' AS "entity_kind", "locations"
            FROM "topic_%s_ref"
            WHERE
                "topic_id" = :topicId
                AND "%s_id" = :entityId;
            """;
        String entityName = entityUtils.getEntityName(entityKind);
        String sql = String.format(template, entityName, entityKind.name(), entityName, entityName);

        defineNamedQuery(queryName, sql, TopicRef.class);
    }

    private Query defineNamedQuery(String queryName, String sql, Class<?> resultClass) {
        Query query = em.createNativeQuery(sql, resultClass);
        em.getEntityManagerFactory().addNamedQuery(queryName, query);
        queryNames.add(queryName);

        LOGGER.debug("Defined query '{}' as:\n{}", queryName, sql);

        return query;
    }

    /**
     * Saves the specified topic reference. N.B. the {@code entityKind} field must set with an {@link EntityKind} name
     * (not a label). If the {@code id} field is set, updates the existing entity; otherwise, creates a new entity.
     * @param topicRef The {@code TopicRef}.
     * @return The saved entity, with its {@code id} field set if created.
     */
    public TopicRef save(TopicRef topicRef) {
        String queryName = getAddUpdateQueryName(topicRef);
        boolean adding = topicRef.getId() == null;

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName)) {
                query = adding //
                    ? defineAddQuery(queryName, topicRef) //
                    : defineUpdateQuery(queryName, topicRef);
            }
        }
        if (query == null)
            query = em.createNamedQuery(queryName, Integer.class);

        if (!adding)
            query.setParameter("id", topicRef.getId());
        query.setParameter("topicId", topicRef.getTopicId()) //
            .setParameter("entityId", topicRef.getEntityId()) //
            .setParameter("locations", topicRef.getLocations());

        TopicRef result = null;
        if (query.executeUpdate() == 1) {
            if (adding) {
                EntityKind entityKind = EntityKind.valueOf(topicRef.getEntityKind());
                Optional<TopicRef> resultOpt = findByEntityId(topicRef.getTopicId(), topicRef.getEntityId(), entityKind);
                if (resultOpt.isPresent())
                    result = resultOpt.get();
            } else {
                result = topicRef;
            }
        }
        return result;
    }

    /**
     * Removes an existing topic:entity reference.
     * @param topicRef The topic reference to remove.
     * @return The count of reference records deleted.
     */
    public int removeTopicRef(TopicRef topicRef) {
        String queryName = getRemoveQueryName(topicRef);

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName))
                query = defineRemoveQuery(queryName, topicRef);
        }
        if (query == null)
            query = em.createNamedQuery(queryName, Integer.class);

        if (topicRef.getId() == null) {
            query.setParameter("topicId", topicRef.getTopicId()) //
                .setParameter("entityId", topicRef.getEntityId());
        } else {
            query.setParameter("id", topicRef.getId());
        }
        return query.executeUpdate();
    }

    public Optional<TopicRef> findById(Long id, EntityKind entityKind) {
        String queryName = "topicRef.find" + entityKind.label() + "RefByTopicRefId";

        TypedQuery<TopicRef> query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName))
                defineFindByTopicRefIdQuery(queryName, entityKind);
        }
        query = em.createNamedQuery(queryName, TopicRef.class);
        query.setParameter("id", id);
        return Optional.of(query.getSingleResult());
    }

    public Optional<TopicRef> findByEntityId(Long topicId, long entityId, EntityKind entityKind) {
        String queryName = "topicRef.find" + entityKind.label() + "RefByEntityId";

        TypedQuery<TopicRef> query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(queryName))
                defineFindByEntityIdQuery(queryName, entityKind);
        }
        query = em.createNamedQuery(queryName, TopicRef.class);
        query.setParameter("topicId", topicId) //
            .setParameter("entityId", entityId);
        return Optional.of(query.getSingleResult());
    }

    @SuppressWarnings("unchecked")
    public Page<TopicRef> findByFilter(TopicRefQueryFilter filter, Pageable pageable) {
        FindQueryMetaData m = getFindQueryMetaData(filter, pageable);

        QueryPair queries = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.countQueryName) || !queryNames.contains(m.selectQueryName))
                queries = defineNamedQueries(filter, pageable, m);
        }
        if (queries == null) {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, TopicRef.class);
            queries = new QueryPair(countQuery, selectQuery);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasTopicId)
            params.put("topicId", filter.getTopicId());
        // if (m.hasEntityKind)
        //     params.put("entityKind", filter.getEntityKind());
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
