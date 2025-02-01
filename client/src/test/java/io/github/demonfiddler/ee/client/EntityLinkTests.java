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
import static io.github.demonfiddler.ee.client.truth.PageSubject.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@SpringBootTest(classes = GraphQLClientMain.class)
@Order(10)
@TestMethodOrder(OrderAnnotation.class)
class EntityLinkTests extends AbstractLinkTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityLinkTests.class);

    private static final String MINIMAL_PAGED_RESPONSE = //
        """
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
    // claim_declaration
    // claim_person
    // claim_publication
    // claim_quotation
    // declaration_person
    // declaration_quotation
    // publication_person
    // quotation_person
    // quotation_publication
    private static final EntityKind[][] LINKED_ENTITY_KIND = { //
        { CLA, DEC, PER, PUB, QUO }, //
        { DEC, PER, QUO }, //
        { PER }, //
        { PUB, PER }, //
        { QUO, PER, PUB }, //
    };
    private static Boolean hasExpectedLink;
    private static Boolean hasExpectedLinks;

    static boolean hasExpectedLink() {
        return hasExpectedLink;
    }

    static boolean hasExpectedLinks() {
        return hasExpectedLinks;
    }

    /** Sets the {@code hasExpectedLink(s)} flags. Only called when {@code TopicalEntityTests) executes on its own. */
    static void forceInit() {
        if (hasExpectedLink == null && hasExpectedLinks == null)
            hasExpectedLink = hasExpectedLinks = true;
    }

    @BeforeAll
    static void beforeAll() {
        AbstractLinkTests.init();
    }

    @Test
    @Order(1)
    @EnabledIf("io.github.demonfiddler.ee.client.TestState#hasExpectedEntity")
    void linkEntity() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        LinkEntitiesInput input = new LinkEntitiesInput();
        for (int i = 0; i < LINKED_ENTITY_KIND.length; i++) {
            EntityKind fromEntityKind = LINKED_ENTITY_KIND[i][0];
            input.setFromEntityKind(fromEntityKind);
            input.setFromEntityId(((IBaseEntity)ENTITY[i]).getId());
            for (int j = 1; j < LINKED_ENTITY_KIND[i].length; j++) {
                EntityKind toEntityKind = LINKED_ENTITY_KIND[i][j];
                ITopicalEntity toEntity = ENTITY_MAP.get(toEntityKind);
                input.setToEntityKind(toEntityKind);
                input.setToEntityId(((IBaseEntity)toEntity).getId());

                LOGGER.debug("Link {} #0 to {} #0", fromEntityKind.label(), toEntityKind.label());

                try {
                    Boolean result = mutationExecutor.linkEntities("", input);
                    assertThat(result).isTrue();
    
                    // Second call doesn't work, because the integrity constraint violation throws an exception.
                    // result = mutationExecutor.linkEntities("", input);
                    // assertWithMessage("Second link attempt").that(result).isFalse();
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
        TopicalEntityQueryFilter filter = new TopicalEntityQueryFilter();
        for (int i = 0; i < LINKED_ENTITY_KIND.length; i++) {
            filter.setMasterEntityKind(LINKED_ENTITY_KIND[i][0]);
            filter.setMasterEntityId(((IBaseEntity)ENTITY[i]).getId());
            for (int j = 1; j < LINKED_ENTITY_KIND[i].length; j++) {
                EntityKind toEntityKind = LINKED_ENTITY_KIND[i][j];
                ITopicalEntity toEntity = ENTITY_MAP.get(toEntityKind);

                try {
                    AbstractPage<?> actual = queryEntities(toEntityKind, MINIMAL_PAGED_RESPONSE, filter, null);
                    assertThat(actual).hasNumber(0);
                    assertThat(actual).hasSize(1);
                    assertThat(actual).hasTotalElements(1);
                    assertThat(actual.getContent()).hasSize(1);
                    assertThat(actual.getContent().get(0).getId()).isEqualTo(((IBaseEntity)toEntity).getId());
                } catch (Exception e) {
                    hasExpectedLink = false;
                    throw e;
                }
            }
        }
    }

    @Test
    @Order(3)
    @EnabledIf("io.github.demonfiddler.ee.client.EntityLinkTests#hasExpectedLink")
    void unlinkEntity() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        LinkEntitiesInput input = new LinkEntitiesInput();
        for (int i = 0; i < LINKED_ENTITY_KIND.length; i++) {
            EntityKind fromEntityKind = LINKED_ENTITY_KIND[i][0];
            Long fromEntityId = ((IBaseEntity)ENTITY[i]).getId();
            input.setFromEntityKind(fromEntityKind);
            input.setFromEntityId(fromEntityId);
            for (int j = 1; j < LINKED_ENTITY_KIND[i].length; j++) {
                EntityKind toEntityKind = LINKED_ENTITY_KIND[i][j];
                ITopicalEntity toEntity = ENTITY_MAP.get(toEntityKind);
                input.setToEntityKind(toEntityKind);
                input.setToEntityId(((IBaseEntity)toEntity).getId());

                LOGGER.debug("Unlink {} #0 to {} #0", fromEntityKind.label(), toEntityKind.label());

                try {
                    Boolean result = mutationExecutor.unlinkEntities("", input);
                    assertThat(result).isTrue();

                    result = mutationExecutor.unlinkEntities("", input);
                    assertThat(result).isFalse();

                    TopicalEntityQueryFilter filter = TopicalEntityQueryFilter.builder() //
                        .withMasterEntityKind(fromEntityKind) //
                        .withMasterEntityId(fromEntityId) //
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
    void linkEntities() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        LinkEntitiesInput input = new LinkEntitiesInput();
        for (int h = 0; h < LINKED_ENTITY_KIND.length; h++) {
            EntityKind[] entityLinkKinds = LINKED_ENTITY_KIND[h];
            EntityKind fromEntityKind = entityLinkKinds[0];
            input.setFromEntityKind(fromEntityKind);

            List<? extends ITopicalEntity> fromEntities = ENTITIES.get(h);
            for (int i = 1; i < fromEntities.size(); i++) {
                ITopicalEntity fromEntity = fromEntities.get(i);
                input.setFromEntityId(((IBaseEntity)fromEntity).getId());

                for (int j = 1; j < entityLinkKinds.length; j++) {
                    EntityKind toEntityKind = entityLinkKinds[j];
                    input.setToEntityKind(toEntityKind);

                    List<? extends ITopicalEntity> toEntities = getEntities(toEntityKind);
                    int[] entityLinkIndexes = ENTITY_LINK_INDEXES[i];
                    for (int k = 0; k < entityLinkIndexes.length; k++) {
                        int toEntityIndex = entityLinkIndexes[k];
                        ITopicalEntity toEntity = toEntities.get(toEntityIndex);
                        input.setToEntityId(((IBaseEntity)toEntity).getId());

                        LOGGER.debug("Link {} #{} to {} #{}", fromEntityKind.label(), i, toEntityKind.label(),
                            toEntityIndex);

                        Boolean result = mutationExecutor.linkEntities("", input);
                        try {
                            assertThat(result).isTrue();
                        } catch (Exception e) {
                            hasExpectedLinks = false;
                            throw e;
                        }
                    }
                }
            }
        }
        hasExpectedLinks = true;
    }

}
