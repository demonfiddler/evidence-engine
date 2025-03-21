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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import graphql.schema.DataFetchingEnvironment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

import java.util.List;

import com.graphql_java_generator.annotation.GraphQLDirective;
import com.graphql_java_generator.annotation.GraphQLIgnore;

/**
 * Parameters to filter an linkable entity query.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("LinkableEntityQueryFilter")
@JsonInclude(Include.NON_NULL)
@SuppressWarnings("unused")
public class LinkableEntityQueryFilter extends /*EntityLinkQueryFilter*/AbstractGraphQLObject {

	public LinkableEntityQueryFilter() {
	}

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	@JsonProperty("topicId")
	@GraphQLScalar(fieldName = "topicId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long topicId;

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	@JsonProperty("recursive")
	@GraphQLScalar(fieldName = "recursive", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean recursive;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The ID of the 'linked-from' (master) entity.
	 */
	@JsonProperty("fromEntityId")
	@GraphQLScalar(fieldName = "fromEntityId", graphQLTypeSimpleName = "ID", javaClass = String.class, listDepth = 0)
	Long fromEntityId;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The kind of the 'linked-from' entity.
	 */
	@JsonProperty("fromEntityKind")
	@GraphQLScalar(fieldName = "fromEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind fromEntityKind;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The ID of the 'linked-to' entity.
	 */
	@JsonProperty("toEntityId")
	@GraphQLScalar(fieldName = "toEntityId", graphQLTypeSimpleName = "ID", javaClass = String.class, listDepth = 0)
	Long toEntityId;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The kind of the 'linked-to' entity.
	 */
	@JsonProperty("toEntityKind")
	@GraphQLScalar(fieldName = "toEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind toEntityKind;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * Return only records with these status codes (default: ALL).
	 */
	@JsonProperty("status")
	@GraphQLScalar(fieldName = "status", graphQLTypeSimpleName = "StatusKind", javaClass = StatusKind.class,
		listDepth = 1)
	List<StatusKind> status;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * Free text search string (applied to the 'linked-to' entity).
	 */
	@JsonProperty("text")
	@GraphQLScalar(fieldName = "text", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String text;

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * Whether to search ```text``` in advanced (boolean) mode.
	 */
	@JsonProperty("advancedSearch")
	@GraphQLScalar(fieldName = "advancedSearch", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean advancedSearch;

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	@JsonProperty("topicId")
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	@JsonProperty("topicId")
	public Long getTopicId() {
		return this.topicId;
	}

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	@JsonProperty("recursive")
	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	@JsonProperty("recursive")
	public Boolean getRecursive() {
		return this.recursive;
	}

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The ID of the 'linked-from' (master) entity.
	 */
	@JsonProperty("fromEntityId")
	public void setFromEntityId(Long fromEntityId) {
		this.fromEntityId = fromEntityId;
	}

	/**
	 * The ID of the 'linked-from' (master) entity.
	 */
	@JsonProperty("fromEntityId")
	public Long getFromEntityId() {
		return this.fromEntityId;
	}

	/**
	 * The kind of the 'linked-from' entity.
	 */
	@JsonProperty("fromEntityKind")
	public void setFromEntityKind(EntityKind fromEntityKind) {
		this.fromEntityKind = fromEntityKind;
	}

	/**
	 * The kind of the 'linked-from' entity.
	 */
	@JsonProperty("fromEntityKind")
	public EntityKind getFromEntityKind() {
		return this.fromEntityKind;
	}

	/**
	 * The ID of the 'linked-to' entity.
	 */
	@JsonProperty("toEntityId")
	public void setToEntityId(Long toEntityId) {
		this.toEntityId = toEntityId;
	}

	/**
	 * The ID of the 'linked-to' entity.
	 */
	@JsonProperty("toEntityId")
	public Long getToEntityId() {
		return this.toEntityId;
	}

	/**
	 * The kind of the 'linked-to' entity.
	 */
	@JsonProperty("toEntityKind")
	public void setToEntityKind(EntityKind toEntityKind) {
		this.toEntityKind = toEntityKind;
	}

	/**
	 * The kind of the 'linked-to' entity.
	 */
	@JsonProperty("toEntityKind")
	public EntityKind getToEntityKind() {
		return this.toEntityKind;
	}

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
	 * Free text search string (applied to the 'linked-to' entity).
	 */
	@JsonProperty("text")
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Free text search string (applied to the 'linked-to' entity).
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

	public String toString() {
		return "LinkableEntityQueryFilter {" //
			+ "topicId: " + this.topicId //
			+ ", " //
			+ "recursive: " + this.recursive //
			+ ", " //
			+ "fromEntityId: " + this.fromEntityId //
			+ ", " //
			+ "fromEntityKind: " + this.fromEntityKind //
			+ ", " //
			+ "toEntityId: " + this.toEntityId //
			+ ", " //
			+ "toEntityKind: " + this.toEntityKind //
			+ ", " //
			+ "status: " + this.status //
			+ ", " //
			+ "text: " + this.text //
			+ ", " //
			+ "advancedSearch: " + this.advancedSearch //
			+ "}";
	}

	public static Builder builder() {
		return new Builder();
	}

	// Uncommented, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder /*extends EntityLinkQueryFilter.Builder*/ {

		private Long topicId;
		private Boolean recursive;
		private Long fromEntityId;
		private EntityKind fromEntityKind;
		private Long toEntityId;
		private EntityKind toEntityKind;
		private List<StatusKind> status;
		private String text;
		private Boolean advancedSearch;

		/**
		 * The topic identifier, mandatory when querying the master list or when master = NONE.
		 */
		public Builder withTopicId(Long topicIdParam) {
			this.topicId = topicIdParam;
			return this;
		}

		/**
		 * Whether queries including a topicId should be executed recursively.
		 */
		public Builder withRecursive(Boolean recursiveParam) {
			this.recursive = recursiveParam;
			return this;
		}

		/**
		 * The ID of the 'linked-from' (master) entity.
		 */
		public Builder withFromEntityId(Long fromEntityIdParam) {
			this.fromEntityId = fromEntityIdParam;
			return this;
		}

		/**
		 * The kind of the 'linked-from' entity.
		 */
		public Builder withFromEntityKind(EntityKind fromEntityKindParam) {
			this.fromEntityKind = fromEntityKindParam;
			return this;
		}

		/**
		 * The ID of the 'linked-to' entity.
		 */
		public Builder withToEntityId(Long toEntityIdParam) {
			this.toEntityId = toEntityIdParam;
			return this;
		}

		/**
		 * The kind of the 'linked-to' entity.
		 */
		public Builder withToEntityKind(EntityKind toEntityKindParam) {
			this.toEntityKind = toEntityKindParam;
			return this;
		}

		/**
		 * Return only records with these status codes (default: ALL).
		 */
		public Builder withStatus(List<StatusKind> statusParam) {
			this.status = statusParam;
			return this;
		}

		/**
		 * Free text search string (applied to the 'linked-to' entity).
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

		public LinkableEntityQueryFilter build() {
			LinkableEntityQueryFilter _object = new LinkableEntityQueryFilter();
			_object.setTopicId(this.topicId);
			_object.setRecursive(this.recursive);
			_object.setFromEntityId(this.fromEntityId);
			_object.setFromEntityKind(this.fromEntityKind);
			_object.setToEntityId(this.toEntityId);
			_object.setToEntityKind(this.toEntityKind);
			_object.setStatus(this.status);
			_object.setText(this.text);
			_object.setAdvancedSearch(this.advancedSearch);
			return _object;
		}

	}

	// Commented out, as InputParameter.getStringContentForAnInputTypeValue() doesn't check superclass fields.
	// @SuppressWarnings("unchecked")
	// abstract static class AbstractBuilder<B extends AbstractBuilder<B, T>, T extends LinkableEntityQueryFilter>
	// 	extends EntityLinkQueryFilter.AbstractBuilder<B, T> {

	// 	private Long topicId;
	// 	private Boolean recursive;

	// 	/**
	// 	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	// 	 */
	// 	public B withTopicId(Long topicIdParam) {
	// 		this.topicId = topicIdParam;
	// 		return (B)this;
	// 	}

	// 	/**
	// 	 * Whether queries including a topicId should be executed recursively.
	// 	 */
	// 	public B withRecursive(Boolean recursiveParam) {
	// 		this.recursive = recursiveParam;
	// 		return (B)this;
	// 	}

	// 	T build(T _object) {
	// 		super.build(_object);
	// 		_object.setTopicId(this.topicId);
	// 		_object.setRecursive(this.recursive);
	// 		return _object;
	// 	}

	// }

	// /**
	//  * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	//  * {@link #builderForLinkableEntityQueryFilter()}
	//  */
	// public static class Builder extends AbstractBuilder<Builder, LinkableEntityQueryFilter> {

	// 	@Override
	// 	public LinkableEntityQueryFilter build() {
	// 		return build(new LinkableEntityQueryFilter());
	// 	}

	// }

}
