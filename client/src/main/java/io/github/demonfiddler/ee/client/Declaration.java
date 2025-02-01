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

package io.github.demonfiddler.ee.client;

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
 * A public declaration or open letter made in respect of given topic(s).
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("Declaration")
@JsonInclude(Include.NON_NULL)
public class Declaration implements IBaseEntity, ITrackedEntity, ITopicalEntity {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public Declaration() {
	}

	/**
	 * The unique declaration identifier.
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
	@GraphQLScalar(fieldName = "created", graphQLTypeSimpleName = "DateTime",
		javaClass = java.time.OffsetDateTime.class, listDepth = 0)
	java.time.OffsetDateTime created;

	/**
	 * The user who created the record.
	 */
	@JsonProperty("createdByUser")
	@GraphQLNonScalar(fieldName = "createdByUser", graphQLTypeSimpleName = "User",
		javaClass = User.class, listDepth = 0)
	User createdByUser;

	/**
	 * When the record was last updated.
	 */
	@JsonProperty("updated")
	@JsonDeserialize(using = CustomJacksonDeserializers.DateTime.class)
	@GraphQLScalar(fieldName = "updated", graphQLTypeSimpleName = "DateTime",
		javaClass = java.time.OffsetDateTime.class, listDepth = 0)
	java.time.OffsetDateTime updated;

	/**
	 * The user who last updated the record.
	 */
	@JsonProperty("updatedByUser")
	@GraphQLNonScalar(fieldName = "updatedByUser", graphQLTypeSimpleName = "User",
		javaClass = User.class, listDepth = 0)
	User updatedByUser;

