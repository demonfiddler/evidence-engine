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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateQuery;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.ClaimPage;
import io.github.demonfiddler.ee.server.model.Declaration;
import io.github.demonfiddler.ee.server.model.DeclarationPage;
import io.github.demonfiddler.ee.server.model.EntityLinkPage;
import io.github.demonfiddler.ee.server.model.EntityLinkQueryFilter;
import io.github.demonfiddler.ee.server.model.EntityStatistics;
import io.github.demonfiddler.ee.server.model.GroupPage;
import io.github.demonfiddler.ee.server.model.JournalPage;
import io.github.demonfiddler.ee.server.model.LinkableEntityQueryFilter;
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
import io.github.demonfiddler.ee.server.model.StatisticsQueryFilter;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicPage;
import io.github.demonfiddler.ee.server.model.TopicQueryFilter;
import io.github.demonfiddler.ee.server.model.TopicStatistics;
import io.github.demonfiddler.ee.server.model.TopicStatisticsDto;
import io.github.demonfiddler.ee.server.model.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.UserPage;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.DeclarationRepository;
import io.github.demonfiddler.ee.server.repository.EntityLinkRepository;
import io.github.demonfiddler.ee.server.repository.GroupRepository;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.repository.PublicationRepository;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import io.github.demonfiddler.ee.server.repository.StatisticsRepository;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import io.github.demonfiddler.ee.server.repository.UserRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateQueryImpl implements DataFetchersDelegateQuery {

    @Resource
    private ClaimRepository claimRepository;
    @Resource
    private DeclarationRepository declarationRepository;
    @Resource
    private EntityLinkRepository entityLinkRepository;
    @Resource
    private JournalRepository journalRepository;
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
    private UserRepository userRepository;
    @Resource
    private GroupRepository groupRepository;
    @Resource
    private StatisticsRepository statisticsRepository;
    @Resource
    private EntityUtils entityUtils;
    @Resource
    private SecurityUtils securityUtils;

    @Override
    public Claim claimById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return claimRepository.findById(id).get();
    }

    @Override
    public Object claims(DataFetchingEnvironment dataFetchingEnvironment, LinkableEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, claimRepository, ClaimPage::new);
    }

    @Override
    public Declaration declarationById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return declarationRepository.findById(id).get();
    }

    @Override
    public Object declarations(DataFetchingEnvironment dataFetchingEnvironment, LinkableEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, declarationRepository, DeclarationPage::new);
    }

    @Override
    public Object entityLinks(DataFetchingEnvironment dataFetchingEnvironment, EntityLinkQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, entityLinkRepository, EntityLinkPage::new);
    }

    @Override
    public Object entityLinkById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return entityLinkRepository.findById(id);
    }

    @Override
    public Object entityLinkByEntityIds(DataFetchingEnvironment dataFetchingEnvironment, Long fromEntityId,
        Long toEntityId) {

        return entityLinkRepository.findByEntityIds(fromEntityId, toEntityId);
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
    public Object persons(DataFetchingEnvironment dataFetchingEnvironment, LinkableEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, personRepository, PersonPage::new);
    }

    @Override
    public Publication publicationById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return publicationRepository.findById(id).get();
    }

    @Override
    public Object publications(DataFetchingEnvironment dataFetchingEnvironment, LinkableEntityQueryFilter filter,
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
    public Object quotations(DataFetchingEnvironment dataFetchingEnvironment, LinkableEntityQueryFilter filter,
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
    public Object users(DataFetchingEnvironment dataFetchingEnvironment, TrackedEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, userRepository, UserPage::new);
    }

    @Override
    public Object userById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Object userByUsername(DataFetchingEnvironment dataFetchingEnvironment, String username) {
        return userRepository.findByUsername(username).get();
    }

    @Override
    public Object currentUser(DataFetchingEnvironment dataFetchingEnvironment) {
        return securityUtils.getCurrentUser();
    }

    @Override
    public Object groups(DataFetchingEnvironment dataFetchingEnvironment, TrackedEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityUtils.findByFilter(filter, pageSort, groupRepository, GroupPage::new);
    }

    @Override
    public Object groupById(DataFetchingEnvironment dataFetchingEnvironment, Long id) {
        return groupRepository.findById(id).get();
    }

    @Override
    public Object groupByGroupname(DataFetchingEnvironment dataFetchingEnvironment, String groupname) {
        return groupRepository.findByGroupname(groupname).get();
    }

    @Override
    public Object entityStatistics(DataFetchingEnvironment dataFetchingEnvironment, StatisticsQueryFilter filter) {
        return statisticsRepository.getEntityStatistics(filter);
    }

    private boolean matchesFilter(Topic topic, StatisticsQueryFilter filter) {
        return filter.getStatus() == null ||
            filter.getStatus().contains(StatusKind.fromGraphQlValue(topic.getStatus()));
    }

    @Override
    public Object topicStatistics(DataFetchingEnvironment dataFetchingEnvironment, StatisticsQueryFilter filter) {
        // First create a TopicStatistics object for every topic.
        Map<Long, TopicStatistics> stats = new HashMap<>();
        List<Topic> topics = topicRepository.findAll();
        topics.forEach(topic -> {
            // if (filter.getStatus() == null ||
            // filter.getStatus().contains(StatusKind.fromGraphQlValue(topic.getStatus()))) {
            TopicStatistics stat = new TopicStatistics();
            stat.setTopic(topic);
            stat.setEntityStatistics(new ArrayList<>());
            stat.setChildren(new ArrayList<>());
            stats.put(topic.getId(), stat);
            // }
        });

        // Then populate the TopicStatistics objects with the actual figures.
        List<TopicStatisticsDto> rawStats = statisticsRepository.getTopicStatistics(filter);
        for (TopicStatisticsDto dto : rawStats) {
            TopicStatistics stat = stats.get(dto.getTopicId());
            stat.getEntityStatistics().add(new EntityStatistics(dto.getEntityKind(), dto.getCount()));
        }

        // Transform the TopicStatistics objects into a tree reflective of the topic hierarchy.
        for (TopicStatistics stat : stats.values()) {
            if (matchesFilter(stat.getTopic(), filter)) {
                Topic parent = stat.getTopic().getParent();
                if (parent != null && matchesFilter(parent, filter)) {
                    Long parentId = parent.getId();
                    stats.get(parentId).getChildren().add(stat);
                }
            }
        }
        // Trim sub-topic stats from the top level.
        Iterator<TopicStatistics> itr = stats.values().iterator();
        while (itr.hasNext()) {
            TopicStatistics stat = itr.next();
            if (!matchesFilter(stat.getTopic(), filter) || stat.getTopic().getParent() != null)
                itr.remove();
        }

        return stats.values();
    }

}
