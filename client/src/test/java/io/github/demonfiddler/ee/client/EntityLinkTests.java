/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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
import static io.github.demonfiddler.ee.client.truth.EntityLinkSubject.assertThat;
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = GraphQLClientMain.class)
@TestInstance(Lifecycle.PER_CLASS)
@Order(10)
@TestMethodOrder(OrderAnnotation.class)
class EntityLinkTests extends AbstractLinkTests {

    private static final String MINIMAL_RESPONSE = """
        {
            id
            status(format: SHORT)
            fromEntity {
                ... on IBaseEntity {
                    id
                }
            }
            fromEntityLocations
            toEntity {
                ... on IBaseEntity {
                    id
                }
            }
            toEntityLocations
        }
        """;
    private static final String MINIMAL_PAGED_RESPONSE = """
        {
            number
            size
            totalElements
            content {
                id
            }
        }
        """;
    // Possible master entity links are as follows:
    // claim_declaration (m:n)
    // claim_person (m:n)
    // claim_publication (m:n)
    // claim_quotation (m:n)
    // declaration_person (m:n)
    // declaration_quotation (1:n)
    // publication_person (m:n)
    // quotation_person (n:1)
    // quotation_publication (n:1)
    private static final EntityKind[][] LINKED_ENTITY_KIND = { //
        { CLA, DEC, PER, PUB, QUO }, //
        { DEC, PER, QUO }, //
        { PER }, //
        { PUB, PER }, //
        { QUO, PER, PUB }, //
    };
    static final Map<EntityKind, List<EntityLink>> ENTITY_LINK_MAP = new LinkedHashMap<>();
    private static Boolean hasExpectedLink;
    private static Boolean hasExpectedLinks;

    static boolean hasExpectedLink() {
        return hasExpectedLink != null && hasExpectedLink;
    }

    static boolean hasExpectedLinks() {
        return hasExpectedLinks != null && hasExpectedLinks;
    }

    /** Sets the {@code hasExpectedLink(s)} flags. Only called when {@code LinkableEntityTests) executes on its own. */
    static void forceInit() {
        if (hasExpectedLink == null && hasExpectedLinks == null)
            hasExpectedLink = hasExpectedLinks = true;
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
        AbstractLinkTests.init();
        init();
    }

    @Test
    @Order(1)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntity")
    void createEntityLink() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkInput input = new EntityLinkInput();
        for (int i = 0; i < LINKED_ENTITY_KIND.length; i++) {
            EntityKind fromEntityKind = LINKED_ENTITY_KIND[i][0];
            input.setFromEntityId(ENTITY[i].getId());
            input.setFromEntityLocations(fromEntityKind.label() + "-locations");
            List<EntityLink> entityLinks = ENTITY_LINK_MAP.computeIfAbsent(fromEntityKind, _ -> new ArrayList<>());
            for (int j = 1; j < LINKED_ENTITY_KIND[i].length; j++) {
                EntityKind toEntityKind = LINKED_ENTITY_KIND[i][j];
                ILinkableEntity toEntity = ENTITY_MAP.get(toEntityKind);
                input.setToEntityId(toEntity.getId());
                input.setToEntityLocations(toEntityKind.label() + "-locations");

                logger.trace("Link {} #0 to {} #0", fromEntityKind.label(), toEntityKind.label());

                try {
                    EntityLink result = mutationExecutor.createEntityLink(MINIMAL_RESPONSE, input);
                    assertThat(result).hasStatus(StatusKind.DRA.name());
                    assertThat(result).fromEntity().hasId(input.getFromEntityId());
                    assertThat(result).hasFromEntityLocations(input.getFromEntityLocations());
                    assertThat(result).toEntity().hasId(input.getToEntityId());
                    assertThat(result).hasToEntityLocations(input.getToEntityLocations());

                    entityLinks.add(result);
                } catch (Exception e) {
                    hasExpectedLink = false;
                    throw e;
                }
            }
        }

