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
	 * A five-star rating for the entity, interpretation depends on entity kind.
	 */
	@GraphQLScalar(fieldName = "rating", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer rating;

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
	* Comments associated with the record.
	*/
	@Transient
	@GraphQLNonScalar(fieldName = "comments", graphQLTypeSimpleName = "CommentPage", javaClass = CommentPage.class,
	listDepth = 0)
	CommentPage comments;

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
	 * A five-star rating for the entity, interpretation depends on entity kind.
	 */
	@Override
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * A five-star rating for the entity, interpretation depends on entity kind.
	 */
	@Override
	public Integer getRating() {
		return this.rating;
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
	public void setLog(LogPage log) {
		this.log = log;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	public LogPage getLog() {
		return this.log;
	}

	/**
	* Comments associated with the record.
	*/
	@Override
	public void setComments(CommentPage comments) {
	this.comments = comments;
	}

	/**
	* Comments associated with the record.
	*/
	@Override
	public CommentPage getComments() {
	return this.comments;
	}

	/**
	 * The Builder that helps building instances of this POJO.
	 */
	@SuppressWarnings("unchecked")
	abstract static class Builder<B extends Builder<B, T>, T extends ITrackedEntity> {

		private Long id;
		private String status;
		private Integer rating;
		private OffsetDateTime created;
		private User createdByUser;
		private OffsetDateTime updated;
		private User updatedByUser;
		private LogPage log;
		private CommentPage comments;

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
		 * A five-star rating for the entity, interpretation depends on entity kind.
		 */
		public final B withRating(Integer ratingParam) {
			this.rating = ratingParam;
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

		/**
		 * Comments associated with the record.
		 */
		public final B withComments(CommentPage commentsParam) {
			this.comments = commentsParam;
			return (B)this;
		}

		T build(T _object) {
			_object.setId(this.id);
			_object.setStatus(this.status);
			_object.setRating(this.rating);
			_object.setCreated(this.created);
			_object.setCreatedByUser(this.createdByUser);
			_object.setUpdated(this.updated);
			_object.setUpdatedByUser(this.updatedByUser);
			_object.setLog(this.log);
			_object.setComments(this.comments);
			return _object;
		}

		public abstract T build();

	}

}
