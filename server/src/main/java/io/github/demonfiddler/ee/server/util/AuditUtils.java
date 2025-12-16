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

package io.github.demonfiddler.ee.server.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.beanutils2.PropertyUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.server.model.EntityAudit;
import io.github.demonfiddler.ee.server.model.EntityLink;
import io.github.demonfiddler.ee.server.model.FieldAudit;
import io.github.demonfiddler.ee.server.model.FieldAuditEntry;
import io.github.demonfiddler.ee.server.model.FieldGroupAuditEntry;
import io.github.demonfiddler.ee.server.model.IFieldAudit;
import io.github.demonfiddler.ee.server.model.ILinkAudit;
import io.github.demonfiddler.ee.server.model.ILinkableEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.LinkAudit;
import io.github.demonfiddler.ee.server.model.LinkAuditEntry;
import io.github.demonfiddler.ee.server.model.LinkGroupAuditEntry;
import io.github.demonfiddler.ee.server.model.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.SeverityKind;
import io.github.demonfiddler.ee.server.repository.EntityLinkRepository;
import jakarta.annotation.Resource;

@Component
public class AuditUtils {

    static class RuleException extends Exception {

        RuleException(String fieldName, Exception cause) {
            super("Error testing " + fieldName, cause);
        }

    }

    /**  Field rules only cause aggregate failure at ERROR-level severity. */
    private static Predicate<FieldAuditEntry> FIELD_PREDICATE =
        fae -> fae.getPass() || fae.getSeverity() != SeverityKind.ERROR;

    /** Abstract base class to perform field validation. */
    private static abstract class FieldRule<T> {

        final String fieldName;
        final SeverityKind severity;

        FieldRule(String fieldName, SeverityKind severity) {
            this.fieldName = fieldName;
            this.severity = severity;
        }

        final String getFieldName() {
            return fieldName;
        }

        final SeverityKind getSeverity() {
            return severity;
        }

        final String getModalVerb() {
            return switch (severity) {
                case INFO -> "could";
                case WARNING -> "should";
                case ERROR -> "must";
            };
        }

        abstract String getMessage();

        final boolean test(ITrackedEntity entity) throws RuleException {
            try {
                @SuppressWarnings("unchecked")
                T value = (T)PropertyUtils.getProperty(entity, fieldName);
                return doTest(value);
            } catch (Exception e) {
                throw new RuleException(fieldName, e);
            }
        }

        abstract boolean doTest(T value);

    }

    /** Field rule that enforces a mandatory field value. */
    @SuppressWarnings("unused")
    private static final class Required extends FieldRule<Object> {

        static FieldRule<Object> info(String fieldName) {
            return new Required(fieldName, SeverityKind.INFO);
        }

        static FieldRule<Object> warning(String fieldName) {
            return new Required(fieldName, SeverityKind.WARNING);
        }

        static FieldRule<Object> error(String fieldName) {
            return new Required(fieldName, SeverityKind.ERROR);
        }

        private Required(String fieldName, SeverityKind severity) {
            super(fieldName, severity);
        }

        @Override
        boolean doTest(Object value) {
            return value != null;
        }

        @Override
        String getMessage() {
            return String.format("%s %s have a non-null value", fieldName, getModalVerb());
        }

    }

    /** Field rule that enforces a minimum allowable string length. */
    @SuppressWarnings("unused")
    private static final class MinLength extends FieldRule<String> {

        static FieldRule<String> info(String fieldName, IntSupplier minLen) {
            return new MinLength(fieldName, SeverityKind.INFO, minLen);
        }

        static FieldRule<String> warning(String fieldName, IntSupplier minLen) {
            return new MinLength(fieldName, SeverityKind.WARNING, minLen);
        }

        static FieldRule<String> error(String fieldName, IntSupplier minLen) {
            return new MinLength(fieldName, SeverityKind.ERROR, minLen);
        }

        private final IntSupplier minLen;

        private MinLength(String fieldName, SeverityKind severity, IntSupplier minLen) {
            super(fieldName, severity);
            this.minLen = minLen;
        }

        @Override
        boolean doTest(String v) {
            return v != null && v.length() >= minLen.getAsInt();
        }

        @Override
        String getMessage() {
            return String.format("%s %s have at least %d characters", fieldName, getModalVerb(), minLen.getAsInt());
        }

    }

    /** Field rule that enforces a maximum allowable value. */
    @SuppressWarnings("unused")
    private static final class Maximum<T extends Comparable<T>> extends FieldRule<T> {

