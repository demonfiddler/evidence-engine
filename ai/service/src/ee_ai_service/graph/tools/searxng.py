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

from httpx import AsyncClient
from logging import getLogger
from langchain_core.tools import tool
from langgraph.prebuilt import ToolRuntime

from ee_ai_service.graph.workflows.workflow import RuntimeState
from ee_ai_service.models.search_result import SearchResult
from ee_ai_service.runtime.config import Config

logger = getLogger(__name__)

client = AsyncClient(timeout=15)

# async def web_search(runtime: ToolRuntime, query: str, num_results: int = 5) -> list[SearchResult]:
@tool
async def web_search(query: str, num_results: int = 5) -> list[SearchResult]:
    """
    Search the web for current information about a subject.
    Returns titles, URLs, and snippets.
    Use this when you need up-to-date facts, biography details, or qualifications.

    Args:
        query: The web query string to pass to the search engine.
        num_results: The number of results to return, default = 5.
    Returns:
        A list of SearchResults.
    """

    # Try and fool the search instance into thinking that we are an ordinary browser.
    # config = runtime.config
    # ee_runtime: RuntimeState = config["metadata"]["runtime"] if config else None
    # ee_config: Config = ee_runtime.config if ee_runtime else None
    # user_agent = ee_config.user_agent if ee_config.user_agent else "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:155.0) Gecko/20100101 Firefox/155.0"
    user_agent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:155.0) Gecko/20100101 Firefox/155.0"
    headers = {"User-Agent": user_agent}

    # Perform search using our local SearXNG instance.
    response = await client.get(
        "http://localhost:8001/search",
        params = {
            "q": query,
            "format": "json",
            "categories": "biography, science"
        },
        headers = headers
    )
    response.raise_for_status()
    data = response.json()

    results = [
        SearchResult(title=r.get("title"), url=r.get("url"), text=r.get("content"))
        for r in data.get("results", [])[:num_results]
    ]

    logger.info(f"web_search for '{query}' returned {len(results)} results")

    return {
        "results": results,
        "status": "complete"
    }
