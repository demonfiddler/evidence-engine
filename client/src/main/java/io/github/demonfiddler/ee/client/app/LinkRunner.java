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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import io.github.demonfiddler.ee.client.Claim;
import io.github.demonfiddler.ee.client.ClaimPage;
import io.github.demonfiddler.ee.client.EntityKind;
import io.github.demonfiddler.ee.client.EntityLink;
import io.github.demonfiddler.ee.client.EntityLinkInput;
import io.github.demonfiddler.ee.client.ILinkableEntity;
import io.github.demonfiddler.ee.client.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.client.Person;
import io.github.demonfiddler.ee.client.PersonPage;
import io.github.demonfiddler.ee.client.Publication;
import io.github.demonfiddler.ee.client.PublicationPage;
import io.github.demonfiddler.ee.client.Quotation;
import io.github.demonfiddler.ee.client.QuotationPage;
import io.github.demonfiddler.ee.client.StatusKind;
import io.github.demonfiddler.ee.client.Topic;
import io.github.demonfiddler.ee.client.TopicPage;
import io.github.demonfiddler.ee.client.TopicQueryFilter;
import io.github.demonfiddler.ee.client.util.Authenticator;
import io.github.demonfiddler.ee.client.util.MutationExecutor;
import io.github.demonfiddler.ee.client.util.QueryExecutor;

/**
 * Links Publications and Quotations to Claim Topics, and Persons to Claims and Topics linked to authored Publications.
 */
@Component
public class LinkRunner extends AbstractClientRunner {

    /**
     * Tracks entity create/update/delete statistics during entity link
     * normalization.
     */
    private static final class LinkData {

        int addedClaimLinkCount;
        int addedTopicLinkCount;
        int deletedTopicLinkCount;
        Set<Long> claimIds;
        Set<Long> topicIds;

        void add(LinkData other) {
            addedClaimLinkCount += other.addedClaimLinkCount;
            addedTopicLinkCount += other.addedTopicLinkCount;
            deletedTopicLinkCount += other.deletedTopicLinkCount;
            if (other.claimIds != null) {
                if (claimIds == null)
                    claimIds = new HashSet<>();
                claimIds.addAll(other.claimIds);
            }
            if (other.topicIds != null) {
                if (topicIds == null)
                    topicIds = new HashSet<>();
                topicIds.addAll(other.topicIds);
            }
        }

