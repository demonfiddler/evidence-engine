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

package io.github.demonfiddler.ee.server.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import graphql.schema.DataFetchingEnvironment;

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

import com.graphql_java_generator.annotation.GraphQLDirective;

/**
 * An input to specify the page number, size and sort order.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("PageableInput")
@SuppressWarnings("unused")
public class PageableInput {

	/**
	 * 0-based page number, must be >= 0. Omit or set to 0 for first page.
	 */
	@GraphQLScalar(fieldName = "pageNumber", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer pageNumber;

	/**
	 * The number of records to return. Omit or set to 0 for unpaginated.
	 */
	@GraphQLScalar(fieldName = "pageSize", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer pageSize;

	/**
	 * How to sort the results. Default is to use native database order.
	 */
	@GraphQLNonScalar(fieldName = "sort", graphQLTypeSimpleName = "SortInput", javaClass = SortInput.class,
		listDepth = 0)
	SortInput sort;

	/**
	 * 0-based page number, must be >= 0. Omit or set to 0 for first page.
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * 0-based page number, must be >= 0. Omit or set to 0 for first page.
	 */
	public Integer getPageNumber() {
		return this.pageNumber;
	}

	/**
	 * The number of records to return. Omit or set to 0 for unpaginated.
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * The number of records to return. Omit or set to 0 for unpaginated.
	 */
	public Integer getPageSize() {
		return this.pageSize;
	}

	/**
	 * How to sort the results. Default is to use native database order.
	 */
	public void setSort(SortInput sort) {
		this.sort = sort;
	}

	/**
	 * How to sort the results. Default is to use native database order.
	 */
	public SortInput getSort() {
		return this.sort;
	}

	public String toString() {
		return "PageableInput {" //$NON-NLS-1$
			+ "pageNumber: " + this.pageNumber //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "pageSize: " + this.pageSize //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "sort: " + this.sort //$NON-NLS-1$
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
		private Integer pageNumber;
		private Integer pageSize;
		private SortInput sort;

		/**
		 * 0-based page number, must be >= 0. Omit or set to 0 for first page.
		 */
		public Builder withPageNumber(Integer pageNumberParam) {
			this.pageNumber = pageNumberParam;
			return this;
		}

		/**
		 * The number of records to return. Omit or set to 0 for unpaginated.
		 */
		public Builder withPageSize(Integer pageSizeParam) {
			this.pageSize = pageSizeParam;
			return this;
		}

		/**
		 * How to sort the results. Default is to use native database order.
		 */
		public Builder withSort(SortInput sortParam) {
			this.sort = sortParam;
			return this;
		}

		public PageableInput build() {
			PageableInput _object = new PageableInput();
			_object.setPageNumber(this.pageNumber);
			_object.setPageSize(this.pageSize);
			_object.setSort(this.sort);
			return _object;
		}
	}

}