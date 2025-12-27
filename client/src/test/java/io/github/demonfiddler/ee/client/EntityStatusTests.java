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

import static com.google.common.truth.Truth.assertThat;
import static io.github.demonfiddler.ee.client.StatusKind.PUB;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = GraphQLClientMain.class)
@Order(11)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EntityStatusTests extends AbstractGraphQLTests {

	private static final String MINIMAL_RESPONSE_SPEC = """
        {
            id
            status%s
        }
        """;
    private static final String MINIMAL_PAGED_RESPONSE = """
        {
            size
            content {
                id
                status(format: LONG)
            }
        }
        """;

    @BeforeAll
    static void beforeAll() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        // Need to ensure in-memory presence of the entity instances required by these tests. This is to enable test
        // execution against an already-populated database without having to run the entire integration test suite.
        ClaimTests.ensureExpectedClaims();
        DeclarationTests.ensureExpectedDeclarations();
        JournalTests.ensureExpectedJournals();
        PersonTests.ensureExpectedPersons();
        PublicationTests.ensureExpectedPublications();
        PublisherTests.ensureExpectedPublishers();
        QuotationTests.ensureExpectedQuotations();
        TopicTests.ensureExpectedTopics();
    }

    @Test
    @Order(1)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntities")
    void publishEntities() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        String responseSpec = MINIMAL_RESPONSE_SPEC.formatted("(format: SHORT)");
        for (List<? extends ITrackedEntity> list : TestState.getExpectedTrackedEntities()) {
            for (int i = list == TopicTests.topics ? 2 : 1; i < list.size(); i++) {
                ITrackedEntity entity = list.get(i);
                Long entityId = entity.getId();
                ITrackedEntity result = mutationExecutor.setEntityStatus(responseSpec, entityId, PUB);
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo("PUB");
                entity.setStatus(StatusKind.PUB.label());
            }
        }
    }

    @Test
    @Order(2)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntities")
    void readPublishedEntities() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TrackedEntityQueryFilter trackedEntityFilter = TrackedEntityQueryFilter.builder().withStatus(List.of(PUB)).build();
        LinkableEntityQueryFilter topicalEntityFilter = LinkableEntityQueryFilter.builder().withStatus(List.of(PUB)).build();
        TopicQueryFilter topicQueryFilter = TopicQueryFilter.builder().withStatus(List.of(PUB)).build();

        ClaimPage claimPage = queryExecutor.claims(MINIMAL_PAGED_RESPONSE, topicalEntityFilter, null);
        checkPage(claimPage, ClaimTests.claims.size() - 1, PUB);

        DeclarationPage declarationPage = queryExecutor.declarations(MINIMAL_PAGED_RESPONSE, topicalEntityFilter, null);
        checkPage(declarationPage, DeclarationTests.declarations.size() - 1, PUB);

        JournalPage journalPage = queryExecutor.journals(MINIMAL_PAGED_RESPONSE, trackedEntityFilter, null);
        checkPage(journalPage, JournalTests.journals.size() - 1, PUB);

        PersonPage personPage = queryExecutor.persons(MINIMAL_PAGED_RESPONSE, topicalEntityFilter, null);
        checkPage(personPage, PersonTests.persons.size() - 1, PUB);

        PublicationPage publicationPage = queryExecutor.publications(MINIMAL_PAGED_RESPONSE, topicalEntityFilter, null);
        checkPage(publicationPage, PublicationTests.publications.size() - 1, PUB);

        PublisherPage publisherPage = queryExecutor.publishers(MINIMAL_PAGED_RESPONSE, trackedEntityFilter, null);
        checkPage(publisherPage, PublisherTests.publishers.size() - 1, PUB);

        QuotationPage actual = queryExecutor.quotations(MINIMAL_PAGED_RESPONSE, topicalEntityFilter, null);
        checkPage(actual, QuotationTests.quotations.size() - 1, PUB);

        TopicPage topicPage = queryExecutor.topics(MINIMAL_PAGED_RESPONSE, topicQueryFilter, null);
        checkPage(topicPage, TopicTests.topics.size() - 2, PUB);
    }

    private void checkPage(AbstractPage<? extends ITrackedEntity> actual, int size, StatusKind status) {
        assertThat(actual).hasSize(size);
        assertThat(actual).content().hasSize(size);
        assertThat(actual.getContent().stream().allMatch(c -> c.getStatus() == status.label()));
    }

}
