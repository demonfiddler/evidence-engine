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

import java.net.URL;
import java.time.LocalDate;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

/**
 * A publication associated with given topics.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("PUB")
@GraphQLObjectType("Publication")
public class Publication extends AbstractLinkableEntity {

	/**
	 * The publication title.
	 */
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The names of the authors, one per line.
	 */
	@GraphQLScalar(fieldName = "authors", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String authors;

	/**
	 * The journal in which the publication appeared.
	 */
	@GraphQLNonScalar(fieldName = "journal", graphQLTypeSimpleName = "Journal", javaClass = Journal.class,
		listDepth = 0)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "journal_id", nullable = true)
	Journal journal;

	/**
	 * The publication kind.
	 */
	@GraphQLScalar(fieldName = "kind", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String kind;

	/**
	 * The publication date.
	 */
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate date;

	/**
	 * The publication year.
	 */
	@GraphQLScalar(fieldName = "year", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer year;

	/**
	 * The publication abstract.
	 */
	@Column(name = "abstract")
	@GraphQLScalar(fieldName = "abstract", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String _abstract;

	/**
	 * User notes about the publication.
	 */
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * Whether the publication has been peer reviewed.
	 */
	@GraphQLScalar(fieldName = "peerReviewed", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean peerReviewed;

	/**
	 * The digital object identifier.
	 */
	@GraphQLScalar(fieldName = "doi", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String doi;

	/**
	 * The International Standard Book Number.
	 */
	@GraphQLScalar(fieldName = "isbn", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String isbn;

	/**
	 * The URL for the publication online.
	 */
	@GraphQLScalar(fieldName = "url", graphQLTypeSimpleName = "URL", javaClass = URL.class, listDepth = 0)
	URL url;

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	@GraphQLScalar(fieldName = "cached", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean cached;

	/**
	 * The date the publication was accessed when compiling the database.
	 */
	@GraphQLScalar(fieldName = "accessed", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate accessed;

	@Override
	public String getEntityKind() {
		return EntityKind.PUB.name();
	}

	/**
	 * The publication title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The publication title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * The names of the authors, one per line.
	 */
	public void setAuthors(String authors) {
		this.authors = authors;
	}

	/**
	 * The names of the authors, one per line.
	 */
	public String getAuthors() {
		return this.authors;
	}

	/**
	 * The journal in which the publication appeared.
	 */
	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	/**
	 * The journal in which the publication appeared.
	 */
	public Journal getJournal() {
		return this.journal;
	}

	/**
	 * The publication kind.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * The publication kind.
	 */
	public String getKind() {
		return this.kind;
	}

	/**
	 * The publication date.
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * The publication date.
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * The publication year.
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * The publication year.
	 */
	public Integer getYear() {
		return this.year;
	}

	/**
	 * The publication abstract.
	 */
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}

	/**
	 * The publication abstract.
	 */
	public String getAbstract() {
		return this._abstract;
	}

	/**
	 * User notes about the publication.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * User notes about the publication.
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Whether the publication has been peer reviewed.
	 */
	public void setPeerReviewed(Boolean peerReviewed) {
		this.peerReviewed = peerReviewed;
	}

	/**
	 * Whether the publication has been peer reviewed.
	 */
	public Boolean getPeerReviewed() {
		return this.peerReviewed;
	}

	/**
	 * The digital object identifier.
	 */
	public void setDoi(String doi) {
		this.doi = doi;
	}

	/**
	 * The digital object identifier.
	 */
	public String getDoi() {
		return this.doi;
	}

	/**
	 * The International Standard Book Number.
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * The International Standard Book Number.
	 */
	public String getIsbn() {
		return this.isbn;
	}

	/**
	 * The URL for the publication online.
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * The URL for the publication online.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	public void setCached(Boolean cached) {
		this.cached = cached;
	}

	/**
	 * Flag to indicate that url content is cached on this application server.
	 */
	public Boolean getCached() {
		return this.cached;
	}

	/**
	 * The date the publication was accessed when compiling the database.
	 */
	public void setAccessed(LocalDate accessed) {
		this.accessed = accessed;
	}

	/**
	 * The date the publication was accessed when compiling the database.
	 */
	public LocalDate getAccessed() {
		return this.accessed;
	}

	public String toString() {
		return "Publication {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "entityKind: " + this.getEntityKind() //$NON-NLS-1$
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
			+ "fromEntityLinks: " + this.fromEntityLinks //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "toEntityLinks: " + this.toEntityLinks //$NON-NLS-1$
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
			+ "}"; //$NON-NLS-1$
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder extends AbstractLinkableEntity.Builder<Builder, Publication> {

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

		@Override
		public Publication build() {
			Publication _object = build(new Publication());
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
			return _object;
		}

	}

}