        static <T extends Comparable<T>> FieldRule<T> info(String fieldName, Supplier<T> max) {
            return new Maximum<T>(fieldName, SeverityKind.INFO, max);
        }

        static <T extends Comparable<T>> FieldRule<T> warning(String fieldName, Supplier<T> max) {
            return new Maximum<T>(fieldName, SeverityKind.WARNING, max);
        }

        static <T extends Comparable<T>> FieldRule<T> error(String fieldName, Supplier<T> max) {
            return new Maximum<T>(fieldName, SeverityKind.ERROR, max);
        }

        private final Supplier<T> max;

        private Maximum(String fieldName, SeverityKind severity, Supplier<T> max) {
            super(fieldName, severity);
            Objects.requireNonNull(max);
            this.max = max;
        }

        @Override
        boolean doTest(T value) {
            // NOTE: Maximum accepts a null value because a maximum does not necessarily imply 'requiredness', which
            // can be enforced separately.
            return value == null || value.compareTo(max.get()) <= 0;
        }

        @Override
        String getMessage() {
            return String.format("%s %s be less than or equal to %s", fieldName, getModalVerb(), max.get().toString());
        }

    }

    private static final FieldRule<Object> RATING = Required.warning("rating");

    private static final record Rule(Map<String, Integer> links, Collection<Map<String, Integer>> linkGroups,
        Collection<FieldRule<?>> fields, Collection<Collection<FieldRule<?>>> fieldGroups) {
    }

    private static final Map<String, Rule> RULES;

    static {
        Map<String, Rule> rules = new HashMap<>();
        rules.put("CLA", new Rule( //
            Map.of( //
                "TOP", 1 //
            ), //
            List.of( //
                Map.of( //
                    "DEC", 1, //
                    "PER", 1, //
                    "PUB", 1, //
                    "QUO", 1 //
                ) //
            ), //
            List.of( //
                RATING, //
                Maximum.error("date", () -> LocalDate.now())), //
            null)); // Claim
        rules.put("COM", null); // Comment
        rules.put("COU", null); // Country
        rules.put("DEC", new Rule( //
            Map.of( //
                "PER", 1, //
                "TOP", 1 //
            ), //
            null, //
            List.of( //
                RATING, //
                Required.warning("date"), //
                Maximum.error("date", () -> LocalDate.now()), //
                Required.warning("country"), //
                Required.warning("url"), //
                Required.error("signatories"), //
                Required.error("signatoryCount"), Required.info("notes")), //
            null)); // Declaration
        rules.put("GRP", null); // Group
        rules.put("JOU", new Rule( //
            null, null, //
            List.of(RATING, //
                Required.warning("abbreviation"), //
                Required.warning("url"), //
                Required.warning("issn"), //
                Required.info("notes"), //
                Required.warning("publisher"), //
                Required.error("peerReviewed") //
            ), //
            null)); // Journal
        rules.put("LNK", null); //
        rules.put("PER", new Rule( //
            Map.of( //
                "TOP", 1 //
            ), //
            List.of( //
                Map.of( //
                    "CLA", 1, //
                    "DEC", 1, //
                    "PUB", 1, //
                    "QUO", 1 //
                ) //
            ), //
            List.of( //
                RATING, //
                Required.warning("qualifications"), //
                Required.warning("country"), //
                Required.info("notes")), //
            null)); // Person
        rules.put("PUB", new Rule( //
            Map.of( //
                "CLA", 1, //
                "PER", 1, //
                "TOP", 1 //
            ), //
            null, //
            List.of( //
                RATING, //
                Required.warning("journal"), //
                Required.warning("date"), //
                Maximum.error("date", () -> LocalDate.now()), //
                Required.warning("year"), //
                Maximum.error("year", () -> LocalDate.now().getYear()), //
                Required.warning("keywords"), //
                Required.warning("abstract"), //
                Required.info("notes"), //
                Required.warning("peerReviewed"), //
                Required.info("accessed") //
            // Maximum.error("accessed", () -> LocalDate.now().getYear()) ?
            ), //
            List.of( //
                List.of( //
                    Required.info("doi"), //
                    Required.info("isbn"), //
                    Required.info("pmcid"), //
                    Required.info("pmid"), //
                    Required.info("hsid"), //
                    Required.info("arxivid"), //
                    Required.info("biorxivid"), //
                    Required.info("medrxivid"), //
                    Required.info("ericid"), //
                    Required.info("ihepid"), //
                    Required.info("oaipmhid"), //
                    Required.info("halid"), //
                    Required.info("zenodoid"), //
                    Required.info("scopuseid"), //
                    Required.info("wsan"), //
                    Required.info("pinfoan") //
                )))); // Publication
        rules.put("PBR", new Rule( //
            null, null, //
            List.of(RATING, //
                Required.warning("location"), //
                Required.warning("country"), //
                Required.warning("url"), //
                Required.warning("journalCount"), //
                Required.info("notes") //
            ), //
            null)); // Publisher
        rules.put("QUO", new Rule( //
            Map.of( //
                "PER", 1, //
                "TOP", 1 //
            ), //
            null, //
            List.of( //
                RATING, //
                Required.warning("date"), //
                Maximum.error("date", () -> LocalDate.now()), //
                Required.warning("source"), //
                Required.warning("url"), //
                Required.info("notes")), //
            null)); // Quotation
        rules.put("TOP", new Rule( //
            null, //
            List.of( //
                Map.of( //
                    "CLA", 1, //
                    "DEC", 1, //
                    "PER", 1, //
                    "PUB", 1, //
                    "QUO", 1 //
                ) //
            ), //
            List.of( //
                RATING, //
                Required.warning("description")), //
            null)); // Topic
        rules.put("USR", null); // User
        RULES = Collections.unmodifiableMap(rules);
    }

