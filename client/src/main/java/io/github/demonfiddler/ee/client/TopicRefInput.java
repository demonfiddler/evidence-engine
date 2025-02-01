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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

import io.github.demonfiddler.ee.client.util.CustomJacksonSerializers;

/**
 * An input for creating or updating an association between a topic and an entity record.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("TopicRefInput")
@JsonInclude(Include.NON_NULL)
public class TopicRefInput {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public TopicRefInput() {
	}

	/**
	 * The unique topic identifier. Only necessary because graphql-java-generator emits code with errors (missing ctor)
	 * if there is no ID field.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The unique identifier of the associated topic.
	 */
	@JsonProperty("topicId")
	@JsonSerialize(using = CustomJacksonSerializers.Long.class)
	@GraphQLScalar(fieldName = "topicId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long topicId;

	/**
	 * The associated entity kind.
	 */
	@JsonProperty("entityKind")
	@GraphQLScalar(fieldName = "entityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind entityKind;

	/**
	 * The unique identifier of the associated entity.
	 */
	@JsonProperty("entityId")
	@JsonSerialize(using = CustomJacksonSerializers.Long.class)
	@GraphQLScalar(fieldName = "entityId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long entityId;

	/**
	 * The locations within the associated record, one per line.
	 */
	@JsonProperty("locations")
	@JsonSerialize(using = CustomJacksonSerializers.ListURI.class)
	@GraphQLScalar(fieldName = "locations", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String locations;

	/**
	 * The unique topic identifier. Only necessary because graphql-java-generator emits code with errors (missing ctor)
	 * if there is no ID field.
	 */
	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique topic identifier. Only necessary because graphql-java-generator emits code with errors (missing ctor)
	 * if there is no ID field.
	 */
	@JsonProperty("id")
	public Long getId() {
		return this.id;
	}

	/**
	 * The unique identifier of the associated topic.
	 */
	@JsonProperty("topicId")
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	/**
	 * The unique identifier of the associated topic.
	 */
	@JsonProperty("topicId")
	public Long getTopicId() {
		return this.topicId;
	}

	/**
	 * The associated entity kind.
	 */
	@JsonProperty("entityKind")
	public void setEntityKind(EntityKind entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * The associated entity kind.
	 */
	@JsonProperty("entityKind")
	public EntityKind getEntityKind() {
		return this.entityKind;
	}

	/**
	 * The unique identifier of the associated entity.
	 */
	@JsonProperty("entityId")
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/**
	 * The unique identifier of the associated entity.
	 */
	@JsonProperty("entityId")
	public Long getEntityId() {
		return this.entityId;
	}

	/**
	 * The locations within the associated record, one per line.
	 */
	@JsonProperty("locations")
	public void setLocations(String locations) {
		this.locations = locations;
	}

	/**
	 * The locations within the associated record, one per line.
	 */
	@JsonProperty("locations")
	public String getLocations() {
		return this.locations;
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
		return "TopicRefInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "topicId: " + this.topicId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entityKind: " + this.entityKind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entityId: " + this.entityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "locations: " + this.locations //$NON-NLS-1$
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

		private Long id;
		private Long topicId;
		private EntityKind entityKind;
		private Long entityId;
		private String locations;

		/**
		 * The unique topic identifier. Only necessary because graphql-java-generator emits code with errors (missing
		 * ctor) if there is no ID field.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The unique identifier of the associated topic.
		 */
		public Builder withTopicId(Long topicIdParam) {
			this.topicId = topicIdParam;
			return this;
		}

		/**
		 * The associated entity kind.
		 */
		public Builder withEntityKind(EntityKind entityKindParam) {
			this.entityKind = entityKindParam;
			return this;
		}

		/**
		 * The unique identifier of the associated entity.
		 */
		public Builder withEntityId(Long entityIdParam) {
			this.entityId = entityIdParam;
			return this;
		}

		/**
		 * The locations within the associated record, one per line.
		 */
		public Builder withLocations(String locationsParam) {
			this.locations = locationsParam;
			return this;
		}

		public TopicRefInput build() {
			TopicRefInput _object = new TopicRefInput();
			_object.setId(this.id);
			_object.setTopicId(this.topicId);
			_object.setEntityKind(this.entityKind);
			_object.setEntityId(this.entityId);
			_object.setLocations(this.locations);
			return _object;
		}

	}

}
