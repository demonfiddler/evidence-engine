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
import static io.github.demonfiddler.ee.client.EntityKind.JOU;
import static io.github.demonfiddler.ee.client.EntityKind.PBR;
import static io.github.demonfiddler.ee.client.EntityKind.PER;
import static io.github.demonfiddler.ee.client.EntityKind.PUB;
import static io.github.demonfiddler.ee.client.EntityKind.QUO;
import static io.github.demonfiddler.ee.client.EntityKind.TOP;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.truth.Ordered;

import io.github.demonfiddler.ee.client.util.MutationExecutor;
import io.github.demonfiddler.ee.client.util.QueryExecutor;

abstract class AbstractGraphQLTests {

    static final String NL = System.lineSeparator();

    static record EntityMetaData(String name, EntityKind kind) {};

    private static final Map<Class<? extends ITrackedEntity>, EntityMetaData> ENTITY_META_DATA;

    static <T extends ITrackedEntity> EntityKind getEntityKind(T entity) {
        return ENTITY_META_DATA.get(entity.getClass()).kind;
    }

    static <T extends ITrackedEntity> String getEntityName(T entity) {
        return ENTITY_META_DATA.get(entity.getClass()).name;
    }

    static <T extends ITrackedEntity> EntityMetaData getEntityMetaData(T entity) {
        return ENTITY_META_DATA.get(entity.getClass());
    }

    static {
        Map<Class<? extends ITrackedEntity>, EntityMetaData> map = new HashMap<>();
        map.put(Claim.class, new EntityMetaData("claim", CLA));
        map.put(Declaration.class, new EntityMetaData("declaration", DEC));
        map.put(Journal.class, new EntityMetaData("journal", JOU));
        map.put(Person.class, new EntityMetaData("person", PER));
        map.put(Publication.class, new EntityMetaData("publication", PUB));
        map.put(Publisher.class, new EntityMetaData("publisher", PBR));
        map.put(Quotation.class, new EntityMetaData("quotation", QUO));
        map.put(Topic.class, new EntityMetaData("topic", TOP));
        ENTITY_META_DATA = Collections.unmodifiableMap(map);
    }

	@Autowired
    QueryExecutor queryExecutor;
    @Autowired
    MutationExecutor mutationExecutor;

    protected AbstractGraphQLTests() {
    }

	/*<P extends AbstractPage<T>, T extends IBaseEntity>*/ void checkPage(AbstractPage<?> actual, int totalElements, int totalPages, int size, int pageNumber,
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
	<T> List<T> subList(List<T> src, int... indexes) {
		List<T> subList = new ArrayList<>(indexes.length);
		for (int i = 0; i < indexes.length; i++)
			subList.add(src.get(indexes[i]));
		return subList;
	}

}
