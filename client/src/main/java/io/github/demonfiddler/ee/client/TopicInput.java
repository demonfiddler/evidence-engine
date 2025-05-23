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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import io.github.demonfiddler.ee.client.util.CustomJacksonSerializers;

/**
 * An input for creating or updating a topic.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("TopicInput")
@JsonInclude(Include.NON_NULL)
public class TopicInput extends AbstractBaseEntityInput {

	public TopicInput() {
	}

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The unique topic identifier.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

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
	 * The identifier of the parent topic.
	 */
	@JsonProperty("parentId")
	@JsonSerialize(using = CustomJacksonSerializers.Long.class)
	@GraphQLScalar(fieldName = "parentId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long parentId;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The unique topic identifier.
	 */
	@Override
	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The unique topic identifier.
	 */
	@Override
	@JsonProperty("id")
	public Long getId() {
		return this.id;
	}

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
	 * The identifier of the parent topic.
	 */
	@JsonProperty("parentId")
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * The identifier of the parent topic.
	 */
	@JsonProperty("parentId")
	public Long getParentId() {
		return this.parentId;
	}

	public String toString() {
		return "TopicInput {" //
			+ "id: " + this.id //
			+ ", " //
			+ "label: " + this.label //
			+ ", " //
			+ "description: " + this.description //
			+ ", " //
			+ "parentId: " + this.parentId //
			+ "}";
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractBaseEntityInput.Builder<Builder, TopicInput> {

		private String label;
		private String description;
		private Long parentId;

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
		 * The identifier of the parent topic.
		 */
		public Builder withParentId(Long parentIdParam) {
			this.parentId = parentIdParam;
			return this;
		}

		@Override
		public TopicInput build() {
			TopicInput _object = build(new TopicInput());
			_object.setLabel(this.label);
			_object.setDescription(this.description);
			_object.setParentId(this.parentId);
			return _object;
		}

	}

}
