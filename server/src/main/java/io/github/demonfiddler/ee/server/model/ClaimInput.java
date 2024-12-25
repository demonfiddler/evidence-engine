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

import java.time.LocalDate;

import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * An input for creating or updating a claim.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLInputType("ClaimInput")
public class ClaimInput {

	/**
	 * The claim identifier, required if updating an existing record.
	 */
	@GraphQLScalar(fieldName = "id", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long id;

	/**
	 * The text of the claim.
	 */
	@GraphQLScalar(fieldName = "text", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String text;

	/**
	 * The date the claim was made.
	 */
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate date;

	/**
	 * Added notes about the claim.
	 */
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	/**
	 * The claim identifier, required if updating an existing record.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The claim identifier, required if updating an existing record.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * The text of the claim.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The text of the claim.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * The date the claim was made.
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * The date the claim was made.
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * Added notes about the claim.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Added notes about the claim.
	 */
	public String getNotes() {
		return this.notes;
	}

	public String toString() {
		return "ClaimInput {" //$NON-NLS-1$
			+ "id: " + this.id //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "text: " + this.text //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "date: " + this.date //$NON-NLS-1$
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
		private String text;
		private LocalDate date;
		private String notes;

		/**
		 * The claim identifier, required if updating an existing record.
		 */
		public Builder withId(Long idParam) {
			this.id = idParam;
			return this;
		}

		/**
		 * The text of the claim.
		 */
		public Builder withText(String textParam) {
			this.text = textParam;
			return this;
		}

		/**
		 * The date the claim was made.
		 */
		public Builder withDate(LocalDate dateParam) {
			this.date = dateParam;
			return this;
		}

		/**
		 * Added notes about the claim.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		public ClaimInput build() {
			ClaimInput _object = new ClaimInput();
			_object.setId(this.id);
			_object.setText(this.text);
			_object.setDate(this.date);
			_object.setNotes(this.notes);
			return _object;
		}
	}
}
