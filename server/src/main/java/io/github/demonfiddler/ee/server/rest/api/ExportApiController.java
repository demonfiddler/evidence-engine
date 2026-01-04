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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextRenderer;
import org.openpdf.text.Anchor;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import freemarker.template.TemplateException;
import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.model.Comment;
import io.github.demonfiddler.ee.server.model.CommentQueryFilter;
import io.github.demonfiddler.ee.server.model.DirectionKind;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.LinkableEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.LogQueryFilter;
import io.github.demonfiddler.ee.server.model.OrderInput;
import io.github.demonfiddler.ee.server.model.OrderInput.Builder;
import io.github.demonfiddler.ee.server.model.PageableInput;
import io.github.demonfiddler.ee.server.model.SortInput;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.TopicQueryFilter;
import io.github.demonfiddler.ee.server.model.TrackedEntityQueryFilter;
import io.github.demonfiddler.ee.server.model.TransactionKind;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.repository.ClaimRepository;
import io.github.demonfiddler.ee.server.repository.CommentRepository;
import io.github.demonfiddler.ee.server.repository.DeclarationRepository;
import io.github.demonfiddler.ee.server.repository.GroupRepository;
import io.github.demonfiddler.ee.server.repository.JournalRepository;
import io.github.demonfiddler.ee.server.repository.LogRepository;
import io.github.demonfiddler.ee.server.repository.PersonRepository;
import io.github.demonfiddler.ee.server.repository.PublicationRepository;
import io.github.demonfiddler.ee.server.repository.PublisherRepository;
import io.github.demonfiddler.ee.server.repository.QuotationRepository;
import io.github.demonfiddler.ee.server.repository.TopicRepository;
import io.github.demonfiddler.ee.server.repository.TrackedEntityRepository;
import io.github.demonfiddler.ee.server.repository.UserRepository;
import io.github.demonfiddler.ee.server.rest.tables.ClaimColumns;
import io.github.demonfiddler.ee.server.rest.tables.Column;
import io.github.demonfiddler.ee.server.rest.tables.CommentColumns;
import io.github.demonfiddler.ee.server.rest.tables.DeclarationColumns;
import io.github.demonfiddler.ee.server.rest.tables.GroupColumns;
import io.github.demonfiddler.ee.server.rest.tables.JournalColumns;
import io.github.demonfiddler.ee.server.rest.tables.LogColumns;
import io.github.demonfiddler.ee.server.rest.tables.PersonColumns;
import io.github.demonfiddler.ee.server.rest.tables.PublicationColumns;
import io.github.demonfiddler.ee.server.rest.tables.PublisherColumns;
import io.github.demonfiddler.ee.server.rest.tables.QuotationColumns;
import io.github.demonfiddler.ee.server.rest.tables.TopicColumns;
import io.github.demonfiddler.ee.server.rest.tables.UserColumns;
import io.github.demonfiddler.ee.server.rest.util.NewlineNormalizingOutputStream;
import io.github.demonfiddler.ee.server.rest.util.RenderUtils;
import io.github.demonfiddler.ee.server.rest.util.TemplateUtils;
import io.github.demonfiddler.ee.server.util.EntityUtils;
import io.github.demonfiddler.ee.server.util.SecurityUtils;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-10-31T13:22:31.925863500Z[Europe/London]", comments = "Generator version: 7.17.0")
@Controller
@RequestMapping("${openapi.evidenceEngineRESTInterfaceOpenAPI311.base-path:/rest}")
public class ExportApiController implements ExportApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportApiController.class);
    private static final Pattern COLUMN = Pattern.compile("^([a-zA-Z]+)(?:\s+([aA][sS][cC]|[dD][eE][sS][cC]))?$");

    @Autowired
    private NativeWebRequest request;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private TemplateUtils templateUtils;
    @Autowired
    private EntityUtils entityUtils;
    @Autowired
    private RenderUtils utils;
    @Autowired
    private ClaimRepository claimRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private DeclarationRepository declarationRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private QuotationRepository quotationRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrackedEntityRepository trackedEntityRepository;
    @Autowired
    private RenderUtils renderUtils;
    @Value("${web.server.url}")
    private String webServerUrl;
    @Value("${data.server.url}")
    private String dataServerUrl;

    public ExportApiController() {
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Resource> export(@NotNull String recordKind, @Valid Long recordId, @Valid String contentType,
        @Valid String text, @Valid Boolean advancedSearch, @Valid String status, @Valid Long fromEntityId,
        @Valid String fromEntityKind, @Valid Long toEntityId, @Valid String toEntityKind, @Valid Long topicId,
        @Valid Boolean recursive, @Valid String targetKind, @Valid Long targetId, @Valid Long parentId,
        @Valid Long userId,
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})Z?$") @Valid String from,
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})Z?$") @Valid String to,
        @Valid String entityKind, @Valid Long entityId, @Valid String transactionKind, @Valid Integer pageNumber,
        @Valid Integer pageSize,
        @Valid List<@jakarta.validation.constraints.Pattern(
            regexp = "^[a-zA-Z]+(?:\\s+(?:[aA][sS][cC]|[dD][eE][sS][cC]))?$") String> sort,
        @Valid List<@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z]+$") String> col, @Valid String paper,
        @Valid String orientation, @Min(8) @Max(16) @Valid Integer fontSize, @Valid Boolean renderTable,
        @Valid Boolean renderDetails) {

        // First prepare the query filter.
        Object filter = switch (recordKind) {
            case "claims", "declarations", "persons", "publications", "quotations" -> newLinkableEntityQueryFilter(
                recordId, text, advancedSearch, status, fromEntityId, fromEntityKind, toEntityId, toEntityKind, topicId,
                recursive);
            case "journals", "publishers", "groups", "users" -> newTrackedEntityQueryFilter(recordId, text,
                advancedSearch, status);
            case "comments" -> newCommentQueryFilter(recordId, text, advancedSearch, status, targetKind, targetId,
                parentId, userId, from, to);
            case "log" -> newLogQueryFilter(entityKind, entityId, userId, transactionKind, from, to);
            case "topics" -> newTopicQueryFilter(recordId, text, advancedSearch, status, parentId, recursive);
            default -> throw new IllegalArgumentException("Unsupported recordKind: " + recordKind);
        };

        // Enforce database primary key ordering.
        List<OrderInput> orderInputs;
        if (sort == null || sort.isEmpty()) {
            orderInputs = List.of(OrderInput.builder().withProperty("id").build());
        } else {
            if (!sort.stream().anyMatch(s -> s.equals("id")))
                sort.add("id");
            orderInputs = sort.stream().map(s -> {
                Matcher matcher = COLUMN.matcher(s);
                if (matcher.matches()) {
                    String property = matcher.group(1);
                    String direction = matcher.group(2);
                    Builder builder = OrderInput.builder().withProperty(property);
                    if ("desc".equalsIgnoreCase(direction))
                        builder.withDirection(DirectionKind.DESC);
                    return builder.build();
                } else {
                    throw new IllegalStateException("Match failed for sort=" + s);
                }
            }).toList();
        }
        SortInput sortInput = SortInput.builder() //
            .withOrders(orderInputs) //
            .build();
        PageableInput pageableInput = PageableInput.builder() //
            .withPageNumber(pageNumber) //
            .withPageSize(pageSize) //
            .withSort(sortInput) //
            .build();
        Pageable pageable = entityUtils.toPageable(pageableInput);

        // Retrieve the list of matching records.
        Page<?> page = switch (recordKind) {
            case "claims" -> claimRepository.findByFilter((LinkableEntityQueryFilter)filter, pageable);
            case "comments" -> commentRepository.findByFilter((CommentQueryFilter)filter, pageable);
            case "declarations" -> declarationRepository.findByFilter((LinkableEntityQueryFilter)filter, pageable);
            case "groups" -> groupRepository.findByFilter((TrackedEntityQueryFilter)filter, pageable);
            case "journals" -> journalRepository.findByFilter((TrackedEntityQueryFilter)filter, pageable);
            case "log" -> logRepository.findByFilter((LogQueryFilter)filter, pageable);
            case "persons" -> personRepository.findByFilter((LinkableEntityQueryFilter)filter, pageable);
            case "publications" -> publicationRepository.findByFilter((LinkableEntityQueryFilter)filter, pageable);
            case "publishers" -> publisherRepository.findByFilter((TrackedEntityQueryFilter)filter, pageable);
            case "quotations" -> quotationRepository.findByFilter((LinkableEntityQueryFilter)filter, pageable);
            case "topics" -> topicRepository.findByFilter((TopicQueryFilter)filter, pageable);
            case "users" -> userRepository.findByFilter((TrackedEntityQueryFilter)filter, pageable);
            default -> throw new IllegalArgumentException("Unsupported recordKind: " + recordKind);
        };

        LOGGER.debug(
            "export %s to %s: recordId=%d, text=%s, advancedSearch=%s, status=%s, fromEntityId=%d, fromEntityKind=%s, toEntityId=%d, toEntityKind=%s, topicId=%d, recursive=%s, targetKind=%s, targetId=%d, parentId=%d, userId=%d, from=%s, to=%s, entityKind=%s, entityId=%d, transactionKind=%s, pageNumber=%d, pageSize=%d, sort=%s: found %d records",
            recordKind, contentType, recordId, text, advancedSearch, status, fromEntityId, fromEntityKind, toEntityId,
            toEntityKind, topicId, recursive, targetKind, targetId, parentId, userId, from, to, entityKind, entityId,
            transactionKind, pageNumber, pageSize, sort, page.getNumberOfElements());

        // Map the user-specified column names into column objects.
        List<Column<? extends IBaseEntity>> allColumns = allColumnsFor(recordKind);
        List<Column<? extends IBaseEntity>> tableColumns;
        if (col != null && !col.isEmpty()) {
            tableColumns = new ArrayList<>();
            for (String c : col) {
                for (Column<? extends IBaseEntity> ac : allColumns) {
                    if (ac.id().equals(c)) {
                        tableColumns.add(ac);
                        break;
                    }
                }
            }
        } else {
            tableColumns = defaultColumnsFor(recordKind);
        }

        // For HTML/PDF output, calculate details float layout.
        int singleFloatWidth =
            renderDetails && ("text/html".equals(contentType) || "application/pdf".equals(contentType))
                ? renderUtils.calculateSingleFloatWidth(paper, orientation, fontSize) : 0;

        // Reconstruct the data URL.
        StringBuilder dataUrl = new StringBuilder();
        char sep = '?';
        dataUrl.append(dataServerUrl).append("rest/export/").append(recordKind);
        sep = appendQueryParam(dataUrl, sep, "contentType", contentType);
        if (recordId != null) {
            sep = appendQueryParam(dataUrl, sep, "recordId", recordId);
        } else {
            sep = appendQueryParam(dataUrl, sep, "text", text);
            sep = appendQueryParam(dataUrl, sep, "advancedSearch", advancedSearch);
            sep = appendQueryParam(dataUrl, sep, "status", status);
            sep = appendQueryParam(dataUrl, sep, "fromEntityId", fromEntityId);
            sep = appendQueryParam(dataUrl, sep, "fromEntityKind", fromEntityKind);
            sep = appendQueryParam(dataUrl, sep, "toEntityId", toEntityId);
            sep = appendQueryParam(dataUrl, sep, "toEntityKind", toEntityKind);
            sep = appendQueryParam(dataUrl, sep, "topicId", topicId);
            sep = appendQueryParam(dataUrl, sep, "recursive", recursive);
            sep = appendQueryParam(dataUrl, sep, "targetKind", targetKind);
            sep = appendQueryParam(dataUrl, sep, "targetId", targetId);
            sep = appendQueryParam(dataUrl, sep, "parentId", parentId);
            sep = appendQueryParam(dataUrl, sep, "userId", userId);
            sep = appendQueryParam(dataUrl, sep, "from", from);
            sep = appendQueryParam(dataUrl, sep, "to", to);
            sep = appendQueryParam(dataUrl, sep, "entityKind", entityKind);
            sep = appendQueryParam(dataUrl, sep, "entityId", entityId);
            sep = appendQueryParam(dataUrl, sep, "transactionKind", transactionKind);
        }
        sep = appendQueryParam(dataUrl, sep, "pageNumber", pageNumber);
        sep = appendQueryParam(dataUrl, sep, "pageSize", pageSize);
        sep = appendQueryParam(dataUrl, sep, "paper", paper);
        sep = appendQueryParam(dataUrl, sep, "orientation", orientation);
        sep = appendQueryParam(dataUrl, sep, "fontSize", fontSize);
        sep = appendQueryParam(dataUrl, sep, "renderTable", renderTable);
        sep = appendQueryParam(dataUrl, sep, "renderDetails", renderDetails);
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort)
                sep = appendQueryParam(dataUrl, sep, "sort", s);
        }
        if (col != null && !col.isEmpty()) {
            for (String c : col)
                sep = appendQueryParam(dataUrl, sep, "col", c);
        }

        // Reconstruct the web URL.
        StringBuilder webUrl = new StringBuilder();
        sep = '?';
        webUrl.append(webServerUrl);
        switch (recordKind) {
            case "journals":
            case "publishers":
            case "topics":
            case "groups":
            case "users":
                webUrl.append("admin/");
        }
        String lastSeg = recordKind.equals("groups") || recordKind.equals("users") ? "security" : recordKind;
        webUrl.append(lastSeg);
        if (recordId != null) {
            sep = appendQueryParam(webUrl, sep, "recordId", recordId);
        } else {
            sep = appendQueryParam(webUrl, sep, "text", text);
            sep = appendQueryParam(webUrl, sep, "advancedSearch", advancedSearch);
            sep = appendQueryParam(webUrl, sep, "status", status);
            sep = appendQueryParam(webUrl, sep, "fromEntityId", fromEntityId);
            sep = appendQueryParam(webUrl, sep, "fromEntityKind", fromEntityKind);
            sep = appendQueryParam(webUrl, sep, "toEntityId", toEntityId);
            sep = appendQueryParam(webUrl, sep, "toEntityKind", toEntityKind);
            sep = appendQueryParam(webUrl, sep, "topicId", topicId);
            sep = appendQueryParam(webUrl, sep, "recursive", recursive);
            sep = appendQueryParam(webUrl, sep, "targetKind", targetKind);
            sep = appendQueryParam(webUrl, sep, "targetId", targetId);
            sep = appendQueryParam(webUrl, sep, "parentId", parentId);
            sep = appendQueryParam(webUrl, sep, "userId", userId);
            sep = appendQueryParam(webUrl, sep, "from", from);
            sep = appendQueryParam(webUrl, sep, "to", to);
            sep = appendQueryParam(webUrl, sep, "entityKind", entityKind);
            sep = appendQueryParam(webUrl, sep, "entityId", entityId);
            sep = appendQueryParam(webUrl, sep, "transactionKind", transactionKind);
        }

        // Set up filtering/sorting labels for template.
        String recordLabel;
        if (recordId != null) {
            ITrackedEntity record = trackedEntityRepository.findById(recordId).orElseThrow();
            recordLabel = renderUtils.renderEntityLabel(record);
        } else {
            recordLabel = null;
        }
        String statusLabel = status != null ? StatusKind.valueOf(status).label() : null;
        String masterTopicLabel;
        if (topicId != null) {
            Topic topicObj = topicRepository.findById(topicId).get();
            masterTopicLabel = "Topic #" + topicId + ": " + topicObj.getLabel();
        } else {
            masterTopicLabel = null;
        }
        String masterRecordLabel;
        if (fromEntityId != null || toEntityId != null) {
            Long masterRecordId = fromEntityId != null ? fromEntityId : toEntityId;
            ITrackedEntity record = trackedEntityRepository.findById(masterRecordId).get();
            masterRecordLabel = entityUtils.getEntityLabel(record);
        } else {
            masterRecordLabel = null;
        }
        String masterRecordKind;
        if (fromEntityKind != null || toEntityKind != null) {
            masterRecordKind = EntityKind.valueOf(fromEntityKind != null ? fromEntityKind : toEntityKind).label();
        } else {
            masterRecordKind = null;
        }
        String targetKindLabel;
        if (targetKind != null) {
            targetKindLabel = EntityKind.valueOf(targetKind).label();
        } else {
            targetKindLabel = null;
        }
        String targetLabel;
        if (targetId != null) {
            ITrackedEntity target = trackedEntityRepository.findById(targetId).orElseThrow();
            targetLabel = renderUtils.renderEntityLabel(target);
        } else {
            targetLabel = null;
        }
        String parentLabel;
        if (parentId != null && parentId != -1) {
            Comment parent = commentRepository.findById(parentId).orElseThrow();
            parentLabel = renderUtils.renderEntityLabel(parent);
        } else {
            parentLabel = null;
        }
        String userLabel;
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow();
            userLabel = renderUtils.renderEntityLabel(user);
        } else {
            userLabel = null;
        }
        String entityKindLabel;
        if (entityKind != null) {
            entityKindLabel = EntityKind.valueOf(entityKind).label();
        } else {
            entityKindLabel = null;
        }
        String entityLabel;
        if (entityId != null) {
            ITrackedEntity entity = trackedEntityRepository.findById(entityId).orElseThrow();
            entityLabel = renderUtils.renderEntityLabel(entity);
        } else {
            entityLabel = null;
        }
        String transactionKindLabel;
        if (transactionKind != null) {
            transactionKindLabel = TransactionKind.valueOf(transactionKind).label();
        } else {
            transactionKindLabel = null;
        }

        // Set up the root object to pass to the FreeMarker template.
        String timestamp = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now());
        Map<String, Object> root = new HashMap<>();
        root.put("debug", true);
        root.put("recordKind", recordKind);
        root.put("contentType", contentType);
        root.put("user", getUserName());
        root.put("timestamp", timestamp);
        root.put("dataUrl", dataUrl.toString());
        root.put("webUrl", webUrl.toString());
        root.put("filter", filter);
        root.put("pageSort", pageableInput);
        root.put("page", page);
        root.put("recordLabel", recordLabel);
        root.put("statusLabel", statusLabel);
        root.put("masterTopicLabel", masterTopicLabel);
        root.put("masterRecordLabel", masterRecordLabel);
        root.put("masterRecordKind", masterRecordKind);
        root.put("targetKindLabel", targetKindLabel);
        root.put("targetLabel", targetLabel);
        root.put("parentLabel", parentLabel);
        root.put("userLabel", userLabel);
        root.put("entityKindLabel", entityKindLabel);
        root.put("entityLabel", entityLabel);
        root.put("transactionKindLabel", transactionKindLabel);
        root.put("from", from);
        root.put("to", to);
        root.put("paper", paper);
        root.put("orientation", orientation);
        root.put("fontSize", fontSize);
        root.put("singleFloatWidth", singleFloatWidth);
        root.put("renderTable", renderTable);
        root.put("renderDetails", renderDetails);
        root.put("allColumns", allColumns);
        root.put("columns", tableColumns);
        root.put("utils", utils);

        // Set up output streams to pass to FreeMarker.
        String fileExt;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStream fout = new NewlineNormalizingOutputStream(bout);

        // Use the template appropriate to the requested content type to render the output.
        if ("text/csv".equals(contentType)) {
            if (renderTable && renderDetails)
                throw new IllegalArgumentException(
                    "The renderTable and renderDetail options are mutually exclusive when exporting to CSV");

            contentType = "text/csv; charset=utf-8; header=present";
            fileExt = "csv";
            try (Writer writer = new OutputStreamWriter(fout, UTF_8)) {
                templateUtils.render("export-csv", root, writer);
                LOGGER.debug("export %s to %s", recordKind, contentType);
            } catch (TemplateException | IOException e) {
                throw new RuntimeException("Template error", e);
            }
        } else if ("application/x-research-info-systems".equals(contentType)) {
            if (renderTable)
                throw new IllegalArgumentException("RIS export does not support the renderTable option");

            fileExt = "ris";
            try (Writer writer = new OutputStreamWriter(fout, UTF_8)) {
                templateUtils.render("export-ris", root, writer);
                LOGGER.debug("export %s to %s", recordKind, contentType);
            } catch (TemplateException | IOException e) {
                throw new RuntimeException("Template error", e);
            }
        } else {
            // We're exporting to HTML or PDF; in either case we must first generate HTML.
            try (Writer writer = new OutputStreamWriter(fout, UTF_8)) {
                templateUtils.render("export-html", root, writer);

                LOGGER.debug("export %s to %s: generated HTML", recordKind, contentType);
            } catch (TemplateException | IOException e) {
                throw new RuntimeException("Template error", e);
            }

            // To generate PDF, convert the HTML.
            if ("application/pdf".equals(contentType)) {
                fileExt = "pdf";
                ITextRenderer renderer = new ITextRenderer();
                ITextFontResolver resolver = renderer.getFontResolver();
                try {
                    resolver.addFont("/fonts/DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    // resolver.addFont("/fonts/LiberationSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to load font", e);
                }
                renderer.setPdfPageEvent(new PdfPageEventHelper() {

                    @Override
                    public void onEndPage(PdfWriter writer, Document document) {
                        try {
                            LOGGER.debug("onEndPage page " + writer.getPageNumber());
                            Rectangle pageSize = document.getPageSize();
                            float leftX = pageSize.getLeft() + 35;
                            float centerX = (pageSize.getLeft() + pageSize.getRight()) / 2;
                            float rightX = pageSize.getRight() - 35;
                            float footerY = pageSize.getBottom() + 25;
                            Font font = new Font(BaseFont.createFont(), fontSize);
                            PdfContentByte cb = writer.getDirectContent();

                            // Header
                            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase(StringUtils.firstToUpper(recordKind) + " Export", font), centerX,
                                pageSize.getTop() - 30, 0);

                            // Left footer
                            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(timestamp, font), leftX,
                                footerY, 0);

                            // Centre footer
                            Phrase centrePhrase = new Phrase();
                            centrePhrase.setFont(font);
                            centrePhrase.add(new Chunk("From ", font));
                            Font linkFont = new Font(font);
                            linkFont.setColor(Color.BLUE);
                            linkFont.setStyle(Font.UNDERLINE);
                            Anchor anchor = new Anchor("Evidence Engine", linkFont);
                            anchor.setReference(webServerUrl);
                            centrePhrase.add(anchor);
                            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, centrePhrase, centerX, footerY, 0);

                            // Right footer
                            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                                new Phrase("Page " + writer.getPageNumber(), font), rightX, footerY, 0);
                        } catch (Exception e) {
                            LOGGER.error("Error processing page end", e);
                        }
                    }

                });
                String html = new String(bout.toByteArray(), UTF_8);
                renderer.setDocumentFromString(html);
                // As an alternative to placing a CSS @Page rule in the HTML:
                // ((ITextOutputDevice)renderer.getOutputDevice()).setPageSize(PageSize.A4/*.rotate()*/);
                renderer.layout();
                bout = new ByteArrayOutputStream();
                renderer.createPDF(bout);
                renderer.finishPDF();

                LOGGER.debug("export %s to %s: generated PDF");
            } else {
                fileExt = "html";
            }
        }

        // Return the generated response as a file download.
        String ts = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        ts = ts.substring(0, ts.indexOf('.')).replace(":", "_");
        String contentDisposition = "attachment; filename=" + recordKind + '-' + ts + '.' + fileExt;

        InputStream in = new ByteArrayInputStream(bout.toByteArray());
        Resource resource = new InputStreamResource(in);
        return ResponseEntity.ok() //
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition) //
            .header(HttpHeaders.CONTENT_ENCODING, UTF_8.name()) //
            .contentType(MediaType.parseMediaType(contentType)) //
            .contentLength(bout.size()) //
            .body(resource);
    }

    private LinkableEntityQueryFilter newLinkableEntityQueryFilter(Long recordId, String text, Boolean advancedSearch,
        String status, Long fromEntityId, String fromEntityKind, Long toEntityId, String toEntityKind, Long topicId,
        Boolean recursive) {

        LinkableEntityQueryFilter filter;
        if (recordId != null || text != null || status != null || fromEntityId != null || fromEntityKind != null
            || toEntityId != null || toEntityKind != null || topicId != null) {

            LinkableEntityQueryFilter.Builder builder = LinkableEntityQueryFilter.builderForLinkableEntityQueryFilter();
            if (recordId != null) {
                builder.withRecordId(recordId);
            } else {
                builder //
                    .withText(text) //
                    .withAdvancedSearch(text != null ? advancedSearch : null) //
                    .withStatus(status != null ? List.of(StatusKind.valueOf(status)) : null) //
                    .withFromEntityId(fromEntityId) //
                    .withFromEntityKind(fromEntityKind != null ? EntityKind.valueOf(fromEntityKind) : null) //
                    .withToEntityId(toEntityId) //
                    .withToEntityKind(toEntityKind != null ? EntityKind.valueOf(toEntityKind) : null) //
                    .withTopicId(topicId) //
                    .withRecursive(recursive != null && recursive ? recursive : null);
            }
            filter = builder.build();
        } else {
            filter = null;
        }
        return filter;
    }

    private TrackedEntityQueryFilter newTrackedEntityQueryFilter(Long recordId, String text, Boolean advancedSearch,
        String status) {

        TrackedEntityQueryFilter filter;
        if (recordId != null || text != null || status != null) {
            TrackedEntityQueryFilter.Builder builder = TrackedEntityQueryFilter.builderForTrackedEntityQueryFilter();
            if (recordId != null) {
                builder.withRecordId(recordId);
            } else {
                builder //
                    .withText(text) //
                    .withAdvancedSearch(
                        text != null && advancedSearch != null && advancedSearch ? advancedSearch : null) //
                    .withStatus(status != null ? List.of(StatusKind.valueOf(status)) : null);
            }
            filter = builder.build();
        } else {
            filter = null;
        }
        return filter;
    }

    private CommentQueryFilter newCommentQueryFilter(Long recordId, String text, Boolean advancedSearch, String status,
        String targetKind, Long targetId, Long parentId, Long userId, String from, String to) {

        CommentQueryFilter filter;
        if (recordId != null || text != null || status != null || targetKind != null || targetId != null
            || parentId != null || userId != null || from != null || to != null) {

            CommentQueryFilter.Builder builder = CommentQueryFilter.builderForCommentQueryFilter();
            if (recordId != null) {
                builder.withRecordId(recordId);
            } else {
                builder //
                    .withText(text) //
                    .withAdvancedSearch(
                        text != null && advancedSearch != null && advancedSearch ? advancedSearch : null) //
                    .withStatus(status != null ? List.of(StatusKind.valueOf(status)) : null) //
                    .withTargetKind(targetKind != null ? EntityKind.valueOf(targetKind) : null) //
                    .withTargetId(targetId) //
                    .withParentId(parentId) //
                    .withUserId(userId) //
                    .withFrom(from != null ? OffsetDateTime.parse(from) : null) //
                    .withTo(to != null ? OffsetDateTime.parse(to) : null);
            }
            filter = builder.build();
        } else {
            filter = null;
        }
        return filter;
    }

    private LogQueryFilter newLogQueryFilter(String entityKind, Long entityId, Long userId, String transactionKind,
        String from, String to) {

        LogQueryFilter filter;
        if (entityKind != null || entityId != null || userId != null || transactionKind != null || from != null
            || to != null) {

            filter = LogQueryFilter.builder() //
                .withEntityKind(entityKind != null ? EntityKind.valueOf(entityKind) : null) //
                .withEntityId(entityId) //
                .withUserId(userId) //
                .withTransactionKinds(
                    transactionKind != null ? List.of(TransactionKind.valueOf(transactionKind)) : null) //
                .withFrom(from != null ? OffsetDateTime.parse(from) : null) //
                .withTo(to != null ? OffsetDateTime.parse(to) : null) //
                .build();
        } else {
            filter = null;
        }
        return filter;
    }

    private TopicQueryFilter newTopicQueryFilter(Long recordId, String text, Boolean advancedSearch, String status,
        Long parentId, Boolean recursive) {
        TopicQueryFilter filter;

        if (recordId != null || text != null || status != null || parentId != null || recursive != null && recursive) {
            TopicQueryFilter.Builder builder = TopicQueryFilter.builderForTopicQueryFilter();
            if (recordId != null) {
                builder.withRecordId(recordId);
            } else {
                builder //
                    .withText(text) //
                    .withAdvancedSearch(
                        text != null && advancedSearch != null && advancedSearch ? advancedSearch : null) //
                    .withStatus(status != null ? List.of(StatusKind.valueOf(status)) : null) //
                    .withParentId(parentId) //
                    .withRecursive(recursive != null && recursive ? recursive : null);
            }
            filter = builder.build();
        } else {
            filter = null;
        }
        return filter;
    }

    private String getUserName() {
        String username = securityUtils.getCurrentUsername();
        return username.equals("anonymousUser") ? "guest" : username;
    }

    private List<Column<? extends IBaseEntity>> allColumnsFor(String recordKind) {
        return switch (recordKind) {
            case "claims" -> ClaimColumns.ALL_COLUMNS;
            case "comments" -> CommentColumns.ALL_COLUMNS;
            case "declarations" -> DeclarationColumns.ALL_COLUMNS;
            case "groups" -> GroupColumns.ALL_COLUMNS;
            case "journals" -> JournalColumns.ALL_COLUMNS;
            case "log" -> LogColumns.ALL_COLUMNS;
            case "persons" -> PersonColumns.ALL_COLUMNS;
            case "publications" -> PublicationColumns.ALL_COLUMNS;
            case "publishers" -> PublisherColumns.ALL_COLUMNS;
            case "quotations" -> QuotationColumns.ALL_COLUMNS;
            case "topics" -> TopicColumns.ALL_COLUMNS;
            case "users" -> UserColumns.ALL_COLUMNS;
            default -> throw new IllegalArgumentException("Unsupported recordKind: " + recordKind);
        };
    }

    private List<Column<? extends IBaseEntity>> defaultColumnsFor(String recordKind) {
        return switch (recordKind) {
            case "claims" -> ClaimColumns.ALL_DEFAULT_COLUMNS;
            case "comments" -> CommentColumns.ALL_DEFAULT_COLUMNS;
            case "declarations" -> DeclarationColumns.ALL_DEFAULT_COLUMNS;
            case "groups" -> GroupColumns.ALL_DEFAULT_COLUMNS;
            case "journals" -> JournalColumns.ALL_DEFAULT_COLUMNS;
            case "log" -> LogColumns.ALL_DEFAULT_COLUMNS;
            case "persons" -> PersonColumns.ALL_DEFAULT_COLUMNS;
            case "publications" -> PublicationColumns.ALL_DEFAULT_COLUMNS;
            case "publishers" -> PublisherColumns.ALL_DEFAULT_COLUMNS;
            case "quotations" -> QuotationColumns.ALL_DEFAULT_COLUMNS;
            case "topics" -> TopicColumns.ALL_DEFAULT_COLUMNS;
            case "users" -> UserColumns.ALL_DEFAULT_COLUMNS;
            default -> throw new IllegalArgumentException("Unsupported recordKind: " + recordKind);
        };
    }

    private char appendQueryParam(StringBuilder buf, char sep, String name, Object value) {
        if (value != null) {
            buf.append(sep).append(name).append('=').append(value.toString().replace(" ", "+"));
            sep = '&';
        }
        return sep;
    }

}
