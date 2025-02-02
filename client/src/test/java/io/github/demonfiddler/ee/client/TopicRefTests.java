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
import static io.github.demonfiddler.ee.client.FormatKind.SHORT;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.TopicRefSubject.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = GraphQLClientMain.class)
@Order(9)
@TestMethodOrder(OrderAnnotation.class)
class TopicRefTests extends AbstractGraphQLTests {

    static boolean hasExpectedTopicRef() {
        return //
        claimTopicRef != null && //
            declarationTopicRef != null && //
            personTopicRef != null && //
            publicationTopicRef != null && //
            quotationTopicRef != null;
    }

    static boolean hasExpectedTopicRefs() {
        return //
        claimTopicRefs != null && //
            declarationTopicRefs != null && //
            personTopicRefs != null && //
            publicationTopicRefs != null && //
            quotationTopicRefs != null;
    }

    private static final String RESPONSE_SPEC = //
        """
        {
            id
            topicId
            entityId
            entityKind(format: SHORT)
            locations
        }
        """;
    private static final String PAGED_RESPONSE_SPEC = //
        """
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
                topicId
                entityId
                entityKind(format: SHORT)
                locations
            }
        }
        """;

    // Indexed by topic number in creation/id/numerical order.
    private static final int[][] ENTITY_INDEXES =
        new int[][] { { 1, 5 }, { 2, 6 }, { 3, 7 }, { 4, 8 }, { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 } };
    private static final int[][] TOPIC_REF_INDEXES = { //
        { 0, 0 }, // #TOPIC-1-CLAIM-1
        { 0, 1 }, // #TOPIC-1-CLAIM-5
        { 1, 0 }, // #topic-2-claim-2
        { 1, 1 }, // #topic-2-claim-6
        { 2, 0 }, // null (#topic-3-claim-3)
        { 2, 1 }, // null (#topic-3-claim-7)
        { 3, 0 }, // #topic-4-claim-4
        { 3, 1 }, // #topic-4-claim-8
        { 4, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 4, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 5, 0 }, // null (#topic-6-claim-3)
        { 5, 1 }, // null (#topic-6-claim-4)
        { 6, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 6, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 7, 0 }, // #topic-8-claim-7-filtered
        { 7, 1 }, // #topic-8-claim-8-filtered
    };
    // Ordered on [locations, topicId, entityId]
    private static final int[][] TOPIC_REF_INDEXES_SORTED = { //
        { 2, 0 }, // null (#topic-3-claim-3)
        { 2, 1 }, // null (#topic-3-claim-7)
        { 5, 0 }, // null (#topic-6-claim-3)
        { 5, 1 }, // null (#topic-6-claim-4)
        { 0, 0 }, // #TOPIC-1-CLAIM-1
        { 0, 1 }, // #TOPIC-1-CLAIM-5
        { 4, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 4, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 6, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 6, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 1, 0 }, // #topic-2-claim-2
        { 1, 1 }, // #topic-2-claim-6
        { 3, 0 }, // #topic-4-claim-4
        { 3, 1 }, // #topic-4-claim-8
        { 7, 0 }, // #topic-8-claim-7-filtered
        { 7, 1 }, // #topic-8-claim-8-filtered
    };
    // Ordered on [locations (ignore case), topicId, entityId]
    private static final int[][] TOPIC_REF_INDEXES_SORTED_IGNORE_CASE = { //
        { 2, 0 }, // null (#topic-3-claim-3)
        { 2, 1 }, // null (#topic-3-claim-7)
        { 5, 0 }, // null (#topic-6-claim-3)
        { 5, 1 }, // null (#topic-6-claim-4)
        { 0, 0 }, // #TOPIC-1-CLAIM-1
        { 0, 1 }, // #TOPIC-1-CLAIM-5
        { 1, 0 }, // #topic-2-claim-2
        { 1, 1 }, // #topic-2-claim-6
        { 3, 0 }, // #topic-4-claim-4
        { 3, 1 }, // #topic-4-claim-8
        { 4, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 4, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 6, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 6, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 7, 0 }, // #topic-8-claim-7-filtered
        { 7, 1 }, // #topic-8-claim-8-filtered
    };
    // Ordered on [locations (nulls last), topicId, entityId]
    private static final int[][] TOPIC_REF_INDEXES_SORTED_NULLS_LAST = { //
        { 0, 0 }, // #TOPIC-1-CLAIM-1
        { 0, 1 }, // #TOPIC-1-CLAIM-5
        { 4, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 4, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 6, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 6, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 1, 0 }, // #topic-2-claim-2
        { 1, 1 }, // #topic-2-claim-6
        { 3, 0 }, // #topic-4-claim-4
        { 3, 1 }, // #topic-4-claim-8
        { 7, 0 }, // #topic-8-claim-7-filtered
        { 7, 1 }, // #topic-8-claim-8-filtered
        { 2, 0 }, // null (#topic-3-claim-3)
        { 2, 1 }, // null (#topic-3-claim-7)
        { 5, 0 }, // null (#topic-6-claim-3)
        { 5, 1 }, // null (#topic-6-claim-4)
    };
    // Filtered on text
    private static final int[][] TOPIC_REF_INDEXES_FILTERED = { //
        { 4, 0 }, // #TOPIC-5-CLAIM-1-filtered
        { 4, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 6, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 6, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 7, 0 }, // #topic-8-claim-7-filtered
        { 7, 1 }, // #topic-8-claim-8-filtered
    };
    // Filtered on text, ordered on [locations (descending), topicId, entityId]
    private static final int[][] TOPIC_REF_INDEXES_FILTERED_SORTED = { //
        { 7, 1 }, // #topic-8-claim-8-filtered
        { 7, 0 }, // #topic-8-claim-7-filtered
        { 6, 1 }, // #TOPIC-7-CLAIM-6-filtered
        { 6, 0 }, // #TOPIC-7-CLAIM-5-filtered
        { 4, 1 }, // #TOPIC-5-CLAIM-2-filtered
        { 4, 0 }, // #TOPIC-5-CLAIM-1-filtered
    };

