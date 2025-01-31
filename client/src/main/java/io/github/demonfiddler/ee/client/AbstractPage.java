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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.client.GraphQLObjectMapper;

public abstract class AbstractPage<T extends IBaseEntity> {

	AbstractPage() {
	}

	/*
	 * This is REALLY annoying! GraphqlClientUtils doesn't check superclasses for inherited fields, so we can't define
	 * the shared fields in this class and rely on Java inheritance. So instead, we'll make all the getters and setters
	 * abstract, so that AbstractPageSubject can still invoke them via this base class.
	 */

	/**
	 * This map contains the deserialized values for the alias, as parsed from the JSON response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	// /**
	// * Dummy ID required for @Entity classes
	// */
	// @JsonProperty("dummy")
	// @GraphQLScalar( fieldName = "dummy", graphQLTypeSimpleName = "ID", javaClass = String.class, listDepth = 0)
	// String dummy;

	// /**
	// * Whether the page has content.
	// */
	// @JsonProperty("hasContent")
	// @GraphQLScalar( fieldName = "hasContent", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth
	// = 0)
	// Boolean hasContent;

	// /**
	// * Whether the page is empty (no content).
	// */
	// @JsonProperty("isEmpty")
	// @GraphQLScalar( fieldName = "isEmpty", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth =
	// 0)
	// Boolean isEmpty;

	// /**
	// * The current page number.
	// */
	// @JsonProperty("number")
	// @GraphQLScalar( fieldName = "number", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	// Integer number;

	// /**
	// * The page size.
	// */
	// @JsonProperty("size")
	// @GraphQLScalar( fieldName = "size", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth = 0)
	// Integer size;

	// /**
	// * The number of elements on this page.
	// */
	// @JsonProperty("numberOfElements")
	// @GraphQLScalar( fieldName = "numberOfElements", graphQLTypeSimpleName = "Int", javaClass = Integer.class,
	// listDepth = 0)
	// Integer numberOfElements;

	// /**
	// * The total number of pages available.
	// */
	// @JsonProperty("totalPages")
	// @GraphQLScalar( fieldName = "totalPages", graphQLTypeSimpleName = "Int", javaClass = Integer.class, listDepth =
	// 0)
	// Integer totalPages;

	// /**
	// * The total number of records.
	// */
	// @JsonProperty("totalElements")
	// @JsonDeserialize(using = CustomJacksonDeserializers.Long.class)
	// @GraphQLScalar( fieldName = "totalElements", graphQLTypeSimpleName = "Long", javaClass = Long.class, listDepth =
	// 0)
	// Long totalElements;

	// /**
	// * Whether this is the first page.
	// */
	// @JsonProperty("isFirst")
	// @GraphQLScalar( fieldName = "isFirst", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth =
	// 0)
	// Boolean isFirst;

	// /**
	// * Whether this is the last page.
	// */
	// @JsonProperty("isLast")
	// @GraphQLScalar( fieldName = "isLast", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth =
	// 0)
	// Boolean isLast;

	// /**
	// * Whether there is next page.
	// */
	// @JsonProperty("hasNext")
	// @GraphQLScalar( fieldName = "hasNext", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class, listDepth =
	// 0)
	// Boolean hasNext;

	// /**
	// * Whether there is previous page.
	// */
	// @JsonProperty("hasPrevious")
	// @GraphQLScalar( fieldName = "hasPrevious", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class,
	// listDepth = 0)
	// Boolean hasPrevious;

	// @JsonProperty("__typename")
	// @GraphQLScalar( fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class, listDepth =
	// 0)
	// String __typename;

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	public abstract void setDummy(Long dummy);
	// public final void setDummy(String dummy) {
	// this.dummy = dummy;
	// }

	/**
	 * Dummy ID required for @Entity classes
	 */
	@JsonProperty("dummy")
	public abstract Long getDummy();
	// public final String getDummy() {
	// return this.dummy;
	// }

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
	public abstract void setHasContent(Boolean hasContent);
	// public final void setHasContent(Boolean hasContent) {
	// this.hasContent = hasContent;
	// }