        @Override
        public String toString() {
            return "LinkData [addedClaimLinkCount=" + addedClaimLinkCount + ", addedTopicLinkCount="
                    + addedTopicLinkCount + ", deletedTopicLinkCount="
                    + deletedTopicLinkCount + ", claimIds=" + claimIds + ", topicIds=" + topicIds + "]";
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkRunner.class);
    private static final String FRAGMENT_PAGE_FIELDS = """
                hasContent
                hasNext
                numberOfElements
            """;
    private static final String FRAGMENT_TRACKED_ENTITY_FIELDS = """
                    entityKind(format: SHORT)
                    status(format: SHORT)
            """;
    private static final String FRAGMENT_LINKED_ENTITY_FIELDS = """
            ... on Claim {
                id
                entityKind(format: SHORT)
                status(format: SHORT)
                text
            }
            ... on Declaration {
                id
                entityKind(format: SHORT)
                status(format: SHORT)
                title
            }
            ... on Person {
                id
                entityKind(format: SHORT)
                status(format: SHORT)
                title
                firstName
                prefix
                lastName
                suffix
            }
            ... on Publication {
                id
                entityKind(format: SHORT)
                status(format: SHORT)
                title
            }
            ... on Quotation {
                id
                entityKind(format: SHORT)
                status(format: SHORT)
                text
            }
            ... on Topic {
                id
                entityKind(format: SHORT)
                status(format: SHORT)
                label
            }
            """;
    private static final String FRAGMENT_LINKABLE_ENTITY_FIELDS = """
                fromEntityLinks {
                    content {
                        id
                        status(format: SHORT)
                        toEntity {
                            %s
                        }
                    }
                }
                toEntityLinks {
                    content {
                        id
                        status(format: SHORT)
                        fromEntity {
                            %s
                        }
                    }
                }
            """.formatted(FRAGMENT_LINKED_ENTITY_FIELDS, FRAGMENT_LINKED_ENTITY_FIELDS);
    private static final String FRAGMENT_ENTITY_LINK_FIELDS = """
                fromEntity {
            %s
                }
                toEntity {
            %s
                }
            """.formatted(FRAGMENT_LINKED_ENTITY_FIELDS, FRAGMENT_LINKED_ENTITY_FIELDS);
    private static final String FRAGMENT_SUBTOPIC_FIELDS = """
            id
            status(format: SHORT)
            label
            """.formatted(FRAGMENT_TRACKED_ENTITY_FIELDS);
    private static final Object[] SUBTOPIC_FIELDS_ARRAY = new String[10];
    static {
        Arrays.fill(SUBTOPIC_FIELDS_ARRAY, FRAGMENT_SUBTOPIC_FIELDS);
    }
    private static final String FRAGMENT_TOPIC_FIELDS_RECURSIVE = """
            children {
            %s
                children {
            %s
                    children {
            %s
                        children {
            %s
                            children {
            %s
                                children {
            %s
                                    children {
            %s
                                        children {
            %s
                                            children {
            %s
                                                children {
            %s
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            """.formatted(SUBTOPIC_FIELDS_ARRAY);
    private static final String FRAGMENT_TOPIC_HIERARCHY_FIELDS = """
            %s
            %s
            """.formatted(FRAGMENT_SUBTOPIC_FIELDS, FRAGMENT_TOPIC_FIELDS_RECURSIVE);
    private static final String RESP_LINKABLE_ENTITIES = """
            {
            %s
                content {
                    id
            %s
            %s
                }
            }
            """.formatted(FRAGMENT_PAGE_FIELDS, FRAGMENT_TRACKED_ENTITY_FIELDS, FRAGMENT_LINKABLE_ENTITY_FIELDS);
    private static final String RESP_TOPICS = """
            {
            %s
                content {
            %s
                }
            }
            """.formatted(FRAGMENT_PAGE_FIELDS, FRAGMENT_TOPIC_HIERARCHY_FIELDS);
    private static final String RESP_ENTITY_LINK = """
            {
            %s
            %s
            }
            """.formatted(FRAGMENT_TRACKED_ENTITY_FIELDS, FRAGMENT_ENTITY_LINK_FIELDS);

    private Map<Long, Claim> claimsById = new HashMap<>();
    private Map<Long, Person> personsById = new HashMap<>();
    private Map<Long, Topic> topicsById = new HashMap<>();
    private Map<Long, Set<Long>> requiredClaimIdsByPersonId = new HashMap<>();
    private Map<Long, Set<Long>> requiredTopicIdsByPersonId = new HashMap<>();
    private Set<Long> personIds = new HashSet<>();

