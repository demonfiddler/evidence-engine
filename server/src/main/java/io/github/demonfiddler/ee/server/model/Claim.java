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

import java.time.LocalDate;

import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

/**
 * A claim made in respect of given topic(s).
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("CLA")
@GraphQLObjectType("Claim")
public class Claim extends AbstractLinkableEntity {

	/**
	 * The date the claim was made.
	 */
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = LocalDate.class, listDepth = 0)
	LocalDate date;

	/**
	 * The text of the claim.
	 */
	@GraphQLScalar(fieldName = "text", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String text;

	/**
	 * Added notes about the claim.
	 */
	@GraphQLScalar(fieldName = "notes", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth = 0)
	String notes;

	@Override
	public String getEntityKind() {
		return EntityKind.CLA.name();
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
		return "Claim {" //$NON-NLS-1$
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
			+ "date: " + this.date //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "text: " + this.text //$NON-NLS-1$
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
	public static class Builder extends AbstractLinkableEntity.Builder<Builder, Claim> {

		private String text;
		private LocalDate date;
		private String notes;

		/**
		 * The date the claim was made.
		 */
		public Builder withDate(LocalDate dateParam) {
			this.date = dateParam;
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
		 * Added notes about the claim.
		 */
		public Builder withNotes(String notesParam) {
			this.notes = notesParam;
			return this;
		}

		@Override
		public Claim build() {
			Claim _object = build(new Claim());
			_object.setDate(this.date);
			_object.setText(this.text);
			_object.setNotes(this.notes);
			return _object;
		}

	}

}
