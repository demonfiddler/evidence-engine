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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * An association between a topic and an entity record.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("TopicRef")
@JsonInclude(Include.NON_NULL)
public class TopicRef implements IBaseEntity {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public TopicRef() {
	}

	// NOTE: only necessary because graphql-java-generator emits code with errors (missing ctor) if there is no ID
	// field.
	/**
	 * The unique topic ref identifier.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The unique identifier of the associated entity record.
	 */
	@JsonProperty("topicId")
	@JsonDeserialize(using = CustomJacksonDeserializers.Long.class)
	@GraphQLScalar(fieldName = "topicId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long topicId;

	/**
	 * The unique identifier of the associated entity record.
	 */
	@JsonProperty("entityId")
	@JsonDeserialize(using = CustomJacksonDeserializers.Long.class)
	@GraphQLScalar(fieldName = "entityId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long entityId;

	/**
	 * The associated entity kind.
	 */
	@JsonProperty("entityKind")
	@GraphQLInputParameters(names = { "format" }, types = { "FormatKind" }, mandatories = { false }, listDepths = { 0 },
		itemsMandatory = { false })
	@GraphQLScalar(fieldName = "entityKind", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String entityKind;

	// TODO: consider whether to return the identifier or the entity record.
	// /**
	//  * The associated topic.
	//  */
	// @JsonProperty("topic")
	// @GraphQLNonScalar(fieldName = "topic", graphQLTypeSimpleName = "Topic", javaClass = Topic.class, listDepth = 0)
	// Topic topic;

	// /**
	//  * The associated record.
	//  */
	// @JsonProperty("entity")
	// @GraphQLNonScalar(fieldName = "entity", graphQLTypeSimpleName = "ITopicalEntity", javaClass = ITopicalEntity.class,
	// 	listDepth = 0)
	// ITopicalEntity entity;

	/**
	 * The locations within the associated record, one per line.
	 */
	@JsonProperty("locations")
	@GraphQLScalar(fieldName = "locations", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String locations;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	// NOTE: only necessary because graphql-java-generator emits code with errors (missing ctor) if there is no ID
	// field.
	/**
	 * The unique topic ref identifier.
	 */
	@Override
	@JsonIgnore
	public void setId(Long id) {
		this.id = id;
	}

	// NOTE: only necessary because graphql-java-generator emits code with errors (missing ctor) if there is no ID
	// field.
	/**
	 * The unique topic ref identifier.
	 */
	@Override
	@JsonIgnore
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
	 * The unique identifier of the associated record.
	 */
	@JsonProperty("entityId")
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/**
	 * The unique identifier of the associated record.
	 */
	@JsonProperty("entityId")
	public Long getEntityId() {
		return this.entityId;
	}

	/**
	 * The associated entity kind.
	 */
	@JsonProperty("entityKind")
	public void setEntityKind(String entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * The associated entity kind.
	 */
	@JsonProperty("entityKind")
	public String getEntityKind() {
		return this.entityKind;
	}

	// /**
	//  * The associated topic.
	//  */
	// @JsonProperty("topic")
	// public void setTopic(Topic topic) {
	// 	this.topic = topic;
	// }

	// /**
	//  * The associated topic.
	//  */
	// @JsonProperty("topic")
	// public Topic getTopic() {
	// 	return this.topic;
	// }

	// /**
	//  * The associated record.
	//  */
	// @JsonProperty("entity")
	// public void setEntity(ITopicalEntity entity) {
	// 	this.entity = entity;
	// }

	// /**
	//  * The associated record.
	//  */
	// @JsonProperty("entity")
	// public ITopicalEntity getEntity() {
	// 	return this.entity;
	// }

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

	@Override
	@JsonIgnore
	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	@Override
	@JsonIgnore
	public String get__typename() {
		return this.__typename;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasValues == null) ? 0 : aliasValues.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((topicId == null) ? 0 : topicId.hashCode());
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + ((entityKind == null) ? 0 : entityKind.hashCode());
		// result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		// result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((locations == null) ? 0 : locations.hashCode());
		result = prime * result + ((__typename == null) ? 0 : __typename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicRef other = (TopicRef)obj;
		if (aliasValues == null) {
			if (other.aliasValues != null)
				return false;
		} else if (!aliasValues.equals(other.aliasValues))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (topicId == null) {
			if (other.topicId != null)
				return false;
		} else if (!topicId.equals(other.topicId))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (entityKind == null) {
			if (other.entityKind != null)
				return false;
		} else if (!entityKind.equals(other.entityKind))
			return false;
		// if (topic == null) {
		// 	if (other.topic != null)
		// 		return false;
		// } else if (!topic.equals(other.topic))
		// 	return false;
		// if (entity == null) {
		// 	if (other.entity != null)
		// 		return false;
		// } else if (!entity.equals(other.entity))
		// 	return false;
		if (locations == null) {
			if (other.locations != null)
				return false;
		} else if (!locations.equals(other.locations))
			return false;
		if (__typename == null) {
			if (other.__typename != null)
				return false;
		} else if (!__typename.equals(other.__typename))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TopicRef {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "topicId: " + this.topicId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entityId: " + this.entityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entityKind: " + this.entityKind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			// + "topic: " + this.topic //$NON-NLS-1$
			// + ", " //$NON-NLS-1$
			// + "entity: " + this.entity //$NON-NLS-1$
			// + ", " //$NON-NLS-1$
			+ "locations: " + this.locations //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "__typename: " + this.__typename //$NON-NLS-1$
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
		private Long entityId;
		private String entityKind;
		// private Topic topic;
		// private ITopicalEntity entity;
		private String locations;

		// NOTE: only necessary because graphql-java-generator emits code with errors (missing ctor) if there is no ID
		// field.
		/**
		 * The unique topic ref identifier.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The associated topic.
		 */
		public Builder withTopicId(Long topicIdParam) {
			this.topicId = topicIdParam;
			return this;
		}

		/**
		 * The unique identifier of the associated record.
		 */
		public Builder withEntityId(Long entityIdParam) {
			this.entityId = entityIdParam;
			return this;
		}

		/**
		 * The associated entity kind.
		 */
		public Builder withEntityKind(String entityKindParam) {
			this.entityKind = entityKindParam;
			return this;
		}

		// /**
		//  * The associated topic.
		//  */
		// public Builder withTopic(Topic topicParam) {
		// 	this.topic = topicParam;
		// 	return this;
		// }

		// /**
		//  * The associated entity record.
		//  */
		// public Builder withEntity(ITopicalEntity entityParam) {
		// 	this.entity = entityParam;
		// 	return this;
		// }

		/**
		 * The locations within the associated record, one per line.
		 */
		public Builder withLocations(String locationsParam) {
			this.locations = locationsParam;
			return this;
		}

		public TopicRef build() {
			TopicRef _object = new TopicRef();
			_object.setId(this.id);
			_object.setTopicId(this.topicId);
			_object.setEntityId(this.entityId);
			_object.setEntityKind(this.entityKind);
			// _object.setTopic(this.topic);
			// _object.setEntity(this.entity);
			_object.setLocations(this.locations);
			_object.set__typename("TopicRef"); //$NON-NLS-1$
			return _object;
		}

	}

}
