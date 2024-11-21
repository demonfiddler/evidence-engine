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

package io.github.demonfiddler.ee.server.datafetcher.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.ITopicalEntity;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.model.TopicRefPage;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;
import io.github.demonfiddler.ee.server.repository.TopicRefRepository;
import jakarta.annotation.Resource;

/**
 * Base class containing implementations of common methods. It is necessary because the generated DataFetchersDelegate*
 * class hierachy does not reflect the inheritance expressed in the GraphQL schema.
 */
abstract class DataFetchersDelegateITopicalEntityBaseImpl<T extends /*IBaseEntity & ITrackedEntity &*/ ITopicalEntity>
    extends DataFetchersDelegateITrackedEntityBaseImpl/*<T>*/ {

    @Resource
    protected TopicRefRepository topicRefRepository;

    // public Object topicRefs(DataFetchingEnvironment dataFetchingEnvironment, T origin, TopicRefQueryFilter filter,
    // PageableInput pageSort) {
    protected Object _topicRefs(DataFetchingEnvironment dataFetchingEnvironment, ITopicalEntity origin,
        TopicRefQueryFilter filter, PageableInput pageSort) {
        filter = fixFilter((IBaseEntity)origin, filter);
        Pageable pageable = entityUtils.toPageable(pageSort);
        Page<TopicRef> page = topicRefRepository.findByFilter(filter, pageable);
        return entityUtils.toEntityPage(page, TopicRefPage::new);
    }

    private TopicRefQueryFilter fixFilter(IBaseEntity origin, TopicRefQueryFilter filter) {
        if (filter == null)
            filter = new TopicRefQueryFilter();
        filter.setEntityId(origin.getId());
        filter.setEntityKind(entityUtils.getEntityKind(origin.getClass()));
        return filter;
    }

}
