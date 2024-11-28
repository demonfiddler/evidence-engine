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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

import io.github.demonfiddler.ee.client.util.CustomJacksonSerializers;

/**
 * An input for creating or updating a journal.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("JournalInput")
@JsonInclude(Include.NON_NULL)
public class JournalInput {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public JournalInput() {
	}

	/**
	 * The abreviation identifier, required if updating an existing record.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The full journal title.
	 */
	@JsonProperty("title")
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The official abbreviation.
	 */
	@JsonProperty("abbreviation")
	@GraphQLScalar(fieldName = "abbreviation", graphQLTypeSimpleName = "String", javaClass = String.class,
		listDepth = 0)
	String abbreviation;

	@JsonProperty("url")
	@JsonSerialize(using = CustomJacksonSerializers.URL.class)
	@GraphQLScalar(fieldName = "url", graphQLTypeSimpleName = "URL", javaClass = java.net.URL.class, listDepth = 0)
	java.net.URL url;

	/**
	 * The International Standard Serial Number.
	 */
	@JsonProperty("issn")
	@JsonSerialize(using = CustomJacksonSerializers.ISSN.class)
	@GraphQLScalar(fieldName = "issn", graphQLTypeSimpleName = "ISSN", javaClass = String.class, listDepth = 0)
	String issn;

	/**
	 * The ID of the journal publisher.
	 */
	@JsonProperty("publisherId")
	@JsonSerialize(using = CustomJacksonSerializers.Long.class)
	@GraphQLScalar(fieldName = "publisherId", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long publisherId;

	/**
	 * Notes about the journal.
	 */
	@JsonProperty("notes")
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * The abreviation identifier, required if updating an existing record.
	 */
	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The abreviation identifier, required if updating an existing record.
	 */
	@JsonProperty("id")
	public Long getId() {
		return this.id;
	}

	/**
	 * The full journal title.
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The full journal title.
	 */
	@JsonProperty("title")
	public String getTitle() {
		return this.title;
	}

	/**
	 * The official abbreviation.
	 */
	@JsonProperty("abbreviation")
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * The official abbreviation.
	 */
	@JsonProperty("abbreviation")
	public String getAbbreviation() {
		return this.abbreviation;
	}

	@JsonProperty("url")
	public void setUrl(java.net.URL url) {
		this.url = url;
	}

	@JsonProperty("url")
	public java.net.URL getUrl() {
		return this.url;
	}

	/**
	 * The International Standard Serial Number.
	 */
	@JsonProperty("issn")
	public void setIssn(String issn) {
		this.issn = issn;
	}

	/**
	 * The International Standard Serial Number.
	 */
	@JsonProperty("issn")
	public String getIssn() {
		return this.issn;
	}

	/**
	 * The ID of the journal publisher.
	 */
	@JsonProperty("publisherId")
	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	/**
	 * The ID of the journal publisher.
	 */
	@JsonProperty("publisherId")
	public Long getPublisherId() {
		return this.publisherId;
	}

	/**
	 * Notes about the journal.
	 */
	@JsonProperty("notes")
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Notes about the journal.
	 */
	@JsonProperty("notes")
	public String getNotes() {
		return this.notes;
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
		return "JournalInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "title: " + this.title //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "abbreviation: " + this.abbreviation //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "url: " + this.url //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "issn: " + this.issn //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "publisherId: " + this.publisherId //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "notes: " + this.notes //$NON-NLS-1$
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
		private String abbreviation;
		private java.net.URL url;
		private String issn;
		private Long publisherId;
		private String notes;

		/**
		 * The abreviation identifier, required if updating an existing record.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The full journal title.
		 */
		public Builder withTitle(String titleParam) {
			this.title = titleParam;
			return this;
		}

		/**
		 * The official abbreviation.
		 */
		public Builder withAbbreviation(String abbreviationParam) {
			this.abbreviation = abbreviationParam;
			return this;
		}

		public Builder withUrl(java.net.URL urlParam) {
			this.url = urlParam;
			return this;
		}

		/**
		 * The International Standard Serial Number.
		 */
		public Builder withIssn(String issnParam) {
			this.issn = issnParam;
			return this;
		}

		/**
		 * The ID of the journal publisher.
		 */
		public Builder withPublisherId(Long publisherIdParam) {
			this.publisherId = publisherIdParam;
			return this;
		}

		/**
		 * Notes about the journal.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		public JournalInput build() {
			JournalInput _object = new JournalInput();
			_object.setId(this.id);
			_object.setTitle(this.title);
			_object.setAbbreviation(this.abbreviation);
			_object.setUrl(this.url);
			_object.setIssn(this.issn);
			_object.setPublisherId(this.publisherId);
			_object.setNotes(this.notes);
			return _object;
		}

	}

}