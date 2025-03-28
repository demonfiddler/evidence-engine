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
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;

/**
 * An input to specify output sort order.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("SortInput")
@JsonInclude(Include.NON_NULL)
public class SortInput extends AbstractGraphQLObject {

	public SortInput() {
	}

	/**
	 * A list of field sort specifiers.
	 */
	@JsonProperty("orders")
	@GraphQLNonScalar(fieldName = "orders", graphQLTypeSimpleName = "OrderInput", javaClass = OrderInput.class,
		listDepth = 1)
	List<OrderInput> orders;

	/**
	 * A list of field sort specifiers.
	 */
	@JsonProperty("orders")
	public void setOrders(List<OrderInput> orders) {
		this.orders = orders;
	}

	/**
	 * A list of field sort specifiers.
	 */
	@JsonProperty("orders")
	public List<OrderInput> getOrders() {
		return this.orders;
	}

	public String toString() {
		return "SortInput {" //
			+ "orders: " + this.orders //
			+ "}";
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {
		private List<OrderInput> orders;

		/**
		 * A list of field sort specifiers.
		 */
		public Builder withOrders(List<OrderInput> ordersParam) {
			this.orders = ordersParam;
			return this;
		}

		public SortInput build() {
			SortInput _object = new SortInput();
			_object.setOrders(this.orders);
			return _object;
		}

	}

}
