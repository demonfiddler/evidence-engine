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

package io.github.demonfiddler.ee.server.datafetcher.impl;

import static io.github.demonfiddler.ee.common.util.StringUtils.countLines;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateMutation;
import io.github.demonfiddler.ee.server.model.AbstractLinkableEntity;
import io.github.demonfiddler.ee.server.model.AbstractTrackedEntity;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.ClaimInput;
import io.github.demonfiddler.ee.server.model.Declaration;
import io.github.demonfiddler.ee.server.model.DeclarationInput;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.ILinkableEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.JournalInput;
import io.github.demonfiddler.ee.server.model.EntityLinkInput;
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
import io.github.demonfiddler.ee.server.model.EntityLink;
import io.github.demonfiddler.ee.server.model.TransactionKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.model.UserInput;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.DeclarationRepository;
import io.github.demonfiddler.ee.server.repository.EntityLinkRepository;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LinkableEntityRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.repository.PublicationRepository;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import io.github.demonfiddler.ee.server.repository.TrackedEntityRepository;
import io.github.demonfiddler.ee.server.repository.UserRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateMutationImpl implements DataFetchersDelegateMutation {

    @Resource
    private ClaimRepository claimRepository;
    @Resource
    private DeclarationRepository declarationRepository;
    @Resource
    private JournalRepository journalRepository;
    @Resource
    private EntityLinkRepository entityLinkRepository;
    @Resource
    LinkableEntityRepository linkableEntityRepository;
    @Resource
    private LogRepository logRepository;
    @Resource
    private PersonRepository personRepository;
    @Resource
    private PublicationRepository publicationRepository;
    @Resource
    private PublisherRepository publisherRepository;
    @Resource
    private QuotationRepository quotationRepository;
    @Resource
    private TopicRepository topicRepository;
    @Resource
    TrackedEntityRepository trackedEntityRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private EntityUtils entityUtils;
    @Resource
    protected SecurityUtils securityUtils;

    private void setCreatedFields(ITrackedEntity entity) {
        entity.setStatus(StatusKind.DRA.name());
        entity.setCreated(OffsetDateTime.now());
        entity.setCreatedByUser(getUser().get());
    }

    private void setUpdatedFields(ITrackedEntity entity) {
        entity.setUpdated(OffsetDateTime.now());
        entity.setUpdatedByUser(getUser().get());
    }

    private Optional<User> getUser() {
        return securityUtils.getCurrentUser();
    }

    private void log(TransactionKind txnKind, ITrackedEntity entity, OffsetDateTime timestamp) {
        log(txnKind, entity.getId(), entityUtils.getEntityKind(entity), null, null, timestamp);
    }

    private void log(TransactionKind txnKind, Long entityId, EntityKind entityKind, Long linkedEntityId,
        EntityKind linkedEntityKind, OffsetDateTime timestamp) {

        Log log = new Log();
        log.setTransactionKind(txnKind.name());
        log.setTimestamp(timestamp);
        log.setUser(getUser().get());
        log.setEntityId(entityId);
        log.setEntityKind(entityKind.name());
        if (linkedEntityId != null && linkedEntityKind != null) {
            log.setLinkedEntityId(linkedEntityId);
            log.setLinkedEntityKind(linkedEntityKind.name());
        }
        logRepository.save(log);
    }

    private <T extends ITrackedEntity> void logCreated(T entity) {
        log(TransactionKind.CRE, entity, entity.getCreated());
    }

    private <T extends ITrackedEntity> void logUpdated(T entity) {
        log(TransactionKind.UPD, entity, entity.getUpdated());
    }

    private <T extends ITrackedEntity> void logDeleted(T entity) {
        log(TransactionKind.DEL, entity, entity.getUpdated());
    }

    private void logLinked(EntityLink entityLink, Long entityId, EntityKind entityKind, Long linkedEntityId,
        EntityKind linkedEntityKind) {

        OffsetDateTime timestamp = getLatestDate(entityLink);
        log(TransactionKind.LNK, entityId, entityKind, linkedEntityId, linkedEntityKind, timestamp);
        log(TransactionKind.LNK, linkedEntityId, linkedEntityKind, entityId, entityKind, timestamp);
    }

    private void logUnlinked(EntityLink entityLink, Long entityId, EntityKind entityKind, Long linkedEntityId,
        EntityKind linkedEntityKind) {

        OffsetDateTime timestamp = getLatestDate(entityLink);
        log(TransactionKind.UNL, entityId, entityKind, linkedEntityId, linkedEntityKind, timestamp);
        log(TransactionKind.UNL, linkedEntityId, linkedEntityKind, entityId, entityKind, timestamp);
    }

    private OffsetDateTime getLatestDate(EntityLink entityLink) {
        return entityLink.getUpdated() == null //
            ? entityLink.getCreated() //
            : entityLink.getUpdated();
    }

    private <T extends ITrackedEntity, K> T delete(K id, CrudRepository<T, K> repository) {
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
    @PreAuthorize("hasAuthority('CRE')")
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
    @PreAuthorize("hasAuthority('UPD')")
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
    @PreAuthorize("hasAuthority('DEL')")
    public Object deleteClaim(DataFetchingEnvironment dataFetchingEnvironment, Long claimId) {
        return delete(claimId, claimRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
    public Object createDeclaration(DataFetchingEnvironment dataFetchingEnvironment, DeclarationInput input) {
        Declaration declaration = new Declaration();
        declaration.setCached(false);
        declaration.setCountry(input.getCountry());
        declaration.setDate(input.getDate());
        declaration.setKind(input.getKind() == null ? null : input.getKind().name());
        declaration.setNotes(input.getNotes());
        declaration.setSignatories(input.getSignatories());
        declaration.setSignatoryCount(countLines(input.getSignatories()));
        declaration.setTitle(input.getTitle());
        declaration.setUrl(input.getUrl());
        setCreatedFields(declaration);

        declaration = declarationRepository.save(declaration);

        logCreated(declaration);

        return declaration;
    }

    @Override
    @PreAuthorize("hasAuthority('UPD')")
    public Object updateDeclaration(DataFetchingEnvironment dataFetchingEnvironment, DeclarationInput input) {
        Optional<Declaration> declarationOpt = declarationRepository.findById(input.getId());
        if (declarationOpt.isEmpty())
            return null;

        Declaration declaration = declarationOpt.get();
        declaration.setCountry(input.getCountry());
        declaration.setDate(input.getDate());
        declaration.setKind(input.getKind() == null ? null : input.getKind().name());
        declaration.setNotes(input.getNotes());
        declaration.setSignatories(input.getSignatories());
        declaration.setSignatoryCount(countLines(input.getSignatories()));
        declaration.setTitle(input.getTitle());
        declaration.setUrl(input.getUrl());
        setUpdatedFields(declaration);

        declaration = declarationRepository.save(declaration);

        logUpdated(declaration);

        return declaration;
    }

    @Override
    @PreAuthorize("hasAuthority('DEL')")
    public Object deleteDeclaration(DataFetchingEnvironment dataFetchingEnvironment, Long declarationId) {
        return delete(declarationId, declarationRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('LNK')")
    public Object createEntityLink(DataFetchingEnvironment dataFetchingEnvironment, EntityLinkInput input) {
        AbstractLinkableEntity fromEntity = linkableEntityRepository.getReferenceById(input.getFromEntityId());
        AbstractLinkableEntity toEntity = linkableEntityRepository.getReferenceById(input.getToEntityId());
        EntityLink entityLink = EntityLink.builder() //
            .withFromEntity(fromEntity) //
            .withFromEntityLocations(input.getFromEntityLocations()) //
            .withToEntity(toEntity) //
            .withToEntityLocations(input.getToEntityLocations()) //
            .build();
        setCreatedFields(entityLink);
        entityLink = entityLinkRepository.save(entityLink);
        logCreated(entityLink);
        EntityKind fromEntityKind = EntityKind.valueOf(fromEntity.getEntityKind());
        EntityKind toEntityKind = EntityKind.valueOf(toEntity.getEntityKind());
        logLinked(entityLink, input.getFromEntityId(), fromEntityKind, input.getToEntityId(), toEntityKind);
        return entityLink;
    }

    @Override
    @PreAuthorize("hasAuthority('LNK')")
    public Object updateEntityLink(DataFetchingEnvironment dataFetchingEnvironment, EntityLinkInput input) {
        Optional<EntityLink> entityLinkOpt = entityLinkRepository.findById(input.getId());
        if (entityLinkOpt.isEmpty())
            return null;

        EntityLink entityLink = entityLinkOpt.get();
        AbstractLinkableEntity oldFromEntity = entityLink.getFromEntity();
        AbstractLinkableEntity oldToEntity = entityLink.getToEntity();

        setUpdatedFields(entityLink);
        boolean fromEntityChanged = !oldFromEntity.getId().equals(input.getFromEntityId());
        boolean toEntityChanged = !oldToEntity.getId().equals(input.getToEntityId());
        if (fromEntityChanged || toEntityChanged) {
            AbstractLinkableEntity newFromEntity = fromEntityChanged //
                ? linkableEntityRepository.getReferenceById(input.getFromEntityId()) //
                : oldFromEntity;
            AbstractLinkableEntity newToEntity = toEntityChanged //
                ? linkableEntityRepository.getReferenceById(input.getToEntityId()) //
                : oldToEntity;
            if (fromEntityChanged)
                entityLink.setFromEntity(newFromEntity);
            if (toEntityChanged)
                entityLink.setToEntity(newToEntity);
            logUnlinked(entityLink, oldFromEntity.getId(), entityUtils.getEntityKind(oldFromEntity),
                oldToEntity.getId(), entityUtils.getEntityKind(oldToEntity));
            logLinked(entityLink, input.getFromEntityId(), entityUtils.getEntityKind(newFromEntity),
                input.getToEntityId(), entityUtils.getEntityKind(newToEntity));
        }
        entityLink.setFromEntityLocations(input.getFromEntityLocations());
        entityLink.setToEntityLocations(input.getToEntityLocations());
        logUpdated(entityLink);

        return entityLinkRepository.save(entityLink);
    }

    @Override
    @PreAuthorize("hasAuthority('LNK')")
    public Object deleteEntityLink(DataFetchingEnvironment dataFetchingEnvironment, Long entityLinkId) {
        EntityLink entityLink = entityLinkRepository.getReferenceById(entityLinkId);
        entityLink.setStatus(StatusKind.DEL.name());
        setUpdatedFields(entityLink);
        logDeleted(entityLink);

        ILinkableEntity fromEntity = entityLink.getFromEntity();
        ILinkableEntity toEntity = entityLink.getToEntity();
        logUnlinked(entityLink, fromEntity.getId(), entityUtils.getEntityKind(fromEntity), toEntity.getId(),
            entityUtils.getEntityKind(toEntity));

        return entityLinkRepository.save(entityLink);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
    public Object createJournal(DataFetchingEnvironment dataFetchingEnvironment, JournalInput input) {
        Journal journal = new Journal();
        journal.setAbbreviation(input.getAbbreviation());
        journal.setIssn(input.getIssn());
        journal.setNotes(input.getNotes());
        Long publisherId = input.getPublisherId();
        if (publisherId != null) {
            Optional<Publisher> publisherOpt = publisherRepository.findById(publisherId);
            if (publisherOpt.isPresent()) {
                Publisher publisher = publisherOpt.get();
                journal.setPublisher(publisher);
            }
        }
        journal.setTitle(input.getTitle());
        journal.setUrl(input.getUrl());
        setCreatedFields(journal);

        journal = journalRepository.save(journal);

        logCreated(journal);

        return journal;
    }

    @Override
    @PreAuthorize("hasAuthority('UPD')")
    public Object updateJournal(DataFetchingEnvironment dataFetchingEnvironment, JournalInput input) {
        Optional<Journal> journalOpt = journalRepository.findById(input.getId());
        if (journalOpt.isEmpty())
            return null;

        Journal journal = journalOpt.get();
        journal.setAbbreviation(input.getAbbreviation());
        journal.setIssn(input.getIssn());
        journal.setNotes(input.getNotes());
        Long publisherId = input.getPublisherId();
        if (publisherId != null) {
            Optional<Publisher> publisherOpt = publisherRepository.findById(publisherId);
            if (publisherOpt.isPresent()) {
                Publisher publisher = publisherOpt.get();
                journal.setPublisher(publisher);
            }
        }
        journal.setTitle(input.getTitle());
        journal.setUrl(input.getUrl());
        setUpdatedFields(journal);

        journal = journalRepository.save(journal);

        logUpdated(journal);

        return journal;
    }

    @Override
    @PreAuthorize("hasAuthority('DEL')")
    public Object deleteJournal(DataFetchingEnvironment dataFetchingEnvironment, Long journalId) {
        return delete(journalId, journalRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
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
    @PreAuthorize("hasAuthority('UPD')")
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
    @PreAuthorize("hasAuthority('DEL')")
    public Object deletePerson(DataFetchingEnvironment dataFetchingEnvironment, Long personId) {
        return delete(personId, personRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
    public Object createPublication(DataFetchingEnvironment dataFetchingEnvironment, PublicationInput input) {
        Journal journal = null;
        if (input.getJournalId() != null)
            journal = journalRepository.findById(input.getJournalId()).get();
        Publication publication = new Publication();
        publication.setTitle(input.getTitle());
        publication.setJournal(journal);
        publication.setAuthors(input.getAuthorNames());
        publication.setAbstract(input.getAbstract());
        publication.setDate(input.getDate());
        publication.setYear(input.getYear());
        publication.setKind(input.getKind() == null ? null : input.getKind().name());
        publication.setDoi(input.getDoi());
        publication.setIsbn(input.getIsbn());
        publication.setNotes(input.getNotes());
        publication.setPeerReviewed(input.getPeerReviewed());
        publication.setUrl(input.getUrl());
        publication.setCached(input.getCached());
        publication.setAccessed(input.getAccessed());
        setCreatedFields(publication);

        publication = publicationRepository.save(publication);

        logCreated(publication);

        return publication;
    }

    @Override
    @PreAuthorize("hasAuthority('UPD')")
    public Object updatePublication(DataFetchingEnvironment dataFetchingEnvironment, PublicationInput input) {
        Optional<Publication> publicationOpt = publicationRepository.findById(input.getId());
        if (publicationOpt.isEmpty())
            return null;

        Journal journal = null;
        if (input.getJournalId() != null)
            journal = journalRepository.findById(input.getJournalId()).get();
        Publication publication = publicationOpt.get();
        publication.setTitle(input.getTitle());
        publication.setJournal(journal);
        publication.setAuthors(input.getAuthorNames());
        publication.setAbstract(input.getAbstract());
        publication.setDate(input.getDate());
        publication.setYear(input.getYear());
        publication.setKind(input.getKind() == null ? null : input.getKind().name());
        publication.setDoi(input.getDoi());
        publication.setIsbn(input.getIsbn());
        publication.setNotes(input.getNotes());
        publication.setPeerReviewed(input.getPeerReviewed());
        publication.setUrl(input.getUrl());
        publication.setCached(input.getCached());
        publication.setAccessed(input.getAccessed());
        setUpdatedFields(publication);

        publication = publicationRepository.save(publication);

        logUpdated(publication);

        return publication;
    }

    @Override
    @PreAuthorize("hasAuthority('DEL')")
    public Object deletePublication(DataFetchingEnvironment dataFetchingEnvironment, Long publicationId) {
        return delete(publicationId, publicationRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
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
    @PreAuthorize("hasAuthority('UPD')")
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
    @PreAuthorize("hasAuthority('DEL')")
    public Object deletePublisher(DataFetchingEnvironment dataFetchingEnvironment, Long publisherId) {
        return delete(publisherId, publisherRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
    public Object createQuotation(DataFetchingEnvironment dataFetchingEnvironment, QuotationInput input) {
        Quotation quotation = new Quotation();
        quotation.setQuotee(input.getQuotee());
        quotation.setText(input.getText());
        quotation.setDate(input.getDate());
        quotation.setSource(input.getSource());
        quotation.setUrl(input.getUrl());
        quotation.setNotes(input.getNotes());
        setCreatedFields(quotation);

        quotation = quotationRepository.save(quotation);

        logCreated(quotation);

        return quotation;
    }

    @Override
    @PreAuthorize("hasAuthority('UPD')")
    public Object updateQuotation(DataFetchingEnvironment dataFetchingEnvironment, QuotationInput input) {
        Optional<Quotation> quotationOpt = quotationRepository.findById(input.getId());
        if (quotationOpt.isEmpty())
            return null;

        Quotation quotation = quotationOpt.get();
        quotation.setQuotee(input.getQuotee());
        quotation.setText(input.getText());
        quotation.setDate(input.getDate());
        quotation.setSource(input.getSource());
        quotation.setUrl(input.getUrl());
        quotation.setNotes(input.getNotes());
        setUpdatedFields(quotation);

        quotation = quotationRepository.save(quotation);

        logUpdated(quotation);

        return quotation;
    }

    @Override
    @PreAuthorize("hasAuthority('DEL')")
    public Object deleteQuotation(DataFetchingEnvironment dataFetchingEnvironment, Long quotationId) {
        return delete(quotationId, quotationRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
    public Object createTopic(DataFetchingEnvironment dataFetchingEnvironment, TopicInput input) {
        Topic topic = new Topic();
        topic.setLabel(input.getLabel());
        topic.setDescription(input.getDescription());
        Long parentId = input.getParentId();
        if (parentId != null) {
            Optional<Topic> parentOpt = topicRepository.findById(parentId);
            topic.setParent(parentOpt.get());
        }
        setCreatedFields(topic);

        topic = topicRepository.save(topic);

        logCreated(topic);

        return topic;
    }

    @Override
    @PreAuthorize("hasAuthority('UPD')")
    public Object updateTopic(DataFetchingEnvironment dataFetchingEnvironment, TopicInput input) {
        Optional<Topic> quotationOpt = topicRepository.findById(input.getId());
        if (quotationOpt.isEmpty())
            return null;

        Topic topic = quotationOpt.get();
        topic.setLabel(input.getLabel());
        topic.setDescription(input.getDescription());
        Long parentId = input.getParentId();
        if (parentId == null) {
            topic.setParent(null);
        } else {
            Optional<Topic> parentOpt = topicRepository.findById(parentId);
            topic.setParent(parentOpt.get());
        }
        setUpdatedFields(topic);

        topic = topicRepository.save(topic);

        logUpdated(topic);

        return topic;
    }

    @Override
    @PreAuthorize("hasAuthority('DEL')")
    public Object deleteTopic(DataFetchingEnvironment dataFetchingEnvironment, Long topicId) {
        return delete(topicId, topicRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('ADM')")
    public Object createUser(DataFetchingEnvironment dataFetchingEnvironment, UserInput input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        setCreatedFields(user);

        user = userRepository.save(user);

        logCreated(user);

        return user;
    }

    @Override
    @PreAuthorize("hasAuthority('ADM')")
    public Object updateUser(DataFetchingEnvironment dataFetchingEnvironment, UserInput input) {
        Optional<User> userOpt = userRepository.findById(input.getId());
        if (userOpt.isEmpty())
            return null;

        User user = userOpt.get();
        user.setUsername(input.getUsername());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        setUpdatedFields(user);

        user = userRepository.save(user);

        logUpdated(user);

        return user;
    }

    @Override
    @PreAuthorize("hasAuthority('ADM')")
    public Object deleteUser(DataFetchingEnvironment dataFetchingEnvironment, Long userId) {
        return delete(userId, userRepository);
    }

    @Override
    @PreAuthorize("hasAuthority('ADM')")
    public Object grantUserPermissions(DataFetchingEnvironment dataFetchingEnvironment, Long userId,
        List<PermissionKind> permissions) {

        int updateCount = userRepository.addUserPermissions(userId, permissions);
        return updateCount == permissions.size();
    }

    @Override
    @PreAuthorize("hasAuthority('ADM')")
    public Object revokeUserPermissions(DataFetchingEnvironment dataFetchingEnvironment, Long userId,
        List<PermissionKind> permissions) {

        int removeCount = userRepository.removeUserPermissions(userId, permissions);
        return removeCount == permissions.size();
    }

    @Override
    @PreAuthorize("hasAuthority('UPD')")
    public Object setEntityStatus(DataFetchingEnvironment dataFetchingEnvironment, Long entityId, StatusKind status) {

        Optional<AbstractTrackedEntity> entityOpt = trackedEntityRepository.findById(entityId);
        if (entityOpt.isPresent()) {
            AbstractTrackedEntity entity = entityOpt.get();
            entity.setStatus(status.name());
            setUpdatedFields(entity);
            logUpdated(entity);
            trackedEntityRepository.save(entity);
            return true;
        } else {
            return false;
        }
    }

}
