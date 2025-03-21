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
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * An input to specify how to sort results on a given field.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("OrderInput")
@JsonInclude(Include.NON_NULL)
public class OrderInput extends AbstractGraphQLObject {

	public OrderInput() {
	}

	/**
	 * The field name.
	 */
	@JsonProperty("property")
	@GraphQLScalar(fieldName = "property", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String property;

	/**
	 * The sort direction.
	 */
	@JsonProperty("direction")
	@GraphQLScalar(fieldName = "direction", graphQLTypeSimpleName = "DirectionKind", javaClass = DirectionKind.class,
		listDepth = 0)
	DirectionKind direction;

	/**
	 * Whether to ignore case.
	 */
	@JsonProperty("ignoreCase")
	@GraphQLScalar(fieldName = "ignoreCase", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean ignoreCase;

	/**
	 * Defines ordering of ```null``` vs non-```null``` values.
	 */
	@JsonProperty("nullHandling")
	@GraphQLScalar(fieldName = "nullHandling", graphQLTypeSimpleName = "NullHandlingKind",
		javaClass = NullHandlingKind.class, listDepth = 0)
	NullHandlingKind nullHandling;

	/**
	 * The field name.
	 */
	@JsonProperty("property")
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * The field name.
	 */
	@JsonProperty("property")
	public String getProperty() {
		return this.property;
	}

	/**
	 * The sort direction.
	 */
	@JsonProperty("direction")
	public void setDirection(DirectionKind direction) {
		this.direction = direction;
	}

	/**
	 * The sort direction.
	 */
	@JsonProperty("direction")
	public DirectionKind getDirection() {
		return this.direction;
	}

	/**
	 * Whether to ignore case.
	 */
	@JsonProperty("ignoreCase")
	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
	 * Whether to ignore case.
	 */
	@JsonProperty("ignoreCase")
	public Boolean getIgnoreCase() {
		return this.ignoreCase;
	}

	/**
	 * Defines ordering of ```null``` vs non-```null``` values.
	 */
	@JsonProperty("nullHandling")
	public void setNullHandling(NullHandlingKind nullHandling) {
		this.nullHandling = nullHandling;
	}

	/**
	 * Defines ordering of ```null``` vs non-```null``` values.
	 */
	@JsonProperty("nullHandling")
	public NullHandlingKind getNullHandling() {
		return this.nullHandling;
	}

	public String toString() {
		return "OrderInput {" //
			+ "property: " + this.property //
			+ ", " //
			+ "direction: " + this.direction //
			+ ", " //
			+ "ignoreCase: " + this.ignoreCase //
			+ ", " //
			+ "nullHandling: " + this.nullHandling //
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

		private String property;
		private DirectionKind direction;
		private Boolean ignoreCase;
		private NullHandlingKind nullHandling;

		/**
		 * The field name.
		 */
		public Builder withProperty(String propertyParam) {
			this.property = propertyParam;
			return this;
		}

		/**
		 * The sort direction.
		 */
		public Builder withDirection(DirectionKind directionParam) {
			this.direction = directionParam;
			return this;
		}

		/**
		 * Whether to ignore case.
		 */
		public Builder withIgnoreCase(Boolean ignoreCaseParam) {
			this.ignoreCase = ignoreCaseParam;
			return this;
		}

		/**
		 * Defines ordering of {@code null} vs non-{@code null} values.
		 */
		public Builder withNullHandling(NullHandlingKind nullHandlingParam) {
			this.nullHandling = nullHandlingParam;
			return this;
		}

		public OrderInput build() {
			OrderInput _object = new OrderInput();
			_object.setProperty(this.property);
			_object.setDirection(this.direction);
			_object.setIgnoreCase(this.ignoreCase);
			_object.setNullHandling(this.nullHandling);
			return _object;
		}

	}

}
