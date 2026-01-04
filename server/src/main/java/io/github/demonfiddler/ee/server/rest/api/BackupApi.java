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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2025-12-21T13:57:39.773008Z[Europe/London]", comments = "Generator version: 7.17.0")
@Validated
@Tag(name = "backup", description = "the backup API")
public interface BackupApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    String PATH_BACKUP = "/backup";

    /**
     * GET /backup : Backup the database Backs up the database as a zipped collection of CSV files
     * @param kind Whether to include static lookup table data or just the application data (required)
     * @return Response from a backup operation (status code 200) or Bad request (e.g., invalid combination of query
     * parameters) (status code 400) or Internal server error (status code 500)
     */
    @Operation(operationId = "backup", summary = "Backup the database",
        description = "Backs up the database as a zipped collection of CSV files",
        responses = {
            @ApiResponse(responseCode = "200", description = "Response from a backup operation",
                content = {
                    @Content(mediaType = "application/zip", schema = @Schema(implementation = Resource.class)) }),
            @ApiResponse(responseCode = "400",
                description = "Bad request (e.g., invalid combination of query parameters)"),
            @ApiResponse(responseCode = "500", description = "Internal server error") },
        security = { @SecurityRequirement(name = "BearerAuth", scopes = { "ADM" }) })
    @RequestMapping(method = RequestMethod.GET, value = BackupApi.PATH_BACKUP, produces = { "application/zip" })
    default ResponseEntity<Resource>
        backup(@NotNull @Parameter(name = "kind",
            description = "Whether to include static lookup table data or just the application data", required = true,
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "kind", required = true) String kind) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
