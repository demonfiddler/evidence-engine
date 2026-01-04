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

package io.github.demonfiddler.ee.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * A custom repository that supports query filtering, pagination and/or sorting.
 * @param <T> The entity type managed by the repository.
 * @param <F> The filter type supported by the repository.
 */
public interface CustomRepository<T, F> {

    /**
     * Finds entities using a filter, with pagination and/or sorting.
     * @param filter The filter to apply.
     * @param pageable How to paginate/sort the results.
     * @return A pageful of matching entities.
     */
    Page<T> findByFilter(F filter, Pageable pageable);

}
