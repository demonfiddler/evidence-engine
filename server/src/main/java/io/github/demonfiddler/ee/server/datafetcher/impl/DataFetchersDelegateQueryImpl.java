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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateQuery;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.ClaimPage;
import io.github.demonfiddler.ee.server.model.Declaration;
import io.github.demonfiddler.ee.server.model.DeclarationPage;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.JournalPage;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.Person;
import io.github.demonfiddler.ee.server.model.PersonPage;
import io.github.demonfiddler.ee.server.model.Publication;
import io.github.demonfiddler.ee.server.model.PublicationPage;
import io.github.demonfiddler.ee.server.model.PublisherPage;
import io.github.demonfiddler.ee.server.model.Quotation;
import io.github.demonfiddler.ee.server.model.QuotationPage;
import io.github.demonfiddler.ee.server.model.TopicPage;
import io.github.demonfiddler.ee.server.model.TopicQueryFilter;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.model.TopicRefPage;
import io.github.demonfiddler.ee.server.model.TopicRefQueryFilter;
import io.github.demonfiddler.ee.server.model.TopicalEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.UserPage;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.DeclarationRepository;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LinkRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.repository.PublicationRepository;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import io.github.demonfiddler.ee.server.repository.TopicRefRepository;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import io.github.demonfiddler.ee.server.repository.UserRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateQueryImpl implements DataFetchersDelegateQuery {

    @Resource
    private ClaimRepository claimRepository;
    @Resource
    private DeclarationRepository declarationRepository;
    @Resource
    private JournalRepository journalRepository;
    @Resource
    private LinkRepository linkRepository;
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
    private TopicRefRepository topicRefRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private EntityUtils entityUtils;

    @Override
    public Claim claimById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return claimRepository.findById(id).get();
    }

    @Override
    public Object claims(DataFetchingEnvironment dataFetchingEnvironment, TopicalEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, claimRepository, ClaimPage::new);
    }

    @Override
    public Declaration declarationById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return declarationRepository.findById(id).get();
    }

    @Override
    public Object declarations(DataFetchingEnvironment dataFetchingEnvironment, TopicalEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, declarationRepository, DeclarationPage::new);
    }

    @Override
    public Object journalById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return journalRepository.findById(id).get();
    }

    @Override
    public Object journals(DataFetchingEnvironment dataFetchingEnvironment, TrackedEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, journalRepository, JournalPage::new);
    }

    @Override
    public Object log(DataFetchingEnvironment dataFetchingEnvironment, LogQueryFilter filter, PageableInput pageSort) {
        return entityUtils.findByFilter(filter, pageSort, logRepository, LogPage::new);
    }

    @Override
    public Person personById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return personRepository.findById(id).get();
    }

    @Override
    public Object persons(DataFetchingEnvironment dataFetchingEnvironment, TopicalEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, personRepository, PersonPage::new);
    }

    @Override
    public Publication publicationById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return publicationRepository.findById(id).get();
    }

    @Override
    public Object publications(DataFetchingEnvironment dataFetchingEnvironment, TopicalEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, publicationRepository, PublicationPage::new);
    }

    @Override
    public Object publisherById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return publisherRepository.findById(id).get();
    }

    @Override
    public Object publishers(DataFetchingEnvironment dataFetchingEnvironment, TrackedEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, publisherRepository, PublisherPage::new);
    }

    @Override
    public Quotation quotationById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return quotationRepository.findById(id).get();
    }

    @Override
    public Object quotations(DataFetchingEnvironment dataFetchingEnvironment, TopicalEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, quotationRepository, QuotationPage::new);
    }

    @Override
    public Object topicById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return topicRepository.findById(id).get();
    }

    @Override
    public Object topics(DataFetchingEnvironment dataFetchingEnvironment, TopicQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, topicRepository, TopicPage::new);
    }

    @Override
    public Object topicRefById(DataFetchingEnvironment dataFetchingEnvironment, Long id, EntityKind entityKind) {
        return topicRefRepository.findById(id, entityKind);
    }

    @Override
	public Object topicRefByEntityId(DataFetchingEnvironment dataFetchingEnvironment, Long topicId, Long entityId, EntityKind entityKind) {
        return topicRefRepository.findByEntityId(topicId, entityId, entityKind);
    }

    @Override
    public Object topicRefs(DataFetchingEnvironment dataFetchingEnvironment, TopicRefQueryFilter filter,
        PageableInput pageSort) {

        Pageable pageable = entityUtils.toPageable(pageSort);
        Page<TopicRef> page = topicRefRepository.findByFilter(filter, pageable);
        return entityUtils.toEntityPage(page, TopicRefPage::new);
    }

    @Override
    public Object userById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Object userByLogin(DataFetchingEnvironment dataFetchingEnvironment, String login) {
        return userRepository.findByLogin(login).get();
    }

    @Override
    public Object users(DataFetchingEnvironment dataFetchingEnvironment, TrackedEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, userRepository, UserPage::new);
    }

}
