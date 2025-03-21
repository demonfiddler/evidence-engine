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

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * Returns paginated quotation query results.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("QuotationPage")
@JsonInclude(Include.NON_NULL)
public class QuotationPage extends AbstractPage<Quotation> {

	public QuotationPage() {
	}

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	@JsonDeserialize(using = CustomJacksonDeserializers.ListQuotation.class)
	@GraphQLNonScalar(fieldName = "content", graphQLTypeSimpleName = "Quotation", javaClass = Quotation.class,
		listDepth = 1)
	List<Quotation> content;

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	public void setContent(List<Quotation> content) {
		this.content = content;
	}

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	public List<Quotation> getContent() {
		return this.content;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractPage.Builder<Builder, QuotationPage, Quotation> {

		@Override
		QuotationPage createPage() {
			return new QuotationPage();
		}

		@Override
		String getTypeName() {
			return "QuotationPage";
		}

	}

}
