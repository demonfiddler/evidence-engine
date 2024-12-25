/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024 Adrian Price. All rights reserved.
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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("__Schema")
@JsonInclude(Include.NON_NULL)
public class __Schema {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public __Schema() {
	}

	@JsonProperty("description")
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	@JsonProperty("types")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__Type.class)
	@GraphQLNonScalar(fieldName = "types", graphQLTypeSimpleName = "__Type", javaClass = __Type.class, listDepth = 1)
	List<__Type> types;

	@JsonProperty("queryType")
	@GraphQLNonScalar(fieldName = "queryType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class,
		listDepth = 0)
	__Type queryType;

	@JsonProperty("mutationType")
	@GraphQLNonScalar(fieldName = "mutationType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class,
		listDepth = 0)
	__Type mutationType;

	@JsonProperty("subscriptionType")
	@GraphQLNonScalar(fieldName = "subscriptionType", graphQLTypeSimpleName = "__Type", javaClass = __Type.class,
		listDepth = 0)
	__Type subscriptionType;

	@JsonProperty("directives")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__Directive.class)
	@GraphQLNonScalar(fieldName = "directives", graphQLTypeSimpleName = "__Directive", javaClass = __Directive.class,
		listDepth = 1)
	List<__Directive> directives;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("description")
	public String getDescription() {
		return this.description;
	}

	@JsonProperty("types")
	public void setTypes(List<__Type> types) {
		this.types = types;
	}

	@JsonProperty("types")
	public List<__Type> getTypes() {
		return this.types;
	}

	@JsonProperty("queryType")
	public void setQueryType(__Type queryType) {
		this.queryType = queryType;
	}

	@JsonProperty("queryType")
	public __Type getQueryType() {
		return this.queryType;
	}

	@JsonProperty("mutationType")
	public void setMutationType(__Type mutationType) {
		this.mutationType = mutationType;
	}

	@JsonProperty("mutationType")
	public __Type getMutationType() {
		return this.mutationType;
	}

	@JsonProperty("subscriptionType")
	public void setSubscriptionType(__Type subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	@JsonProperty("subscriptionType")
	public __Type getSubscriptionType() {
		return this.subscriptionType;
	}

	@JsonProperty("directives")
	public void setDirectives(List<__Directive> directives) {
		this.directives = directives;
	}

	@JsonProperty("directives")
	public List<__Directive> getDirectives() {
		return this.directives;
	}

	@JsonProperty("__typename")
	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	@JsonProperty("__typename")
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

	public String toString() {
		return "__Schema {" //$NON-NLS-1$
			+ "description: " + this.description //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "types: " + this.types //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "queryType: " + this.queryType //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "mutationType: " + this.mutationType //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "subscriptionType: " + this.subscriptionType //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "directives: " + this.directives //$NON-NLS-1$
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
		private String description;
		private List<__Type> types;
		private __Type queryType;
		private __Type mutationType;
		private __Type subscriptionType;
		private List<__Directive> directives;

		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		public Builder withTypes(List<__Type> typesParam) {
			this.types = typesParam;
			return this;
		}

		public Builder withQueryType(__Type queryTypeParam) {
			this.queryType = queryTypeParam;
			return this;
		}

		public Builder withMutationType(__Type mutationTypeParam) {
			this.mutationType = mutationTypeParam;
			return this;
		}

		public Builder withSubscriptionType(__Type subscriptionTypeParam) {
			this.subscriptionType = subscriptionTypeParam;
			return this;
		}

		public Builder withDirectives(List<__Directive> directivesParam) {
			this.directives = directivesParam;
			return this;
		}

		public __Schema build() {
			__Schema _object = new __Schema();
			_object.setDescription(this.description);
			_object.setTypes(this.types);
			_object.setQueryType(this.queryType);
			_object.setMutationType(this.mutationType);
			_object.setSubscriptionType(this.subscriptionType);
			_object.setDirectives(this.directives);
			_object.set__typename("__Schema"); //$NON-NLS-1$
			return _object;
		}
	}
}
