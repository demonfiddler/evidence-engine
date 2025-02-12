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
import static io.github.demonfiddler.ee.client.truth.QuotationSubject.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
@Order(7)
@TestMethodOrder(OrderAnnotation.class)
class QuotationTests extends AbstractTopicalEntityTests<Quotation> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuotationTests.class);

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
			quotee
			text
			date
			source
			url
			notes
		}
		""";
	private static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status%s
			date
			text
			notes
		}
		""";
	static final String PAGED_RESPONSE_SPEC = //
		"""
		{
			number
			size
			numberOfElements
			totalPages
			totalElements
			isFirst
			isLast
			hasNext
			hasPrevious
			isEmpty
			hasContent
			content {
				id
				status%s
				date
				text
				notes
			}
		}
		""";

	static Quotation quotation;
	static List<Quotation> quotations;

	static boolean hasExpectedQuotation() {
		return quotation != null;
	}

	static boolean hasExpectedQuotations() {
		return quotations != null;
	}	

	static void ensureExpectedQuotations() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		if (quotations == null) {
			QueryExecutor queryExecutor = SpringContext.getApplicationContext().getBean(QueryExecutor.class);
			String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
			List<Quotation> content = queryExecutor.quotations(responseSpec, null, null).getContent();
			if (content.isEmpty()) {
				LOGGER.error("Failed to initialise quotations list from server");
			} else {
				quotations = content;
				LOGGER.debug("Initialised quotations list from server");
			}
		}
	}

	@Test
	@Order(1)
	void createQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		quotation = null;

		QuotationInput input = QuotationInput.builder() //
			.withQuotee("Fred Bloggs") //
			.withText("Test text") //
			.withDate(LocalDate.now()) //
			.withSource("Test source") //
			.withUrl(URI.create("http://domain.tld").toURL()) //
			.withNotes("Test notes") //
			.build();
		OffsetDateTime earliestCreated = OffsetDateTime.now();
		Quotation actual = mutationExecutor.createQuotation(RESPONSE_SPEC, input);

		LOG_DATES[0] = actual.getCreated();
		checkQuotation(actual, StatusKind.DRA.label(), earliestCreated, null, input.getQuotee(), input.getText(),
			input.getDate(), input.getSource(), input.getUrl(), input.getNotes(), CRE);

		quotation = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void readQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Quotation expected = quotation;
		quotation = null;

		Quotation actual = queryExecutor.quotationById(RESPONSE_SPEC, expected.getId());

		checkQuotation(actual, expected, CRE);

		quotation = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void updateQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Quotation expected = quotation;
		quotation = null;

		QuotationInput input = QuotationInput.builder() //
			.withId(expected.getId()) //
			.withQuotee("John Doe") //
			.withText("Updated test text") //
			.withDate(LocalDate.now()) //
			.withSource("Updated test source") //
			.withUrl(URI.create("http://updated-domain.tld").toURL()) //
			.withNotes("Updated test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Quotation actual = mutationExecutor.updateQuotation(RESPONSE_SPEC, input);

		// Check read quotation against the one created by the preceding createQuotation() test.
		LOG_DATES[1] = actual.getUpdated();
		checkQuotation(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, input.getQuotee(),
			input.getText(), input.getDate(), input.getSource(), input.getUrl(), input.getNotes(), CRE, UPD);

		quotation = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void deleteQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Quotation expected = quotation;
		quotation = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Quotation actual = mutationExecutor.deleteQuotation(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkQuotation(actual, StatusKind.DEL.label(), expected.getCreated(), earliestUpdated, expected.getQuotee(),
			expected.getText(), expected.getDate(), expected.getSource(), expected.getUrl(), expected.getNotes(), CRE,
			UPD, DEL);

		quotation = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void createQuotations() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Create another eight quotations and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int quotationCount = 8;
		List<Quotation> quotations = new ArrayList<>(quotationCount + 1);
		Quotation quotation0 = new Quotation();
		quotation0.setId(quotation.getId());
		quotation0.setStatus(quotation.getStatus());
		quotation0.setDate(quotation.getDate());
		quotation0.setText(quotation.getText());
		quotation0.setNotes(quotation.getNotes());
		quotation0.set__typename(quotation.get__typename());
		quotations.add(quotation0);
		String[] numbers = {null, "one", "two", "three", "four", "five", "six", "seven", "eight"};
		for (int i = 1; i <= quotationCount; i++) {
			LocalDate date = LocalDate.now();
			String quotee = "Quotee " + numbers[i];
			String text = "Quotation " + numbers[i];
			String notes = "Notes #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				text = text.toUpperCase();
			if (i % 3 == 0)
				notes = null;
			QuotationInput input = QuotationInput.builder() //
				.withQuotee(quotee) //
				.withDate(date) //
				.withText(text) //
				.withNotes(notes) //
				.build();
			quotations.add(mutationExecutor.createQuotation(responseSpec, input));
		}

		QuotationTests.quotations = quotations;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotations() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		QuotationPage actuals = queryExecutor.quotations(responseSpec, null, null);

		checkPage(actuals, quotations.size(), 1, quotations.size(), 0, false, false, true, true, quotations, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		List<Quotation> expected = subList(quotations, 5, 7, 8);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.quotations(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(quotations, 0);
		actuals = queryExecutor.quotations(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "QUOTATION FIVE", "QUOTATION ONE", "QUOTATION SEVEN", "QUOTATION THREE", "Quotation eight", "Quotation four", "Quotation six",
		// "Quotation two", "Updated test title"
		// 5, 1, 7, 3, 8, 4, 6, 2, 0
		List<Quotation> expected = subList(quotations, 5, 1, 7, 3, 8, 4, 6, 2, 0);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Quotation eight", "QUOTATION FIVE", "Quotation four", "QUOTATION ONE", "QUOTATION SEVEN", "Quotation six", "QUOTATION THREE",
		// "Quotation two", "Updated test title"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Quotation> expected = subList(quotations, 8, 5, 4, 1, 7, 6, 3, 2, 0);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput textOrder = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, textOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"QUOTATION THREE", null/"Quotation six", "Notes #1"/"QUOTATION ONE", "Notes #2"/"Quotation two",
		// "Notes #4"/"Quotation four", "Notes #5 (filtered)"/"QUOTATION FIVE", "Notes #7 (filtered)"/"QUOTATION SEVEN",
		// "Notes #8 (filtered)"/"Quotation eight", "Updated test notes"/"Updated test title"
		// 3, 6, 1, 2, 4, 5, 7, 8, 0
		List<Quotation> expected = subList(quotations, 3, 6, 1, 2, 4, 5, 7, 8, 0);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		expected = subList(quotations, 6, 3, 1, 2, 4, 5, 7, 8, 0);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"QUOTATION ONE", "Notes #2"/"Quotation two", "Notes #4"/"Quotation four", "Notes #5 (filtered)"/"QUOTATION FIVE",
		// "Notes #7 (filtered)"/"QUOTATION SEVEN", "Notes #8 (filtered)"/"Quotation eight",
		// "Updated test notes"/"Updated test title", null/"QUOTATION THREE", null/"Quotation six",
		// 1, 2, 4, 5, 7, 8, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(null);
		expected = subList(quotations, 1, 2, 4, 5, 7, 8, 0, 3, 6);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		expected = subList(quotations, 1, 2, 4, 5, 7, 8, 0, 6, 3);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "QUOTATION FIVE", "QUOTATION SEVEN", "Quotation eight", "Quotation six"
		// 5, 7, 8, 6
		List<Quotation> expected = subList(quotations, 5, 7, 8);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("quotation") //
			.build();
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput textOrder = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, textOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// null/"QUOTATION THREE", null/"Quotation six", "Notes #1"/"QUOTATION ONE", "Notes #2"/"Quotation two",
		// "Notes #4"/"Quotation four", "Notes #5 (filtered)"/"QUOTATION FIVE", "Notes #7 (filtered)"/"QUOTATION SEVEN",
		// "Notes #8 (filtered)"/"Quotation eight", "Updated test notes"/"Updated test title"
		// 3, 6, 1, 2, 4, 5, 7, 8
		List<Quotation> expected = subList(quotations, 3, 6, 1, 2, 4, 5, 7, 8);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		expected = subList(quotations, 6, 3, 1, 2, 4, 5, 7, 8);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"QUOTATION ONE", "Notes #2"/"Quotation two", "Notes #4"/"Quotation four", "Notes #5 (filtered)"/"QUOTATION FIVE",
		// "Notes #7 (filtered)"/"QUOTATION SEVEN", "Notes #8 (filtered)"/"Quotation eight", null/"QUOTATION THREE", null/"Quotation six"
		// 1, 2, 4, 5, 7, 8, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(DirectionKind.ASC);
		expected = subList(quotations, 1, 2, 4, 5, 7, 8, 3, 6);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		expected = subList(quotations, 1, 2, 4, 5, 7, 8, 6, 3);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		List<Quotation> expected = quotations.subList(0, 4);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		expected = quotations.subList(4, 8);
		checkPage(actuals, quotations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		expected = quotations.subList(8, 9);
		checkPage(actuals, quotations.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		List<Quotation> expected = subList(quotations, 5, 7);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations,8);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		// "QUOTATION FIVE", "QUOTATION ONE", "QUOTATION SEVEN", "QUOTATION THREE", "Quotation eight", "Quotation four", "Quotation six",
		// "Quotation two", "Updated test title"
		// 5, 1, 7, 3, 8, 4, 6, 2, 0
		List<Quotation> expected = subList(quotations, 5, 1, 7, 3);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations, 8, 4, 6, 2);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(quotations, 0);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(quotations, 5, 1, 7, 3);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations, 8, 4, 6, 2);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(quotations, 0);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(quotations, 0, 2, 6, 4);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations, 8, 3, 7, 1);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(quotations, 5);
		actuals = queryExecutor.quotations(responseSpec, null, pageSort);
		checkPage(actuals, quotations.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotations")
	void readQuotationsPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		// "QUOTATION FIVE", "QUOTATION SEVEN", "Quotation eight"
		// 5, 7, 8
		List<Quotation> expected = subList(quotations, 5, 7);
		QuotationPage actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations, 8);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(quotations, 5, 7);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations, 8);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(quotations, 8, 7);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(quotations, 5);
		actuals = queryExecutor.quotations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkQuotation(Quotation quotation, Quotation expected, TransactionKind... txnKinds) {
		checkQuotation(quotation, expected.getStatus(), expected.getCreated(), expected.getUpdated(),
			expected.getQuotee(), expected.getText(), expected.getDate(), expected.getSource(), expected.getUrl(),
			expected.getNotes(), txnKinds);
	}

	private void checkQuotation(Quotation quotation, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String quotee, String text, LocalDate date, String source, URL url,
		String notes, TransactionKind... txnKinds) {

		checkTopicalEntity(quotation, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(quotation).hasQuotee(quotee);
		assertThat(quotation).hasText(text);
		assertThat(quotation).hasDate(date);
		assertThat(quotation).hasSource(source);
		assertThat(quotation).hasUrl(url);
		assertThat(quotation).hasNotes(notes);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.QUO;
	}

}
