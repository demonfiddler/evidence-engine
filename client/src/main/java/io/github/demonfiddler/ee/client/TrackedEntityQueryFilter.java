/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
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
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

/**
 * Parameters to filter an ```ITrackedEntity``` query.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("TrackedEntityQueryFilter")
@JsonInclude(Include.NON_NULL)
public class TrackedEntityQueryFilter {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public TrackedEntityQueryFilter() {
	}

	/**
	 * Return only records with these status codes (default: ALL).
	 */
	@JsonProperty("status")
	@GraphQLScalar(fieldName = "status", graphQLTypeSimpleName = "StatusKind", javaClass = StatusKind.class,
		listDepth = 1)
	List<StatusKind> status;

	/**
	 * Free text search string.
	 */
	@JsonProperty("text")
	@GraphQLScalar(fieldName = "text", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String text;

	/**
	 * Whether to search ```text``` in advanced (boolean) mode.
	 */
	@JsonProperty("advancedSearch")
	@GraphQLScalar(fieldName = "advancedSearch", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean advancedSearch;

	/**
	 * Return only records with these status codes (default: ALL).
	 */
	@JsonProperty("status")
	public void setStatus(List<StatusKind> status) {
		this.status = status;
	}

	/**
	 * Return only records with these status codes (default: ALL).
	 */
	@JsonProperty("status")
	public List<StatusKind> getStatus() {
		return this.status;
	}

	/**
	 * Free text search string.
	 */
	@JsonProperty("text")
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Free text search string.
	 */
	@JsonProperty("text")
	public String getText() {
		return this.text;
	}

	/**
	 * Whether to search ```text``` in advanced (boolean) mode.
	 */
	@JsonProperty("advancedSearch")
	public void setAdvancedSearch(Boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	/**
	 * Whether to search ```text``` in advanced (boolean) mode.
	 */
	@JsonProperty("advancedSearch")
	public Boolean getAdvancedSearch() {
		return this.advancedSearch;
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
		return "TrackedEntityQueryFilter {" //$NON-NLS-1$
			+ "status: " + this.status //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "text: " + this.text //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "advancedSearch: " + this.advancedSearch //$NON-NLS-1$
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

		private List<StatusKind> status;
		private String text;
		private Boolean advancedSearch;

		/**
		 * Return only records with these status codes (default: ALL).
		 */
		public Builder withStatus(List<StatusKind> statusParam) {
			this.status = statusParam;
			return this;
		}

		/**
		 * Free text search string.
		 */
		public Builder withText(String textParam) {
			this.text = textParam;
			return this;
		}

		/**
		 * Whether to search ```text``` in advanced (boolean) mode.
		 */
		public Builder withAdvancedSearch(Boolean advancedSearchParam) {
			this.advancedSearch = advancedSearchParam;
			return this;
		}

		public TrackedEntityQueryFilter build() {
			TrackedEntityQueryFilter _object = new TrackedEntityQueryFilter();
			_object.setStatus(this.status);
			_object.setText(this.text);
			_object.setAdvancedSearch(this.advancedSearch);
			return _object;
		}

	}

}