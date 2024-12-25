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

import java.util.List;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

/**
 * Returns paginated publication query results.
 * @author generated by graphql-java-generator
 * @see <a href=
 * "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Entity
@GraphQLObjectType("PublicationPage")
public class PublicationPage implements IBaseEntityPage<Publication> {

	/**
	 * Dummy ID required for @Entity classes
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GraphQLScalar(fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long dummy;

	/**
	 * The requested pageful of records.
	 */
	@Transient
	@GraphQLNonScalar(fieldName = "content", graphQLTypeSimpleName = "Publication", javaClass = Publication.class,
		listDepth = 1)
	List<Publication> content;

	/**
	 * Whether the page has content.
	 */
	@GraphQLScalar(fieldName = "hasContent", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean hasContent;

	/**
	 * Whether the page is empty (no content).
	 */
	@GraphQLScalar(fieldName = "isEmpty", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isEmpty;

	/**
	 * The current page number.
	 */
	@GraphQLScalar(fieldName = "number", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer number;

	/**
	 * The page size.
	 */
	@GraphQLScalar(fieldName = "size", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer size;

	/**
	 * The number of elements on this page.
	 */
	@GraphQLScalar(fieldName = "numberOfElements", graphQLTypeSimpleName = "Int", javaClass = Integer.class,
		listDepth = 0)
	Integer numberOfElements;

	/**
	 * The total number of pages available.
	 */
	@GraphQLScalar(fieldName = "totalPages", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer totalPages;

	/**
	 * The total number of records.
	 */
	@GraphQLScalar(fieldName = "totalElements", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long totalElements;

	/**
	 * Whether this is the first page.
	 */
	@GraphQLScalar(fieldName = "isFirst", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isFirst;

	/**
	 * Whether this is the last page.
	 */
	@GraphQLScalar(fieldName = "isLast", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isLast;

	/**
	 * Whether there is next page.
	 */
	@GraphQLScalar(fieldName = "hasNext", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean hasNext;

	/**
	 * Whether there is previous page.
	 */
	@GraphQLScalar(fieldName = "hasPrevious", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean hasPrevious;

	/**
	 * Dummy ID required for @Entity classes
	 */
	public void setDummy(Long dummy) {
		this.dummy = dummy;
	}

	/**
	 * Dummy ID required for @Entity classes
	 */
	public Long getDummy() {
		return this.dummy;
	}

	/**
	 * The requested pageful of records.
	 */
	public void setContent(List<Publication> content) {
		this.content = content;
	}

	/**
	 * The requested pageful of records.
	 */
	public List<Publication> getContent() {
		return this.content;
	}

	/**
	 * Whether the page has content.
	 */
	public void setHasContent(Boolean hasContent) {
		this.hasContent = hasContent;
	}

	/**
	 * Whether the page has content.
	 */
	public Boolean getHasContent() {
		return this.hasContent;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	public void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	public Boolean getIsEmpty() {
		return this.isEmpty;
	}

	/**
	 * The current page number.
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * The current page number.
	 */
	public Integer getNumber() {
		return this.number;
	}

	/**
	 * The page size.
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * The page size.
	 */
	public Integer getSize() {
		return this.size;
	}

	/**
	 * The number of elements on this page.
	 */
	public void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * The number of elements on this page.
	 */
	public Integer getNumberOfElements() {
		return this.numberOfElements;
	}

	/**
	 * The total number of pages available.
	 */
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * The total number of pages available.
	 */
	public Integer getTotalPages() {
		return this.totalPages;
	}

	/**
	 * The total number of records.
	 */
	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * The total number of records.
	 */
	public Long getTotalElements() {
		return this.totalElements;
	}

	/**
	 * Whether this is the first page.
	 */
	public void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}

	/**
	 * Whether this is the first page.
	 */
	public Boolean getIsFirst() {
		return this.isFirst;
	}

	/**
	 * Whether this is the last page.
	 */
	public void setIsLast(Boolean isLast) {
		this.isLast = isLast;
	}

	/**
	 * Whether this is the last page.
	 */
	public Boolean getIsLast() {
		return this.isLast;
	}

	/**
	 * Whether there is next page.
	 */
	public void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	/**
	 * Whether there is next page.
	 */
	public Boolean getHasNext() {
		return this.hasNext;
	}

	/**
	 * Whether there is previous page.
	 */
	public void setHasPrevious(Boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	/**
	 * Whether there is previous page.
	 */
	public Boolean getHasPrevious() {
		return this.hasPrevious;
	}

	public String toString() {
		return "PublicationPage {" //$NON-NLS-1$
			+ "dummy: " + this.dummy //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "content: " + this.content //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "hasContent: " + this.hasContent //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isEmpty: " + this.isEmpty //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "number: " + this.number //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "size: " + this.size //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "numberOfElements: " + this.numberOfElements //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "totalPages: " + this.totalPages //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "totalElements: " + this.totalElements //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isFirst: " + this.isFirst //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isLast: " + this.isLast //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "hasNext: " + this.hasNext //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "hasPrevious: " + this.hasPrevious //$NON-NLS-1$
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
		private Long dummy;
		private List<Publication> content;
		private Boolean hasContent;
		private Boolean isEmpty;
		private Integer number;
		private Integer size;
		private Integer numberOfElements;
		private Integer totalPages;
		private Long totalElements;
		private Boolean isFirst;
		private Boolean isLast;
		private Boolean hasNext;
		private Boolean hasPrevious;

		/**
		 * Dummy ID required for @Entity classes
		 */
		public Builder withDummy(Long dummyParam) {
			this.dummy = dummyParam;
			return this;
		}

		/**
		 * The requested pageful of records.
		 */
		public Builder withContent(List<Publication> contentParam) {
			this.content = contentParam;
			return this;
		}

		/**
		 * Whether the page has content.
		 */
		public Builder withHasContent(Boolean hasContentParam) {
			this.hasContent = hasContentParam;
			return this;
		}

		/**
		 * Whether the page is empty (no content).
		 */
		public Builder withIsEmpty(Boolean isEmptyParam) {
			this.isEmpty = isEmptyParam;
			return this;
		}

		/**
		 * The current page number.
		 */
		public Builder withNumber(Integer numberParam) {
			this.number = numberParam;
			return this;
		}

		/**
		 * The page size.
		 */
		public Builder withSize(Integer sizeParam) {
			this.size = sizeParam;
			return this;
		}

		/**
		 * The number of elements on this page.
		 */
		public Builder withNumberOfElements(Integer numberOfElementsParam) {
			this.numberOfElements = numberOfElementsParam;
			return this;
		}

		/**
		 * The total number of pages available.
		 */
		public Builder withTotalPages(Integer totalPagesParam) {
			this.totalPages = totalPagesParam;
			return this;
		}

		/**
		 * The total number of records.
		 */
		public Builder withTotalElements(Long totalElementsParam) {
			this.totalElements = totalElementsParam;
			return this;
		}

		/**
		 * Whether this is the first page.
		 */
		public Builder withIsFirst(Boolean isFirstParam) {
			this.isFirst = isFirstParam;
			return this;
		}

		/**
		 * Whether this is the last page.
		 */
		public Builder withIsLast(Boolean isLastParam) {
			this.isLast = isLastParam;
			return this;
		}

		/**
		 * Whether there is next page.
		 */
		public Builder withHasNext(Boolean hasNextParam) {
			this.hasNext = hasNextParam;
			return this;
		}

		/**
		 * Whether there is previous page.
		 */
		public Builder withHasPrevious(Boolean hasPreviousParam) {
			this.hasPrevious = hasPreviousParam;
			return this;
		}

		public PublicationPage build() {
			PublicationPage _object = new PublicationPage();
			_object.setDummy(this.dummy);
			_object.setContent(this.content);
			_object.setHasContent(this.hasContent);
			_object.setIsEmpty(this.isEmpty);
			_object.setNumber(this.number);
			_object.setSize(this.size);
			_object.setNumberOfElements(this.numberOfElements);
			_object.setTotalPages(this.totalPages);
			_object.setTotalElements(this.totalElements);
			_object.setIsFirst(this.isFirst);
			_object.setIsLast(this.isLast);
			_object.setHasNext(this.hasNext);
			_object.setHasPrevious(this.hasPrevious);
			return _object;
		}
	}

}
