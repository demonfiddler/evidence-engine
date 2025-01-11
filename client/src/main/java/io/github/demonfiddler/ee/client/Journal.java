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

package io.github.demonfiddler.ee.client;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphQLObjectMapper;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

/**
 * Journal details including abbreviation, publisher, etc.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("Journal")
@JsonInclude(Include.NON_NULL)
public class Journal implements IBaseEntity, ITrackedEntity {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public Journal() {
	}

	/**
	 * The unique abreviation identifier.
	 */
	@JsonProperty("id")
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The entity status.
	 */
	@JsonProperty("status")
	@GraphQLInputParameters(names = { "format" }, types = { "FormatKind" }, mandatories = { false }, listDepths = { 0 },
		itemsMandatory = { false })
	@GraphQLScalar(fieldName = "status", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String status;

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

	/**
	 * Web link to the journal's home page.
	 */
	@JsonProperty("url")
	@JsonDeserialize(using = CustomJacksonDeserializers.URL.class)
	@GraphQLScalar(fieldName = "url", graphQLTypeSimpleName = "URL", javaClass = URL.class, listDepth = 0)
	URL url;

	/**
	 * The International Standard Serial Number.
	 */
	@JsonProperty("issn")
	@JsonDeserialize(using = CustomJacksonDeserializers.ISSN.class)
	@GraphQLScalar(fieldName = "issn", graphQLTypeSimpleName = "ISSN", javaClass = String.class, listDepth = 0)
	String issn;

	/**
	 * The journal publisher.
	 */
	@JsonProperty("publisher")
	@GraphQLNonScalar(fieldName = "publisher", graphQLTypeSimpleName = "Publisher", javaClass = Publisher.class,
		listDepth = 0)
	Publisher publisher;

	/**
	 * Notes about the journal.
	 */
	@JsonProperty("notes")
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	/**
	 * The unique abreviation identifier.
	 */
	@Override
	@JsonIgnore
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique abreviation identifier.
	 */
	@Override
	@JsonIgnore
	public Long getId() {
		return this.id;
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
	public void setCreatedByUser(User createdByUser) {
		this.createdByUser = createdByUser;
	}

	/**
	 * The user who created the record.
	 */
	@Override
	@JsonIgnore
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
	public void setUpdatedByUser(User updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	/**
	 * The user who last updated the record.
	 */
	@Override
	@JsonIgnore
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

	/**
	 * Web link to the journal's home page.
	 */
	@JsonProperty("url")
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * Web link to the journal's home page.
	 */
	@JsonProperty("url")
	public URL getUrl() {
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
	 * The journal publisher.
	 */
	@JsonProperty("publisher")
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	/**
	 * The journal publisher.
	 */
	@JsonProperty("publisher")
	public Publisher getPublisher() {
		return this.publisher;
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

	@Override
	@JsonIgnore
	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	@Override
	@JsonIgnore
	public String get__typename() {
		return this.__typename;
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

	@Override
	public String toString() {
		return "Journal {" //$NON-NLS-1$
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
			+ "title: " + this.title //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "abbreviation: " + this.abbreviation //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "url: " + this.url //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "issn: " + this.issn //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "publisher: " + this.publisher //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "notes: " + this.notes //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "__typename: " + this.__typename //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasValues == null) ? 0 : aliasValues.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((createdByUser == null) ? 0 : createdByUser.hashCode());
		result = prime * result + ((updated == null) ? 0 : updated.hashCode());
		result = prime * result + ((updatedByUser == null) ? 0 : updatedByUser.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((issn == null) ? 0 : issn.hashCode());
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((__typename == null) ? 0 : __typename.hashCode());
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
		Journal other = (Journal)obj;
		if (aliasValues == null) {
			if (other.aliasValues != null)
				return false;
		} else if (!aliasValues.equals(other.aliasValues))
			return false;
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
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(other.abbreviation))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (issn == null) {
			if (other.issn != null)
				return false;
		} else if (!issn.equals(other.issn))
			return false;
		if (publisher == null) {
			if (other.publisher != null)
				return false;
		} else if (!publisher.equals(other.publisher))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (__typename == null) {
			if (other.__typename != null)
				return false;
		} else if (!__typename.equals(other.__typename))
			return false;
		return true;
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
		private String title;
		private String abbreviation;
		private URL url;
		private String issn;
		private Publisher publisher;
		private String notes;

		/**
		 * The unique abreviation identifier.
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

		/**
		 * Web link to the journal's home page.
		 */
		public Builder withUrl(URL urlParam) {
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
		 * The journal publisher.
		 */
		public Builder withPublisher(Publisher publisherParam) {
			this.publisher = publisherParam;
			return this;
		}

		/**
		 * Notes about the journal.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		public Journal build() {
			Journal _object = new Journal();
			_object.setId(this.id);
			_object.setStatus(this.status);
			_object.setCreated(this.created);
			_object.setCreatedByUser(this.createdByUser);
			_object.setUpdated(this.updated);
			_object.setUpdatedByUser(this.updatedByUser);
			_object.setLog(this.log);
			_object.setTitle(this.title);
			_object.setAbbreviation(this.abbreviation);
			_object.setUrl(this.url);
			_object.setIssn(this.issn);
			_object.setPublisher(this.publisher);
			_object.setNotes(this.notes);
			_object.set__typename("Journal"); //$NON-NLS-1$
			return _object;
		}

	}

}
