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
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateJournal;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.repository.CommentRepository;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;

@Component
public class DataFetchersDelegateJournalImpl extends DataFetchersDelegateITrackedEntityBaseImpl<Journal>
    implements DataFetchersDelegateJournal {

    private final JournalRepository journalRepository;

    public DataFetchersDelegateJournalImpl(CommentRepository commentRepository, LogRepository logRepository,
        EntityUtils entityUtils, FormatUtils formatUtils, SecurityUtils securityUtils,
        JournalRepository journalRepository) {

        super(commentRepository, logRepository, entityUtils, formatUtils, securityUtils);
        this.journalRepository = journalRepository;
    }

    @Override
    public List<Journal> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return journalRepository.findAllById(keys);
    }

    @SuppressWarnings("null")
    @Override
    public Map<Journal, Publisher> publisher(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Journal> keys) {

        return entityUtils.getValuesMap(keys, Journal::getPublisher);
    }

}