    @Resource
    protected EntityLinkRepository entityLinkRepository;

    private void auditFields(Collection<FieldRule<?>> fieldRules, boolean and, ITrackedEntity entity,
        IFieldAudit fieldAudit) {

        if (fieldRules != null) {
            for (FieldRule<?> fieldRule : fieldRules) {
                FieldAuditEntry.Builder builder = FieldAuditEntry.builder() //
                    .withFieldName(fieldRule.getFieldName()); //
                try {
                    boolean pass = fieldRule.test(entity);
                    builder //
                        .withSeverity(fieldRule.getSeverity()) //
                        .withMessage(fieldRule.getMessage()) //
                        .withPass(pass);
                } catch (RuleException e) {
                    builder //
                        .withSeverity(SeverityKind.ERROR) //
                        .withMessage(e.getCause().getClass().getSimpleName() + " during rule execution") //
                        .withPass(false);
                }
                FieldAuditEntry entry = builder.build();
                fieldAudit.getFields().add(entry);
            }
        }

        fieldAudit.setPass( //
            and //
                ? fieldAudit.getPass() && fieldAudit.getFields().stream().allMatch(FIELD_PREDICATE) //
                : fieldAudit.getPass() || fieldAudit.getFields().stream().anyMatch(FIELD_PREDICATE) //
        );
    }

    private FieldAudit auditFields(ITrackedEntity entity, Rule rule) {
        List<FieldAuditEntry> fieldAuditEntries = new ArrayList<>();
        List<FieldGroupAuditEntry> fieldGroupAuditEntries = new ArrayList<>();
        FieldAudit fieldAudit = FieldAudit.builder() //
            .withFields(fieldAuditEntries) //
            .withGroups(fieldGroupAuditEntries) //
            .withPass(true) //
            .build();

        auditFields(rule.fields, true, entity, fieldAudit);

        if (rule.fieldGroups() != null) {
            for (Collection<FieldRule<?>> fieldGroup : rule.fieldGroups()) {
                FieldGroupAuditEntry fieldGroupAuditEntry = FieldGroupAuditEntry.builder() //
                    .withFields(new ArrayList<>()) //
                    .withPass(false) //
                    .build();
                fieldGroupAuditEntries.add(fieldGroupAuditEntry);

                auditFields(fieldGroup, false, entity, fieldGroupAuditEntry);

                fieldAudit.setPass(fieldAudit.getPass() && fieldGroupAuditEntry.getPass());
            }
        }

        return fieldAudit;
    }

    private void aggregateLinkResults(ILinkAudit linkAudit, boolean and) {
        linkAudit.setPass( //
            and //
                ? linkAudit.getPass() && //
                    linkAudit.getLinks().stream().allMatch(lae -> lae.getPass()) //
                : linkAudit.getPass() || //
                    linkAudit.getLinks().stream().anyMatch(lae -> lae.getPass()) //
        );
    }

