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

package io.github.demonfiddler.ee.client;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

/**
 * An input for creating or updating a person.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("PersonInput")
@JsonInclude(Include.NON_NULL)
public class PersonInput {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public PersonInput() {
	}

	/**
	 * The person identifier, required if updating an existing record.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The person's title(s).
	 */
	@JsonProperty("title")
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The person's first name(s).
	 */
	@JsonProperty("firstName")
	@GraphQLScalar(fieldName = "firstName", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String firstName;

	/**
	 * The person's nickname.
	 */
	@JsonProperty("nickname")
	@GraphQLScalar(fieldName = "nickname", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String nickname;

	/**
	 * The prefix to the person's last name.
	 */
	@JsonProperty("prefix")
	@GraphQLScalar(fieldName = "prefix", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String prefix;

	/**
	 * The person's last name.
	 */
	@JsonProperty("lastName")
	@GraphQLScalar(fieldName = "lastName", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String lastName;

	/**
	 * The suffix to the person's name.
	 */
	@JsonProperty("suffix")
	@GraphQLScalar(fieldName = "suffix", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String suffix;

	/**
	 * Alias name.
	 */
	@JsonProperty("alias")
	@GraphQLScalar(fieldName = "alias", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String alias;

	/**
	 * Person's description and biographical details.
	 */
	@JsonProperty("notes")
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * Person's academic qualifications.
	 */
	@JsonProperty("qualifications")
	@GraphQLScalar(fieldName = "qualifications", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String qualifications;

	/**
	 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
	 */
	@JsonProperty("country")
	@GraphQLScalar(fieldName = "country", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String country;

	/**
	 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
	 */
	@JsonProperty("rating")
	@GraphQLScalar(fieldName = "rating", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer rating;

	/**
	 * Whether the person's credentials have been checked.
	 */
	@JsonProperty("checked")
	@GraphQLScalar(fieldName = "checked", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean checked;

	/**
	 * Whether the person has authored any peer-reviewed publications.
	 */
	@JsonProperty("published")
	@GraphQLScalar(fieldName = "published", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean published;

	/**
	 * The person identifier, required if updating an existing record.
	 */
	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The person identifier, required if updating an existing record.
	 */
	@JsonProperty("id")
	public Long getId() {
		return this.id;
	}

	/**
	 * The person's title(s).
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The person's title(s).
	 */
	@JsonProperty("title")
	public String getTitle() {
		return this.title;
	}

	/**
	 * The person's first name(s).
	 */
	@JsonProperty("firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * The person's first name(s).
	 */
	@JsonProperty("firstName")
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * The person's nickname.
	 */
	@JsonProperty("nickname")
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * The person's nickname.
	 */
	@JsonProperty("nickname")
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * The prefix to the person's last name.
	 */
	@JsonProperty("prefix")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * The prefix to the person's last name.
	 */
	@JsonProperty("prefix")
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * The person's last name.
	 */
	@JsonProperty("lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * The person's last name.
	 */
	@JsonProperty("lastName")
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * The suffix to the person's name.
	 */
	@JsonProperty("suffix")
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * The suffix to the person's name.
	 */
	@JsonProperty("suffix")
	public String getSuffix() {
		return this.suffix;
	}

	/**
	 * Alias name.
	 */
	@JsonProperty("alias")
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Alias name.
	 */
	@JsonProperty("alias")
	public String getAlias() {
		return this.alias;
	}

	/**
	 * Person's description and biographical details.
	 */
	@JsonProperty("notes")
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Person's description and biographical details.
	 */
	@JsonProperty("notes")
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Person's academic qualifications.
	 */
	@JsonProperty("qualifications")
	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}

	/**
	 * Person's academic qualifications.
	 */
	@JsonProperty("qualifications")
	public String getQualifications() {
		return this.qualifications;
	}

	/**
	 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
	 */
	@JsonProperty("country")
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * The ISO-3166-1 alpha-2 code for the primary country associated with the person.
	 */
	@JsonProperty("country")
	public String getCountry() {
		return this.country;
	}

	/**
	 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
	 */
	@JsonProperty("rating")
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * A five-star rating for the person, intended to reflect credibility, experience, qualifications, etc.
	 */
	@JsonProperty("rating")
	public Integer getRating() {
		return this.rating;
	}

	/**
	 * Whether the person's credentials have been checked.
	 */
	@JsonProperty("checked")
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * Whether the person's credentials have been checked.
	 */
	@JsonProperty("checked")
	public Boolean getChecked() {
		return this.checked;
	}

	/**
	 * Whether the person has authored any peer-reviewed publications.
	 */
	@JsonProperty("published")
	public void setPublished(Boolean published) {
		this.published = published;
	}

	/**
	 * Whether the person has authored any peer-reviewed publications.
	 */
	@JsonProperty("published")
	public Boolean getPublished() {
		return this.published;
	}

	/**
	 * This method is called during the json deserialization process, by the {@link GraphQLObjectMapper}, each time an
	 * alias value is read from the json.
	 * @param aliasName
	 * @param aliasDeserializedValue
	 */
	public void setAliasValue(String aliasName, Object aliasDeserializedValue) {
		this.aliasValues.put(aliasName, aliasDeserializedValue);
	}

	/**
	 * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
	 * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
	 * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
	 * this method with a basis json deserialization, but this value won't be the proper custom scalar value.
	 * @param alias
	 * @return
	 */
	public Object getAliasValue(String alias) {
		return this.aliasValues.get(alias);
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