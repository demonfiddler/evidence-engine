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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import io.github.demonfiddler.ee.server.model.EntityStatistics;
import io.github.demonfiddler.ee.server.model.StatisticsQueryFilter;
import io.github.demonfiddler.ee.server.model.TopicStatisticsDto;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomStatisticsRepositoryImpl extends AbstractCustomRepositoryImpl implements StatisticsRepository {

    /** Describes the elements of a query. */
    private static record QueryMetaData(@Nullable StatisticsQueryFilter filter, String queryName, boolean hasStatus) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomStatisticsRepositoryImpl.class);

    @PersistenceContext
    EntityManager em;
    @Resource
    EntityUtils entityUtils;

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param resultClass The class of the results yielded by the query.
     * @param filter The query filter.
     * @return Query metadata.
     */
    @SuppressWarnings("null")
    private QueryMetaData getQueryMetaData(Class<?> resultClass, @Nullable StatisticsQueryFilter filter) {
        boolean hasFilter = filter != null;
        boolean hasStatus = hasFilter && filter.getStatus() != null && !filter.getStatus().isEmpty();

        StringBuilder queryName = new StringBuilder();
        StringBuilder[] queryNames = { queryName };
        queryName.append(resultClass.getSimpleName()).append(".get");
        if (hasStatus) {
            queryName.append("By");
            if (hasStatus)
                append("Status", queryNames);
        }

        return new QueryMetaData(filter, queryName.toString(), hasStatus);
    }

    /**
     * Defines a JPA named native query. N.B. This method requires external synchronisation.
     * @param queryName The query name.
     * @param sql The native SQL.
     * @param resultClass The class that the query returns.
     * @return The prepared query.
     */
    private Query defineNamedQuery(String queryName, String sql, String resultSetMapping) {
        Query query = em.createNativeQuery(sql, resultSetMapping);
        em.getEntityManagerFactory().addNamedQuery(queryName, query);
        queryNames.add(queryName);

        if (getLogger().isTraceEnabled())
            getLogger().trace("Defined query '{}' as:\n{}", queryName, sql);
        else
            getLogger().debug("Defined query '{}'", queryName);

        return query;
    }

    private Query defineEntityStatisticsQuery(QueryMetaData m) {
        /*
        SELECT "dtype" AS entityKind, COUNT(*) AS count
        FROM "entity" e
        WHERE e."status" IN (:status)
        GROUP BY entityKind
        UNION
        SELECT 'TLT', COUNT(*) FROM "entity" e
        JOIN "topic" t
        ON t."id" = e."id"
        WHERE
          e."status" IN (:status) AND
          t."parent_id" IS NULL
        ORDER BY "entityKind";
        */
        StringBuilder selectBuf = new StringBuilder();
        selectBuf.append("SELECT \"dtype\" AS entityKind, COUNT(*) AS count").append(NL) //
            .append("FROM \"entity\" e");
        if (m.hasStatus) {
            selectBuf.append(NL) //
                .append("WHERE e.\"status\" IN (:status)");
        }
        selectBuf.append(NL) //
            .append("GROUP BY entityKind").append(NL) //
            .append("UNION").append(NL) //
            .append("SELECT 'TLT', COUNT(*) FROM \"entity\" e").append(NL) //
            .append("JOIN \"topic\" t").append(NL) //
            .append("ON t.\"id\" = e.\"id\"").append(NL) //
            .append("WHERE");
        if (m.hasStatus) {
            selectBuf.append(NL) //
                .append("  e.\"status\" IN (:status) AND");
        }
        selectBuf.append(NL) //
            .append("  t.\"parent_id\" IS NULL").append(NL) //
            .append("ORDER BY \"entityKind\";");

        String selectSql = selectBuf.toString();

        return defineNamedQuery(m.queryName, selectSql, "EntityStatisticsMapping");
    }

    private Query defineTopicStatisticsQuery(QueryMetaData m) {
        /*
        SELECT te."id" AS topicId, e."dtype" AS entityKind, COUNT(*) AS count
        FROM "entity" te
        JOIN "entity" e
        JOIN "entity_link" el
        ON el."from_entity_id" = te."id" AND
           el."to_entity_id" = e."id"
        WHERE te."dtype" = 'TOP' AND
              te."status" IN (:status) AND
              e."status" IN (:status)
        GROUP BY te."id", entityKind;
        */

        StringBuilder selectBuf = new StringBuilder();
        selectBuf.append("SELECT te.\"id\" AS topicId, e.\"dtype\" AS entityKind, COUNT(*) AS count").append(NL) //
            .append("FROM \"entity\" te").append(NL) //
            .append("JOIN \"entity\" e").append(NL) //
            .append("JOIN \"entity_link\" el").append(NL) //
            .append("ON el.\"from_entity_id\" = te.\"id\" AND ").append(NL) //
            .append("  el.\"to_entity_id\" = e.\"id\"").append(NL) //
            .append("WHERE te.\"dtype\" = 'TOP'");
        if (m.hasStatus) {
            selectBuf.append(" AND").append(NL) //
                .append("  te.\"status\" IN (:status) AND").append(NL) //
                .append("  e.\"status\" IN (:status)");
        }
        selectBuf.append(NL) //
            .append("GROUP BY topicId, entityKind;");

        String selectSql = selectBuf.toString();

        return defineNamedQuery(m.queryName, selectSql, "TopicStatisticsDtoMapping");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EntityStatistics> getEntityStatistics(StatisticsQueryFilter filter) {
        QueryMetaData m = getQueryMetaData(EntityStatistics.class, filter);

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.queryName))
                query = defineEntityStatisticsQuery(m);
        }
        if (query == null)
            query = em.createNamedQuery(m.queryName, EntityStatistics.class);
        if (m.hasStatus)
            query.setParameter("status", m.filter.getStatus().stream().map(s -> s.name()).toList());

        return (List<EntityStatistics>)query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TopicStatisticsDto> getTopicStatistics(StatisticsQueryFilter filter) {
        QueryMetaData m = getQueryMetaData(TopicStatisticsDto.class, filter);

        Query query = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.queryName))
                query = defineTopicStatisticsQuery(m);
        }
        if (query == null)
            query = em.createNamedQuery(m.queryName, TopicStatisticsDto.class);
        if (m.hasStatus)
            query.setParameter("status", m.filter.getStatus().stream().map(s -> s.name()).toList());

        return (List<TopicStatisticsDto>)query.getResultList();
    }

}
