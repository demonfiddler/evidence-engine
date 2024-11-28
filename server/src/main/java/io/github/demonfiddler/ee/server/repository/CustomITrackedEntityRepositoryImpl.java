/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
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
// import org.springframework.transaction.annotation.Transactional;

import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * An abstract base implementation of the {@code CustomRepository} interface.
 * @param <T> The type handled by the implementation.
 */
public abstract class CustomITrackedEntityRepositoryImpl<T extends IBaseEntity & ITrackedEntity>
    implements CustomRepository<T, TrackedEntityQueryFilter> {

    /** Describes the elements of a query. */
    static record QueryMetaData(String countQueryName, String selectQueryName, String entityName, boolean hasStatus,
        boolean hasText, boolean isAdvanced, boolean isPaged, boolean isSorted) {
    }

    private static Logger logger = LoggerFactory.getLogger(CustomITrackedEntityRepositoryImpl.class);

    @PersistenceContext
    protected EntityManager em;
    @Resource
    protected EntityUtils entityUtils;

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
    private QueryMetaData getQueryMetaData(@NonNull TrackedEntityQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasText = filter.getText() != null && !filter.getText().isEmpty();
        boolean isAdvanced = hasText && filter.getAdvancedSearch();
        boolean hasStatus = filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();
        String entityName = entityUtils.getEntityName(getEntityClass());

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        countQueryName.append(entityName).append(".countBy");
        selectQueryName.append(entityName).append(".findBy");
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
        if (isSorted) {
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);
        }

        return new QueryMetaData(countQueryName.toString(), selectQueryName.toString(), entityName, hasStatus, hasText,
            isAdvanced, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of COUNT and SELECT named queries with the {@code EntityManagerFactory}.
     * @param filter The query parameters, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(TrackedEntityQueryFilter filter, Pageable pageable, QueryMetaData m) {
        StringBuilder selectBuf = new StringBuilder();
        selectBuf.append(NL).append("FROM \"").append(m.entityName).append("\"");
        boolean needsAnd = false;
        if (m.hasText || m.hasStatus) {
            selectBuf.append(NL).append("WHERE").append(NL).append("    ");
            if (m.hasText) {
                selectBuf.append("MATCH (").append(getFulltextColumns()).append(") AGAINST (:text");
                if (m.isAdvanced)
                    selectBuf.append(" IN BOOLEAN MODE");
                selectBuf.append(')');
                needsAnd = true;
            }
            if (m.hasStatus) {
                if (needsAnd) {
                    selectBuf.append(NL).append("    AND ");
                }
                selectBuf.append("\"status\" IN (:status)");
            }
        }
        StringBuffer countBuf = new StringBuffer(selectBuf);
        countBuf.insert(0, "SELECT COUNT(*)");
        countBuf.append(';');
        selectBuf.insert(0, "SELECT *");
        if (m.isSorted)
            entityUtils.appendOrderByClause(selectBuf, pageable, "", true);
        selectBuf.append(';');

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT query
        // with different ORDER BY clauses will result in the registration of multiple identical COUNT queries, each of
        // which will simply overwrite the previous definition. This is not a problem, but it is somewhat inefficient.
        String countSql = countBuf.toString();
        Query countQuery = em.createNativeQuery(countSql, Long.class);
        em.getEntityManagerFactory().addNamedQuery(m.countQueryName, countQuery);
        logger.debug("Defined query '{}' as:\n{}", m.countQueryName, countSql);

        String selectSql = selectBuf.toString();
        Query selectQuery = em.createNativeQuery(selectSql, getEntityClass());
        em.getEntityManagerFactory().addNamedQuery(m.selectQueryName, selectQuery);
        logger.debug("Defined query '{}' as:\n{}", m.selectQueryName, selectSql);

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    // @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Page<T> findByFilter(@NonNull TrackedEntityQueryFilter filter, @NonNull Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries;
        try {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, getEntityClass());
            queries = new QueryPair(countQuery, selectQuery);
        } catch (IllegalArgumentException e) {
            queries = defineNamedQueries(filter, pageable, m);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasText)
            params.put("text", filter.getText());
        if (m.hasStatus)
            params.put("status", filter.getStatus().stream().map(s -> s.name()).toList());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<T> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, pageable, total);
    }

}
