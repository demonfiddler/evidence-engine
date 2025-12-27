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

import static io.github.demonfiddler.ee.client.EntityKind.CLA;
import static io.github.demonfiddler.ee.client.EntityKind.DEC;
import static io.github.demonfiddler.ee.client.EntityKind.LNK;
import static io.github.demonfiddler.ee.client.EntityKind.JOU;
import static io.github.demonfiddler.ee.client.EntityKind.PBR;
import static io.github.demonfiddler.ee.client.EntityKind.PER;
import static io.github.demonfiddler.ee.client.EntityKind.PUB;
import static io.github.demonfiddler.ee.client.EntityKind.QUO;
import static io.github.demonfiddler.ee.client.EntityKind.TOP;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Range;
import com.google.common.truth.Ordered;

import io.github.demonfiddler.ee.client.util.Authenticator;
import io.github.demonfiddler.ee.client.util.MutationExecutor;
import io.github.demonfiddler.ee.client.util.QueryExecutor;

abstract class AbstractGraphQLTests {

	static record EntityMetaData(String name, EntityKind kind) {
	}

	static final String NL = System.lineSeparator();
	static final boolean CASE_INSENSITIVE = Boolean.getBoolean("ee.test.case-insensitive");

	private static final Map<Class<? extends ITrackedEntity>, EntityMetaData> ENTITY_META_DATA;

	static {
		Map<Class<? extends ITrackedEntity>, EntityMetaData> map = new HashMap<>();
		map.put(Claim.class, new EntityMetaData("claim", CLA));
		map.put(Declaration.class, new EntityMetaData("declaration", DEC));
		map.put(EntityLink.class, new EntityMetaData("entity_link", LNK));
		map.put(Journal.class, new EntityMetaData("journal", JOU));
		map.put(Person.class, new EntityMetaData("person", PER));
		map.put(Publication.class, new EntityMetaData("publication", PUB));
		map.put(Publisher.class, new EntityMetaData("publisher", PBR));
		map.put(Quotation.class, new EntityMetaData("quotation", QUO));
		map.put(Topic.class, new EntityMetaData("topic", TOP));
		ENTITY_META_DATA = Collections.unmodifiableMap(map);
	}

	static EntityKind getEntityKind(ITrackedEntity entity) {
		return ENTITY_META_DATA.get(entity.getClass()).kind;
	}

	static String getEntityName(ITrackedEntity entity) {
		return ENTITY_META_DATA.get(entity.getClass()).name;
	}

	static EntityMetaData getEntityMetaData(ITrackedEntity entity) {
		return ENTITY_META_DATA.get(entity.getClass());
	}

	/**
	 * Returns a timestamp for the current instant, rounded down to the nearest second. This is necessary because
	 * MariaDB timestamps only have second precision.
	 * @return The current timestamp rounded down to the nearest second.
	 */
	static OffsetDateTime getCurrentTimestamp() {
		OffsetDateTime now = OffsetDateTime.now();
		long nanos = now.get(ChronoField.NANO_OF_SECOND);
		return now.minus(nanos, ChronoUnit.NANOS);
	}

	/**
	 * Creates a temporal range with &#177; one second either way of a given date-time value.
	 * @param dt The date-time value.
	 * @return A range spanning {@code dt} minus one second to {@code dt} plus one second.
	 */
	static Range<OffsetDateTime> createRange(OffsetDateTime dt) {
		return createRange(dt, 1, ChronoUnit.SECONDS);
	}

	/**
	 * Creates a temporal range centred around a given date-time value.
	 * @param dt The date-time value.
	 * @param tolerance The tolerance to apply.
	 * @param unit The tolerance unit to use.
	 * @return A range spanning {@code dt} minus {@code tolerance} {@code unit}s to {@code dt} plus {@code tolerance}
	 * {@code unit}s.
	 */
	static Range<OffsetDateTime> createRange(OffsetDateTime dt, long tolerance, TemporalUnit unit) {
		OffsetDateTime dtMin = dt.minus(tolerance, unit);
		OffsetDateTime dtMax = dt.plus(tolerance, unit);
		return Range.open(dtMin, dtMax);
	}

	static void checkPage(AbstractPage<?> actual, int totalElements, int totalPages, int size, int pageNumber,
		boolean hasPrevious, boolean hasNext, boolean isFirst, boolean isLast, List<?> expected, boolean checkOrder) {

		assertThat(actual).hasTotalElements(totalElements);
		assertThat(actual).hasTotalPages(totalPages);
		assertThat(actual).hasSize(size);
		assertThat(actual).hasNumber(pageNumber);
		assertThat(actual).hasPrevious().isEqualTo(hasPrevious);
		assertThat(actual).hasNext().isEqualTo(hasNext);
		assertThat(actual).isFirst().isEqualTo(isFirst);
		assertThat(actual).isLast().isEqualTo(isLast);
		if (expected != null) {
			assertThat(actual).hasContent().isEqualTo(!expected.isEmpty());
			assertThat(actual).isEmpty().isEqualTo(expected.isEmpty());
			assertThat(actual).hasNumberOfElements(expected.size());
			assertThat(actual).content().hasSize(expected.size());
			Ordered content = assertThat(actual).content().containsExactlyElementsIn(expected);
			if (checkOrder)
				content.inOrder();
		}
	}

	/**
	 * Extracts the specified elements of a source list.
	 * @param <T> The list element type.
	 * @param src The source list.
	 * @param indexes The indexes of {@code src} to extract.
	 * @return A sub-list containing the specified elements from {@code src}.
	 */
	static <T> List<T> subList(List<T> src, int... indexes) {
		List<T> subList = new ArrayList<>(indexes.length);
		for (int i = 0; i < indexes.length; i++)
			subList.add(src.get(indexes[i]));
		return subList;
	}

	final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	QueryExecutor queryExecutor;
	@Autowired
	MutationExecutor mutationExecutor;
	@Autowired
	Authenticator authenticator;

	AbstractGraphQLTests() {
	}

}
