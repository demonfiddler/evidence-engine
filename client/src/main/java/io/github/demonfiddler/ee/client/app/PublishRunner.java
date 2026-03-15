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

package io.github.demonfiddler.ee.client.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.client.ClaimPage;
import io.github.demonfiddler.ee.client.CommentPage;
import io.github.demonfiddler.ee.client.CommentQueryFilter;
import io.github.demonfiddler.ee.client.DeclarationPage;
import io.github.demonfiddler.ee.client.EntityAudit;
import io.github.demonfiddler.ee.client.EntityKind;
import io.github.demonfiddler.ee.client.EntityLinkPage;
import io.github.demonfiddler.ee.client.EntityLinkQueryFilter;
import io.github.demonfiddler.ee.client.ITrackedEntity;
import io.github.demonfiddler.ee.client.JournalPage;
import io.github.demonfiddler.ee.client.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.client.PersonPage;
import io.github.demonfiddler.ee.client.PublicationPage;
import io.github.demonfiddler.ee.client.PublisherPage;
import io.github.demonfiddler.ee.client.QuotationPage;
import io.github.demonfiddler.ee.client.StatusKind;
import io.github.demonfiddler.ee.client.TopicPage;
import io.github.demonfiddler.ee.client.TopicQueryFilter;
import io.github.demonfiddler.ee.client.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.client.util.Authenticator;
import io.github.demonfiddler.ee.client.util.MutationExecutor;
import io.github.demonfiddler.ee.client.util.QueryExecutor;

@Component
public class PublishRunner extends AbstractClientRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishRunner.class);
    // HACK ALERT: GraphQL Java Generator is so broken that it can't handle a return type of interface ITrackedEntity.
    private static final String RESP_TRACKED_ENTITY = """
        {
            ... on Claim {
                id
                status(format: SHORT)
            }
            ... on Comment {
                id
                status(format: SHORT)
            }
            ... on Declaration {
                id
                status(format: SHORT)
            }
            ... on EntityLink {
                id
                status(format: SHORT)
            }
            ... on Journal {
                id
                status(format: SHORT)
            }
            ... on Person {
                id
                status(format: SHORT)
            }
            ... on Publication {
                id
                status(format: SHORT)
            }
            ... on Publisher {
                id
                status(format: SHORT)
            }
            ... on Quotation {
                id
                status(format: SHORT)
            }
            ... on Topic {
                id
                status(format: SHORT)
            }
        }
        """;
    private static final String RESP_TRACKED_ENTITY_PAGE = """
        {
            hasNext
            content {
                id
                status(format: SHORT)
            }
        }
        """;
    private static final String RESP_AUDIT = """
        {
            pass
        }
        """;

    public PublishRunner(ConfigurableApplicationContext context, Authenticator authenticator,
        QueryExecutor queryExecutor, MutationExecutor mutationExecutor) {

        super(context, authenticator, queryExecutor, mutationExecutor);
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    @Override
    void doRun() throws Exception {
        if (cmdline.hasOption(OPT_PUBLISH)) {
            // Process command line arg values.
            List<EntityKind> entityKinds = parseEntityKinds(OPT_PUBLISH);
            List<StatusKind> status = parseStatus();
            TrackedEntityQueryFilter trackedEntityQueryFilter = status != null
                    ? TrackedEntityQueryFilter.builder().withStatus(status).build() //
                    : null;
            LinkableEntityQueryFilter linkableEntityQueryFilter = status != null || topicId != null
                    ? LinkableEntityQueryFilter.builder().withStatus(status).withTopicId(topicId)
                            .withRecursive(recursive).build() //
                    : null;

            int totalEntityCount = 0;
            int totalPublishedCount = 0;
            for (EntityKind entityKind : entityKinds) {
                List<? extends ITrackedEntity> entities;
                switch (entityKind) {
                    case CLA:
                        entities = readPaged(ClaimPage.class, linkableEntityQueryFilter,
                            (f, p) -> queryExecutor.claims(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case COM:
                        CommentQueryFilter commentQueryFilter = status != null //
                            ? CommentQueryFilter.builder().withStatus(status).build() //
                            : null;
                        entities = readPaged(CommentPage.class, commentQueryFilter,
                            (f, p) -> queryExecutor.comments(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case DEC:
                        entities = readPaged(DeclarationPage.class, linkableEntityQueryFilter,
                            (f, p) -> queryExecutor.declarations(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case JOU:
                        entities = readPaged(JournalPage.class, trackedEntityQueryFilter,
                            (f, p) -> queryExecutor.journals(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case LNK:
                        EntityLinkQueryFilter entityLinkQueryFilter = status != null //
                            ? EntityLinkQueryFilter.builder().withStatus(status).build() //
                            : null;
                        entities = readPaged(EntityLinkPage.class, entityLinkQueryFilter,
                            (f, p) -> queryExecutor.entityLinks(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case PER:
                        entities = readPaged(PersonPage.class, linkableEntityQueryFilter,
                            (f, p) -> queryExecutor.persons(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case PUB:
                        entities = readPaged(PublicationPage.class, linkableEntityQueryFilter,
                            (f, p) -> queryExecutor.publications(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case PBR:
                        entities = readPaged(PublisherPage.class, trackedEntityQueryFilter,
                            (f, p) -> queryExecutor.publishers(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case QUO:
                        entities = readPaged(QuotationPage.class, linkableEntityQueryFilter,
                            (f, p) -> queryExecutor.quotations(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    case TOP:
                        TopicQueryFilter topicQueryFilter = status != null //
                            ? TopicQueryFilter.builder().withStatus(status).build() //
                            : null;
                        entities = readPaged(TopicPage.class, topicQueryFilter,
                            (f, p) -> queryExecutor.topics(RESP_TRACKED_ENTITY_PAGE, f, p)).getContent();
                        break;
                    default:
                        LOGGER.warn("Unsupported entity kind: {}", entityKind);
                        continue;
                }

                LOGGER.trace("Loaded {} {}s", entities.size(), entityKind.label());

                int publishedCount = 0;
                for (ITrackedEntity entity : entities) {
                    StatusKind entityStatus = StatusKind.valueOf(entity.getStatus());
                    if (entityStatus == StatusKind.PUB) {
                        LOGGER.warn("{} #{} is already published, skipping", entityKind.label(), entity.getId());
                        continue;
                    }

                    // Make sure the record passes audit before publishing.
                    EntityAudit audit = queryExecutor.audit(RESP_AUDIT, entity.getId());
                    if (!Boolean.TRUE.equals(audit.getPass())) {
                        LOGGER.warn("{} #{} failed audit, skipping", entityKind.label(), entity.getId());
                        continue;
                    }

                    if (!dryRun)
                        mutationExecutor.setEntityStatus(RESP_TRACKED_ENTITY, entity.getId(), StatusKind.PUB);
                    publishedCount++;

                    LOGGER.trace("Published {} #{}", entityKind.label(), entity.getId());
                }
                totalEntityCount += entities.size();
                totalPublishedCount += publishedCount;

                LOGGER.info("Published {} of {} {}s", publishedCount, entities.size(), entityKind.label());
            }
            LOGGER.info("Published {} of {} records", totalPublishedCount, totalEntityCount);
        }
    }

}
