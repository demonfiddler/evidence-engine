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
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateJournal;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateJournalImpl extends DataFetchersDelegateITrackedEntityBaseImpl
    implements DataFetchersDelegateJournal {

    @Resource
    private JournalRepository journalRepository;

    @Override
    public List<Journal> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return journalRepository.findByIds(keys);
    }

    @Override
    public Object status(DataFetchingEnvironment dataFetchingEnvironment, Journal origin, FormatKind format) {
        return _status(dataFetchingEnvironment, origin, format);
    }

    @Override
    public Map<Journal, User> createdByUser(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Journal> keys) {

        return _createdByUserMap(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Map<Journal, User> updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Journal> keys) {

        return _updatedByUserMap(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Object log(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, LogPage> dataLoader,
        Journal origin, LogQueryFilter filter, PageableInput pageSort) {

        return _log(dataFetchingEnvironment, origin, filter, pageSort);
    }

    @Override
    public Map<Journal, Publisher> publisher(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Journal> keys) {

        return entityUtils.getValuesMap(keys, Journal::getPublisher);
    }

}
