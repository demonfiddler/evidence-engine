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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLScalar;

import io.github.demonfiddler.ee.client.util.CustomJacksonDeserializers;

public abstract class AbstractPage<T extends IGraphQLObject> extends AbstractGraphQLEntity implements IPage {

	AbstractPage() {
	}

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	@GraphQLScalar(fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = Long.class, listDepth = 0)
	Long dummy;

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	@GraphQLScalar(fieldName = "hasContent", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean hasContent;

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	@GraphQLScalar(fieldName = "isEmpty", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isEmpty;

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	@GraphQLScalar(fieldName = "number", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer number;

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	@GraphQLScalar(fieldName = "size", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer size;

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	@GraphQLScalar(fieldName = "numberOfElements", graphQLTypeSimpleName = "Int", javaClass = Integer.class,
		listDepth = 0)
	Integer numberOfElements;

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	@GraphQLScalar(fieldName = "totalPages", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	Integer totalPages;

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	@JsonDeserialize(using = CustomJacksonDeserializers.Long.class)
	@GraphQLScalar(fieldName = "totalElements", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth = 0)
	Long totalElements;

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	@GraphQLScalar(fieldName = "isFirst", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isFirst;

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	@GraphQLScalar(fieldName = "isLast", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean isLast;

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	@GraphQLScalar(fieldName = "hasNext", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth = 0)
	Boolean hasNext;

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	@GraphQLScalar(fieldName = "hasPrevious", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
		listDepth = 0)
	Boolean hasPrevious;

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	public final void setDummy(Long dummy) {
		this.dummy = dummy;
	}

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	public final Long getDummy() {
		return this.dummy;
	}

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	public abstract void setContent(List<T> content);

	/**
	 * The requested pageful of records.
	 */
	@JsonProperty("content")
	public abstract List<T> getContent();

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	public final void setHasContent(Boolean hasContent) {
		this.hasContent = hasContent;
	}

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	public final Boolean getHasContent() {
		return this.hasContent;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	public final void setIsEmpty(Boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	public final Boolean getIsEmpty() {
		return this.isEmpty;
	}

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	public final void setNumber(Integer number) {
		this.number = number;
	}

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	public final Integer getNumber() {
		return this.number;
	}

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	public final void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	public final Integer getSize() {
		return this.size;
	}

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	public final void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	public final Integer getNumberOfElements() {
		return this.numberOfElements;
	}

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	public final void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	public final Integer getTotalPages() {
		return this.totalPages;
	}

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	public final void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	public final Long getTotalElements() {
		return this.totalElements;
	}

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	public final void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	public final Boolean getIsFirst() {
		return this.isFirst;
	}

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	public final void setIsLast(Boolean isLast) {
		this.isLast = isLast;
	}

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	public final Boolean getIsLast() {
		return this.isLast;
	}

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	public final void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	public final Boolean getHasNext() {
		return this.hasNext;
	}

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	public final void setHasPrevious(Boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	public final Boolean getHasPrevious() {
		return this.hasPrevious;
	}

	public final String toString() {
		return getClass().getSimpleName() //
			+ " {" //
			+ "dummy: " + this.getDummy() //
			+ ", " //
			+ "content: " + this.getContent() //
			+ ", " //
			+ "hasContent: " + this.getHasContent() //
			+ ", " //
			+ "isEmpty: " + this.getIsEmpty() //
			+ ", " //
			+ "number: " + this.getNumber() //
			+ ", " //
			+ "size: " + this.getSize() //
			+ ", " //
			+ "numberOfElements: " + this.getNumberOfElements() //
			+ ", " //
			+ "totalPages: " + this.getTotalPages() //
			+ ", " //
			+ "totalElements: " + this.getTotalElements() //
			+ ", " //
			+ "isFirst: " + this.getIsFirst() //
			+ ", " //
			+ "isLast: " + this.getIsLast() //
			+ ", " //
			+ "hasNext: " + this.getHasNext() //
			+ ", " //
			+ "hasPrevious: " + this.getHasPrevious() //
			+ ", " //
			+ "__typename: " + this.get__typename() //
			+ "}";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getDummy() == null) ? 0 : getDummy().hashCode());
		result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
		result = prime * result + ((getHasContent() == null) ? 0 : getHasContent().hashCode());
		result = prime * result + ((getIsEmpty() == null) ? 0 : getIsEmpty().hashCode());
		result = prime * result + ((getNumber() == null) ? 0 : getNumber().hashCode());
		result = prime * result + ((getSize() == null) ? 0 : getSize().hashCode());
		result = prime * result + ((getNumberOfElements() == null) ? 0 : getNumberOfElements().hashCode());
		result = prime * result + ((getTotalPages() == null) ? 0 : getTotalPages().hashCode());
		result = prime * result + ((getTotalElements() == null) ? 0 : getTotalElements().hashCode());
		result = prime * result + ((getIsFirst() == null) ? 0 : getIsFirst().hashCode());
		result = prime * result + ((getIsLast() == null) ? 0 : getIsLast().hashCode());
		result = prime * result + ((getHasNext() == null) ? 0 : getHasNext().hashCode());
		result = prime * result + ((getHasPrevious() == null) ? 0 : getHasPrevious().hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		AbstractPage<T> other = (AbstractPage<T>)obj;
		if (getDummy() == null) {
			if (other.getDummy() != null)
				return false;
		} else if (!getDummy().equals(other.getDummy()))
			return false;
		if (getContent() == null) {
			if (other.getContent() != null)
				return false;
		} else if (!getContent().equals(other.getContent()))
			return false;
		if (getHasContent() == null) {
			if (other.getHasContent() != null)
				return false;
		} else if (!getHasContent().equals(other.getHasContent()))
			return false;
		if (getIsEmpty() == null) {
			if (other.getIsEmpty() != null)
				return false;
		} else if (!getIsEmpty().equals(other.getIsEmpty()))
			return false;
		if (getNumber() == null) {
			if (other.getNumber() != null)
				return false;
		} else if (!getNumber().equals(other.getNumber()))
			return false;
		if (getSize() == null) {
			if (other.getSize() != null)
				return false;
		} else if (!getSize().equals(other.getSize()))
			return false;
		if (getNumberOfElements() == null) {
			if (other.getNumberOfElements() != null)
				return false;
		} else if (!getNumberOfElements().equals(other.getNumberOfElements()))
			return false;
		if (getTotalPages() == null) {
			if (other.getTotalPages() != null)
				return false;
		} else if (!getTotalPages().equals(other.getTotalPages()))
			return false;
		if (getTotalElements() == null) {
			if (other.getTotalElements() != null)
				return false;
		} else if (!getTotalElements().equals(other.getTotalElements()))
			return false;
		if (getIsFirst() == null) {
			if (other.getIsFirst() != null)
				return false;
		} else if (!getIsFirst().equals(other.getIsFirst()))
			return false;
		if (getIsLast() == null) {
			if (other.getIsLast() != null)
				return false;
		} else if (!getIsLast().equals(other.getIsLast()))
			return false;
		if (getHasNext() == null) {
			if (other.getHasNext() != null)
				return false;
		} else if (!getHasNext().equals(other.getHasNext()))
			return false;
		if (getHasPrevious() == null) {
			if (other.getHasPrevious() != null)
				return false;
		} else if (!getHasPrevious().equals(other.getHasPrevious()))
			return false;
		return true;
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	@SuppressWarnings("unchecked")
	static abstract class Builder<B extends Builder<B, P, T>, P extends AbstractPage<T>, T extends IBaseEntity>
		extends AbstractGraphQLEntity.Builder<B, P> {

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

		Builder() {
		}

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

		public final P build() {
			P _object = build(createPage());
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

		abstract P createPage();

	}

}
