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

import static com.google.common.truth.Truth.assertThat;
import static io.github.demonfiddler.ee.client.truth.LogPageSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.LogSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.TrackedEntitySubject.assertThatTrackedEntity;
import static io.github.demonfiddler.ee.client.truth.UserSubject.assertThat;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

import com.google.common.truth.Ordered;

abstract class TrackedEntityTests<T extends IBaseEntity & ITrackedEntity> extends AbstractGraphQLTests<T> {

	static final OffsetDateTime[] LOG_DATES = new OffsetDateTime[3];

	@BeforeAll
	static void resetTestState() {
		Arrays.fill(LOG_DATES, null);
	}

	abstract EntityKind getEntityKind();

	void checkTrackedEntity(T entity, String status, OffsetDateTime earliestCreated, OffsetDateTime earliestUpdated,
		TransactionKind... txnKinds) {

		assertThatTrackedEntity(entity).isNotNull();
		assertThatTrackedEntity(entity).hasStatus(status);
		assertThatTrackedEntity(entity).created().isAtLeast(earliestCreated);
		checkUser(entity.getCreatedByUser());
		if (earliestUpdated == null) {
			assertThatTrackedEntity(entity).updated().isNull();
			assertThatTrackedEntity(entity).updatedByUser().isNull();
		} else {
			assertThatTrackedEntity(entity).updated().isNotNull();
			assertThatTrackedEntity(entity).updated().isAtLeast(earliestUpdated);
			checkUser(entity.getUpdatedByUser());
		}
		LogPage logPage = entity.getLog();
		assertThat(logPage).isNotNull();
		assertThat(logPage).hasContent().isTrue();
		assertThat(logPage).hasNext().isFalse();
		assertThat(logPage).hasPrevious().isFalse();
		assertThat(logPage).hasNumber(0);
		assertThat(logPage).hasNumberOfElements(txnKinds.length);
		assertThat(logPage).hasSize(txnKinds.length);
		assertThat(logPage).hasTotalElements(txnKinds.length);
		assertThat(logPage).hasTotalPages(1);
		assertThat(logPage).isEmpty().isFalse();
		assertThat(logPage).isFirst().isTrue();
		assertThat(logPage).isLast().isTrue();
		List<Log> logEntries = logPage.getContent();
		assertThat(logEntries).isNotNull();
		assertThat(logPage).hasSize(logEntries.size());
		for (int i = 0; i < logEntries.size(); i++) {
			Log log = logPage.getContent().get(i);
			assertThat(log).hasTimestamp(LOG_DATES[i]);
			assertThat(log).hasTransactionKind(txnKinds[i].getLabel());
			assertThat(log).hasEntityKind(getEntityKind().getLabel());
			assertThat(log).hasEntityId(Long.valueOf(entity.getId()));
			User logUser = log.getUser();
			checkUser(logUser);
		}
	}

	void checkPage(AbstractPage<T> actuals, int totalElements, int totalPages, int pageSize, int pageNumber,
		boolean hasPrevious, boolean hasNext, boolean isFirst, boolean isLast, List<T> expected, boolean checkOrder) {

		assertThat(actuals).hasTotalElements(totalElements);
		assertThat(actuals).hasTotalPages(totalPages);
		assertThat(actuals).hasSize(pageSize);
		assertThat(actuals).hasNumber(pageNumber);
		assertThat(actuals).hasPrevious().isEqualTo(hasPrevious);
		assertThat(actuals).hasNext().isEqualTo(hasNext);
		assertThat(actuals).isFirst().isEqualTo(isFirst);
		assertThat(actuals).isLast().isEqualTo(isLast);
		assertThat(actuals).hasContent().isTrue();
		assertThat(actuals).isEmpty().isFalse();
		if (expected != null) {
			assertThat(actuals).hasNumberOfElements(expected.size());
			assertThat(actuals).content().hasSize(expected.size());
			Ordered content = assertThat(actuals).content().containsExactlyElementsIn(expected);
			if (checkOrder)
				content.inOrder();
		}
	}

	void checkUser(User user) {
		assertThat(user).isNotNull();
		assertThat(user).hasId(0L);
		assertThat(user).hasLogin("root");
		assertThat(user).hasFirstName("Root");
		assertThat(user).hasLastName("User");
	}

	Long getEntityId(IBaseEntity entity) {
		return entity == null ? null : entity.getId();
	}

	List<Long> getEntityIds(List<? extends IBaseEntity> entities) {
		return entities == null ? Collections.emptyList() : entities.stream().map(e -> e.getId()).toList();
	}

	/**
	 * Extracts the specified elements of a source list.
	 * @param <T> The list element type.
	 * @param src The source list.
	 * @param indexes The indexes of {@code src} to extract.
	 * @return A sub-list containing the specified elements from {@code src}.
	 */
	List<T> subList(List<T> src, int... indexes) {
		List<T> subList = new ArrayList<>(indexes.length);
		for (int i = 0; i < indexes.length; i++)
			subList.add(src.get(indexes[i]));
		return subList;
	}

}