	/**
	 * Whether the page has content.
	 */
	@JsonProperty("hasContent")
	public abstract Boolean getHasContent();
	// public final Boolean getHasContent() {
	// return this.hasContent;
	// }

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	public abstract void setIsEmpty(Boolean isEmpty);
	// public final void setIsEmpty(Boolean isEmpty) {
	// this.isEmpty = isEmpty;
	// }

	/**
	 * Whether the page is empty (no content).
	 */
	@JsonProperty("isEmpty")
	public abstract Boolean getIsEmpty();
	// public final Boolean getIsEmpty() {
	// return this.isEmpty;
	// }

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	public abstract void setNumber(Integer number);
	// public final void setNumber(Integer number) {
	// this.number = number;
	// }

	/**
	 * The current page number.
	 */
	@JsonProperty("number")
	public abstract Integer getNumber();
	// public final Integer getNumber() {
	// return this.number;
	// }

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	public abstract void setSize(Integer size);
	// public final void setSize(Integer size) {
	// this.size = size;
	// }

	/**
	 * The page size.
	 */
	@JsonProperty("size")
	public abstract Integer getSize();
	// public final Integer getSize() {
	// return this.size;
	// }

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	public abstract void setNumberOfElements(Integer numberOfElements);
	// public final void setNumberOfElements(Integer numberOfElements) {
	// this.numberOfElements = numberOfElements;
	// }

	/**
	 * The number of elements on this page.
	 */
	@JsonProperty("numberOfElements")
	public abstract Integer getNumberOfElements();
	// public final Integer getNumberOfElements() {
	// return this.numberOfElements;
	// }

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	public abstract void setTotalPages(Integer totalPages);
	// public final void setTotalPages(Integer totalPages) {
	// this.totalPages = totalPages;
	// }

	/**
	 * The total number of pages available.
	 */
	@JsonProperty("totalPages")
	public abstract Integer getTotalPages();
	// public final Integer getTotalPages() {
	// return this.totalPages;
	// }

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	public abstract void setTotalElements(Long totalElements);
	// public final void setTotalElements(Long totalElements) {
	// this.totalElements = totalElements;
	// }

	/**
	 * The total number of records.
	 */
	@JsonProperty("totalElements")
	public abstract Long getTotalElements();
	// public final Long getTotalElements() {
	// return this.totalElements;
	// }

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	public abstract void setIsFirst(Boolean isFirst);
	// public final void setIsFirst(Boolean isFirst) {
	// this.isFirst = isFirst;
	// }

	/**
	 * Whether this is the first page.
	 */
	@JsonProperty("isFirst")
	public abstract Boolean getIsFirst();
	// public final Boolean getIsFirst() {
	// return this.isFirst;
	// }

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	public abstract void setIsLast(Boolean isLast);
	// public final void setIsLast(Boolean isLast) {
	// this.isLast = isLast;
	// }

	/**
	 * Whether this is the last page.
	 */
	@JsonProperty("isLast")
	public abstract Boolean getIsLast();
	// public final Boolean getIsLast() {
	// return this.isLast;
	// }

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	public abstract void setHasNext(Boolean hasNext);
	// public final void setHasNext(Boolean hasNext) {
	// this.hasNext = hasNext;
	// }

	/**
	 * Whether there is next page.
	 */
	@JsonProperty("hasNext")
	public abstract Boolean getHasNext();
	// public final Boolean getHasNext() {
	// return this.hasNext;
	// }

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	public abstract void setHasPrevious(Boolean hasPrevious);
	// public final void setHasPrevious(Boolean hasPrevious) {
	// this.hasPrevious = hasPrevious;
	// }

	/**
	 * Whether there is previous page.
	 */
	@JsonProperty("hasPrevious")
	public abstract Boolean getHasPrevious();
	// public final Boolean getHasPrevious() {
	// return this.hasPrevious;
	// }

	@JsonProperty("__typename")
	public abstract void set__typename(String __typename);
	// public final void set__typename(String __typename) {
	// this.__typename = __typename;
	// }

	@JsonProperty("__typename")
	public abstract String get__typename();
	// public final String get__typename() {
	// return this.__typename;
	// }

