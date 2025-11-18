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

package io.github.demonfiddler.ee.server.rest.api;

import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-10-31T13:22:31.925863500Z[Europe/London]", comments = "Generator version: 7.17.0")
@Validated
@Tag(name = "CSV", description = "Exported list in CSV format")
@Tag(name = "HTML", description = "Exported list in HTML format")
@Tag(name = "PDF", description = "Exported list in PDF format")
@Tag(name = "RIS", description = "Exported list in RIS format.")
public interface ExportApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    String PATH_EXPORT = "/export/{recordKind}";

    /**
     * GET /export/{recordKind} : Exports a list Exports an optionally filtered list to CSV, HTML, PDF or RIS
     * @param recordKind The list&#39;s record type. (required)
     * @param recordId The identifier of the single record to return. Applicable to all recordKind except logs.
     * (optional)
     * @param contentType Overrides the HTTP request&#39;s Accept header, since browsers ignore the type attribute in,
     * for example, &lt;a href&#x3D;\&quot;...\&quot; download&#x3D;\&quot;filename.pdf\&quot;
     * type&#x3D;\&quot;application/pdf\&quot;&gt;filename.pdf&lt;/a&gt;. In other words, there&#39;s no way for an HTML
     * link to force a browser to request the necessary content type. The
     * \&quot;application/x-research-info-systems\&quot; content type is only supported for
     * recordKind&#x3D;\&quot;publications\&quot;. (optional, default to application/pdf)
     * @param text Free text search string. Applicable to all recordKind except logs. (optional)
     * @param advancedSearch Whether to search text in advanced (boolean) mode, used in conjunction with the text
     * parameter. Applicable to all recordKind except logs. (optional)
     * @param status Return only records with this status code. Applicable to all recordKind except logs. (optional)
     * @param fromEntityId The ID of the &#39;linked-from&#39; master record. Applicable to recordKind claims,
     * declarations, persons, publications, quotations. (optional)
     * @param fromEntityKind The kind of the &#39;linked-from&#39; master record. Applicable to recordKind claims,
     * declarations, persons, publications, quotations. (optional)
     * @param toEntityId The ID of the &#39;linked-to&#39; master record. Applicable to recordKind claims, declarations,
     * persons, publications, quotations. (optional)
     * @param toEntityKind The kind of the &#39;linked-to&#39; master record. Applicable to recordKind claims,
     * declarations, persons, publications, quotations. (optional)
     * @param topicId The master topic identifier. Applicable to recordKind claims, declarations, persons, publications,
     * quotations. (optional)
     * @param recursive Whether queries including a topicId should recursively include sub-topics. Applicable to
     * recordKind claims, declarations, persons, publications, quotations. (optional)
     * @param targetKind Restrict to specific target entity kind. Applicable to recordKind comments. (optional)
     * @param targetId Restrict to specific target entity ID. Applicable to recordKind comments. (optional)
     * @param parentId Restrict to sub-topics thereof or replies to a specific comment. Applicable to recordKind
     * comments, topics. (optional)
     * @param userId Restrict to logs for a specific user or comments created by a specific user. Applicable to
     * recordKind comments, logs. (optional)
     * @param from Timestamp of first record to include. Applicable to recordKind comments, logs. (optional)
     * @param to Timestamp of last record to include. Applicable to recordKind comments, logs. (optional)
     * @param entityKind Restrict to logs for this specific entity kind. Applicable to recordKind logs. (optional)
     * @param entityId Restrict to logs for this specific entity ID. Applicable to recordKind logs. (optional)
     * @param transactionKind Restrict to logs for this specific transaction kind. Applicable to recordKind logs.
     * (optional)
     * @param pageNumber The (zero-based) page number to request. (optional)
     * @param pageSize The page size to use. (optional)
     * @param sort The sort order(s) to apply, formatted as column-name[ ASC|DESC]. (optional)
     * @param col The columns to include. (optional)
     * @param paper The size of paper to use (applies only to PDF output) (optional, default to A4)
     * @param orientation The page orientation (applies only to PDF output) (optional, default to portrait)
     * @param fontSize The font size to use (applies only to HTML and PDF output) (optional, default to 12)
     * @param renderTable Whether to render a table (optional, default to false)
     * @param renderDetails Whether to render record details (optional, default to false)
     * @return The exported list content (status code 200) or Bad request (e.g., invalid combination of query
     * parameters) (status code 400)
     */
    @Operation(operationId = "export", summary = "Exports a list",
        description = "Exports an optionally filtered list to CSV, HTML, PDF or RIS",
        tags = { "CSV", "HTML", "PDF", "RIS" },
        responses = {
            @ApiResponse(responseCode = "200", description = "The exported list content",
                content = { @Content(mediaType = "application/pdf", schema = @Schema(implementation = Resource.class)),
                    @Content(mediaType = "application/x-research-info-systems",
                        schema = @Schema(implementation = Resource.class)),
                    @Content(mediaType = "text/csv", schema = @Schema(implementation = Resource.class)),
                    @Content(mediaType = "text/html", schema = @Schema(implementation = Resource.class)) }),
            @ApiResponse(responseCode = "400",
                description = "Bad request (e.g., invalid combination of query parameters)") })
    @RequestMapping(method = RequestMethod.GET, value = ExportApi.PATH_EXPORT,
        produces = { "application/pdf", "application/x-research-info-systems", "text/csv", "text/html" })
    default ResponseEntity<Resource> export(
        @NotNull @Parameter(name = "recordKind", description = "The list's record type.", required = true,
            in = ParameterIn.PATH) @PathVariable("recordKind") String recordKind,
        @Parameter(name = "recordId",
            description = "The identifier of the single record to return. Applicable to all recordKind except logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "recordId", required = false) @Nullable Long recordId,
        @Parameter(name = "contentType",
            description = "Overrides the HTTP request's Accept header, since browsers ignore the type attribute in, for example, <a href=\"...\" download=\"filename.pdf\" type=\"application/pdf\">filename.pdf</a>. In other words, there's no way for an HTML link to force a browser to request the necessary content type. The \"application/x-research-info-systems\" content type is only supported for recordKind=\"publications\".",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "contentType", required = false,
                defaultValue = "application/pdf") String contentType,
        @Parameter(name = "text", description = "Free text search string. Applicable to all recordKind except logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "text", required = false) @Nullable String text,
        @Parameter(name = "advancedSearch",
            description = "Whether to search text in advanced (boolean) mode, used in conjunction with the text parameter. Applicable to all recordKind except logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "advancedSearch",
                required = false) @Nullable Boolean advancedSearch,
        @Parameter(name = "status",
            description = "Return only records with this status code. Applicable to all recordKind except logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "status", required = false) @Nullable String status,
        @Parameter(name = "fromEntityId",
            description = "The ID of the 'linked-from' master record. Applicable to recordKind claims, declarations, persons, publications, quotations.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "fromEntityId",
                required = false) @Nullable Long fromEntityId,
        @Parameter(name = "fromEntityKind",
            description = "The kind of the 'linked-from' master record. Applicable to recordKind claims, declarations, persons, publications, quotations.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "fromEntityKind",
                required = false) @Nullable String fromEntityKind,
        @Parameter(name = "toEntityId",
            description = "The ID of the 'linked-to' master record. Applicable to recordKind claims, declarations, persons, publications, quotations.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "toEntityId",
                required = false) @Nullable Long toEntityId,
        @Parameter(name = "toEntityKind",
            description = "The kind of the 'linked-to' master record. Applicable to recordKind claims, declarations, persons, publications, quotations.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "toEntityKind",
                required = false) @Nullable String toEntityKind,
        @Parameter(name = "topicId",
            description = "The master topic identifier. Applicable to recordKind claims, declarations, persons, publications, quotations.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "topicId", required = false) @Nullable Long topicId,
        @Parameter(name = "recursive",
            description = "Whether queries including a topicId should recursively include sub-topics. Applicable to recordKind claims, declarations, persons, publications, quotations.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "recursive",
                required = false) @Nullable Boolean recursive,
        @Parameter(name = "targetKind",
            description = "Restrict to specific target entity kind. Applicable to recordKind comments.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "targetKind",
                required = false) @Nullable String targetKind,
        @Parameter(name = "targetId",
            description = "Restrict to specific target entity ID. Applicable to recordKind comments.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "targetId", required = false) @Nullable Long targetId,
        @Parameter(name = "parentId",
            description = "Restrict to sub-topics thereof or replies to a specific comment. Applicable to recordKind comments, topics.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "parentId", required = false) @Nullable Long parentId,
        @Parameter(name = "userId",
            description = "Restrict to logs for a specific user or comments created by a specific user. Applicable to recordKind comments, logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "userId", required = false) @Nullable Long userId,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})Z?$") @Parameter(name = "from",
            description = "Timestamp of first record to include. Applicable to recordKind comments, logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "from", required = false) @Nullable String from,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})Z?$") @Parameter(name = "to",
            description = "Timestamp of last record to include. Applicable to recordKind comments, logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "to", required = false) @Nullable String to,
        @Parameter(name = "entityKind",
            description = "Restrict to logs for this specific entity kind. Applicable to recordKind logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "entityKind",
                required = false) @Nullable String entityKind,
        @Parameter(name = "entityId",
            description = "Restrict to logs for this specific entity ID. Applicable to recordKind logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "entityId", required = false) @Nullable Long entityId,
        @Parameter(name = "transactionKind",
            description = "Restrict to logs for this specific transaction kind. Applicable to recordKind logs.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "transactionKind",
                required = false) @Nullable String transactionKind,
        @Parameter(name = "pageNumber", description = "The page number to request.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "pageNumber",
                required = false) @Nullable Integer pageNumber,
        @Parameter(name = "pageSize", description = "The page size to use.",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "pageSize",
                required = false) @Nullable Integer pageSize,
        @Parameter(name = "sort", description = "The sort order(s) to apply, formatted as column-name[ ASC|DESC].",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "sort",
                required = false) @Nullable List<@Pattern(
                    regexp = "^[a-zA-Z]+(?:\\s+(?:[aA][sS][cC]|[dD][eE][sS][cC]))?$") String> sort,
        @Parameter(name = "col", description = "The columns to include.", in = ParameterIn.QUERY) @Valid @RequestParam(
            value = "col", required = false) @Nullable List<@Pattern(regexp = "^[a-zA-Z]+$") String> col,
        @Parameter(name = "paper", description = "The size of paper to use (applies only to PDF output)",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "paper", required = false,
                defaultValue = "A4") String paper,
        @Parameter(name = "orientation", description = "The page orientation (applies only to PDF output)",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "orientation", required = false,
                defaultValue = "portrait") String orientation,
        @Min(value = 8) @Max(value = 16) @Parameter(name = "fontSize",
            description = "The font size to use  (applies only to HTML and PDF output)",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "fontSize", required = false,
                defaultValue = "12") Integer fontSize,
        @Parameter(name = "renderTable", description = "Whether to render a table",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "renderTable", required = false,
                defaultValue = "false") Boolean renderTable,
        @Parameter(name = "renderDetails", description = "Whether to render record details",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "renderDetails", required = false,
                defaultValue = "false") Boolean renderDetails) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
