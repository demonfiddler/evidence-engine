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

import static com.google.common.truth.Truth.assertThat;
import static io.github.demonfiddler.ee.client.truth.LogPageSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.LogSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.TrackedEntitySubject.assertThatTrackedEntity;
import static io.github.demonfiddler.ee.client.truth.UserSubject.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import com.google.common.collect.Range;

@TestInstance(PER_CLASS)
abstract class AbstractTrackedEntityTests<T extends ITrackedEntity> extends AbstractGraphQLTests {

	static final String FORMAT_LONG = "(format: LONG)";

	static final OffsetDateTime[] LOG_DATES = new OffsetDateTime[3];

	@BeforeAll
	void beforeAll() throws Exception {
		Arrays.fill(LOG_DATES, null);
		assertThat(authenticator.login()).isTrue();
	}

	abstract EntityKind getEntityKind();

	void checkTrackedEntity(T entity, String status, OffsetDateTime earliestCreated, OffsetDateTime earliestUpdated,
		TransactionKind... txnKinds) {

		Range<OffsetDateTime> creationRange = createRange(earliestCreated);
		assertThatTrackedEntity(entity).isNotNull();
		assertThatTrackedEntity(entity).hasStatus(status);
		assertThatTrackedEntity(entity).created().isIn(creationRange);
		checkUser(entity.getCreatedByUser());
		if (earliestUpdated == null) {
			assertThatTrackedEntity(entity).updated().isNull();
			assertThatTrackedEntity(entity).updatedByUser().isNull();
		} else {
			Range<OffsetDateTime> updatedRange = createRange(earliestUpdated);
			assertThatTrackedEntity(entity).updated().isNotNull();
			assertThatTrackedEntity(entity).updated().isIn(updatedRange);
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
			Range<OffsetDateTime> logRange = createRange(LOG_DATES[i]);
			assertThat(log).timestamp().isIn(logRange);
			assertThat(log).hasTransactionKind(txnKinds[i].label());
			assertThat(log).hasEntityKind(getEntityKind().label());
			assertThat(log).hasEntityId(Long.valueOf(entity.getId()));
			User logUser = log.getUser();
			checkUser(logUser);
		}
	}

	void checkUser(User user) {
		User authUser = authenticator.getUser();
		assertThat(authUser).isNotNull();
		assertThat(user).isNotNull();
		assertThat(user).hasId(authUser.getId());
		assertThat(user).hasUsername(authUser.getUsername());
		assertThat(user).hasFirstName(authUser.getFirstName());
		assertThat(user).hasLastName(authUser.getLastName());
	}

	Long getEntityId(IBaseEntity entity) {
		return entity == null ? null : entity.getId();
	}

	List<Long> getEntityIds(List<? extends IBaseEntity> entities) {
		return entities == null ? Collections.emptyList() : entities.stream().map(e -> e.getId()).toList();
	}

}
