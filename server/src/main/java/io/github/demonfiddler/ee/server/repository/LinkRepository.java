/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
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

package io.github.demonfiddler.ee.server.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.github.demonfiddler.ee.server.model.LinkEntitiesInput;
import io.github.demonfiddler.ee.server.model.TopicRef;
import io.github.demonfiddler.ee.server.model.TopicRefInput;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * Provides database access for cross-entity references that don't have a corresponding JPA entity class. Such links
 * include topic:entity and entity:entity references, both implemented via association tables specific to the entity
 * type pairs involved.
 */
@Repository
public class LinkRepository {

    @Resource
    EntityManager em;
    @Resource
    EntityUtils util;

    public List<TopicRef> findByIds(List<Long> keys) {
        // TODO: figure out how to model topic references in the GraphQL schema.
        throw new UnsupportedOperationException("findByIds(List<Long>)");
    }

    /**
     * Adds a new topic:entity reference.
     * @param topicRef The topic reference to add.
     * @return The count of reference records inserted.
     */
    public int addTopicRef(TopicRefInput topicRef) {
        String template = """
                        INSERT IGNORE INTO `topic_%s_ref`
                            (`topic_id`, `%s_id`, `locations`)
                        VALUES
                            (:topicId, :entityId, :locations);
                        """;
        String entityName = util.getEntityName(topicRef.getEntityKind());
        String sql = String.format(template, entityName, entityName);
        Query query = em.createNativeQuery(sql, getClass());
        query.setParameter("topicId", topicRef.getTopicId()).setParameter("entityId", topicRef.getEntityId());
        return query.executeUpdate();
    }

    /**
     * Removes an existing topic:entity reference.
     * @param topicRef The topic reference to remove.
     * @return The count of reference records deleted.
     */
    public int removeTopicRef(TopicRefInput topicRef) {
        String template = """
                        DELETE FROM `topic_%s_ref`
                        WHERE
                            `topic_id` = :topicId
                            AND `%s_id` = :entityId;
                        """;
        String entityName = util.getEntityName(topicRef.getEntityKind());
        String sql = String.format(template, entityName, entityName);
        Query query = em.createNativeQuery(sql, getClass());
        query.setParameter("topicId", topicRef.getTopicId()).setParameter("entityId", topicRef.getEntityId());
        return query.executeUpdate();
    }

    /**
     * Creates an association between two entities. Note that the 'from' and 'to' entities must be chosen to reflect the
     * name of the association table for that entity pair. Association tables are named
     * {@literal <from-entity-name>_<to-entity-name>}.
     * @param linkInput Details of 'from' and 'to' entities to link.
     * @return The number of association records inserted.
     */
    public int linkEntities(LinkEntitiesInput linkInput) {
        String template = """
                        INSERT IGNORE INTO `%s_%s`
                            (`%s_id`, `%s_id`)
                        VALUES
                            (:fromEntityId, :toEntityId);
                        """;
        String fromEntityName = util.getEntityName(linkInput.getFromEntityKind());
        String toEntityName = util.getEntityName(linkInput.getToEntityKind());
        String sql = String.format(template, fromEntityName, toEntityName, fromEntityName, toEntityName);
        Query query = em.createNativeQuery(sql);
        query.setParameter("entityId", linkInput.getFromEntityId()).setParameter("toEntityId",
            linkInput.getToEntityId());
        return query.executeUpdate();
    }

    /**
     * Removes an association between two entities. Note that the 'from' and 'to' entities must be chosen to reflect the
     * name of the association table for that entity pair. Association tables are named
     * {@literal <from-entity-name>_<to-entity-name>}.
     * @param linkInput Details of 'from' and 'to' entities to unlink.
     * @return The number of association records deleted.
     */
    public int unlinkEntities(LinkEntitiesInput linkInput) {
        String template = """
                        DELETE FROM `%s_%s`
                        WHERE
                            `%s_id` = :fromEntityId
                            AND `%s_id` = :toEntityId;
                        """;
        String fromEntityName = util.getEntityName(linkInput.getFromEntityKind());
        String toEntityName = util.getEntityName(linkInput.getToEntityKind());
        String sql = String.format(template, fromEntityName, toEntityName, fromEntityName, toEntityName);
        Query query = em.createNativeQuery(sql);
        query.setParameter("fromEntityId", linkInput.getFromEntityId()).setParameter("toEntityId",
            linkInput.getToEntityId());
        return query.executeUpdate();
    }

}
