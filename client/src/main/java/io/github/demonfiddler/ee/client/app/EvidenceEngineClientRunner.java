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

import static io.github.demonfiddler.ee.common.util.StringUtils.parseBoolean;
import static io.github.demonfiddler.ee.common.util.StringUtils.parseCountry;
import static io.github.demonfiddler.ee.common.util.StringUtils.parseInteger;
import static io.github.demonfiddler.ee.common.util.StringUtils.parseLocalDate;
import static io.github.demonfiddler.ee.common.util.StringUtils.parseUrl;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import io.github.demonfiddler.ee.client.Declaration;
import io.github.demonfiddler.ee.client.DeclarationInput;
import io.github.demonfiddler.ee.client.DeclarationKind;
import io.github.demonfiddler.ee.client.EntityLink;
import io.github.demonfiddler.ee.client.EntityLinkInput;
import io.github.demonfiddler.ee.client.ILinkableEntity;
import io.github.demonfiddler.ee.client.Person;
import io.github.demonfiddler.ee.client.PersonInput;
import io.github.demonfiddler.ee.client.Publication;
import io.github.demonfiddler.ee.client.PublicationInput;
import io.github.demonfiddler.ee.client.PublicationKind;
import io.github.demonfiddler.ee.client.Quotation;
import io.github.demonfiddler.ee.client.QuotationInput;
import io.github.demonfiddler.ee.client.util.Authenticator;
import io.github.demonfiddler.ee.client.util.MutationExecutor;

@Component
public class EvidenceEngineClientRunner implements CommandLineRunner {

    static enum RecordKind {
        Declaration, Person, Publication, Quotation
    }

    private static final String OPT_TOPIC_ID = "topic_id";
    private static final String OPT_LOAD = "load";
    private static final Logger LOGGER = LoggerFactory.getLogger(EvidenceEngineClientRunner.class);
    private static final Map<String, DeclarationKind> DECLARATION_KINDS = Map.of(//
        "Declaration", DeclarationKind.DECL, //
        "Open Letter", DeclarationKind.OPLE, //
        "Petition", DeclarationKind.PETN //
    );
    private static final String RESPONSEDEF_ID = "{ id }";

    class HandlerFactory {

        /*<T extends ILinkableEntity>*/ CsvHandler<? extends ILinkableEntity> create(RecordKind recordKind) {
            return switch (recordKind) {
                case Declaration -> new DeclarationHandler();
                case Person -> new PersonHandler();
                case Publication -> new PublicationHandler();
                case Quotation -> new QuotationHandler();
                default -> throw new IllegalArgumentException("Unexpected recordKind: " + recordKind);
            };
        }

    }
    private final HandlerFactory factory = new HandlerFactory();

    interface CsvHandler<T extends ILinkableEntity> {

        T handle(CSVRecord rec) throws Exception;

    }

    class DeclarationHandler implements CsvHandler<Declaration> {

        @Override
        public Declaration handle(CSVRecord rec) throws Exception {
            String signatories = rec.get("SIGNATORIES");

            // ID, TYPE, TITLE, DATE, COUNTRY, URL, SIGNATORY_COUNT, SIGNATORIES
            DeclarationInput input = DeclarationInput.builder() //
                .withKind(DECLARATION_KINDS.getOrDefault(rec.get("TYPE"), DeclarationKind.DECL)) //
                .withTitle(rec.get("TITLE")) //
                .withSignatories(signatories.replace("|", "\n")) //
                .withDate(parseLocalDate(rec.get("DATE"))) //
                .withCountry(parseCountry(rec.get("COUNTRY"))) //
                .withUrl(parseUrl(rec.get("URL"))) //
                .build();
            return mutationExecutor.createDeclaration(RESPONSEDEF_ID, input);
        }

    }

    class PersonHandler implements CsvHandler<Person> {

        @Override
        public Person handle(CSVRecord rec) throws Exception {
            // ID, TITLE, FIRST_NAME, NICKNAME, PREFIX, LAST_NAME, SUFFIX, ALIAS, DESCRIPTION, QUALIFICATIONS, COUNTRY,
            // RATING, CHECKED, PUBLISHED

            // Honour database CC_rating constraint.
            Integer rating = parseInteger(rec.get("RATING"));
            if (rating != null && (rating < 1 || rating > 5))
                rating = null;

            PersonInput input = PersonInput.builder() //
                .withTitle(rec.get("TITLE")) //
                .withFirstName(rec.get("FIRST_NAME")) //
                .withNickname(rec.get("NICKNAME")) //
                .withPrefix(rec.get("PREFIX")) //
                .withLastName(rec.get("LAST_NAME")) //
                .withSuffix(rec.get("SUFFIX")) //
                .withAlias(rec.get("ALIAS")) //
                .withNotes(rec.get("DESCRIPTION")) //
                .withQualifications(rec.get("QUALIFICATIONS")) //
                .withCountry(parseCountry(rec.get("COUNTRY"))) //
                .withRating(rating) //
                .withChecked(parseBoolean(rec.get("CHECKED"))) //
                .withPublished(parseBoolean(rec.get("PUBLISHED"))) //
                .build();
            return mutationExecutor.createPerson(RESPONSEDEF_ID, input);
        }

    }

    class PublicationHandler implements CsvHandler<Publication> {