    private static TopicRef claimTopicRef;
    private static TopicRef declarationTopicRef;
    private static TopicRef personTopicRef;
    private static TopicRef publicationTopicRef;
    private static TopicRef quotationTopicRef;

    // Indexed by topic number in creation/id/numerical order.
    private static TopicRef[][] claimTopicRefs;
    private static TopicRef[][] declarationTopicRefs;
    private static TopicRef[][] personTopicRefs;
    private static TopicRef[][] publicationTopicRefs;
    private static TopicRef[][] quotationTopicRefs;

    @Test
    @Order(1)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntity")
    void createTopicRef() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        Topic topic = TopicTests.parentTopic;
        TopicRef claimTopicRef = addTopicRef(topic, ClaimTests.claim, "#claim-location");
        TopicRef declarationTopicRef = addTopicRef(topic, DeclarationTests.declaration, "#declaration-location");
        TopicRef personTopicRef = addTopicRef(topic, PersonTests.person, "#person-location");
        TopicRef publicationTopicRef = addTopicRef(topic, PublicationTests.publication, "#publication-location");
        TopicRef quotationTopicRef = addTopicRef(topic, QuotationTests.quotation, "#quotation-location");

        TopicRefTests.claimTopicRef = claimTopicRef;
        TopicRefTests.declarationTopicRef = declarationTopicRef;
        TopicRefTests.personTopicRef = personTopicRef;
        TopicRefTests.publicationTopicRef = publicationTopicRef;
        TopicRefTests.quotationTopicRef = quotationTopicRef;
    }

    @Test
    @Order(2)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRef")
    void readTopicRef() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRefQueryFilter filter = TopicRefQueryFilter.builder().withEntityKind(CLA).build();
        List<TopicRef> expected = List.of(claimTopicRef);
        TopicRefPage actual;
        readTopicRefs(filter, null, expected, false);

        filter.setEntityKind(DEC);
        expected = List.of(declarationTopicRef);
        actual = queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setEntityKind(PER);
        expected = List.of(personTopicRef);
        actual = queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setEntityKind(PUB);
        expected = List.of(publicationTopicRef);
        actual = queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);

        filter.setEntityKind(QUO);
        expected = List.of(quotationTopicRef);
        actual = queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null);
        checkPage(actual, expected.size(), 1, expected.size(), 0, false, false, true, true, expected, false);
    }

    @Test
    @Order(3)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRef")
    void updateTopicRef() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRef claimTopicRef = TopicRefTests.claimTopicRef;
        TopicRef declarationTopicRef = TopicRefTests.declarationTopicRef;
        TopicRef personTopicRef = TopicRefTests.personTopicRef;
        TopicRef publicationTopicRef = TopicRefTests.publicationTopicRef;
        TopicRef quotationTopicRef = TopicRefTests.quotationTopicRef;

        TopicRefTests.claimTopicRef = null;
        TopicRefTests.declarationTopicRef = null;
        TopicRefTests.personTopicRef = null;
        TopicRefTests.publicationTopicRef = null;
        TopicRefTests.quotationTopicRef = null;

        Topic topic = TopicTests.parentTopic;
        TopicRef actualClaimTopicRef =
            mutateTopicRef(claimTopicRef.getId(), topic, ClaimTests.claim, "#updated-claim-location");
        TopicRef actualDeclarationTopicRef = mutateTopicRef(declarationTopicRef.getId(), topic,
            DeclarationTests.declaration, "#updated-declaration-location");
        TopicRef actualPersonTopicRef =
            mutateTopicRef(personTopicRef.getId(), topic, PersonTests.person, "#updated-person-location");
        TopicRef actualPublicationTopicRef = mutateTopicRef(publicationTopicRef.getId(), topic,
            PublicationTests.publication, "#updated-publication-location");
        TopicRef actualQuotationTopicRef =
            mutateTopicRef(quotationTopicRef.getId(), topic, QuotationTests.quotation, "#updated-quotation-location");

        TopicRefTests.claimTopicRef = actualClaimTopicRef;
        TopicRefTests.declarationTopicRef = actualDeclarationTopicRef;
        TopicRefTests.personTopicRef = actualPersonTopicRef;
        TopicRefTests.publicationTopicRef = actualPublicationTopicRef;
        TopicRefTests.quotationTopicRef = actualQuotationTopicRef;
    }

    @Test
    @Order(4)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRef")
    void deleteTopicRef() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRef claimTopicRef = TopicRefTests.claimTopicRef;
        TopicRef declarationTopicRef = TopicRefTests.declarationTopicRef;
        TopicRef personTopicRef = TopicRefTests.personTopicRef;
        TopicRef publicationTopicRef = TopicRefTests.publicationTopicRef;
        TopicRef quotationTopicRef = TopicRefTests.quotationTopicRef;

        TopicRefTests.claimTopicRef = null;
        TopicRefTests.declarationTopicRef = null;
        TopicRefTests.personTopicRef = null;
        TopicRefTests.publicationTopicRef = null;
        TopicRefTests.quotationTopicRef = null;

        TopicRefInput topicRefInput = new TopicRefInput();

        assertThat(mutationExecutor.removeTopicRef("", copyFields(topicRefInput, claimTopicRef))).isTrue();
        topicRefInput.setId(declarationTopicRef.getId());
        assertThat(mutationExecutor.removeTopicRef("", copyFields(topicRefInput, declarationTopicRef))).isTrue();
        topicRefInput.setId(personTopicRef.getId());
        assertThat(mutationExecutor.removeTopicRef("", copyFields(topicRefInput, personTopicRef))).isTrue();
        topicRefInput.setId(publicationTopicRef.getId());
        assertThat(mutationExecutor.removeTopicRef("", copyFields(topicRefInput, publicationTopicRef))).isTrue();
        topicRefInput.setId(quotationTopicRef.getId());
        assertThat(mutationExecutor.removeTopicRef("", copyFields(topicRefInput, quotationTopicRef))).isTrue();

        TopicRefQueryFilter filter = TopicRefQueryFilter.builder().withEntityKind(CLA).build();
        assertThat(queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setEntityKind(DEC);
        assertThat(queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setEntityKind(PER);
        assertThat(queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setEntityKind(PUB);
        assertThat(queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
        filter.setEntityKind(QUO);
        assertThat(queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, null)).isEmpty();
    }

    @Test
    @Order(5)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntities")
    void createTopicRefs() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRef[][] claimTopicRefs = createTopicRefs(ClaimTests.claims);
        TopicRef[][] declarationTopicRefs = createTopicRefs(DeclarationTests.declarations);
        TopicRef[][] personTopicRefs = createTopicRefs(PersonTests.persons);
        TopicRef[][] publicationTopicRefs = createTopicRefs(PublicationTests.publications);
        TopicRef[][] quotationTopicRefs = createTopicRefs(QuotationTests.quotations);

        TopicRefTests.claimTopicRefs = claimTopicRefs;
        TopicRefTests.declarationTopicRefs = declarationTopicRefs;
        TopicRefTests.personTopicRefs = personTopicRefs;
        TopicRefTests.publicationTopicRefs = publicationTopicRefs;
        TopicRefTests.quotationTopicRefs = quotationTopicRefs;
    }

    @Test
    @Order(6)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefs() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        readTopicRefsForEachEntityKind(filter, null, TOPIC_REF_INDEXES);
    }

    @Test
    @Order(7)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsFilteredByTopicId() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = new TopicRefQueryFilter();

        // Reads and checks TopicRefs for all entity kinds and topics.
        for (int i = 0; i < ENTITY_INDEXES.length; i++) {
            filter.setTopicId(TopicTests.topics.get(i + 2).getId());

            filter.setEntityKind(CLA);
            readTopicRefs(filter, claimTopicRefs[i]);

            filter.setEntityKind(DEC);
            readTopicRefs(filter, declarationTopicRefs[i]);

            filter.setEntityKind(PER);
            readTopicRefs(filter, personTopicRefs[i]);

            filter.setEntityKind(PUB);
            readTopicRefs(filter, publicationTopicRefs[i]);

            filter.setEntityKind(QUO);
            readTopicRefs(filter, quotationTopicRefs[i]);
        }
    }

    @Test
    @Order(8)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsFilteredByEntityId() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = new TopicRefQueryFilter();

        // Reads and checks TopicRefs for all entity kinds and entityIds.
        filter.setEntityKind(CLA);
        readTopicRefsFilteredByEntityId(filter, claimTopicRefs, ClaimTests.claims);

        filter.setEntityKind(DEC);
        readTopicRefsFilteredByEntityId(filter, declarationTopicRefs, DeclarationTests.declarations);

        filter.setEntityKind(PER);
        readTopicRefsFilteredByEntityId(filter, personTopicRefs, PersonTests.persons);

        filter.setEntityKind(PUB);
        readTopicRefsFilteredByEntityId(filter, publicationTopicRefs, PublicationTests.publications);

        filter.setEntityKind(QUO);
        readTopicRefsFilteredByEntityId(filter, quotationTopicRefs, QuotationTests.quotations);
    }

    @Test
    @Order(9)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsFilteredByText() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        filter.setText("filtered");

        readTopicRefsForEachEntityKind(filter, null, TOPIC_REF_INDEXES_FILTERED);
    }

    @Test
    @Order(10)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        OrderInput locationsOrder = OrderInput.builder().withProperty("locations").build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("topic_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES_SORTED);
    }

    @Test
    @Order(11)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsSortedIgnoreCase() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        OrderInput locationsOrder = OrderInput.builder().withProperty("locations").withIgnoreCase(true).build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("topic_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES_SORTED_IGNORE_CASE);
    }

    @Test
    @Order(12)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsSortedNullHandling() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        OrderInput locationsOrder =
            OrderInput.builder().withProperty("locations").withNullHandling(NullHandlingKind.NULLS_LAST).build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("topic_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES_SORTED_NULLS_LAST);
    }

    @Test
    @Order(13)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsFilteredSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        TopicRefQueryFilter filter = TopicRefQueryFilter.builder().withText("filtered").build();
        OrderInput locationsOrder =
            OrderInput.builder().withProperty("locations").withDirection(DirectionKind.DESC).build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("topic_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder().withSort(sort).build();

        readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES_FILTERED_SORTED);
    }

    @Test
    @Order(14)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsPaged() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        PageableInput pageSort = PageableInput.builder() //
            .withPageSize(8) //
            .build();

        for (int i = 0; i < 2; i++) {
            pageSort.setPageNumber(i);

            readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES);
        }
    }

    @Test
    @Order(15)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsPagedSorted() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = new TopicRefQueryFilter();
        OrderInput locationsOrder = OrderInput.builder().withProperty("locations").build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("topic_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder() //
            .withPageSize(8) //
            .withSort(sort) //
            .build();

        for (int i = 0; i < 2; i++) {
            pageSort.setPageNumber(i);

            readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES_SORTED);
        }
    }

    @Test
    @Order(16)
    @EnabledIf("io.github.demonfiddler.ee.client.TopicRefTests#hasExpectedTopicRefs")
    void readTopicRefsPagedFilteredSorted()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefQueryFilter filter = TopicRefQueryFilter.builder().withText("filtered").build();
        OrderInput locationsOrder = OrderInput.builder().withProperty("locations").build();
        OrderInput topicIdOrder = OrderInput.builder().withProperty("topic_id").build();
        OrderInput entityIdOrder = OrderInput.builder().withProperty("entity_id").build();
        List<OrderInput> orders = List.of(locationsOrder, topicIdOrder, entityIdOrder);
        SortInput sort = SortInput.builder().withOrders(orders).build();
        PageableInput pageSort = PageableInput.builder() //
            .withPageSize(3) //
            .withSort(sort) //
            .build();

        for (int i = 0; i < 2; i++) {
            pageSort.setPageNumber(i);

            readTopicRefsForEachEntityKind(filter, pageSort, TOPIC_REF_INDEXES_FILTERED);
        }
    }

    private <T extends ITopicalEntity> TopicRef[][] createTopicRefs(List<T> entities)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        int topicCount = ENTITY_INDEXES.length;
        // List<TopicRef> actualTopicRefs = new ArrayList<>();
        TopicRef[][] actualTopicRefs = new TopicRef[topicCount][2];
        for (int i = 0; i < topicCount; i++) {
            Topic topic = TopicTests.topics.get(i + 2);
            for (int j = 0; j < 2; j++) {
                int entityIndex = ENTITY_INDEXES[i][j];
                T entity = entities.get(entityIndex);
                String locations = "#topic-" + (i + 1) + '-' + getEntityName(entity) + '-' + entityIndex;
                if ((i + 1) % 2 != 0)
                    locations = locations.toUpperCase();
                if ((i + 1) % 3 == 0)
                    locations = null;
                else if (i > 3)
                    locations += "-filtered";
                TopicRef actual = addTopicRef(topic, entity, locations);
                checkTopicRef(null, topic.getId(), entity.getId(), getEntityKind(entity), locations, actual);

                // actualTopicRefs.add(actual);
                actualTopicRefs[i][j] = actual;
            }
        }
        return actualTopicRefs;
    }

    private <T extends ITopicalEntity> TopicRef addTopicRef(Topic topic, T entity,
        String locations) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        return mutateTopicRef(null, topic, entity, locations);
    }

    private <T extends ITopicalEntity> TopicRef mutateTopicRef(Long id, Topic topic,
        T entity, String locations) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        Long topicId = topic.getId();
        Long entityId = entity.getId();
        EntityKind entityKind = getEntityKind(entity);
        TopicRefInput topicRefInput = TopicRefInput.builder() //
            .withId(id) //
            .withTopicId(topicId) //
            .withEntityId(entityId) //
            .withEntityKind(entityKind) //
            .withLocations(locations).build();
        TopicRef actual = id == null //
            ? mutationExecutor.addTopicRef(RESPONSE_SPEC, topicRefInput)
            : mutationExecutor.updateTopicRef(RESPONSE_SPEC, topicRefInput);

        checkTopicRef(id, topicId, entityId, entityKind, locations, actual);

        return actual;
    }

    private void checkTopicRef(Long id, Long topicId, Long entityId, EntityKind entityKind, String locations,
        TopicRef actual) {
        if (id == null)
            assertThat(actual).id().isNotNull();
        else
            assertThat(actual).hasId(id);
        assertThat(actual).hasTopicId(topicId);
        assertThat(actual).hasEntityId(entityId);
        assertThat(actual).hasEntityKind(entityKind, SHORT);
        assertThat(actual).hasLocations(locations);
    }

    private TopicRefInput copyFields(TopicRefInput topicRefInput, TopicRef topicRef) {
        topicRefInput.setId(topicRef.getId());
        topicRefInput.setTopicId(topicRef.getTopicId());
        topicRefInput.setEntityId(topicRef.getEntityId());
        topicRefInput.setEntityKind(EntityKind.valueOf(topicRef.getEntityKind()));
        return topicRefInput;
    }

    private void readTopicRefsForEachEntityKind(TopicRefQueryFilter filter, PageableInput pageSort,
        int[][] topicRefIndexes) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        filter.setEntityKind(CLA);
        readTopicRefs(filter, pageSort, claimTopicRefs, topicRefIndexes);

        filter.setEntityKind(DEC);
        readTopicRefs(filter, pageSort, declarationTopicRefs, topicRefIndexes);

        filter.setEntityKind(PER);
        readTopicRefs(filter, pageSort, personTopicRefs, topicRefIndexes);

        filter.setEntityKind(PUB);
        readTopicRefs(filter, pageSort, publicationTopicRefs, topicRefIndexes);

        filter.setEntityKind(QUO);
        readTopicRefs(filter, pageSort, quotationTopicRefs, topicRefIndexes);
    }

    private void readTopicRefs(TopicRefQueryFilter filter, PageableInput pageSort, TopicRef[][] topicRefs,
        int[][] topicRefIdxsByEntityIdx) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        int pageSize = pageSort == null || pageSort.getPageSize() == null ? 0 : pageSort.getPageSize();
        int pageNumber = pageSort == null || pageSort.getPageNumber() == null ? 0 : pageSort.getPageNumber();
        int[][] topicRefIdxsByEntityIdxPage;
        if (pageSize == 0) {
            topicRefIdxsByEntityIdxPage = topicRefIdxsByEntityIdx;
            pageSize = topicRefIdxsByEntityIdxPage.length;
        } else {
            // N.B. this code only works if last page is full.
            topicRefIdxsByEntityIdxPage = new int[pageSize][2];
            int startIndex = pageNumber * pageSize;
            System.arraycopy(topicRefIdxsByEntityIdx, startIndex, topicRefIdxsByEntityIdxPage, 0, pageSize);
        }
        int totalElements = topicRefIdxsByEntityIdx.length;
        int totalPages = totalElements / pageSize; // N.B. only works if last page is full.
        boolean hasPrevious = pageNumber > 0;
        boolean hasNext = pageNumber + 1 < totalPages;
        boolean isFirst = pageNumber == 0;
        boolean isLast = pageNumber + 1 == totalPages;
        List<TopicRef> expected = subList(topicRefs, topicRefIdxsByEntityIdxPage);
        readTopicRefs(filter, pageSort, totalElements, totalPages, hasPrevious, hasNext, isFirst, isLast, expected,
            true);
    }

    private List<TopicRef> subList(TopicRef[][] topicRefs, int[][] indexes) {
        List<TopicRef> expected = new ArrayList<>();
        for (int i = 0; i < indexes.length; i++)
            expected.add(topicRefs[indexes[i][0]][indexes[i][1]]);
        return expected;
    }

    private <T extends IBaseEntity> void readTopicRefsFilteredByEntityId(TopicRefQueryFilter filter,
        TopicRef[][] topicRefs, List<T> entities)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        // Indexes into xxxTopicRefs by entity index - 1.
        // For example, the topic refs which reference:
        // ClaimTest.claims[1] is referenced by: claimTopicRefs[0][0], claimTopicRefs[4][0]
        // ClaimTest.claims[2] is referenced by: claimTopicRefs[1][0], claimTopicRefs[4][1]
        // ClaimTest.claims[3] is referenced by: claimTopicRefs[2][0], claimTopicRefs[5][0]
        // ClaimTest.claims[4] is referenced by: claimTopicRefs[3][0], claimTopicRefs[5][1]
        // ClaimTest.claims[5] is referenced by: claimTopicRefs[0][1], claimTopicRefs[6][0]
        // ClaimTest.claims[6] is referenced by: claimTopicRefs[1][1], claimTopicRefs[6][1]
        // ClaimTest.claims[7] is referenced by: claimTopicRefs[2][1], claimTopicRefs[7][0]
        // ClaimTest.claims[8] is referenced by: claimTopicRefs[3][1], claimTopicRefs[7][1]
        int[][][] topicRefIdxsByEntityIdx = { //
            { { 0, 0 }, { 4, 0 } }, //
            { { 1, 0 }, { 4, 1 } }, //
            { { 2, 0 }, { 5, 0 } }, //
            { { 3, 0 }, { 5, 1 } }, //
            { { 0, 1 }, { 6, 0 } }, //
            { { 1, 1 }, { 6, 1 } }, //
            { { 2, 1 }, { 7, 0 } }, //
            { { 3, 1 }, { 7, 1 } }, //
        };
        for (int i = 1; i < entities.size(); i++) {
            Long entityId = entities.get(i).getId();
            filter.setEntityId(entityId);
            List<TopicRef> expected = subList(topicRefs, topicRefIdxsByEntityIdx[i - 1]);
            readTopicRefs(filter, null, expected, false);
        }
    }

    private void readTopicRefs(TopicRefQueryFilter filter, TopicRef[] expected)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readTopicRefs(filter, null, List.of(expected), false);
    }

    private void readTopicRefs(TopicRefQueryFilter filter, PageableInput pageSort, List<TopicRef> expected,
        boolean checkOrder) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readTopicRefs(filter, pageSort, expected.size(), 1, false, false, true, true, expected, checkOrder);
    }

    private void readTopicRefs(TopicRefQueryFilter filter, PageableInput pageSort, int totalElements, int totalPages,
        boolean hasPrevious, boolean hasNext, boolean isFirst, boolean isLast, List<TopicRef> expected,
        boolean checkOrder) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicRefPage actual = queryExecutor.topicRefs(PAGED_RESPONSE_SPEC, filter, pageSort);

        int pageSize = pageSort == null || pageSort.getPageSize() == null ? totalElements : pageSort.getPageSize();
        int pageNumber = pageSort == null || pageSort.getPageNumber() == null ? 0 : pageSort.getPageNumber();
        checkPage(actual, totalElements, totalPages, pageSize, pageNumber, hasPrevious, hasNext, isFirst, isLast,
            expected, checkOrder);
    }

}
