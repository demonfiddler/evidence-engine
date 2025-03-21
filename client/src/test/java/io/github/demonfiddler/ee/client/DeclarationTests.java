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
import static io.github.demonfiddler.ee.client.truth.DeclarationSubject.assertThat;

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
import io.github.demonfiddler.ee.common.util.StringUtils;

@SpringBootTest(classes = GraphQLClientMain.class)
@Order(2)
@TestMethodOrder(OrderAnnotation.class)
class DeclarationTests extends AbstractLinkableEntityTests<Declaration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationTests.class);

	static final String RESPONSE_SPEC = //
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
			kind
			title
			date
			country
			url
			cached
			signatories
			signatoryCount
			notes
		}
		""";
	static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status
			date
			kind
			title
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
				kind
				title
				notes
			}
		}
		""";

	static Declaration declaration;
	static List<Declaration> declarations;

	static boolean hasExpectedDeclaration() {
		return declaration != null;
	}

	static boolean hasExpectedDeclarations() {
		return declarations != null;
	}

	static void ensureExpectedDeclarations() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		if (declarations == null) {
			QueryExecutor queryExecutor = SpringContext.getApplicationContext().getBean(QueryExecutor.class);
			String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
			List<Declaration> content = queryExecutor.declarations(responseSpec, null, null).getContent();
			if (content.isEmpty()) {
				LOGGER.error("Failed to initialise declations list from server");
			} else {
				declarations = content;
				declaration = declarations.get(0);
				LOGGER.debug("Initialised declarations list from server");
			}
		}
	}

	@Test
	@Order(1)
	void createDeclaration() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		declaration = null;

		LocalDate declarationDate = LocalDate.now();
		DeclarationInput input = DeclarationInput.builder() //
			.withKind(DeclarationKind.DECL) //
			.withTitle("Test title") //
			.withDate(declarationDate) //
			.withCountry("GB") //
			.withUrl(URI.create("http://domain.tld").toURL()) //
			.withSignatories("Adrian Price\nDemon Fiddler") //
			.withNotes("Test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Declaration actual = mutationExecutor.createDeclaration(RESPONSE_SPEC, input);

		LOG_DATES[0] = actual.getCreated();
		checkDeclaration(actual, StatusKind.DRA.label(), earliestUpdated, null, input.getKind().label(),
			input.getTitle(), declarationDate, "United Kingdom", input.getUrl(), input.getSignatories(),
			input.getNotes(), CRE);

		declaration = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void readDeclaration() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Declaration expected = declaration;
		declaration = null;

		Declaration actual = queryExecutor.declarationById(RESPONSE_SPEC, expected.getId());

		checkDeclaration(actual, expected, CRE);

		declaration = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void updateDeclaration()
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, MalformedURLException {

		Declaration expected = declaration;
		declaration = null;

		DeclarationInput input = DeclarationInput.builder() //
			.withId(expected.getId()) //
			.withKind(DeclarationKind.OPLE) //
			.withTitle("Updated test title") //
			.withDate(LocalDate.now()) //
			.withCountry("US") //
			.withUrl(URI.create("https://updated-domain.tld").toURL()) //
			.withSignatories("Martin Phillips\nOscar Diamentes\nLouise Pendros") //
			.withNotes("Updated test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Declaration actual = mutationExecutor.updateDeclaration(RESPONSE_SPEC, input);

		LOG_DATES[1] = actual.getUpdated();
		checkDeclaration(actual, expected.getStatus(), expected.getCreated(), earliestUpdated,
			input.getKind().label(), input.getTitle(), input.getDate(), "United States of America", input.getUrl(),
			input.getSignatories(), input.getNotes(), CRE, UPD);

		declaration = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void deleteDeclaration() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Declaration expected = declaration;
		declaration = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Declaration actual = mutationExecutor.deleteDeclaration(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkDeclaration(actual, StatusKind.DEL.label(), expected.getCreated(), earliestUpdated, expected.getKind(),
			expected.getTitle(), expected.getDate(), expected.getCountry(), expected.getUrl(),
			expected.getSignatories(), expected.getNotes(), CRE, UPD, DEL);

		declaration = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void createDeclarations() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Create another eight declarations and store them all in an array together with the previously created one.
		final int declarationCount = 8;
		List<Declaration> declarations = new ArrayList<>(declarationCount + 1);
		Declaration declaration0 = new Declaration();
		declaration0.setId(declaration.getId());
		declaration0.setDate(declaration.getDate());
		declaration0.setStatus(declaration.getStatus());
		declaration0.setKind(declaration.getKind());
		declaration0.setTitle(declaration.getTitle());
		declaration0.setNotes(declaration.getNotes());
		declaration0.set__typename(declaration.get__typename());
		declarations.add(declaration0);
		String[] numbers = {null, "one", "two", "three", "four", "five", "six", "seven", "eight"};
		for (int i = 1; i <= declarationCount; i++) {
			LocalDate date = LocalDate.now();
			String title = "Declaration " + numbers[i];
			String notes = "Notes #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				title = title.toUpperCase();
			if (i % 3 == 0)
				notes = null;
			DeclarationInput input = DeclarationInput.builder() //
				.withDate(date) //
				.withKind(DeclarationKind.DECL) //
				.withTitle(title) //
				.withNotes(notes) //
				.build();
			declarations.add(mutationExecutor.createDeclaration(MINIMAL_RESPONSE_SPEC, input));
		}
		DeclarationTests.declarations = declarations;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarations() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, null, null);

		checkPage(actuals, declarations.size(), 1, declarations.size(), 0, false, false, true, true, declarations, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		// "Notes #5 (filtered)"/"DECLARATION FIVE", "Notes #7 (filtered)"/"DECLARATION SEVEN",
		// "Notes #8 (filtered)"/"Declaration eight"
		List<Declaration> expected = subList(declarations, 5, 7, 8);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.declarations(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(declarations, 0);
		actuals = queryExecutor.declarations(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "DECLARATION FIVE", "DECLARATION ONE", "DECLARATION SEVEN", "DECLARATION THREE", "Declaration eight",
		// "Declaration four", "Declaration six", "Declaration two", "Updated test declaration"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1, 7, 6, 3, 2, 0 } //
			: new int[] { 5, 1, 7, 3, 8, 4, 6, 2, 0 };
		List<Declaration> expected = subList(declarations, indexes);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		Collections.reverse(expected);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Declaration eight", "DECLARATION FIVE", "Declaration four", "DECLARATION ONE", "DECLARATION SEVEN",
		// "Declaration six", "DECLARATION THREE", "Declaration two", "Updated test declaration"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Declaration> expected = subList(declarations, 8, 5, 4, 1, 7, 6, 3, 2, 0);

		DeclarationPage actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		Collections.reverse(expected);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
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

		// null/"DECLARATION THREE", null/"Declaration six", "Notes #1"/"DECLARATION ONE", "Notes #2"/"Declaration two",
		// "Notes #4"/"Declaration four", "Notes #5 (filtered)"/"DECLARATION FIVE",
		// "Notes #7 (filtered)"/"DECLARATION SEVEN", "Notes #8 (filtered)"/"Declaration eight",
		// "Updated test notes"/"Updated test declaration"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8, 0
		// CS: 3, 6, 1, 2, 4, 5, 7, 8, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 };
		List<Declaration> expected = subList(declarations, indexes);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"DECLARATION ONE", "Notes #2"/"Declaration two", "Notes #4"/"Declaration four",
		// "Notes #5 (filtered)"/"DECLARATION FIVE", "Notes #7 (filtered)"/"DECLARATION SEVEN",
		// "Notes #8 (filtered)"/"Declaration eight", "Updated test notes"/"Updated test declaration",
		// null/"DECLARATION THREE", null/"Declaration six",
		// CI: 1, 2, 4, 5, 7, 8, 0, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(null);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
			OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "DECLARATION FIVE", "DECLARATION SEVEN", "Declaration eight"
		// 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 7 } //
			: new int[] { 5, 7, 8 };
		List<Declaration> expected = subList(declarations, indexes);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("declaration") //
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

		// null/"DECLARATION THREE", null/"Declaration six", "Notes #1"/"DECLARATION ONE",
		// "Notes #2"/"Declaration two", "Notes #4"/"Declaration four", "Notes #5 (filtered)"/"DECLARATION FIVE",
		// "Notes #7 (filtered)"/"DECLARATION SEVEN", "Notes #8 (filtered)"/"Declaration eight"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8
		// CS: 3, 6, 1, 2, 4, 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8 };
		List<Declaration> expected = subList(declarations, indexes);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"DECLARATION ONE", "Notes #2"/"Declaration two", "Notes #4"/"Declaration four",
		// "Notes #5 (filtered)"/"DECLARATION FIVE", "Notes #7 (filtered)"/"DECLARATION SEVEN",
		// "Notes #8 (filtered)"/"Declaration eight", null/"DECLARATION THREE", null/"Declaration six"
		// CI: 1, 2, 4, 5, 7, 8, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(DirectionKind.ASC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 3, 6 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 6, 3 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		// NOTE: assume that records are returned in the same order as the unpaged query.
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		
		List<Declaration> expected = declarations.subList(0, 4);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = declarations.subList(4, 8);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = declarations.subList(8, 9);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		List<Declaration> expected = subList(declarations, 5, 7);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(declarations,8);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
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

		// "DECLARATION FIVE", "DECLARATION ONE", "DECLARATION SEVEN", "DECLARATION THREE", "Declaration eight",
		// "Declaration four", "Declaration six", "Declaration two", "Updated test declaration"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		List<Declaration> expected = subList(declarations, indexes);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0, 2, 3, 6 } //
			: new int[] { 0, 2, 6, 4 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 1, 4, 5 } //
			: new int[] { 8, 3, 7, 1 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, null, pageSort);
		checkPage(actuals, declarations.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclarations")
	void readDeclarationsPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
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

		// "DECLARATION FIVE", "DECLARATION SEVEN", "Declaration eight"
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		List<Declaration> expected = subList(declarations, indexes);
		DeclarationPage actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 5 } //
			: new int[] { 8, 7 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(declarations, indexes);
		actuals = queryExecutor.declarations(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkDeclaration(Declaration actual, Declaration expected, TransactionKind... txnKinds) {
		checkDeclaration(actual, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getKind(),
			expected.getTitle(), expected.getDate(), expected.getCountry(), expected.getUrl(),
			expected.getSignatories(), expected.getNotes(), txnKinds);
	}

	private void checkDeclaration(Declaration actual, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String kind, String title, LocalDate date, String country, URL url,
		String signatories, String notes, TransactionKind... txnKinds) {

		checkLinkableEntity(actual, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(actual).cached().isFalse();
		assertThat(actual).hasKind(kind);
		assertThat(actual).hasTitle(title);
		assertThat(actual).hasDate(date);
		assertThat(actual).hasCountry(country);
		assertThat(actual).hasUrl(url);
		assertThat(actual).hasSignatories(signatories);
		assertThat(actual).hasSignatoryCount(StringUtils.countLines(signatories));
		assertThat(actual).hasNotes(notes);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.DEC;
	}

}
