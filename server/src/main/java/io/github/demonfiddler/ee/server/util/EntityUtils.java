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

package io.github.demonfiddler.ee.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.Country;
import io.github.demonfiddler.ee.server.model.Declaration;
// import io.github.demonfiddler.ee.server.model.Group;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.EntityLink;
import io.github.demonfiddler.ee.server.model.Group;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.IBaseEntityPage;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.OrderInput;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.Person;
import io.github.demonfiddler.ee.server.model.Publication;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.model.Quotation;
import io.github.demonfiddler.ee.server.model.SortInput;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.CustomRepository;
import io.github.demonfiddler.ee.server.repository.QueryPair;
import jakarta.persistence.Query;

/**
 * A Spring bean that contains various utilities.
 * @author demonfiddler
 */
@Component
public class EntityUtils {

	/** The platform's newline character sequence. */
	public static final CharSequence NL = System.getProperty("line.separator");
	private static final BidiMap<Class<?>, EntityKind> ENTITY_KINDS = new DualHashBidiMap<>();
	private static final BidiMap<Class<?>, String> ENTITY_NAMES = new DualHashBidiMap<>();

	static {
		ENTITY_KINDS.put(Claim.class, EntityKind.CLA);
		ENTITY_KINDS.put(Country.class, EntityKind.COU);
		ENTITY_KINDS.put(Declaration.class, EntityKind.DEC);
		ENTITY_KINDS.put(Group.class, EntityKind.GRP);
		ENTITY_KINDS.put(Journal.class, EntityKind.JOU);
		ENTITY_KINDS.put(EntityLink.class, EntityKind.LNK);
		ENTITY_KINDS.put(Person.class, EntityKind.PER);
		ENTITY_KINDS.put(Publication.class, EntityKind.PUB);
		ENTITY_KINDS.put(Publisher.class, EntityKind.PBR);
		ENTITY_KINDS.put(Quotation.class, EntityKind.QUO);
		ENTITY_KINDS.put(Topic.class, EntityKind.TOP);
		ENTITY_KINDS.put(User.class, EntityKind.USR);

		ENTITY_NAMES.put(Claim.class, "claim");
		ENTITY_NAMES.put(Country.class, "country");
		ENTITY_NAMES.put(Declaration.class, "declaration");
		ENTITY_NAMES.put(Group.class, "group");
		ENTITY_NAMES.put(Journal.class, "journal");
		ENTITY_NAMES.put(EntityLink.class, "entity_link");
		ENTITY_NAMES.put(Person.class, "person");
		ENTITY_NAMES.put(Publication.class, "publication");
		ENTITY_NAMES.put(Publisher.class, "publisher");
		ENTITY_NAMES.put(Quotation.class, "quotation");
		ENTITY_NAMES.put(Topic.class, "topic");
		ENTITY_NAMES.put(User.class, "user");
	}

	/**
	 * Converts an {@link Iterable} of a given source class into a list of a given target class.
	 * @param <T> The target class
	 * @param sources The {@link Iterable} of source instances.
	 * @param sourceClass The source class
	 * @param targetClass The target class
	 * @return The list of target classes, where each instance is mapped from the source class found in <I>sources</I>.
	 * It returns null if <I>sources</I> is null.
	 */
	public <T> List<T> toList(Iterable<T> sources, Class<T> targetClass) {
		if (sources == null)
			return null;

		List<T> ret = new ArrayList<>();
		for (T t : sources)
			ret.add(t);

		return ret;
	}

