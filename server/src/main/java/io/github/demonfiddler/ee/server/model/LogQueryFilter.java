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

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import graphql.schema.DataFetchingEnvironment;

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import java.util.List;

import com.graphql_java_generator.annotation.GraphQLDirective;

/**
 * For filtering log records.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("LogQueryFilter")
@SuppressWarnings("unused")
public class LogQueryFilter {

	/**
	 * Restrict to specific entity kind.
	 */
	@GraphQLScalar(fieldName = "entityKind", graphQLTypeSimpleName = "EntityKind", javaClass = EntityKind.class,
		listDepth = 0)
	EntityKind entityKind;

	/**
	 * Restrict to specific entity ID.
	 */
	@GraphQLScalar(fieldName = "entityId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long entityId;

	/**
	 * Restrict to transactions performed by a specific user.
	 */
	@GraphQLScalar(fieldName = "userId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long userId;

	/**
	 * Restrict to specific transaction kinds.
	 */
	@GraphQLScalar(fieldName = "transactionKinds", graphQLTypeSimpleName = "TransactionKind",
		javaClass = TransactionKind.class, listDepth = 1)
	List<TransactionKind> transactionKinds;

	/**
	 * Timestamp of first log entry to include.
	 */
	@GraphQLScalar(fieldName = "from", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime from;

	/**
	 * Timestamp of last log entry to include.
	 */
	@GraphQLScalar(fieldName = "to", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime to;

	/**
	 * Restrict to specific entity kind.
	 */
	public void setEntityKind(EntityKind entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * Restrict to specific entity kind.
	 */
	public EntityKind getEntityKind() {
		return this.entityKind;
	}

	/**
	 * Restrict to specific entity ID.
	 */
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/**
	 * Restrict to specific entity ID.
	 */
	public Long getEntityId() {
		return this.entityId;
	}

	/**
	 * Restrict to transactions performed by a specific user.
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Restrict to transactions performed by a specific user.
	 */
	public Long getUserId() {
		return this.userId;
	}

	/**
	 * Restrict to specific transaction kinds.
	 */
	public void setTransactionKinds(List<TransactionKind> transactionKinds) {
		this.transactionKinds = transactionKinds;
	}

	/**
	 * Restrict to specific transaction kinds.
	 */
	public List<TransactionKind> getTransactionKinds() {
		return this.transactionKinds;
	}

	/**
	 * Timestamp of first log entry to include.
	 */
	public void setFrom(java.time.OffsetDateTime from) {
		this.from = from;
	}

	/**
	 * Timestamp of first log entry to include.
	 */
	public java.time.OffsetDateTime getFrom() {
		return this.from;
	}

	/**
	 * Timestamp of last log entry to include.
	 */
	public void setTo(java.time.OffsetDateTime to) {
		this.to = to;
	}

	/**
	 * Timestamp of last log entry to include.
	 */
	public java.time.OffsetDateTime getTo() {
		return this.to;
	}

	public String toString() {
		return "LogQueryFilter {" //$NON-NLS-1$
			+ "entityKind: " + this.entityKind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entityId: " + this.entityId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "userId: " + this.userId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "transactionKinds: " + this.transactionKinds //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "from: " + this.from //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "to: " + this.to //$NON-NLS-1$
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
		private EntityKind entityKind;
		private Long entityId;
		private Long userId;
		private List<TransactionKind> transactionKinds;
		private OffsetDateTime from;
		private OffsetDateTime to;

		/**
		 * Restrict to specific entity kind.
		 */
		public Builder withEntityKind(EntityKind entityKindParam) {
			this.entityKind = entityKindParam;
			return this;
		}

		/**
		 * Restrict to specific entity ID.
		 */
		public Builder withEntityId(Long entityIdParam) {
			this.entityId = entityIdParam;
			return this;
		}

		/**
		 * Restrict to transactions performed by a specific user.
		 */
		public Builder withUserId(Long userIdParam) {
			this.userId = userIdParam;
			return this;
		}

		/**
		 * Restrict to specific transaction kinds.
		 */
		public Builder withTransactionKinds(List<TransactionKind> transactionKindsParam) {
			this.transactionKinds = transactionKindsParam;
			return this;
		}

		/**
		 * Timestamp of first log entry to include.
		 */
		public Builder withFrom(OffsetDateTime fromParam) {
			this.from = fromParam;
			return this;
		}

		/**
		 * Timestamp of last log entry to include.
		 */
		public Builder withTo(OffsetDateTime toParam) {
			this.to = toParam;
			return this;
		}

		public LogQueryFilter build() {
			LogQueryFilter _object = new LogQueryFilter();
			_object.setEntityKind(this.entityKind);
			_object.setEntityId(this.entityId);
			_object.setUserId(this.userId);
			_object.setTransactionKinds(this.transactionKinds);
			_object.setFrom(this.from);
			_object.setTo(this.to);
			return _object;
		}
	}

}