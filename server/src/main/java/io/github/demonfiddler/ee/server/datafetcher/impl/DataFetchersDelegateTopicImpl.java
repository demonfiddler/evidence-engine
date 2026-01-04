/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopic;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateTopicImpl extends DataFetchersDelegateILinkableEntityBaseImpl<Topic>
    implements DataFetchersDelegateTopic {

    @Resource
    private TopicRepository topicRepository;

    @Override
    public List<Topic> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return topicRepository.findAllById(keys);
    }

    @Override
    public Map<Topic, Topic> parent(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Topic> keys) {

        return entityUtils.getValuesMap(keys, Topic::getParent);
    }

    @Override
    public Map<Topic, List<Topic>> children(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Topic> keys) {

        return entityUtils.getListValuesMap(keys, Topic::getChildren);
    }

}