    public LinkRunner(ConfigurableApplicationContext context, Authenticator authenticator, QueryExecutor queryExecutor,
            MutationExecutor mutationExecutor) {

        super(context, authenticator, queryExecutor, mutationExecutor);
    }

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    /**
     * Ensures that Publication and Person linking is consistent. Ensures that
     * Publications are linked to the same
     * Topics as their Claims are linked to and that Persons are linked to the same
     * Claims and Topics as the
     * Publications they have authored.
     */
    @Override
    void doRun() throws Exception {
        if (cmdline.hasOption(OPT_LINK)) {
            // Process command line arg values.
            List<EntityKind> linkKinds = parseEntityKinds(OPT_LINK);
            List<StatusKind> status = parseStatus();
            LinkableEntityQueryFilter entityFilter = status != null || topicId != null
                    ? LinkableEntityQueryFilter.builder().withStatus(status).withTopicId(topicId)
                            .withRecursive(recursive).build() //
                    : null;

            List<Claim> claims = readPaged(ClaimPage.class, entityFilter,
                    (f, p) -> queryExecutor.claims(RESP_LINKABLE_ENTITIES, f, p)).getContent();
            claims.forEach(claim -> claimsById.put(claim.getId(), claim));
            LOGGER.trace("Loaded {} Claims", claims.size());

            List<Person> persons = readPaged(PersonPage.class, entityFilter,
                    (f, p) -> queryExecutor.persons(RESP_LINKABLE_ENTITIES, f, p)).getContent();
            persons.forEach(person -> personsById.put(person.getId(), person));
            LOGGER.trace("Loaded {} Persons", persons.size());

            TopicQueryFilter topicFilter = TopicQueryFilter.builder().withStatus(status).withParentId(-1L).build();
            List<Topic> topics = readPaged(TopicPage.class, topicFilter,
                    (f, p) -> queryExecutor.topics(RESP_TOPICS, f, p)).getContent();
            int topicCount = indexTopics(null, topics);
            LOGGER.trace("Loaded {} top-level Topics, total {}", topics.size(), topicCount);

            LinkData runLinkData = new LinkData();
            LinkData publicationLinkData = null;
            LinkData quotationLinkData = null;
            LinkData personLinkData = null;

            if (linkKinds.contains(EntityKind.PUB)) {
                // Fetch ALL Publications and index them.
                List<Publication> publications = readPaged(PublicationPage.class, entityFilter,
                        (f, p) -> queryExecutor.publications(RESP_LINKABLE_ENTITIES, f, p)).getContent();
                LOGGER.trace("Loaded {} Publications", publications.size());

                publicationLinkData = processLinkableEntities(EntityKind.PUB, publications);
                runLinkData.add(publicationLinkData);
            }

            // WARNING: If Quotation-Claim-Topic links are coarser-grained than
            // Publication-Claim-Topic links, this will
            // have the undesirable effect of making the resulting Person-Topic links
            // coarser-grained than they would
            // otherwise be (i.e., linked to higher-level ancestor Topics rather than
            // lower-level sub-Topics).
            if (linkKinds.contains(EntityKind.QUO)) {
                // Fetch ALL Quotations and index them.
                List<Quotation> quotations = readPaged(QuotationPage.class, entityFilter,
                        (f, p) -> queryExecutor.quotations(RESP_LINKABLE_ENTITIES, f, p)).getContent();
                LOGGER.trace("Loaded {} Quotations", quotations.size());

                quotationLinkData = processLinkableEntities(EntityKind.QUO, quotations);
                runLinkData.add(quotationLinkData);
            }

            if (linkKinds.contains(EntityKind.PER)) {
                // Ensure that each Person is linked from the same Claims and Topics as their
                // Publications/Quotations.
                personLinkData = new LinkData();
                for (Long personId : personIds) {
                    LOGGER.trace("Processing Person #{}", personId);

                    Person person = personsById.get(personId);
                    if (person == null) {
                        LOGGER.error("  Person #{} not found", personId);
                        continue;
                    }

                    Set<Long> requiredClaimIds = requiredClaimIdsByPersonId.get(personId);
                    Set<Long> requiredTopicIds = requiredTopicIdsByPersonId.get(personId);
                    LinkData linkData = normalizeEntityLinks(person, requiredClaimIds, requiredTopicIds);
                    personLinkData.add(linkData);
                }

                runLinkData.add(personLinkData);
            }

            LOGGER.info("Linking complete:");
            log(publicationLinkData, EntityKind.PUB);
            log(quotationLinkData, EntityKind.QUO);
            log(personLinkData, EntityKind.PER);
            log(runLinkData, null);
        }
    }

    private LinkData processLinkableEntities(EntityKind entityKind, List<? extends ILinkableEntity> entities)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        LinkData entityLinkData = new LinkData();
        Set<Long> personIds = new HashSet<>();
        for (ILinkableEntity entity : entities) {
            LOGGER.trace("Processing {} #{}", entityKind.label(), entity.getId());

            // For each entity, gather its linked person IDs.
            List<EntityLink> fromEntityLinks = entity.getFromEntityLinks().getContent();
            gatherLinkedEntityIds(fromEntityLinks, entityKind, EntityKind.PER, false, personIds, true);
            this.personIds.addAll(personIds);

            LinkData linkData = normalizeEntityLinks(entity, null, null);
            entityLinkData.add(linkData);

            // Remember the IDs of Claims and Topics from which this Person should be
            // linked.
            for (Long personId : personIds) {
                addToMappedSet(requiredClaimIdsByPersonId, personId, linkData.claimIds);
                addToMappedSet(requiredTopicIdsByPersonId, personId, linkData.topicIds);
            }
        }

