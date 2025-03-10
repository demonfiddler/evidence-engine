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

package io.github.demonfiddler.ee.server.model;

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * An input for linking or unlinking two entities.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("LinkEntitiesInput")
public class LinkEntitiesInput {

	/**
	 * The ID of the 'linked from' entity.
	 */
	@GraphQLScalar(fieldName = "fromEntityId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long fromEntityId;

	/**
	 * The kind of the 'linked from' entity.
	 */
	@GraphQLScalar(fieldName = "fromEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind fromEntityKind;

	/**
	 * The ID of the 'linked to' entity.
	 */
	@GraphQLScalar(fieldName = "toEntityId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long toEntityId;

	/**
	 * The kind of the 'linked to' entity.
	 */
	@GraphQLScalar(fieldName = "toEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind toEntityKind;

	/**
	 * The ID of the 'linked from' entity.
	 */
	public void setFromEntityId(Long fromEntityId) {
		this.fromEntityId = fromEntityId;
	}

	/**
	 * The ID of the 'linked from' entity.
	 */
	public Long getFromEntityId() {
		return this.fromEntityId;
	}

	/**
	 * The kind of the 'linked from' entity.
	 */
	public void setFromEntityKind(EntityKind fromEntityKind) {
		this.fromEntityKind = fromEntityKind;
	}

	/**
	 * The kind of the 'linked from' entity.
	 */
	public EntityKind getFromEntityKind() {
		return this.fromEntityKind;
	}

	/**
	 * The ID of the 'linked to' entity.
	 */
	public void setToEntityId(Long toEntityId) {
		this.toEntityId = toEntityId;
	}

	/**
	 * The ID of the 'linked to' entity.
	 */
	public Long getToEntityId() {
		return this.toEntityId;
	}

	/**
	 * The kind of the 'linked to' entity.
	 */
	public void setToEntityKind(EntityKind toEntityKind) {
		this.toEntityKind = toEntityKind;
	}

	/**
	 * The kind of the 'linked to' entity.
	 */
	public EntityKind getToEntityKind() {
		return this.toEntityKind;
	}

	public String toString() {
		return "LinkEntitiesInput {" //$NON-NLS-1$
			+ "fromEntityId: " + this.fromEntityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "fromEntityKind: " + this.fromEntityKind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "toEntityId: " + this.toEntityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "toEntityKind: " + this.toEntityKind //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {
		private Long fromEntityId;
		private EntityKind fromEntityKind;
		private Long toEntityId;
		private EntityKind toEntityKind;

		/**
		 * The ID of the 'linked from' entity.
		 */
		public Builder withFromEntityId(Long fromEntityIdParam) {
			this.fromEntityId = fromEntityIdParam;
			return this;
		}

		/**
		 * The kind of the 'linked from' entity.
		 */
		public Builder withFromEntityKind(EntityKind fromEntityKindParam) {
			this.fromEntityKind = fromEntityKindParam;
			return this;
		}

		/**
		 * The ID of the 'linked to' entity.
		 */
		public Builder withToEntityId(Long toEntityIdParam) {
			this.toEntityId = toEntityIdParam;
			return this;
		}

		/**
		 * The kind of the 'linked to' entity.
		 */
		public Builder withToEntityKind(EntityKind toEntityKindParam) {
			this.toEntityKind = toEntityKindParam;
			return this;
		}

		public LinkEntitiesInput build() {
			LinkEntitiesInput _object = new LinkEntitiesInput();
			_object.setFromEntityId(this.fromEntityId);
			_object.setFromEntityKind(this.fromEntityKind);
			_object.setToEntityId(this.toEntityId);
			_object.setToEntityKind(this.toEntityKind);
			return _object;
		}
	}

}
