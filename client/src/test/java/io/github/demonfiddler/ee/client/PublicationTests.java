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
import static io.github.demonfiddler.ee.client.truth.PublicationSubject.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
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
@Order(5)
@TestMethodOrder(OrderAnnotation.class)
class PublicationTests extends AbstractLinkableEntityTests<Publication> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublicationTests.class);

	private static record IsbnPub4Range(short registrationGroup, short registrantMin, short registrantMax) {
	}

	private static final Random RANDOM = new Random();
	private static final IsbnPub4Range[] ISBN_PUB4_RANGES = { //
		new IsbnPub4Range((short)0, (short)2280, (short)2289), //
		new IsbnPub4Range((short)0, (short)3690, (short)3699), //
		new IsbnPub4Range((short)0, (short)6390, (short)6397), //
		new IsbnPub4Range((short)0, (short)6550, (short)6559), //
		new IsbnPub4Range((short)0, (short)7000, (short)8499), //
		new IsbnPub4Range((short)1, (short)350, (short)399), //
		new IsbnPub4Range((short)1, (short)700, (short)999), //
		new IsbnPub4Range((short)1, (short)3980, (short)5499), //
		new IsbnPub4Range((short)1, (short)6500, (short)6799), //
		new IsbnPub4Range((short)1, (short)6860, (short)7139), //
		new IsbnPub4Range((short)1, (short)7170, (short)7319), //
		new IsbnPub4Range((short)1, (short)7620, (short)7634), //
		new IsbnPub4Range((short)1, (short)7900, (short)7999), //
		new IsbnPub4Range((short)1, (short)8672, (short)8675), //
		new IsbnPub4Range((short)1, (short)9730, (short)9877) //
	};
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
			authors
			journal {
				id
			}
			kind
			date
			year
			abstract
			notes
			peerReviewed
			doi
			url
			cached
			isbn
			accessed
		}
		""";
	private static final String MINIMAL_RESPONSE_SPEC = //
		"""
		{
			id
			status%s
			kind
			title
			abstract
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
				kind
				title
				abstract
				notes
			}
		}
		""";

	static Publication publication;
	static List<Publication> publications;

	static boolean hasExpectedPublication() {
		return publication != null;
	}
	
	static boolean hasExpectedPublications() {
		return publications != null;
	}

	static void ensureExpectedPublications() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		if (publications == null) {
			QueryExecutor queryExecutor = SpringContext.getApplicationContext().getBean(QueryExecutor.class);
			String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
			List<Publication> content = queryExecutor.publications(responseSpec, null, null).getContent();
			if (content.isEmpty()) {
				LOGGER.error("Failed to initialise publications list from server");
			} else {
				publications = content;
				publication = publications.get(0);
				LOGGER.debug("Initialised publications list from server");
			}
		}
	}

	@Test
	@Order(1)
	void createPublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		publication = null;

		LocalDate date = LocalDate.now();
		PublicationInput input = PublicationInput.builder() //
			.withTitle("Test title") //
			.withAuthorNames("Fred Bloggs\nJohn Doe") //
			.withKind(PublicationKind.JOUR) //
			.withDate(date) //
			.withYear(date.getYear()) //
			.withAbstract("Test abstract") //
			.withNotes("Test notes") //
			.withPeerReviewed(false) //
			.withDoi(generateRandomDoi()) //
			.withIsbn(generateRandomIsbn()) //
			.withUrl(URI.create("http://domain.tld").toURL()) //
			.withCached(false) //
			.withAccessed(LocalDate.now()) //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publication actual = mutationExecutor.createPublication(RESPONSE_SPEC, input);

		LOG_DATES[0] = actual.getCreated();
		checkPublication(actual, StatusKind.DRA.label(), earliestUpdated, null, input.getTitle(),
			input.getAuthorNames(), input.getJournalId(), input.getKind().label(), input.getDate(), input.getYear(),
			input.getAbstract(), input.getNotes(), input.getPeerReviewed(), input.getDoi(), input.getIsbn(),
			input.getUrl(), input.getCached(), input.getAccessed(), CRE);

		publication = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void readPublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Publication expected = publication;
		publication = null;

		Publication actual = queryExecutor.publicationById(RESPONSE_SPEC, expected.getId());

		checkPublication(actual, expected, CRE);

		publication = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void updatePublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Publication expected = publication;
		publication = null;

		LocalDate date = LocalDate.now();
		PublicationInput input = PublicationInput.builder() //
			.withId(expected.getId()) //
			.withTitle("Updated test title") //
			.withAuthorNames("Joanna Smith\nJane Doe") //
			// .withJournalId(0L) //
			.withKind(PublicationKind.BOOK) //
			.withDate(date) //
			.withYear(date.getYear()) //
			.withAbstract("Updated test abstract") //
			.withNotes("Updated test notes") //
			.withPeerReviewed(false) //
			.withDoi(generateRandomDoi()) //
			.withIsbn(generateRandomIsbn()) //
			.withUrl(URI.create("http://updated-domain.tld").toURL()) //
			.withCached(true) //
			.withAccessed(LocalDate.now()) //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publication actual = mutationExecutor.updatePublication(RESPONSE_SPEC, input);

		// Check read publication against the one created by the preceding createPublication() test.
		LOG_DATES[1] = actual.getUpdated();
		checkPublication(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, input.getTitle(),
			input.getAuthorNames(), getJournalId(expected), "Book, whole", input.getDate(), input.getYear(),
			input.getAbstract(), input.getNotes(), input.getPeerReviewed(), input.getDoi(), input.getIsbn(),
			input.getUrl(), input.getCached(), input.getAccessed(), CRE, UPD);

		publication = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void deletePublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Publication expected = publication;
		publication = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publication actual = mutationExecutor.deletePublication(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkPublication(actual, StatusKind.DEL.label(), expected.getCreated(), earliestUpdated, expected.getTitle(),
			expected.getAuthors(), getJournalId(actual), expected.getKind(), expected.getDate(), expected.getYear(),
			expected.getAbstract(), expected.getNotes(), expected.getPeerReviewed(), expected.getDoi(),
			expected.getIsbn(), expected.getUrl(), expected.getCached(), expected.getAccessed(), CRE, UPD, DEL);

		publication = actual;
	}

	@Test
	@Order(5)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void createPublications() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Create another eight publications and store them all in an array together with the previously created one.
		String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("");
		final int publicationCount = 8;
		List<Publication> publications = new ArrayList<>(publicationCount + 1);
		Publication publication0 = new Publication();
		publication0.setId(publication.getId());
		publication0.setStatus(publication.getStatus());
		publication0.setKind(publication.getKind());
		publication0.setTitle(publication.getTitle());
		publication0.setAbstract(publication.getAbstract());
		publication0.setNotes(publication.getNotes());
		publication0.set__typename(publication.get__typename());
		publications.add(publication0);
		String[] numbers = {null, "one", "two", "three", "four", "five", "six", "seven", "eight"};
		for (int i = 1; i <= publicationCount; i++) {
			String authors = "Author " + numbers[i];
			String _abstract = "Abstract " + numbers[i];
			String title = "Publication " + numbers[i];
			String notes = "Notes #" + i + (i > 4 ? " (filtered)" : "");
			if (i % 2 != 0)
				title = title.toUpperCase();
			if (i % 3 == 0)
				notes = null;
			PublicationInput input = PublicationInput.builder() //
				.withKind(PublicationKind.JOUR) //
				.withAuthorNames(authors) //
				.withTitle(title) //
				.withAbstract(_abstract) //
				.withNotes(notes) //
				.withCached(false) //
				.withPeerReviewed(false) //
				.build();
			publications.add(mutationExecutor.createPublication(responseSpec, input));
		}

		PublicationTests.publications = publications;
	}

	@Test
	@Order(6)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublications() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PublicationPage actuals = queryExecutor.publications(responseSpec, null, null);

		checkPage(actuals, publications.size(), 1, publications.size(), 0, false, false, true, true, publications, true);
	}

	@Test
	@Order(7)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();

		List<Publication> expected = subList(publications, 5, 7, 8);
		PublicationPage actuals = queryExecutor.publications(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DRA));
		actuals = queryExecutor.publications(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		filter.setStatus(List.of(StatusKind.DEL));
		filter.setText(null);
		expected = subList(publications, 0);
		actuals = queryExecutor.publications(responseSpec, filter, null);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(8)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "PUBLICATION FIVE", "PUBLICATION ONE", "PUBLICATION SEVEN", "PUBLICATION THREE", "Publication eight",
		// "Publication four", "Publication six", "Publication two", "Updated test publication"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1, 7, 6, 3, 2, 0 } //
			: new int[] { 5, 1, 7, 3, 8, 4, 6, 2, 0 };
		List<Publication> expected = subList(publications, indexes);
		PublicationPage actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(9)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsSortedIgnoreCase() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		OrderInput order = OrderInput.builder() //
			.withProperty("title") //
			.withIgnoreCase(true) //
			.build();
		List<OrderInput> orders = Collections.singletonList(order);
		SortInput sort = SortInput.builder().withOrders(orders).build();
		PageableInput pageSort = PageableInput.builder().withSort(sort).build();

		// "Publication eight", "PUBLICATION FIVE", "Publication four", "PUBLICATION ONE", "PUBLICATION SEVEN",
		// "Publication six", "PUBLICATION THREE", "Publication two", "Updated test publication"
		// 8, 5, 4, 1, 7, 6, 3, 2, 0
		List<Publication> expected = subList(publications, 8, 5, 4, 1, 7, 6, 3, 2, 0);

		PublicationPage actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		Collections.reverse(expected);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(10)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsSortedNullOrdered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
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

		// null/"PUBLICATION THREE", null/"Publication six", "Notes #1"/"PUBLICATION ONE", "Notes #2"/"Publication two",
		// "Notes #4"/"Publication four", "Notes #5 (filtered)"/"PUBLICATION FIVE", "Notes #7 (filtered)"/"PUBLICATION SEVEN",
		// "Notes #8 (filtered)"/"Publication eight", "Updated test notes"/"Updated test publication"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8, 0
		// CS: 3, 6, 1, 2, 4, 5, 7, 8, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 };
		List<Publication> expected = subList(publications, indexes);
		PublicationPage actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8, 0 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8, 0 };
		expected = subList(publications, indexes);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"PUBLICATION ONE", "Notes #2"/"Publication two", "Notes #4"/"Publication four", "Notes #5 (filtered)"/"PUBLICATION FIVE",
		// "Notes #7 (filtered)"/"PUBLICATION SEVEN", "Notes #8 (filtered)"/"Publication eight",
		// "Updated test notes"/"Updated test publication", null/"PUBLICATION THREE", null/"Publication six",
		// CI: 1, 2, 4, 5, 7, 8, 0, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 0, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(null);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 0, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 0, 6, 3 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(11)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
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

		// "PUBLICATION FIVE", "PUBLICATION SEVEN", "Publication eight"
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 7 } //
			: new int[] { 5, 7, 8 };
		List<Publication> expected = subList(publications, indexes);
		PublicationPage actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		Collections.reverse(expected);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(12)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsFilteredSortedNullHandling() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("publication") //
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

		// null/"PUBLICATION THREE", null/"Publication six", "Notes #1"/"PUBLICATION ONE", "Notes #2"/"Publication two",
		// "Notes #4"/"Publication four", "Notes #5 (filtered)"/"PUBLICATION FIVE", "Notes #7 (filtered)"/"PUBLICATION SEVEN",
		// "Notes #8 (filtered)"/"Publication eight"
		// CI: 6, 3, 1, 2, 4, 5, 7, 8
		// CS: 3, 6, 1, 2, 4, 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 6, 3, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 3, 6, 1, 2, 4, 5, 7, 8 };
		List<Publication> expected = subList(publications, indexes);
		PublicationPage actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.ASC);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 3, 6, 1, 2, 4, 5, 7, 8 } //
			: new int[] { 6, 3, 1, 2, 4, 5, 7, 8 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		// "Notes #1"/"PUBLICATION ONE", "Notes #2"/"Publication two", "Notes #4"/"Publication four", "Notes #5 (filtered)"/"PUBLICATION FIVE",
		// "Notes #7 (filtered)"/"PUBLICATION SEVEN", "Notes #8 (filtered)"/"Publication eight",
		// null/"PUBLICATION THREE", null/"Publication six"
		// CI: 1, 2, 4, 5, 7, 8, 6, 3
		// CS: 1, 2, 4, 5, 7, 8, 3, 6
		notesOrder.setNullHandling(NullHandlingKind.NULLS_LAST);
		textOrder.setDirection(DirectionKind.ASC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 6, 3 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 3, 6 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);

		textOrder.setDirection(DirectionKind.DESC);
		indexes = CASE_INSENSITIVE //
			? new int[] { 1, 2, 4, 5, 7, 8, 3, 6 } //
			: new int[] { 1, 2, 4, 5, 7, 8, 6, 3 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, true);
	}

	@Test
	@Order(13)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsPaged() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		// NOTE: assume that records are returned in the same order as the unpaged query.
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(4) //
			.build();
		PublicationPage actuals = queryExecutor.publications(responseSpec, null, pageSort);
		List<Publication> expected = publications.subList(0, 4);
		checkPage(actuals, publications.size(), 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = publications.subList(4, 8);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, publications.size(), 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		expected = publications.subList(8, 9);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, publications.size(), 3, 4, 2, true, false, false, true, expected, true);
	}	

	@Test
	@Order(14)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsPagedFiltered() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
		String responseSpec = PAGED_RESPONSE_SPEC.formatted("");
		LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
			.withText("filtered") //
			.build();
		PageableInput pageSort = PageableInput.builder() //
			.withPageNumber(0) //
			.withPageSize(2) //
			.build();

		List<Publication> expected = subList(publications, 5, 7);
		PublicationPage actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		expected = subList(publications,8);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	@Test
	@Order(15)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsPagedSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
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

		// "PUBLICATION FIVE", "PUBLICATION ONE", "PUBLICATION SEVEN", "PUBLICATION THREE", "Publication eight", "Publication four", "Publication six",
		// "Publication two", "Updated test publication"
		// CI: 8, 5, 4, 1, 7, 6, 3, 2, 0
		// CS: 5, 1, 7, 3, 8, 4, 6, 2, 0
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		List<Publication> expected = subList(publications, indexes);
		PublicationPage actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(publications, 0);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5, 4, 1 } //
			: new int[] { 5, 1, 7, 3 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 6, 3, 2 } //
			: new int[] { 8, 4, 6, 2 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0 } //
			: new int[] { 0 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 0, 2, 3, 6 } //
			: new int[] { 0, 2, 6, 4 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 1, 4, 5 } //
			: new int[] { 8, 3, 7, 1 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 1, true, true, false, false, expected, true);

		pageSort.setPageNumber(2);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, null, pageSort);
		checkPage(actuals, 9, 3, 4, 2, true, false, false, true, expected, true);
	}

	@Test
	@Order(16)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublications")
	void readPublicationsPagedFilteredSorted() throws GraphQLRequestPreparationException , GraphQLRequestExecutionException {
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

		// "PUBLICATION FIVE", "PUBLICATION SEVEN", "Publication eight"
		// CI: 8, 5, 7
		// CS: 5, 7, 8
		int[] indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		List<Publication> expected = subList(publications, indexes);
		PublicationPage actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.ASC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8, 5 } //
			: new int[] { 5, 7 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7 } //
			: new int[] { 8 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);

		order.setDirection(DirectionKind.DESC);
		pageSort.setPageNumber(0);
		indexes = CASE_INSENSITIVE //
			? new int[] { 7, 5 } //
			: new int[] { 8, 7 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 0, false, true, true, false, expected, true);

		pageSort.setPageNumber(1);
		indexes = CASE_INSENSITIVE //
			? new int[] { 8 } //
			: new int[] { 5 };
		expected = subList(publications, indexes);
		actuals = queryExecutor.publications(responseSpec, filter, pageSort);
		checkPage(actuals, 3, 2, 2, 1, true, false, false, true, expected, true);
	}

	private void checkPublication(Publication publication, Publication expected, TransactionKind... txnKinds) {
		Long journalId = getJournalId(publication);
		checkPublication(publication, expected.getStatus(), expected.getCreated(), expected.getUpdated(),
			expected.getTitle(), expected.getAuthors(), journalId, expected.getKind(), expected.getDate(),
			expected.getYear(), expected.getAbstract(), expected.getNotes(), expected.getPeerReviewed(),
			expected.getDoi(), expected.getIsbn(), expected.getUrl(), expected.getCached(), expected.getAccessed(),
			txnKinds);
	}

	private void checkPublication(Publication publication, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String title, String authors, Long journalId, String kind, LocalDate date,
		Integer year, String _abstract, String notes, Boolean peerReviewed, String doi, String isbn, URL url,
		Boolean cached, LocalDate accessed, TransactionKind... txnKinds) {

		checkLinkableEntity(publication, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(publication).hasTitle(title);
		assertThat(publication).hasAuthors(authors);
		if (journalId == null) {
			assertThat(publication).journal().isNull();
		} else {
			assertThat(publication).journal().isNotNull();
			assertThat(publication).journal().hasId(journalId);
		}
		assertThat(publication).hasKind(kind);
		assertThat(publication).hasDate(date);
		assertThat(publication).hasYear(year);
		assertThat(publication).hasAbstract(_abstract);
		assertThat(publication).hasNotes(notes);
		assertThat(publication).peerReviewed().isEqualTo(peerReviewed);
		assertThat(publication).hasDoi(doi);
		assertThat(publication).hasIsbn(isbn);
		assertThat(publication).hasUrl(url);
		assertThat(publication).hasCached(cached);
		assertThat(publication).hasAccessed(accessed);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.PUB;
	}

	private Long getJournalId(Publication publication) {
		return getEntityId(publication.getJournal());
	}

	private String generateRandomDoi() {
		StringBuilder doi = new StringBuilder("10.");
		int prefix = 1000 + RANDOM.nextInt(999);
		doi.append(prefix);
		doi.append('/');
		int suffix = 10000 + RANDOM.nextInt(99999);
		doi.append(suffix);
		return doi.toString();
	}

	private String generateRandomIsbn() {
		// ISBN format: prefix-registrationgroup-registrant-publication-checksum
		// For simplicity's sake, we will generate 13-digit ISBNs with a 978- prefix
		// and a publication element length of 4-digits.
		// Format: 978-x-xxxx-xxxx-x

		int index = RANDOM.nextInt(ISBN_PUB4_RANGES.length);
		IsbnPub4Range range = ISBN_PUB4_RANGES[index];
		short[] digits = new short[13];
		digits[0] = 9;
		digits[1] = 7;
		digits[2] = 8;
		digits[3] = range.registrationGroup;
		int registrant = range.registrantMin + RANDOM.nextInt(range.registrantMax - range.registrantMin + 1);
		int publication = RANDOM.nextInt(10000);
		append(digits, 4, 4, registrant);
		append(digits, 8, 4, publication);
		computeCheckDigit(digits);

		StringBuilder isbn = new StringBuilder(17);
		for (int i = 0; i < 13; i++)
			isbn.append(digits[i]);
		isbn.insert(3, '-');
		isbn.insert(5, '-');
		isbn.insert(10, '-');
		isbn.insert(15, '-');

		return isbn.toString();
	}

	private void append(short[] digits, int index, int count, int value) {
		for (int i = index + count - 1; i >= index; i--) {
			digits[i] = (short)(value % 10);
			value /= 10;
		}
	}

	private void computeCheckDigit(short[] digits) {
		int s = 0;
		int[] coefficients = { 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1 };
		for (int i = 0; i < 12; i++)
			s += digits[i] * coefficients[i];
		s %= 10;
		s = 10 - s;
		digits[12] = (short)s;
	}

}