        LOGGER.debug("Added {} new {}-Topic links", entityLinkData.addedTopicLinkCount, entityKind.label());
        LOGGER.debug("Deleted {} existing {}-Topic links", entityLinkData.deletedTopicLinkCount, entityKind.label());

        return entityLinkData;
    }

    /**
     * Normalizes the Claim and Topic links to a given {@code ILinkableEntity}.
     * 
     * @param entity           The {@code Publication}, {@code Quotation} or
     *                         {@code Person}.
     * @param requiredClaimIds The IDs of Claims that should be linked to
     *                         {@code toEntity}. Pass {@code null} when
     *                         {@code entity} is not Person.
     * @param requiredTopicIds The IDs of Topics that should be linked to
     *                         {@code toEntity}. Pass {@code null} when
     *                         {@code entity} is not Person.
     * @throws GraphQLRequestPreparationException
     * @throws GraphQLRequestExecutionException
     */
    private LinkData normalizeEntityLinks(ILinkableEntity entity, Set<Long> requiredClaimIds,
            Set<Long> requiredTopicIds) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        // Compute existing linked Claims and Topics from entity.
        //
        // Then, if required Claims are passed in:
        // - Reconcile pre-existing Claims with required Claims, creating or deleting
        // links as necessary.
        //
        // Then, if required Topics are passed in:
        // - Reconcile pre-existing Topics with required Topics, creating or deleting
        // links as necessary.
        // Else:
        // - Compute implied linked Topics transitively from existing linked Claims.
        // - Reconcile pre-existing Topics with implied Topics, creating or deleting
        // links as necessary.
        //
        // Return pre-existing Claims and their implied/transitively linked Claim-Topics

        EntityKind entityKind = EntityKind.valueOf(entity.getEntityKind());
        Long entityId = entity.getId();
        LinkData linkData = new LinkData();

        // Get existing 'to' EntityLinks and index them by fromEntityId.
        List<EntityLink> toEntityLinks = entity.getToEntityLinks().getContent();
        Map<Long, EntityLink> toEntityLinksByFromEntityId = new HashMap<>();
        toEntityLinks
                .forEach(entityLink -> toEntityLinksByFromEntityId.put(entityLink.getFromEntity().getId(), entityLink));

        // Gather existing linked Claim IDs.
        Set<Long> existingClaimIds = new HashSet<>();
        gatherLinkedEntityIds(toEntityLinks, entityKind, EntityKind.CLA, true, existingClaimIds, false);

        // Gather existing linked Topic IDs.
        Set<Long> existingTopicIds = new HashSet<>();
        gatherLinkedEntityIds(toEntityLinks, entityKind, EntityKind.TOP, true, existingTopicIds, false);

        // If any required Claim IDs were passed in, ensure that they are linked.
        if (requiredClaimIds != null) {
            Set<Long> missingClaimIds = new HashSet<>(requiredClaimIds);
            // Remove any required Claim IDs which are already linked to the entity.
            missingClaimIds.removeAll(existingClaimIds);

            // Create any missing Claim links.
            if (!missingClaimIds.isEmpty()) {
                for (Long missingClaimId : missingClaimIds)
                    createEntityLink(EntityKind.CLA, missingClaimId, entityKind, entityId);
                existingClaimIds.addAll(missingClaimIds);

                linkData.addedClaimLinkCount = missingClaimIds.size();
                LOGGER.trace("  Added {} new {}-Claim links", linkData.addedClaimLinkCount, entityKind.label());
            }
        }

        // If requiredTopicIds is unspecified, compute it from existingClaimIds (which
        // includes the newly-added links).
        if (requiredTopicIds == null) {
            requiredTopicIds = new HashSet<>();
            for (Long claimId : existingClaimIds) {
                Claim claim = claimsById.get(claimId);
                List<EntityLink> claimToEntityLinks = claim.getToEntityLinks().getContent();
                gatherLinkedEntityIds(claimToEntityLinks, EntityKind.CLA, EntityKind.TOP, true, requiredTopicIds,
                        false);
            }
        }
        // Should we include any other Topic links or delete them as extraneous?

