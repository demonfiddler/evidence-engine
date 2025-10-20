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
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import io.github.demonfiddler.ee.server.datafetcher.DataFetchersDelegateComment;
import io.github.demonfiddler.ee.server.model.Comment;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.repository.CommentRepository;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateCommentImpl extends DataFetchersDelegateITrackedEntityBaseImpl<Comment>
    implements DataFetchersDelegateComment {

    @Resource
    private CommentRepository commentRepository;
    // @Resource
    // protected SecurityUtils securityUtils;

    public static String getCommentText(Comment comment) {
        return comment.getStatus().equals(StatusKind.DEL.name())
            // && !comment.getCreatedByUser().getUsername().equals(securityUtils.getCurrentUsername())
            // && !securityUtils.hasAuthority(AuthorityKind.ADM) //
            ? "-COMMENT DELETED-" //
            : comment.getText();
    }

    @Override
    public List<Comment> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment environment) {
        return commentRepository.findAllById(keys);
    }

    @Override
    public Map<Comment, ITrackedEntity> target(BatchLoaderEnvironment batchLoaderEnvironment,
        GraphQLContext graphQLContext, List<Comment> keys) {

        return entityUtils.getValuesMap(keys, Comment::getTarget);
    }

    @Override
    public Map<Comment, Comment> parent(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Comment> keys) {

        return entityUtils.getValuesMap(keys, Comment::getParent);
    }

    @Override
    public Map<Comment, String> text(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
        List<Comment> keys) {

        return entityUtils.getValuesMap(keys, DataFetchersDelegateCommentImpl::getCommentText);
    }

}
