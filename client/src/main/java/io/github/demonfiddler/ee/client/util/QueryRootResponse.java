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

package io.github.demonfiddler.ee.client.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

import io.github.demonfiddler.ee.client.Query;

public class QueryRootResponse {

	@JsonProperty("data")
	@GraphQLNonScalar(fieldName = "Query", graphQLTypeSimpleName = "Query", javaClass = Query.class)
	Query query;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	@JsonProperty("extensions")
	public JsonNode extensions;

	// This getter is needed for the Json serialization
	public Query getData() {
		return this.query;
	}

	// This setter is needed for the Json deserialization
	public void setData(Query queryParam) {
		this.query = queryParam;
	}

	public List<Error> getErrors() {
		return this.errors;
	}

	public void setErrors(List<Error> errorsParam) {
		this.errors = errorsParam;
	}

	public JsonNode getExtensions() {
		return this.extensions;
	}

	public void setExtensions(JsonNode extensionsParam) {
		this.extensions = extensionsParam;
	}

}
