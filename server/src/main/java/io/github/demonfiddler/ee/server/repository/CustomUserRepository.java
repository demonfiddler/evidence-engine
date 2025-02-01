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

package io.github.demonfiddler.ee.server.repository;

import java.util.List;

import io.github.demonfiddler.ee.server.model.PermissionKind;
import io.github.demonfiddler.ee.server.model.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.User;

/**
 * A custom user repository that supports arbitrary filtering and user permission administration.
 */
public interface CustomUserRepository extends CustomRepository<User, TrackedEntityQueryFilter> {

    /**
     * Returns the permissions associated with the given user.
     * @param userId The user ID.
     * @return The user's permissions.
     */
    List<PermissionKind> findUserPermissions(Long userId);

    /**
     * Adds permissions to the specified user.
     * @param userId The user ID.
     * @param permissions The permissions to add.
     * @return the count of permissions added.
     */
    int addUserPermissions(Long userId, List<PermissionKind> permissions);

    /**
     * Removes permissions from the specified user.
     * @param userId The user ID.
     * @param permissions The permissions to remove.
     * @return the count of permissions removed.
     */
    int removeUserPermissions(Long userId, List<PermissionKind> permissions);

}
