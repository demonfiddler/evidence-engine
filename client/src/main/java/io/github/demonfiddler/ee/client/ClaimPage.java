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
 * Returns paginated claim query results.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("ClaimPage")
@JsonInclude(Include.NON_NULL)
public class ClaimPage extends AbstractPage<Claim> {

	public ClaimPage() {
	}

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	@GraphQLScalar(fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long dummy;

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	@JsonDeserialize(using = CustomJacksonDeserializers.ListClaim.class)
	@GraphQLNonScalar(fieldName = "content", graphQLTypeSimpleName = "Claim", javaClass = Claim.class, listDepth = 1)
	List<Claim> content;

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	@GraphQLScalar(fieldName = "hasContent", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean hasContent;

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	@GraphQLScalar(fieldName = "isEmpty", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isEmpty;

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	@GraphQLScalar(fieldName = "number", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer number;

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	@GraphQLScalar(fieldName = "size", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer size;

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	@GraphQLScalar(fieldName = "numberOfElements", graphQLTypeSimpleName = "Int", javaClass = Integer.class,
		listDepth = 0)
	Integer numberOfElements;

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	@GraphQLScalar(fieldName = "totalPages", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer totalPages;

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	@JsonDeserialize(using = CustomJacksonDeserializers.Long.class)
	@GraphQLScalar(fieldName = "totalElements", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long totalElements;

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	@GraphQLScalar(fieldName = "isFirst", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isFirst;

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	@GraphQLScalar(fieldName = "isLast", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isLast;

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	@GraphQLScalar(fieldName = "hasNext", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean hasNext;

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	@GraphQLScalar(fieldName = "hasPrevious", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean hasPrevious;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	public void setDummy(Long dummy) {
		this.dummy = dummy;
	}

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	public Long getDummy() {
		return this.dummy;
	}

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	@Override
	public void setContent(List<Claim> content) {
		this.content = content;
	}

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	@Override
	public List<Claim> getContent() {
		return this.content;
	}

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	@Override
	public void setHasContent(Boolean hasContent) {
		this.hasContent = hasContent;
	}

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	@Override
	public Boolean getHasContent() {
		return this.hasContent;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	@Override
	public void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	@Override
	public Boolean getIsEmpty() {
		return this.isEmpty;
	}

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	@Override
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	@Override
	public Integer getNumber() {
		return this.number;
	}

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	@Override
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	@Override
	public Integer getSize() {
		return this.size;
	}

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	@Override
	public void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	@Override
	public Integer getNumberOfElements() {
		return this.numberOfElements;
	}

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	@Override
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	@Override
	public Integer getTotalPages() {
		return this.totalPages;
	}

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	@Override
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	@Override
	public Long getTotalElements() {
		return this.totalElements;
	}

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	@Override
	public void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	@Override
	public Boolean getIsFirst() {
		return this.isFirst;
	}

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	@Override
	public void setIsLast(Boolean isLast) {
		this.isLast = isLast;
	}

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	@Override
	public Boolean getIsLast() {
		return this.isLast;
	}

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	@Override
	public void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	@Override
	public Boolean getHasNext() {
		return this.hasNext;
	}

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	@Override
	public void setHasPrevious(Boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	@Override
	public Boolean getHasPrevious() {
		return this.hasPrevious;
	}

	@JsonProperty("__typename")
	@Override
	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	@JsonProperty("__typename")
	@Override
	public String get__typename() {
		return this.__typename;
	}

	public static Builder builder() {
		return new Builder();
	}

	static class Builder extends AbstractPage.AbstractBuilder<ClaimPage, Claim> {

		Builder() {
		}

		@Override
		ClaimPage createPage() {
			return new ClaimPage();
		}

		@Override
		String getTypeName() {
			return "ClaimPage";
		}

	}

}
