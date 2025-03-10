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

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * Parameters to control an {@code ITopicalEntity} query.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("TopicalEntityQueryFilter")
public class TopicalEntityQueryFilter extends TrackedEntityQueryFilter {

	/**
	 * The topic identifier, mandatory when querying the master list or when master = NONE.
	 */
	@GraphQLScalar(fieldName = "topicId", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long topicId;

	/**
	 * Whether queries including a topicId should be executed recursively.
	 */
	@GraphQLScalar(fieldName = "recursive", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean recursive = true;

	/**
	 * The master entity kind, if a master list is designated.
	 */
	@GraphQLScalar(fieldName = "masterEntityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind masterEntityKind;

	/**
	 * The master entity identifier, if a master list is designated.
	 */
	@GraphQLScalar(fieldName = "masterEntityId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long masterEntityId;

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

	/**
	 * The master entity kind, if a master list is designated.
	 */
	public void setMasterEntityKind(EntityKind masterEntityKind) {
		this.masterEntityKind = masterEntityKind;
	}

	/**
	 * The master entity kind, if a master list is designated.
	 */
	public EntityKind getMasterEntityKind() {
		return this.masterEntityKind;
	}

	/**
	 * The master entity identifier, if a master list is designated.
	 */
	public void setMasterEntityId(Long masterEntityId) {
		this.masterEntityId = masterEntityId;
	}

	/**
	 * The master entity identifier, if a master list is designated.
	 */
	public Long getMasterEntityId() {
		return this.masterEntityId;
	}

	public String toString() {
		return "TopicalEntityQueryFilter {" //$NON-NLS-1$
			+ "topicId: " + this.topicId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "recursive: " + this.recursive //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "masterEntityKind: " + this.masterEntityKind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "masterEntityId: " + this.masterEntityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "text: " + this.text //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "advancedSearch: " + this.advancedSearch //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "status: " + this.status //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends TrackedEntityQueryFilter.Builder {

		private Long topicId;
		private Boolean recursive = true;
		private EntityKind masterEntityKind;
		private Long masterEntityId;

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
		 * The master entity kind, if a master list is designated.
		 */
		public Builder withMasterEntityKind(EntityKind masterEntityKindParam) {
			this.masterEntityKind = masterEntityKindParam;
			return this;
		}

		/**
		 * The master entity identifier, if a master list is designated.
		 */
		public Builder withMasterEntityId(Long masterEntityIdParam) {
			this.masterEntityId = masterEntityIdParam;
			return this;
		}

		public TopicalEntityQueryFilter build() {
			TopicalEntityQueryFilter _object = new TopicalEntityQueryFilter();
			_object.setTopicId(this.topicId);
			_object.setRecursive(this.recursive);
			_object.setMasterEntityKind(this.masterEntityKind);
			_object.setMasterEntityId(this.masterEntityId);
			_object.setText(this.text);
			_object.setAdvancedSearch(this.advancedSearch);
			_object.setStatus(this.status);
			return _object;
		}

	}

}
