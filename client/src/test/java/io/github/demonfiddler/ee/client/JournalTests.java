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
import static io.github.demonfiddler.ee.client.truth.JournalSubject.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
class JournalTests extends TrackedEntityTests<Journal> {

	private static final Random RANDOM = new Random();
	private static Journal EXPECTED;
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
			title
			abbreviation
			url
			issn
			publisher
			notes
		}
		""";
	private static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status%s
			title
			notes
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
				title
				notes
			}
		}
		""";
	private static List<Journal> JOURNALS;

	static boolean hasExpectedJournal() {
		return EXPECTED != null;
	}

	static boolean hasExpectedJournals() {
		return JOURNALS != null;
	}

	@Test
	@Order(1)
	void createJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		EXPECTED = null;

		// Create the test fixture.
		JournalInput input = JournalInput.builder() //
			.withTitle("Test title") //
			.withAbbreviation("Tst jour") //
			.withUrl(URI.create("http://domain.org").toURL()) //
			.withIssn(generateRandomIssn()) // "^[0-9]{4}-[0-9]{3}[0-9X]$"
			// .withPublisherId(0L) //
			.withNotes("Test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Journal actual = mutationExecutor.createJournal(RESPONSE_SPEC, input);

		// Check the returned Journal object for correctness.
		LOG_DATES[0] = actual.getCreated();
		checkJournal(actual, StatusKind.DRA.getLabel(), earliestUpdated, null, input.getTitle(),
			input.getAbbreviation(), input.getUrl(), input.getIssn(), null, input.getNotes(), CRE);

		// Test passed, so remember result for following tests.
		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void readJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Journal expected = EXPECTED;
		EXPECTED = null;

		Journal actual = queryExecutor.journalById(RESPONSE_SPEC, expected.getId());

		// Check read journal against the one created by the preceding createJournal() test.
		checkJournal(actual, expected, CRE);
		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void updateJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Journal expected = EXPECTED;
		EXPECTED = null;

		JournalInput input = JournalInput.builder() //
			.withId(expected.getId()) //
			.withTitle("Updated Test Title") //
			.withAbbreviation("Upd Tst Jour") //
			.withUrl(URI.create("http://updated-domain.org").toURL()) //
			.withIssn(generateRandomIssn()) // "^[0-9]{4}-[0-9]{3}[0-9X]$"
			// .withPublisherId(0L) //
			.withNotes("Updated Test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Journal actual = mutationExecutor.updateJournal(RESPONSE_SPEC, input);

		// Check read journal against the one returned by the preceding readJournal() test.
		LOG_DATES[1] = actual.getUpdated();
		checkJournal(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, input.getTitle(),
			input.getAbbreviation(), input.getUrl(), input.getIssn(), null, input.getNotes(), CRE, UPD);

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void deleteJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Journal expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Journal actual = mutationExecutor.deleteJournal(RESPONSE_SPEC, expected.getId());

		// Check read journal against the one updated by the preceding updateJournal() test.
		LOG_DATES[2] = actual.getUpdated();
		checkJournal(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getTitle(),
			expected.getAbbreviation(), expected.getUrl(), expected.getIssn(), getPublisherId(expected),
			expected.getNotes(), CRE, UPD, DEL);

		EXPECTED = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void createJournals() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Create another eight journals and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int journalCount = 8;
		List<Journal> journals = new ArrayList<>(journalCount + 1);
		Journal journal0 = new Journal();
		journal0.setId(EXPECTED.getId());
		journal0.setStatus(EXPECTED.getStatus());
		journal0.setTitle(EXPECTED.getTitle());
		journal0.setNotes(EXPECTED.getNotes());
		journal0.set__typename(EXPECTED.get__typename());
		journals.add(journal0);
		String[] numbers = {null, "one", "two", "three", "four", "five", "six", "seven", "eight"};
		for (int i = 1; i <= journalCount; i++) {
			String title = "Journal " + numbers[i];
			String abbreviation = "Jnl " + numbers[i];
			String notes = "Notes #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				title = title.toUpperCase();
			if (i % 3 == 0)
				notes = null;
			JournalInput input = JournalInput.builder() //
				.withTitle(title) //
				.withAbbreviation(abbreviation) //
				.withNotes(notes) //
				.build();
			journals.add(mutationExecutor.createJournal(responseSpec, input));
		}
		JOURNALS = journals;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournals() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		JournalPage actuals = queryExecutor.journals(responseSpec, null, null);

		checkPage(actuals, JOURNALS.size(), 1, JOURNALS.size(), 0, false, false, true, true, JOURNALS, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		JournalPage actuals = queryExecutor.journals(responseSpec, filter, null);
		List<Journal> expected = subList(JOURNALS, 5, 7, 8);
		checkPage(actuals, expected.size(), 1, 3, 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.journals(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, 3, 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		actuals = queryExecutor.journals(responseSpec, filter, null);
		expected = JOURNALS.subList(0, 1);
		checkPage(actuals, expected.size(), 1, 1, 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "JOURNAL FIVE", "JOURNAL ONE", "JOURNAL SEVEN", "JOURNAL THREE", "Journal eight",
		// "Journal four", "Journal six", "Journal two", "Updated Test journal"
		// 5, 1, 7, 3, 8, 4, 6, 2, 0
		List<Journal> expected = subList(JOURNALS, 5, 1, 7, 3, 8, 4, 6, 2, 0);
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Journal eight", "JOURNAL FIVE", "Journal four", "JOURNAL ONE", "JOURNAL SEVEN",
		// "Journal six", "JOURNAL THREE", "Journal two", "Updated Test journal"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Journal> expected = subList(JOURNALS, 8, 5, 4, 1, 7, 6, 3, 2, 0);
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput textOrder = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, textOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"JOURNAL THREE", null/"Journal six", "Notes #1"/"JOURNAL ONE", "Notes #2"/"Journal two",
		// "Notes #4"/"Journal four", "Notes #5 (filtered)"/"JOURNAL FIVE",
		// "Notes #7 (filtered)"/"JOURNAL SEVEN", "Notes #8 (filtered)"/"Journal eight",
		// "Updated Test notes"/"Updated Test journal"
		// 3, 6, 1, 2, 4, 5, 7, 8, 0
		List<Journal> expected = subList(JOURNALS, 3, 6, 1, 2, 4, 5, 7, 8, 0);
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		expected = subList(JOURNALS, 6, 3, 1, 2, 4, 5, 7, 8, 0);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"JOURNAL ONE", "Notes #2"/"Journal two", "Notes #4"/"Journal four",
		// "Notes #5 (filtered)"/"JOURNAL FIVE", "Notes #7 (filtered)"/"JOURNAL SEVEN",
		// "Notes #8 (filtered)"/"Journal eight", "Updated Test notes"/"Updated Test journal",
		// null/"JOURNAL THREE", null/"Journal six",
		// 1, 2, 4, 5, 7, 8, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(null);
		expected = subList(JOURNALS, 1, 2, 4, 5, 7, 8, 0, 3, 6);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		expected = subList(JOURNALS, 1, 2, 4, 5, 7, 8, 0, 6, 3);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
			OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "JOURNAL FIVE", "JOURNAL SEVEN", "Journal eight"
		// 5, 7, 8
		List<Journal> expected = subList(JOURNALS, 5, 7, 8);
		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		Collections.reverse(expected);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("journal") //
			.build();
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput textOrder = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, textOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"JOURNAL THREE", null/"Journal six", "Notes #1"/"JOURNAL ONE",
		// "Notes #2"/"Journal two", "Notes #4"/"Journal four", "Notes #5 (filtered)"/"JOURNAL FIVE",
		// "Notes #7 (filtered)"/"JOURNAL SEVEN", "Notes #8 (filtered)"/"Journal eight"
		// 3, 6, 1, 2, 4, 5, 7, 8
		List<Journal> expected = subList(JOURNALS, 3, 6, 1, 2, 4, 5, 7, 8);
		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		expected = subList(JOURNALS, 6, 3, 1, 2, 4, 5, 7, 8);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"JOURNAL ONE", "Notes #2"/"Journal two", "Notes #4"/"Journal four",
		// "Notes #5 (filtered)"/"JOURNAL FIVE", "Notes #7 (filtered)"/"JOURNAL SEVEN",
		// "Notes #8 (filtered)"/"Journal eight", null/"JOURNAL THREE", null/"Journal six",
		// 1, 2, 4, 5, 7, 8, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(DirectionKind.ASC);
		expected = subList(JOURNALS, 1, 2, 4, 5, 7, 8, 3, 6);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		expected = subList(JOURNALS, 1, 2, 4, 5, 7, 8, 6, 3);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		List<Journal> expected = JOURNALS.subList(0, 4);
		checkPage(actuals, JOURNALS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		expected = JOURNALS.subList(4, 8);
		checkPage(actuals, JOURNALS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		expected = JOURNALS.subList(8, 9);
		checkPage(actuals, JOURNALS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		List<Journal> expected = subList(JOURNALS, 5, 7);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		expected = subList(JOURNALS,8);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		// "JOURNAL FIVE", "JOURNAL ONE", "JOURNAL SEVEN", "JOURNAL THREE", "Journal eight",
		// "Journal four", "Journal six", "Journal two", "Updated Test journal"
		// 5, 1, 7, 3, 8, 4, 6, 2, 0
		List<Journal> expected = subList(JOURNALS, 5, 1, 7, 3);
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(JOURNALS, 8, 4, 6, 2);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(JOURNALS, 0);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(JOURNALS, 5, 1, 7, 3);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(JOURNALS, 8, 4, 6, 2);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(JOURNALS, 0);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(JOURNALS, 0, 2, 6, 4);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(JOURNALS, 8, 3, 7, 1);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(JOURNALS, 5);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		// "JOURNAL FIVE", "JOURNAL SEVEN", "Journal eight"
		// 5, 7, 8
		List<Journal> expected = subList(JOURNALS, 5, 7);
		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(JOURNALS, 8);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(JOURNALS, 5, 7);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(JOURNALS, 8);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(JOURNALS, 8, 7);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(JOURNALS, 5);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkJournal(Journal actual, Journal expected, TransactionKind... txnKinds) {
		checkJournal(actual, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getTitle(),
			expected.getAbbreviation(), expected.getUrl(), expected.getIssn(), getPublisherId(expected),
			expected.getNotes(), txnKinds);
	}

	private void checkJournal(Journal actual, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String title, String abbreviation, URL url, String issn, Long publisherId,
		String notes, TransactionKind... txnKinds) {

		checkTrackedEntity(actual, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(actual).hasTitle(title);
		assertThat(actual).hasAbbreviation(abbreviation);
		assertThat(actual).hasUrl(url);
		assertThat(actual).hasIssn(issn);
		if (publisherId == null) {
			assertThat(actual).publisher().isNull();
		} else {
			assertThat(actual).publisher().isNotNull();
			assertThat(actual).publisher().hasId(publisherId);
		}
		assertThat(actual).hasNotes(notes);
	}

	private Long getPublisherId(Journal journal) {
		Publisher publisher = journal.getPublisher();
		return publisher == null ? null : publisher.getId();
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.JOU;
	}

	private String generateRandomIssn() {
		StringBuilder issn = new StringBuilder(9);
		issn.append(RANDOM.nextInt(100000000));
		for (int i = 0, n = 8 - issn.length(); i < n; i++)
			issn.insert(0, '0');
		issn.insert(4, '-');
		return issn.toString();
	}

}