	/**
	 * Returns a {@code Map} containing the values of a specified field.
	 * @param <K> The type of the key
	 * @param <V> The type of the value
	 * @param keys A list of keys
	 * @param accessor The accessor method to retrieve the values.
	 * @return A list of values extracted from {@code keys} using {@code accessor}.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getValuesMap(List<K> keys, Function<K, V> accessor) {
		Map<K, V> values = new HashMap<>();
		for (K key : keys) {
			V value = accessor.apply(key);
			if (value != null) {
				if (value instanceof HibernateProxy)
					value = (V)Hibernate.unproxy(value);
				values.put(key, value);
			}
		}
		return values;
	}

	/**
	 * Returns a {@code Map} containing the {@code List} values of a specified field.
	 * @param <K> The type of the key
	 * @param <V> The type of the value
	 * @param keys A list of keys
	 * @param accessor The accessor method to retrieve the values.
	 * @return A map of list of values extracted from {@code keys} using {@code accessor}.
	 */
	public <K, V> Map<K, List<V>> getListValuesMap(List<K> keys, Function<K, List<V>> accessor) {
		Map<K, List<V>> values = new HashMap<>(keys.size());
		for (K key : keys) {
			List<V> value = accessor.apply(key);
			if (value != null)
				values.put(key, value);
		}
		return values;
	}

	/**
	 * Returns the {@code EntityKind} for the given entity.
	 * @param entityClass The entity.
	 * @return The corresponding entity kind.
	 */
	public EntityKind getEntityKind(ITrackedEntity entity) {
		return EntityKind.valueOf(entity.getEntityKind());
	}

	/**
	 * Returns the {@code EntityKind} for the given entity class.
	 * @param entityClass The entity class.
	 * @return The corresponding entity kind.
	 */
	public EntityKind getEntityKind(Class<? extends IBaseEntity> entityClass) {
		return ENTITY_KINDS.get(entityClass);
	}

	/**
	 * Returns the class for the given {@code EntityKind}.
	 * @param entityKind The entity kind.
	 * @return The corresponding entity class.
	 */
	public Class<?> getEntityClass(EntityKind entityKind) {
		return ENTITY_KINDS.getKey(entityKind);
	}

	/**
	 * Returns the database entity name for the given entity class.
	 * @param entityClass The entity class.
	 * @return The corresponding entity name.
	 */
	public String getEntityName(Class<? extends IBaseEntity> entityClass) {
		return ENTITY_NAMES.get(entityClass);
	}

	/**
	 * Returns the database entity name for the given {@code EntityKind}.
	 * @param entityKind The entity kind.
	 * @return The corresponding entity name.
	 */
	public String getEntityName(EntityKind entityKind) {
		return ENTITY_NAMES.get(getEntityClass(entityKind));
	}

	/**
	 * Invokes the appropriate {@code find*()} method in the specified repository.
	 * @param <T> The entity type.
	 * @param <F> The query filter type.
	 * @param <R> The repository type.
	 * @param <P> The {@code *Page} class to return.
	 * @param filter The query filter to apply.
	 * @param pageSort How to paginate and/or sort the results.
	 * @param repository The repository to query.
	 * @param ctor The result page query supplier (typically a constructor reference).
	 */
	public <T extends IBaseEntity, F, R extends JpaRepository<T, ?> & CustomRepository<T, F>, P extends IBaseEntityPage<T>>
		P findByFilter(F filter, PageableInput pageSort, R repository, Supplier<P> ctor) {

		Pageable pageable = toPageable(pageSort);
		Page<T> page;
		if (filter == null) {
			if (pageable.isPaged()) {
				page = repository.findAll(pageable);
			} else {
				if (pageable.getSort().isSorted()) {
					// If the sort involves non-native null precedence, call findByFilter() instead.
					// TODO: TEMPORARY: JPA 3.2 NullHandling won't be supported until Spring Data JPA 4.0;
					// see https://github.com/spring-projects/spring-data-jpa/issues/3729
					if (usesNonNativeNullPrecedence(pageable.getSort()))
						page = repository.findByFilter(null, pageable);
					else
						page = new PageImpl<>(repository.findAll(pageable.getSort()));
				} else {
					page = new PageImpl<>(repository.findAll());
				}
			}
		} else {
			page = repository.findByFilter(filter, pageable);
		}
		return toEntityPage(page, ctor);
	}

	/**
	 * Indicates whether a sort involves a non-native null precedence specification.
	 * @param sort The sort specification.
	 * @return {@code true} if any of {@code sort}'s {@code Order}'s are other than
	 * {@code NullHandling.NATIVE}.
	 */
	private boolean usesNonNativeNullPrecedence(Sort sort) {
		return !sort.filter(o -> o.getNullHandling() != NullHandling.NATIVE).isEmpty();
	}