        hasExpectedLink = true;
    }

    @Test
    @Order(2)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLink")
    void readLinkedEntity() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        LinkableEntityQueryFilter filter = new LinkableEntityQueryFilter();
        for (int i = 0; i < LINKED_ENTITY_KIND.length; i++) {
            filter.setFromEntityKind(LINKED_ENTITY_KIND[i][0]);
            filter.setFromEntityId(ENTITY[i].getId());
            for (int j = 1; j < LINKED_ENTITY_KIND[i].length; j++) {
                EntityKind toEntityKind = LINKED_ENTITY_KIND[i][j];
                ILinkableEntity toEntity = ENTITY_MAP.get(toEntityKind);

                try {
                    AbstractPage<? extends IBaseEntity> actual = queryEntities(toEntityKind, MINIMAL_PAGED_RESPONSE, filter, null);
                    assertThat(actual).hasNumber(0);
                    assertThat(actual).hasSize(1);
                    assertThat(actual).hasTotalElements(1);
                    assertThat(actual.getContent()).hasSize(1);
                    assertThat(actual.getContent().get(0).getId()).isEqualTo(toEntity.getId());
                } catch (Exception e) {
                    hasExpectedLink = false;
                    throw e;
                }
            }
        }
        // TODO: find entities by their ID.
    }

    @Test
    @Order(3)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLink")
    void deleteEntityLink() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        for (int i = 0; i < LINKED_ENTITY_KIND.length; i++) {
            EntityKind fromEntityKind = LINKED_ENTITY_KIND[i][0];
            Long fromEntityId = ENTITY[i].getId();
            List<EntityLink> entityLinks = ENTITY_LINK_MAP.get(fromEntityKind);
            for (int j = 1; j < LINKED_ENTITY_KIND[i].length; j++) {
                EntityKind toEntityKind = LINKED_ENTITY_KIND[i][j];
                ILinkableEntity toEntity = ENTITY_MAP.get(toEntityKind);
                Long toEntityId = toEntity.getId();

                logger.trace("Unlink {} #0 from {} #0", toEntityKind.label(), fromEntityKind.label());

                try {
                    EntityLink entityLink = entityLinks.get(j - 1);
                    EntityLink result = mutationExecutor.deleteEntityLink(MINIMAL_RESPONSE, entityLink.getId());
                    assertThat(result).hasStatus(StatusKind.DEL.name());
                    assertThat(result).fromEntity().hasId(fromEntityId);
                    assertThat(result).hasFromEntityLocations(entityLink.getFromEntityLocations());
                    assertThat(result).toEntity().hasId(toEntityId);
                    assertThat(result).hasToEntityLocations(entityLink.getToEntityLocations());

                    LinkableEntityQueryFilter filter = LinkableEntityQueryFilter.builder() //
                        .withFromEntityKind(fromEntityKind) //
                        .withFromEntityId(fromEntityId) //
                        .withStatus(List.of(StatusKind.DRA, StatusKind.PUB, StatusKind.SUS)) //
                        .build();
                    AbstractPage<?> actual = queryEntities(toEntityKind, MINIMAL_PAGED_RESPONSE, filter, null);
                    assertThat(actual).hasNumber(0);
                    assertThat(actual).hasSize(0);
                    assertThat(actual).hasTotalElements(0);
                    assertThat(actual).content().isEmpty();
                } catch (Exception e) {
                    hasExpectedLink = false;
                    throw e;
                }
            }
        }
    }

    @Test
    @Order(4)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntities")
    void createEntityLinks() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        EntityLinkInput input = new EntityLinkInput();
        for (int h = 0; h < LINKED_ENTITY_KIND.length; h++) {
            EntityKind[] entityLinkKinds = LINKED_ENTITY_KIND[h];
            EntityKind fromEntityKind = entityLinkKinds[0];
            // input.setFromEntityKind(fromEntityKind);

            List<? extends ILinkableEntity> fromEntities = ENTITIES.get(h);
            for (int i = 1; i < fromEntities.size(); i++) {
                ILinkableEntity fromEntity = fromEntities.get(i);
                input.setFromEntityId(((IBaseEntity)fromEntity).getId());

                for (int j = 1; j < entityLinkKinds.length; j++) {
                    EntityKind toEntityKind = entityLinkKinds[j];
                    // input.setToEntityKind(toEntityKind);

                    List<? extends ILinkableEntity> toEntities = getEntities(toEntityKind);
                    int[] entityLinkIndexes = ENTITY_LINK_INDEXES[i];
                    for (int k = 0; k < entityLinkIndexes.length; k++) {
                        int toEntityIndex = entityLinkIndexes[k];
                        ILinkableEntity toEntity = toEntities.get(toEntityIndex);
                        input.setToEntityId(((IBaseEntity)toEntity).getId());

                        logger.trace("Link {} #{} to {} #{}", fromEntityKind.label(), i, toEntityKind.label(),
                            toEntityIndex);

                        EntityLink result = mutationExecutor.createEntityLink(MINIMAL_RESPONSE, input);
                        assertThat(result).hasStatus(StatusKind.DRA.name());
                        assertThat(result).fromEntity().hasId(input.getFromEntityId());
                        assertThat(result).hasFromEntityLocations(input.getFromEntityLocations());
                        assertThat(result).toEntity().hasId(input.getToEntityId());
                        assertThat(result).hasToEntityLocations(input.getToEntityLocations());
                    }
                }
            }
        }
        hasExpectedLinks = true;
    }

}