        // Discard any required Topics which are descendants of any other in the set.
        removeDescendants(requiredTopicIds, requiredTopicIds);

        // If any of the required Topics are ancestors or descendants of existing entity
        // Topics, relink the latter to
        // the former. To achieve this, for each existing entity-Topic link whose Topic
        // IS an ancestor or descendant
        // of one or more required Topics, hard-delete it. Then ensure that the entity
        // is linked to all of the required
        // Topics by creating a new link.
        Set<Long> lineageTopicIds = removeLineage(existingTopicIds, requiredTopicIds);
        for (Long lineageTopicId : lineageTopicIds) {
            EntityLink lineageTopicLink = toEntityLinksByFromEntityId.get(lineageTopicId);
            deleteEntityLink(lineageTopicLink);
        }
        linkData.deletedTopicLinkCount += lineageTopicIds.size();

        // Remove required Topics which are already linked to the entity.
        Set<Long> missingTopicIds = new HashSet<>(requiredTopicIds);
        missingTopicIds.removeAll(existingTopicIds);

        // Create any missing Topic links.
        for (Long missingTopicId : missingTopicIds) {
            createEntityLink(EntityKind.TOP, missingTopicId, entityKind, entityId);
            linkData.addedTopicLinkCount++;
        }

        if (entityKind != EntityKind.PER) {
            linkData.claimIds = existingClaimIds;
            linkData.topicIds = requiredTopicIds;
        }

        if (linkData.addedTopicLinkCount != 0)
            LOGGER.debug("  Added {} new {}-Topic links", linkData.addedTopicLinkCount, entityKind.label());
        if (linkData.deletedTopicLinkCount != 0)
            LOGGER.debug("  Deleted {} existing {}-Topic links", linkData.deletedTopicLinkCount, entityKind.label());

