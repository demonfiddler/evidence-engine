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

import java.util.Collections;
import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateUser;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.PermissionKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.UserRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateUserImpl extends DataFetchersDelegateITrackedEntityBaseImpl<User>
    implements DataFetchersDelegateUser {

    @Resource
    private UserRepository userRepository;

    @Override
    public List<User> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return userRepository.findAllById(keys);
    }

    @Override
    public Object permissions(DataFetchingEnvironment dataFetchingEnvironment, User origin, FormatKind format) {
        // TODO: fetch permissions from user_authority and group_authority tables
        List<String> permissionStrings = origin.getPermissions();
        List<PermissionKind> permissions = //
            permissionStrings == null //
                ? Collections.emptyList() //
                : permissionStrings.stream().map(s -> PermissionKind.valueOf(s)).toList();
        return formatUtils.formatPermissionKinds(permissions, format);
    }

}
