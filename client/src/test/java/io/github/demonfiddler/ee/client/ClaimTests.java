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
import static io.github.demonfiddler.ee.client.truth.ClaimSubject.assertThat;

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
class ClaimTests extends TopicalEntityTests<Claim> {

	private static Claim EXPECTED;
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
			date
			text
			notes
		}
		""";

	static boolean hasExpectedClaim() {
		return EXPECTED != null;
	}

	@Test
	@Order(1)
	void createClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		EXPECTED = null;

		// Create the test fixture.
		LocalDate claimDate = LocalDate.now();
		ClaimInput input = ClaimInput.builder() //
			.withDate(claimDate) //
			.withText("Test claim") //
			.withNotes("Test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Claim actual = mutationExecutor.createClaim(RESPONSE_SPEC, input);

		// Check the returned Claim object for correctness.
		LOG_DATES[0] = actual.getCreated();
		checkClaim(actual, StatusKind.DRA.getLabel(), earliestUpdated, null, claimDate, input.getText(),
			input.getNotes(), CRE);

		// Test passed, so remember result for following tests.
		EXPECTED = actual;
	}

	@Test
	@Order(2)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void readClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Claim expected = EXPECTED;
		EXPECTED = null;

		Claim actual = queryExecutor.claimById(RESPONSE_SPEC, expected.getId());

		// Check read claim against the one created by the preceding createClaim() test.
		checkClaim(actual, expected, CRE);
		EXPECTED = actual;
	}

	@Test
	@Order(3)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void updateClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Claim expected = EXPECTED;
		EXPECTED = null;

		ClaimInput input = ClaimInput.builder() //
			.withId(expected.getId()) //
			.withDate(expected.getDate()) //
			.withText("Updated Test claim") //
			.withNotes("Updated Test notes") //
			.build();
		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Claim actual = mutationExecutor.updateClaim(RESPONSE_SPEC, input);

		// Check read claim against the one created by the preceding createClaim() test.
		LOG_DATES[1] = actual.getUpdated();
		checkClaim(actual, expected.getStatus(), expected.getCreated(), earliestUpdated, expected.getDate(),
			input.getText(), input.getNotes(), CRE, UPD);

		EXPECTED = actual;
	}

	@Test
	@Order(4)
	@EnabledIf("io.github.demonfiddler.ee.client.ClaimTests#hasExpectedClaim")
	void deleteClaim() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		Claim expected = EXPECTED;
		EXPECTED = null;

		OffsetDateTime earliestUpdated = OffsetDateTime.now();
		Claim actual = mutationExecutor.deleteClaim(RESPONSE_SPEC, expected.getId());

		// Check read claim against the one created by the preceding createClaim() test.
		LOG_DATES[2] = actual.getUpdated();
		checkClaim(actual, StatusKind.DEL.getLabel(), expected.getCreated(), earliestUpdated, expected.getDate(),
			expected.getText(), expected.getNotes(), CRE, UPD, DEL);

		EXPECTED = actual;
	}

	private void checkClaim(Claim claim, Claim expected, TransactionKind... txnKinds) {
		checkClaim(claim, expected.getStatus(), expected.getCreated(), expected.getUpdated(), expected.getDate(),
			expected.getText(), expected.getNotes(), txnKinds);
	}

	private void checkClaim(Claim claim, String status, OffsetDateTime earliestCreated, OffsetDateTime earliestUpdated,
		LocalDate date, String text, String notes, TransactionKind... txnKinds) {

		checkTopicalEntity(claim, status, earliestCreated, earliestUpdated, txnKinds);
		assertThat(claim).hasDate(date);
		assertThat(claim).hasText(text);
		assertThat(claim).hasNotes(notes);
	}

	@Override
	EntityKind getEntityKind() {
		return EntityKind.CLA;
	}

}
