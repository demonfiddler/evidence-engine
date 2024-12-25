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
import static io.github.demonfiddler.ee.client.truth.DeclarationSubject.assertThat;

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

import io.github.demonfiddler.ee.common.util.StringUtils;

@SpringBootTest(classes = GraphQLClientMain.class)
@TestMethodOrder(OrderAnnotation.class)
class DeclarationTests extends TopicalEntityTests<Declaration> {

	private static Declaration EXPECTED;

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

	static boolean hasExpectedDeclaration() {
		return EXPECTED != null;
	}

	@Test
	@Order(1)
	void createDeclaration() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, //
		MalformedURLException {

		EXPECTED = null;

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
		checkDeclaration(actual, StatusKind.DRA.getLabel(), earliestUpdated, null, input.getKind().getLabel(),
			input.getTitle(), declarationDate, "United Kingdom", input.getUrl(), input.getSignatories(),
			input.getNotes(), CRE);

		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void readDeclaration() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Declaration expected = EXPECTED;
		EXPECTED = null;

		Declaration actual = queryExecutor.declarationById(RESPONSE_SPEC, expected.getId());

		checkDeclaration(actual, expected, CRE);

		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void updateDeclaration()
		throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, MalformedURLException {

		Declaration expected = EXPECTED;
		EXPECTED = null;

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
			input.getKind().getLabel(), input.getTitle(), input.getDate(), "United States of America", input.getUrl(),
			input.getSignatories(), input.getNotes(), CRE, UPD);

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.DeclarationTests#hasExpectedDeclaration")
	void deleteDeclaration() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Declaration expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Declaration actual = mutationExecutor.deleteDeclaration(RESPONSE_SPEC, expected.getId());

		LOG_DATES[2] = actual.getUpdated();
		checkDeclaration(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getKind(),
			expected.getTitle(), expected.getDate(), expected.getCountry(), expected.getUrl(),
			expected.getSignatories(), expected.getNotes(), CRE, UPD, DEL);

		EXPECTED = actual;
	}

	private void checkDeclaration(Declaration actual, Declaration expected, TransactionKind... txnKinds) {
		checkDeclaration(actual, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getKind(),
			expected.getTitle(), expected.getDate(), expected.getCountry(), expected.getUrl(),
			expected.getSignatories(), expected.getNotes(), txnKinds);
	}

	private void checkDeclaration(Declaration actual, String status, OffsetDateTime earliestCreated,
		OffsetDateTime earliestUpdated, String kind, String title, LocalDate date, String country, URL url,
		String signatories, String notes, TransactionKind... txnKinds) {

		checkTopicalEntity(actual, status, earliestCreated, earliestUpdated, txnKinds);
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