	/**
	 * This method is called during the json deserialization process, by the {@link GraphQLObjectMapper}, each time an
	 * alias value is read from the json.
	 * @param aliasName
	 * @param aliasDeserializedValue
	 */
	public final void setAliasValue(String aliasName, Object aliasDeserializedValue) {
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
	public final Object getAliasValue(String alias) {
		return this.aliasValues.get(alias);
	}

	public final String toString() {
		return getClass().getSimpleName() // $NON-NLS-1$
			+ " {" //$NON-NLS-1$
			+ "dummy: " + this.getDummy() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "content: " + this.getContent() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "hasContent: " + this.getHasContent() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isEmpty: " + this.getIsEmpty() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "number: " + this.getNumber() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "size: " + this.getSize() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "numberOfElements: " + this.getNumberOfElements() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "totalPages: " + this.getTotalPages() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "totalElements: " + this.getTotalElements() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isFirst: " + this.getIsFirst() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "isLast: " + this.getIsLast() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "hasNext: " + this.getHasNext() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "hasPrevious: " + this.getHasPrevious() //$NON-NLS-1$
			+ ", " //$NON-NLS-1$
			+ "__typename: " + this.get__typename() //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
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
		result = prime * result + ((get__typename() == null) ? 0 : get__typename().hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
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
		if (get__typename() == null) {
			if (other.get__typename() != null)
				return false;
		} else if (!get__typename().equals(other.get__typename()))
			return false;
		return true;
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	static abstract class AbstractBuilder<P extends AbstractPage<T>, T extends IBaseEntity> {

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

		AbstractBuilder() {
		}

		/**
		 * Dummy ID required for @Entity classes
		 */
		public final AbstractBuilder<P, T> withDummy(Long dummyParam) {
			this.dummy = dummyParam;
			return this;
		}

		/**
		 * The requested pageful of records.
		 */
		public final AbstractBuilder<P, T> withContent(List<T> contentParam) {
			this.content = contentParam;
			return this;
		}

		/**
		 * Whether the page has content.
		 */
		public final AbstractBuilder<P, T> withHasContent(Boolean hasContentParam) {
			this.hasContent = hasContentParam;
			return this;
		}

		/**
		 * Whether the page is empty (no content).
		 */
		public final AbstractBuilder<P, T> withIsEmpty(Boolean isEmptyParam) {
			this.isEmpty = isEmptyParam;
			return this;
		}

		/**
		 * The current page number.
		 */
		public final AbstractBuilder<P, T> withNumber(Integer numberParam) {
			this.number = numberParam;
			return this;
		}

		/**
		 * The page size.
		 */
		public final AbstractBuilder<P, T> withSize(Integer sizeParam) {
			this.size = sizeParam;
			return this;
		}

		/**
		 * The number of elements on this page.
		 */
		public final AbstractBuilder<P, T> withNumberOfElements(Integer numberOfElementsParam) {
			this.numberOfElements = numberOfElementsParam;
			return this;
		}

		/**
		 * The total number of pages available.
		 */
		public final AbstractBuilder<P, T> withTotalPages(Integer totalPagesParam) {
			this.totalPages = totalPagesParam;
			return this;
		}

		/**
		 * The total number of records.
		 */
		public final AbstractBuilder<P, T> withTotalElements(Long totalElementsParam) {
			this.totalElements = totalElementsParam;
			return this;
		}

		/**
		 * Whether this is the first page.
		 */
		public final AbstractBuilder<P, T> withIsFirst(Boolean isFirstParam) {
			this.isFirst = isFirstParam;
			return this;
		}

		/**
		 * Whether this is the last page.
		 */
		public final AbstractBuilder<P, T> withIsLast(Boolean isLastParam) {
			this.isLast = isLastParam;
			return this;
		}

		/**
		 * Whether there is next page.
		 */
		public final AbstractBuilder<P, T> withHasNext(Boolean hasNextParam) {
			this.hasNext = hasNextParam;
			return this;
		}

		/**
		 * Whether there is previous page.
		 */
		public final AbstractBuilder<P, T> withHasPrevious(Boolean hasPreviousParam) {
			this.hasPrevious = hasPreviousParam;
			return this;
		}

		public final P build() {
			P _object = createPage();
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
			_object.set__typename(getTypeName()); // -NLS-1$
			return _object;
		}

		abstract P createPage();

		abstract String getTypeName();

	}

}
