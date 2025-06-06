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

package io.github.demonfiddler.ee.server.model;

import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * Interface for returning paged results.
 * 
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInterfaceType("IPage")
public interface IPage {

	@Id
	@GeneratedValue
	@GraphQLScalar(fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	void setDummy(Long dummy);

	@Id
	@GeneratedValue
	@GraphQLScalar(fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long getDummy();

	/**
	 *  "The requested pageful of records."
	 *  content: [IBaseEntity!]!
	 */
	@GraphQLScalar(fieldName = "hasContent", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	void setHasContent(Boolean hasContent);

	/**
	 *  "The requested pageful of records."
	 *  content: [IBaseEntity!]!
	 */
	@GraphQLScalar(fieldName = "hasContent", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean getHasContent();

	@GraphQLScalar(fieldName = "isEmpty", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	void setIsEmpty(Boolean isEmpty);

	@GraphQLScalar(fieldName = "isEmpty", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean getIsEmpty();

	@GraphQLScalar(fieldName = "number", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	void setNumber(Integer number);

	@GraphQLScalar(fieldName = "number", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer getNumber();

	@GraphQLScalar(fieldName = "size", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	void setSize(Integer size);

	@GraphQLScalar(fieldName = "size", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer getSize();

	@GraphQLScalar(fieldName = "numberOfElements", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	void setNumberOfElements(Integer numberOfElements);

	@GraphQLScalar(fieldName = "numberOfElements", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer getNumberOfElements();

	@GraphQLScalar(fieldName = "totalPages", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	void setTotalPages(Integer totalPages);

	@GraphQLScalar(fieldName = "totalPages", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer getTotalPages();

	@GraphQLScalar(fieldName = "totalElements", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	void setTotalElements(Long totalElements);

	@GraphQLScalar(fieldName = "totalElements", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long getTotalElements();

	@GraphQLScalar(fieldName = "isFirst", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	void setIsFirst(Boolean isFirst);

	@GraphQLScalar(fieldName = "isFirst", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean getIsFirst();

	@GraphQLScalar(fieldName = "isLast", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	void setIsLast(Boolean isLast);

	@GraphQLScalar(fieldName = "isLast", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean getIsLast();

	@GraphQLScalar(fieldName = "hasNext", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	void setHasNext(Boolean hasNext);

	@GraphQLScalar(fieldName = "hasNext", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean getHasNext();

	@GraphQLScalar(fieldName = "hasPrevious", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	void setHasPrevious(Boolean hasPrevious);

	@GraphQLScalar(fieldName = "hasPrevious", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean getHasPrevious();

}
