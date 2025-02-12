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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.ResponseError;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Negative tests to check that unauthenticated or read-only authenticated users cannot perform updates.
 */
abstract class AbstractSecurityTests extends AbstractTrackedEntityTests<ITrackedEntity> {

    @FunctionalInterface
    static interface Method {

        void accept(OperationKind opcode) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

    }

    static enum OperationKind {
        CREATE, READ, UPDATE, DELETE
    }

    static final String RESPONSE_SPEC = """
        {
          id
        }
        """;

    EntityKind entityKind;

    @Override
    EntityKind getEntityKind() {
        return entityKind;
    }

    @Test
    @Order(1)
    void createClaim() throws GraphQLRequestPreparationException {
        execute(this::mutateClaim, OperationKind.CREATE);
    }

    @Test
    @Order(2)
    void createDeclaration() throws GraphQLRequestPreparationException {
        execute(this::mutateDeclaration, OperationKind.CREATE);
    }

    @Test
    @Order(3)
    void createJournal() throws GraphQLRequestPreparationException {
        execute(this::mutateJournal, OperationKind.CREATE);
    }

    @Test
    @Order(4)
    void createPerson() throws GraphQLRequestPreparationException {
        execute(this::mutatePerson, OperationKind.CREATE);
    }

    @Test
    @Order(5)
    void createPublication() throws GraphQLRequestPreparationException {
        execute(this::mutatePublication, OperationKind.CREATE);
    }

    @Test
    @Order(6)
    void createPublisher() throws GraphQLRequestPreparationException {
        execute(this::mutatePublisher, OperationKind.CREATE);
    }

    @Test
    @Order(7)
    void createQuotation() throws GraphQLRequestPreparationException {
        execute(this::mutateQuotation, OperationKind.CREATE);
    }

    @Test
    @Order(8)
    void createTopic() throws GraphQLRequestPreparationException {
        execute(this::mutateTopic, OperationKind.CREATE);
    }

    @Test
    @Order(9)
    void createTopicRef() throws GraphQLRequestPreparationException {
        execute(this::mutateTopicRef, OperationKind.CREATE);
    }

    @Test
    @Order(10)
    void createEntityLink() throws GraphQLRequestPreparationException {
        execute(this::mutateEntityLink, OperationKind.CREATE);
    }

    @Test
    @Order(11)
    void updateClaim() throws GraphQLRequestPreparationException {
        execute(this::mutateClaim, OperationKind.UPDATE);
    }

    @Test
    @Order(12)
    void updateDeclaration() throws GraphQLRequestPreparationException {
        execute(this::mutateDeclaration, OperationKind.UPDATE);
    }

    @Test
    @Order(13)
    void updateJournal() throws GraphQLRequestPreparationException {
        execute(this::mutateJournal, OperationKind.UPDATE);
    }

    @Test
    @Order(14)
    void updatePerson() throws GraphQLRequestPreparationException {
        execute(this::mutatePerson, OperationKind.UPDATE);
    }

    @Test
    @Order(15)
    void updatePublication() throws GraphQLRequestPreparationException {
        execute(this::mutatePublication, OperationKind.UPDATE);
    }

    @Test
    @Order(16)
    void updatePublisher() throws GraphQLRequestPreparationException {
        execute(this::mutatePublisher, OperationKind.UPDATE);
    }

    @Test
    @Order(17)
    void updateQuotation() throws GraphQLRequestPreparationException {
        execute(this::mutateQuotation, OperationKind.UPDATE);
    }

    @Test
    @Order(18)
    void updateTopic() throws GraphQLRequestPreparationException {
        execute(this::mutateTopic, OperationKind.UPDATE);
    }

    @Test
    @Order(19)
    void deleteClaim() throws GraphQLRequestPreparationException {
        execute(this::mutateClaim, OperationKind.DELETE);
    }

    @Test
    @Order(20)
    void deleteDeclaration() throws GraphQLRequestPreparationException {
        execute(this::mutateDeclaration, OperationKind.DELETE);
    }

    @Test
    @Order(21)
    void deleteJournal() throws GraphQLRequestPreparationException {
        execute(this::mutateJournal, OperationKind.DELETE);
    }

    @Test
    @Order(22)
    void deletePerson() throws GraphQLRequestPreparationException {
        execute(this::mutatePerson, OperationKind.DELETE);
    }

    @Test
    @Order(23)
    void deletePublication() throws GraphQLRequestPreparationException {
        execute(this::mutatePublication, OperationKind.DELETE);
    }

    @Test
    @Order(24)
    void deletePublisher() throws GraphQLRequestPreparationException {
        execute(this::mutatePublisher, OperationKind.DELETE);
    }

    @Test
    @Order(25)
    void deleteQuotation() throws GraphQLRequestPreparationException {
        execute(this::mutateQuotation, OperationKind.DELETE);
    }

    @Test
    @Order(26)
    void deleteTopic() throws GraphQLRequestPreparationException {
        execute(this::mutateTopic, OperationKind.DELETE);
    }

