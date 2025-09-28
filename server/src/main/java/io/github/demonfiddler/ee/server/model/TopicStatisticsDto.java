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

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;

/**
 * A DTO for building statistics about a given topic.
 */
@Entity
@SqlResultSetMapping(name = "TopicStatisticsDtoMapping",
	classes = {
		@ConstructorResult(targetClass = TopicStatisticsDto.class,
			columns = {
				@ColumnResult(name = "topicId", type = Long.class),
				@ColumnResult(name = "entityKind", type = String.class),
				@ColumnResult(name = "count", type = Long.class)
			}),
	}
)
public class TopicStatisticsDto {

	/**
	 * The topic in question.
	 */
	@Id
	Long topicId;

	/**
	 * The entity kind.
	 */
	@GraphQLScalar(fieldName = "entityKind", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String entityKind;

	/**
	 * The number of entities of that kind.
	 */
	@GraphQLScalar(fieldName = "count", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long count;

	public TopicStatisticsDto(Long topicId, String entityKind, Long count) {
		this.topicId = topicId;
		this.entityKind = entityKind;
		this.count = count;
	}

	/**
	 * The ID of the related topic.
	 */
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	/**
	 * The ID of the related topic.
	 */
	public Long getTopicId() {
		return this.topicId;
	}

	/**
	 * The entity kind.
	 */
	public void setEntityKind(String entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * The entity kind.
	 */
	public String getEntityKind() {
		return this.entityKind;
	}

	/**
	 * The number of entities of that kind.
	 */
	public void setCount(Long count) {
		this.count = count;
	}

	/**
	 * The number of entities of that kind.
	 */
	public Long getCount() {
		return this.count;
	}

	@Override
	public String toString() {
		return "TopicStatisticsDto[topicId=" + topicId + //
		", entityKind=" + entityKind + //
		", count=" + count + ']';
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {

		private Long topicId;
		private String entityKind;
		private Long count;

		/**
		 * The topic in question.
		 */
		public Builder withTopicId(Long topicIdParam) {
			this.topicId = topicIdParam;
			return this;
		}

		/**
		 * The entity kind.
		 */
		public Builder withEntityKind(String entityKindParam) {
			this.entityKind = entityKindParam;
			return this;
		}

		/**
		 * The number of entities of that kind.
		 */
		public Builder withCount(Long countParam) {
			this.count = countParam;
			return this;
		}

		public TopicStatisticsDto build() {
			// Hibernate insists on initialising all DTO fields in the constructor.
			return new TopicStatisticsDto(this.topicId, this.entityKind, this.count);
		}

	}

}
