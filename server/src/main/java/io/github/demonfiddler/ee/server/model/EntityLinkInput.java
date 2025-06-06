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
@GraphQLInputType("EntityLinkInput")
public class EntityLinkInput extends AbstractBaseEntityInput {

	/**
	 * The ID of the 'linked from' entity.
	 */
	@GraphQLScalar(fieldName = "fromEntityId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long fromEntityId;

	/**
	 * The ID of the 'linked to' entity.
	 */
	@GraphQLScalar(fieldName = "toEntityId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long toEntityId;

	/**
	 * The locations within the 'linked-from' record, one per line.
	 */
	@GraphQLScalar(fieldName = "fromEntityLocations", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String fromEntityLocations;

	/**
	 * The locations within the 'linked-to' record, one per line.
	 */
	@GraphQLScalar(fieldName = "toEntityLocations", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String toEntityLocations;

	/**
	 * The ID of the 'linked-from' entity.
	 */
	public void setFromEntityId(Long fromEntityId) {
		this.fromEntityId = fromEntityId;
	}

	/**
	 * The ID of the 'linked-from' entity.
	 */
	public Long getFromEntityId() {
		return this.fromEntityId;
	}

	/**
	 * The locations within the 'linked-from' record, one per line.
	 */
	public void setFromEntityLocations(String locations) {
		this.fromEntityLocations = locations;
	}

	/**
	 * The locations within the 'linked-from' record, one per line.
	 */
	public String getFromEntityLocations() {
		return fromEntityLocations;
	}

	/**
	 * The ID of the 'linked-to' entity.
	 */
	public void setToEntityId(Long toEntityId) {
		this.toEntityId = toEntityId;
	}

	/**
	 * The ID of the 'linked-to' entity.
	 */
	public Long getToEntityId() {
		return this.toEntityId;
	}

	/**
	 * The locations within the 'linked-to' record, one per line.
	 */
	public void setToEntityLocations(String locations) {
		this.toEntityLocations = locations;
	}

	/**
	 * The locations within the 'linked-to' record, one per line.
	 */
	public String getToEntityLocations() {
		return toEntityLocations;
	}

	public String toString() {
		return "EntityLinkInput {" //$NON-NLS-1$
			+ "id: " + this.id  //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "fromEntityId: " + this.fromEntityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "toEntityId: " + this.toEntityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "fromEntityLocations: " + this.fromEntityLocations //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "toEntityLocations: " + this.toEntityLocations //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractBaseEntityInput.Builder<Builder, EntityLinkInput> {

		private Long fromEntityId;
		private String fromEntityLocations;
		private Long toEntityId;
		private String toEntityLocations;

		/**
		 * The ID of the 'linked from' entity.
		 */
		public Builder withFromEntityId(Long fromEntityIdParam) {
			this.fromEntityId = fromEntityIdParam;
			return this;
		}

		/**
		 * The locations within the 'linked-from' record, one per line.
		 */
		public Builder withFromEntityLocations(String locationsParam) {
			this.fromEntityLocations = locationsParam;
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
		 * The locations within the 'linked-to' record, one per line.
		 */
		public Builder withToEntityLocations(String locationsParam) {
			this.toEntityLocations = locationsParam;
			return this;
		}

		@Override
		public EntityLinkInput build() {
			EntityLinkInput _object = build(new EntityLinkInput());
			_object.setFromEntityId(this.fromEntityId);
			_object.setFromEntityLocations(this.fromEntityLocations);
			_object.setToEntityId(this.toEntityId);
			_object.setToEntityLocations(this.toEntityLocations);
			return _object;
		}

	}

}
