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

import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateClaim;
import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.CommentRepository;
import io.github.demonfiddler.ee.server.repository.EntityLinkRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.FormatUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;

@Component
public class DataFetchersDelegateClaimImpl extends DataFetchersDelegateILinkableEntityBaseImpl<Claim>
    implements DataFetchersDelegateClaim {

    private final ClaimRepository claimRepository;

    public DataFetchersDelegateClaimImpl(CommentRepository commentRepository, LogRepository logRepository,
        EntityUtils entityUtils, FormatUtils formatUtils, SecurityUtils securityUtils,
        EntityLinkRepository entityLinkRepository, ClaimRepository claimRepository) {

        super(commentRepository, logRepository, entityUtils, formatUtils, securityUtils, entityLinkRepository);
        this.claimRepository = claimRepository;
    }

    @Override
    public List<Claim> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return claimRepository.findAllById(keys);
    }

}