	/**
	 * Converts a domain pagination object to the corresponding JPA repository API type.
	 * @param pageableInput The domain pagination object, can be {@code null}.
	 * @return An equivalent JPA repository pageable, will never be {@code null}.
	 */
	@SuppressWarnings("null")
	public Pageable toPageable(PageableInput pageableInput) {
		Pageable pageable;
		if (pageableInput != null) {
			SortInput sortInput = pageableInput.getSort();
			boolean isSorted = sortInput != null;
			Sort sort;
			if (isSorted) {
				List<Order> orders = new ArrayList<>(sortInput.getOrders().size());
				for (OrderInput oi : sortInput.getOrders()) {
					Order order;
					switch (oi.getDirection()) {
						case ASC:
							order = Order.asc(oi.getProperty());
							break;
						case DESC:
							order = Order.desc(oi.getProperty());
							break;
						default:
							throw new IllegalArgumentException("Unsupported direction: " + oi.getDirection());
					}
					switch (oi.getNullHandling()) {
						case NATIVE:
							break;
						case NULLS_FIRST:
							order = order.nullsFirst();
							break;
						case NULLS_LAST:
							order = order.nullsLast();
							break;
						default:
							throw new IllegalArgumentException("Unsupported NullHandling: " + oi.getNullHandling());
					}
					if (oi.getIgnoreCase())
						order = order.ignoreCase();
					orders.add(order);
				}
				sort = Sort.by(orders);
			} else {
				sort = Sort.unsorted();
			}
			Integer pageSize = pageableInput.getPageSize();
			if (pageSize != null && pageSize > 0) {
				Integer pageNumberObj = pageableInput.getPageNumber();
				int pageNumber = pageNumberObj == null ? 0 : pageNumberObj;
				if (isSorted) {
					pageable = PageRequest.of(pageNumber, pageSize, sort);
				} else {
					pageable = PageRequest.of(pageNumber, pageSize);
				}
			} else if (isSorted) {
				pageable = Pageable.unpaged(sort);
			} else {
				pageable = Pageable.unpaged();
			}
		} else {
			pageable = Pageable.unpaged();
		}
		return pageable;
	}

	/**
	 * Converts a JPA {@code Page<T>} to a domain page type that implements {@code IBaseEntityPage<T>}.
	 * @param <P> The domain page type.
	 * @param <T> The domain content type.
	 * @param jpaPage The JPA page.
	 * @param ctor The public no-args constructor for the domain page.
	 * @return The equivalent domain page instance.
	 */
	public <P extends IBaseEntityPage<T>, T extends IBaseEntity> P toEntityPage(Page<T> jpaPage, Supplier<P> ctor) {
		P entityPage = ctor.get();
		entityPage.setContent(jpaPage.getContent());
		entityPage.setHasContent(jpaPage.hasContent());
		entityPage.setIsEmpty(jpaPage.isEmpty());
		entityPage.setHasNext(jpaPage.hasNext());
		entityPage.setHasPrevious(jpaPage.hasPrevious());
		entityPage.setIsFirst(jpaPage.isFirst());
		entityPage.setIsLast(jpaPage.isLast());
		entityPage.setNumber(jpaPage.getNumber());
		entityPage.setNumberOfElements(jpaPage.getNumberOfElements());
		entityPage.setSize(jpaPage.getSize());
		entityPage.setTotalElements(jpaPage.getTotalElements());
		entityPage.setTotalPages(jpaPage.getTotalPages());
		return entityPage;
	}

