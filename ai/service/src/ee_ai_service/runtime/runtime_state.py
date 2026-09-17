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

from dataclasses import dataclass
from httpx import AsyncClient
from langchain_core.language_models.chat_models import BaseChatModel
from langchain_core.embeddings import Embeddings

from ee_ai_service.clients.ee_graphql_client import EEGraphQLClient
from ee_ai_service.clients.ee_rest_client import EERestClient
from ee_ai_service.runtime.config import Config

@dataclass
class RuntimeState:
    config: Config
    graphql_client: EEGraphQLClient
    rest_client: EERestClient
    web_client: AsyncClient
    inference_client: BaseChatModel
    embedding_client: Embeddings
