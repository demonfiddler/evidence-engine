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

	static boolean hasExpectedPerson() {
		return EXPECTED != null;
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