	/**
	 * Log of transactions involving the record.
	 */
	@JsonProperty("log")
	@GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "LogQueryFilter", "PageableInput" },
		mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
	@GraphQLNonScalar(fieldName = "log", graphQLTypeSimpleName = "LogPage",
		javaClass = LogPage.class, listDepth = 0)
	LogPage log;

	/**
	 * The topic(s) associated with the declaration.
	 */
	@JsonProperty("topicRefs")
	@GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "TopicRefQueryFilter", "PageableInput" },
		mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
	@GraphQLNonScalar(fieldName = "topicRefs", graphQLTypeSimpleName = "TopicRefPage",
		javaClass = TopicRefPage.class, listDepth = 0)
	TopicRefPage topicRefs;

	/**
	 * The kind of declaration.
	 */
	@JsonProperty("kind")
	@GraphQLInputParameters(names = { "format" }, types = { "FormatKind" }, mandatories = { false }, listDepths = { 0 },
		itemsMandatory = { false })
	@GraphQLScalar(fieldName = "kind", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String kind;

	/**
	 * The declaration name or title.
	 */
	@JsonProperty("title")
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The date the declaration was published.
	 */
	@JsonProperty("date")
	@JsonDeserialize(using = CustomJacksonDeserializers.Date.class)
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = java.time.LocalDate.class,
		listDepth = 0)
	java.time.LocalDate date;

	/**
	 * The country to which the declaration relates.
	 */
	@JsonProperty("country")
	@GraphQLInputParameters(names = { "format" }, types = { "CountryFormatKind" }, mandatories = { false },
		listDepths = { 0 }, itemsMandatory = { false })
	@GraphQLScalar(fieldName = "country", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String country;

	/**
	 * The URL for the declaration online.
	 */
	@JsonProperty("url")
	@JsonDeserialize(using = CustomJacksonDeserializers.URL.class)
	@GraphQLScalar(fieldName = "url", graphQLTypeSimpleName = "URL", javaClass = java.net.URL.class, listDepth = 0)
	java.net.URL url;

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	@JsonProperty("cached")
	@GraphQLScalar(fieldName = "cached", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean cached;

	/**
	 * Names of persons who signed the declaration, one per line.
	 */
	@JsonProperty("signatories")
	@GraphQLScalar(fieldName = "signatories", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String signatories;

	/**
	 * The number of signatories.
	 */
	@JsonProperty("signatoryCount")
	@GraphQLScalar(fieldName = "signatoryCount", graphQLTypeSimpleName = "Int", javaClass = Integer.class,
		listDepth = 0)
	Integer signatoryCount;

	/**
	 * Added notes about the declaration.
	 */
	@JsonProperty("notes")
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	/**
	 * The unique declaration identifier.
	 */
	@Override
	@JsonIgnore
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique declaration identifier.
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
	public void setCreated(java.time.OffsetDateTime created) {
		this.created = created;
	}

	/**
	 * When the record was created.
	 */
	@Override
	@JsonIgnore
	public java.time.OffsetDateTime getCreated() {
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
	public void setUpdated(java.time.OffsetDateTime updated) {
		this.updated = updated;
	}

	/**
	 * When the record was last updated.
	 */
	@Override
	@JsonIgnore
	public java.time.OffsetDateTime getUpdated() {
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
	 * The topic(s) associated with the declaration.
	 */
	@Override
	@JsonIgnore
	public void setTopicRefs(TopicRefPage topicRefs) {
		this.topicRefs = topicRefs;
	}

	/**
	 * The topic(s) associated with the declaration.
	 */
	@Override
	@JsonIgnore
	public TopicRefPage getTopicRefs() {
		return this.topicRefs;
	}

	/**
	 * The kind of declaration.
	 */
	@JsonProperty("kind")
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * The kind of declaration.
	 */
	@JsonProperty("kind")
	public String getKind() {
		return this.kind;
	}

	/**
	 * The declaration name or title.
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The declaration name or title.
	 */
	@JsonProperty("title")
	public String getTitle() {
		return this.title;
	}

	/**
	 * The date the declaration was published.
	 */
	@JsonProperty("date")
	public void setDate(java.time.LocalDate date) {
		this.date = date;
	}

	/**
	 * The date the declaration was published.
	 */
	@JsonProperty("date")
	public java.time.LocalDate getDate() {
		return this.date;
	}

	/**
	 * The country to which the declaration relates.
	 */
	@JsonProperty("country")
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * The country to which the declaration relates.
	 */
	@JsonProperty("country")
	public String getCountry() {
		return this.country;
	}

	/**
	 * The URL for the declaration online.
	 */
	@JsonProperty("url")
	public void setUrl(java.net.URL url) {
		this.url = url;
	}

	/**
	 * The URL for the declaration online.
	 */
	@JsonProperty("url")
	public java.net.URL getUrl() {
		return this.url;
	}

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	@JsonProperty("cached")
	public void setCached(Boolean cached) {
		this.cached = cached;
	}

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	@JsonProperty("cached")
	public Boolean getCached() {
		return this.cached;
	}

	/**
	 * Names of persons who signed the declaration, one per line.
	 */
	@JsonProperty("signatories")
	public void setSignatories(String signatories) {
		this.signatories = signatories;
	}

	/**
	 * Names of persons who signed the declaration, one per line.
	 */
	@JsonProperty("signatories")
	public String getSignatories() {
		return this.signatories;
	}

	/**
	 * The number of signatories.
	 */
	@JsonProperty("signatoryCount")
	public void setSignatoryCount(Integer signatoryCount) {
		this.signatoryCount = signatoryCount;
	}

	/**
	 * The number of signatories.
	 */
	@JsonProperty("signatoryCount")
	public Integer getSignatoryCount() {
		return this.signatoryCount;
	}

	/**
	 * Added notes about the declaration.
	 */
	@JsonProperty("notes")
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Added notes about the declaration.
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
		return "Declaration {" //$NON-NLS-1$
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
			+ "topicRefs: " + this.topicRefs //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "kind: " + this.kind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "title: " + this.title //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "date: " + this.date //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "country: " + this.country //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "url: " + this.url //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "cached: " + this.cached //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "signatories: " + this.signatories //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "signatoryCount: " + this.signatoryCount //$NON-NLS-1$
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
		result = prime * result + ((topicRefs == null) ? 0 : topicRefs.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((cached == null) ? 0 : cached.hashCode());
		result = prime * result + ((signatories == null) ? 0 : signatories.hashCode());
		result = prime * result + ((signatoryCount == null) ? 0 : signatoryCount.hashCode());
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
		Declaration other = (Declaration)obj;
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
		if (topicRefs == null) {
			if (other.topicRefs != null)
				return false;
		} else if (!topicRefs.equals(other.topicRefs))
			return false;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (cached == null) {
			if (other.cached != null)
				return false;
		} else if (!cached.equals(other.cached))
			return false;
		if (signatories == null) {
			if (other.signatories != null)
				return false;
		} else if (!signatories.equals(other.signatories))
			return false;
		if (signatoryCount == null) {
			if (other.signatoryCount != null)
				return false;
		} else if (!signatoryCount.equals(other.signatoryCount))
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
		private java.time.OffsetDateTime created;
		private User createdByUser;
		private java.time.OffsetDateTime updated;
		private User updatedByUser;
		private LogPage log;
		private TopicRefPage topicRefs;
		private String kind;
		private String title;
		private java.time.LocalDate date;
		private String country;
		private java.net.URL url;
		private Boolean cached;
		private String signatories;
		private Integer signatoryCount;
		private String notes;

		/**
		 * The unique declaration identifier.
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
		public Builder withCreated(java.time.OffsetDateTime createdParam) {
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
		public Builder withUpdated(java.time.OffsetDateTime updatedParam) {
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
		 * The topic(s) associated with the declaration.
		 */
		public Builder withTopicRefs(TopicRefPage topicRefsParam) {
			this.topicRefs = topicRefsParam;
			return this;
		}

		/**
		 * The kind of declaration.
		 */
		public Builder withKind(String kindParam) {
			this.kind = kindParam;
			return this;
		}

		/**
		 * The declaration name or title.
		 */
		public Builder withTitle(String titleParam) {
			this.title = titleParam;
			return this;
		}

		/**
		 * The date the declaration was published.
		 */
		public Builder withDate(java.time.LocalDate dateParam) {
			this.date = dateParam;
			return this;
		}

		/**
		 * The country to which the declaration relates.
		 */
		public Builder withCountry(String countryParam) {
			this.country = countryParam;
			return this;
		}

		/**
		 * The URL for the declaration online.
		 */
		public Builder withUrl(java.net.URL urlParam) {
			this.url = urlParam;
			return this;
		}

		/**
		 * Flag to indicate that url content is cached on this application server.
		 */
		public Builder withCached(Boolean cachedParam) {
			this.cached = cachedParam;
			return this;
		}

		/**
		 * Names of persons who signed the declaration, one per line.
		 */
		public Builder withSignatories(String signatoriesParam) {
			this.signatories = signatoriesParam;
			return this;
		}

		/**
		 * The number of signatories.
		 */
		public Builder withSignatoryCount(Integer signatoryCountParam) {
			this.signatoryCount = signatoryCountParam;
			return this;
		}

		/**
		 * Added notes about the declaration.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		public Declaration build() {
			Declaration _object = new Declaration();
			_object.setId(this.id);
			_object.setStatus(this.status);
			_object.setCreated(this.created);
			_object.setCreatedByUser(this.createdByUser);
			_object.setUpdated(this.updated);
			_object.setUpdatedByUser(this.updatedByUser);
			_object.setLog(this.log);
			_object.setTopicRefs(this.topicRefs);
			_object.setKind(this.kind);
			_object.setTitle(this.title);
			_object.setDate(this.date);
			_object.setCountry(this.country);
			_object.setUrl(this.url);
			_object.setCached(this.cached);
			_object.setSignatories(this.signatories);
			_object.setSignatoryCount(this.signatoryCount);
			_object.setNotes(this.notes);
			_object.set__typename("Declaration"); //$NON-NLS-1$
			return _object;
		}

	}

}
