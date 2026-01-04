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

import org.dataloader.DataLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import graphql.schema.DataFetchingEnvironment;
import io.github.demonfiddler.ee.server.model.EntityLink;
import io.github.demonfiddler.ee.server.model.EntityLinkPage;
import io.github.demonfiddler.ee.server.model.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.ILinkableEntity;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.repository.EntityLinkRepository;
import jakarta.annotation.Resource;

/**
 * Base class containing implementations of common methods. It is necessary because the generated DataFetchersDelegate*
 * class hierachy does not reflect the inheritance expressed in the GraphQL schema.
 */
abstract class DataFetchersDelegateILinkableEntityBaseImpl<T extends ILinkableEntity>
    extends DataFetchersDelegateITrackedEntityBaseImpl<T> {

    @Resource
    protected EntityLinkRepository entityLinkRepository;

    /**
     * Outbound links for which {@code origin} is the 'linked-from' entity.
     * @param dataFetchingEnvironment
	 * @param dataLoader
     * @param origin
     * @param filter
     * @param pageSort
     * @return
     */
    public final Object fromEntityLinks(DataFetchingEnvironment dataFetchingEnvironment,
        DataLoader<Long, EntityLinkPage> dataLoader, T origin, LinkableEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityLinks(dataFetchingEnvironment, dataLoader, origin, filter, pageSort, true);
    }

    /**
     * Inbound links for which {@code origin} is the 'linked-to' entity.
     * @param dataFetchingEnvironment
	 * @param dataLoader
     * @param origin
     * @param filter
     * @param pageSort
     * @return
     */
    public final Object toEntityLinks(DataFetchingEnvironment dataFetchingEnvironment,
        DataLoader<Long, EntityLinkPage> dataLoader, T origin, LinkableEntityQueryFilter filter,
        PageableInput pageSort) {

        return entityLinks(dataFetchingEnvironment, dataLoader, origin, filter, pageSort, false);
    }

    private Object entityLinks(DataFetchingEnvironment dataFetchingEnvironment,
        DataLoader<Long, EntityLinkPage> dataLoader, T origin, LinkableEntityQueryFilter filter, PageableInput pageSort,
        boolean fromEntityLinks) {

        filter = fixFilter(origin, filter, fromEntityLinks);
        Pageable pageable = entityUtils.toPageable(pageSort);
        Page<EntityLink> page = entityLinkRepository.findByFilter(filter, pageable);
        return entityUtils.toEntityPage(page, EntityLinkPage::new);
    }

    private LinkableEntityQueryFilter fixFilter(T origin, LinkableEntityQueryFilter filter, boolean fromEntityLinks) {
        if (filter == null)
            filter = new LinkableEntityQueryFilter();
        // Unauthenticated queries should only return published links.
        if (securityUtils.getCurrentUsername().equals("anonymousUser"))
            filter.setStatus(List.of(StatusKind.PUB));
        if (fromEntityLinks)
            filter.setFromEntityId(origin.getId());
        else
            filter.setToEntityId(origin.getId());
        return filter;
    }

}