    private void auditLinks(Map<String, Integer> linkRules, List<EntityLink> entityLinks, boolean isFromEntity,
        ILinkAudit linkAudit) {

        if (linkRules == null || linkRules.isEmpty())
            return;

        // Collect the entity links by other entity kind.
        Function<? super EntityLink, ? extends String> classifier = //
            isFromEntity //
                ? el -> el.getToEntity().getEntityKind() //
                : el -> el.getFromEntity().getEntityKind();
        Map<String, List<EntityLink>> linksByOtherEntityKind =
            entityLinks.stream().collect(Collectors.groupingBy(classifier));

        // Check each rule for conformance.
        for (Entry<String, Integer> linkRuleEntry : linkRules.entrySet()) {
            String otherEntityKind = linkRuleEntry.getKey();
            List<EntityLink> otherEntityLinks = linksByOtherEntityKind.get(otherEntityKind);
            int otherEntityLinkCount = otherEntityLinks != null ? otherEntityLinks.size() : 0;
            LinkAuditEntry entry;
            Optional<LinkAuditEntry> entryOpt =
                linkAudit.getLinks().stream().filter(l -> l.getLinkedEntityKind().equals(otherEntityKind)).findFirst();
            if (entryOpt.isEmpty()) {
                entry = LinkAuditEntry.builder() //
                    .withLinkedEntityKind(otherEntityKind) //
                    .withActual(otherEntityLinkCount) //
                    .withMin(linkRuleEntry.getValue()) //
                    .build();
                linkAudit.getLinks().add(entry);
            } else {
                entry = entryOpt.get();
                entry.setActual(entry.getActual() + otherEntityLinkCount);
            }
            boolean pass = entry.getActual() >= linkRuleEntry.getValue();
            entry.setPass(pass);
        }
    }

    private LinkAudit auditLinks(ILinkableEntity entity, Rule rule) {
        LinkableEntityQueryFilter filter = new LinkableEntityQueryFilter();
        filter.setFromEntityId(entity.getId());
        List<EntityLink> fromEntityLinks = entityLinkRepository.findByFilter(filter, Pageable.unpaged()).getContent();
        filter.setFromEntityId(null);
        filter.setToEntityId(entity.getId());
        List<EntityLink> toEntityLinks = entityLinkRepository.findByFilter(filter, Pageable.unpaged()).getContent();

        List<LinkAuditEntry> linkAuditEntries = new ArrayList<>();
        List<LinkGroupAuditEntry> linkGroupAuditEntries = new ArrayList<>();
        LinkAudit linkAudit = LinkAudit.builder() //
            .withLinks(linkAuditEntries) //
            .withGroups(linkGroupAuditEntries) //
            .withPass(true) //
            .build();

        auditLinks(rule.links, fromEntityLinks, true, linkAudit);
        auditLinks(rule.links, toEntityLinks, false, linkAudit);
        aggregateLinkResults(linkAudit, true);

        if (rule.linkGroups() != null) {
            for (Map<String, Integer> requirements : rule.linkGroups()) {
                LinkGroupAuditEntry linkGroupAuditEntry = LinkGroupAuditEntry.builder() //
                    .withLinks(new ArrayList<>()) //
                    .withPass(false) //
                    .build();
                linkGroupAuditEntries.add(linkGroupAuditEntry);

                auditLinks(requirements, fromEntityLinks, true, linkGroupAuditEntry);
                auditLinks(requirements, toEntityLinks, false, linkGroupAuditEntry);
                aggregateLinkResults(linkGroupAuditEntry, false);

                linkAudit.setPass(linkAudit.getPass() && linkGroupAuditEntry.getPass());
            }
        }

        return linkAudit;
    }

    public EntityAudit audit(ITrackedEntity entity) {
        EntityAudit audit;
        String entityKind = entity.getEntityKind();
        Rule rule = RULES.get(entityKind);
        if (rule != null) {
            FieldAudit fieldAudit = rule.fields != null && !rule.fields.isEmpty() //
                ? auditFields(entity, rule) //
                : null;

            LinkAudit linkAudit = entity instanceof ILinkableEntity && rule.links != null && !rule.links.isEmpty() //
                ? auditLinks((ILinkableEntity)entity, rule) //
                : null;

            audit = EntityAudit.builder() //
                .withEntity(entity) //
                .withFieldAudit(fieldAudit) //
                .withLinkAudit(linkAudit) //
                .withPass((fieldAudit == null || fieldAudit.getPass()) && (linkAudit == null || linkAudit.getPass()))
                .build();
        } else {
            audit = new EntityAudit();
        }
        return audit;
    }

}
