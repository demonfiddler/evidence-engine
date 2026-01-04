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
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateGroup;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.AuthorityKind;
import io.github.demonfiddler.ee.server.model.Group;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.GroupRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateGroupImpl extends DataFetchersDelegateITrackedEntityBaseImpl<Group>
    implements DataFetchersDelegateGroup {

    @Resource
    private GroupRepository groupRepository;

    @Override
    public List<Group> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return groupRepository.findAllById(keys);
    }

    @Override
    public Object authorities(DataFetchingEnvironment dataFetchingEnvironment, Group origin, FormatKind format) {
        List<AuthorityKind> authorities = origin.getAuthorities();
        return formatUtils.formatAuthorityKinds(authorities, format);
    }

    @Override
    public Map<Group, List<User>> members(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Group> keys) {

        return entityUtils.getListValuesMap(keys, Group::getMembers);
    }

}
