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

package io.github.demonfiddler.ee.server.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * A request for counting entities. It is used when the client requests only the total number of matching records without actually fetching them.
 */
public class CountRequest implements Countable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /** The singleton instance of the count request. */
    public static final CountRequest INSTANCE = new CountRequest();

	private CountRequest() {
	}

	@Override
	public boolean isPaged() {
		return false;
	}

	@Override
	public Pageable previousOrFirst() {
		return this;
	}

	@Override
	public Pageable next() {
		return this;
	}

	@Override
	public boolean hasPrevious() {
		return false;
	}

	@Override
	public Sort getSort() {
		return Sort.unsorted();
	}

	@Override
	public int getPageSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageNumber() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getOffset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Pageable first() {
		return this;
	}

	@Override
	public Countable withPage(int pageNumber) {
		if (pageNumber == 0)
			return this;

		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object o) {
        return o == this;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

}
