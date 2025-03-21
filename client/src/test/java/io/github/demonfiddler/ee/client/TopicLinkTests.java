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
import static io.github.demonfiddler.ee.client.EntityKind.CLA;
import static io.github.demonfiddler.ee.client.EntityKind.DEC;
import static io.github.demonfiddler.ee.client.EntityKind.PER;
import static io.github.demonfiddler.ee.client.EntityKind.PUB;
import static io.github.demonfiddler.ee.client.EntityKind.QUO;
import static io.github.demonfiddler.ee.client.EntityKind.TOP;
import static io.github.demonfiddler.ee.client.truth.EntityLinkSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
@TestInstance(Lifecycle.PER_CLASS)
@Order(9)
@TestMethodOrder(OrderAnnotation.class)
class TopicLinkTests extends AbstractGraphQLTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicLinkTests.class);

    private static final String RESPONSE_SPEC = """
        {
            id
            status
            fromEntity {
                ... on IBaseEntity {
                    id
                }
                ... on ITrackedEntity {
                    entityKind
                }
            }
            toEntity {
                ... on IBaseEntity {
                    id
                }
                ... on ITrackedEntity {
                    entityKind
                }
            }
            fromEntityLocations
            toEntityLocations
        }
        """;
    private static final String PAGED_RESPONSE_SPEC = """
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
                status
                fromEntity {
                    ... on IBaseEntity {
                        id
                    }
                    ... on ITrackedEntity {
                        entityKind
                    }
                }
                toEntity {
                    ... on IBaseEntity {
                        id
                    }
                    ... on ITrackedEntity {
                        entityKind
                    }
                }
                fromEntityLocations
                toEntityLocations
            }
        }
        """;

    // These array indexes correspond to indexes of TopicTests.topics.
    // The values in the sub-arrays are the indexes of the entities linked to from the indexed topic.
    // NOTE the potential for confusion: the first two topics, at indexes 0 and 1, are labelled
    // "[updated ]parent label" and "[updated ]child label" respectively. The remaining topics, at index 2 and above,
    // are named "TOPIC ONE" (at index 2), "Topic two" (at index 3), and so on.
    private static final int[][] ENTITY_INDEXES = { //
        { 0 }, //
        {}, //
        { 1, 5 }, //
        { 2, 6 }, //
        { 3, 7 }, //
        { 4, 8 }, //
        { 1, 2 }, //
        { 3, 4 }, //
        { 5, 6 }, //
        { 7, 8 }, //
    };
    // Elements in the following TOPIC_LINK_INDEXES* arrays correspond to links between topics and entities in the order
    // in which the entity links are returned by queries unsorted, sorted, sorted ignoring case, sorted with nulls last,
    // filtered and lastly, filtered and sorted.
    // The values in the sub-arrays are used to build a list of expected topic links. The values are respectively the
    // primary and secondary indexes into the xxxTopicLinks array
    // In general, the ith expected element would be given by:
    // EntityLink[] expected; expected[i] = xxxTopicLinks[TOPIC_LINK_INDEXES[i][0]][TOPIC_LINK_INDEXES[i][1]]
    // For example
    private static final int[][] TOPIC_LINK_INDEXES = { //
        { 0, 0 }, // #updated-claim-location (topic@0 -> entity@0)
        { 1, 0 }, // #TOPIC-1-ENTITY-1
        { 1, 1 }, // #TOPIC-1-ENTITY-5
        { 2, 0 }, // #topic-2-entity-2
        { 2, 1 }, // #topic-2-entity-6
        { 3, 0 }, // null (#topic-3-entity-3)
        { 3, 1 }, // null (#topic-3-entity-7)
        { 4, 0 }, // #topic-4-entity-4
        { 4, 1 }, // #topic-4-entity-8
        { 5, 0 }, // #TOPIC-5-ENTITY-1-filtered
        { 5, 1 }, // #TOPIC-5-ENTITY-2-filtered
        { 6, 0 }, // null (#topic-6-entity-3)
        { 6, 1 }, // null (#topic-6-entity-4)
        { 7, 0 }, // #TOPIC-7-ENTITY-5-filtered
        { 7, 1 }, // #TOPIC-7-ENTITY-6-filtered
        { 8, 0 }, // #topic-8-entity-7-filtered
        { 8, 1 }, // #topic-8-entity-8-filtered
    };
    // Ordered on [toEntityLocations, topicId, entityId]
    private static final int[][] TOPIC_LINK_INDEXES_SORTED = { //
        { 3, 0 }, // null (#topic-3-claim-3)
        { 3, 1 }, // null (#topic-3-claim-7)
        { 6, 0 }, // null (#topic-6-claim-3)
        { 6, 1 }, // null (#topic-6-claim-4)
        { 1, 0 }, // #TOPIC-1-CLAIM-1
        { 1, 1 }, // #TOPIC-1-CLAIM-5
        { 5, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 5, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 7, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 7, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 2, 0 }, // #topic-2-claim-2
        { 2, 1 }, // #topic-2-claim-6
        { 4, 0 }, // #topic-4-claim-4
        { 4, 1 }, // #topic-4-claim-8
        { 8, 0 }, // #topic-8-claim-7-filtered
        { 8, 1 }, // #topic-8-claim-8-filtered
        { 0, 0 }, // #updated-claim-location (topic-0 -> entity-0)
    };
    // Ordered on [toEntityLocations (ignore case), topicId, entityId]
    private static final int[][] TOPIC_LINK_INDEXES_SORTED_IGNORE_CASE = { //
        { 3, 0 }, // null (#topic-3-claim-3)
        { 3, 1 }, // null (#topic-3-claim-7)
        { 6, 0 }, // null (#topic-6-claim-3)
        { 6, 1 }, // null (#topic-6-claim-4)
        { 1, 0 }, // #TOPIC-1-CLAIM-1
        { 1, 1 }, // #TOPIC-1-CLAIM-5
        { 2, 0 }, // #topic-2-claim-2
        { 2, 1 }, // #topic-2-claim-6
        { 4, 0 }, // #topic-4-claim-4
        { 4, 1 }, // #topic-4-claim-8
        { 5, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 5, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 7, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 7, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 8, 0 }, // #topic-8-claim-7-filtered
        { 8, 1 }, // #topic-8-claim-8-filtered
        { 0, 0 }, // #updated-claim-location (topic-0 -> entity-0)
    };
    // Ordered on [toEntityLocations (nulls last), topicId, entityId]
    private static final int[][] TOPIC_LINK_INDEXES_SORTED_NULLS_LAST = { //
        { 1, 0 }, // #TOPIC-1-CLAIM-1
        { 1, 1 }, // #TOPIC-1-CLAIM-5
        { 5, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 5, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 7, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 7, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 2, 0 }, // #topic-2-claim-2
        { 2, 1 }, // #topic-2-claim-6
        { 4, 0 }, // #topic-4-claim-4
        { 4, 1 }, // #topic-4-claim-8
        { 8, 0 }, // #topic-8-claim-7-filtered
        { 8, 1 }, // #topic-8-claim-8-filtered
        { 0, 0 }, // #updated-claim-location (topic-0 -> entity-0)
        { 3, 0 }, // null (#topic-3-claim-3)
        { 3, 1 }, // null (#topic-3-claim-7)
        { 6, 0 }, // null (#topic-6-claim-3)
        { 6, 1 }, // null (#topic-6-claim-4)
    };
    // Ordered on [toEntityLocations (nulls last, ignore case), topicId, entityId]
    private static final int[][] TOPIC_LINK_INDEXES_SORTED_NULLS_LAST_IGNORE_CASE = { //
        { 1, 0 }, // #TOPIC-1-CLAIM-1
        { 1, 1 }, // #TOPIC-1-CLAIM-5
        { 2, 0 }, // #topic-2-claim-2
        { 2, 1 }, // #topic-2-claim-6
        { 4, 0 }, // #topic-4-claim-4
        { 4, 1 }, // #topic-4-claim-8
        { 5, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 5, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 7, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 7, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 8, 0 }, // #topic-8-claim-7-filtered
        { 8, 1 }, // #topic-8-claim-8-filtered
        { 0, 0 }, // #updated-claim-location (topic-0 -> entity-0)
        { 3, 0 }, // null (#topic-3-claim-3)
        { 3, 1 }, // null (#topic-3-claim-7)
        { 6, 0 }, // null (#topic-6-claim-3)
        { 6, 1 }, // null (#topic-6-claim-4)
    };
    // Filtered on text
    private static final int[][] TOPIC_LINK_INDEXES_FILTERED = { //
        { 5, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 5, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 7, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 7, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 8, 0 }, // #topic-8-claim-7-filtered
        { 8, 1 }, // #topic-8-claim-8-filtered
    };
    // Filtered on text, ordered on [toEntityLocations (descending), topicId, entityId]
    private static final int[][] TOPIC_LINK_INDEXES_FILTERED_SORTED = { //
        { 8, 1 }, // #topic-8-claim-8-filtered
        { 8, 0 }, // #topic-8-claim-7-filtered
        { 7, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 7, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 5, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 5, 0 }, // #TOPIC-5-CLAIM-1-filtered
    };

    private static EntityLink claimTopicLink;
    private static EntityLink declarationTopicLink;
    private static EntityLink personTopicLink;
    private static EntityLink publicationTopicLink;
    private static EntityLink quotationTopicLink;

    // Indexed by topic number in creation/id/numerical order.
    private static EntityLink[][] claimTopicLinks;
    private static EntityLink[][] declarationTopicLinks;
    private static EntityLink[][] personTopicLinks;
    private static EntityLink[][] publicationTopicLinks;
    private static EntityLink[][] quotationTopicLinks;

    static boolean hasExpectedTopicLink() {
        return //
        claimTopicLink != null && //
            declarationTopicLink != null && //
            personTopicLink != null && //
            publicationTopicLink != null && //
            quotationTopicLink != null;
    }

    static boolean hasExpectedTopicLinks() {
        return //
        claimTopicLinks != null && //
            declarationTopicLinks != null && //
            personTopicLinks != null && //
            publicationTopicLinks != null && //
            quotationTopicLinks != null;
    }

    static boolean hasExpectedEntityButNoTopicLink() {
        return TestState.hasExpectedEntity() && !hasExpectedTopicLink();
    }

    static boolean hasExpectedEntityButNoTopicLinks() {
        return TestState.hasExpectedEntity() && !hasExpectedTopicLinks();
    }

    static boolean hasExpectedEntitiesButNoTopicLinks() {
        return TestState.hasExpectedEntities() && !hasExpectedTopicLinks();
    }

    private static EntityLink findEntityLink(QueryExecutor queryExecutor, Topic topic, ILinkableEntity entity)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        return queryExecutor.entityLinkByEntityIds(RESPONSE_SPEC, topic.getId(), entity.getId());
    }

    private static EntityLink[][] findEntityLinks(QueryExecutor queryExecutor, List<? extends ILinkableEntity> entities)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        int topicLinkCount = 0;
        for (int i = 0; i < ENTITY_INDEXES.length; i++) {
            if (ENTITY_INDEXES[i].length != 0)
                topicLinkCount++;
        }
        EntityLink[][] actualTopicLinks = new EntityLink[topicLinkCount][];
        for (int i = 0, topicLinkIdx = 0; i < ENTITY_INDEXES.length; i++) {
            int subArrayLen = ENTITY_INDEXES[i].length;
            if (subArrayLen == 0)
                continue;
            Topic topic = TopicTests.topics.get(i);
            actualTopicLinks[topicLinkIdx] = new EntityLink[subArrayLen];
            for (int j = 0; j < subArrayLen; j++) {
                int entityIndex = ENTITY_INDEXES[i][j];
                ILinkableEntity entity = entities.get(entityIndex);
                EntityLink actual = findEntityLink(queryExecutor, topic, entity);
                actualTopicLinks[topicLinkIdx][j] = actual;
            }
            topicLinkIdx++;
        }
        return actualTopicLinks;
    }

    static void init() throws GraphQLRequestPreparationException {
        if (!hasExpectedTopicLink() || !hasExpectedTopicLinks()) {
            QueryExecutor queryExecutor = SpringContext.getApplicationContext().getBean(QueryExecutor.class);
            if (!hasExpectedTopicLinks()) {
                try {
                    claimTopicLinks = findEntityLinks(queryExecutor, ClaimTests.claims);
                    declarationTopicLinks = findEntityLinks(queryExecutor, DeclarationTests.declarations);
                    personTopicLinks = findEntityLinks(queryExecutor, PersonTests.persons);
                    publicationTopicLinks = findEntityLinks(queryExecutor, PublicationTests.publications);
                    quotationTopicLinks = findEntityLinks(queryExecutor, QuotationTests.quotations);
                } catch (GraphQLRequestExecutionException _) {
                }
                if (hasExpectedTopicLinks() && !hasExpectedTopicLink()) {
                    claimTopicLink = claimTopicLinks[0][0];
                    declarationTopicLink = declarationTopicLinks[0][0];
                    personTopicLink = personTopicLinks[0][0];
                    publicationTopicLink = publicationTopicLinks[0][0];
                    quotationTopicLink = quotationTopicLinks[0][0];
                }

                if (!hasExpectedTopicLink() || !hasExpectedTopicLinks()) {
                    // We didn't succeed in initialising all topic links, so clear them all.
                    claimTopicLink = declarationTopicLink = personTopicLink = //
                        publicationTopicLink = quotationTopicLink = null;
                    claimTopicLinks = declarationTopicLinks = personTopicLinks = //
                        publicationTopicLinks = quotationTopicLinks = null;

                    LOGGER.debug("Topic links not found on server");
                } else {
                    LOGGER.debug("Fetched topic links from server");
                }
            }
        }
    }

    @BeforeAll
    void beforeAll() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        assertThat(authenticator.login()).isTrue();

        // Need to ensure in-memory presence of the entity instances required by these tests. This is to enable test
        // execution against an already-populated database without having to run the entire integration test suite.
        ClaimTests.ensureExpectedClaims();
        DeclarationTests.ensureExpectedDeclarations();
        PersonTests.ensureExpectedPersons();
        PublicationTests.ensureExpectedPublications();
        QuotationTests.ensureExpectedQuotations();
        TopicTests.ensureExpectedTopics();
        init();
    }

    @Test
    @Order(1)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedEntityButNoTopicLink")
    void createTopicLink() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        Topic topic = TopicTests.parentTopic;
        EntityLink claimTopicLink = createTopicLink(topic, ClaimTests.claim, "#claim-location");
        EntityLink declarationTopicLink = createTopicLink(topic, DeclarationTests.declaration, "#declaration-location");
        EntityLink personTopicLink = createTopicLink(topic, PersonTests.person, "#person-location");
        EntityLink publicationTopicLink = createTopicLink(topic, PublicationTests.publication, "#publication-location");
        EntityLink quotationTopicLink = createTopicLink(topic, QuotationTests.quotation, "#quotation-location");

        TopicLinkTests.claimTopicLink = claimTopicLink;
        TopicLinkTests.declarationTopicLink = declarationTopicLink;
        TopicLinkTests.personTopicLink = personTopicLink;
        TopicLinkTests.publicationTopicLink = publicationTopicLink;
        TopicLinkTests.quotationTopicLink = quotationTopicLink;
    }

    @Test
    @Order(2)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedEntityButNoTopicLinks")
    void readTopicLink() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();

        filter.setToEntityKind(CLA);
        List<EntityLink> expected = List.of(claimTopicLink);
        EntityLinkPage actual = queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null);
        // readTopicLinks(filter, null, expected, false);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setToEntityKind(DEC);
        expected = List.of(declarationTopicLink);
        actual = queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setToEntityKind(PER);
        expected = List.of(personTopicLink);
        actual = queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setToEntityKind(PUB);
        expected = List.of(publicationTopicLink);
        actual = queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setToEntityKind(QUO);
        expected = List.of(quotationTopicLink);
        actual = queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);
    }

    @Test
    @Order(3)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLink")
    void updateTopicLink() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLink claimTopicLink = TopicLinkTests.claimTopicLink;
        EntityLink declarationTopicLink = TopicLinkTests.declarationTopicLink;
        EntityLink personTopicLink = TopicLinkTests.personTopicLink;
        EntityLink publicationTopicLink = TopicLinkTests.publicationTopicLink;
        EntityLink quotationTopicLink = TopicLinkTests.quotationTopicLink;

        TopicLinkTests.claimTopicLink = null;
        TopicLinkTests.declarationTopicLink = null;
        TopicLinkTests.personTopicLink = null;
        TopicLinkTests.publicationTopicLink = null;
        TopicLinkTests.quotationTopicLink = null;

        Topic topic = TopicTests.parentTopic;
        EntityLink actualClaimTopicLink =
            mutateTopicLink(claimTopicLink.getId(), topic, ClaimTests.claim, "#updated-claim-location");
        EntityLink actualDeclarationTopicLink = mutateTopicLink(declarationTopicLink.getId(), topic,
            DeclarationTests.declaration, "#updated-declaration-location");
        EntityLink actualPersonTopicLink =
            mutateTopicLink(personTopicLink.getId(), topic, PersonTests.person, "#updated-person-location");
        EntityLink actualPublicationTopicLink = mutateTopicLink(publicationTopicLink.getId(), topic,
            PublicationTests.publication, "#updated-publication-location");
        EntityLink actualQuotationTopicLink =
            mutateTopicLink(quotationTopicLink.getId(), topic, QuotationTests.quotation, "#updated-quotation-location");

        TopicLinkTests.claimTopicLink = actualClaimTopicLink;
        TopicLinkTests.declarationTopicLink = actualDeclarationTopicLink;
        TopicLinkTests.personTopicLink = actualPersonTopicLink;
        TopicLinkTests.publicationTopicLink = actualPublicationTopicLink;
        TopicLinkTests.quotationTopicLink = actualQuotationTopicLink;
    }

    @Test
    @Order(4)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLink")
    void deleteTopicLink() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLink claimTopicLink = TopicLinkTests.claimTopicLink;
        EntityLink declarationTopicLink = TopicLinkTests.declarationTopicLink;
        EntityLink personTopicLink = TopicLinkTests.personTopicLink;
        EntityLink publicationTopicLink = TopicLinkTests.publicationTopicLink;
        EntityLink quotationTopicLink = TopicLinkTests.quotationTopicLink;

        TopicLinkTests.claimTopicLink = null;
        TopicLinkTests.declarationTopicLink = null;
        TopicLinkTests.personTopicLink = null;
        TopicLinkTests.publicationTopicLink = null;
        TopicLinkTests.quotationTopicLink = null;

        String deletedStatus = StatusKind.DEL.label();
        claimTopicLink = mutationExecutor.deleteEntityLink(RESPONSE_SPEC, claimTopicLink.getId());
        assertThat(claimTopicLink).hasStatus(deletedStatus);
        declarationTopicLink = mutationExecutor.deleteEntityLink(RESPONSE_SPEC, declarationTopicLink.getId());
        assertThat(declarationTopicLink).hasStatus(deletedStatus);
        personTopicLink = mutationExecutor.deleteEntityLink(RESPONSE_SPEC, personTopicLink.getId());
        assertThat(personTopicLink).hasStatus(deletedStatus);
        publicationTopicLink = mutationExecutor.deleteEntityLink(RESPONSE_SPEC, publicationTopicLink.getId());
        assertThat(publicationTopicLink).hasStatus(deletedStatus);
        quotationTopicLink = mutationExecutor.deleteEntityLink(RESPONSE_SPEC, quotationTopicLink.getId());
        assertThat(quotationTopicLink).hasStatus(deletedStatus);

        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .withStatus(List.of(StatusKind.DRA, StatusKind.PUB, StatusKind.SUS)) //
            .build();
        filter.setToEntityKind(CLA);
        assertThat(queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setToEntityKind(DEC);
        assertThat(queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setToEntityKind(PER);
        assertThat(queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setToEntityKind(PUB);
        assertThat(queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setToEntityKind(QUO);
        assertThat(queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();

        TopicLinkTests.claimTopicLink = claimTopicLink;
        TopicLinkTests.declarationTopicLink = declarationTopicLink;
        TopicLinkTests.personTopicLink = personTopicLink;
        TopicLinkTests.publicationTopicLink = publicationTopicLink;
        TopicLinkTests.quotationTopicLink = quotationTopicLink;
    }

    @Test
    @Order(5)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedEntitiesButNoTopicLinks")
    void createTopicLinks() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLink[][] claimTopicLinks = createTopicLinks(ClaimTests.claims, claimTopicLink);
        EntityLink[][] declarationTopicLinks = createTopicLinks(DeclarationTests.declarations, declarationTopicLink);
        EntityLink[][] personTopicLinks = createTopicLinks(PersonTests.persons, personTopicLink);
        EntityLink[][] publicationTopicLinks = createTopicLinks(PublicationTests.publications, publicationTopicLink);
        EntityLink[][] quotationTopicLinks = createTopicLinks(QuotationTests.quotations, quotationTopicLink);

        TopicLinkTests.claimTopicLinks = claimTopicLinks;
        TopicLinkTests.declarationTopicLinks = declarationTopicLinks;
        TopicLinkTests.personTopicLinks = personTopicLinks;
        TopicLinkTests.publicationTopicLinks = publicationTopicLinks;
        TopicLinkTests.quotationTopicLinks = quotationTopicLinks;
    }

    @Test
    @Order(6)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinks() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();
        readTopicLinksForEachEntityKind(filter, null, TOPIC_LINK_INDEXES);
    }

    @Test
    @Order(7)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksFilteredByTopicId() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = new EntityLinkQueryFilter();

        // Reads and checks TopicLinks for all topics and entity kinds.
        for (int i = 0, topicLinkIdx = 0; i < ENTITY_INDEXES.length; i++) {
            if (ENTITY_INDEXES[i].length == 0)
                continue;

            filter.setFromEntityId(TopicTests.topics.get(i).getId());

            filter.setToEntityKind(CLA);
            readTopicLinks(filter, claimTopicLinks[topicLinkIdx]);

            filter.setToEntityKind(DEC);
            readTopicLinks(filter, declarationTopicLinks[topicLinkIdx]);

            filter.setToEntityKind(PER);
            readTopicLinks(filter, personTopicLinks[topicLinkIdx]);

            filter.setToEntityKind(PUB);
            readTopicLinks(filter, publicationTopicLinks[topicLinkIdx]);

            filter.setToEntityKind(QUO);
            readTopicLinks(filter, quotationTopicLinks[topicLinkIdx]);

            topicLinkIdx++;
        }
    }

    @Test
    @Order(8)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksFilteredByEntityId()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();

        // Reads and checks topic entity links for all entity kinds and entityIds.
        filter.setToEntityKind(CLA);
        readTopicLinksFilteredByEntityId(filter, claimTopicLinks, ClaimTests.claims);

        filter.setToEntityKind(DEC);
        readTopicLinksFilteredByEntityId(filter, declarationTopicLinks, DeclarationTests.declarations);

        filter.setToEntityKind(PER);
        readTopicLinksFilteredByEntityId(filter, personTopicLinks, PersonTests.persons);

        filter.setToEntityKind(PUB);
        readTopicLinksFilteredByEntityId(filter, publicationTopicLinks, PublicationTests.publications);

        filter.setToEntityKind(QUO);
        readTopicLinksFilteredByEntityId(filter, quotationTopicLinks, QuotationTests.quotations);
    }

    @Test
    @Order(9)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksFilteredByText() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .withText("filtered") //
            .build();

        readTopicLinksForEachEntityKind(filter, null, TOPIC_LINK_INDEXES_FILTERED);
    }

    @Test
    @Order(10)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();
        OrderInput locationsOrder = OrderInput.builder().withProperty("to_entity_locations").build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("from_entity_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("to_entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        int[][] topicLinkIndexes = CASE_INSENSITIVE ? TOPIC_LINK_INDEXES_SORTED_IGNORE_CASE : TOPIC_LINK_INDEXES_SORTED;
        readTopicLinksForEachEntityKind(filter, pageSort, topicLinkIndexes);
    }

    @Test
    @Order(11)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksSortedIgnoreCase() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();
        OrderInput locationsOrder =
            OrderInput.builder().withProperty("to_entity_locations").withIgnoreCase(true).build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("from_entity_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("to_entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        readTopicLinksForEachEntityKind(filter, pageSort, TOPIC_LINK_INDEXES_SORTED_IGNORE_CASE);
    }

    @Test
    @Order(12)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksSortedNullHandling()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();
        OrderInput locationsOrder = OrderInput.builder().withProperty("to_entity_locations")
            .withNullHandling(NullHandlingKind.NULLS_LAST).build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("from_entity_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("to_entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        int[][] topicLinkIndexes = CASE_INSENSITIVE //
            ? TOPIC_LINK_INDEXES_SORTED_NULLS_LAST_IGNORE_CASE //
            : TOPIC_LINK_INDEXES_SORTED_NULLS_LAST;
        readTopicLinksForEachEntityKind(filter, pageSort, topicLinkIndexes);
    }

    @Test
    @Order(13)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksFilteredSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .withText("filtered") //
            .build();
        OrderInput locationsOrder =
            OrderInput.builder().withProperty("to_entity_locations").withDirection(DirectionKind.DESC).build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("from_entity_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("to_entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        readTopicLinksForEachEntityKind(filter, pageSort, TOPIC_LINK_INDEXES_FILTERED_SORTED);
    }

    @Test
    @Order(14)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksPaged() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();
        int pageSize = 8;
        PageableInput pageSort = PageableInput.builder() //
            .withPageSize(pageSize) //
            .build();

        int totalPages = totalPages(TOPIC_LINK_INDEXES, pageSize);
        for (int i = 0; i < totalPages; i++) {
            pageSort.setPageNumber(i);

            readTopicLinksForEachEntityKind(filter, pageSort, TOPIC_LINK_INDEXES);
        }
    }

    @Test
    @Order(15)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksPagedSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .build();
        OrderInput locationsOrder = OrderInput.builder().withProperty("to_entity_locations").build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("from_entity_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("to_entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        int pageSize = 8;
        PageableInput pageSort = PageableInput.builder() //
            .withPageSize(pageSize) //
            .withSort(sort) //
            .build();

        int[][] topicLinkIndexes = CASE_INSENSITIVE ? TOPIC_LINK_INDEXES_SORTED_IGNORE_CASE : TOPIC_LINK_INDEXES_SORTED;
        int totalPages = totalPages(topicLinkIndexes, pageSize);
        for (int i = 0; i < totalPages; i++) {
            pageSort.setPageNumber(i);

            readTopicLinksForEachEntityKind(filter, pageSort, topicLinkIndexes);
        }
    }

    @Test
    @Order(16)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicLinkTests#hasExpectedTopicLinks")
    void readTopicLinksPagedFilteredSorted()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        EntityLinkQueryFilter filter = EntityLinkQueryFilter.builder() //
            .withFromEntityKind(TOP) //
            .withText("filtered") //
            .build();
        OrderInput locationsOrder = OrderInput.builder().withProperty("to_entity_locations").build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("from_entity_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("to_entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        int pageSize = 3;
        PageableInput pageSort = PageableInput.builder() //
            .withPageSize(pageSize) //
            .withSort(sort) //
            .build();

        int totalPages = totalPages(TOPIC_LINK_INDEXES_FILTERED, pageSize);
        for (int i = 0; i < totalPages; i++) {
            pageSort.setPageNumber(i);

            readTopicLinksForEachEntityKind(filter, pageSort, TOPIC_LINK_INDEXES_FILTERED);
        }
    }

    private int totalPages(int[][] array, int pageSize) {
        return array.length / pageSize + (array.length % pageSize == 0 ? 0 : 1);
    }

    private <T extends ILinkableEntity> EntityLink[][] createTopicLinks(List<T> entities, EntityLink entityLink0)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        int topicLinkCount = 0;
        for (int i = 0; i < ENTITY_INDEXES.length; i++) {
            if (ENTITY_INDEXES[i].length != 0)
                topicLinkCount++;
        }
        EntityLink[][] topicLinks = new EntityLink[topicLinkCount][];
        topicLinks[0] = new EntityLink[] { entityLink0 };
        for (int i = 2, topicLinkIdx = 1, topicLabelNum = 1; i < ENTITY_INDEXES.length; i++) {
            int subArrayLen = ENTITY_INDEXES[i].length;
            if (subArrayLen == 0)
                continue;
            Topic topic = TopicTests.topics.get(i);
            topicLinks[topicLinkIdx] = new EntityLink[subArrayLen];
            for (int j = 0; j < subArrayLen; j++) {
                int entityIndex = ENTITY_INDEXES[i][j];
                T entity = entities.get(entityIndex);
                String toEntityLocations = "#topic-" + topicLabelNum + '-' + getEntityName(entity) + '-' + entityIndex;
                if (topicLabelNum % 2 != 0)
                    toEntityLocations = toEntityLocations.toUpperCase();
                if (topicLabelNum % 3 == 0)
                    toEntityLocations = null;
                else if (topicLabelNum > 4)
                    toEntityLocations += "-filtered";
                EntityLink actual = createTopicLink(topic, entity, toEntityLocations);
                checkTopicLink(null, topic.getId(), entity.getId(), getEntityKind(entity), toEntityLocations, actual);

                topicLinks[topicLinkIdx][j] = actual;
            }
            topicLinkIdx++;
            topicLabelNum++;
        }
        return topicLinks;
    }

    private <T extends ILinkableEntity> EntityLink createTopicLink(Topic topic, T entity, String toEntityLocations)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        return mutateTopicLink(null, topic, entity, toEntityLocations);
    }

    private <T extends ILinkableEntity> EntityLink mutateTopicLink(Long id, Topic topic, T entity,
        String toEntityLocations) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        Long topicId = topic.getId();
        Long entityId = entity.getId();
        EntityKind entityKind = getEntityKind(entity);
        EntityLinkInput topicLinkInput = EntityLinkInput.builder() //
            .withId(id) //
            .withFromEntityId(topicId) //
            .withToEntityId(entityId) //
            .withToEntityLocations(toEntityLocations) //
            .build();
        EntityLink actual = id == null //
            ? mutationExecutor.createEntityLink(RESPONSE_SPEC, topicLinkInput)
            : mutationExecutor.updateEntityLink(RESPONSE_SPEC, topicLinkInput);

        checkTopicLink(id, topicId, entityId, entityKind, toEntityLocations, actual);

        return actual;
    }

    private void checkTopicLink(Long id, Long topicId, Long entityId, EntityKind entityKind, String locations,
        EntityLink actual) {

        if (id == null)
            assertThat(actual).id().isNotNull();
        else
            assertThat(actual).hasId(id);
        assertThat(actual).fromEntity().hasId(topicId);
        assertThat(actual).toEntity().hasId(entityId);
        assertThat(actual).toEntity().hasEntityKind(entityKind.label());
        assertThat(actual).hasToEntityLocations(locations);
    }

    private void readTopicLinksForEachEntityKind(EntityLinkQueryFilter filter, PageableInput pageSort,
        int[][] topicLinkIndexes) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        filter.setToEntityKind(CLA);
        readTopicLinks(filter, pageSort, claimTopicLinks, topicLinkIndexes);

        filter.setToEntityKind(DEC);
        readTopicLinks(filter, pageSort, declarationTopicLinks, topicLinkIndexes);

        filter.setToEntityKind(PER);
        readTopicLinks(filter, pageSort, personTopicLinks, topicLinkIndexes);

        filter.setToEntityKind(PUB);
        readTopicLinks(filter, pageSort, publicationTopicLinks, topicLinkIndexes);

        filter.setToEntityKind(QUO);
        readTopicLinks(filter, pageSort, quotationTopicLinks, topicLinkIndexes);
    }

    private void readTopicLinks(EntityLinkQueryFilter filter, PageableInput pageSort, EntityLink[][] topicLinks,
        int[][] topicLinkIdxsByEntityIdx) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        int pageSize = pageSort == null || pageSort.getPageSize() == null ? 0 : pageSort.getPageSize();
        int pageNumber = pageSort == null || pageSort.getPageNumber() == null ? 0 : pageSort.getPageNumber();
        int[][] topicLinkIdxsByEntityIdxPage;
        if (pageSize == 0) {
            topicLinkIdxsByEntityIdxPage = topicLinkIdxsByEntityIdx;
            pageSize = topicLinkIdxsByEntityIdxPage.length;
        } else {
            int startIndex = pageNumber * pageSize;
            int numberOfElements = Math.min(topicLinkIdxsByEntityIdx.length - startIndex, pageSize);
            topicLinkIdxsByEntityIdxPage = new int[numberOfElements][];
            System.arraycopy(topicLinkIdxsByEntityIdx, startIndex, topicLinkIdxsByEntityIdxPage, 0, numberOfElements);
        }
        int totalElements = topicLinkIdxsByEntityIdx.length;
        int totalPages = totalPages(topicLinkIdxsByEntityIdx, pageSize);
        boolean hasPrevious = pageNumber > 0;
        boolean hasNext = pageNumber + 1 < totalPages;
        boolean isFirst = pageNumber == 0;
        boolean isLast = pageNumber + 1 == totalPages;
        List<EntityLink> expected = subList(topicLinks, topicLinkIdxsByEntityIdxPage);
        boolean sorted = pageSort != null && pageSort.getSort() != null && !pageSort.getSort().getOrders().isEmpty();
        readTopicLinks(filter, pageSort, totalElements, totalPages, hasPrevious, hasNext, isFirst, isLast, expected,
            sorted);
    }

    private List<EntityLink> subList(EntityLink[][] topicLinks, int[][] topicLinkIdxs) {
        List<EntityLink> expected = new ArrayList<>();
        for (int i = 0; i < topicLinkIdxs.length; i++) {
            int[] topicLinkIdx = topicLinkIdxs[i];
            if (topicLinkIdx.length == 2)
                expected.add(topicLinks[topicLinkIdx[0]][topicLinkIdx[1]]);
        }
        return expected;
    }

    private <T extends IBaseEntity> void readTopicLinksFilteredByEntityId(EntityLinkQueryFilter filter,
        EntityLink[][] topicLinks, List<T> entities)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        // Indexes into xxxTopicLinks by entity index. For example:
        // XxxTest.xxxs[0] is referenced by: xxxTopicLinks[0][0]
        // XxxTest.xxxs[1] is referenced by: xxxTopicLinks[1][0] & xxxTopicLinks[5][0]
        // XxxTest.xxxs[2] is referenced by: xxxTopicLinks[2][0] & xxxTopicLinks[5][1]
        // XxxTest.xxxs[3] is referenced by: xxxTopicLinks[3][0] & xxxTopicLinks[6][0]
        // XxxTest.xxxs[4] is referenced by: xxxTopicLinks[4][0] & xxxTopicLinks[6][1]
        // XxxTest.xxxs[5] is referenced by: xxxTopicLinks[1][1] & xxxTopicLinks[7][0]
        // XxxTest.xxxs[6] is referenced by: xxxTopicLinks[2][1] & xxxTopicLinks[7][1]
        // XxxTest.xxxs[7] is referenced by: xxxTopicLinks[3][1] & xxxTopicLinks[8][0]
        // XxxTest.xxxs[8] is referenced by: xxxTopicLinks[4][1] & xxxTopicLinks[8][1]
        int[][][] topicLinkIdxsByEntityIdx = { //
            { { 0, 0 } }, //
            { { 1, 0 }, { 5, 0 } }, //
            { { 2, 0 }, { 5, 1 } }, //
            { { 3, 0 }, { 6, 0 } }, //
            { { 4, 0 }, { 6, 1 } }, //
            { { 1, 1 }, { 7, 0 } }, //
            { { 2, 1 }, { 7, 1 } }, //
            { { 3, 1 }, { 8, 0 } }, //
            { { 4, 1 }, { 8, 1 } }, //
        };
        for (int i = 0; i < entities.size(); i++) {
            Long entityId = entities.get(i).getId();
            filter.setToEntityId(entityId);
            List<EntityLink> expected = subList(topicLinks, topicLinkIdxsByEntityIdx[i]);
            readTopicLinks(filter, null, expected, false);
        }
    }

    private void readTopicLinks(EntityLinkQueryFilter filter, EntityLink[] expected)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readTopicLinks(filter, null, List.of(expected), false);
    }

    private void readTopicLinks(EntityLinkQueryFilter filter, PageableInput pageSort, List<EntityLink> expected,
        boolean checkOrder) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readTopicLinks(filter, pageSort, expected.size(), 1, false, false, true, true, expected, checkOrder);
    }

    private void readTopicLinks(EntityLinkQueryFilter filter, PageableInput pageSort, int totalElements, int totalPages,
        boolean hasPrevious, boolean hasNext, boolean isFirst, boolean isLast, List<EntityLink> expected,
        boolean checkOrder) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        EntityLinkPage actual = queryExecutor.entityLinks(PAGED_RESPONSE_SPEC, filter, pageSort);

        int pageSize = pageSort == null || pageSort.getPageSize() == null ? totalElements : pageSort.getPageSize();
        int pageNumber = pageSort == null || pageSort.getPageNumber() == null ? 0 : pageSort.getPageNumber();
        checkPage(actual, totalElements, totalPages, pageSize, pageNumber, hasPrevious, hasNext, isFirst, isLast,
            expected, checkOrder);
    }

}
