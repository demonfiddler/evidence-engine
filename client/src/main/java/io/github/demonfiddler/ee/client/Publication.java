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
import java.time.LocalDate;
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
 * A publication associated with given topics.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("Publication")
@JsonInclude(Include.NON_NULL)
public class Publication implements IBaseEntity, ITrackedEntity, ITopicalEntity {

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public Publication() {
	}

	/**
	 * The unique publication identifier.
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
	 * The topic(s) associated with the publication.
	 */
	@JsonProperty("topicRefs")
	@GraphQLInputParameters(names = { "filter", "pageSort" }, types = { "TopicRefQueryFilter", "PageableInput" },
		mandatories = { false, false }, listDepths = { 0, 0 }, itemsMandatory = { false, false })
	@GraphQLNonScalar(fieldName = "topicRefs", graphQLTypeSimpleName = "TopicRefPage", javaClass = TopicRefPage.class,
		listDepth = 0)
	TopicRefPage topicRefs;

	/**
	 * The publication title.
	 */
	@JsonProperty("title")
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The names of the authors, one per line.
	 */
	@JsonProperty("authors")
	@GraphQLScalar(fieldName = "authors", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String authors;

	/**
	 * The name of the journal in which the publication appeared.
	 */
	@JsonProperty("journal")
	@GraphQLNonScalar(fieldName = "journal", graphQLTypeSimpleName = "Journal", javaClass = Journal.class,
		listDepth = 0)
	Journal journal;

	/**
	 * The publication kind.
	 */
	@JsonProperty("kind")
	@GraphQLInputParameters(names = { "format" }, types = { "FormatKind" }, mandatories = { false }, listDepths = { 0 },
		itemsMandatory = { false })
	@GraphQLScalar(fieldName = "kind", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String kind;

	/**
	 * The publication date.
	 */
	@JsonProperty("date")
	@JsonDeserialize(using = CustomJacksonDeserializers.Date.class)
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate date;

	/**
	 * The publication year.
	 */
	@JsonProperty("year")
	@GraphQLScalar(fieldName = "year", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer year;

	/**
	 * The publication abstract.
	 */
	@JsonProperty("abstract")
	@GraphQLScalar(fieldName = "abstract", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String _abstract;

	/**
	 * User notes about the publication.
	 */
	@JsonProperty("notes")
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * Whether the publication has been peer reviewed.
	 */
	@JsonProperty("peerReviewed")
	@GraphQLScalar(fieldName = "peerReviewed", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean peerReviewed;

	/**
	 * The digital object identifier.
	 */
	@JsonProperty("doi")
	@GraphQLScalar(fieldName = "doi", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String doi;

	/**
	 * The International Standard Book Number.
	 */
	@JsonProperty("isbn")
	@GraphQLScalar(fieldName = "isbn", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String isbn;

	/**
	 * The URL for the publication online.
	 */
	@JsonProperty("url")
	@JsonDeserialize(using = CustomJacksonDeserializers.URL.class)
	@GraphQLScalar(fieldName = "url", graphQLTypeSimpleName = "URL", javaClass = URL.class, listDepth = 0)
	URL url;

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	@JsonProperty("cached")
	@GraphQLScalar(fieldName = "cached", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean cached;

	/**
	 * The date the publication was accessed when compiling the database.
	 */
	@JsonProperty("accessed")
	@JsonDeserialize(using = CustomJacksonDeserializers.Date.class)
	@GraphQLScalar(fieldName = "accessed", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate accessed;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String __typename;

	/**
	 * The unique publication identifier.
	 */
	@Override
	@JsonIgnore
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The unique publication identifier.
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
	 * The topic(s) associated with the publication.
	 */
	@Override
	@JsonIgnore
	public void setTopicRefs(TopicRefPage topicRefs) {
		this.topicRefs = topicRefs;
	}

	/**
	 * The topic(s) associated with the publication.
	 */
	@Override
	@JsonIgnore
	public TopicRefPage getTopicRefs() {
		return this.topicRefs;
	}

	/**
	 * The publication title.
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The publication title.
	 */
	@JsonProperty("title")
	public String getTitle() {
		return this.title;
	}

	/**
	 * The names of the authors, one per line.
	 */
	@JsonProperty("authors")
	public void setAuthors(String authors) {
		this.authors = authors;
	}

	/**
	 * The names of the authors, one per line.
	 */
	@JsonProperty("authors")
	public String getAuthors() {
		return this.authors;
	}

	/**
	 * The name of the journal in which the publication appeared.
	 */
	@JsonProperty("journal")
	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	/**
	 * The name of the journal in which the publication appeared.
	 */
	@JsonProperty("journal")
	public Journal getJournal() {
		return this.journal;
	}

	/**
	 * The publication kind.
	 */
	@JsonProperty("kind")
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * The publication kind.
	 */
	@JsonProperty("kind")
	public String getKind() {
		return this.kind;
	}

	/**
	 * The publication date.
	 */
	@JsonProperty("date")
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * The publication date.
	 */
	@JsonProperty("date")
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * The publication year.
	 */
	@JsonProperty("year")
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * The publication year.
	 */
	@JsonProperty("year")
	public Integer getYear() {
		return this.year;
	}

	/**
	 * The publication abstract.
	 */
	@JsonProperty("abstract")
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}

	/**
	 * The publication abstract.
	 */
	@JsonProperty("abstract")
	public String getAbstract() {
		return this._abstract;
	}

	/**
	 * User notes about the publication.
	 */
	@JsonProperty("notes")
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * User notes about the publication.
	 */
	@JsonProperty("notes")
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Whether the publication has been peer reviewed.
	 */
	@JsonProperty("peerReviewed")
	public void setPeerReviewed(Boolean peerReviewed) {
		this.peerReviewed = peerReviewed;
	}

	/**
	 * Whether the publication has been peer reviewed.
	 */
	@JsonProperty("peerReviewed")
	public Boolean getPeerReviewed() {
		return this.peerReviewed;
	}

	/**
	 * The digital object identifier.
	 */
	@JsonProperty("doi")
	public void setDoi(String doi) {
		this.doi = doi;
	}

	/**
	 * The digital object identifier.
	 */
	@JsonProperty("doi")
	public String getDoi() {
		return this.doi;
	}

	/**
	 * The International Standard Serial/Book Number.
	 */
	@JsonProperty("isbn")
	public void setIsbn(String issnIsbn) {
		this.isbn = issnIsbn;
	}

	/**
	 * The International Standard Serial/Book Number.
	 */
	@JsonProperty("isbn")
	public String getIsbn() {
		return this.isbn;
	}

	/**
	 * The URL for the publication online.
	 */
	@JsonProperty("url")
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * The URL for the publication online.
	 */
	@JsonProperty("url")
	public URL getUrl() {
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
	 * The date the publication was accessed when compiling the database.
	 */
	@JsonProperty("accessed")
	public void setAccessed(LocalDate accessed) {
		this.accessed = accessed;
	}

	/**
	 * The date the publication was accessed when compiling the database.
	 */
	@JsonProperty("accessed")
	public LocalDate getAccessed() {
		return this.accessed;
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

	public String toString() {
		return "Publication {" //$NON-NLS-1$
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
			+ "title: " + this.title //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "authors: " + this.authors //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "journal: " + this.journal //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "kind: " + this.kind //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "date: " + this.date //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "year: " + this.year //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "_abstract: " + this._abstract //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "notes: " + this.notes //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "peerReviewed: " + this.peerReviewed //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "doi: " + this.doi //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isbn: " + this.isbn //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "url: " + this.url //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "cached: " + this.cached //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "accessed: " + this.accessed //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "__typename: " + this.__typename //$NON-NLS-1$
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
		private TopicRefPage topicRefs;
		private String title;
		private String authors;
		private Journal journal;
		private String kind;
		private LocalDate date;
		private Integer year;
		private String _abstract;
		private String notes;
		private Boolean peerReviewed;
		private String doi;
		private String isbn;
		private URL url;
		private Boolean cached;
		private LocalDate accessed;

		/**
		 * The unique publication identifier.
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
		 * The topic(s) associated with the publication.
		 */
		public Builder withTopicRefs(TopicRefPage topicRefsParam) {
			this.topicRefs = topicRefsParam;
			return this;
		}

		/**
		 * The publication title.
		 */
		public Builder withTitle(String titleParam) {
			this.title = titleParam;
			return this;
		}

		/**
		 * The names of the authors, one per line.
		 */
		public Builder withAuthors(String authorsParam) {
			this.authors = authorsParam;
			return this;
		}

		/**
		 * The name of the journal in which the publication appeared.
		 */
		public Builder withJournal(Journal journalParam) {
			this.journal = journalParam;
			return this;
		}

		/**
		 * The publication kind.
		 */
		public Builder withKind(String kindParam) {
			this.kind = kindParam;
			return this;
		}

		/**
		 * The publication date.
		 */
		public Builder withDate(LocalDate dateParam) {
			this.date = dateParam;
			return this;
		}

		/**
		 * The publication year.
		 */
		public Builder withYear(Integer yearParam) {
			this.year = yearParam;
			return this;
		}

		/**
		 * The publication abstract.
		 */
		public Builder withAbstract(String _abstractParam) {
			this._abstract = _abstractParam;
			return this;
		}

		/**
		 * User notes about the publication.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		/**
		 * Whether the publication has been peer reviewed.
		 */
		public Builder withPeerReviewed(Boolean peerReviewedParam) {
			this.peerReviewed = peerReviewedParam;
			return this;
		}

		/**
		 * The digital object identifier.
		 */
		public Builder withDoi(String doiParam) {
			this.doi = doiParam;
			return this;
		}

		/**
		 * The International Standard Book Number.
		 */
		public Builder withIsbn(String isbnParam) {
			this.isbn = isbnParam;
			return this;
		}

		/**
		 * The URL for the publication online.
		 */
		public Builder withUrl(URL urlParam) {
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
		 * The date the publication was accessed when compiling the database.
		 */
		public Builder withAccessed(LocalDate accessedParam) {
			this.accessed = accessedParam;
			return this;
		}

		public Publication build() {
			Publication _object = new Publication();
			_object.setId(this.id);
			_object.setStatus(this.status);
			_object.setCreated(this.created);
			_object.setCreatedByUser(this.createdByUser);
			_object.setUpdated(this.updated);
			_object.setUpdatedByUser(this.updatedByUser);
			_object.setLog(this.log);
			_object.setTopicRefs(this.topicRefs);
			_object.setTitle(this.title);
			_object.setAuthors(this.authors);
			_object.setJournal(this.journal);
			_object.setKind(this.kind);
			_object.setDate(this.date);
			_object.setYear(this.year);
			_object.setAbstract(this._abstract);
			_object.setNotes(this.notes);
			_object.setPeerReviewed(this.peerReviewed);
			_object.setDoi(this.doi);
			_object.setIsbn(this.isbn);
			_object.setUrl(this.url);
			_object.setCached(this.cached);
			_object.setAccessed(this.accessed);
			_object.set__typename("Publication"); //$NON-NLS-1$
			return _object;
		}

	}

}
