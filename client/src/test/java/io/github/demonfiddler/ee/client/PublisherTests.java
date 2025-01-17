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
import static io.github.demonfiddler.ee.client.truth.PublisherSubject.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
class PublisherTests extends TrackedEntityTests<Publisher> {

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
			name
			location
			country
			url
			journalCount
		}
		""";
	private static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status%s
			name
			location
			url
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
				name
				location
				url
			}
		}
		""";
	private static Publisher EXPECTED;
	private static List<Publisher> PUBLISHERS;

	static boolean hasExpectedPublisher() {
		return EXPECTED != null;
	}

	static boolean hasExpectedPublishers() {
		return PUBLISHERS != null;
	}	

	@Test
	@Order(1)
	void createPublisher() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		EXPECTED = null;

		PublisherInput input = PublisherInput.builder() //
			.withName("Test name") //
			.withLocation("Test location") //
			.withUrl(URI.create("http://domain.org").toURL()) //
			.withJournalCount(2) //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publisher actual = mutationExecutor.createPublisher(RESPONSE_SPEC, input);

		LOG_DATES[0] = actual.getCreated();
		checkPublisher(actual, StatusKind.DRA.getLabel(), earliestUpdated, null, input.getName(), input.getLocation(),
			input.getUrl(), input.getJournalCount(), CRE);

		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublisher")
	void readPublisher() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Publisher expected = EXPECTED;
		EXPECTED = null;

		Publisher actual = queryExecutor.publisherById(RESPONSE_SPEC, expected.getId());

		checkPublisher(actual, expected, CRE);
		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublisher")
	void updatePublisher() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Publisher expected = EXPECTED;
		EXPECTED = null;

		PublisherInput input = PublisherInput.builder() //
			.withId(expected.getId()) //
			.withName("Updated test name") //
			.withLocation("Updated test location") //
			.withUrl(URI.create("http://updated-domain.org").toURL()) //
			.withJournalCount(3) //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publisher actual = mutationExecutor.updatePublisher(RESPONSE_SPEC, input);

		LOG_DATES[1] = actual.getUpdated();
		checkPublisher(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, input.getName(),
			input.getLocation(), input.getUrl(), input.getJournalCount(), CRE, UPD);

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublisher")
	void deletePublisher() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Publisher expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publisher actual = mutationExecutor.deletePublisher(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkPublisher(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getName(),
			expected.getLocation(), expected.getUrl(), expected.getJournalCount(), CRE, UPD, DEL);

		EXPECTED = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublisher")
	void createPublishers() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Create another eight publishers and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int publisherCount = 8;
		List<Publisher> publishers = new ArrayList<>(publisherCount + 1);
		Publisher publisher0 = new Publisher();
		publisher0.setId(EXPECTED.getId());
		publisher0.setStatus(EXPECTED.getStatus());
		publisher0.setName(EXPECTED.getName());
		publisher0.setLocation(EXPECTED.getLocation());
		publisher0.setUrl(EXPECTED.getUrl());
		publisher0.set__typename(EXPECTED.get__typename());
		publishers.add(publisher0);
		String[] numbers = {null, "one", "two", "three", "four", "five", "six", "seven", "eight"};
		for (int i = 1; i <= publisherCount; i++) {
			String name = "Publisher " + numbers[i];
			String location = "Location #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				name = name.toUpperCase();
			if (i % 3 == 0)
				location = null;
			PublisherInput input = PublisherInput.builder() //
				.withName(name) //
				.withLocation(location) //
				.build();
			publishers.add(mutationExecutor.createPublisher(responseSpec, input));
		}
		PUBLISHERS = publishers;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishers() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PublisherPage actuals = queryExecutor.publishers(responseSpec, null, null);

		checkPage(actuals, PUBLISHERS.size(), 1, PUBLISHERS.size(), 0, false, false, true, true, PUBLISHERS, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		List<Publisher> expected = subList(PUBLISHERS, 5, 7, 8);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.publishers(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(PUBLISHERS, 0);
		actuals = queryExecutor.publishers(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("name") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "PUBLISHER FIVE", "PUBLISHER ONE", "PUBLISHER SEVEN", "PUBLISHER THREE", "Publisher eight", "Publisher four", "Publisher six",
		// "Publisher two", "Updated test name"
		// 5, 1, 7, 3, 8, 4, 6, 2, 0
		List<Publisher> expected = subList(PUBLISHERS, 5, 1, 7, 3, 8, 4, 6, 2, 0);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("name") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Publisher eight", "PUBLISHER FIVE", "Publisher four", "PUBLISHER ONE", "PUBLISHER SEVEN", "Publisher six", "PUBLISHER THREE",
		// "Publisher two", "Updated test name"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Publisher> expected = subList(PUBLISHERS, 8, 5, 4, 1, 7, 6, 3, 2, 0);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		Collections.reverse(expected);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput locationOrder = OrderInput.builder() //
			.withProperty("location") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput nameOrder = OrderInput.builder() //
			.withProperty("name") //
			.build();
		List<OrderInput> orders = List.of(locationOrder, nameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"PUBLISHER THREE", null/"Publisher six", "Location #1"/"PUBLISHER ONE", "Location #2"/"Publisher two",
		// "Location #4"/"Publisher four", "Location #5 (filtered)"/"PUBLISHER FIVE", "Location #7 (filtered)"/"PUBLISHER SEVEN",
		// "Location #8 (filtered)"/"Publisher eight", "Updated test location"/"Updated test name"
		// 3, 6, 1, 2, 4, 5, 7, 8, 0
		List<Publisher> expected = subList(PUBLISHERS, 3, 6, 1, 2, 4, 5, 7, 8, 0);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.DESC);
		expected = subList(PUBLISHERS, 6, 3, 1, 2, 4, 5, 7, 8, 0);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Location #1"/"PUBLISHER ONE", "Location #2"/"Publisher two", "Location #4"/"Publisher four", "Location #5 (filtered)"/"PUBLISHER FIVE",
		// "Location #7 (filtered)"/"PUBLISHER SEVEN", "Location #8 (filtered)"/"Publisher eight",
		// "Updated test location"/"Updated test name", null/"PUBLISHER THREE", null/"Publisher six",
		// 1, 2, 4, 5, 7, 8, 0, 3, 6
		locationOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		nameOrder.setDirection(null);
		expected = subList(PUBLISHERS, 1, 2, 4, 5, 7, 8, 0, 3, 6);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.DESC);
		expected = subList(PUBLISHERS, 1, 2, 4, 5, 7, 8, 0, 6, 3);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("name") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "PUBLISHER FIVE", "PUBLISHER SEVEN", "Publisher eight"
		// 5, 7, 8
		List<Publisher> expected = subList(PUBLISHERS, 5, 7, 8);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("publisher") //
			.build();
		OrderInput locationOrder = OrderInput.builder() //
			.withProperty("location") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput nameOrder = OrderInput.builder() //
			.withProperty("name") //
			.build();
		List<OrderInput> orders = List.of(locationOrder, nameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"PUBLISHER THREE", null/"Publisher six", "Location #1"/"PUBLISHER ONE", "Location #2"/"Publisher two",
		// "Location #4"/"Publisher four", "Location #5 (filtered)"/"PUBLISHER FIVE", "Location #7 (filtered)"/"PUBLISHER SEVEN",
		// "Location #8 (filtered)"/"Publisher eight", "Updated test location"/"Updated test name"
		// 3, 6, 1, 2, 4, 5, 7, 8
		List<Publisher> expected = subList(PUBLISHERS, 3, 6, 1, 2, 4, 5, 7, 8);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.DESC);
		expected = subList(PUBLISHERS, 6, 3, 1, 2, 4, 5, 7, 8);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Location #1"/"PUBLISHER ONE", "Location #2"/"Publisher two", "Location #4"/"Publisher four", "Location #5 (filtered)"/"PUBLISHER FIVE",
		// "Location #7 (filtered)"/"PUBLISHER SEVEN", "Location #8 (filtered)"/"Publisher eight", null/"PUBLISHER THREE", null/"Publisher six"
		// 1, 2, 4, 5, 7, 8, 3, 6
		locationOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		nameOrder.setDirection(DirectionKind.ASC);
		expected = subList(PUBLISHERS, 1, 2, 4, 5, 7, 8, 3, 6);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		nameOrder.setDirection(DirectionKind.DESC);
		expected = subList(PUBLISHERS, 1, 2, 4, 5, 7, 8, 6, 3);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		PublisherPage actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		List<Publisher> expected = PUBLISHERS.subList(0, 4);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		expected = PUBLISHERS.subList(4, 8);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		expected = PUBLISHERS.subList(8, 9);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		List<Publisher> expected = subList(PUBLISHERS, 5, 7);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS,8);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("name") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		// "PUBLISHER FIVE", "PUBLISHER ONE", "PUBLISHER SEVEN", "PUBLISHER THREE", "Publisher eight", "Publisher four", "Publisher six",
		// "Publisher two", "Updated test name"
		// 5, 1, 7, 3, 8, 4, 6, 2, 0
		List<Publisher> expected = subList(PUBLISHERS, 5, 1, 7, 3);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS, 8, 4, 6, 2);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(PUBLISHERS, 0);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(PUBLISHERS, 5, 1, 7, 3);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS, 8, 4, 6, 2);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(PUBLISHERS, 0);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(PUBLISHERS, 0, 2, 6, 4);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS, 8, 3, 7, 1);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(PUBLISHERS, 5);
		actuals = queryExecutor.publishers(responseSpec, null, pageSort);
		checkPage(actuals, PUBLISHERS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.PublisherTests#hasExpectedPublishers")
	void readPublishersPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("name") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		// "PUBLISHER FIVE", "PUBLISHER SEVEN", "Publisher eight"
		// 5, 7, 8
		List<Publisher> expected = subList(PUBLISHERS, 5, 7);
		PublisherPage actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS, 8);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(PUBLISHERS, 5, 7);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS, 8);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(PUBLISHERS, 8, 7);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PUBLISHERS, 5);
		actuals = queryExecutor.publishers(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkPublisher(Publisher actual, Publisher expected, TransactionKind... txnKinds) {
		checkPublisher(actual, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getName(),
			expected.getLocation(), expected.getUrl(), expected.getJournalCount(), txnKinds);
	}

	private void checkPublisher(Publisher actual, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String name, String location, URL url, int journalCount,
		TransactionKind... txnKinds) {

		checkTrackedEntity(actual, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(actual).hasName(name);
		assertThat(actual).hasLocation(location);
		assertThat(actual).hasUrl(url);
		assertThat(actual).hasJournalCount(journalCount);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.PBR;
	}

}
