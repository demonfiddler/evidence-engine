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

import java.util.List;

/** Checks completeness of state built up by integration tests. */
class TestState {

    static List<ITrackedEntity> getExpectedTrackedEntity() {
        return List.of(ClaimTests.claim, DeclarationTests.declaration, JournalTests.journal, PersonTests.person,
            PublicationTests.publication, PublisherTests.publisher, QuotationTests.quotation, TopicTests.parentTopic,
            TopicTests.childTopic);
    }

    static List<List<? extends ITrackedEntity>> getExpectedTrackedEntities() {
        return List.of(ClaimTests.claims, DeclarationTests.declarations, JournalTests.journals, PersonTests.persons,
            PublicationTests.publications, PublisherTests.publishers, QuotationTests.quotations, TopicTests.topics);
    }

    static List<ILinkableEntity> getExpectedLinkableEntity() {
        return List.of(ClaimTests.claim, DeclarationTests.declaration, PersonTests.person, PublicationTests.publication,
            QuotationTests.quotation);
    }

    static List<List<? extends ILinkableEntity>> getExpectedLinkableEntities() {
        return List.of(ClaimTests.claims, DeclarationTests.declarations, PersonTests.persons,
            PublicationTests.publications, QuotationTests.quotations);
    }

    static boolean hasExpectedEntity() {
        return //
        ClaimTests.hasExpectedClaim() && //
            DeclarationTests.hasExpectedDeclaration() && //
            PersonTests.hasExpectedPerson() && //
            PublicationTests.hasExpectedPublication() && //
            QuotationTests.hasExpectedQuotation();
    }

    static boolean hasExpectedEntities() {
        return //
        ClaimTests.hasExpectedClaims() && //
            DeclarationTests.hasExpectedDeclarations() && //
            PersonTests.hasExpectedPersons() && //
            PublicationTests.hasExpectedPublications() && //
            QuotationTests.hasExpectedQuotations();
    }

}
