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

import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePublisher;
import io.github.demonfiddler.ee.server.model.CountryFormatKind;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.util.CountryUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegatePublisherImpl extends DataFetchersDelegateITrackedEntityBaseImpl<Publisher>
    implements DataFetchersDelegatePublisher {

    @Resource
    private CountryUtils countryUtils;
    @Resource
    private PublisherRepository publisherRepository;

    @Override
    public List<Publisher> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return publisherRepository.findAllById(keys);
    }

    @Override
    public Object country(DataFetchingEnvironment dataFetchingEnvironment, Publisher origin, CountryFormatKind format) {
        return countryUtils.formatCountry(origin.getCountry(), format);
    }

}
