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

package io.github.demonfiddler.ee.server.datafetcher.impl;

import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.data.domain.Pageable;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Base class containing implementations of common methods. It is necessary because the generated DataFetchersDelegate*
 * class hierachy does not reflect the inheritance expressed in the GraphQL schema.
 */
abstract class DataFetchersDelegateITrackedEntityBaseImpl {

    @Resource
    protected LogRepository logRepository;
    @Resource
    protected EntityUtils entityUtils;
    @Resource
    protected FormatUtils formatUtils;
    @PersistenceContext
    EntityManager em;

    protected Object _status(DataFetchingEnvironment dataFetchingEnvironment, ITrackedEntity origin,
        FormatKind format) {
        StatusKind status = StatusKind.valueOf(origin.getStatus());
        return formatUtils.formatStatusKind(status, format);
    }

    // protected Flux<User> _createdByUser(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
    // List<? extends ITrackedEntity> keys) {

    // return entityUtils.getValues(keys, ITrackedEntity::getUpdatedByUser);
    // }

    protected <T extends ITrackedEntity> Map<T, User> _createdByUserMap(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<T> keys) {

        return entityUtils.getValuesMap(keys, ITrackedEntity::getCreatedByUser);
    }

    // protected Flux<User> _updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
    // List<? extends ITrackedEntity> keys) {

    // return entityUtils.getValues(keys, ITrackedEntity::getUpdatedByUser);
    // }

    protected <T extends ITrackedEntity> Map<T, User> _updatedByUserMap(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<T> keys) {

        return entityUtils.getValuesMap(keys, ITrackedEntity::getUpdatedByUser);
    }

    protected LogPage _log(DataFetchingEnvironment dataFetchingEnvironment, IBaseEntity origin, LogQueryFilter filter,
        PageableInput pageSort) {
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
    protected LogQueryFilter fixFilter(IBaseEntity entity, LogQueryFilter filter) {
        if (filter == null)
            filter = new LogQueryFilter();
        EntityKind entityKind = entityUtils.getEntityKind(entity.getClass());
        filter.setEntityId(entity.getId());
        filter.setEntityKind(entityKind);
        return filter;
    }

}
