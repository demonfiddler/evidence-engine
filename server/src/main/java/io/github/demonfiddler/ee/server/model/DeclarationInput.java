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

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * An input for creating or updating a declaration.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("DeclarationInput")
public class DeclarationInput extends AbstractBaseEntityInput {

	/**
	 * The kind of declaration.
	 */
	@GraphQLScalar(fieldName = "kind", graphQLTypeSimpleName = "DeclarationKind", javaClass = DeclarationKind.class,
		listDepth = 0)
	DeclarationKind kind;

	/**
	 * The declaration title.
	 */
	@GraphQLScalar(fieldName = "title", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String title;

	/**
	 * The date the declaration was published.
	 */
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate date;

	/**
	 * The ISO-3166-1 alpha-2 code for the country to which the declaration relates.
	 */
	@GraphQLScalar(fieldName = "country", graphQLTypeSimpleName = "Country", javaClass = String.class, listDepth = 0)
	String country;

	/**
	 * The URL for the declaration online.
	 */
	@GraphQLScalar(fieldName = "url", graphQLTypeSimpleName = "URL", javaClass = URL.class, listDepth = 0)
	URL url;

	/**
	 * Names of persons who signed the declaration, one per line.
	 */
	@GraphQLScalar(fieldName = "signatories", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String signatories;

	/**
	 * Added notes about the declaration.
	 */
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * The kind of declaration.
	 */
	public void setKind(DeclarationKind kind) {
		this.kind = kind;
	}

	/**
	 * The kind of declaration.
	 */
	public DeclarationKind getKind() {
		return this.kind;
	}

	/**
	 * The declaration title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The declaration title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * The date the declaration was published.
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * The date the declaration was published.
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * The ISO-3166-1 alpha-2 code for the country to which the declaration relates.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * The ISO-3166-1 alpha-2 code for the country to which the declaration relates.
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * The URL for the declaration online.
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * The URL for the declaration online.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * Names of persons who signed the declaration, one per line.
	 */
	public void setSignatories(String signatories) {
		this.signatories = signatories;
	}

	/**
	 * Names of persons who signed the declaration, one per line.
	 */
	public String getSignatories() {
		return this.signatories;
	}

	/**
	 * Added notes about the declaration.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Added notes about the declaration.
	 */
	public String getNotes() {
		return this.notes;
	}

	public String toString() {
		return "DeclarationInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
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
			+ "signatories: " + this.signatories //$NON-NLS-1$
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
	public static class Builder extends AbstractBaseEntityInput.Builder<Builder, DeclarationInput> {

		private DeclarationKind kind;
		private String title;
		private LocalDate date;
		private String country;
		private URL url;
		private String signatories;
		private String notes;

		/**
		 * The kind of declaration.
		 */
		public Builder withKind(DeclarationKind kindParam) {
			this.kind = kindParam;
			return this;
		}

		/**
		 * The declaration title.
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
		 * The ISO-3166-1 alpha-2 code for the country to which the declaration relates.
		 */
		public Builder withCountry(String countryParam) {
			this.country = countryParam;
			return this;
		}

		/**
		 * The URL for the declaration online.
		 */
		public Builder withUrl(URL urlParam) {
			this.url = urlParam;
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
		 * Added notes about the declaration.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		@Override
		public DeclarationInput build() {
			DeclarationInput _object = build(new DeclarationInput());
			_object.setKind(this.kind);
			_object.setTitle(this.title);
			_object.setDate(this.date);
			_object.setCountry(this.country);
			_object.setUrl(this.url);
			_object.setSignatories(this.signatories);
			_object.setNotes(this.notes);
			return _object;
		}

	}

}