        @Override
        public Publication handle(CSVRecord rec) throws Exception {
            // This isn't going to work well, as the CSV from Climate Science Client does not contain the abstract.

            PublicationKind publicationKind = PublicationKind.valueOf(rec.get("PUBLICATION_TYPE_ID"));
            String isbn = switch (publicationKind) {
                case BOOK, EBOOK, EDBOOK, CHAP, ECHAP -> rec.get("ISSN_ISBN");
                default -> null;
            };

            // ID, TITLE, AUTHORS, JOURNAL, LOCATION, PUBLICATION_TYPE_ID, PUBLICATION_DATE, PUBLICATION_YEAR,
            // PEER_REVIEWED, DOI, ISSN_ISBN, URL, ACCESSED
            PublicationInput input = PublicationInput.builder() //
                .withTitle(rec.get("TITLE")) //
                .withAuthorNames(rec.get("AUTHORS")) //
                .withKind(publicationKind) //
                .withDate(parseLocalDate(rec.get("PUBLICATION_DATE"))) //
                .withYear(parseInteger(rec.get("PUBLICATION_YEAR"))) //
                .withPeerReviewed(parseBoolean(rec.get("PEER_REVIEWED"))) //
                .withDoi(rec.get("DOI")) //
                .withIsbn(isbn) //
                .withUrl(parseUrl(rec.get("URL"))) //
                .withAccessed(parseLocalDate(rec.get("ACCESSED"))) //
                .build();
            return mutationExecutor.createPublication(RESPONSEDEF_ID, input);
        }

    }

    class QuotationHandler implements CsvHandler<Quotation> {

        @Override
        public Quotation handle(CSVRecord rec) throws Exception {
            // ID, PERSON_ID, AUTHOR, TEXT, DATE, SOURCE, URL
            QuotationInput input = QuotationInput.builder() //
                .withQuotee(rec.get("AUTHOR")) //
                .withText(rec.get("TEXT")) //
                .withDate(parseLocalDate(rec.get("DATE"))) //
                .withSource(rec.get("SOURCE")) //
                .withUrl(parseUrl(rec.get("URL"))) //
                .build();
            return mutationExecutor.createQuotation(RESPONSEDEF_ID, input);
        }

    }

    private final ConfigurableApplicationContext context;
    private final Authenticator authenticator;
    // private final QueryExecutor queryExecutor;
    private final MutationExecutor mutationExecutor;

    public EvidenceEngineClientRunner(ConfigurableApplicationContext context, Authenticator authenticator,
        /*QueryExecutor queryExecutor, */MutationExecutor mutationExecutor) {
        this.context = context;
        this.authenticator = authenticator;
        // this.queryExecutor = queryExecutor;
        this.mutationExecutor = mutationExecutor;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!authenticator.login())
            throw new BadCredentialsException("Authentication failed");

        // Need to be able to load the EE database from files:
        // declarations.csv, persons.csv, publications.csv, quotations.csv
        Options options = new Options() //
            .addOption("l", OPT_LOAD, true, "Load data from CSV file") //
            .addOption("t", OPT_TOPIC_ID, true, "Link imported records to specified topic");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdline = parser.parse(options, args);
        if (cmdline.hasOption("load")) {
            String recordKindStr = cmdline.getOptionValue(OPT_LOAD);
            RecordKind recordKind = RecordKind.valueOf(recordKindStr);
            String topicIdStr = cmdline.getOptionValue(OPT_TOPIC_ID);
            Long topicId = topicIdStr != null ? Long.valueOf(topicIdStr) : null;

            String[] remainingArgs = cmdline.getArgs();
            if (remainingArgs.length != 1)
                throw new IllegalArgumentException("Exactly one filename must be specified");
            File file = new File(remainingArgs[0]);
            try (Reader in = new FileReader(file)) {
                CSVFormat fmt = CSVFormat.Builder.create() //
                    .setDelimiter("\t") //
                    .setSkipHeaderRecord(false) //
                    .setHeader() //
                    .setCommentMarker(null) //
                    .setEscape('"') //
                    .setQuoteMode(QuoteMode.NONE) //
                    .setTrim(true) //
                    .get();
                CsvHandler<?> handler = factory.create(recordKind);
                Iterable<CSVRecord> it = fmt.parse(in);
                it.forEach(rec -> {
                    ILinkableEntity record;
                    try {
                        record = handler.handle(rec);

                        LOGGER.debug("Imported record #{} as {}#{}", rec.getRecordNumber(), recordKind, record.getId());
                    } catch (Exception e) {
                        LOGGER.error("Error creating record #{} ({}: {})", rec.getRecordNumber(),
                            e.getClass().getSimpleName(), e.getMessage());
                        return;
                    }
                    if (topicId != null) {
                        EntityLinkInput input = EntityLinkInput.builder() //
                            .withFromEntityId(topicId) //
                            .withToEntityId(record.getId()) //
                            .build();
                        try {
                            EntityLink link = mutationExecutor.createEntityLink(RESPONSEDEF_ID, input);

                            LOGGER.debug("Linked record #{} to Topic#{} as EntityLink#{}", rec.getRecordNumber(),
                                topicId, link.getId());
                        } catch (GraphQLRequestPreparationException | GraphQLRequestExecutionException e) {
                            LOGGER.error("Error linking record#{} {}#{} to Topic #{} ({}: {})", rec.getRecordNumber(),
                                recordKind, record.getId(), topicId, e.getClass().getSimpleName(), e.getMessage());
                        }
                    }
                });
            }
        }
        SpringApplication.exit(context);
        context.close();
    }

}
