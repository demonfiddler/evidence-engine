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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;

import graphql.schema.DataFetchingEnvironment;

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import com.graphql_java_generator.annotation.GraphQLDirective;

/**
 * An input for creating or updating a person.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("PersonInput")
@SuppressWarnings("unused")
public class PersonInput {

	/**
	 * The person identifier, required if updating an existing record.
	 */
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The person's title(s).
	 */
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The person's first name(s).
	 */
	@GraphQLScalar(fieldName = "firstName", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String firstName;

	/**
	 * The person's nickname.
	 */
	@GraphQLScalar(fieldName = "nickname", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String nickname;

	/**
	 * The prefix to the person's last name.
	 */
	@GraphQLScalar(fieldName = "prefix", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String prefix;

	/**
	 * The person's last name.
	 */
	@GraphQLScalar(fieldName = "lastName", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String lastName;

	/**
	 * The suffix to the person's name.
	 */
	@GraphQLScalar(fieldName = "suffix", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String suffix;

	/**
	 * Alias name.
	 */
	@GraphQLScalar(fieldName = "alias", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String alias;

	/**
	 * Person's description and biographical details.
	 */
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * Person's academic qualifications.
	 */
	@GraphQLScalar(fieldName = "qualifications", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String qualifications;

	/**
	 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
	 */
	@GraphQLScalar(fieldName = "country", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String country;

	/**
	 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
	 */
	@GraphQLScalar(fieldName = "rating", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer rating;

	/**
	 * Whether the person's credentials have been checked.
	 */
	@GraphQLScalar(fieldName = "checked", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean checked;

	/**
	 * Whether the person has authored any peer-reviewed publications.
	 */
	@GraphQLScalar(fieldName = "published", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean published;

	/**
	 * The person identifier, required if updating an existing record.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The person identifier, required if updating an existing record.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * The person's title(s).
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The person's title(s).
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * The person's first name(s).
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * The person's first name(s).
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * The person's nickname.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * The person's nickname.
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * The prefix to the person's last name.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * The prefix to the person's last name.
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * The person's last name.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * The person's last name.
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * The suffix to the person's name.
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * The suffix to the person's name.
	 */
	public String getSuffix() {
		return this.suffix;
	}

	/**
	 * Alias name.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Alias name.
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * Person's description and biographical details.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Person's description and biographical details.
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Person's academic qualifications.
	 */
	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}

	/**
	 * Person's academic qualifications.
	 */
	public String getQualifications() {
		return this.qualifications;
	}

	/**
	 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
	 */
	public Integer getRating() {
		return this.rating;
	}

	/**
	 * Whether the person's credentials have been checked.
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * Whether the person's credentials have been checked.
	 */
	public Boolean getChecked() {
		return this.checked;
	}

	/**
	 * Whether the person has authored any peer-reviewed publications.
	 */
	public void setPublished(Boolean published) {
		this.published = published;
	}

	/**
	 * Whether the person has authored any peer-reviewed publications.
	 */
	public Boolean getPublished() {
		return this.published;
	}

	public String toString() {
		return "PersonInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "title: " + this.title //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "firstName: " + this.firstName //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "nickname: " + this.nickname //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "prefix: " + this.prefix //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "lastName: " + this.lastName //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "suffix: " + this.suffix //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "alias: " + this.alias //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "notes: " + this.notes //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "qualifications: " + this.qualifications //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "country: " + this.country //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "rating: " + this.rating //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "checked: " + this.checked //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "published: " + this.published //$NON-NLS-1$
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
		private String title;
		private String firstName;
		private String nickname;
		private String prefix;
		private String lastName;
		private String suffix;
		private String alias;
		private String notes;
		private String qualifications;
		private String country;
		private Integer rating;
		private Boolean checked;
		private Boolean published;

		/**
		 * The person identifier, required if updating an existing record.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The person's title(s).
		 */
		public Builder withTitle(String titleParam) {
			this.title = titleParam;
			return this;
		}

		/**
		 * The person's first name(s).
		 */
		public Builder withFirstName(String firstNameParam) {
			this.firstName = firstNameParam;
			return this;
		}

		/**
		 * The person's nickname.
		 */
		public Builder withNickname(String nicknameParam) {
			this.nickname = nicknameParam;
			return this;
		}

		/**
		 * The prefix to the person's last name.
		 */
		public Builder withPrefix(String prefixParam) {
			this.prefix = prefixParam;
			return this;
		}

		/**
		 * The person's last name.
		 */
		public Builder withLastName(String lastNameParam) {
			this.lastName = lastNameParam;
			return this;
		}

		/**
		 * The suffix to the person's name.
		 */
		public Builder withSuffix(String suffixParam) {
			this.suffix = suffixParam;
			return this;
		}

		/**
		 * Alias name.
		 */
		public Builder withAlias(String aliasParam) {
			this.alias = aliasParam;
			return this;
		}

		/**
		 * Person's description and biographical details.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		/**
		 * Person's academic qualifications.
		 */
		public Builder withQualifications(String qualificationsParam) {
			this.qualifications = qualificationsParam;
			return this;
		}

		/**
		 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
		 */
		public Builder withCountry(String countryParam) {
			this.country = countryParam;
			return this;
		}

		/**
		 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
		 */
		public Builder withRating(Integer ratingParam) {
			this.rating = ratingParam;
			return this;
		}

		/**
		 * Whether the person's credentials have been checked.
		 */
		public Builder withChecked(Boolean checkedParam) {
			this.checked = checkedParam;
			return this;
		}

		/**
		 * Whether the person has authored any peer-reviewed publications.
		 */
		public Builder withPublished(Boolean publishedParam) {
			this.published = publishedParam;
			return this;
		}

		public PersonInput build() {
			PersonInput _object = new PersonInput();
			_object.setId(this.id);
			_object.setTitle(this.title);
			_object.setFirstName(this.firstName);
			_object.setNickname(this.nickname);
			_object.setPrefix(this.prefix);
			_object.setLastName(this.lastName);
			_object.setSuffix(this.suffix);
			_object.setAlias(this.alias);
			_object.setNotes(this.notes);
			_object.setQualifications(this.qualifications);
			_object.setCountry(this.country);
			_object.setRating(this.rating);
			_object.setChecked(this.checked);
			_object.setPublished(this.published);
			return _object;
		}
	}

}
