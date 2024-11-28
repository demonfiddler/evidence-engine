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
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateLog;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.Log;
import io.github.demonfiddler.ee.server.model.TransactionKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateLogImpl implements DataFetchersDelegateLog {

    @Resource
    private LogRepository logRepository;
    @Resource
    private EntityUtils entityUtils;
    @Resource
    private FormatUtils formatUtils;

    public List<Log> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return logRepository.findByIds(keys);
    }

    @Override
    public Map<Log, User> user(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Log> keys) {

        return entityUtils.getValuesMap(keys, Log::getUser);
    }

    @Override
    public Object transactionKind(DataFetchingEnvironment dataFetchingEnvironment, Log origin, FormatKind format) {
        TransactionKind kind = TransactionKind.valueOf(origin.getTransactionKind());
        return formatUtils.formatTransactionKind(kind, format);
    }

    @Override
    public Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, Log origin, FormatKind format) {
        EntityKind kind = EntityKind.valueOf(origin.getEntityKind());
        return formatUtils.formatEntityKind(kind, format);
    }

    @Override
    public Object linkedEntityKind(DataFetchingEnvironment dataFetchingEnvironment, Log origin, FormatKind format) {
        String linkedEntityKind = origin.getLinkedEntityKind();
        if (linkedEntityKind != null) {
            EntityKind kind = EntityKind.valueOf(linkedEntityKind);
            return formatUtils.formatEntityKind(kind, format);
        }
        return null;
    }

}
