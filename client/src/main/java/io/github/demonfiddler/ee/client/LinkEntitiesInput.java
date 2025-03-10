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

package io.github.demonfiddler.ee.client;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

/**
 * An input for linking or unlinking two entities.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("LinkEntitiesInput")
@JsonInclude(Include.NON_NULL)
public class LinkEntitiesInput {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public LinkEntitiesInput() {
	}

	/**
	 * The ID of the 'linked from' entity.
	 */
	@JsonProperty("fromEntityId")
	@GraphQLScalar(fieldName = "fromEntityId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long fromEntityId;

	/**
	 * The kind of the 'linked from' entity.
	 */
	@JsonProperty("fromEntityKind")
	@GraphQLScalar(fieldName = "fromEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind fromEntityKind;

	/**
	 * The ID of the 'linked to' entity.
	 */
	@JsonProperty("toEntityId")
	@GraphQLScalar(fieldName = "toEntityId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long toEntityId;

	/**
	 * The kind of the 'linked to' entity.
	 */
	@JsonProperty("toEntityKind")
	@GraphQLScalar(fieldName = "toEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind toEntityKind;

	/**
	 * The ID of the 'linked from' entity.
	 */
	@JsonProperty("fromEntityId")
	public void setFromEntityId(Long fromEntityId) {
		this.fromEntityId = fromEntityId;
	}

	/**
	 * The ID of the 'linked from' entity.
	 */
	@JsonProperty("fromEntityId")
	public Long getFromEntityId() {
		return this.fromEntityId;
	}

	/**
	 * The kind of the 'linked from' entity.
	 */
	@JsonProperty("fromEntityKind")
	public void setFromEntityKind(EntityKind fromEntityKind) {
		this.fromEntityKind = fromEntityKind;
	}

	/**
	 * The kind of the 'linked from' entity.
	 */
	@JsonProperty("fromEntityKind")
	public EntityKind getFromEntityKind() {
		return this.fromEntityKind;
	}

	/**
	 * The ID of the 'linked to' entity.
	 */
	@JsonProperty("toEntityId")
	public void setToEntityId(Long toEntityId) {
		this.toEntityId = toEntityId;
	}

	/**
	 * The ID of the 'linked to' entity.
	 */
	@JsonProperty("toEntityId")
	public Long getToEntityId() {
		return this.toEntityId;
	}

	/**
	 * The kind of the 'linked to' entity.
	 */
	@JsonProperty("toEntityKind")
	public void setToEntityKind(EntityKind toEntityKind) {
		this.toEntityKind = toEntityKind;
	}

	/**
	 * The kind of the 'linked to' entity.
	 */
	@JsonProperty("toEntityKind")
	public EntityKind getToEntityKind() {
		return this.toEntityKind;
	}

	/**
	 * This method is called during the json deserialization process, by the {@link GraphQLObjectMapper}, each time an
	 * alias value is read from the json.
	 * @param aliasName
	 * @param aliasDeserializedValue
	 */
	public void setAliasValue(String aliasName, Object aliasDeserializedValue) {
		this.aliasValues.put(aliasName, aliasDeserializedValue);
	}

	/**
	 * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
	 * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
	 * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
	 * this method with a basis json deserialization, but this value won't be the proper custom scalar value.
	 * @param alias
	 * @return
	 */
	public Object getAliasValue(String alias) {
		return this.aliasValues.get(alias);
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
