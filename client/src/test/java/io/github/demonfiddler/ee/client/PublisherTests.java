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

	private static Publisher EXPECTED;
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

	static boolean hasExpectedPublisher() {
		return EXPECTED != null;
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
