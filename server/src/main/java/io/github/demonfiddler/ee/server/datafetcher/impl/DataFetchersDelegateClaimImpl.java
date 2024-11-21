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

import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateClaim;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.TopicRefPage;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

@Component
public class DataFetchersDelegateClaimImpl extends DataFetchersDelegateITopicalEntityBaseImpl<Claim>
    implements DataFetchersDelegateClaim {

    @Resource
    private ClaimRepository claimRepository;

    @Override
    public List<Claim> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return claimRepository.findByIds(keys);
    }

    @Override
    public Object status(DataFetchingEnvironment dataFetchingEnvironment, Claim origin, FormatKind format) {
        return _status(dataFetchingEnvironment, origin, format);
    }

    @Override
    public Flux<User> createdByUser(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Claim> keys) {

        return _createdByUser(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Flux<User> updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Claim> keys) {

        return _updatedByUser(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Object log(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, LogPage> dataLoader,
        Claim origin, LogQueryFilter filter, PageableInput pageSort) {

        return _log(dataFetchingEnvironment, origin, filter, pageSort);
    }

    @Override
    public Object topicRefs(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, TopicRefPage> dataLoader,
        Claim origin, TopicRefQueryFilter filter, PageableInput pageSort) {

        return _topicRefs(dataFetchingEnvironment, origin, filter, pageSort);
    }

}
