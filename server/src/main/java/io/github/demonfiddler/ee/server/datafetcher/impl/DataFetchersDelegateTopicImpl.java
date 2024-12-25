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

package io.github.demonfiddler.ee.server.datafetcher.impl;

import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateTopic;
import io.github.demonfiddler.ee.server.model.ClaimPage;
import io.github.demonfiddler.ee.server.model.DeclarationPage;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.IBaseEntityPage;
import io.github.demonfiddler.ee.server.model.LogPage;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.PersonPage;
import io.github.demonfiddler.ee.server.model.PublicationPage;
import io.github.demonfiddler.ee.server.model.QuotationPage;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicalEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.DeclarationRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.repository.PublicationRepository;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateTopicImpl extends DataFetchersDelegateITrackedEntityBaseImpl
    implements DataFetchersDelegateTopic {

    @Resource
    private ClaimRepository claimRepository;
    @Resource
    private DeclarationRepository declarationRepository;
    @Resource
    private PersonRepository personRepository;
    @Resource
    private PublicationRepository publicationRepository;
    @Resource
    private QuotationRepository quotationRepository;
    @Resource
    private TopicRepository topicRepository;

    @Override
    public List<Topic> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return topicRepository.findByIds(keys);
    }

    @Override
    public Object status(DataFetchingEnvironment dataFetchingEnvironment, Topic origin, FormatKind format) {
        return _status(dataFetchingEnvironment, origin, format);
    }

    @Override
    public Map<Topic, User> createdByUser(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Topic> keys) {

        return _createdByUserMap(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Map<Topic, User> updatedByUser(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Topic> keys) {

        return _updatedByUserMap(batchLoaderEnvironment, graphQLContext, keys);
    }

    @Override
    public Object log(DataFetchingEnvironment dataFetchingEnvironment, DataLoader<Long, LogPage> dataLoader,
        Topic origin, LogQueryFilter filter, PageableInput pageSort) {

        return _log(dataFetchingEnvironment, origin, filter, pageSort);
    }

    @Override
    public Map<Topic, Topic> parent(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Topic> keys) {

        return entityUtils.getValuesMap(keys, Topic::getParent);
    }

    @Override
    public Map<Topic, List<Topic>> children(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Topic> keys) {

        return entityUtils.getListValuesMap(keys, Topic::getChildren);
    }

    @Override
    public Object entities(DataFetchingEnvironment dataFetchingEnvironment, Topic origin, EntityKind entityKind,
        TopicalEntityQueryFilter filter, PageableInput pageSort) {

        filter = fixFilter(origin, filter);
        Pageable pageable = entityUtils.toPageable(pageSort);

        IBaseEntityPage<?> results;
        switch (entityKind) {
            case CLA:
                results = entityUtils.toEntityPage(claimRepository.findByFilter(filter, pageable), ClaimPage::new);
                break;
            case DEC:
                results = entityUtils.toEntityPage(declarationRepository.findByFilter(filter, pageable),
                    DeclarationPage::new);
                break;
            case PER:
                results = entityUtils.toEntityPage(personRepository.findByFilter(filter, pageable), PersonPage::new);
                break;
            case PUB:
                results = entityUtils.toEntityPage(publicationRepository.findByFilter(filter, pageable),
                    PublicationPage::new);
                break;
            case QUO:
                results =
                    entityUtils.toEntityPage(quotationRepository.findByFilter(filter, pageable), QuotationPage::new);
                break;
            default:
                throw new IllegalArgumentException("entityKind must be CLA, DEC, PER, PUB or QUO");
        }
        return results;
    }

    private TopicalEntityQueryFilter fixFilter(Topic topic, TopicalEntityQueryFilter filter) {
        if (filter == null)
            filter = new TopicalEntityQueryFilter();
        filter.setTopicId(topic.getId());
        return filter;
    }

}
