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

package io.github.demonfiddler.ee.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * An abstract base class for {@code IBaseEntity} implementations.
 */
public abstract class AbstractBaseEntity extends AbstractGraphQLEntity implements IBaseEntity {

	/**
	 * The unique claim identifier.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The unique claim identifier.
	 */
	@Override
	@JsonIgnore
	public final void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique claim identifier.
	 */
	@Override
	@JsonIgnore
	public final Long getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		AbstractBaseEntity other = (AbstractBaseEntity)obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * The Builder that helps building instances of this POJO.
	 */
	@SuppressWarnings("unchecked")
	static abstract class Builder<B extends Builder<B, T>, T extends IBaseEntity>
		extends AbstractGraphQLEntity.Builder<B, T> {

		private Long id;

		/**
		 * The unique entity identifier.
		 */
		public final B withId(Long idParam) {
			this.id = idParam;
			return (B)this;
		}

		T build(T _object) {
			super.build(_object);
			_object.setId(this.id);
			return _object;
		}

	}

}
