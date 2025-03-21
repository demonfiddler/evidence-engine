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
import static io.github.demonfiddler.ee.client.truth.ClaimSubject.assertThat;

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
@Order(1)
@TestMethodOrder(OrderAnnotation.class)
class ClaimTests extends AbstractLinkableEntityTests<Claim> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimTests.class);

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
				date
				text
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

	static Claim claim;
	static List<Claim> claims;

	static boolean hasExpectedClaim() {
		return claim != null;
	}

	static boolean hasExpectedClaims() {
		return claims != null;
	}

	static void ensureExpectedClaims() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		if (claims == null) {
			QueryExecutor queryExecutor = SpringContext.getApplicationContext().getBean(QueryExecutor.class);
			String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
			List<Claim> content = queryExecutor.claims(responseSpec, null, null).getContent();
			if (content.isEmpty()) {
				LOGGER.error("Failed to initialise claims list from server");
			} else {
				claims = content;
				claim = claims.get(0);
				LOGGER.debug("Initialised claims list from server");
			}
		}
	}

	@Test
	@Order(1)
	void createClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		claim = null;

		LocalDate claimDate = LocalDate.now();
		ClaimInput input = ClaimInput.builder() //
			.withDate(claimDate) //
			.withText("Test text") //
			.withNotes("Test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Claim actual = mutationExecutor.createClaim(RESPONSE_SPEC, input);

		LOG_DATES[0] = actual.getCreated();
		checkClaim(actual, StatusKind.DRA.label(), earliestUpdated, null, claimDate, input.getText(), input.getNotes(),
			CRE);

		claim = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void readClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Claim expected = claim;
		claim = null;

		Claim actual = queryExecutor.claimById(RESPONSE_SPEC, expected.getId());

		checkClaim(actual, expected, CRE);
		claim = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void updateClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Claim expected = claim;
		claim = null;

		ClaimInput input = ClaimInput.builder() //
			.withId(expected.getId()) //
			.withDate(expected.getDate()) //
			.withText("Updated test text") //
			.withNotes("Updated test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Claim actual = mutationExecutor.updateClaim(RESPONSE_SPEC, input);

		LOG_DATES[1] = actual.getUpdated();
		checkClaim(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, expected.getDate(),
			input.getText(), input.getNotes(), CRE, UPD);

		claim = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void deleteClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Claim expected = claim;
		claim = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Claim actual = mutationExecutor.deleteClaim(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkClaim(actual, StatusKind.DEL.label(), expected.getCreated(), earliestUpdated, expected.getDate(),
			expected.getText(), expected.getNotes(), CRE, UPD, DEL);

		claim = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void createClaims() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Create another eight claims and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int claimCount = 8;
		List<Claim> claims = new ArrayList<>(claimCount + 1);
		Claim claim0 = new Claim();
		claim0.setId(claim.getId());
		claim0.setDate(claim.getDate());
		claim0.setStatus(claim.getStatus());
		claim0.setText(claim.getText());
		claim0.setNotes(claim.getNotes());
		claim0.set__typename(claim.get__typename());
		claims.add(claim0);
		String[] numbers = { null, "one", "two", "three", "four", "five", "six", "seven", "eight" };
		for (int i = 1; i <= claimCount; i++) {
			LocalDate date = LocalDate.now();
			String text = "Claim " + numbers[i];
			String notes = "Notes #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				text = text.toUpperCase();
			if (i % 3 == 0)
				notes = null;
			ClaimInput input = ClaimInput.builder() //
				.withDate(date) //
				.withText(text) //
				.withNotes(notes) //
				.build();
			claims.add(mutationExecutor.createClaim(responseSpec, input));
		}
		ClaimTests.claims = claims;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaims() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		ClaimPage actuals = queryExecutor.claims(responseSpec, null, null);

		checkPage(actuals, claims.size(), 1, claims.size(), 0, false, false, true, true, claims, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsFiltered() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		List<Claim> expected = subList(claims, 5, 7, 8);
		ClaimPage actuals = queryExecutor.claims(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.claims(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(claims, 0);
		actuals = queryExecutor.claims(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "CLAIM FIVE", "CLAIM ONE", "CLAIM SEVEN", "CLAIM THREE", "Claim eight", "Claim four", "Claim six",
		// "Claim two", "Updated test title"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1, 7, 6, 3, 2, 0 } //
			: new int[] { 5, 1, 7, 3, 8, 4, 6, 2, 0 };
		List<Claim> expected = subList(claims, indexes);

		ClaimPage actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		Collections.reverse(expected);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsSortedIgnoreCase() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Claim eight", "CLAIM FIVE", "Claim four", "CLAIM ONE", "CLAIM SEVEN", "Claim six", "CLAIM THREE",
		// "Claim two", "Updated test title"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Claim> expected = subList(claims, 8, 5, 4, 1, 7, 6, 3, 2, 0);

		ClaimPage actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsSortedNullOrdered() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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

		// null/"CLAIM THREE", null/"Claim six", "Notes #1"/"CLAIM ONE", "Notes #2"/"Claim two",
		// "Notes #4"/"Claim four", "Notes #5 (filtered)"/"CLAIM FIVE", "Notes #7 (filtered)"/"CLAIM SEVEN",
		// "Notes #8 (filtered)"/"Claim eight", "Updated test notes"/"Updated test title"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8, 0
		// CS: 3, 6, 1, 2, 4, 5, 7, 8, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 };
		List<Claim> expected = subList(claims, indexes);
		ClaimPage actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"CLAIM ONE", "Notes #2"/"Claim two", "Notes #4"/"Claim four", "Notes #5 (filtered)"/"CLAIM FIVE",
		// "Notes #7 (filtered)"/"CLAIM SEVEN", "Notes #8 (filtered)"/"Claim eight",
		// "Updated test notes"/"Updated test title", null/"CLAIM THREE", null/"Claim six"
		// CI: 1, 2, 4, 5, 7, 8, 0, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(null);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsFilteredSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput order = OrderInput.builder() //
			.withProperty("text") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "CLAIM FIVE", "CLAIM SEVEN", "Claim eight"
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 7 } //
			: new int[] { 5, 7, 8 };
		List<Claim> expected = subList(claims, indexes);
		ClaimPage actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 1, 3, 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 1, 3, 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 1, 3, 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsFilteredSortedNullHandling()
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("claim") //
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

		// null/"CLAIM THREE", null/"Claim six", "Notes #1"/"CLAIM ONE", "Notes #2"/"Claim two",
		// "Notes #4"/"Claim four", "Notes #5 (filtered)"/"CLAIM FIVE", "Notes #7 (filtered)"/"CLAIM SEVEN",
		// "Notes #8 (filtered)"/"Claim eight"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8
		// CS: 3, 6, 1, 2, 4, 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8 };
		List<Claim> expected = subList(claims, indexes);
		ClaimPage actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"CLAIM ONE", "Notes #2"/"Claim two", "Notes #4"/"Claim four", "Notes #5 (filtered)"/"CLAIM FIVE",
		// "Notes #7 (filtered)"/"CLAIM SEVEN", "Notes #8 (filtered)"/"Claim eight", null/"CLAIM THREE", null/"Claim
		// six"
		// CI: 1, 2, 4, 5, 7, 8, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(DirectionKind.ASC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 3, 6 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 6, 3 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsPaged() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		ClaimPage actuals = queryExecutor.claims(responseSpec, null, pageSort);
		List<Claim> expected = claims.subList(0, 4);
		checkPage(actuals, claims.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		expected = claims.subList(4, 8);
		checkPage(actuals, claims.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		expected = claims.subList(8, 9);
		checkPage(actuals, claims.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsPagedFiltered() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		ClaimPage actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		List<Claim> expected = subList(claims, 5, 7);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		expected = subList(claims, 8);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsPagedSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
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

		// "CLAIM FIVE", "CLAIM ONE", "CLAIM SEVEN", "CLAIM THREE", "Claim eight", "Claim four", "Claim six",
		// "Claim two", "Updated test title"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		List<Claim> expected = subList(claims, indexes);
		ClaimPage actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0, 2, 3, 6 } //
			: new int[] { 0, 2, 6, 4 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 1, 4, 5 } //
			: new int[] { 8, 3, 7, 1 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, null, pageSort);
		checkPage(actuals, claims.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaims")
	void readClaimsPagedFilteredSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
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

		// "CLAIM FIVE", "CLAIM SEVEN", "Claim eight"
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		List<Claim> expected = subList(claims, indexes);
		ClaimPage actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 5 } //
			: new int[] { 8, 7 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(claims, indexes);
		actuals = queryExecutor.claims(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkClaim(Claim claim, Claim expected, TransactionKind... txnKinds) {
		checkClaim(claim, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getDate(),
			expected.getText(), expected.getNotes(), txnKinds);
	}

	private void checkClaim(Claim claim, String status, OffsetDateTime earliestCreated, OffsetDateTime earliestUpdated,
		LocalDate date, String text, String notes, TransactionKind... txnKinds) {

		checkLinkableEntity(claim, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(claim).hasDate(date);
		assertThat(claim).hasText(text);
		assertThat(claim).hasNotes(notes);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.CLA;
	}

}