        return linkData;
    }

    /**
     * Updates a mapped set with the contents of a given collection. The method
     * ensures that {@code mappedSets} contains
     * a mapped {@code Set} for the key {@code key}, creating and adding one if not,
     * then adds the contents of
     * {@code coll} to the mapped set.
     * 
     * @param mappedSets Maps {@code key} to a {@code Set}.
     * @param key        The value to use as a key into {@code mappedSets}.
     * @param coll       The items to add to the {@code Set} mapped to {@code key}.
     */
    private <T> void addToMappedSet(Map<T, Set<T>> mappedSets, T key, Collection<T> coll) {
        Set<T> mappedSet = mappedSets.get(key);
        if (mappedSet == null)
            mappedSets.put(key, mappedSet = new HashSet<>());
        mappedSet.addAll(coll);
    }

    /**
     * Removes Topic IDs from {@code topicIds} if they are ancestors of any of
     * {@code refTopicIds}.
     * 
     * @param topicIds    The Topic IDs to check and, if necessary, remove.
     * @param refTopicIds The Topic IDs against which to check.
     * @return The Topic IDs that were removed from {@code topicIds}.
     */
    @SuppressWarnings("unused")
    private Set<Long> removeAncestors(Collection<Long> topicIds, Collection<Long> refTopicIds) {
        Set<Long> ancestorTopicIds = new HashSet<>();
        Iterator<Long> it = topicIds.iterator();
        while (it.hasNext()) {
            Long topicId = it.next();
            if (isAncestorOfAny(topicId, refTopicIds)) {
                it.remove();
                ancestorTopicIds.add(topicId);
            }
        }

        if (!ancestorTopicIds.isEmpty())
            LOGGER.trace("  Removed {} ancestors", ancestorTopicIds.size());

        return ancestorTopicIds;
    }

    /**
     * Removes Topic IDs from {@code topicIds} if they are ancestors or descendants
     * of any of {@code refTopicIds}.
     * 
     * @param topicIds    The Topic IDs to check and, if necessary, remove.
     * @param refTopicIds The Topic IDs against which to check.
     * @return The Topic IDs that were removed from {@code topicIds}.
     */
    private Set<Long> removeLineage(Collection<Long> topicIds, Collection<Long> refTopicIds) {
        Set<Long> lineageTopicIds = new HashSet<>();
        Iterator<Long> it = topicIds.iterator();
        while (it.hasNext()) {
            Long topicId = it.next();
            if (isAncestorOfAny(topicId, refTopicIds) || isDescendantOfAny(topicId, refTopicIds)) {
                it.remove();
                lineageTopicIds.add(topicId);
            }
        }

        if (!lineageTopicIds.isEmpty())
            LOGGER.trace("  Removed {} lineage Topics", lineageTopicIds.size());

        return lineageTopicIds;
    }

    /**
     * Removes Topic IDs from {@code topicIds} if they are descendants of any of
     * {@code refTopicIds}.
     * 
     * @param topicIds    The Topic IDs to check and, if necessary, remove.
     * @param refTopicIds The Topic IDs against which to check.
     * @return The Topic IDs that were removed from {@code topicIds}.
     */
    private Set<Long> removeDescendants(Collection<Long> topicIds, Collection<Long> refTopicIds) {
        Set<Long> descendantTopicIds = new HashSet<>();
        Iterator<Long> it = topicIds.iterator();
        while (it.hasNext()) {
            Long topicId = it.next();
            if (isDescendantOfAny(topicId, refTopicIds)) {
                it.remove();
                descendantTopicIds.add(topicId);
            }
        }

        if (!descendantTopicIds.isEmpty())
            LOGGER.trace("  Removed {} descendants", descendantTopicIds.size());

        return descendantTopicIds;
    }

    /**
     * Checks whether a given Topic ID is an ancestor of any given Topic IDs.
     * 
     * @param topicId     The Topic ID to check.
     * @param refTopicIds The Topic IDs against which to check.
     * @return {@code true} if {@code topicID} is an ancestor of any in
     *         {@code refTopicIds}.
     */
    private boolean isAncestorOfAny(Long topicId, Collection<Long> refTopicIds) {
        for (Long refTopicId : refTopicIds) {
            Topic refTopic = topicsById.get(refTopicId);
            if (refTopic == null) {
                LOGGER.error("Unable to find Topic #{}", refTopicId);
                return false;
            }

            Topic refParent = refTopic;
            while ((refParent = refParent.getParent()) != null) {
                if (refParent.getId().equals(topicId))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given Topic ID is a descendant of any given Topic IDs.
     * 
     * @param topicId     The Topic ID to check.
     * @param refTopicIds The Topic IDs against which to check.
     * @return {@code true} if {@code topicID} is a descendant of any in
     *         {@code refTopicIds}.
     */
    private boolean isDescendantOfAny(Long topicId, Collection<Long> refTopicIds) {
        Topic topic = topicsById.get(topicId);
        if (topic == null) {
            LOGGER.error("Unable to find Topic #{}", topicId);
            return false;
        }
        Topic parent = topic;
        while ((parent = parent.getParent()) != null) {
            if (refTopicIds.contains(parent.getId()))
                return true;
        }
        return false;
    }

    /**
     * Gathers the IDs of linked entities of a specified kind.
     * 
     * @param links            The entity links to process.
     * @param thisEntityKind   The entity kind at 'this' end.
     * @param otherEntityKind  The entity kind at the 'other' end.
     * @param gatherFromEntity Whether to gather the IDs of 'from entity' (pass
     *                         {@code true}) or 'to entity' (pass
     *                         {@code false}) end of the links.
     * @param result           The collection to update with the results.
     * @param clearResult      {@code true} to clear {@code result} before adding
     *                         the linked entity IDs.
     */
    private void gatherLinkedEntityIds(Collection<EntityLink> links, EntityKind thisEntityKind,
            EntityKind otherEntityKind, boolean gatherFromEntity, Set<Long> result, boolean clearResult) {

        if (clearResult)
            result.clear();

        for (EntityLink link : links) {
            String linkEntityKind = gatherFromEntity ? link.getFromEntity().getEntityKind()
                    : link.getToEntity().getEntityKind();
            if (linkEntityKind.equals(otherEntityKind.name()))
                result.add((gatherFromEntity ? link.getFromEntity() : link.getToEntity()).getId());
        }

        LOGGER.trace("  Found {} {}-{} links", result.size(), thisEntityKind.label(), otherEntityKind.label());
    }

    /**
     * Indexes Topics by ID and ensures that all are connected to their parent.
     * 
     * @param parent The current parent Topic of {@code topics} or {@code null} if
     *               they are top-level.
     * @param topics The Topics to index.
     * @return The total number of Topics in {topics}, including sub-Topics.
     */
    private int indexTopics(Topic parent, List<Topic> topics) {
        int count = topics.size();
        for (Topic topic : topics) {
            topic.setParent(parent);
            topicsById.put(topic.getId(), topic);
            count += indexTopics(topic, topic.getChildren());
        }
        return count;
    }

    /**
     * Creates an entity link.
     * 
     * @param fromEntityKind The 'from Entity' kind.
     * @param fromEntityId   The 'from Entity' ID.
     * @param toEntityKind   The 'to Entity' kind.
     * @param toEntityId     The 'to Entity' ID.
     * @return The new entity link or {@code null} if a dry run.
     * @throws GraphQLRequestPreparationException
     * @throws GraphQLRequestExecutionException
     */
    private EntityLink createEntityLink(EntityKind fromEntityKind, Long fromEntityId, EntityKind toEntityKind,
            Long toEntityId) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        EntityLink entityLink;
        Long entityLinkId = 0L;
        if (dryRun) {
            entityLink = null;
            entityLinkId = 0L;
        } else {
            EntityLinkInput input = EntityLinkInput.builder() //
                    .withFromEntityId(fromEntityId) //
                    .withToEntityId(toEntityId) //
                    .build();
            entityLink = mutationExecutor.createEntityLink(RESP_ENTITY_LINK, input);
            entityLinkId = entityLink.getId();
        }

        LOGGER.trace("  Created EntityLink #{} from {} #{} to {} #{}", entityLinkId, fromEntityKind.label(),
                fromEntityId, toEntityKind.label(), toEntityId);

        return entityLink;
    }

    private EntityLink deleteEntityLink(EntityLink entityLink)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

        entityLink.setStatus(StatusKind.DEL.name());
        if (!dryRun)
            mutationExecutor.deleteEntityLink(RESP_ENTITY_LINK, entityLink.getId(), Boolean.TRUE);

        if (LOGGER.isTraceEnabled()) {
            ILinkableEntity fromEntity = entityLink.getFromEntity();
            ILinkableEntity toEntity = entityLink.getToEntity();
            String fromEntityKind = fromEntity != null ? EntityKind.valueOf(fromEntity.getEntityKind()).label() : null;
            String toEntityKind = toEntity != null ? EntityKind.valueOf(toEntity.getEntityKind()).label() : null;
            Long fromEntityId = fromEntity != null ? fromEntity.getId() : null;
            Long toEntityId = toEntity != null ? toEntity.getId() : null;
            LOGGER.trace("  Deleted EntityLink #{} from {} #{} to {} #{}", entityLink.getId(),
                    fromEntityKind, fromEntityId,
                    toEntityKind, toEntityId);
        }

        return entityLink;
    }

    private void log(LinkData linkData, EntityKind entityKind) {
        if (linkData != null && LOGGER.isDebugEnabled()) {
            String label = entityKind != null ? entityKind.label() : "Entity";
            // if (linkData.addedClaimLinkCount != 0) {
            LOGGER.debug("Added {} new {}-{} links", linkData.addedClaimLinkCount, label,
                    EntityKind.CLA.label());
            // }
            // if (linkData.addedTopicLinkCount != 0) {
            LOGGER.debug("Added {} new {}-{} links", linkData.addedTopicLinkCount, label,
                    EntityKind.TOP.label());
            // }
            // if (linkData.deletedTopicLinkCount != 0) {
            LOGGER.debug("Deleted {} existing {}-{} links", linkData.deletedTopicLinkCount, label,
                    EntityKind.TOP.label());
            // }
        }
    }

}
