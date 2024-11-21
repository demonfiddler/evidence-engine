/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
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

package io.github.demonfiddler.ee.server.datafetcher.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateMutation;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.ClaimInput;
import io.github.demonfiddler.ee.server.model.Declaration;
import io.github.demonfiddler.ee.server.model.DeclarationInput;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.JournalInput;
import io.github.demonfiddler.ee.server.model.LinkEntitiesInput;
import io.github.demonfiddler.ee.server.model.Log;
import io.github.demonfiddler.ee.server.model.PermissionKind;
import io.github.demonfiddler.ee.server.model.Person;
import io.github.demonfiddler.ee.server.model.PersonInput;
import io.github.demonfiddler.ee.server.model.Publication;
import io.github.demonfiddler.ee.server.model.PublicationInput;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.model.PublisherInput;
import io.github.demonfiddler.ee.server.model.Quotation;
import io.github.demonfiddler.ee.server.model.QuotationInput;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicInput;
import io.github.demonfiddler.ee.server.model.TopicRefInput;
import io.github.demonfiddler.ee.server.model.TransactionKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.model.UserInput;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.DeclarationRepository;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LinkRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.repository.PublicationRepository;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import io.github.demonfiddler.ee.server.repository.UserRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateMutationImpl implements DataFetchersDelegateMutation {

    @Resource
    ClaimRepository claimRepository;
    @Resource
    DeclarationRepository declarationRepository;
    @Resource
    JournalRepository journalRepository;
    @Resource
    LinkRepository linkRepository;
    @Resource
    LogRepository logRepository;
    @Resource
    PersonRepository personRepository;
    @Resource
    PublicationRepository publicationRepository;
    @Resource
    PublisherRepository publisherRepository;
    @Resource
    QuotationRepository quotationRepository;
    @Resource
    TopicRepository topicRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    EntityUtils util;

    User root;

    private void setCreatedFields(ITrackedEntity entity) {
        entity.setCreated(OffsetDateTime.now());
        entity.setCreatedByUser(getUser());
        entity.setStatus(StatusKind.DRA.name());
    }

    private void setUpdatedFields(ITrackedEntity entity) {
        entity.setUpdated(OffsetDateTime.now());
        entity.setUpdatedByUser(getUser());
    }

    private User getUser() {
        // TODO: return the authenticated user

        if (root == null)
            root = userRepository.findById(0L).get();
        return root;
    }

    private void log(TransactionKind txnKind, IBaseEntity entity) {
        log(txnKind, entity.getId(), util.getEntityKind(entity.getClass()), null, null);
    }

    private void log(TransactionKind txnKind, Long entityId, EntityKind entityKind, Long linkedEntityId,
        EntityKind linkedEntityKind) {
        Log log = new Log();
        log.setTransactionKind(txnKind.name());
        log.setTimestamp(OffsetDateTime.now());
        log.setUser(getUser());
        log.setEntityId(entityId);
        log.setEntityKind(entityKind.name());
        if (linkedEntityId != null && linkedEntityKind != null) {
            log.setEntityId(linkedEntityId);
            log.setEntityKind(linkedEntityKind.name());
        }
        logRepository.save(log);
    }

    private void logCreated(IBaseEntity entity) {
        log(TransactionKind.CRE, entity);
    }

    private void logUpdated(IBaseEntity entity) {
        log(TransactionKind.UPD, entity);
    }

    private void logDeleted(IBaseEntity entity) {
        log(TransactionKind.DEL, entity);
    }

    private void logLinked(Long entityId, EntityKind entityKind, Long linkedEntityId, EntityKind linkedEntityKind) {
        log(TransactionKind.LNK, entityId, entityKind, linkedEntityId, linkedEntityKind);
    }

    private void logUnlinked(Long entityId, EntityKind entityKind, Long linkedEntityId, EntityKind linkedEntityKind) {
        log(TransactionKind.UNL, entityId, entityKind, linkedEntityId, linkedEntityKind);
    }

    private <T extends IBaseEntity & ITrackedEntity, K> T delete(K id, CrudRepository<T, K> repository) {
        Optional<T> entityOpt = repository.findById(id);
        if (entityOpt.isEmpty())
            return null;
        T entity = entityOpt.get();
        if (StatusKind.valueOf(entity.getStatus()) == StatusKind.DEL)
            return entity;

        entity.setStatus(StatusKind.DEL.name());
        setUpdatedFields(entity);
        entity = repository.save(entity);

        logDeleted(entity);

        return entity;
    }

    @Override
    public Object createClaim(DataFetchingEnvironment dataFetchingEnvironment, ClaimInput input) {
        Claim claim = new Claim();
        claim.setDate(input.getDate());
        claim.setNotes(input.getNotes());
        claim.setText(input.getText());
        setCreatedFields(claim);

        claim = claimRepository.save(claim);

        logCreated(claim);

        return claim;
    }

    @Override
    public Object updateClaim(DataFetchingEnvironment dataFetchingEnvironment, ClaimInput input) {
        Optional<Claim> claimOpt = claimRepository.findById(input.getId());
        if (claimOpt.isEmpty())
            return null;

        Claim claim = claimOpt.get();
        claim.setDate(input.getDate());
        claim.setNotes(input.getNotes());
        claim.setText(input.getText());
        setUpdatedFields(claim);

        claim = claimRepository.save(claim);

        logUpdated(claim);

        return claim;
    }

    @Override
    public Object deleteClaim(DataFetchingEnvironment dataFetchingEnvironment, Long claimId) {
        return delete(claimId, claimRepository);
    }

    @Override
    public Object createDeclaration(DataFetchingEnvironment dataFetchingEnvironment, DeclarationInput input) {
        Declaration declaration = new Declaration();
        declaration.setCountry(input.getCountry());
        declaration.setDate(input.getDate());
        declaration.setKind(input.getKind() == null ? null : input.getKind().name());
        declaration.setNotes(input.getNotes());
        declaration.setSignatories(input.getSignatories());
        declaration.setTitle(input.getTitle());
        declaration.setUrl(input.getUrl());
        setCreatedFields(declaration);

        declaration = declarationRepository.save(declaration);

        logCreated(declaration);

        return declaration;
    }

    @Override
    public Object updateDeclaration(DataFetchingEnvironment dataFetchingEnvironment, DeclarationInput input) {
        Optional<Declaration> declarationOpt = declarationRepository.findById(input.getId());
        if (declarationOpt.isEmpty())
            return null;

        Declaration declaration = declarationOpt.get();
        declaration.setCountry(input.getCountry());
        declaration.setDate(input.getDate());
        declaration.setKind(input.getKind() == null ? null : input.getKind().name());
        declaration.setNotes(input.getNotes());
        // declaration.setStatus(input.getStatus());
        declaration.setSignatories(input.getSignatories());
        declaration.setTitle(input.getTitle());
        declaration.setUrl(input.getUrl());
        setUpdatedFields(declaration);

        declaration = declarationRepository.save(declaration);

        logUpdated(declaration);

        return declaration;
    }

    @Override
    public Object deleteDeclaration(DataFetchingEnvironment dataFetchingEnvironment, Long declarationId) {
        return delete(declarationId, declarationRepository);
    }

    @Override
    public Object createJournal(DataFetchingEnvironment dataFetchingEnvironment, JournalInput input) {
        Journal journal = new Journal();
        journal.setAbbreviation(input.getAbbreviation());
        journal.setIssn(input.getIssn());
        journal.setNotes(input.getNotes());
        Optional<Publisher> publisherOpt = publisherRepository.findById(input.getPublisherId());
        if (publisherOpt.isPresent()) {
            Publisher publisher = publisherOpt.get();
            journal.setPublisher(publisher);
        }
        journal.setTitle(input.getTitle());
        journal.setUrl(input.getUrl());
        setCreatedFields(journal);

        journal = journalRepository.save(journal);

        logCreated(journal);

        return journal;
    }

    @Override
    public Object updateJournal(DataFetchingEnvironment dataFetchingEnvironment, JournalInput input) {
        Optional<Journal> journalOpt = journalRepository.findById(input.getId());
        if (journalOpt.isEmpty())
            return null;

        Journal journal = journalOpt.get();
        journal.setAbbreviation(input.getAbbreviation());
        journal.setIssn(input.getIssn());
        journal.setNotes(input.getNotes());
        Optional<Publisher> publisherOpt = publisherRepository.findById(input.getPublisherId());
        if (publisherOpt.isPresent()) {
            Publisher publisher = publisherOpt.get();
            journal.setPublisher(publisher);
        }
        // journal.setStatus(input.getStatus());
        journal.setTitle(input.getTitle());
        journal.setUrl(input.getUrl());
        setUpdatedFields(journal);

        journal = journalRepository.save(journal);

        logUpdated(journal);

        return journal;
    }

    @Override
    public Object deleteJournal(DataFetchingEnvironment dataFetchingEnvironment, Long journalId) {
        return delete(journalId, journalRepository);
    }

    @Override
    public Object createPerson(DataFetchingEnvironment dataFetchingEnvironment, PersonInput input) {
        Person person = new Person();
        person.setAlias(input.getAlias());
        person.setTitle(input.getTitle());
        person.setFirstName(input.getFirstName());
        person.setNickname(input.getNickname());
        person.setPrefix(input.getPrefix());
        person.setLastName(input.getLastName());
        person.setSuffix(input.getSuffix());
        person.setNotes(input.getNotes());
        person.setQualifications(input.getQualifications());
        person.setCountry(input.getCountry());
        person.setChecked(input.getChecked());
        person.setPublished(input.getPublished());
        person.setRating(input.getRating());
        setCreatedFields(person);

        person = personRepository.save(person);

        logCreated(person);

        return person;
    }

    @Override
    public Object updatePerson(DataFetchingEnvironment dataFetchingEnvironment, PersonInput input) {
        Optional<Person> personOpt = personRepository.findById(input.getId());
        if (personOpt.isEmpty())
            return null;

        Person person = personOpt.get();
        person.setAlias(input.getAlias());
        person.setTitle(input.getTitle());
        person.setFirstName(input.getFirstName());
        person.setNickname(input.getNickname());
        person.setPrefix(input.getPrefix());
        person.setLastName(input.getLastName());
        person.setSuffix(input.getSuffix());
        person.setNotes(input.getNotes());
        person.setQualifications(input.getQualifications());
        person.setCountry(input.getCountry());
        person.setChecked(input.getChecked());
        person.setPublished(input.getPublished());
        person.setRating(input.getRating());
        setUpdatedFields(person);

        person = personRepository.save(person);

        logUpdated(person);

        return person;
    }

    @Override
    public Object deletePerson(DataFetchingEnvironment dataFetchingEnvironment, Long personId) {
        return delete(personId, personRepository);
    }

    @Override
    public Object createPublication(DataFetchingEnvironment dataFetchingEnvironment, PublicationInput input) {
        Publication publication = new Publication();
        publication.setTitle(input.getTitle());
        // publication.setJournal(input.getJournal());
        publication.setAuthors(input.getAuthorNames());
        publication.setAbstract(input.getAbstract());
        publication.setDate(input.getDate());
        publication.setYear(input.getYear());
        publication.setDoi(input.getDoi());
        publication.setIssnIsbn(input.getIsbn());
        publication.setNotes(input.getNotes());
        publication.setLocation(input.getLocation());
        publication.setPeerReviewed(input.getPeerReviewed());
        publication.setUrl(input.getUrl());
        publication.setAccessed(input.getAccessed());
        setCreatedFields(publication);

        publication = publicationRepository.save(publication);

        logCreated(publication);

        return publication;
    }

    @Override
    public Object updatePublication(DataFetchingEnvironment dataFetchingEnvironment, PublicationInput input) {
        Optional<Publication> publicationOpt = publicationRepository.findById(input.getId());
        if (publicationOpt.isEmpty())
            return null;

        Publication publication = publicationOpt.get();
        publication.setTitle(input.getTitle());
        // publication.setJournal(input.getJournal());
        publication.setAuthors(input.getAuthorNames());
        publication.setAbstract(input.getAbstract());
        publication.setDate(input.getDate());
        publication.setYear(input.getYear());
        publication.setDoi(input.getDoi());
        publication.setIssnIsbn(input.getIsbn());
        publication.setNotes(input.getNotes());
        publication.setLocation(input.getLocation());
        publication.setPeerReviewed(input.getPeerReviewed());
        publication.setUrl(input.getUrl());
        publication.setAccessed(input.getAccessed());
        setUpdatedFields(publication);

        publication = publicationRepository.save(publication);

        logUpdated(publication);

        return publication;
    }

    @Override
    public Object deletePublication(DataFetchingEnvironment dataFetchingEnvironment, Long publicationId) {
        return delete(publicationId, publicationRepository);
    }

    @Override
    public Object createPublisher(DataFetchingEnvironment dataFetchingEnvironment, PublisherInput input) {
        Publisher publisher = new Publisher();
        publisher.setCountry(input.getCountry());
        publisher.setJournalCount(input.getJournalCount());
        publisher.setLocation(input.getLocation());
        publisher.setName(input.getName());
        publisher.setUrl(input.getUrl());
        setCreatedFields(publisher);

        publisher = publisherRepository.save(publisher);

        logCreated(publisher);

        return publisher;
    }

    @Override
    public Object updatePublisher(DataFetchingEnvironment dataFetchingEnvironment, PublisherInput input) {
        Optional<Publisher> publisherOpt = publisherRepository.findById(input.getId());
        if (publisherOpt.isEmpty())
            return null;

        Publisher publisher = publisherOpt.get();
        publisher.setCountry(input.getCountry());
        publisher.setJournalCount(input.getJournalCount());
        publisher.setLocation(input.getLocation());
        publisher.setName(input.getName());
        publisher.setUrl(input.getUrl());
        setUpdatedFields(publisher);

        publisher = publisherRepository.save(publisher);

        logUpdated(publisher);

        return publisher;
    }

    @Override
    public Object deletePublisher(DataFetchingEnvironment dataFetchingEnvironment, Long publisherId) {
        return delete(publisherId, publisherRepository);
    }

    @Override
    public Object createQuotation(DataFetchingEnvironment dataFetchingEnvironment, QuotationInput input) {
        Quotation quotation = new Quotation();
        quotation.setDate(input.getDate());
        // quotation.setAuthor(input.getPersonId());
        quotation.setSource(input.getSource());
        quotation.setText(input.getText());
        quotation.setUrl(input.getUrl());
        setCreatedFields(quotation);

        quotation = quotationRepository.save(quotation);

        logCreated(quotation);

        return quotation;
    }

    @Override
    public Object updateQuotation(DataFetchingEnvironment dataFetchingEnvironment, QuotationInput input) {
        Optional<Quotation> quotationOpt = quotationRepository.findById(input.getId());
        if (quotationOpt.isEmpty())
            return null;

        Quotation quotation = quotationOpt.get();
        quotation.setDate(input.getDate());
        // quotation.setAuthor(input.getPersonId());
        quotation.setSource(input.getSource());
        quotation.setText(input.getText());
        quotation.setUrl(input.getUrl());
        setUpdatedFields(quotation);

        quotation = quotationRepository.save(quotation);

        logUpdated(quotation);

        return quotation;
    }

    @Override
    public Object deleteQuotation(DataFetchingEnvironment dataFetchingEnvironment, Long quotationId) {
        return delete(quotationId, quotationRepository);
    }

    @Override
    public Object createTopic(DataFetchingEnvironment dataFetchingEnvironment, TopicInput input) {
        Topic topic = new Topic();
        topic.setDescription(input.getDescription());
        topic.setLabel(input.getLabel());
        Optional<Topic> parentOpt = topicRepository.findById(input.getParentId());
        // if (parentOpt.isPresent())
        topic.setParent(parentOpt.get());
        setCreatedFields(topic);

        topic = topicRepository.save(topic);

        logCreated(topic);

        return topic;
    }

    @Override
    public Object updateTopic(DataFetchingEnvironment dataFetchingEnvironment, TopicInput input) {
        Optional<Topic> quotationOpt = topicRepository.findById(input.getId());
        if (quotationOpt.isEmpty())
            return null;

        Topic topic = quotationOpt.get();
        topic.setDescription(input.getDescription());
        topic.setLabel(input.getLabel());
        Optional<Topic> parentOpt = topicRepository.findById(input.getParentId());
        // if (parentOpt.isPresent())
        topic.setParent(parentOpt.get());
        setUpdatedFields(topic);

        topic = topicRepository.save(topic);

        logUpdated(topic);

        return topic;
    }

    @Override
    public Object deleteTopic(DataFetchingEnvironment dataFetchingEnvironment, Long topicId) {
        return delete(topicId, topicRepository);
    }

    @Override
    public Object createUser(DataFetchingEnvironment dataFetchingEnvironment, UserInput input) {
        User user = new User();
        user.setLogin(input.getLogin());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        setCreatedFields(user);

        user = userRepository.save(user);

        logCreated(user);

        return user;
    }

    @Override
    public Object updateUser(DataFetchingEnvironment dataFetchingEnvironment, UserInput input) {
        Optional<User> userOpt = userRepository.findById(input.getId());
        if (userOpt.isEmpty())
            return null;

        User user = userOpt.get();
        user.setLogin(input.getLogin());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        setUpdatedFields(user);

        user = userRepository.save(user);

        logUpdated(user);

        return user;
    }

    @Override
    public Object deleteUser(DataFetchingEnvironment dataFetchingEnvironment, Long userId) {
        return delete(userId, userRepository);
    }

    @Override
    public Object grantUserPermissions(DataFetchingEnvironment dataFetchingEnvironment, Long userId,
        List<PermissionKind> permissions) {

        int updateCount = userRepository.addUserPermissions(userId, permissions);
        return updateCount == permissions.size();
    }

    @Override
    public Object revokeUserPermissions(DataFetchingEnvironment dataFetchingEnvironment, Long userId,
        List<PermissionKind> permissions) {

        int removeCount = userRepository.removeUserPermissions(userId, permissions);
        return removeCount == permissions.size();
    }

    @Override
    public Object addTopicRef(DataFetchingEnvironment dataFetchingEnvironment, TopicRefInput topicRef) {
        int addCount = linkRepository.addTopicRef(topicRef);
        if (addCount == 1) {
            logLinked(topicRef.getTopicId(), EntityKind.TOP, topicRef.getEntityId(), topicRef.getEntityKind());
            return true;
        }
        return false;
    }

    @Override
    public Object removeTopicRef(DataFetchingEnvironment dataFetchingEnvironment, TopicRefInput topicRef) {
        int removeCount = linkRepository.removeTopicRef(topicRef);
        if (removeCount == 1) {
            logUnlinked(topicRef.getTopicId(), EntityKind.TOP, topicRef.getEntityId(), topicRef.getEntityKind());
            return true;
        }
        return false;
    }

    @Override
    public Object linkEntities(DataFetchingEnvironment dataFetchingEnvironment, LinkEntitiesInput linkInput) {
        int linkCount = linkRepository.linkEntities(linkInput);
        if (linkCount == 1) {
            logLinked(linkInput.getFromEntityId(), linkInput.getFromEntityKind(), linkInput.getToEntityId(),
                linkInput.getToEntityKind());
            return true;
        }
        return false;
    }

    @Override
    public Object unlinkEntities(DataFetchingEnvironment dataFetchingEnvironment, LinkEntitiesInput linkInput) {
        int unlinkCount = linkRepository.unlinkEntities(linkInput);
        if (unlinkCount == 1) {
            logUnlinked(linkInput.getFromEntityId(), linkInput.getFromEntityKind(), linkInput.getToEntityId(),
                linkInput.getToEntityKind());
            return true;
        }
        return false;
    }

    @Override
    public Object setEntityStatus(DataFetchingEnvironment dataFetchingEnvironment, EntityKind entityKind, Long entityId,
        StatusKind status) {

        CrudRepository<? extends ITrackedEntity, Long> repository;
        switch (entityKind) {
            case CLA:
                repository = claimRepository;
                break;
            case DEC:
                repository = declarationRepository;
                break;
            case JOU:
                repository = journalRepository;
                break;
            case PBR:
                repository = publisherRepository;
                break;
            case PER:
                repository = personRepository;
                break;
            case PUB:
                repository = publicationRepository;
                break;
            case QUO:
                repository = quotationRepository;
                break;
            case TOP:
                repository = topicRepository;
                break;
            case USR:
                repository = userRepository;
                break;
            default:
                throw new IllegalArgumentException("Unsupported entity kind: " + entityKind);
        }
        Optional<? extends ITrackedEntity> entityOpt = repository.findById(entityId);
        if (entityOpt.isPresent()) {
            ITrackedEntity entity = entityOpt.get();
            entity.setStatus(status.name());
            setUpdatedFields(entity);
            logUpdated((IBaseEntity)entity);
            // Oh dear - there's no way to invoke save(), because the compiler doesn't know what ? represents!
            // repository.save(entity);
            return true;
        } else {
            return false;
        }
    }

}
