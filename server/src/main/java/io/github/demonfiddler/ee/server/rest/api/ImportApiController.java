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

package io.github.demonfiddler.ee.server.rest.api;

import static io.github.demonfiddler.ee.server.rest.model.ImportMessage.SeverityEnum.ERROR;
import static io.github.demonfiddler.ee.server.rest.model.ImportMessage.SeverityEnum.INFO;
import static io.github.demonfiddler.ee.server.rest.model.ImportMessage.SeverityEnum.WARNING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.controller.MutationController;
import io.github.demonfiddler.ee.server.model.AbstractLinkableEntity;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.EntityLink;
import io.github.demonfiddler.ee.server.model.EntityLinkInput;
import io.github.demonfiddler.ee.server.model.ILinkableEntity;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.JournalInput;
import io.github.demonfiddler.ee.server.model.Publication;
import io.github.demonfiddler.ee.server.model.PublicationInput;
import io.github.demonfiddler.ee.server.model.PublicationKind;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.model.PublisherInput;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LinkableEntityRepository;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.rest.model.ImportMessage;
import io.github.demonfiddler.ee.server.rest.model.ImportMessage.SeverityEnum;
import io.github.demonfiddler.ee.server.rest.model.ImportedRecord;
import io.github.demonfiddler.ee.server.rest.model.ImportedRecord.ResultEnum;
import io.github.demonfiddler.ee.server.rest.util.Iso4Utils;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-11-19T12:59:29.359836300Z[Europe/London]", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.evidenceEngineRESTInterfaceOpenAPI311.base-path:/rest}")
public class ImportApiController implements ImportApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportApiController.class);
    private static final Pattern PATTERN_RIS_ITEM = Pattern.compile("^(?<tag>[A-Z][A-Z0-9]{1,5})  - ?(?<value>.*)$");
    private static final Pattern PATTERN_ISSN = Pattern.compile("^[0-9]{4}-?[0-9]{3}[0-9X]$");

    private final NativeWebRequest request;
    private final MutationController mutationController;
    private final LinkableEntityRepository linkableEntityRepository;
    private final PublisherRepository publisherRepository;
    private final JournalRepository journalRepository;
    private final Iso4Utils iso4Utils;
    private final EntityUtils entityUtils;

    public ImportApiController(NativeWebRequest request, MutationController mutationController,
        LinkableEntityRepository linkableEntityRepository, PublisherRepository publisherRepository,
        JournalRepository journalRepository, Iso4Utils iso4Utils, EntityUtils entityUtils) {

        this.request = request;
        this.mutationController = mutationController;
        this.linkableEntityRepository = linkableEntityRepository;
        this.publisherRepository = publisherRepository;
        this.journalRepository = journalRepository;
        this.iso4Utils = iso4Utils;
        this.entityUtils = entityUtils;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    private List<ImportedRecord> importPublications(MultipartFile file, Long masterTopicId, Long fromMasterEntityId,
        Long toMasterEntityId) throws IOException {

        try (BufferedReader in =
            new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<ImportedRecord> results = new ArrayList<>();
            PublicationInput.Builder builder = null;
            PublicationKind kind = null;
            String title = null;
            String tag = null;
            String publisherName = null;
            String publisherLocation = null;
            Publisher publisher = null;
            String journalTitle = null;
            String journalAbbrev = null;
            String journalIssn = null;
            Journal journalByTitle = null;
            Journal journalByAbbrev = null;
            Journal journalByIssn = null;
            StringBuilder authors = new StringBuilder();
            StringBuilder notes = new StringBuilder();
            StringBuilder keywords = new StringBuilder();
            ImportedRecord importedRecord = null;
            int lineNum = 0;
            String line;
            while ((line = in.readLine()) != null) {
                lineNum++;
                String value;
                Matcher matcher = PATTERN_RIS_ITEM.matcher(line);
                if (matcher.matches()) {
                    tag = matcher.group("tag");
                    value = matcher.group("value").trim();
                } else if (tag != null) {
                    // Continuation line(s) - add to current tag.
                    value = line;
                } else if (line.isBlank()) {
                    // blank line between records
                    continue;
                } else {
                    addMessage(importedRecord, lineNum, WARNING, "Invalid RIS content");
                    continue;
                }
                if (!"TY".equals(tag) && builder == null) {
                    addMessage(importedRecord, lineNum, WARNING,
                        "Skipping '" + tag + "' tag because TY tag is missing or invalid");
                    continue;
                }
                if (!"ER".equals(tag) && StringUtils.isBlank(value)) {
                    addMessage(importedRecord, lineNum, INFO, "Skipping empty '" + tag + "' tag");
                    continue;
                }
                switch (tag) {
                    case "TY":
                        importedRecord = new ImportedRecord();
                        try {
                            kind = PublicationKind.valueOf(value);
                            builder = PublicationInput.builder() //
                                .withKind(kind) //
                                .withCached(Boolean.FALSE) //
                                .withPeerReviewed(Boolean.FALSE);
                        } catch (IllegalArgumentException _) {
                            addMessage(importedRecord, lineNum, ERROR, "Skipping record with unknown TY  - " + tag);
                        }
                        break;
                    case "AB": // Abstract or synopsis.[6][14][8][15][17][18][19] Notes.[11][20] Synonym of N2.[8]
                    case "N2": // Abstract.[14][9][11][8][16][18] Synonym of AB.[8]
                        if (builder != null)
                            builder.withAbstract(value);
                        break;
                    case "AV": // Notes.[14] Availability (description of where to find it).[9][22][8][15] Location in
                               // archives.[citation needed]
                    case "PA": // Personal notes.[15]
                    case "J1": // Notes.[14] User abbreviation 1 of journal/periodical name.[9][25][8][13][18]
                    case "RN": // Research notes.[14][18][20]
                    case "N1": // Notes.[6][14][9][11][8][16][17][18][19][20]
                    case "NO": // Notes.[15]
                    case "U1": // Notes.[14][18] User definable 1.[9][22][8][15][16][17] Thesis-type hint.[27][19]
                    case "U2": // Notes.[14][18] User definable 2–5.[9][22][8][15][16][17]
                    case "U3":
                    case "U4":
                    case "U5":
                    case "U6": // User definable 6–15.[15]
                    case "U7":
                    case "U8":
                    case "U9":
                    case "U10":
                    case "U11":
                    case "U12":
                    case "U13":
                    case "U14":
                    case "U15":
                        if (builder != null) {
                            if (!notes.isEmpty())
                                notes.append("\n\n");
                            notes.append(tag).append(": ").append(value);
                        }
                        break;
                    case "A1": // Interviewee.[14] (Primary) author.[9][10][15][16][17][18] Synonym of AU.[8]
                    case "A2": // Secondary author/editor/translator, e.g. editor, performers, sponsor, series editor,
                               // reporter, institution, name of file, producer, series director, department,
                               // interviewer, issuing organization, recipient, or narrator. The tag must be repeated
                               // for each person. Synonym of ED.[6][9][10][14][8][15][16][18][19][20]
                    case "A3": // Tertiary author/editor/translator, e.g. series editor/author, illustrator, editor,
                               // higher court, producer, director, international author, publisher, or advisor. The tag
                               // must be repeated for each person.[6][9][10][14][8][15][18][19][20]
                               // Collaborators.[16][17]
                    case "A4": // Subsidiary/quaternary author/editor/translator, e.g. translator, counsel, sponsor,
                               // funding agency, performers, producer, department/division, or volume editor. The tag
                               // must be repeated for each person.[6][14][15][18][20]
                    case "A5": // Quinary author / compiler.[15]
                    case "AU": // (Primary) author/editor/translator, e.g. author, artist, created by, attribution,
                               // programmer, investigators, editor, director, interviewee, cartographer, composer,
                               // reporter, inventor, or institution. The tag must be repeated for each person. Synonym
                               // of A1.[6][10][14][8][16][17][18][19][20]
                    case "ED": // Secondary author.[10] Editor.[17][18][19] Synonym of A2.[8][16] Edition.[15]
                    case "TA": // Translated author.[14][20]
                        if (authors.indexOf(value) == -1) {
                            if (!authors.isEmpty())
                                authors.append('\n');
                            authors.append(value);
                        }
                        break;
                    case "BT": // Primary/secondary title.[8][23][16][18][19] For Whole Book (BOOK) and Unpublished Work
                               // (UNPB) references, this maps to T1 or TI; for all other types, this maps to T2.[8][23]
                               // Subtitle.[16] Book title.[17]
                    case "CT": // Caption.[14] Primary title.[23][16] Address.[18] Title of unpublished
                               // reference.[citation needed]
                    case "T1": // (Primary) title.[14][9][23][13][15][16][17][18]
                        // case "T2": //Secondary title, journal, periodical, publication title, code, title of weblog,
                        // series title, book title, image source program, conference name, dictionary title, periodical
                        // title, encyclopedia title, committee, program, title number, magazine, collection title,
                        // album title, newspaper, published source, title of show, section title, academic department,
                        // or full journal name.[6][14][9][23][8][13][15][16][18][19][20] Subtitle.[16]
                    case "T3": // Tertiary title, volume title, series title, legislative body, institution, decision,
                               // website title, location of work, supplement no., international title, paper number,
                               // international source, or department.[14][9][23][8][15][16][18][19][20]
                    case "ST": // Short title or abbreviated case name.[14][15][18][20]
                    case "TI": // (Primary) title, e.g. title of entry/grant/podcast/work, case name, or name of
                               // act.[14][23][8][13][16][17][18][19][20]
                    case "TT": // Translated title.[14][18][20]
                        if (builder != null) {
                            if (title == null) {
                                builder.withTitle(title = value);
                            } else if (tag.equals("TI") || tag.equals("T1")) {
                                builder.withTitle(title = value);
                                addMessage(importedRecord, lineNum, WARNING, "Previously set title '" + title
                                    + "', overwritten by '" + tag + "  - " + value + '\'');
                            } else {
                                addMessage(importedRecord, lineNum, INFO,
                                    "Title already set to '" + title + "', skipping '" + tag + "  - " + value + '\'');
                            }
                        }
                        break;
                    case "DI": // Digital Object Identifier (DOI).[18]
                    case "DO": // Digital Object Identifier (DOI).[6][14][15][16][18][19][20] This used to be documented
                               // as "Document Object Index".[24]
                    case "DOI": // Digital Object Identifier (DOI).[17]
                    case "L3": // DOI.[14] Related records.[9][22] Internet link.[16]
                        if (builder != null)
                            builder.withDoi(value);
                        break;
                    case "DA": // Date, e.g. date accessed, last update date, date decided, date of collection, date
                               // released, deadline, date of code edition, or date enacted.[6][14][18][20]
                        // Thus far, all the RIS files I've seen use DA for publication date.
                        try {
                            if (builder != null)
                                builder.withDate(LocalDate.parse(normalizeDate(value)));
                        } catch (DateTimeParseException _) {
                            addMessage(importedRecord, lineNum, ERROR,
                                "Unable to parse '" + tag + "  - " + value + "' as a date");
                        }
                        break;
                    case "Y2": // Access date or date enacted.[14][18][20] Secondary date.[9][11][8][16] Date of
                               // publication.[17]
                    case "RD": // Retrieved date.[15]
                        try {
                            if (builder != null)
                                builder.withAccessed(LocalDate.parse(normalizeDate(value)));
                        } catch (DateTimeParseException _) {
                            addMessage(importedRecord, lineNum, ERROR,
                                "Unable to parse '" + tag + "  - " + value + "' as a date");
                        }
                        break;
                    case "PY": // (Primary) (publication) year/date, e.g. year decided, year of conference, or year
                               // released.[6][14][11][8][16][17][18][20] Must always use 4 digits, with leading zeros
                               // if before 1000.[6] Synonym of Y1.[8]
                    case "YR": // Publication year.[15]
                    case "Y1": // "Year///Date".[14] Primary date/year.[9][11][16][17][18] Synonym of PY.[8]
                        // In the RIS files I've seen PY/Y1 can be either a date or a year.
                        if (builder != null) {
                            if (value.length() == 4) {
                                try {
                                    builder.withYear(Integer.valueOf(value));
                                } catch (NumberFormatException _) {
                                    addMessage(importedRecord, lineNum, ERROR,
                                        "Unable to parse '" + tag + "  - " + value + "' as a year");
                                }
                            } else {
                                try {
                                    LocalDate date = LocalDate.parse(normalizeDate(value));
                                    builder.withDate(date);
                                    builder.withYear(date.get(ChronoField.YEAR));
                                } catch (DateTimeParseException _) {
                                    addMessage(importedRecord, lineNum, ERROR,
                                        "Unable to parse '" + tag + "  - " + value + "' as a date");
                                }
                            }
                        }
                        break;
                    case "K1": // Keyword.[15]
                    case "KW": // Keyword/phrase. Must be at most 255 characters long. May be repeated any number of
                               // times to add multiple keywords.[6][14][9][26][8][16][17][18][19][20]
                        if (builder != null) {
                            if (!keywords.isEmpty())
                                keywords.append(", ");
                            keywords.append(value);
                        }
                        break;
                    case "JF": // Full name of journal/periodical.[9][25][8][13][15][16][17][18][20]
                    case "T2": // Secondary title, journal, periodical, publication title, code, title of weblog, series
                               // title, book title, image source program, conference name, dictionary title, periodical
                               // title, encyclopedia title, committee, program, title number, magazine, collection
                               // title, album title, newspaper, published source, title of show, section title,
                               // academic department, or full journal name.[6][14][9][23][8][13][15][16][18][19][20]
                               // Subtitle.[16]
                        if (builder != null) {
                            journalTitle = value;
                            Optional<Journal> journal = journalRepository.findByTitle(journalTitle);
                            if (journal.isPresent()) {
                                journalByTitle = journal.get();
                                addMessage(importedRecord, lineNum, INFO, "Found existing Journal#"
                                    + journalByTitle.getId() + " with title '" + journalTitle + "'");
                            } else {
                                addMessage(importedRecord, lineNum, INFO,
                                    "Could not find journal with title '" + journalTitle + "'");
                            }
                        }
                        break;
                    case "J2": // Alternate title, e.g. alternate journal, abbreviated publication, abbreviation, or
                               // alternate magazine. If possible, it should be a standard abbreviation, preferably
                               // using the Index Medicus style including periods. This field is used for the
                               // abbreviated title of a book or journal name, the latter mapped to
                               // T2.[6][14][13][18][20]. User abbreviation 2 of journal/periodical name.[9][25][8][13]
                    case "JA": // Standard abbreviation for journal/periodical name.[25][13][16][17][18]
                    case "JO": // Abbreviation (for journal/periodical name).[14][8][13][15][16][17][18][19] "Periodical
                               // name: full format. This is an alphanumeric field of up to 255 characters."[9][25]
                        if (builder != null) {
                            String abbreviation = iso4Utils.normalizeAbbreviation(value);
                            if (journalAbbrev == null) {
                                journalAbbrev = abbreviation;
                            } else if (tag.equals("JA")) {
                                journalAbbrev = abbreviation;
                                addMessage(importedRecord, lineNum, INFO, "Previous journal abbreviation '"
                                    + journalAbbrev + "', overwritten by '" + tag + "  - " + value + '\'');
                            } else {
                                addMessage(importedRecord, lineNum, INFO, "Ignoring '" + tag + "  - " + value
                                    + "', as journal abbreviation has already been set");
                            }
                            Optional<Journal> journal = journalRepository.findByAbbreviation(abbreviation);
                            if (journal.isPresent()) {
                                journalByAbbrev = journal.get();
                                addMessage(importedRecord, lineNum, INFO, "Found existing Journal#"
                                    + journalByAbbrev.getId() + " with abbreviation '" + abbreviation + '\'');
                            } else {
                                addMessage(importedRecord, lineNum, INFO,
                                    "Could not find journal with abbreviated title '" + abbreviation + '\'');
                            }
                        }
                        break;
                    case "UR": // Web/URL. Can be repeated for multiple tags, or multiple URLs can be entered in the
                               // same tag as a semicolon-separated list.[6][9][22][8][15][16][17][18][19][20]
                    case "L1": // File attachments, e.g. figure.[6][14][18][19][20] "Link to PDF. There is no practical
                               // length limit to this field. URL addresses can be entered individually, one per tag or
                               // multiple addresses can be entered on one line using a semi-colon as a separator. These
                               // links should end with a file name, and not simply a landing page. Use the UR tag for
                               // URL links."[9][22] Internet link.[16] Local file.[18]
                    case "L2": // URL.[14] "Link to Full-text. There is no practical length limit to this field. URL
                               // addresses can be entered individually, one per tag or multiple addresses can be
                               // entered on one line using a semi-colon as a separator."[9][22] Internet link.[16]
                        if (builder != null) {
                            try {
                                // TODO: handle multiple or semicolon-delimited URLs
                                builder.withUrl(new URI(value).toURL());
                            } catch (URISyntaxException _) {
                                addMessage(importedRecord, lineNum, ERROR,
                                    "Could not parse '" + tag + "  - " + value + "' as a URL");
                            }
                        }
                        break;
                    case "PMCID": // PMCID.[15] regex: ^PMC\d{7}$ url: https://pmc.ncbi.nlm.nih.gov/articles/
                        // TODO: regex PMCID validation
                        if (builder != null)
                            builder.withPmcid(value);
                        break;
                    case "PMID": // PMID.[15] regex: ^/d{1,10}$ url: https://pubmed.ncbi.nlm.nih.gov/
                        // TODO: regex PMID validation
                        if (builder != null)
                            builder.withPmid(value);
                        break;
                    case "SN": // ISSN, ISBN, or report/document/patent number.[14][9][21][8][15][16][17][18][19][20]
                        if (builder != null && kind != null) {
                            switch (kind) {
                                case BOOK:
                                case EBOOK:
                                case EDBOOK:
                                case CHAP:
                                case ECHAP:
                                    // TODO: regex ISBN validation
                                    builder.withIsbn(normalizeIsbn(value));
                                    break;
                                case JFULL:
                                case JOUR:
                                case EJOUR:
                                case MGZN:
                                case NEWS:
                                case SER:
                                    // TODO: regex ISSN
                                    // Sometimes an SN tag contains multiple ISSN values. We store only the first one.
                                    StringTokenizer tok = new StringTokenizer(value, ", ");
                                    if (tok.countTokens() > 1) {
                                        if (!notes.isEmpty())
                                            notes.append(" ");
                                        notes.append("SN: ").append(value).append(".");
                                        value = tok.nextToken();
                                    }
                                    if (PATTERN_ISSN.matcher(value).matches()) {
                                        journalIssn = normalizeIssn(value);
                                        Optional<Journal> journalOpt = journalRepository.findByIssn(journalIssn);
                                        if (journalOpt.isPresent()) {
                                            journalByIssn = journalOpt.get();
                                            addMessage(importedRecord, lineNum, INFO, "Found existing Journal#"
                                                + journalByIssn.getId() + " with ISSN '" + journalIssn + '\'');
                                        } else {
                                            addMessage(importedRecord, lineNum, INFO,
                                                "Could not find journal with ISSN '" + journalIssn + '\'');
                                        }
                                    } else if (value.length() >= 10) {
                                        builder.withIsbn(normalizeIsbn(value));
                                    }
                                    break;
                                default:
                                    addMessage(importedRecord, lineNum, INFO, "Skipping '" + tag + "  - " + value
                                        + "' as record is neither a book nor a periodical");
                                    break;
                            }
                        }
                        break;
                    case "PB": // Publisher, e.g. court, distributor, sponsoring agency, library/archive, assignee,
                               // institution, source, or university / degree
                               // grantor.[6][14][9][21][8][15][16][17][18][19][20]
                        publisherName = value;
                        Optional<Publisher> publisherOpt = publisherRepository.findByName(publisherName);
                        if (publisherOpt.isPresent()) {
                            publisher = publisherOpt.get();
                            addMessage(importedRecord, lineNum, INFO, "Found existing Publisher#" + publisher.getId()
                                + " with name '" + publisherName + '\'');
                        } else {
                            addMessage(importedRecord, lineNum, INFO,
                                "Could not find publisher with name '" + publisherName + '\'');
                        }
                        break;
                    case "PP": // Place of publication.[15]
                    case "CP": // City/place of publication.[9][16] Issue.[21][18]
                    case "CY": // Place published, e.g. city, conference location, country, or activity
                               // location.[6][14][21][8][16][18][19][20]
                        publisherLocation = value;
                        break;
                    case "M2": // Start page, notes, or number of pages.[14][20] Miscellaneous 2.[9][22][17][18]
                    case "EP": // Pages.[14] End page.[9][21][8][16][17][18][19]
                    case "IS": // Number, e.g. issue or number of volumes.[6][14][9][21][8][15][16][17][18][19][20]
                    case "SE": // Section, screens, code section, message number, pages, chapter, filed date, number of
                               // pages, original release date, version, e-pub date, duration of grant, section number,
                               // start page, international patent number, or running time.[14][18][20]
                    case "SP": // Pages, description, code pages, number of pages, first/start page, or running
                               // time.[14][9][21][8][15][16][17][18][19][20]
                    case "VL": // Volume, code volume, access year, reporter volume, image size, edition, amount
                               // requested, rule number, volume/storage container, number, patent version number, code
                               // number, or degree.[14][9][21][8][16][17][18][19][20]
                    case "VO": // Volume.[15][18] Published Standard number.[citation needed]
                    case "NV": // Number of volumes, e.g. extent of work, reporter abbreviation, catalog number, study
                               // number, document number, version, amount received, session number, frequency,
                               // manuscript number, US patent classification, communication number, series volume, or
                               // statute number. Ignored for Press Release (PRESS).[6][14][18][19][20]
                    case "SV": // Series volume.[14]
                    case "M1": // Number, publication number, text number, size, bill number, series volume, computer,
                               // issue, chapter, status, document number, start page, issue number, folio number,
                               // number of screens, application number, number of pages, public law number, or access
                               // date.[14][18][20] Miscellaneous 1.[9][22][8][17][18] A good place for type or genre
                               // information.[8]
                        // TODO: figure out how to handle Volume/Section/Page numbers
                    case "A6": // Website editor.[15]
                    case "AD": // (Author/editor/inventor) address, e.g. postal address, email address, phone number,
                               // and/or fax number.[6][14][9][21][8][15][18][19] Institution.[20]
                    case "AN": // Accession number.[6][14][15][20]
                    case "C1": // Custom 1, e.g. legal note, cast, author affiliation, section, place published, time
                               // period, term, year cited, government body, contact name, scale, format of music,
                               // column, or sender's e-mail.[6][14][18][20]
                    case "C2": // Custom 2, e.g. PMCID, credits, year published, unit of observation, date cited,
                               // congress number, contact address, area, form of composition, issue, issue date,
                               // recipients e-mail, or report number.[6][14][18][20]
                    case "C3": // Custom 3, e.g. size/length, title prefix, proceedings title, data type, PMCID,
                               // congress session, contact phone, size, music parts, or designated
                               // states.[6][14][18][20]
                    case "C4": // Custom 4, e.g. reviewer, dataset(s), genre, contact fax, target audience, or
                               // attorney/agent.[6][14][18][20]
                    case "C5": // Custom 5, e.g. format, packaging method, issue title, last update date, funding
                               // number, accompanying matter, format/length, references, or publisher.[6][14][18][20]
                    case "C6": // Custom 6, e.g. NIHMSID, CFDA number, legal status, issue, or volume.[6][14][18][20]
                    case "C7": // Custom 7, e.g. article number or PMCID.[6][14][18][20]
                    case "C8": // Custom 8, not used by any of the standard types.[6][14][18][20]
                        // TODO: handle custom mappings to journal registry IDs.
                    case "CA": // Caption.[6][14][18][19][20]
                    case "CL": // Classification.[15]
                    case "CN": // Call number.[6][14][15][16][18][19][20]
                    case "CR": // Cited references.[15]
                    case "DB": // Name of database.[6][14][15][18][20]
                    case "DP": // Database provider.[6][14][18][20]
                    case "DS": // Data source.[15]
                    case "ET": // Edition, e.g. epub (electronic publication?) date, date published, session, action of
                               // higher court, version, requirement, description of material, international patent
                               // classification, or description.[6][14][16][18][19][20]
                    case "FD": // Free-form publication data.[15]
                    case "H1": // Location (library).[16]
                    case "H2": // Location (call number).[16]
                    case "ID": // Reference identifier, may be limited to 20 alphanumeric
                               // characters.[9][23][8][15][18][19]
                    case "IP": // Identifying phrase.[15]
                    case "L4": // Figure, e.g. URL or file attachments.[6][14][18][19][20] Images.[9][22] Internet
                               // link.[16] Local file.[18]
                    case "LA": // Language.[6][14][15][18][19][20]
                    case "LB": // Label.[6][14][18][20]
                    case "LK": // Links.[15]
                    case "LL": // Sponsoring library location.[15]
                    case "M3": // Type of work, e.g. type (of work/article/medium/image); citation of reversal; medium;
                               // funding, patent, or thesis type; format; or form of item.[6][14][18][20] Miscellaneous
                               // 3.[9][22][8][17][18] Suitable to hold the medium.[8]
                    case "OL": // Output language (using one of the documented numeric codes).[15]
                    case "OP": // Original publication, e.g. contents, history, content, version history, original grant
                               // number, or priority numbers.[6][14][18] Other pages.[15][20] Original foreign
                               // title.[15]
                    case "RI": // Reviewed item, geographic coverage, or article number.[14][18][20]
                    case "RP": // Reprint status, e.g. reprint edition, review date, or notes. Has three possible
                               // values: "IN FILE", "NOT IN FILE", or "ON REQUEST". "ON REQUEST" must be followed by an
                               // MM/DD/YY date in parentheses.[6][14][9][26][8][17][18][20]
                    case "RT": // Reference type.[15]
                    case "SF": // Subfile/database.[15]
                    case "SL": // Sponsoring library.[15]
                    case "SR": // Source type: Print(0) or Electronic(1).[15]
                    case "WP": // Date of electronic publication.[15]
                    case "WT": // Website title.[15]
                    case "WV": // Website version.[15]
                        addMessage(importedRecord, lineNum, INFO, "Skipping unsupported tag: " + tag);
                        break;
                    case "ER":
                        if (builder != null && importedRecord != null) {
                            if (kind != null && title != null) {
                                builder.withAuthorNames(normalizeAuthors(authors.toString()));
                                if (!notes.isEmpty())
                                    builder.withNotes(notes.toString());
                                if (!keywords.isEmpty())
                                    builder.withKeywords(normalizeKeywords(keywords.toString()));

                                if (publisher == null && publisherName != null) {
                                    PublisherInput input = PublisherInput.builder() //
                                        .withName(publisherName) //
                                        .withLocation(publisherLocation) //
                                        // .withNotes("Auto-created while importing Publication '" + title
                                        // + "'. Please complete manually.") //
                                        .build();
                                    try {
                                        publisher = (Publisher)mutationController.createPublisher(null, input);
                                        String msg = "Created new Publisher#" + publisher.getId() + " with name '"
                                            + publisherName + "'";
                                        LOGGER.trace(msg);
                                        addMessage(importedRecord, lineNum, INFO, msg);
                                    } catch (Exception e) {
                                        String msg = "Failed to create Publisher '" + publisherName + "': "
                                            + normalizeExceptionMessage(e.getMessage());
                                        LOGGER.error(msg);
                                        addMessage(importedRecord, lineNum, ERROR, msg);
                                    }
                                }

                                // Check that the three possible journal references all point to the same one.
                                compareJournals(journalByIssn, journalByTitle, "issn", journalIssn, "title",
                                    journalTitle, importedRecord, lineNum);
                                compareJournals(journalByIssn, journalByAbbrev, "issn", journalIssn, "abbreviation",
                                    journalAbbrev, importedRecord, lineNum);
                                compareJournals(journalByTitle, journalByAbbrev, "title", journalTitle, "abbreviation",
                                    journalAbbrev, importedRecord, lineNum);

                                // Decide which existing journal to use, or create a new one.
                                Journal journal = null;
                                boolean journalExists = false;
                                if (journalByIssn != null) {
                                    journal = journalByIssn;
                                    journalExists = true;
                                } else if (journalByTitle != null) {
                                    journal = journalByTitle;
                                    journalExists = true;
                                } else if (journalByAbbrev != null) {
                                    journal = journalByAbbrev;
                                    journalExists = true;
                                } else if (journalTitle != null) {
                                    if (journalAbbrev == null)
                                        journalAbbrev = iso4Utils.abbreviate(journalTitle);
                                    JournalInput input = JournalInput.builder() //
                                        .withTitle(journalTitle) //
                                        .withAbbreviation(journalAbbrev) //
                                        .withIssn(journalIssn) //
                                        .withPublisherId(publisher == null ? null : publisher.getId()) //
                                        .withNotes("Auto-created while importing Publication '" + title
                                            + "'. Please complete manually.") //
                                        .build();
                                    try {
                                        journal = (Journal)mutationController.createJournal(null, input);
                                        String msg = "Created new Journal#" + journal.getId() + " with title '"
                                            + journalTitle + "'";
                                        LOGGER.trace(msg);
                                        addMessage(importedRecord, lineNum, INFO, msg);
                                        if (publisherName == null) {
                                            addMessage(importedRecord, lineNum, WARNING,
                                                "No Publisher name specified for Journal#" + journal.getId()
                                                    + " (missing PB tag?).");
                                        }
                                    } catch (Exception e) {
                                        String msg = "Failed to create Journal '" + journalTitle + "': "
                                            + normalizeExceptionMessage(e.getMessage());
                                        LOGGER.error(msg);
                                        addMessage(importedRecord, lineNum, ERROR, msg);
                                    }
                                } else {
                                    addMessage(importedRecord, lineNum, WARNING,
                                        "No Journal specified for Publication (missing JF/T2/JA/J2/JO/SN tag?)");
                                }

                                if (journal != null) {
                                    builder.withJournalId(journal.getId());
                                    builder.withPeerReviewed(journal.getPeerReviewed());
                                    if (journalExists && journal.getPublisher() == null) {
                                        addMessage(importedRecord, lineNum, WARNING,
                                            "No Publisher associated with existing Journal# " + journal.getId());
                                    }
                                }

                                PublicationInput input = builder.build();
                                try {
                                    Publication publication =
                                        (Publication)mutationController.createPublication(null, input);
                                    importedRecord.setId(publication.getId());
                                    importedRecord.setLabel(publication.getTitle());
                                    importedRecord.setResult(ResultEnum.IMPORTED);
                                    String msg = "Created new Publication#" + publication.getId() + " with title '"
                                        + publication.getTitle() + "'";
                                    LOGGER.trace(msg);
                                    addMessage(importedRecord, lineNum, INFO, msg);

                                    createLinks(importedRecord, lineNum, publication, masterTopicId, fromMasterEntityId,
                                        toMasterEntityId);
                                } catch (DuplicateKeyException e) {
                                    importedRecord.setResult(ResultEnum.DUPLICATE);
                                    addMessage(importedRecord, lineNum, ERROR,
                                        "Imported record duplicates an existing Publication: "
                                            + normalizeExceptionMessage(e.getMessage()));
                                } catch (DataIntegrityViolationException e) {
                                    importedRecord.setResult(ResultEnum.ERROR);
                                    addMessage(importedRecord, lineNum, ERROR, "Imported record violates a constraint: "
                                        + normalizeExceptionMessage(e.getMessage()));
                                } catch (Exception e) {
                                    importedRecord.setResult(ResultEnum.ERROR);
                                    addMessage(importedRecord, lineNum, ERROR,
                                        "Error persisting Publication: " + normalizeExceptionMessage(e.getMessage()));
                                }
                            } else {
                                addMessage(importedRecord, lineNum, ERROR,
                                    "Skipping incomplete record (missing/invalid TY and/or TI tags)");
                                importedRecord.setResult(ResultEnum.ERROR);
                            }
                            results.add(importedRecord);
                        } else if (importedRecord != null) {
                            addMessage(importedRecord, lineNum, ERROR, "Unmatched ER tag (missing TY tag?)");
                            importedRecord.setResult(ResultEnum.ERROR);
                        }

                        // Reset fields for next record.
                        builder = null;
                        kind = null;
                        title = null;
                        tag = null;
                        publisherName = null;
                        publisherLocation = null;
                        publisher = null;
                        journalTitle = null;
                        journalAbbrev = null;
                        journalIssn = null;
                        journalByTitle = null;
                        journalByAbbrev = null;
                        journalByIssn = null;
                        authors.setLength(0);
                        notes.setLength(0);
                        keywords.setLength(0);
                        importedRecord = null;
                        break;
                    default:
                        addMessage(importedRecord, lineNum, WARNING, "Skipping unknown tag: " + tag);
                }
            }
            ;

            return results;
        }
    }

    private void createLinks(ImportedRecord result, int lineNum, ILinkableEntity entity, Long masterTopicId,
        Long fromMasterEntityId, Long toMasterEntityId) {

        if (masterTopicId != null) {
            EntityLinkInput input = EntityLinkInput.builder() //
                .withFromEntityId(masterTopicId) //
                .withToEntityId(entity.getId()) //
                .build();
            try {
                EntityLink topicLink = (EntityLink)mutationController.createEntityLink(null, input);
                String msg = "Linked to Topic#" + masterTopicId + " by RecordLink#" + topicLink.getId();
                LOGGER.trace(msg);
                addMessage(result, lineNum, SeverityEnum.INFO, msg);
            } catch (Exception e) {
                addMessage(result, lineNum, ERROR, "Error creating Topic link: " + e.getMessage());
            }

        }

        Long masterEntityId = fromMasterEntityId != null ? fromMasterEntityId : toMasterEntityId;
        if (masterEntityId != null) {
            try {
                EntityLinkInput input = EntityLinkInput.builder() //
                    .withFromEntityId(fromMasterEntityId != null ? fromMasterEntityId : entity.getId()) //
                    .withToEntityId(toMasterEntityId != null ? toMasterEntityId : entity.getId()) //
                    .build();
                EntityLink recordLink = (EntityLink)mutationController.createEntityLink(null, input);

                addMessage(result, lineNum, SeverityEnum.INFO,
                    "Linked to record#" + masterEntityId + " by RecordLink#" + recordLink.getId());
            } catch (Exception e) {
                addMessage(result, lineNum, ERROR, "Error creating record link: " + e.getMessage());
            }

        }
    }

    private void compareJournals(Journal journal1, Journal journal2, String keyname1, String key1, String keyname2,
        String key2, ImportedRecord importedRecord, int lineNum) {

        if (journal1 != null && journal2 != null) {
            if (!journal1.getId().equals(journal2.getId())) {
                addMessage(importedRecord, lineNum, WARNING, "Journal " + keyname1 + " '" + key1 + "'' and " + keyname2
                    + " '" + key2 + "' point to different existing database records");
            }
        }
    }

    private void addMessage(ImportedRecord result, int lineNum, SeverityEnum severity, String text) {
        if (result != null)
            result.addMessagesItem(new ImportMessage().lineNum(lineNum).severity(severity).text(text));
    }

    private String normalizeDate(String value) {
        // Some RIS date values have a trailing slash, only a year, or include a time component.
        return value.substring(0, Math.min(10, value.length())).replace("///", "/01/01").replace("/", "-");
    }

    private String normalizeIsbn(String value) {
        StringTokenizer tok = new StringTokenizer(value, " ");
        if (tok.countTokens() > 1)
            value = tok.nextToken();
        return value;
    }

    private String normalizeIssn(String value) {
        // Hyphenate the ISSN if necessary.
        if (value.length() == 8 && value.indexOf('-') == -1)
            value = new StringBuilder(value).insert(4, '-').toString();
        // The canonical form of (13-) and 10-digit ISBNs is complicated because the (5)/4 elements do not have a fixed
        // width:
        // (GS-1 prefix [3]), registration group element [1-5], registrant element, publication element [1-6], check
        // digit [1].
        return value;
    }

    private String normalizeAuthors(String value) {
        return value == null ? "(unknown)" : value.length() > 2000 ? value.substring(0, 2000) : value;
    }

    private String normalizeKeywords(String value) {
        return value.length() > 255 ? value.substring(0, 255) : value;
    }

    private String normalizeExceptionMessage(String msg) {
        // Strip off extraneous technical detail.
        int idx = msg.indexOf(" [insert into");
        return idx != -1 ? msg.substring(0, idx) : msg;
    }

    @Override
    @PreAuthorize("hasAuthority('CRE')")
    public ResponseEntity<List<ImportedRecord>> callImport(@NotNull String importRecordKind, @Valid Long masterTopicId,
        @Valid Long masterRecordId, MultipartFile file) {

        LOGGER.debug("Importing RIS records");
        try {
            // If a master topic is specified, make sure it exists.
            if (masterTopicId != null && !linkableEntityRepository.existsById(masterTopicId))
                return ResponseEntity.badRequest().build();

            // If a master record is specified, make sure it exists and figure out how to link it.
            Long fromMasterEntityId = null;
            Long toMasterEntityId = null;
            if (masterRecordId != null) {
                Optional<AbstractLinkableEntity> masterRecordOpt = linkableEntityRepository.findById(masterRecordId);
                if (masterRecordOpt.isPresent()) {
                    EntityKind entityKind = switch (importRecordKind) {
                        case "publications" -> EntityKind.PUB;
                        default -> null;
                    };
                    ILinkableEntity masterRecord = masterRecordOpt.get();
                    EntityKind masterEntityKind = EntityKind.valueOf(masterRecord.getEntityKind());

                    Collection<EntityKind> toEntityKinds = entityUtils.getToEntityKinds(entityKind);
                    if (toEntityKinds.contains(masterEntityKind)) {
                        toMasterEntityId = masterRecordId;
                    } else {
                        toEntityKinds = entityUtils.getToEntityKinds(masterEntityKind);
                        if (toEntityKinds.contains(entityKind))
                            fromMasterEntityId = masterRecordId;
                    }
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }

            List<ImportedRecord> response = switch (importRecordKind) {
                case "publications" -> importPublications(file, masterTopicId, fromMasterEntityId, toMasterEntityId);
                default -> null;
            };
            return response != null //
                ? ResponseEntity.ok() //
                    .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name()) //
                    .contentType(MediaType.parseMediaType("application/json")) //
                    .body(response) //
                : ResponseEntity.badRequest().build();
        } catch (IOException _) {
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(null);
        }
    }

}
