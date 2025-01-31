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

import static io.github.demonfiddler.ee.client.EntityKind.CLA;
import static io.github.demonfiddler.ee.client.EntityKind.DEC;
import static io.github.demonfiddler.ee.client.EntityKind.PER;
import static io.github.demonfiddler.ee.client.EntityKind.PUB;
import static io.github.demonfiddler.ee.client.EntityKind.QUO;
import static io.github.demonfiddler.ee.client.EntityKind.TOP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

abstract class AbstractLinkTests extends AbstractGraphQLTests {

    private static final Map<EntityKind, EntityKind[]> LINKED_ENTITY_KIND_MAP = Map.of( //
        CLA, new EntityKind[] {DEC, PER, PUB, QUO}, //
        DEC, new EntityKind[] {PER, QUO}, //
        PER, new EntityKind[] {}, //
        PUB, new EntityKind[] {PER}, //
        QUO, new EntityKind[] {PER, PUB} //
    );
    static final int[][] ENTITY_LINK_INDEXES = { //
        { 0 }, //
        { 1 /* <-- k */, 2, 7, 8 }, // <-- entityLinkIndexes
        { 2, 3, 6, 7 }, //
        { 3, 4, 5, 6 }, //
        { 1, 4, 5, 8 }, //
        { 3, 4, 5, 6 }, //
        { 2, 3, 6, 7 }, //
        { 1, 2, 7, 8 }, //
        { 1, 2, 7, 8 }, //
    };
    static final ITopicalEntity[] ENTITY = new ITopicalEntity[5];
    static final Map<EntityKind, ITopicalEntity> ENTITY_MAP = new LinkedHashMap<>();
    static final List<List<? extends ITopicalEntity>> ENTITIES = new ArrayList<>(5);
    static Map<EntityKind, List<? extends IBaseEntity>> ENTITIES_MAP = new LinkedHashMap<>();
    /** Maps entityID to entity index, mapped by entity kind. */
    private static final Map<EntityKind, Map<Long, Integer>> ENTITY_ID_TO_INDEX = new HashMap<>();
    private static volatile boolean initialised;

    static synchronized void init() {
        if (!initialised) {
            ENTITY[0] = ClaimTests.claim;
            ENTITY[1] = DeclarationTests.declaration;
            ENTITY[2] = PersonTests.person;
            ENTITY[3] = PublicationTests.publication;
            ENTITY[4] = QuotationTests.quotation;
            ENTITY_MAP.put(CLA, ENTITY[0]);
            ENTITY_MAP.put(DEC, ENTITY[1]);
            ENTITY_MAP.put(PER, ENTITY[2]);
            ENTITY_MAP.put(PUB, ENTITY[3]);
            ENTITY_MAP.put(QUO, ENTITY[4]);
            ENTITIES.add(ClaimTests.claims); // <-- fromEntities, fromEntities[i] <-- fromEntity
            ENTITIES.add(DeclarationTests.declarations);
            ENTITIES.add(PersonTests.persons);
            ENTITIES.add(PublicationTests.publications);
            ENTITIES.add(QuotationTests.quotations);
            ENTITIES_MAP.put(CLA, ClaimTests.claims);
            ENTITIES_MAP.put(DEC, DeclarationTests.declarations);
            ENTITIES_MAP.put(PER, PersonTests.persons);
            ENTITIES_MAP.put(PUB, PublicationTests.publications);
            ENTITIES_MAP.put(QUO, QuotationTests.quotations);
            ENTITIES_MAP.put(TOP, TopicTests.topics);
            for (Entry<EntityKind, List<? extends IBaseEntity>> entry : ENTITIES_MAP.entrySet()) {
                EntityKind entityKind = entry.getKey();
                Map<Long, Integer> indexById = new LinkedHashMap<>();
                ENTITY_ID_TO_INDEX.put(entityKind, indexById);
                List<? extends IBaseEntity> entities = entry.getValue();
                for (int i = 0; i < entities.size(); i++) {
                    IBaseEntity entity = entities.get(i);
                    indexById.put(entity.getId(), i);
                }
            }
            initialised = true;
        }
    }

    int getEntityIndex(EntityKind entityKind, IBaseEntity entity) {
        return getEntityIndex(entityKind, entity.getId());
    }

    int getEntityIndex(EntityKind entityKind, Long id) {
        return ENTITY_ID_TO_INDEX.get(entityKind).get(id);
    }

    @SuppressWarnings("unchecked")
    <T> List<T> getEntities(EntityKind entityKind) {
        return (List<T>)ENTITIES_MAP.get(entityKind);
    }

    EntityKind[] getLinkedEntityKinds(EntityKind masterEntityKind) {
        return LINKED_ENTITY_KIND_MAP.get(masterEntityKind);
    }

    @SuppressWarnings("unchecked")
    <P extends AbstractPage<T>, T extends IBaseEntity> P queryEntities(EntityKind entityKind, String queryResponseDef,
        TopicalEntityQueryFilter filter, PageableInput pageSort)
        throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

            switch (entityKind) {
            case CLA:
                return (P) queryExecutor.claims(queryResponseDef, filter, pageSort);
            case DEC:
                return (P) queryExecutor.declarations(queryResponseDef, filter, pageSort);
            case PER:
                return (P) queryExecutor.persons(queryResponseDef, filter, pageSort);
            case PUB:
                return (P) queryExecutor.publications(queryResponseDef, filter, pageSort);
            case QUO:
                return (P) queryExecutor.quotations(queryResponseDef, filter, pageSort);
            default:
                throw new IllegalArgumentException();
        }
    }

}
