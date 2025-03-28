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
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("__Directive")
@JsonInclude(Include.NON_NULL)
public class __Directive extends AbstractGraphQLEntity {

	public __Directive() {
	}

	@JsonProperty("name")
	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String name;

	@JsonProperty("description")
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	@JsonProperty("locations")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__DirectiveLocation.class)
	@GraphQLScalar(fieldName = "locations", graphQLTypeSimpleName = "__DirectiveLocation",
		javaClass = __DirectiveLocation.class, listDepth = 1)
	List<__DirectiveLocation> locations;

	@JsonProperty("args")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__InputValue.class)
	@GraphQLInputParameters(names = { "includeDeprecated" }, types = { "Boolean" }, mandatories = { false },
		listDepths = { 0 }, itemsMandatory = { false })
	@GraphQLNonScalar(fieldName = "args", graphQLTypeSimpleName = "__InputValue", javaClass = __InputValue.class,
		listDepth = 1)
	List<__InputValue> args;

	@JsonProperty("isRepeatable")
	@GraphQLScalar(fieldName = "isRepeatable", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean isRepeatable;

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("name")
	public String getName() {
		return this.name;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("description")
	public String getDescription() {
		return this.description;
	}

	@JsonProperty("locations")
	public void setLocations(List<__DirectiveLocation> locations) {
		this.locations = locations;
	}

	@JsonProperty("locations")
	public List<__DirectiveLocation> getLocations() {
		return this.locations;
	}

	@JsonProperty("args")
	public void setArgs(List<__InputValue> args) {
		this.args = args;
	}

	@JsonProperty("args")
	public List<__InputValue> getArgs() {
		return this.args;
	}

	@JsonProperty("isRepeatable")
	public void setIsRepeatable(Boolean isRepeatable) {
		this.isRepeatable = isRepeatable;
	}

	@JsonProperty("isRepeatable")
	public Boolean getIsRepeatable() {
		return this.isRepeatable;
	}

	public String toString() {
		return "__Directive {" //
			+ "name: " + this.name //
			+ ", " //
			+ "description: " + this.description //
			+ ", " //
			+ "locations: " + this.locations //
			+ ", " //
			+ "args: " + this.args //
			+ ", " //
			+ "isRepeatable: " + this.isRepeatable //
			+ ", " //
			+ "__typename: " + this.__typename //
			+ "}";
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractGraphQLEntity.Builder<Builder, __Directive> {

		private String name;
		private String description;
		private List<__DirectiveLocation> locations;
		private List<__InputValue> args;
		private Boolean isRepeatable;

		public Builder withName(String nameParam) {
			this.name = nameParam;
			return this;
		}

		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		public Builder withLocations(List<__DirectiveLocation> locationsParam) {
			this.locations = locationsParam;
			return this;
		}

		public Builder withArgs(List<__InputValue> argsParam) {
			this.args = argsParam;
			return this;
		}

		public Builder withIsRepeatable(Boolean isRepeatableParam) {
			this.isRepeatable = isRepeatableParam;
			return this;
		}

		public __Directive build() {
			__Directive _object = build(new __Directive());
			_object.setName(this.name);
			_object.setDescription(this.description);
			_object.setLocations(this.locations);
			_object.setArgs(this.args);
			_object.setIsRepeatable(this.isRepeatable);
			return _object;
		}

		@Override
		String getTypeName() {
			return "__Directive";
		}

	}

}
