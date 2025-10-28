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

import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateEntityStatistics;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.EntityStatistics;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateEntityStatisticsImpl implements DataFetchersDelegateEntityStatistics {

    @Resource
    protected FormatUtils formatUtils;

    @Override
    public Object entityKind(DataFetchingEnvironment dataFetchingEnvironment, EntityStatistics origin,
        FormatKind format) {

        // This is a bit of a kludge to handle TLT = 'Topic (top-level)'
        String entityKindStr = origin.getEntityKind();
        if (format == FormatKind.LONG) {
            boolean isTopicLevelTopic = entityKindStr.equals("TLT");
            EntityKind entityKind = EntityKind.valueOf(isTopicLevelTopic ? "TOP" : entityKindStr);
            entityKindStr = formatUtils.formatEntityKind(entityKind, format);
            if (isTopicLevelTopic && format == FormatKind.LONG)
                entityKindStr += " (top-level)";
        }
        return entityKindStr;
    }

}
