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

import static io.github.demonfiddler.ee.client.TransactionKind.CRE;
import static io.github.demonfiddler.ee.client.TransactionKind.DEL;
import static io.github.demonfiddler.ee.client.TransactionKind.UPD;
import static io.github.demonfiddler.ee.client.truth.TopicSubject.assertThat;

import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = GraphQLClientMain.class)
@TestMethodOrder(OrderAnnotation.class)
class TopicTests extends TrackedEntityTests<Topic> {

	private static Topic EXPECTED_PARENT;
	private static Topic EXPECTED_CHILD;
	private static final OffsetDateTime[] LOG_DATES_PARENT = new OffsetDateTime[3];
	private static final OffsetDateTime[] LOG_DATES_CHILD = new OffsetDateTime[3];
	private static final String RESPONSE_SPEC = //
		"""
		{
			id
			status
			created
			createdByUser {
				id
				login
				firstName
				lastName
			}
			updated
			updatedByUser {
				id
				login
				firstName
				lastName
			}
			log {
				hasContent
				isEmpty
				number
				size
				numberOfElements
				totalPages
				totalElements
				isFirst
				isLast
				hasNext
				hasPrevious
				content {
					timestamp
					transactionKind
					entityId
					entityKind
					user {
						id
						login
						firstName
						lastName
					}
				}
			}
			label
			description
			parent {
				id
			}
			children {
				id
			}
		}
		""";

	static boolean hasExpectedTopics() {
		return EXPECTED_PARENT != null && EXPECTED_CHILD != null;
	}

	@Test
	@Order(1)
	void createTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		EXPECTED_PARENT = null;
		EXPECTED_CHILD = null;

		TopicInput parentInput = TopicInput.builder() //
			.withLabel("Parent label") //
			.withDescription("Parent description") //
			.build();
		OffsetDateTime earliestCreated = OffsetDateTime.now();
		Topic actualParent = mutationExecutor.createTopic(RESPONSE_SPEC, parentInput);

		LOG_DATES[0] = LOG_DATES_PARENT[0] = actualParent.getCreated();
		checkTopic(actualParent, StatusKind.DRA.getLabel(), earliestCreated, null, parentInput.getLabel(),
			parentInput.getDescription(), parentInput.getParentId(), Collections.emptyList(), CRE);

		TopicInput childInput = TopicInput.builder() //
			.withLabel("Child label") //
			.withDescription("Child description") //
			.withParentId(actualParent.getId()) //
			.build();
		earliestCreated = OffsetDateTime.now();
		Topic actualChild = mutationExecutor.createTopic(RESPONSE_SPEC, childInput);

		LOG_DATES[0] = LOG_DATES_CHILD[0] = actualChild.getCreated();
		checkTopic(actualChild, StatusKind.DRA.getLabel(), earliestCreated, null, childInput.getLabel(),
			childInput.getDescription(), childInput.getParentId(), Collections.emptyList(), CRE);

		EXPECTED_PARENT = actualParent;
		EXPECTED_CHILD = actualChild;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Topic expectedParent = EXPECTED_PARENT;
		Topic expectedChild = EXPECTED_CHILD;
		EXPECTED_PARENT = null;
		EXPECTED_CHILD = null;

		Topic actualParent = queryExecutor.topicById(RESPONSE_SPEC, expectedParent.getId());
		LOG_DATES[0] = LOG_DATES_PARENT[0];
		checkTopic(actualParent, expectedParent.getStatus(), expectedParent.getCreated(), expectedParent.getUpdated(),
			expectedParent.getLabel(), expectedParent.getDescription(), getParentId(expectedParent),
			List.of(expectedChild.getId()), CRE);

		Topic actualChild = queryExecutor.topicById(RESPONSE_SPEC, expectedChild.getId());
		LOG_DATES[0] = LOG_DATES_CHILD[0];
		checkTopic(actualChild, expectedChild, CRE);

		EXPECTED_PARENT = actualParent;
		EXPECTED_CHILD = actualChild;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void updateTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Topic expectedParent = EXPECTED_PARENT;
		Topic expectedChild = EXPECTED_CHILD;
		EXPECTED_PARENT = null;
		EXPECTED_CHILD = null;

