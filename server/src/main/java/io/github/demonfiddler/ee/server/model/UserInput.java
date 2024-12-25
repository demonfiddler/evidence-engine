/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
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

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * Input for creating or updating a user of the system.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("UserInput")
public class UserInput {

	/**
	 * The immutable, unique user identifier (system-assigned).
	 */
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

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

	/**
	 * A hash of the user's password.
	 */
	@GraphQLScalar(fieldName = "passwordHash", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String passwordHash;

	/**
	 * The immutable, unique user identifier (system-assigned).
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The immutable, unique user identifier (system-assigned).
	 */
	public Long getId() {
		return this.id;
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

	/**
	 * A hash of the user's password.
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/**
	 * A hash of the user's password.
	 */
	public String getPasswordHash() {
		return this.passwordHash;
	}

	public String toString() {
		return "UserInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
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
		private String login;
		private String firstName;
		private String lastName;
		private String email;
		private String passwordHash;

		/**
		 * The immutable, unique user identifier (system-assigned).
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
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

		/**
		 * A hash of the user's password.
		 */
		public Builder withPasswordHash(String passwordHashParam) {
			this.passwordHash = passwordHashParam;
			return this;
		}

		public UserInput build() {
			UserInput _object = new UserInput();
			_object.setId(this.id);
			_object.setLogin(this.login);
			_object.setFirstName(this.firstName);
			_object.setLastName(this.lastName);
			_object.setEmail(this.email);
			_object.setPasswordHash(this.passwordHash);
			return _object;
		}

	}

}
