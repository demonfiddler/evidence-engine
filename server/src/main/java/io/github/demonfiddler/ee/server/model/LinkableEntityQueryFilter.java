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

import com.graphql_java_generator.annotation.GraphQLScalar;

public class LinkableEntityQueryFilter extends EntityLinkQueryFilter {

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	@GraphQLScalar(fieldName = "topicId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long topicId;

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	@GraphQLScalar(fieldName = "recursive", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean recursive;

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	public Long getTopicId() {
		return this.topicId;
	}

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	public Boolean getRecursive() {
		return this.recursive;
	}

	public String toString() {
		return "LinkableEntityQueryFilter {" //
			+ "topicId: " + this.topicId //
			+ ", " //
			+ "recursive: " + this.recursive //
			+ ", " //
			+ "fromEntityKind: " + this.fromEntityKind //
			+ ", " //
			+ "fromEntityId: " + this.fromEntityId //
			+ ", " //
			+ "toEntityKind: " + this.toEntityKind //
			+ ", " //
			+ "toEntityId: " + this.toEntityId //
			+ ", " //
			+ "status: " + this.status //
			+ ", " //
			+ "text: " + this.text //
			+ ", " //
			+ "advancedSearch: " + this.advancedSearch //
			+ "}"; //
	}

	public static Builder builderForLinkableEntityQueryFilter() {
		return new Builder();
	}

	@SuppressWarnings("unchecked")
	abstract static class AbstractBuilder<B extends AbstractBuilder<B, T>, T extends LinkableEntityQueryFilter>
		extends EntityLinkQueryFilter.AbstractBuilder<B, T> {

		private Long topicId;
		private Boolean recursive;

		/**
		 * The topic identifier, mandatory when querying the master list or when master = NONE.
		 */
		public B withTopicId(Long topicIdParam) {
			this.topicId = topicIdParam;
			return (B)this;
		}

		/**
		 * Whether queries including a topicId should be executed recursively.
		 */
		public B withRecursive(Boolean recursiveParam) {
			this.recursive = recursiveParam;
			return (B)this;
		}

		T build(T _object) {
			super.build(_object);
			_object.setTopicId(this.topicId);
			_object.setRecursive(this.recursive);
			return _object;
		}

	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class by calling the
	 * {@link #builderForLinkableEntityQueryFilter()} method.
	 */
	public static class Builder extends AbstractBuilder<Builder, LinkableEntityQueryFilter> {

		@Override
		public LinkableEntityQueryFilter build() {
			return build(new LinkableEntityQueryFilter());
		}

	}

}
