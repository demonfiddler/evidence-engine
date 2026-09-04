# ----------------------------------------------------------------------------------------------------------------------
#  Evidence Engine: A system for managing evidence on arbitrary scientific topics.
#  Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
#  Copyright © 2024-26 Adrian Price. All rights reserved.
#
#  This file is part of Evidence Engine.
#
#  Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
#  GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
#  or (at your option) any later version.
#
#  Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
#  If not, see <https://www.gnu.org/licenses/>. 
# ----------------------------------------------------------------------------------------------------------------------

from httpx import AsyncClient, Response
from pydantic import BaseModel
from urllib.parse import urlencode

from ee_ai_service.models.enums.content_type_kind import ContentTypeKind
from ee_ai_service.models.enums.direction_kind import DirectionKind
from ee_ai_service.models.id import ID
from ee_ai_service.models.imported_record import ImportedRecord
from ee_ai_service.models.inputs.comment_query_filter import CommentQueryFilter
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.inputs.log_query_filter import LogQueryFilter
from ee_ai_service.models.inputs.pageable_input import PageableInput
from ee_ai_service.models.inputs.render_options import RenderOptions
from ee_ai_service.models.inputs.topic_query_filter import TopicQueryFilter
from ee_ai_service.models.inputs.tracked_entity_query_filter import TrackedEntityQueryFilter
from ee_ai_service.runtime.config import Config

EXPORT = "export"
IMPORT = "import"
BACKUP = "backup"
RESTORE = "restore"
RENDER_PARAMETERS = [
    "paper",
    "orientation",
    "fontSize",
    "renderTable",
    "renderDetails",
]
FILTER_PARAMETERS = [
    "recordId",
    "contentType",
    "text",
    "advancedSearch",
    "status",
    "fromEntityId",
    "fromEntityKind",
    "toEntityId",
    "toEntityKind",
    "topicId",
    "recursive",
    "targetKind",
    "targetId",
    "parentId",
    "userId",
    "from",
    "to",
    "entityKind",
    "entityId",
    "transactionKind",
]
PAGESORT_PARAMETERS = [
    "pageNumber",
    "pageSize",
]

class EERestClient:
    """
    Strongly typed Evidence Engine OpenAPI REST client.
    """

    def __init__(self, config: Config):
        self.base_url = config.rest_base_url
        self.api_key = config.api_key
        headers = {} if not config or not hasattr(config, "api_key") or config.api_key is None else {"Authorization": f"Bearer {self.api_key}"}
        self.client = AsyncClient(base_url = self.base_url, headers = headers)

    def create_url(
            self,
            verb: str,
            record_type: str,
            content_type: str | None,
            options: RenderOptions | None,
            filter: BaseModel | None,
            page_sort: PageableInput | None) -> str:

        # Build query string.
        # TODO: Consider using httpx.QueryParams
        # TODO: Consider using Dictionary instead of Pydantic models for options, filter and page_sort
        params = {}
        if content_type is not None:
            params["contentType"] = content_type
        if options is not None:
            # TODO: paper & orientation only apply to PDF; fontSize only applies to HTML & PDF.
            for key in options.__class__.model_fields:
                if (key in RENDER_PARAMETERS):
                    value = getattr(options, key)
                    if value is not None:
                        params[key] = value
            if options.columns is not None:
                cols = []
                for column in options.columns:
                    cols.append(column)
                params["col"] = cols
        if filter is not None:
            for key in filter.__class__.model_fields:
                if (key in FILTER_PARAMETERS):
                    value = getattr(filter, key)
                    if value is not None:
                        params[key] = value
        if page_sort is not None:
            for key in page_sort.__class__.model_fields:
                if (key in PAGESORT_PARAMETERS):
                    value = getattr(page_sort, key)
                    if value is not None:
                        params[key] = value
            if page_sort.sort is not None:
                sort = []
                for order in page_sort.sort.orders:
                    field = order.property
                    if order.direction == DirectionKind.DESCENDING:
                        field += " DESC"
                    sort.append(field)
                params["sort"] = sort

        # Create URL and append query string if necessary.
        url = f"{self.base_url}/{verb}/{record_type}"
        if len(params) > 0:
            query = urlencode(params, doseq = True)
            url += f"?{query}"

        return url

    async def exportClaims(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: LinkableEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Claims in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Claims to export.
            page_sort: Pagination and sorting options.
        Returns:
            The HTTP Response object.
        Raises:
            HTTPStatusError if response status is not 2xx.
        """
        url = self.create_url(EXPORT, "claims", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportComments(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: CommentQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Comments in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Comments to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "comments", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportDeclarations(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: LinkableEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Declarations in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Declarations to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "declarations", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportGroups(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: TrackedEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Groups in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Groups to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "groups", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportJournals(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: TrackedEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Journals in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Journals to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "journals", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportLogs(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: LogQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Log records in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Log records to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "logs", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportPersons(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: LinkableEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Persons in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Persons to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "persons", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportPublications(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: LinkableEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Publications in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Publications to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "publications", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportPublishers(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: TrackedEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Publishers in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Publishers to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "publishers", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportQuotations(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: LinkableEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Quotations in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Quotations to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "quotations", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportTopics(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: TopicQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Topics in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Topics to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "topics", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def exportUsers(
            self,
            content_type: ContentTypeKind,
            options: RenderOptions,
            filter: TrackedEntityQueryFilter | None = None,
            page_sort: PageableInput | None = None) -> Response:
        """
        Exports Users in the specified format.
        Args:
            content_type: The Content-Type into which to export.
            options: The rendering options.
            filter: Selects which Users to export.
            page_sort: Pagination and sorting options.
        """
        url = self.create_url(EXPORT, "users", content_type, options, filter, page_sort)
        response = await self.client.get(url)
        return response.raise_for_status()

    async def importPublications(
            self,
            source,
            topicId: ID | None = None,
            recordId: ID | None = None) -> list[ImportedRecord]:
        """
        Imports Publications from a file, optionally linking them to a Topic and/or other master record.
        Args:
            source: The file from which to import the Publications.
            topicId: The ID of the Topic with which to link the new Publications.
            recordId: The ID of the master record with which to link the new Publications.
        """
        filter = {} if topicId is not None or recordId is not None else None
        if topicId is not None:
            filter["topicId"] = topicId
        if recordId is not None:
            filter["recordId"] = recordId
        url = self.create_url(EXPORT, "users")
        pass
