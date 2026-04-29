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
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.client.Journal;
import io.github.demonfiddler.ee.client.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.client.Publication;
import io.github.demonfiddler.ee.client.PublicationInput;
import io.github.demonfiddler.ee.client.PublicationKind;
import io.github.demonfiddler.ee.client.PublicationPage;
import io.github.demonfiddler.ee.client.Publisher;
import io.github.demonfiddler.ee.client.StatusKind;
import io.github.demonfiddler.ee.client.util.Authenticator;
import io.github.demonfiddler.ee.client.util.MutationExecutor;
import io.github.demonfiddler.ee.client.util.QueryExecutor;

/**
 * Runner to update {@code publication.publisher} to match {@code publication.journal.publisher}.
 */
@Component
public class UpdatePublicationsRunner extends AbstractClientRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePublicationsRunner.class);
    private static final String RESP_PUBLICATION_PAGE = """
        {
            hasNext
            content {
                id
                abstract
                accessed
                arxivid
                authors
                biorxivid
                cached
                date
                doi
                ericid
                halid
                hsid
                ihepid
                isbn
                journal {
                    id
                    title
                    publisher {
                        id
                        name
                    }
                }
                keywords
                kind
                medrxivid
                notes
                oaipmhid
                peerReviewed
                pinfoan
                pmcid
                pmid
                publisher {
                    id
                    name
                }
                rating
                scopuseid
                title
                url
                wsan
                year
                zenodoid
            }
        }
        """;
    private static final String RESP_PUBLICATION = """
        {
            id
            publisher {
                id
            }
        }
        """;

    public UpdatePublicationsRunner(ConfigurableApplicationContext context, Authenticator authenticator,
        QueryExecutor queryExecutor, MutationExecutor mutationExecutor) {

        super(context, authenticator, queryExecutor, mutationExecutor);
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    @Override
    void doRun() throws Exception {
        if (cmdline.hasOption(OPT_UPDATE_PUBLICATIONS)) {
            // Process command line arg values.
            List<StatusKind> status = parseStatus();
            LinkableEntityQueryFilter linkableEntityQueryFilter = status != null || topicId != null
                    ? LinkableEntityQueryFilter.builder().withStatus(status).withTopicId(topicId)
                            .withRecursive(recursive).build() //
                    : null;

            List<Publication> publications = readPaged(PublicationPage.class, linkableEntityQueryFilter,
                (f, p) -> queryExecutor.publications(RESP_PUBLICATION_PAGE, f, p)).getContent();

            LOGGER.trace("Loaded {} Publications", publications.size());

            int updatedCount = 0;
            for (Publication publication : publications) {
                Publisher publicationPublisher = publication.getPublisher();
                Long publicationPublisherId = publicationPublisher != null ? publicationPublisher.getId() : null;
                Journal journal = publication.getJournal();
                Publisher journalPublisher = journal != null ? journal.getPublisher() : null;
                Long journalPublisherId = journalPublisher != null ? journalPublisher.getId() : null;

                // Do we need to update this Publication's publisher field?
                if (journalPublisherId == null // no publication.journal, so there's nothing we can do anyway
                    || publicationPublisherId != null && !force // publication.publisher is set and we're not forcing
                    || Objects.equals(publicationPublisherId, journalPublisherId)) { // publisherIds are equal

                    LOGGER.info("Publication #{} publisher is already correct, skipping", publication.getId());
                    continue;
                }

                if (!dryRun) {
                    // N.B. THIS CODE MUST BE KEPT UP TO DATE WITH ALL Publisher FIELDS!
                    PublicationInput input = PublicationInput.builder() //
                        .withAbstract(publication.getAbstract()) //
                        .withAccessed(publication.getAccessed()) //
                        .withArxivid(publication.getArxivid()) //
                        .withAuthorNames(publication.getAuthors()) //
                        .withBiorxivid(publication.getBiorxivid()) //
                        .withCached(publication.getCached()) //
                        .withDate(publication.getDate()) //
                        .withDoi(publication.getDoi()) //
                        .withEricid(publication.getEricid()) //
                        .withHalid(publication.getHalid()) //
                        .withHsid(publication.getHsid()) //
                        .withId(publication.getId()) //
                        .withIhepid(publication.getIhepid()) //
                        .withIsbn(publication.getIsbn()) //
                        .withJournalId(journal != null ? journal.getId() : null) //
                        .withKeywords(publication.getKeywords()) //
                        .withKind(PublicationKind.valueOf(publication.getKind())) //
                        .withMedrxivid(publication.getMedrxivid()) //
                        .withNotes(publication.getNotes()) //
                        .withOaipmhid(publication.getOaipmhid()) //
                        .withPeerReviewed(publication.getPeerReviewed()) //
                        .withPinfoan(publication.getPinfoan()) //
                        .withPmcid(publication.getPmcid()) //
                        .withPmid(publication.getPmid()) //
                        .withPublisherId(journalPublisherId) //
                        .withRating(publication.getRating()) //
                        .withScopuseid(publication.getScopuseid()) //
                        .withTitle(publication.getTitle()) //
                        .withUrl(publication.getUrl()) //
                        .withWsan(publication.getWsan()) //
                        .withYear(publication.getYear()) //
                        .withZenodoid(publication.getZenodoid()) //
                        .build();
                    mutationExecutor.updatePublication(RESP_PUBLICATION, input);
                }
                updatedCount++;

                if (journalPublisher != null) { // (it cannot ever be null but the compiler is stupid)
                    LOGGER.info("Updated Publication #{} publisher to '#{}: {}'", publication.getId(),
                        journalPublisher.getId(), journalPublisher.getName());
                }
            }

            LOGGER.info("Updated {} of {} Publications", updatedCount, publications.size());
        }
    }

}
