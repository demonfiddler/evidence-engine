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
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateQuotation;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.Quotation;
import io.github.demonfiddler.ee.server.model.TopicRefPage;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateQuotationImpl extends DataFetchersDelegateITopicalEntityBaseImpl<Quotation>
    implements DataFetchersDelegateQuotation {

    @Resource
    private QuotationRepository quotationRepository;

    @Override
    public List<Quotation> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return quotationRepository.findByIds(keys);
    }

    @Override
    public Object status(DataFetchingEnvironment dataFetchingEnvironment, Quotation origin, FormatKind format) {
        return _status(dataFetchingEnvironment, origin, format);
    }

    @Override
    public Map<Quotation, User> createdByUser(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Quotation> keys) {

        return _createdByUserMap(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Map<Quotation, User> updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Quotation> keys) {

        return _updatedByUserMap(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Object log(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, LogPage> dataLoader,
        Quotation origin, LogQueryFilter filter, PageableInput pageSort) {

        return _log(dataFetchingEnvironment, origin, filter, pageSort);
    }

    @Override
    public Object topicRefs(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, TopicRefPage> dataLoader,
        Quotation origin, TopicRefQueryFilter filter, PageableInput pageSort) {

        return _topicRefs(dataFetchingEnvironment, origin, filter, pageSort);
    }

}
