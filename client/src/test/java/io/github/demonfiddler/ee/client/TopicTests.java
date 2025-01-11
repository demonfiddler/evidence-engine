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
import java.util.ArrayList;
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
	private static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status%s
			label
			description
		}
		""";
	private static final String PAGED_RESPONSE_SPEC = //
		"""
		{
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
				id
				status%s
				label
				description
			}
		}
		""";
	private static List<Topic> TOPICS;

	static boolean hasExpectedTopic() {
		return EXPECTED_PARENT != null && EXPECTED_CHILD != null;
	}
	
	static boolean hasExpectedTopics() {
		return TOPICS != null;
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
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopic")
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
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopic")
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
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopic")
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

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopic")
	void createTopics() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Create another eight topics and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int topicCount = 8;
		List<Topic> topics = new ArrayList<>(topicCount + 1);
		Topic topic0 = new Topic();
		topic0.setId(EXPECTED_PARENT.getId());
		topic0.setStatus(EXPECTED_PARENT.getStatus());
		topic0.setLabel(EXPECTED_PARENT.getLabel());
		topic0.setDescription(EXPECTED_PARENT.getDescription());
		topic0.set__typename(EXPECTED_PARENT.get__typename());
		Topic topic1 = new Topic();
		topic1.setId(EXPECTED_CHILD.getId());
		topic1.setStatus(EXPECTED_CHILD.getStatus());
		topic1.setLabel(EXPECTED_CHILD.getLabel());
		topic1.setDescription(EXPECTED_CHILD.getDescription());
		topic1.set__typename(EXPECTED_CHILD.get__typename());
		topics.add(topic0);
		topics.add(topic1);
		String[] numbers = {null, "one", "two", "three", "four", "five", "six", "seven", "eight"};
		for (int i = 1; i <= topicCount; i++) {
			String label = "Topic " + numbers[i];
			String description = "Description #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				label = label.toUpperCase();
			if (i % 3 == 0)
				description = null;
			TopicInput input = TopicInput.builder() //
				.withLabel(label) //
				.withDescription(description) //
				.build();
			topics.add(mutationExecutor.createTopic(responseSpec, input));
		}
		TOPICS = topics;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopics() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicPage actuals = queryExecutor.topics(responseSpec, null, null);

		checkPage(actuals, TOPICS.size(), 1, TOPICS.size(), 0, false, false, true, true, TOPICS, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicQueryFilter filter = TopicQueryFilter.builder() //
			.withText("filtered") //
			.build();

		List<Topic> expected = subList(TOPICS, 6, 8, 9);
		TopicPage actuals = queryExecutor.topics(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.topics(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(TOPICS, 0, 1);
		actuals = queryExecutor.topics(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("label") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "TOPIC FIVE", "TOPIC ONE", "TOPIC SEVEN", "TOPIC THREE", "Topic eight", "Topic four", "Topic six",
		// "Topic two", "Updated child label", "Updated parent label"
		// 6, 2, 8, 4, 9, 5, 7, 3, 1, 0
		List<Topic> expected = subList(TOPICS, 6, 2, 8, 4, 9, 5, 7, 3, 1, 0);
		TopicPage actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("label") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Topic eight", "TOPIC FIVE", "Topic four", "TOPIC ONE", "TOPIC SEVEN", "Topic six", "TOPIC THREE",
		// "Topic two", "Updated child label", "Updated parent label"
		// 9, 6, 5, 2, 8, 7, 4, 3, 1, 0
		List<Topic> expected = subList(TOPICS, 9, 6, 5, 2, 8, 7, 4, 3, 1, 0);
		TopicPage actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput descriptionOrder = OrderInput.builder() //
			.withProperty("description") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput labelOrder = OrderInput.builder() //
			.withProperty("label") //
			.build();
		List<OrderInput> orders = List.of(descriptionOrder, labelOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"TOPIC THREE", null/"Topic six", "Notes #1"/"TOPIC ONE", "Notes #2"/"Topic two",
		// "Notes #4"/"Topic four", "Notes #5 (filtered)"/"TOPIC FIVE", "Notes #7 (filtered)"/"TOPIC SEVEN",
		// "Notes #8 (filtered)"/"Topic eight", "Updated child description"/"Updated child label",
		// "Updated parent description"/"Updated parent label"
		// 4, 7, 2, 3, 5, 6, 8, 9, 1, 0
		List<Topic> expected = subList(TOPICS, 4, 7, 2, 3, 5, 6, 8, 9, 1, 0);
		TopicPage actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.DESC);
		expected = subList(TOPICS, 7, 4, 2, 3, 5, 6, 8, 9, 1, 0);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"TOPIC ONE", "Notes #2"/"Topic two", "Notes #4"/"Topic four", "Notes #5 (filtered)"/"TOPIC FIVE",
		// "Notes #7 (filtered)"/"TOPIC SEVEN", "Notes #8 (filtered)"/"Topic eight",
		// "Updated child description"/"Updated child label", "Updated parent description"/"Updated parent label",
		// null/"TOPIC THREE", null/"Topic six",
		// 2, 3, 5, 6, 8, 9, 1, 0, 4, 7
		descriptionOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		labelOrder.setDirection(null);
		expected = subList(TOPICS, 2, 3, 5, 6, 8, 9, 1, 0, 4, 7);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.DESC);
		expected = subList(TOPICS, 2, 3, 5, 6, 8, 9, 1, 0, 7, 4);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicQueryFilter filter = TopicQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("label") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "TOPIC FIVE", "TOPIC SEVEN", "Topic eight"
		// 6, 8, 9
		List<Topic> expected = subList(TOPICS, 6, 8, 9);
		TopicPage actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 1, 3, 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 1, 3, 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 1, 3, 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicQueryFilter filter = TopicQueryFilter.builder() //
			.withText("topic") //
			.build();
		OrderInput descriptionOrder = OrderInput.builder() //
			.withProperty("description") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput labelOrder = OrderInput.builder() //
			.withProperty("label") //
			.build();
		List<OrderInput> orders = List.of(descriptionOrder, labelOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"TOPIC THREE", null/"Topic six", "Notes #1"/"TOPIC ONE", "Notes #2"/"Topic two",
		// "Notes #4"/"Topic four", "Notes #5 (filtered)"/"TOPIC FIVE", "Notes #7 (filtered)"/"TOPIC SEVEN",
		// "Notes #8 (filtered)"/"Topic eight"
		// 4, 7, 2, 3, 5, 6, 8, 9
		List<Topic> expected = subList(TOPICS, 4, 7, 2, 3, 5, 6, 8, 9);
		TopicPage actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.DESC);
		expected = subList(TOPICS, 7, 4, 2, 3, 5, 6, 8, 9);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"TOPIC ONE", "Notes #2"/"Topic two", "Notes #4"/"Topic four", "Notes #5 (filtered)"/"TOPIC FIVE",
		// "Notes #7 (filtered)"/"TOPIC SEVEN", "Notes #8 (filtered)"/"Topic eight", null/"TOPIC THREE", null/"Topic six"
		// 2, 3, 5, 6, 8, 9, 4, 7
		descriptionOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		labelOrder.setDirection(DirectionKind.ASC);
		expected = subList(TOPICS, 2, 3, 5, 6, 8, 9, 4, 7);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		labelOrder.setDirection(DirectionKind.DESC);
		expected = subList(TOPICS, 2, 3, 5, 6, 8, 9, 7, 4);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		List<Topic> expected = TOPICS.subList(0, 4);
		TopicPage actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = TOPICS.subList(4, 8);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = TOPICS.subList(8, 10);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicQueryFilter filter = TopicQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		List<Topic> expected = subList(TOPICS, 6, 8);
		TopicPage actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS,9);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("label") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		// "TOPIC FIVE", "TOPIC ONE", "TOPIC SEVEN", "TOPIC THREE", "Topic eight", "Topic four", "Topic six",
		// "Topic two", "Updated child label", "Updated parent label"
		// 6, 2, 8, 4, 9, 5, 7, 3, 1, 0
		List<Topic> expected = subList(TOPICS, 6, 2, 8, 4);
		TopicPage actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS, 9, 5, 7, 3);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(TOPICS, 1, 0);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(TOPICS, 6, 2, 8, 4);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS, 9, 5, 7, 3);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(TOPICS, 1, 0);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(TOPICS, 0, 1, 3, 7);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS, 5, 9, 4, 8);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(TOPICS, 2, 6);
		actuals = queryExecutor.topics(responseSpec, null, pageSort);
		checkPage(actuals, TOPICS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.TopicTests#hasExpectedTopics")
	void readTopicsPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicQueryFilter filter = TopicQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("label") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		// "TOPIC FIVE", "TOPIC SEVEN", "Topic eight"
		// 6, 8, 9
		List<Topic> expected = subList(TOPICS, 6, 8);
		TopicPage actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS, 9);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(TOPICS, 6, 8);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS, 9);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(TOPICS, 9, 8);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(TOPICS, 6);
		actuals = queryExecutor.topics(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
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
