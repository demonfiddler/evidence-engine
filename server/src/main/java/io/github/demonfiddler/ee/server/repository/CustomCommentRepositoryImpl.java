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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import io.github.demonfiddler.ee.server.model.Comment;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.CommentQueryFilter;
import jakarta.persistence.Query;

/**
 * An implementation of the {@code CustomCommentRepository} interface.
 * @param <T> The type handled by the implementation.
 */
public class CustomCommentRepositoryImpl extends AbstractCustomRepositoryImpl implements CustomCommentRepository {

    /** Describes the elements of a query. */
    private static record QueryMetaData(@Nullable CommentQueryFilter filter, @NonNull Pageable pageable,
        String countQueryName, String selectQueryName, boolean hasStatus, boolean hasText, boolean hasTextH2,
        boolean hasTextMariaDB, boolean isAdvanced, boolean hasRecordId, boolean hasTargetKind, boolean hasTargetId,
        boolean hasParentId, boolean hasUserId, boolean hasFrom, boolean hasTo, boolean isPaged, boolean isSorted,
        boolean isSortedOnCreatedByUsername, boolean isSortedOnUpdatedByUsername) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCommentRepositoryImpl.class);

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns metadata about a query and paging/sorting specification.
     * @param filter The query filter.
     * @param pageable Specifies sorting and pagination, must not be {@code null}.
     * @return Query metadata.
     */
    @SuppressWarnings("null")
    private QueryMetaData getQueryMetaData(@Nullable CommentQueryFilter filter, @NonNull Pageable pageable) {
        boolean hasFilter = filter != null;
        boolean hasRecordId = hasFilter && filter.getRecordId() != null;
        boolean hasText = !hasRecordId && hasFilter && filter.getText() != null && !filter.getText().isEmpty();
        boolean hasTextH2 = hasText && profileUtils.isIntegrationTesting();
        boolean hasTextMariaDB = hasText && !profileUtils.isIntegrationTesting();
        boolean isAdvanced = hasText && filter.getAdvancedSearch() != null && filter.getAdvancedSearch();
        boolean hasStatus = !hasRecordId && hasFilter && filter.getStatus() != null && !filter.getStatus().isEmpty();
        boolean hasTargetKind = !hasRecordId && hasFilter && filter.getTargetKind() != null;
        boolean hasTargetId = !hasRecordId && hasFilter && filter.getTargetId() != null;
        boolean hasParentId = !hasRecordId && hasFilter && filter.getParentId() != null;
        boolean hasUserId = !hasRecordId && hasFilter && filter.getUserId() != null;
        boolean hasFrom = !hasRecordId && hasFilter && filter.getFrom() != null;
        boolean hasTo = !hasRecordId && hasFilter && filter.getTo() != null;
        boolean isPaged = !hasRecordId && pageable.isPaged();
        boolean isSorted = !hasRecordId && pageable.getSort().isSorted();
        boolean isSortedOnCreatedByUsername = false;
        boolean isSortedOnUpdatedByUsername = false;

        // Unauthenticated queries should only return published results.
        // Consider whether this is the best place to enforce the 'anonymous = Published only' policy.
        if (securityUtils.getCurrentUsername().equals("anonymousUser")) {
            if (filter == null)
                filter = new CommentQueryFilter();
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
        if (isSorted) {
            isSortedOnCreatedByUsername =
                !pageable.getSort().filter(o -> o.getProperty().equals("createdByUsername")).isEmpty();
            isSortedOnUpdatedByUsername =
                !pageable.getSort().filter(o -> o.getProperty().equals("updatedByUsername")).isEmpty();
        }

        StringBuilder countQueryName = new StringBuilder();
        StringBuilder selectQueryName = new StringBuilder();
        StringBuilder[] queryNames = { countQueryName, selectQueryName };
        append("comment", queryNames);
        countQueryName.append(".countBy");
        selectQueryName.append(".findBy");
        if (hasRecordId)
            append("Id", queryNames);
        if (hasStatus)
            append("Status", queryNames);
        if (hasText) {
            append("Text", queryNames);
            if (isAdvanced)
                append("Advanced", queryNames);
        }
        if (hasTargetKind || hasTargetId) {
            append("Target", queryNames);
            if (hasTargetKind)
                append("Kind", queryNames);
            if (hasTargetId)
                append("Id", queryNames);
        }
        if (hasParentId)
            append("Parent", queryNames);
        if (hasUserId)
            append("User", queryNames);
        if (hasFrom)
            append("From", queryNames);
        if (hasTo)
            append("To", queryNames);
        if (isSorted)
            entityUtils.appendOrderByToQueryName(selectQueryName, pageable);

        return new QueryMetaData(filter, pageable, countQueryName.toString(), selectQueryName.toString(), hasStatus,
            hasText, hasTextH2, hasTextMariaDB, isAdvanced, hasRecordId, hasTargetKind, hasTargetId, hasParentId,
            hasUserId, hasFrom, hasTo, isPaged, isSorted, isSortedOnCreatedByUsername, isSortedOnUpdatedByUsername);
    }

    /**
     * Defines and registers a pair of COUNT and SELECT named queries with the {@code EntityManagerFactory}.
     * @param m Query metadata.
     * @return An executable query pair.
     */
    private QueryPair defineNamedQueries(QueryMetaData m) {
        /*
        SELECT COUNT(*) | e."dtype", e."status", e."rating", e."created", e."created_by_user_id", e."updated", e."updated_by_user_id", ee.*
        FROM "entity" e
        JOIN "comment" ee
        ON ee."id" = e."id"
        -- if (m.isSortedOnCreatedByUsername) {
        JOIN "user" cbu
        ON cbu."id" = e."created_by_user_id"
        --}
        -- if (m.isSortedOnUpdatedByUsername) {
        JOIN "user" ubu
        ON ubu."id" = e."updated_by_user_id"
        --}
        -- if (m.hasTextH2) {
        JOIN FT_SEARCH_DATA(:text, 0, 0) ft
        ON ft.\"TABLE\" = 'comment'
            AND ft.\"KEYS\"[1] = e.\"id\"
        -- }
        -- if (m.hasTargetKind) {
        JOIN "entity" te
        ON te."id" = ee."target_id"
        -- }
        -- if (m.hasStatus || m.hasTextMariaDB || m.hasRecordId || m.hasTargetKind || m.hasTargetId || m.hasParentId ||
            m.hasUserId || m.hasFrom || m.hasTo) {
        WHERE
        -- if (m.hasRecordId) {
            e."id" = :recordId
        -- }
        -- if (m.hasStatus) {
            e."status" IN (:status)
        -- }
        -- if (m.hasTextMariaDB) {
            AND MATCH ("${getFulltextColumns()} AGAINST (:text"[ IN BOOLEAN MODE])
        -- }
        -- if (m.hasTargetKind) {
            AND te."dtype" = :targetKind
        -- }
        -- if (m.hasTargetId) {
            AND ee."target_id" = :targetId
        -- }
        -- if (m.hasParentId) {
            AND ee."parent_id" = :parentId
        -- }
        -- if (m.hasUserId) {
            AND e."created_by_user_id" = :userId
        -- }
        -- if (m.hasFrom) {
            AND (e."created" >= :from OR e."updated" IS NOT NULL AND e."updated" >= :from)
        -- }
        -- if (m.hasTo) {
            AND (e."created" <= :to OR e."updated" IS NOT NULL AND e."updated" <= :to)
        -- }
        -- if (m.isSorted) {
        ORDER BY ${m.pageable.getSort()}
        -- }
        -- }
        */
        StringBuilder selectBuf = new StringBuilder();
        selectBuf.append(NL) //
            .append("FROM \"entity\" e").append(NL) //
            .append("JOIN \"comment\" ee").append(NL) //
            .append("ON ee.\"id\" = e.\"id\"");
        if (m.isSortedOnCreatedByUsername) {
            selectBuf.append(NL) //
                .append("JOIN \"user\" cbu").append(NL) //
                .append("ON cbu.\"id\" = e.\"created_by_user_id\"");
        }
        if (m.isSortedOnUpdatedByUsername) {
            selectBuf.append(NL) //
                .append("JOIN \"user\" ubu").append(NL) //
                .append("ON ubu.\"id\" = e.\"updated_by_user_id\"");
        }
        if (m.hasTextH2) {
            selectBuf.append(NL) //
                .append("JOIN FT_SEARCH_DATA(:text, 0, 0) ft").append(NL) //
                .append("ON ft.\"TABLE\" = 'comment'").append(NL) //
                .append("    AND ft.\"KEYS\"[1] = e.\"id\"");
        }
        if (m.hasTargetKind) {
            selectBuf.append(NL) //
                .append("JOIN \"entity\" te").append(NL) //
                .append("ON te.\"id\" = e.\"id\"");
        }
        if (m.hasStatus || m.hasTextMariaDB || m.hasRecordId || m.hasTargetKind || m.hasTargetId || m.hasParentId
            || m.hasUserId || m.hasFrom || m.hasTo) {

            selectBuf.append(NL) //
                .append("WHERE");
            if (m.hasRecordId) {
                selectBuf.append(NL) //
                    .append("    e.\"id\" = :recordId");
            }
            boolean needsAnd = false;
            if (m.hasStatus) {
                selectBuf.append(NL) //
                    .append("    e.\"status\" IN (:status)");
                needsAnd = true;
            }
            if (m.hasTextMariaDB) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("MATCH (\"text\") AGAINST (:text");
                if (m.isAdvanced)
                    selectBuf.append(" IN BOOLEAN MODE");
                selectBuf.append(')');
                needsAnd = true;
            }
            if (m.hasTargetKind) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("te.\"dtype\" = :targetKind");
                needsAnd = true;
            }
            if (m.hasTargetId) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("ee.\"target_id\" = :targetId");
                needsAnd = true;
            }
            if (m.hasParentId) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("ee.\"parent_id\" = :parentId");
                needsAnd = true;
            }
            if (m.hasUserId) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("e.\"created_by_user_id\" = :userId");
                needsAnd = true;
            }
            if (m.hasFrom) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("(e.\"created\" >= :from OR e.\"updated\" IS NOT NULL AND e.\"updated\" >= :from)");
                needsAnd = true;
            }
            if (m.hasTo) {
                selectBuf.append(NL) //
                    .append("    ");
                if (needsAnd)
                    selectBuf.append("AND ");
                selectBuf.append("(e.\"created\" <= :to OR e.\"updated\" IS NOT NULL AND e.\"updated\" <= :to)");
                needsAnd = true;
            }
        }
        StringBuffer countBuf = new StringBuffer(selectBuf);
        countBuf.insert(0, "SELECT COUNT(*)");
        countBuf.append(';');
        selectBuf.insert(0,
            "SELECT e.\"dtype\", e.\"status\", e.\"rating\", e.\"created\", e.\"created_by_user_id\", e.\"updated\", e.\"updated_by_user_id\", ee.*");
        if (m.isSorted)
            entityUtils.appendOrderByClause(selectBuf, m.pageable, "e.", "ee.", "cbu.", "ubu.", true);
        selectBuf.append(';');

        // NOTE: since the COUNT query does not include an ORDER BY clause, multiple executions of the same SELECT query
        // with different ORDER BY clauses will result in the registration of multiple identical COUNT queries, each of
        // which will simply overwrite the previous definition. This is not a problem, but it is somewhat inefficient.
        String countSql = countBuf.toString();
        Query countQuery = defineNamedQuery(m.countQueryName, countSql, Long.class);

        String selectSql = selectBuf.toString();
        Query selectQuery = defineNamedQuery(m.selectQueryName, selectSql, Comment.class);

        return new QueryPair(countQuery, selectQuery);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<Comment> findByFilter(@Nullable CommentQueryFilter filter, Pageable pageable) {
        QueryMetaData m = getQueryMetaData(filter, pageable);

        QueryPair queries = null;
        synchronized (queryNames) {
            if (!queryNames.contains(m.countQueryName) || !queryNames.contains(m.selectQueryName))
                queries = defineNamedQueries(m);
        }
        if (queries == null) {
            Query countQuery = em.createNamedQuery(m.countQueryName, Long.class);
            Query selectQuery = em.createNamedQuery(m.selectQueryName, Comment.class);
            queries = new QueryPair(countQuery, selectQuery);
        }

        Map<String, Object> params = new HashMap<>();
        if (m.hasStatus)
            params.put("status", m.filter.getStatus().stream().map(s -> s.name()).toList());
        if (m.hasText)
            params.put("text", m.filter.getText());
        if (m.hasRecordId)
            params.put("recordId", m.filter.getRecordId());
        if (m.hasTargetKind)
            params.put("targetKind", m.filter.getTargetKind());
        if (m.hasTargetId)
            params.put("targetId", m.filter.getTargetId());
        if (m.hasParentId)
            params.put("parentId", m.filter.getParentId());
        if (m.hasUserId)
            params.put("userId", m.filter.getUserId());
        if (m.hasFrom)
            params.put("from", m.filter.getFrom());
        if (m.hasTo)
            params.put("to", m.filter.getTo());
        entityUtils.setQueryParameters(queries, params);
        if (m.isPaged)
            entityUtils.setQueryPagination(queries.selectQuery(), m.pageable);

        long total = (Long)queries.countQuery().getSingleResult();
        List<Comment> content = queries.selectQuery().getResultList();
        return new PageImpl<>(content, m.pageable, total);
    }

}
