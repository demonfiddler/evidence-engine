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

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import io.github.demonfiddler.ee.server.rest.model.ImportedRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-11-19T12:59:29.359836300Z[Europe/London]", comments = "Generator version: 7.17.0")
@Validated
@Tag(name = "RIS", description = "Exported list in RIS format. Applies only to recordKind = publications.")
public interface ImportApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    String PATH_CALL_IMPORT = "/import/{importRecordKind}";

    /**
     * POST /import/{importRecordKind} : Imports records from a file Imports records from an uploaded file (e.g.
     * RIS-format citations).
     * @param importRecordKind The record type to import. (required)
     * @param file (optional)
     * @return File successfully uploaded. Body contains a summary of the record(s) imported or rejected. (status code
     * 200) or Bad request (e.g., invalid combination of query parameters) (status code 400) or Internal server error
     * (status code 500)
     */
    @Operation(operationId = "callImport", summary = "Imports records from a file",
        description = "Imports records from an uploaded file (e.g. RIS-format citations).", tags = { "RIS" },
        responses = { @ApiResponse(responseCode = "200",
            description = "File successfully uploaded. Body contains a summary of the record(s) imported or rejected.",
            content = { @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ImportedRecord.class))) }),
            @ApiResponse(responseCode = "400",
                description = "Bad request (e.g., invalid combination of query parameters)"),
            @ApiResponse(responseCode = "500", description = "Internal server error") },
        security = { @SecurityRequirement(name = "BearerAuth", scopes = { "CRE" }) })
    @RequestMapping(method = RequestMethod.POST, value = ImportApi.PATH_CALL_IMPORT, produces = { "application/json" },
        consumes = { "multipart/form-data" })
    default ResponseEntity<List<ImportedRecord>> callImport(
        @NotNull @Parameter(name = "importRecordKind", description = "The record type to import.", required = true,
            in = ParameterIn.PATH) @PathVariable("importRecordKind") String importRecordKind,
        @Parameter(name = "file", description = "") @RequestPart(value = "file", required = false) MultipartFile file) {

        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString =
                        "[ { \"result\" : \"imported\", \"id\" : 0, \"label\" : \"label\", \"message\" : \"message\" }, { \"result\" : \"imported\", \"id\" : 0, \"label\" : \"label\", \"message\" : \"message\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
