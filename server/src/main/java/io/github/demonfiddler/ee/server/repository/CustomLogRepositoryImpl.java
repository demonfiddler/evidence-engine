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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import io.github.demonfiddler.ee.server.model.Log;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import jakarta.persistence.Query;

/**
 * A custom Log repository implementation that supports arbitrary filtering.
 */
@Transactional
public class CustomLogRepositoryImpl extends AbstractCustomRepositoryImpl implements CustomLogRepository {

    /** Describes the elements of a query. */
    static record QueryMetaData(@Nullable LogQueryFilter filter, @NonNull Pageable pageable, String countQueryName,
        String selectQueryName, boolean hasEntityId, boolean hasEntityKind, boolean hasUserId,
        boolean hasTransactionKinds, boolean hasFrom, boolean hasTo, boolean isPaged, boolean isSorted) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogRepositoryImpl.class);

    Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    private QueryMetaData getQueryMetaData(@NonNull LogQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasEntityId = filter.getEntityId() != null;
        boolean hasEntityKind = filter.getEntityKind() != null;
        boolean hasUserId = filter.getUserId() != null;
        boolean hasTransactionKinds = filter.getTransactionKinds() != null && !filter.getTransactionKinds().isEmpty();
        boolean hasFrom = filter.getFrom() != null;
        boolean hasTo = filter.getTo() != null;
        boolean isPaged = pageable.isPaged();
        boolean isSorted = pageable.getSort().isSorted();

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        StringBuilder[] queryNames = { countQueryName, selectQueryName };
        append("log.", queryNames);
        countQueryName.append("countBy");
        selectQueryName.append("findBy");
        if (hasEntityKind || hasEntityId) {
            append("Entity", queryNames);
            if (hasEntityKind)
                append("Kind", queryNames);
            if (hasEntityId)
                append("Id", queryNames);
        }
        if (hasUserId)
            append("User", queryNames);
        if (hasTransactionKinds)
            append("TxnKind", queryNames);
        if (hasFrom)
            append("From", queryNames);
        if (hasTo)
            append("To", queryNames);
        if (isSorted) {
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);
        }

        return new QueryMetaData(filter, pageable, countQueryName.toString(), selectQueryName.toString(), hasEntityId,
            hasEntityKind, hasUserId, hasTransactionKinds, hasFrom, hasTo, isPaged, isSorted);
    }

    /**
     * Defines and registers a pair of named queries with the {@code EntityManagerFactory}.
     * @param filter The query filter, must not be {@code null}.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(QueryMetaData m) {
        StringBuilder selectBuf = new StringBuilder();
        selectBuf.append(" FROM \"log\"");
        boolean needsAnd = false;
        if (m.hasEntityId || m.hasEntityKind || m.hasUserId || m.hasTransactionKinds || m.hasFrom || m.hasTo) {
            selectBuf.append(" WHERE");
        }
        if (m.hasEntityId) {
            selectBuf.append(" \"entity_id\" = :entityId");
            needsAnd = true;
        }
        if (m.hasEntityKind) {
            if (needsAnd)
                selectBuf.append(" AND");
            selectBuf.append(" \"entity_kind\" = :entityKind");
            needsAnd = true;
        }
        if (m.hasUserId) {
            if (needsAnd)
                selectBuf.append(" AND");
            selectBuf.append(" \"user_id\" = :userId");
            needsAnd = true;
        }
        if (m.hasTransactionKinds) {
            if (needsAnd)
                selectBuf.append(" AND");
            selectBuf.append(" \"transaction_kind\" IN (:transactionKinds)");
            needsAnd = true;
        }
        if (m.hasFrom) {
            if (needsAnd)
                selectBuf.append(" AND");
            selectBuf.append(" \"from\" >= :from");
            needsAnd = true;
        }
        if (m.hasTo) {
            if (needsAnd)
                selectBuf.append(" AND");
            selectBuf.append(" \"to\" <= :to");
            needsAnd = true;
        }
        StringBuffer countBuf = new StringBuffer(selectBuf);
        countBuf.insert(0, "SELECT COUNT(*)");
        countBuf.append(';');
        selectBuf.insert(0, "SELECT *");
        if (m.isSorted)
            entityUtils.appendOrderByClause(selectBuf, m.pageable, "", "", false);

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT query
        // with different ORDER BY clauses will result in the registration of multiple identical COUNT queries, each of
        // which will simply overwrite the previous definition. This is not a problem, but it is somewhat inefficient.
        String countSql = countBuf.toString();
        Query countQuery = defineNamedQuery(m.countQueryName, countSql, Long.class);

        String selectSql = selectBuf.toString();
        Query selectQuery = defineNamedQuery(m.selectQueryName, selectSql, Log.class);

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<Log> findByFilter(@Nullable LogQueryFilter filter, @NonNull Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.countQueryName) || !queryNames.contains(m.selectQueryName))
                queries = defineNamedQueries(m);
        }
        if (queries == null) {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, Log.class);
            queries = new QueryPair(countQuery, selectQuery);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasEntityId)
            params.put("entityId", m.filter.getEntityId());
        if (m.hasEntityKind)
            params.put("entityKind", m.filter.getEntityKind().name());
        if (m.hasUserId)
            params.put("userId", m.filter.getUserId());
        if (m.hasTransactionKinds)
            params.put("transactionKinds", m.filter.getTransactionKinds().stream().map(t -> t.name()).toList());
        if (m.hasFrom)
            params.put("from", m.filter.getFrom().toInstant().toEpochMilli());
        if (m.hasTo)
            params.put("to", m.filter.getTo().toInstant().toEpochMilli());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), m.pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<Log> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, m.pageable, total);
    }

}
