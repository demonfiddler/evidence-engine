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

package io.github.demonfiddler.ee.server.datafetcher.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.data.domain.Pageable;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.PermissionKind;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Base class containing implementations of common methods. It is necessary because the generated DataFetchersDelegate*
 * class hierachy does not reflect the inheritance expressed in the GraphQL schema.
 */
abstract class DataFetchersDelegateITrackedEntityBaseImpl<T extends ITrackedEntity> {

    @Resource
    protected LogRepository logRepository;
    @Resource
    protected EntityUtils entityUtils;
    @Resource
    protected FormatUtils formatUtils;
    @Resource
    protected SecurityUtils securityUtils;
    @PersistenceContext
    EntityManager em;

    public final Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, T origin,
        FormatKind format) {

        EntityKind entityKind = EntityKind.valueOf(origin.getEntityKind());
        return formatUtils.formatEntityKind(entityKind, format);
    }

    public final Object status(DataFetchingEnvironment dataFetchingEnvironment, T origin, FormatKind format) {
        StatusKind status = StatusKind.valueOf(origin.getStatus());
        return formatUtils.formatStatusKind(status, format);
    }

    public final Map<T, User> createdByUser(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<T> keys) {

        return securityUtils.hasAuthority(PermissionKind.ADM) //
            ? entityUtils.getValuesMap(keys, ITrackedEntity::getCreatedByUser) //
            : Collections.emptyMap();
    }

    public final Map<T, User> updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<T> keys) {

        return securityUtils.hasAuthority(PermissionKind.ADM) //
            ? entityUtils.getValuesMap(keys, ITrackedEntity::getUpdatedByUser) : Collections.emptyMap();
    }

    public final LogPage log(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, LogPage> dataLoader,
        T origin, LogQueryFilter filter, PageableInput pageSort) {

        filter = fixFilter(origin, filter);
        Pageable pageable = entityUtils.toPageable(pageSort);
        return entityUtils.toEntityPage(logRepository.findByFilter(filter, pageable), LogPage::new);
    }

    /**
     * Adds {@code entityKind} and {@code entityId} to {@code params} (creating it if necessary).
     * @param entity The entity (must not be null).
     * @param filter The query filter (can be null).
     * @return A new or updated log params.
     */
    protected LogQueryFilter fixFilter(ITrackedEntity entity, LogQueryFilter filter) {
        if (filter == null)
            filter = new LogQueryFilter();
        EntityKind entityKind = entityUtils.getEntityKind(entity);
        filter.setEntityId(entity.getId());
        filter.setEntityKind(entityKind);
        return filter;
    }

}
