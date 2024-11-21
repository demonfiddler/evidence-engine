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
import com.graphql_java_generator.annotation.GraphQLScalar;

import com.graphql_java_generator.annotation.GraphQLDirective;

/**
 * An input for creating or updating a topic.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("TopicInput")
@SuppressWarnings("unused")
public class TopicInput {

	/**
	 * The unique topic identifier.
	 */
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The topic label for display in the user interface.
	 */
	@GraphQLScalar(fieldName = "label", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String label;

	/**
	 * The topic description.
	 */
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String description;

	/**
	 * The identifier of the parent topic.
	 */
	@GraphQLScalar(fieldName = "parentId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long parentId;

	/**
	 * The unique topic identifier.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique topic identifier.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * The topic label for display in the user interface.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * The topic label for display in the user interface.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * The topic description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The topic description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * The identifier of the parent topic.
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * The identifier of the parent topic.
	 */
	public Long getParentId() {
		return this.parentId;
	}

	public String toString() {
		return "TopicInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "label: " + this.label //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "description: " + this.description //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "parentId: " + this.parentId //$NON-NLS-1$
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

		private Long id;
		private String label;
		private String description;
		private Long parentId;

		/**
		 * The unique topic identifier.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The topic label for display in the user interface.
		 */
		public Builder withLabel(String labelParam) {
			this.label = labelParam;
			return this;
		}

		/**
		 * The topic description.
		 */
		public Builder withDescription(String descriptionParam) {
			this.description = descriptionParam;
			return this;
		}

		/**
		 * The identifier of the parent topic.
		 */
		public Builder withParentId(Long parentIdParam) {
			this.parentId = parentIdParam;
			return this;
		}

		public TopicInput build() {
			TopicInput _object = new TopicInput();
			_object.setId(this.id);
			_object.setLabel(this.label);
			_object.setDescription(this.description);
			_object.setParentId(this.parentId);
			return _object;
		}

	}

}