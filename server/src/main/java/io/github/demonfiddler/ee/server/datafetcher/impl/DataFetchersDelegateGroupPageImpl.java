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

import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateGroupPage;
import io.github.demonfiddler.ee.server.model.Group;
import io.github.demonfiddler.ee.server.model.GroupPage;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateGroupPageImpl implements DataFetchersDelegateGroupPage {

    @Resource
    private EntityUtils entityUtils;

    @Override
    public Map<GroupPage, List<Group>> content(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<GroupPage> keys) {

        return entityUtils.getListValuesMap(keys, GroupPage::getContent);
    }

}
