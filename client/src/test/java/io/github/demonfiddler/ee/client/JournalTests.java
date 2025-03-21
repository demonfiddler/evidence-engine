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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import io.github.demonfiddler.ee.client.util.QueryExecutor;
import io.github.demonfiddler.ee.client.util.SpringContext;

@SpringBootTest(classes = GraphQLClientMain.class)
@Order(3)
@TestMethodOrder(OrderAnnotation.class)
class JournalTests extends AbstractTrackedEntityTests<Journal> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JournalTests.class);

	private static final Random RANDOM = new Random();
	private static final String RESPONSE_SPEC = //
		"""
			{
				id
				status
				created
				createdByUser {
					id
					username
					firstName
					lastName
				}
				updated
				updatedByUser {
					id
					username
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
							username
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

	static Journal journal;
	static List<Journal> journals;

	static boolean hasExpectedJournal() {
		return journal != null;
	}

	static boolean hasExpectedJournals() {
		return journals != null;
	}

	static void ensureExpectedJournals() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		if (journals == null) {
			QueryExecutor queryExecutor = SpringContext.getApplicationContext().getBean(QueryExecutor.class);
			String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
			List<Journal> content = queryExecutor.journals(responseSpec, null, null).getContent();
			if (content.isEmpty()) {
				LOGGER.error("Failed to initialise journals list from server");
			} else {
				journals = content;
				LOGGER.debug("Initialised journals list from server");
			}
		}
	}

	@Test
	@Order(1)
	void createJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		journal = null;

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
		checkJournal(actual, StatusKind.DRA.label(), earliestUpdated, null, input.getTitle(), input.getAbbreviation(),
			input.getUrl(), input.getIssn(), null, input.getNotes(), CRE);

		// Test passed, so remember result for following tests.
		journal = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void readJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Journal expected = journal;
		journal = null;

		Journal actual = queryExecutor.journalById(RESPONSE_SPEC, expected.getId());

		// Check read journal against the one created by the preceding createJournal() test.
		checkJournal(actual, expected, CRE);
		journal = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void updateJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Journal expected = journal;
		journal = null;

		JournalInput input = JournalInput.builder() //
			.withId(expected.getId()) //
			.withTitle("Updated test Title") //
			.withAbbreviation("Upd Tst Jour") //
			.withUrl(URI.create("http://updated-domain.org").toURL()) //
			.withIssn(generateRandomIssn()) // "^[0-9]{4}-[0-9]{3}[0-9X]$"
			// .withPublisherId(0L) //
			.withNotes("Updated test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Journal actual = mutationExecutor.updateJournal(RESPONSE_SPEC, input);

		// Check read journal against the one returned by the preceding readJournal() test.
		LOG_DATES[1] = actual.getUpdated();
		checkJournal(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, input.getTitle(),
			input.getAbbreviation(), input.getUrl(), input.getIssn(), null, input.getNotes(), CRE, UPD);

		journal = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void deleteJournal() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Journal expected = journal;
		journal = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Journal actual = mutationExecutor.deleteJournal(RESPONSE_SPEC, expected.getId());

		// Check read journal against the one updated by the preceding updateJournal() test.
		LOG_DATES[2] = actual.getUpdated();
		checkJournal(actual, StatusKind.DEL.label(), expected.getCreated(), earliestUpdated, expected.getTitle(),
			expected.getAbbreviation(), expected.getUrl(), expected.getIssn(), getPublisherId(expected),
			expected.getNotes(), CRE, UPD, DEL);

		journal = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournal")
	void createJournals() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Create another eight journals and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int journalCount = 8;
		List<Journal> journals = new ArrayList<>(journalCount + 1);
		Journal journal0 = new Journal();
		journal0.setId(journal.getId());
		journal0.setStatus(journal.getStatus());
		journal0.setTitle(journal.getTitle());
		journal0.setNotes(journal.getNotes());
		journal0.set__typename(journal.get__typename());
		journals.add(journal0);
		String[] numbers = { null, "one", "two", "three", "four", "five", "six", "seven", "eight" };
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
		JournalTests.journals = journals;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournals() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		JournalPage actuals = queryExecutor.journals(responseSpec, null, null);

		checkPage(actuals, journals.size(), 1, journals.size(), 0, false, false, true, true, journals, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsFiltered() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		JournalPage actuals = queryExecutor.journals(responseSpec, filter, null);
		List<Journal> expected = subList(journals, 5, 7, 8);
		checkPage(actuals, expected.size(), 1, 3, 0, false, false, true, true, expected, false);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.journals(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, 3, 0, false, false, true, true, expected, false);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		actuals = queryExecutor.journals(responseSpec, filter, null);
		expected = journals.subList(0, 1);
		checkPage(actuals, expected.size(), 1, 1, 0, false, false, true, true, expected, false);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "JOURNAL FIVE", "JOURNAL ONE", "JOURNAL SEVEN", "JOURNAL THREE", "Journal eight",
		// "Journal four", "Journal six", "Journal two", "Updated test journal"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1, 7, 6, 3, 2, 0 } //
			: new int[] { 5, 1, 7, 3, 8, 4, 6, 2, 0 };
		List<Journal> expected = subList(journals, indexes);
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
	void readJournalsSortedIgnoreCase() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Journal eight", "JOURNAL FIVE", "Journal four", "JOURNAL ONE", "JOURNAL SEVEN",
		// "Journal six", "JOURNAL THREE", "Journal two", "Updated test journal"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Journal> expected = subList(journals, 8, 5, 4, 1, 7, 6, 3, 2, 0);
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
	void readJournalsSortedNullOrdered() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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
		// "Updated test notes"/"Updated test journal"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8, 0
		// CS: 3, 6, 1, 2, 4, 5, 7, 8, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 };
		List<Journal> expected = subList(journals, indexes);
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"JOURNAL ONE", "Notes #2"/"Journal two", "Notes #4"/"Journal four",
		// "Notes #5 (filtered)"/"JOURNAL FIVE", "Notes #7 (filtered)"/"JOURNAL SEVEN",
		// "Notes #8 (filtered)"/"Journal eight", "Updated test notes"/"Updated test journal",
		// null/"JOURNAL THREE", null/"Journal six",
		// CI: 1, 2, 4, 5, 7, 8, 0, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(null);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsFilteredSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 7 } //
			: new int[] { 5, 7, 8 };
		List<Journal> expected = subList(journals, indexes);
		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsFilteredSortedNullHandling()
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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
		// CI: 6, 3, 1, 2, 4, 5, 7, 8
		// CS: 3, 6, 1, 2, 4, 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8 };
		List<Journal> expected = subList(journals, indexes);
		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"JOURNAL ONE", "Notes #2"/"Journal two", "Notes #4"/"Journal four",
		// "Notes #5 (filtered)"/"JOURNAL FIVE", "Notes #7 (filtered)"/"JOURNAL SEVEN",
		// "Notes #8 (filtered)"/"Journal eight", null/"JOURNAL THREE", null/"Journal six",
		// CI: 1, 2, 4, 5, 7, 8, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(DirectionKind.ASC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 3, 6 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 6, 3 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPaged() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		List<Journal> expected = journals.subList(0, 4);
		checkPage(actuals, journals.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		expected = journals.subList(4, 8);
		checkPage(actuals, journals.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		expected = journals.subList(8, 9);
		checkPage(actuals, journals.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPagedFiltered() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TrackedEntityQueryFilter filter = TrackedEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		List<Journal> expected = subList(journals, 5, 7);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		expected = subList(journals, 8);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPagedSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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
		// "Journal four", "Journal six", "Journal two", "Updated test journal"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		List<Journal> expected = subList(journals, indexes);
		JournalPage actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0, 2, 3, 6 } //
			: new int[] { 0, 2, 6, 4 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 1, 4, 5 } //
			: new int[] { 8, 3, 7, 1 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.JournalTests#hasExpectedJournals")
	void readJournalsPagedFilteredSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		List<Journal> expected = subList(journals, indexes);
		JournalPage actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 5 } //
			: new int[] { 8, 7 };
		expected = subList(journals, indexes);
		actuals = queryExecutor.journals(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(journals, indexes);
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