    @Test
    @Order(27)
    void deleteTopicRef() throws GraphQLRequestPreparationException {
        execute(this::mutateTopicRef, OperationKind.DELETE);
    }

    @Test
    @Order(28)
    void deleteEntityLink() throws GraphQLRequestPreparationException {
        execute(this::mutateEntityLink, OperationKind.DELETE);
    }

    private void execute(Method method, OperationKind opcode) throws GraphQLRequestPreparationException {
        GraphQLRequestExecutionException e = Assertions.assertThrows(GraphQLRequestExecutionException.class, () -> {
            method.accept(opcode);
        });
        List<ResponseError> errors = e.getErrors();
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).getMessage())
            .isEqualTo(authenticator.isAuthenticated() ? "Forbidden" : "Unauthorized");
    }

    private void mutateClaim(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        ClaimInput input = ClaimInput.builder() //
            .withDate(LocalDate.now()) //
            .withText("Security text") //
            .withNotes("Security notes") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createClaim(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.claimById(RESPONSE_SPEC, ClaimTests.claim.getId());
                break;
            case UPDATE:
                mutationExecutor.updateClaim(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deleteClaim(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutateDeclaration(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        DeclarationInput input = DeclarationInput.builder() //
            .withDate(LocalDate.now()) //
            .withKind(DeclarationKind.DECL) //
            .withTitle("Security title") //
            .withNotes("Security notes") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createDeclaration(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.declarationById(RESPONSE_SPEC, DeclarationTests.declaration.getId());
            case UPDATE:
                mutationExecutor.updateDeclaration(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deleteDeclaration(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutateJournal(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        JournalInput input = JournalInput.builder() //
            .withTitle("Security title") //
            .withAbbreviation("Sec Abbrev") //
            .withNotes("Security notes") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createJournal(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.journalById(RESPONSE_SPEC, JournalTests.journal.getId());
            case UPDATE:
                mutationExecutor.updateJournal(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deleteJournal(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutatePerson(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        PersonInput input = PersonInput.builder() //
            .withTitle("Security title") //
            .withNotes("Security notes") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createPerson(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.personById(RESPONSE_SPEC, PersonTests.person.getId());
            case UPDATE:
                mutationExecutor.updatePerson(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deletePerson(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutatePublication(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        PublicationInput input = PublicationInput.builder() //
            .withTitle("Security title") //
            .withNotes("Security notes") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createPublication(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.publicationById(RESPONSE_SPEC, PublicationTests.publication.getId());
            case UPDATE:
                mutationExecutor.updatePublication(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deletePublication(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutatePublisher(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        PublisherInput input = PublisherInput.builder() //
            .withName("Security name") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createPublisher(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.publisherById(RESPONSE_SPEC, PublisherTests.publisher.getId());
            case UPDATE:
                mutationExecutor.updatePublisher(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deletePublisher(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutateQuotation(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        QuotationInput input = QuotationInput.builder() //
            .withDate(LocalDate.now()) //
            .withQuotee("Security Quotee") //
            .withText("Security text") //
            .withNotes("Security notes") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createQuotation(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.quotationById(RESPONSE_SPEC, QuotationTests.quotation.getId());
            case UPDATE:
                mutationExecutor.updateQuotation(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deleteQuotation(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutateTopic(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        TopicInput input = TopicInput.builder() //
            .withLabel("Security label") //
            .withDescription("Security description") //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.createTopic(RESPONSE_SPEC, input);
                break;
            case READ:
                queryExecutor.topicById(RESPONSE_SPEC, TopicTests.parentTopic.getId());
            case UPDATE:
                mutationExecutor.updateTopic(RESPONSE_SPEC, input);
                break;
            case DELETE:
                mutationExecutor.deleteTopic(RESPONSE_SPEC, 0L);
                break;
        }
    }

    private void mutateTopicRef(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        TopicRefInput input = TopicRefInput.builder() //
            .withTopicId(0L) //
            .withEntityId(0L) //
            .withEntityKind(EntityKind.CLA) //
            .build();
        switch (opcode) {
            case CREATE:
                mutationExecutor.addTopicRef(RESPONSE_SPEC, input);
                break;
            case READ:
                throw new UnsupportedOperationException("read TopicRef");
            case UPDATE:
                throw new UnsupportedOperationException("update TopicRef");
            case DELETE:
                mutationExecutor.removeTopicRef("", input);
                break;
        }
    }

    private void mutateEntityLink(OperationKind opcode)
        throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

        LinkEntitiesInput input = new LinkEntitiesInput();
        input.setFromEntityKind(CLA);
        input.setFromEntityId(0L);
        input.setToEntityKind(DEC);
        input.setToEntityId(0L);
        switch (opcode) {
            case CREATE:
                mutationExecutor.linkEntities("", input);
                break;
            case READ:
                throw new UnsupportedOperationException("read EntityLink");
            case UPDATE:
                throw new UnsupportedOperationException("update EntityLink");
            case DELETE:
                mutationExecutor.unlinkEntities("", input);
                break;
        }
    }

}
