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

import static io.github.demonfiddler.ee.client.EntityKind.CLA;
import static io.github.demonfiddler.ee.client.EntityKind.DEC;
import static io.github.demonfiddler.ee.client.EntityKind.PER;
import static io.github.demonfiddler.ee.client.EntityKind.PUB;
import static io.github.demonfiddler.ee.client.EntityKind.QUO;
import static io.github.demonfiddler.ee.client.EntityKind.TOP;
import static io.github.demonfiddler.ee.client.StatusKind.DRA;
import static java.lang.Boolean.TRUE;

import java.util.List;
import java.util.Map;

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

import io.github.demonfiddler.ee.common.util.ArrayUtils;

@SpringBootTest(classes = GraphQLClientMain.class)
@Order(11)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class TopicalEntityTests extends AbstractLinkTests {

    // The various XxxTests have already tested XxxRepository.findByFilter() paged, sorted with asc/desc, null
    // precedence & ignore case and filtered on: status, text
    // TopicalEntityTests needs to do paged, sorted and filtered on: topicId, recursive,
    // masterEntityKind/masterEntityId, status, text

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicalEntityTests.class);

    private static final List<StatusKind> STATUS_FILTER = List.of(DRA);
    private static final String TEXT_FILTER = "filtered";
    private static final EntityKind[] TOPICAL_ENTITY_KINDS = { CLA, DEC, PER, PUB, QUO };
    private static final EntityKind[] MASTER_ENTITY_KINDS = { CLA, DEC, PUB, QUO };
    private static final int[] ALL_ENTITY_INDEXES = { //
        0, // Updated test title
        1, // ENTITY ONE
        2, // Entity two
        3, // ENTITY THREE
        4, // Entity four
        5, // ENTITY FIVE
        6, // Entity six
        7, // ENTITY SEVEN
        8, // Entity eight
    };
    private static final int[] STATUS_INDEXES = { 1, 2, 3, 4, 5, 6, 7, 8 };
    private static final int[] TEXT_INDEXES = { 5, 7, 8 };
    private static final int[] SORTED_INDEXES = { //
        5, // ENTITY FIVE
        1, // ENTITY ONE
        7, // ENTITY SEVEN
        3, // ENTITY THREE
        8, // Entity eight
        4, // Entity four
        6, // Entity six
        2, // Entity two
        0, // Updated test title
    };
    private static final int[] SORTED_IGNORE_CASE_INDEXES = { //
        8, // Entity eight
        5, // ENTITY FIVE
        4, // Entity four
        1, // ENTITY ONE
        7, // ENTITY SEVEN
        6, // Entity six
        3, // ENTITY THREE
        2, // Entity two
        0, // Updated test title
    };
    private static final int[][] TOPIC_ENTITY_REF_INDEXES = { //
        { 0 }, // Updated parent label
        {}, // Updated child label
        { 1, 5 }, // TOPIC ONE
        { 2, 6 }, // Topic two
        { 3, 7 }, // TOPIC THREE
        { 4, 8 }, // Topic four
        { 1, 2 }, // TOPIC FIVE
        { 3, 4 }, // Topic six
        { 5, 6 }, // TOPIC SEVEN
        { 7, 8 }, // Topic eight
    };
    private static final int[][] TOPIC_ENTITY_REF_RECURSIVE_INDEXES = { //
        { 0 }, // Updated parent label
        {}, // Updated child label
        { 1, 2, 3, 4, 5, 6, 7, 8 }, // TOPIC ONE
        { 2, 3, 4, 6, 7, 8 }, // Topic two
        { 3, 4, 7, 8 }, // TOPIC THREE
        { 4, 8 }, // Topic four
        { 1, 2, 3, 4, 5, 6, 7, 8 }, // TOPIC FIVE
        { 3, 4 }, // Topic six
        { 5, 6 }, // TOPIC SEVEN
        { 7, 8 }, // Topic eight
    };
    private static final int[][] MASTER_ENTITY_REF_INDEXES = { //
        {}, // Updated test title
        { 1, 2, 7, 8 }, // ENTITY ONE
        { 2, 3, 6, 7 }, // Entity two
        { 3, 4, 5, 6 }, // ENTITY THREE
        { 1, 4, 5, 8 }, // Entity four
        { 3, 4, 5, 6 }, // ENTITY FIVE
        { 2, 3, 6, 7 }, // Entity six
        { 1, 2, 7, 8 }, // ENTITY SEVEN
        { 1, 2, 7, 8 }, // Entity eight
    };
    private static final SortInput SORT_TEXT =
        SortInput.builder().withOrders(List.of(OrderInput.builder().withProperty("text").build())).build();
    private static final SortInput SORT_TITLE =
        SortInput.builder().withOrders(List.of(OrderInput.builder().withProperty("title").build())).build();
    private static final SortInput SORT_QUALIFICATIONS =
        SortInput.builder().withOrders(List.of(OrderInput.builder().withProperty("qualifications").build())).build();
    private static final Map<EntityKind, SortInput> SORT_BY_ENTITY_KIND =
        Map.of(CLA, SORT_TEXT, DEC, SORT_TITLE, PER, SORT_QUALIFICATIONS, PUB, SORT_TITLE, QUO, SORT_TEXT);

    @BeforeAll
    static void beforeAll() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
        // Need to ensure in-memory presence of the entity instances required by these tests. This is to enable test
        // execution against an already-populated database without having to run the entire integration test suite.
        ClaimTests.ensureExpectedClaims();
        DeclarationTests.ensureExpectedDeclarations();
        PersonTests.ensureExpectedPersons();
        PublicationTests.ensureExpectedPublications();
        QuotationTests.ensureExpectedQuotations();
        TopicTests.ensureExpectedTopics();
        AbstractLinkTests.init();
        EntityLinkTests.forceInit();
    }

    @Test
    @Order(1)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        readEntitiesWithTopic(false, null, null, null, false, false);
    }

    @Test
    @Order(2)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicStatus() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        readEntitiesWithTopic(false, STATUS_FILTER, null, null, false, false);
    }

    @Test
    @Order(3)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicText() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        readEntitiesWithTopic(false, null, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(4)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(5)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursive()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, null, null, false, false);
    }

    @Test
    @Order(6)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, null, null, false, false);
    }

    @Test
    @Order(7)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(8)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(9)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, null, null, false, false);
    }

    @Test
    @Order(10)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, null, null, false, false);
    }

    @Test
    @Order(11)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(12)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(13)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, null, null, false, false);
    }

    @Test
    @Order(14)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, null, null, false, false);
    }

    @Test
    @Order(15)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(16)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(17)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, null, null, false, false);
    }

    @Test
    @Order(18)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, null, null, false, false);
    }

    @Test
    @Order(19)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(20)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesFilteredTopicRecursiveMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, TEXT_FILTER, null, false, false);
    }

    @Test
    @Order(21)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, null, null, null, true, false);
    }

    @Test
    @Order(22)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, null, null, true, false);
    }

    @Test
    @Order(23)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, null, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(24)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(25)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursive()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, null, null, true, false);
    }

    @Test
    @Order(26)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, null, null, true, false);
    }

    @Test
    @Order(27)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(28)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(29)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, null, null, true, false);
    }

    @Test
    @Order(30)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, null, null, true, false);
    }

    @Test
    @Order(31)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(32)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(33)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, null, null, true, false);
    }

    @Test
    @Order(34)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, null, null, true, false);
    }

    @Test
    @Order(35)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(36)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(37)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, null, null, true, false);
    }

    @Test
    @Order(38)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, null, null, true, false);
    }

    @Test
    @Order(39)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(40)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesSortedFilteredTopicRecursiveMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, TEXT_FILTER, null, true, false);
    }

    @Test
    @Order(41)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopic() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        readEntitiesWithTopic(false, null, null, 4, false, false);
    }

    @Test
    @Order(42)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, null, 4, false, false);
    }

    @Test
    @Order(43)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, null, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(44)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(45)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursive()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, null, 4, false, false);
    }

    @Test
    @Order(46)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, null, 4, false, false);
    }

    @Test
    @Order(47)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(48)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(49)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, null, 2, false, false);
    }

    @Test
    @Order(50)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, null, 2, false, false);
    }

    @Test
    @Order(51)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(52)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(53)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, null, 2, false, false);
    }

    @Test
    @Order(54)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, null, 2, false, false);
    }

    @Test
    @Order(55)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(56)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(57)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, null, 4, false, false);
    }

    @Test
    @Order(58)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, null, 4, false, false);
    }

    @Test
    @Order(59)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, TEXT_FILTER, 2, false, false);
    }

    @Test
    @Order(60)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedFilteredTopicRecursiveMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, TEXT_FILTER, 1, false, false);
    }

    @Test
    @Order(61)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopic()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, null, null, 2, true, false);
    }

    @Test
    @Order(62)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, null, 2, true, false);
    }

    @Test
    @Order(63)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, null, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(64)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(false, STATUS_FILTER, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(65)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursive()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, null, 4, true, false);
    }

    @Test
    @Order(66)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, null, 4, true, false);
    }

    @Test
    @Order(67)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, null, TEXT_FILTER, 4, true, false);
    }

    @Test
    @Order(68)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopic(true, STATUS_FILTER, TEXT_FILTER, 4, true, false);
    }

    @Test
    @Order(69)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, null, 2, true, false);
    }

    @Test
    @Order(70)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, null, 2, true, false);
    }

    @Test
    @Order(71)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(null, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(72)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithMasterEntity(STATUS_FILTER, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(73)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, null, 2, true, false);
    }

    @Test
    @Order(74)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, null, 2, true, false);
    }

    @Test
    @Order(75)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, null, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(76)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(false, STATUS_FILTER, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(77)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveMasterEntity()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, null, 1, true, false);
    }

    @Test
    @Order(78)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveMasterEntityStatus()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, null, 4, true, false);
    }

    @Test
    @Order(79)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveMasterEntityText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, null, TEXT_FILTER, 1, true, false);
    }

    @Test
    @Order(80)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLinks")
    void readEntitiesPagedSortedFilteredTopicRecursiveMasterEntityStatusText()
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        readEntitiesWithTopicAndMasterEntity(true, STATUS_FILTER, TEXT_FILTER, 1, true, false);
    }

    private void readEntitiesWithTopic(boolean recursive, List<StatusKind> status, String text, Integer pageSizeOpt,
        boolean sorted, boolean ignoreCase)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        List<Topic> topics = TopicTests.topics;
        for (EntityKind entityKind : TOPICAL_ENTITY_KINDS) {
            for (int i = 2; i < topics.size(); i++) {
                Long topicId = topics.get(i).getId();
                readEntities(entityKind, topicId, recursive, null, null, status, text, pageSizeOpt, sorted, ignoreCase);
            }
        }
    }

    private void readEntitiesWithMasterEntity(List<StatusKind> status, String text, Integer pageSize, boolean sorted,
        boolean ignoreCase) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        for (EntityKind masterEntityKind : MASTER_ENTITY_KINDS) {
            EntityKind[] linkedEntityKinds = getLinkedEntityKinds(masterEntityKind);
            List<? extends IBaseEntity> masterEntities = getEntities(masterEntityKind);
            for (int i = 0; i < masterEntities.size(); i++) {
                Long masterEntityId = masterEntities.get(i).getId();
                for (int j = 0; j < linkedEntityKinds.length; j++) {
                    EntityKind entityKind = linkedEntityKinds[j];
                    readEntities(entityKind, null, null, masterEntityKind, masterEntityId, status, text, pageSize,
                        sorted, ignoreCase);
                }
            }
        }
    }

    private void readEntitiesWithTopicAndMasterEntity(boolean recursive, List<StatusKind> status, String text,
        Integer pageSize, boolean sorted, boolean ignoreCase)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        List<Topic> topics = TopicTests.topics;
        for (int i = 2; i < topics.size(); i++) {
            Long topicId = topics.get(i).getId();
            for (EntityKind masterEntityKind : MASTER_ENTITY_KINDS) {
                EntityKind[] linkedEntityKinds = getLinkedEntityKinds(masterEntityKind);
                List<? extends IBaseEntity> masterEntities = getEntities(masterEntityKind);
                for (int j = 0; j < masterEntities.size(); j++) {
                    Long masterEntityId = masterEntities.get(j).getId();
                    for (int k = 0; k < linkedEntityKinds.length; k++) {
                        EntityKind entityKind = linkedEntityKinds[k];
                        readEntities(entityKind, topicId, recursive, masterEntityKind, masterEntityId, status, text,
                            pageSize, sorted, ignoreCase);
                    }
                }
            }
        }
    }

    private void readEntities(EntityKind entityKind, Long topicId, Boolean recursive, EntityKind masterEntityKind,
        Long masterEntityId, List<StatusKind> status, String text, Integer pageSizeOpt, boolean sorted,
        boolean ignoreCase) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        TopicalEntityQueryFilter filter = topicId == null && recursive == null && masterEntityKind == null
            && masterEntityId == null && status == null && text == null //
                ? null //
                : TopicalEntityQueryFilter.builder() //
                    .withTopicId(topicId) //
                    .withRecursive(recursive) //
                    .withMasterEntityKind(masterEntityKind) //
                    .withMasterEntityId(masterEntityId) //
                    .withStatus(status) //
                    .withText(text) //
                    .build();
        SortInput sort = sorted //
            ? SORT_BY_ENTITY_KIND.get(entityKind) //
            : null;
        PageableInput pageSort = pageSizeOpt == null && sort == null //
            ? null //
            : PageableInput.builder() //
                .withPageSize(pageSizeOpt) //
                .withSort(sort) //
                .build();

        int pageNumber = 0;
        AbstractPage<?> actual;
        do {
            if (pageSort != null)
                pageSort.setPageNumber(pageNumber);

            actual = queryEntities(entityKind, getPagedResponseDef(entityKind), filter, pageSort);
            checkPage(actual, status != null, text != null, topicId, recursive == TRUE, masterEntityKind,
                masterEntityId, entityKind, pageSizeOpt, pageNumber, sorted, ignoreCase);

            pageNumber++;
        } while (actual.getHasNext());
    }

    private String getPagedResponseDef(EntityKind entityKind) {
        String responseDef;
        switch (entityKind) {
            case CLA:
                responseDef = ClaimTests.PAGED_RESPONSE_SPEC;
                break;
            case DEC:
                responseDef = DeclarationTests.PAGED_RESPONSE_SPEC;
                break;
            case PER:
                responseDef = PersonTests.PAGED_RESPONSE_SPEC;
                break;
            case PUB:
                responseDef = PublicationTests.PAGED_RESPONSE_SPEC;
                break;
            case QUO:
                responseDef = QuotationTests.PAGED_RESPONSE_SPEC;
                break;
            default:
                throw new IllegalArgumentException("Unsupported EntityKind: " + entityKind);
        }
        return responseDef.formatted("");
    }

    private void checkPage(AbstractPage<?> actual, boolean status, boolean text, Long topicId, boolean recursive,
        EntityKind masterEntityKind, Long masterEntityId, EntityKind entityKind, Integer pageSizeOpt, int pageNumber,
        boolean sorted, boolean ignoreCase) {

        LOGGER.debug(
            "checkPage(status: {}, text: {}, topicId: {}, recursive: {}, masterEntityKind: {}, masterEntityId: {}, "
                + "entityKind: {}, pageSize: {}, pageNumber: {}, sorted: {}, ignoreCase: {})",
            status, text, topicId, recursive, masterEntityKind, masterEntityId, entityKind, pageSizeOpt, pageNumber,
            sorted, ignoreCase);

        int[] topicLinkedIndexes;
        if (topicId == null) {
            topicLinkedIndexes = ALL_ENTITY_INDEXES;
        } else {
            int topicIndex = getEntityIndex(TOP, topicId);
            topicLinkedIndexes = //
                recursive //
                    ? TOPIC_ENTITY_REF_RECURSIVE_INDEXES[topicIndex] //
                    : TOPIC_ENTITY_REF_INDEXES[topicIndex];
        }
        int[] masterEntityLinkedIndexes = //
            (masterEntityKind == null || masterEntityId == null) //
                ? ALL_ENTITY_INDEXES //
                : MASTER_ENTITY_REF_INDEXES[getEntityIndex(masterEntityKind, masterEntityId)];
        int[] allIndexes = //
            topicLinkedIndexes == masterEntityLinkedIndexes //
                ? ALL_ENTITY_INDEXES //
                : ArrayUtils.intersection(topicLinkedIndexes, masterEntityLinkedIndexes);
        if (status)
            allIndexes = ArrayUtils.intersection(STATUS_INDEXES, allIndexes);
        if (text)
            allIndexes = ArrayUtils.intersection(TEXT_INDEXES, allIndexes);
        if (sorted) {
            int[] sortedIndexes = ignoreCase ? SORTED_IGNORE_CASE_INDEXES : SORTED_INDEXES;
            allIndexes = ArrayUtils.intersection(sortedIndexes, allIndexes);
        }

        int totalElements = allIndexes.length;
        int size = pageSizeOpt == null ? totalElements : pageSizeOpt;
        int totalPages = size == 0 ? 1 : (totalElements / size) + (totalElements % size == 0 ? 0 : 1);
        boolean hasPrevious = pageNumber > 0;
        boolean hasNext = pageNumber + 1 < totalPages;
        boolean isFirst = pageNumber == 0;
        boolean isLast = totalPages == 0 || pageNumber + 1 == totalPages;
        int[] expectedIndexes;
        if (size == totalElements || totalElements == 0) {
            expectedIndexes = allIndexes;
        } else {
            int start = pageNumber * size;
            int count = Math.min(totalElements - start, size);
            expectedIndexes = new int[count];
            System.arraycopy(allIndexes, start, expectedIndexes, 0, count);
        }
        List<?> allEntities = getEntities(entityKind);
        List<?> expected = subList(allEntities, expectedIndexes);
        checkPage(actual, totalElements, totalPages, size, pageNumber, hasPrevious, hasNext, isFirst, isLast, expected,
            sorted);
    }

}
