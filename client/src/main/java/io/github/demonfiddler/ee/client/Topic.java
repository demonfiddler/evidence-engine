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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * A node in the topic hierarchy tree.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("Topic")
@JsonInclude(Include.NON_NULL)
public class Topic extends AbstractLinkableEntity {

	public Topic() {
	}

	/**
	 * The topic label for display in the user interface.
	 */
	@JsonProperty("label")
	@GraphQLScalar(fieldName = "label", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String label;

	/**
	 * The topic description.
	 */
	@JsonProperty("description")
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	/**
	 * The parent topic.
	 */
	@JsonProperty("parent")
	@GraphQLNonScalar(fieldName = "parent", graphQLTypeSimpleName = "Topic", javaClass = Topic.class, listDepth = 0)
	Topic parent;

	/**
	 * The sub-topics.
	 */
	@JsonProperty("children")
	@JsonDeserialize(using = CustomJacksonDeserializers.ListTopic.class)
	@GraphQLNonScalar(fieldName = "children", graphQLTypeSimpleName = "Topic", javaClass = Topic.class, listDepth = 1)
	List<Topic> children;

	/**
	 * The topic label for display in the user interface.
	 */
	@JsonProperty("label")
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * The topic label for display in the user interface.
	 */
	@JsonProperty("label")
	public String getLabel() {
		return this.label;
	}

	/**
	 * The topic description.
	 */
	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The topic description.
	 */
	@JsonProperty("description")
	public String getDescription() {
		return this.description;
	}

	/**
	 * The parent topic.
	 */
	@JsonProperty("parent")
	public void setParent(Topic parent) {
		this.parent = parent;
	}

	/**
	 * The parent topic.
	 */
	@JsonProperty("parent")
	public Topic getParent() {
		return this.parent;
	}

	/**
	 * The sub-topics.
	 */
	@JsonProperty("children")
	public void setChildren(List<Topic> children) {
		this.children = children;
	}

	/**
	 * The sub-topics.
	 */
	@JsonProperty("children")
	public List<Topic> getChildren() {
		return this.children;
	}

	@Override
	public String toString() {
		return "Topic {" //
			+ "id: " + this.id //
			+ ", " //
			+ "entityKind: " + this.entityKind //
			+ ", " //
			+ "status: " + this.status //
			+ ", " //
			+ "created: " + this.created //
			+ ", " //
			+ "createdByUser.id: " + (this.createdByUser == null ? null : this.createdByUser.getId()) //
			+ ", " //
			+ "updated: " + this.updated //
			+ ", " //
			+ "updatedByUser.id: " + (this.updatedByUser == null ? null : this.updatedByUser.getId()) //
			+ ", " //
			+ "log: " + this.log //
			+ ", " //
			+ "fromEntityLinks: " + this.fromEntityLinks //
			+ ", " //
			+ "toEntityLinks: " + this.toEntityLinks //
			+ ", " //
			+ "label: " + this.label //
			+ ", " //
			+ "description: " + this.description //
			+ ", " //
			+ "parent.id: " + (this.parent == null ? null : this.parent.getId()) //
			+ ", " //
			+ "children: " + this.children //
			+ ", " //
			+ "__typename: " + this.__typename //
			+ "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((parent == null || parent.getId() == null) ? 0 : parent.getId().hashCode());
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
		Topic other = (Topic)obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		return true;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractLinkableEntity.Builder<Builder, Topic> {

		private String label;
		private String description;
		private Topic parent;
		private List<Topic> children;

		/**
		 * The topic label for display in the user interface.
		 */
		public Builder withLabel(String labelParam) {
			this.label = labelParam;
			return this;
		}

		/**
		 * The topic description.
		 */
		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		/**
		 * The parent topic.
		 */
		public Builder withParent(Topic parentParam) {
			this.parent = parentParam;
			return this;
		}

		/**
		 * The sub-topics.
		 */
		public Builder withChildren(List<Topic> childrenParam) {
			this.children = childrenParam;
			return this;
		}

		@Override
		public Topic build() {
			Topic _object = build(new Topic());
			_object.setLabel(this.label);
			_object.setDescription(this.description);
			_object.setParent(this.parent);
			_object.setChildren(this.children);
			return _object;
		}

		@Override
		String getTypeName() {
			return "Topic";
		}

	}

}
