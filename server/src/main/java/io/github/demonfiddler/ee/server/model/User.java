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

import java.time.OffsetDateTime;
import java.util.List;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

/**
 * Describes a user of the system.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Entity
@GraphQLObjectType("User")
public class User implements IBaseEntity, ITrackedEntity {

	/**
	 * The immutable, unique user identifier (system-assigned).
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
	 * The (mutable?) unique login name (user-assigned).
	 */
	@GraphQLScalar(fieldName = "login", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String login;

	/**
	 * The user's first name.
	 */
	@GraphQLScalar(fieldName = "firstName", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String firstName;

	/**
	 * The user's last name.
	 */
	@GraphQLScalar(fieldName = "lastName", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String lastName;

	/**
	 * The user's email address.
	 */
	@GraphQLScalar(fieldName = "email", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String email;

	// TODO: consider whether to reveal this through the GraphQL API.
	/**
	 * A hash of the user's password.
	 */
	@GraphQLScalar(fieldName = "passwordHash", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String passwordHash;

	/**
	 * The permissions granted to the user.
	 */
	@Transient
	@GraphQLScalar(fieldName = "permissions", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 1)
	List<String> permissions;

	/**
	 * The immutable, unique user identifier (system-assigned).
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The immutable, unique user identifier (system-assigned).
	 */
	@Override
	public Long getId() {
		return this.id;
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
	public void setCreatedByUser(User createdByUser) {
		this.createdByUser = createdByUser;
	}

	/**
	 * The user who created the record.
	 */
	@Override
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
	public void setUpdatedByUser(User updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
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
	 * The (mutable?) unique login name (user-assigned).
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * The (mutable?) unique login name (user-assigned).
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * The user's first name.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * The user's first name.
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * The user's last name.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * The user's last name.
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * The user's email address.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * The user's email address.
	 */
	public String getEmail() {
		return this.email;
	}

	// TODO: consider whether to reveal this through the GraphQL API.
	/**
	 * A hash of the user's password.
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	// TODO: consider whether to reveal this through the GraphQL API.
	/**
	 * A hash of the user's password.
	 */
	public String getPasswordHash() {
		return this.passwordHash;
	}

	/**
	 * The permissions granted to the user.
	 */
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	/**
	 * The permissions granted to the user.
	 */
	public List<String> getPermissions() {
		return this.permissions;
	}

	public String toString() {
		return "User {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "status: " + this.status //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "created: " + this.created //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "createdByUser: " + this.createdByUser //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "updated: " + this.updated //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "updatedByUser: " + this.updatedByUser //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "log: " + this.log //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "login: " + this.login //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "firstName: " + this.firstName //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "lastName: " + this.lastName //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "email: " + this.email //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "passwordHash: " + this.passwordHash //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "permissions: " + this.permissions //$NON-NLS-1$
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
		private String status;
		private OffsetDateTime created;
		private User createdByUser;
		private OffsetDateTime updated;
		private User updatedByUser;
		private LogPage log;
		private String login;
		private String firstName;
		private String lastName;
		private String email;
		private String passwordHash;
		private List<String> permissions;

		/**
		 * The immutable, unique user identifier (system-assigned).
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The entity status.
		 */
		public Builder withStatus(String statusParam) {
			this.status = statusParam;
			return this;
		}

		/**
		 * When the record was created.
		 */
		public Builder withCreated(OffsetDateTime createdParam) {
			this.created = createdParam;
			return this;
		}

		/**
		 * The user who created the record.
		 */
		public Builder withCreatedByUser(User createdByUserParam) {
			this.createdByUser = createdByUserParam;
			return this;
		}

		/**
		 * When the record was last updated.
		 */
		public Builder withUpdated(OffsetDateTime updatedParam) {
			this.updated = updatedParam;
			return this;
		}

		/**
		 * The user who last updated the record.
		 */
		public Builder withUpdatedByUser(User updatedByUserParam) {
			this.updatedByUser = updatedByUserParam;
			return this;
		}

		/**
		 * Log of transactions involving the record.
		 */
		public Builder withLog(LogPage logParam) {
			this.log = logParam;
			return this;
		}

		/**
		 * The (mutable?) unique login name (user-assigned).
		 */
		public Builder withLogin(String loginParam) {
			this.login = loginParam;
			return this;
		}

		/**
		 * The user's first name.
		 */
		public Builder withFirstName(String firstNameParam) {
			this.firstName = firstNameParam;
			return this;
		}

		/**
		 * The user's last name.
		 */
		public Builder withLastName(String lastNameParam) {
			this.lastName = lastNameParam;
			return this;
		}

		/**
		 * The user's email address.
		 */
		public Builder withEmail(String emailParam) {
			this.email = emailParam;
			return this;
		}

		// TODO: consider whether to reveal this through the GraphQL API.
		/**
		 * A hash of the user's password.
		 */
		public Builder withPasswordHash(String passwordHashParam) {
			this.passwordHash = passwordHashParam;
			return this;
		}

		/**
		 * The permissions granted to the user.
		 */
		public Builder withPermissions(List<String> permissionsParam) {
			this.permissions = permissionsParam;
			return this;
		}

		public User build() {
			User _object = new User();
			_object.setId(this.id);
			_object.setStatus(this.status);
			_object.setCreated(this.created);
			_object.setCreatedByUser(this.createdByUser);
			_object.setUpdated(this.updated);
			_object.setUpdatedByUser(this.updatedByUser);
			_object.setLog(this.log);
			_object.setLogin(this.login);
			_object.setFirstName(this.firstName);
			_object.setLastName(this.lastName);
			_object.setEmail(this.email);
			_object.setPasswordHash(this.passwordHash);
			_object.setPermissions(this.permissions);
			return _object;
		}

	}

}