	/**
	 * Appends an ORDER BY clause to a string buffer.
	 * @param sql The SQL buffer.
	 * @param pageable Paginator with sorting information.
	 * @param qualifier The qualifier with which to prefix field references (e.g. {@code "e."}). For unprefixed, pass an
	 * empty string.
	 * @param multiline Whether to place the ORDER BY and each sort term on a separate line.
	 */
	public void appendOrderByClause(StringBuilder sql, Pageable pageable, String qualifier, boolean multiline) {
		if (pageable.getSort().isSorted()) {
			if (multiline)
				sql.append(NL);
			else
				sql.append(' ');
			sql.append("ORDER BY");
			if (multiline)
				sql.append(NL).append("    ");
			else
				sql.append(' ');
			boolean needsComma = false;
			for (Order order : pageable.getSort().toList()) {
				String property = toDbColumnName(order.getProperty());
				if (needsComma) {
					sql.append(',');
					if (multiline)
						sql.append(NL).append("    ");
					else
						sql.append(' ');
				}
				switch (order.getNullHandling()) {
					case NULLS_FIRST:
						sql.append("CASE WHEN ").append(qualifier).append('\"').append(property)
							.append("\" is NULL THEN 0 ELSE 1 END, ");
						if (multiline)
							sql.append(NL).append("    ");
						else
							sql.append(' ');
						break;
					case NULLS_LAST:
						sql.append("CASE WHEN ").append(qualifier).append('\"').append(property)
							.append("\" is NULL THEN 1 ELSE 0 END,");
						if (multiline)
							sql.append(NL).append("    ");
						else
							sql.append(' ');
						break;
					default:
						break;
				}
				if (order.isIgnoreCase())
					sql.append("LOWER(");
				// TODO: map domain property name to database column name
				sql.append(qualifier).append('\"').append(property).append('\"');
				if (order.isIgnoreCase())
					sql.append(')');
				sql.append(' ').append(order.getDirection());
				needsComma = true;
			}
		}
	}

	/**
	 * Appends the order-by specification to a query name.
	 * @param queryName The query name under construction.
	 * @param pageable Pagination and sorting specification.
	 */
	public void appendOrderByToQueryName(StringBuilder queryName, Pageable pageable) {
		queryName.append("OrderBy");
		pageable.getSort().forEach(o -> queryName.append(StringUtils.firstToUpper(o.getProperty()))
			.append(o.getNullHandling() == NullHandling.NATIVE //
				? "" : StringUtils.firstToUpper(o.getNullHandling().name()))
			.append(o.isIgnoreCase() ? "IgnoreCase" : "").append(StringUtils.firstToUpper(o.getDirection().name())));
	}

	/**
	 * Sets the query's {@code firstResult} and {@code maxResults} from a {@code Pageable}. This method should only be
	 * called when it has been determined that the query is indeed to be paginated (i.e.,
	 * {@code pageable.isPaged() == true}).
	 * @param query The query for which pagination parameters are to be set.
	 * @param pageable Specifies pagination.
	 */
	public void setQueryPagination(Query query, Pageable pageable) {
		query.setFirstResult((int)pageable.getOffset()).setMaxResults(pageable.getPageSize());
	}

	/**
	 * Sets parameters for both COUNT and SELECT queries.
	 * @param queries The COUNT and SELECT queries.
	 * @param params The parameters to set.
	 */
	public void setQueryParameters(QueryPair queries, Map<String, Object> params) {
		setQueryParameters(queries.countQuery(), params);
		setQueryParameters(queries.selectQuery(), params);
	}

	/**
	 * Sets query parameters.
	 * @param query The query to parameterise.
	 * @param params The parameters to set.
	 */
	public void setQueryParameters(Query query, Map<String, Object> params) {
		for (Entry<String, Object> param : params.entrySet())
			query.setParameter(param.getKey(), param.getValue());
	}

	/**
	 * Converts a JPA property name to a database name using the default camelCase -&gt; snake_case mapping.
	 * @param property The JPA property name.
	 * @return The database column name.
	 */
	private String toDbColumnName(String property) {
		// TODO: see if there is a better way of doing this using the JPA API.
		StringBuilder buf = new StringBuilder(property.length());
		for (int i = 0; i < property.length(); i++) {
			char c = property.charAt(i);
			if (Character.isUpperCase(c))
				buf.append('_').append(Character.toLowerCase(c));
			else
				buf.append(c);
		}
		return buf.toString();
	}

}
