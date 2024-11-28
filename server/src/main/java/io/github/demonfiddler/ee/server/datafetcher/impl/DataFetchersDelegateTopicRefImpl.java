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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopicRef;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.ITopicalEntity;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.repository.LinkRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateTopicRefImpl implements DataFetchersDelegateTopicRef {

    @Resource
    private LinkRepository linkRepository;
    @Resource
    private EntityUtils entityUtils;
    @Resource
    private FormatUtils formatUtils;

    @Override
    public List<TopicRef> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return linkRepository.findByIds(keys);
    }

    @Override
    public Map<TopicRef, Topic> topic(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<TopicRef> keys) {

        return entityUtils.getValuesMap(keys, TopicRef::getTopic);
    }

    @Override
    public Map<TopicRef, ITopicalEntity> entity(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<TopicRef> keys) {

        // FIXME: JPA cannot handle this property!
        return entityUtils.getValuesMap(keys, TopicRef::getEntity);
    }

    @Override
    public Map<TopicRef, List<URI>> locations(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<TopicRef> keys) {

        return entityUtils.getListValuesMap(keys, TopicRef::getLocations);
    }

    @Override
    public Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, TopicRef origin, FormatKind format) {
        EntityKind kind = EntityKind.valueOf(origin.getEntityKind());
        return formatUtils.formatEntityKind(kind, format);
    }

}
