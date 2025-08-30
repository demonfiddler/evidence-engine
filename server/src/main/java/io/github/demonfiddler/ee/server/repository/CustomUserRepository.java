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
import java.util.Optional;

import io.github.demonfiddler.ee.server.model.AuthorityKind;
import io.github.demonfiddler.ee.server.model.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.User;

/**
 * A custom user repository that supports arbitrary filtering and user authority administration.
 */
public interface CustomUserRepository extends CustomRepository<User, TrackedEntityQueryFilter> {

    /**
     * Returns all authorities associated with the given user, including those inherited via group memberships.
     * @param userId The user ID.
     * @return The user's authorities.
     */
    List<AuthorityKind> findAllUserAuthorities(Long userId);

    /**
     * Returns the currently logged-in user, if any.
     * @return the currently logged-in user, empty if unauthenticated.
     */
    Optional<User> getCurrentUser();

}
