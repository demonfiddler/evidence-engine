/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
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
import static io.github.demonfiddler.ee.client.truth.QuotationSubject.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
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
class QuotationTests extends TopicalEntityTests<Quotation> {

	private static Quotation EXPECTED;
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
			quotee
			text
			date
			source
			url
			notes
		}
		""";

	static boolean hasExpectedQuotation() {
		return EXPECTED != null;
	}

	@Test
	@Order(1)
	void createQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		EXPECTED = null;

		QuotationInput input = QuotationInput.builder() //
			.withQuotee("Fred Bloggs") //
			.withText("Test quotation") //
			.withDate(LocalDate.now()) //
			.withSource("Test source") //
			.withUrl(URI.create("http://domain.tld").toURL()) //
			.withNotes("Test notes") //
			.build();
		OffsetDateTime earliestCreated = OffsetDateTime.now();
		Quotation actual = mutationExecutor.createQuotation(RESPONSE_SPEC, input);

		LOG_DATES[0] = actual.getCreated();
		checkQuotation(actual, StatusKind.DRA.getLabel(), earliestCreated, null, input.getQuotee(), input.getText(),
			input.getDate(), input.getSource(), input.getUrl(), input.getNotes(), CRE);

		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void readQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Quotation expected = EXPECTED;
		EXPECTED = null;

		Quotation actual = queryExecutor.quotationById(RESPONSE_SPEC, expected.getId());

		checkQuotation(actual, expected, CRE);
		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void updateQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		Quotation expected = EXPECTED;
		EXPECTED = null;

		QuotationInput input = QuotationInput.builder() //
			.withId(expected.getId()) //
			.withQuotee("John Doe") //
			.withText("Updated test quotation") //
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

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.QuotationTests#hasExpectedQuotation")
	void deleteQuotation() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Quotation expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Quotation actual = mutationExecutor.deleteQuotation(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkQuotation(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getQuotee(),
			expected.getText(), expected.getDate(), expected.getSource(), expected.getUrl(), expected.getNotes(), CRE,
			UPD, DEL);

		EXPECTED = actual;
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
