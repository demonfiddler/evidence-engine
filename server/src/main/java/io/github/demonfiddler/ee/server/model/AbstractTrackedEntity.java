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

import static jakarta.persistence.DiscriminatorType.STRING;
import static jakarta.persistence.InheritanceType.JOINED;

import java.time.OffsetDateTime;

import com.graphql_java_generator.annotation.GraphQLDirective;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * <p>
 * Abstract base implementation for all {@code ITrackedEntity} classes.
 * </p>
 * Implementation note: this class is a JPA entity that uses the JOINED table inheritance strategy. One might be tempted
 * to think that it could extend a notional {@code AbstractBaseEntity} class in order to inherit the "id" field, which
 * would also be useful to other classes such as {@code Log}. However, we don't want {@code Log} to share the base
 * "entity" table, which is effectively used only by ITrackedEntity classes and subclasses. This is the reason why there
 * is no {@code AbstractBaseEntity} class in the hierarchy.
 */
@Entity
@Table(name = "entity")
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "dtype", discriminatorType = STRING)
public abstract class AbstractTrackedEntity implements ITrackedEntity {

	/**
	 * The unique entity identifier.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The entity status.
	 */
	@GraphQLScalar(fieldName = "status", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String status;

	/**
	 * When the record was created.
	 */
	@GraphQLScalar(fieldName = "created", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime created;

	/**
	 * The user who created the record.
	 */
	@GraphQLNonScalar(fieldName = "createdByUser", graphQLTypeSimpleName = "User", javaClass = User.class,
		listDepth = 0)
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_user_id", nullable = false)
	User createdByUser;

	/**
	 * When the record was last updated.
	 */
	@GraphQLScalar(fieldName = "updated", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime updated;

	/**
	 * The user who last updated the record.
	 */
	@GraphQLNonScalar(fieldName = "updatedByUser", graphQLTypeSimpleName = "User", javaClass = User.class,
		listDepth = 0)
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "updated_by_user_id", nullable = true)
	User updatedByUser;

	/**
	 * Log of transactions involving the record.
	 */
	@Transient
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class, listDepth = 0)
	LogPage log;

	/**
	 * The unique claim identifier.
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique claim identifier.
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * The entity kind.
	 */
	@Override
	public abstract String getEntityKind();

	/**
	 * The entity kind.
	 */
	@Override
	public final void setEntityKind(String entityKind) {
		throw new UnsupportedOperationException("setEntityKind");
	}

	/**
	 * The entity status.
	 */
	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * The entity status.
	 */
	@Override
	public String getStatus() {
		return this.status;
	}

	/**
	 * When the record was created.
	 */
	@Override
	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	/**
	 * When the record was created.
	 */
	@Override
	public OffsetDateTime getCreated() {
		return this.created;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public void setCreatedByUser(User createdByUser) {
		this.createdByUser = createdByUser;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public User getCreatedByUser() {
		return this.createdByUser;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	public void setUpdated(OffsetDateTime updated) {
		this.updated = updated;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	public OffsetDateTime getUpdated() {
		return this.updated;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public void setUpdatedByUser(User updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public User getUpdatedByUser() {
		return this.updatedByUser;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	public final void setLog(LogPage log) {
		this.log = log;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	public final LogPage getLog() {
		return this.log;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((createdByUser == null || createdByUser.id == null) ? 0 : createdByUser.id.hashCode());
		result = prime * result + ((updated == null) ? 0 : updated.hashCode());
		result = prime * result + ((updatedByUser == null || updatedByUser.id == null) ? 0 : updatedByUser.id.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractTrackedEntity other = (AbstractTrackedEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (createdByUser == null) {
			if (other.createdByUser != null)
				return false;
		} else if (!createdByUser.equals(other.createdByUser))
			return false;
		if (updated == null) {
			if (other.updated != null)
				return false;
		} else if (!updated.equals(other.updated))
			return false;
		if (updatedByUser == null) {
			if (other.updatedByUser != null)
				return false;
		} else if (!updatedByUser.equals(other.updatedByUser))
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}

	/**
	 * The Builder that helps building instances of this POJO.
	 */
	@SuppressWarnings("unchecked")
	abstract static class Builder<B extends Builder<B, T>, T extends ITrackedEntity> {

		private Long id;
		private String status;
		private OffsetDateTime created;
		private User createdByUser;
		private OffsetDateTime updated;
		private User updatedByUser;
		private LogPage log;

		/**
		 * The unique record identifier.
		 */
		public final B withId(Long idParam) {
			this.id = idParam;
			return (B)this;
		}

		/**
		 * The entity status.
		 */
		public final B withStatus(String statusParam) {
			this.status = statusParam;
			return (B)this;
		}

		/**
		 * When the record was created.
		 */
		public final B withCreated(OffsetDateTime createdParam) {
			this.created = createdParam;
			return (B)this;
		}

		/**
		 * The user who created the record.
		 */
		public final B withCreatedByUser(User createdByUserParam) {
			this.createdByUser = createdByUserParam;
			return (B)this;
		}

		/**
		 * When the record was last updated.
		 */
		public final B withUpdated(OffsetDateTime updatedParam) {
			this.updated = updatedParam;
			return (B)this;
		}

		/**
		 * The user who last updated the record.
		 */
		public final B withUpdatedByUser(User updatedByUserParam) {
			this.updatedByUser = updatedByUserParam;
			return (B)this;
		}

		/**
		 * Log of transactions involving the record.
		 */
		public final B withLog(LogPage logParam) {
			this.log = logParam;
			return (B)this;
		}

		T build(T t) {
			t.setId(this.id);
			t.setStatus(this.status);
			t.setCreated(this.created);
			t.setCreatedByUser(this.createdByUser);
			t.setUpdated(this.updated);
			t.setUpdatedByUser(this.updatedByUser);
			t.setLog(this.log);
			return t;
		}

		public abstract T build();

	}

}
