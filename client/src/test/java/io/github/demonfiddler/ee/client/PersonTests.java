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
import static io.github.demonfiddler.ee.client.truth.PersonSubject.assertThat;

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
class PersonTests extends TopicalEntityTests<Person> {

	private static Person EXPECTED;
	private static List<Person> PERSONS;
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
			firstName
			nickname
			prefix
			lastName
			suffix
			alias
			notes
			qualifications
			country
			rating
			checked
			published
		}
		""";
	private static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status%s
			firstName
			lastName
			alias
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
				firstName
				lastName
				alias
				notes
			}
		}
		""";

	static boolean hasExpectedPerson() {
		return EXPECTED != null;
	}

	static boolean hasExpectedPersons() {
		return PERSONS != null;
	}	

	@Test
	@Order(1)
	void createPerson() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		EXPECTED = null;

		PersonInput input = PersonInput.builder() //
			.withTitle("Mr") //
			.withFirstName("John") //
			.withNickname("Jack") //
			.withPrefix("le") //
			.withLastName("Smith") //
			.withSuffix("Snr") //
			.withAlias("Jones") //
			.withNotes("Test notes") //
			.withQualifications("Test qualifications") //
			.withCountry("GB") //
			.withRating(5) //
			.withChecked(false) //
			.withPublished(false) //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Person actual = mutationExecutor.createPerson(RESPONSE_SPEC, input);

		// Check the returned Person object for correctness.
		LOG_DATES[0] = actual.getCreated();
		checkPerson(actual, StatusKind.DRA.getLabel(), earliestUpdated, null, input.getTitle(), input.getFirstName(),
			input.getNickname(), input.getPrefix(), input.getLastName(), input.getSuffix(), input.getAlias(),
			input.getNotes(), input.getQualifications(), "United Kingdom", input.getRating(), input.getChecked(),
			input.getPublished(), CRE);

		// Test passed, so remember result for following tests.
		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPerson")
	void readPerson() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Person expected = EXPECTED;
		EXPECTED = null;

		Person actual = queryExecutor.personById(RESPONSE_SPEC, expected.getId());

		// Check read person against the one created by the preceding createPerson() test.
		checkPerson(actual, expected, CRE);
		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPerson")
	void updatePerson() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Person expected = EXPECTED;
		EXPECTED = null;

		PersonInput input = PersonInput.builder() //
			.withId(expected.getId()) //
			.withTitle("Mrs") //
			.withFirstName("Joanne") //
			.withNickname("Jo") //
			.withPrefix("de") //
			.withLastName("Smythe") //
			.withSuffix("Jnr") //
			.withAlias("Smutt") //
			.withNotes("Updated test notes") //
			.withQualifications("Updated test qualifications") //
			.withCountry("US") //
			.withRating(4) //
			.withChecked(true) //
			.withPublished(true) //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Person actual = mutationExecutor.updatePerson(RESPONSE_SPEC, input);

		// Check read person against the one created by the preceding createPerson() test.
		LOG_DATES[1] = actual.getUpdated();
		checkPerson(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, input.getTitle(),
			input.getFirstName(), input.getNickname(), input.getPrefix(), input.getLastName(), input.getSuffix(),
			input.getAlias(), input.getNotes(), input.getQualifications(), "United States of America",
			input.getRating(), input.getChecked(), input.getPublished(), CRE, UPD);

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPerson")
	void deletePerson() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Person expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Person actual = mutationExecutor.deletePerson(RESPONSE_SPEC, expected.getId());

		// Check read person against the one created by the preceding createPerson() test.
		LOG_DATES[2] = actual.getUpdated();
		checkPerson(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getTitle(),
			expected.getFirstName(), expected.getNickname(), expected.getPrefix(), expected.getLastName(),
			expected.getSuffix(), expected.getAlias(), expected.getNotes(), expected.getQualifications(),
			expected.getCountry(), expected.getRating(), expected.getChecked(), expected.getPublished(), CRE, UPD, DEL);

		EXPECTED = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPerson")
	void createPersons() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Create another eight persons and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int personCount = 8;
		List<Person> persons = new ArrayList<>(personCount + 1);
		Person person0 = new Person();
		person0.setId(EXPECTED.getId());
		person0.setStatus(EXPECTED.getStatus());
		person0.setFirstName(EXPECTED.getFirstName());
		person0.setLastName(EXPECTED.getLastName());
		person0.setAlias(EXPECTED.getAlias());
		person0.setNotes(EXPECTED.getNotes());
		person0.set__typename(EXPECTED.get__typename());
		persons.add(person0);
		String[] firstNames = {"Heidi", "Gary", "Fiona", "Eric", "Desmond", "Charles", "Beth", "Alison"};
		String[] lastNames = {"Andrews", "Bosworth", "Charlton", "Douglas", "Edwards", "Farquhar", "Gibson", "Heath"};
		String[] aliases = {"Z", null, "X", null, "v", null, "t", null};
		for (int i = 1; i <= personCount; i++) {
			String notes = "Notes #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 == 0)
				notes = notes.toUpperCase();
			if (i % 3 == 0)
				notes = null;
			PersonInput input = PersonInput.builder() //
				.withFirstName(firstNames[i-1]) //
				.withLastName(lastNames[i-1]) //
				.withAlias(aliases[i-1]) //
				.withNotes(notes) //
				.withRating(1) //
				.withChecked(true) //
				.withPublished(true) //
				.build();
			persons.add(mutationExecutor.createPerson(responseSpec, input));
		}
		PERSONS = persons;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersons() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PersonPage actuals = queryExecutor.persons(responseSpec, null, null);

		checkPage(actuals, PERSONS.size(), 1, PERSONS.size(), 0, false, false, true, true, PERSONS, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		List<Person> expected = subList(PERSONS, 5, 7, 8);
		PersonPage actuals = queryExecutor.persons(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.persons(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(PERSONS, 0);
		actuals = queryExecutor.persons(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput lastNameOrder = OrderInput.builder() //
			.withProperty("lastName") //
			.build();
		OrderInput firstNameOrder = OrderInput.builder() //
			.withProperty("firstName") //
			.build();
		List<OrderInput> orders = List.of(lastNameOrder, firstNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// 1: Heidi Andrews (Z), 2: Gary Bosworth (null), 3: Fiona Charlton (X), 4: Eric Douglas (null),
		// 5: Desmond Edwards (v), 6: Charles Farquhar (null), 7: Beth Gibson (t), 8: Alison Heath (null),
		// 0: Joanne Smythe (Smutt)
		List<Person> expected = subList(PERSONS, 1, 2, 3, 4, 5, 6, 7, 8, 0);
		PersonPage actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.withIgnoreCase(true) //
			.build();
		OrderInput lastNameOrder = OrderInput.builder() //
			.withProperty("lastName") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, lastNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// 3: Fiona Charlton (X) / null, 6: Charles Farquhar (null) / null, 1: Heidi Andrews (Z) / Notes #1,
		// 2: Gary Bosworth (null) / NOTES #2, 4: Eric Douglas (null) / NOTES #4,
		// 5: Desmond Edwards (v) / Notes #5 (filtered), 7: Beth Gibson (t) / Notes #7 (FILTERED),
		// 8: Alison Heath (null) / NOTES #8 (FILTERED), 0: Joanne Smythe (Smutt) / Updated test notes,
		// 3, 6, 1, 2, 4, 5, 7, 8, 0
		List<Person> expected = subList(PERSONS, 3, 6, 1, 2, 4, 5, 7, 8, 0);
		PersonPage actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		notesOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		notesOrder.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		expected = subList(PERSONS, 0, 8, 7, 5, 4, 2, 1, 3, 6);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput lastNameOrder = OrderInput.builder() //
			.withProperty("lastName") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, lastNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// 3: null / Fiona Charlton (X), 6: null / Charles Farquhar (null), 2: NOTES #2 / Gary Bosworth (null),
		// 4: NOTES #4 / Eric Douglas (null), 8: NOTES #8 (FILTERED) / Alison Heath (null),
		// 1: Notes #1 / Heidi Andrews (Z), 5: Notes #5 (filtered) / Desmond Edwards (v),
		// 7: Notes #7 (filtered) / Beth Gibson (t), 0: Updated test notes / Joanne Smythe (Smutt)
		// 3, 6, 2, 4, 8, 1, 5, 7, 0
		List<Person> expected = subList(PERSONS, 3, 6, 2, 4, 8, 1, 5, 7, 0);
		PersonPage actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.DESC);
		expected = subList(PERSONS, 6, 3, 2, 4, 8, 1, 5, 7, 0);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// 2: NOTES #2 / Gary Bosworth (null),
		// 4: NOTES #4 / Eric Douglas (null), 8: NOTES #8 (FILTERED) / Alison Heath (null),
		// 1: Notes #1 / Heidi Andrews (Z), 5: Notes #5 (filtered) / Desmond Edwards (v),
		// 7: Notes #7 (filtered) / Beth Gibson (t), 0: Updated test notes / Joanne Smythe (Smutt),
		// 3: null / Fiona Charlton (X), 6: null / Charles Farquhar (null)
		// 2, 4, 8, 1, 5, 7, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		lastNameOrder.setDirection(null);
		expected = subList(PERSONS, 2, 4, 8, 1, 5, 7, 0, 3, 6);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.DESC);
		expected = subList(PERSONS, 2, 4, 8, 1, 5, 7, 0, 6, 3);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput notesOrder = OrderInput.builder() //
			.withProperty("notes") //
			.build();
		OrderInput lastNameOrder = OrderInput.builder() //
			.withProperty("lastName") //
			.build();
		List<OrderInput> orders = List.of(notesOrder, lastNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// 8: Alison Heath (null) / NOTES #8 (FILTERED), 5: Desmond Edwards (v) / Notes #5 (filtered),
		// 7: Beth Gibson (t) / Notes #7 (FILTERED)
		// 8, 5, 7
		List<Person> expected = subList(PERSONS, 8, 5, 7);
		PersonPage actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		notesOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		notesOrder.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput aliasOrder = OrderInput.builder() //
			.withProperty("alias") //
			.withNullHandling(NullHandlingKind.NULLS_FIRST) //
			.build();
		OrderInput lastNameOrder = OrderInput.builder() //
			.withProperty("lastName") //
			.build();
		List<OrderInput> orders = List.of(aliasOrder, lastNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// 8: Alison Heath (null) / NOTES #8 (FILTERED), 7: Beth Gibson (t) / Notes #7 (FILTERED),
		// 5: Desmond Edwards (v) / Notes #5 (filtered)
		// 8, 7, 5
		List<Person> expected = subList(PERSONS, 8, 7, 5);
		PersonPage actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		lastNameOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		aliasOrder.setDirection(DirectionKind.DESC);
		expected = subList(PERSONS, 8, 5, 7);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// 7: Beth Gibson (t) / Notes #7 (FILTERED), 5: Desmond Edwards (v) / Notes #5 (filtered),
		// 8: Alison Heath (null) / NOTES #8 (FILTERED)
		// 7, 5, 8
		aliasOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		aliasOrder.setDirection(DirectionKind.ASC);
		expected = subList(PERSONS,  7, 5, 8);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		aliasOrder.setDirection(DirectionKind.DESC);
		expected = subList(PERSONS, 5, 7, 8);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in database primary key order.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		PersonPage actuals = queryExecutor.persons(responseSpec, null, pageSort);
		List<Person> expected = PERSONS.subList(0, 4);
		checkPage(actuals, PERSONS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		expected = PERSONS.subList(4, 8);
		checkPage(actuals, PERSONS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		expected = PERSONS.subList(8, 9);
		checkPage(actuals, PERSONS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		// When using a filter that uses the full text index, the items may not be returned in database primary key
		// order, so we have to fetch all pages before checking content.
		// 5: Desmond Edwards (v) / Notes #5 (filtered), 7: Beth Gibson (t) / Notes #7 (FILTERED),
		// 8: Alison Heath (null) / NOTES #8 (FILTERED)
		List<Person> expected = subList(PERSONS, 5, 7);
		PersonPage actual = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actual, 3, 2, 2, 0, false, true, true, false, expected, false);
		
		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 8);
		actual = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actual, 3, 2, 2, 1, true, false, false, true, expected, false);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput firstNameOrder = OrderInput.builder() //
			.withProperty("firstName") //
			.build();
		List<OrderInput> orders = Collections.singletonList(firstNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();

		// 8: Alison Heath (null), 7: Beth Gibson (t), 6: Charles Farquhar (null), 5: Desmond Edwards (v),
		// 4: Eric Douglas (null), 3: Fiona Charlton (X), 2: Gary Bosworth (null), 1: Heidi Andrews (Z),
		// 0: Joanne Smythe (Smutt)
		List<Person> expected = subList(PERSONS, 8, 7, 6, 5);
		PersonPage actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 4, 3, 2, 1);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(PERSONS, 0);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 2, true, false, false, true, expected, true);

		firstNameOrder.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(PERSONS, 8, 7, 6, 5);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 4, 3, 2, 1);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(PERSONS, 0);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 2, true, false, false, true, expected, true);

		firstNameOrder.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(PERSONS, 0, 1, 2, 3);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 4, 5, 6, 7);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = subList(PERSONS, 8);
		actuals = queryExecutor.persons(responseSpec, null, pageSort);
		checkPage(actuals, PERSONS.size(), 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.PersonTests#hasExpectedPersons")
	void readPersonsPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		OrderInput firstNameOrder = OrderInput.builder() //
			.withProperty("firstName") //
			.build();
		List<OrderInput> orders = Collections.singletonList(firstNameOrder);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder() //
			.withSort(sort) //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		// 8: Alison Heath (null) / NOTES #8 (FILTERED), 7: Beth Gibson (t) / Notes #7 (FILTERED),
		// 5: Desmond Edwards (v) / Notes #5 (filtered)
		// 8, 7, 5
		List<Person> expected = subList(PERSONS, 8, 7);
		PersonPage actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 5);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		firstNameOrder.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		expected = subList(PERSONS, 8, 7);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 5);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		firstNameOrder.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		expected = subList(PERSONS, 5, 7);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(PERSONS, 8);
		actuals = queryExecutor.persons(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkPerson(Person person, Person expected, TransactionKind... txnKinds) {
		checkPerson(person, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getTitle(),
			expected.getFirstName(), expected.getNickname(), expected.getPrefix(), expected.getLastName(),
			expected.getSuffix(), expected.getAlias(), expected.getNotes(), expected.getQualifications(),
			expected.getCountry(), expected.getRating(), expected.getChecked(), expected.getPublished(), txnKinds);
	}

	private void checkPerson(Person person, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String title, String firstName, String nickname, String prefix, String lastName,
		String suffix, String alias, String notes, String qualifications, String country, Integer rating,
		Boolean checked, Boolean published, TransactionKind... txnKinds) {

		checkTopicalEntity(person, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(person).hasTitle(title);
		assertThat(person).hasFirstName(firstName);
		assertThat(person).hasNickname(nickname);
		assertThat(person).hasPrefix(prefix);
		assertThat(person).hasLastName(lastName);
		assertThat(person).hasSuffix(suffix);
		assertThat(person).hasAlias(alias);
		assertThat(person).hasNotes(notes);
		assertThat(person).hasQualifications(qualifications);
		assertThat(person).hasCountry(country);
		assertThat(person).hasRating(rating);
		assertThat(person).checked().isEqualTo(checked);
		assertThat(person).published().isEqualTo(published);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.PER;
	}

}
