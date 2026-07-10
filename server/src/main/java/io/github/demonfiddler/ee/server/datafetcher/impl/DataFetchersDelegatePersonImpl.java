/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegatePerson;
import io.github.demonfiddler.ee.server.model.CountryFormatKind;
import io.github.demonfiddler.ee.server.model.Person;
import io.github.demonfiddler.ee.server.repository.CommentRepository;
import io.github.demonfiddler.ee.server.repository.EntityLinkRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.util.CountryUtils;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;

@Component
public class DataFetchersDelegatePersonImpl extends DataFetchersDelegateILinkableEntityBaseImpl<Person>
    implements DataFetchersDelegatePerson {

    private final CountryUtils countryUtils;
    private final PersonRepository personRepository;

    public DataFetchersDelegatePersonImpl(CommentRepository commentRepository, LogRepository logRepository,
        EntityUtils entityUtils, FormatUtils formatUtils, SecurityUtils securityUtils,
        EntityLinkRepository entityLinkRepository, CountryUtils countryUtils, PersonRepository personRepository) {

        super(commentRepository, logRepository, entityUtils, formatUtils, securityUtils, entityLinkRepository);
        this.countryUtils = countryUtils;
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return personRepository.findAllById(keys);
    }

    @Override
    public Object country(DataFetchingEnvironment dataFetchingEnvironment, Person origin, CountryFormatKind format) {
        return countryUtils.formatCountry(origin.getCountry(), format);
    }

}
