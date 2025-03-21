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

import java.util.List;

import com.graphql_java_generator.annotation.GraphQLScalar;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Abstract base class for all {@code IPage} implementations.
 */
public abstract class AbstractPage<T extends IBaseEntity> implements IBaseEntityPage<T> {

	/**
	 * Dummy ID required for @Entity classes
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GraphQLScalar(fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long dummy;

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
	public final void setDummy(Long dummy) {
		this.dummy = dummy;
	}

	/**
	 * Dummy ID required for @Entity classes
	 */
	public final Long getDummy() {
		return this.dummy;
	}

	/**
	 * Whether the page has content.
	 */
	public final void setHasContent(Boolean hasContent) {
		this.hasContent = hasContent;
	}

	/**
	 * Whether the page has content.
	 */
	public final Boolean getHasContent() {
		return this.hasContent;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	public final void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	public final Boolean getIsEmpty() {
		return this.isEmpty;
	}

	/**
	 * The current page number.
	 */
	public final void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * The current page number.
	 */
	public final Integer getNumber() {
		return this.number;
	}

	/**
	 * The page size.
	 */
	public final void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * The page size.
	 */
	public final Integer getSize() {
		return this.size;
	}

	/**
	 * The number of elements on this page.
	 */
	public final void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * The number of elements on this page.
	 */
	public final Integer getNumberOfElements() {
		return this.numberOfElements;
	}

	/**
	 * The total number of pages available.
	 */
	public final void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * The total number of pages available.
	 */
	public final Integer getTotalPages() {
		return this.totalPages;
	}

	/**
	 * The total number of records.
	 */
	public final void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * The total number of records.
	 */
	public final Long getTotalElements() {
		return this.totalElements;
	}

	/**
	 * Whether this is the first page.
	 */
	public final void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}

	/**
	 * Whether this is the first page.
	 */
	public final Boolean getIsFirst() {
		return this.isFirst;
	}

	/**
	 * Whether this is the last page.
	 */
	public final void setIsLast(Boolean isLast) {
		this.isLast = isLast;
	}

	/**
	 * Whether this is the last page.
	 */
	public final Boolean getIsLast() {
		return this.isLast;
	}

	/**
	 * Whether there is next page.
	 */
	public final void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	/**
	 * Whether there is next page.
	 */
	public final Boolean getHasNext() {
		return this.hasNext;
	}

	/**
	 * Whether there is previous page.
	 */
	public final void setHasPrevious(Boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	/**
	 * Whether there is previous page.
	 */
	public final Boolean getHasPrevious() {
		return this.hasPrevious;
	}

	public String toString() {
		return getClass().getSimpleName() + " {" //$NON-NLS-1$
			+ "dummy: " + this.dummy //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "content: " + this.getContent() //$NON-NLS-1$
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

	/**
	 * The Builder that helps building instances of this POJO.
	 */
	@SuppressWarnings("unchecked")
	abstract static class Builder<B extends Builder<B, P, T>, P extends AbstractPage<T>, T extends IBaseEntity> {

		private Long dummy;
		private List<T> content;
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
		public final B withDummy(Long dummyParam) {
			this.dummy = dummyParam;
			return (B)this;
		}

		/**
		 * The requested pageful of records.
		 */
		public final B withContent(List<T> contentParam) {
			this.content = contentParam;
			return (B)this;
		}

		/**
		 * Whether the page has content.
		 */
		public final B withHasContent(Boolean hasContentParam) {
			this.hasContent = hasContentParam;
			return (B)this;
		}

		/**
		 * Whether the page is empty (no content).
		 */
		public final B withIsEmpty(Boolean isEmptyParam) {
			this.isEmpty = isEmptyParam;
			return (B)this;
		}

		/**
		 * The current page number.
		 */
		public final B withNumber(Integer numberParam) {
			this.number = numberParam;
			return (B)this;
		}

		/**
		 * The page size.
		 */
		public final B withSize(Integer sizeParam) {
			this.size = sizeParam;
			return (B)this;
		}

		/**
		 * The number of elements on this page.
		 */
		public final B withNumberOfElements(Integer numberOfElementsParam) {
			this.numberOfElements = numberOfElementsParam;
			return (B)this;
		}

		/**
		 * The total number of pages available.
		 */
		public final B withTotalPages(Integer totalPagesParam) {
			this.totalPages = totalPagesParam;
			return (B)this;
		}

		/**
		 * The total number of records.
		 */
		public final B withTotalElements(Long totalElementsParam) {
			this.totalElements = totalElementsParam;
			return (B)this;
		}

		/**
		 * Whether this is the first page.
		 */
		public final B withIsFirst(Boolean isFirstParam) {
			this.isFirst = isFirstParam;
			return (B)this;
		}

		/**
		 * Whether this is the last page.
		 */
		public final B withIsLast(Boolean isLastParam) {
			this.isLast = isLastParam;
			return (B)this;
		}

		/**
		 * Whether there is next page.
		 */
		public final B withHasNext(Boolean hasNextParam) {
			this.hasNext = hasNextParam;
			return (B)this;
		}

		/**
		 * Whether there is previous page.
		 */
		public final B withHasPrevious(Boolean hasPreviousParam) {
			this.hasPrevious = hasPreviousParam;
			return (B)this;
		}

		P build(P _object) {
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

		public abstract P build();

	}

}