		TopicInput childInput = TopicInput.builder() //
			.withId(expectedChild.getId()) //
			.withLabel("Updated child label") //
			.withDescription("Updated child description") //
			.withParentId(null) // detach child from parent
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Topic actualChild = mutationExecutor.updateTopic(RESPONSE_SPEC, childInput);

		LOG_DATES[0] = LOG_DATES_CHILD[0];
		LOG_DATES[1] = LOG_DATES_CHILD[1] = actualChild.getUpdated();
		checkTopic(actualChild, expectedChild.getStatus(), expectedChild.getCreated(), earliestUpdated,
			childInput.getLabel(), childInput.getDescription(), childInput.getParentId(), Collections.emptyList(), CRE,
			UPD);

		TopicInput parentInput = TopicInput.builder() //
			.withId(expectedParent.getId()) //
			.withLabel("Updated parent label") //
			.withDescription("Updated parent description") //
			.build();
		earliestUpdated = OffsetDateTime.now();
		Topic actualParent = mutationExecutor.updateTopic(RESPONSE_SPEC, parentInput);

		LOG_DATES[0] = LOG_DATES_PARENT[0];
		LOG_DATES[1] = LOG_DATES_PARENT[1] = actualParent.getUpdated();
		checkTopic(actualParent, expectedParent.getStatus(), expectedParent.getCreated(), earliestUpdated,
			parentInput.getLabel(), parentInput.getDescription(), parentInput.getParentId(), Collections.emptyList(),
			CRE, UPD);

		EXPECTED_PARENT = actualParent;
		EXPECTED_CHILD = actualChild;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void deleteTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Topic expectedParent = EXPECTED_PARENT;
		Topic expectedChild = EXPECTED_CHILD;
		EXPECTED_PARENT = null;
		EXPECTED_CHILD = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Topic actualParent = mutationExecutor.deleteTopic(RESPONSE_SPEC, expectedParent.getId());

		LOG_DATES[0] = LOG_DATES_PARENT[0];
		LOG_DATES[1] = LOG_DATES_PARENT[1];
		LOG_DATES[2] = LOG_DATES_PARENT[2] = actualParent.getUpdated();
		checkTopic(actualParent, StatusKind.DEL.getLabel(), expectedParent.getCreated(), earliestUpdated,
			expectedParent.getLabel(), expectedParent.getDescription(), getParentId(expectedParent),
			getChildIds(expectedParent), CRE, UPD, DEL);

		earliestUpdated = OffsetDateTime.now();
		Topic actualChild = mutationExecutor.deleteTopic(RESPONSE_SPEC, expectedChild.getId());

		LOG_DATES[0] = LOG_DATES_CHILD[0];
		LOG_DATES[1] = LOG_DATES_CHILD[1];
		LOG_DATES[2] = LOG_DATES_CHILD[2] = actualChild.getUpdated();
		checkTopic(actualChild, StatusKind.DEL.getLabel(), expectedChild.getCreated(), earliestUpdated,
			expectedChild.getLabel(), expectedChild.getDescription(), getParentId(expectedChild),
			getChildIds(expectedChild), CRE, UPD, DEL);

		EXPECTED_PARENT = actualParent;
		EXPECTED_CHILD = actualChild;
	}

	private void checkTopic(Topic actual, Topic expected, TransactionKind... txnKinds) {
		checkTopic(actual, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getLabel(),
			expected.getDescription(), getParentId(expected), getChildIds(expected), txnKinds);
	}

	private void checkTopic(Topic actual, String status, OffsetDateTime earliestCreated, OffsetDateTime earliestUpdated,
		String label, String description, Long parentId, List<Long> childIds, TransactionKind... txnKinds) {

		checkTrackedEntity(actual, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(actual).hasLabel(label);
		assertThat(actual).hasDescription(description);
		if (parentId == null) {
			assertThat(actual).parent().isNull();
		} else {
			assertThat(actual).parent().isNotNull();
			assertThat(actual).parent().hasId(parentId);
		}
		if (childIds == null)
			assertThat(actual).children().isEmpty();
		else
			assertThat(actual).childIds().containsExactly(childIds.toArray());
	}

	private Long getParentId(Topic topic) {
		return getEntityId(topic.getParent());
	}

	private List<Long> getChildIds(Topic topic) {
		return getEntityIds(topic.getChildren());
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.TOP;
	}

}
