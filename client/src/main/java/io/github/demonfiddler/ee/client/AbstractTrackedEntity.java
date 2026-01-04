/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLDirective;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * Abstract base implementation for all {@code ITrackedEntity} classes.
 */
public abstract class AbstractTrackedEntity extends AbstractBaseEntity implements ITrackedEntity {

	/**
	 * The entity kind.
	 */
	@JsonProperty("entityKind")
	@GraphQLInputParameters(names = { "format" }, types = { "FormatKind" }, mandatories = { false }, listDepths = { 0 },
		itemsMandatory = { false })
	@GraphQLScalar(fieldName = "entityKind", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String entityKind;

	/**
	 * The entity status.
	 */
	@JsonProperty("status")
	@GraphQLInputParameters(names = { "format" }, types = { "FormatKind" }, mandatories = { false }, listDepths = { 0 },
		itemsMandatory = { false })
	@GraphQLScalar(fieldName = "status", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String status;

	/**
	 * A five-star rating for the entity, interpretation depends on entity kind.
	 */
	@JsonProperty("rating")
	@GraphQLScalar( fieldName = "rating", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer rating;

	/**
	 * When the record was created.
	 */
	@JsonProperty("created")
	@JsonDeserialize(using = CustomJacksonDeserializers.DateTime.class)
	@GraphQLScalar(fieldName = "created", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime created;

	/**
	 * The user who created the record.
	 */
	@JsonProperty("createdByUser")
	@GraphQLNonScalar(fieldName = "createdByUser", graphQLTypeSimpleName = "User", javaClass = User.class,
		listDepth = 0)
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	User createdByUser;

	/**
	 * When the record was last updated.
	 */
	@JsonProperty("updated")
	@JsonDeserialize(using = CustomJacksonDeserializers.DateTime.class)
	@GraphQLScalar(fieldName = "updated", graphQLTypeSimpleName = "DateTime", javaClass = OffsetDateTime.class,
		listDepth = 0)
	OffsetDateTime updated;

	/**
	 * The user who last updated the record.
	 */
	@JsonProperty("updatedByUser")
	@GraphQLNonScalar(fieldName = "updatedByUser", graphQLTypeSimpleName = "User", javaClass = User.class,
		listDepth = 0)
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	User updatedByUser;

	/**
	 * Log of transactions involving the record.
	 */
	@JsonProperty("log")
	@GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "LogQueryFilter", "PageableInput" },
		mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage", javaClass = LogPage.class, listDepth = 0)
	LogPage log;

	/**
	 * Comments associated with the record.
	 */
	@JsonProperty("comments")
	@GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "CommentQueryFilter", "PageableInput" },
		mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
	@GraphQLNonScalar(fieldName = "comments", graphQLTypeSimpleName = "CommentPage",
		javaClass = CommentPage.class, listDepth = 0)
	CommentPage comments;

	/**
	 * The entity kind.
	 */
	@Override
	@JsonIgnore
	public void setEntityKind(String entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * The entity kind.
	 */
	@Override
	@JsonIgnore
	public String getEntityKind() {
		return this.entityKind;
	}

	/**
	 * The entity status.
	 */
	@Override
	@JsonIgnore
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * The entity status.
	 */
	@Override
	@JsonIgnore
	public String getStatus() {
		return this.status;
	}

	/**
	 * A five-star rating for the entity, interpretation depends on entity kind.
	 */
	@Override
	@JsonIgnore
	public void setRating(Integer rating) {
		this.rating = rating;
	}
  
	/**
	 * A five-star rating for the entity, interpretation depends on entity kind.
	 */
	@Override
	@JsonIgnore
	public Integer getRating() {
		return this.rating;
	}

	/**
	 * When the record was created.
	 */
	@Override
	@JsonIgnore
	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	/**
	 * When the record was created.
	 */
	@Override
	@JsonIgnore
	public OffsetDateTime getCreated() {
		return this.created;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	@JsonIgnore
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public void setCreatedByUser(User createdByUser) {
		this.createdByUser = createdByUser;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	@JsonIgnore
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public User getCreatedByUser() {
		return this.createdByUser;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	@JsonIgnore
	public void setUpdated(OffsetDateTime updated) {
		this.updated = updated;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	@JsonIgnore
	public OffsetDateTime getUpdated() {
		return this.updated;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	@JsonIgnore
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public void setUpdatedByUser(User updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	@JsonIgnore
	@GraphQLDirective(name = "@auth", parameterNames = {}, parameterTypes = {}, parameterValues = {})
	public User getUpdatedByUser() {
		return this.updatedByUser;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	@JsonIgnore
	public void setLog(LogPage log) {
		this.log = log;
	}

	/**
	 * Log of transactions involving the record.
	 */
	@Override
	@JsonIgnore
	public LogPage getLog() {
		return this.log;
	}

	@Override
	@JsonIgnore
	public void setComments(CommentPage comments) {
		this.comments = comments;
	}

	@Override
	@JsonIgnore
	public CommentPage getComments() {
		return this.comments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result
			+ ((createdByUser == null || createdByUser.getId() == null) ? 0 : createdByUser.getId().hashCode());
		result = prime * result + ((updated == null) ? 0 : updated.hashCode());
		result = prime * result
			+ ((updatedByUser == null || updatedByUser.getId() == null) ? 0 : updatedByUser.getId().hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		AbstractTrackedEntity other = (AbstractTrackedEntity)obj;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (createdByUser == null) {
			if (other.createdByUser != null)
				return false;
		} else {
			if (other.createdByUser == null)
				return false;
			Long createdByUserId = createdByUser.getId();
			Long otherFromEntityId = other.createdByUser.getId();
			if (createdByUserId == null) {
				if (otherFromEntityId != null)
					return false;
			} else if (!createdByUserId.equals(otherFromEntityId))
				return false;
		}
		if (updated == null) {
			if (other.updated != null)
				return false;
		} else if (!updated.equals(other.updated))
			return false;
		if (updatedByUser == null) {
			if (other.updatedByUser != null)
				return false;
		} else {
			if (other.updatedByUser == null)
				return false;
			Long updatedByUserId = updatedByUser.getId();
			Long otherFromEntityId = other.updatedByUser.getId();
			if (updatedByUserId == null) {
				if (otherFromEntityId != null)
					return false;
			} else if (!updatedByUserId.equals(otherFromEntityId))
				return false;
		}
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		return true;
	}

	/**
	 * The Builder that helps building instance of this POJO.
	 */
	@SuppressWarnings("unchecked")
	static abstract class Builder<B extends Builder<B, T>, T extends ITrackedEntity>
		extends AbstractBaseEntity.Builder<B, T> {

		private String entityKind;
		private String status;
		private Integer rating;
		private OffsetDateTime created;
		private User createdByUser;
		private OffsetDateTime updated;
		private User updatedByUser;
		private LogPage log;
		private CommentPage comments;

		/**
		 * The entity kind.
		 */
		public final B withEntityKind(String entityKindParam) {
			this.entityKind = entityKindParam;
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
			super.build(_object);
			_object.setEntityKind(this.entityKind);
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

	}

}
