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

from contextlib import asynccontextmanager
from os import getenv
from fastapi import FastAPI

from ee_ai_service.clients.ee_graphql_client import EEGraphQLClient
from ee_ai_service.clients.ee_rest_client import EERestClient
from ee_ai_service.runtime.logging import configure_logging
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.runtime.config import Config
from langchain_ollama import ChatOllama, OllamaEmbeddings

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    FastAPI lifespan events.
    """

    filename = app.state.config_file or getenv("EE_AI_CONFIG", "config.ini")
    config = Config.load(filename)
    app.state.config = config
    configure_logging(config)

    graphql_client = EEGraphQLClient(config)
    auth = await graphql_client.login(config.username, config.password)
    if auth is None:
        raise Exception(f"Failed to authenticate user {config.username}" )
    config.api_key = auth.token

    rest_client = EERestClient(config)

    inference_client = ChatOllama(model = config.inference_model)
    embedding_client = OllamaEmbeddings(model = config.embedding_model)

    runtime = RuntimeState(config, graphql_client, rest_client, inference_client, embedding_client)

    app.state.runtime = runtime

    yield

    # --- Shutdown ---
    # await graphql_client.aclose()
    # await rest_client.aclose()
