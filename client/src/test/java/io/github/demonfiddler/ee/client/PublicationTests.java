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
import static io.github.demonfiddler.ee.client.truth.PublicationSubject.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
class PublicationTests extends TopicalEntityTests<Publication> {

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
	private static Publication EXPECTED;
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

	static boolean hasExpectedPublication() {
		return EXPECTED != null;
	}

	@Test
	@Order(1)
	void createPublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		EXPECTED = null;

		LocalDate date = LocalDate.now();
		PublicationInput input = PublicationInput.builder() //
			.withTitle("Test title") //
			.withAuthorNames("Fred Bloggs\nJohn Doe") //
			// .withJournalId(0L) //
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

		// Check the returned Publication object for correctness.
		LOG_DATES[0] = actual.getCreated();
		checkPublication(actual, StatusKind.DRA.getLabel(), earliestUpdated, null, input.getTitle(),
			input.getAuthorNames(), input.getJournalId(), input.getKind().getLabel(), input.getDate(), input.getYear(),
			input.getAbstract(), input.getNotes(), input.getPeerReviewed(), input.getDoi(), input.getIsbn(),
			input.getUrl(), input.getCached(), input.getAccessed(), CRE);

		// Test passed, so remember result for following tests.
		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void readPublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Publication expected = EXPECTED;
		EXPECTED = null;

		Publication actual = queryExecutor.publicationById(RESPONSE_SPEC, expected.getId());

		// Check read publication against the one created by the preceding createPublication() test.
		checkPublication(actual, expected, CRE);
		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void updatePublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Publication expected = EXPECTED;
		EXPECTED = null;

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

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.PublicationTests#hasExpectedPublication")
	void deletePublication() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Publication expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Publication actual = mutationExecutor.deletePublication(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkPublication(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getTitle(),
			expected.getAuthors(), getJournalId(actual), expected.getKind(), expected.getDate(), expected.getYear(),
			expected.getAbstract(), expected.getNotes(), expected.getPeerReviewed(), expected.getDoi(),
			expected.getIsbn(), expected.getUrl(), expected.getCached(), expected.getAccessed(), CRE, UPD, DEL);

		EXPECTED = actual;
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

		checkTopicalEntity(publication, status, earliestCreated, earliestUpdated, txnKinds);
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
