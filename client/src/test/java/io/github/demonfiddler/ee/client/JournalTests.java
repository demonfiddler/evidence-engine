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

	static boolean hasExpectedJournal() {
		return EXPECTED != null;
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
			.withTitle("Updated Test Journal") //
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
