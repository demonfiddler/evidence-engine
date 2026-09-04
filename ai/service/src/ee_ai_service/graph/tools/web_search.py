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

from langchain_core.tools import tool
import httpx

@tool
def web_search(query: str, num_results: int = 5) -> str:
    """
    Search the web for current information about a subject.
    Returns titles, URLs, and snippets.
    Use this when you need up-to-date facts, biography details, or qualifications.

    Args:
        query: The web query string to pass to the search engine.
        num_results: The number of results to return, default = 5.
    Returns:
        Search results concatenated into a single string, or "No results found." if empty.
    """
    with httpx.Client(timeout=15) as client:
        resp = client.get(
            "https://api.duckduckgo.com/",
            params={"q": query, "format": "json", "no_redirect": 1}
        )
        resp.raise_for_status()
        data = resp.json()

    # Extract meaningful results
    results = []
    for item in data.get("RelatedTopics", []):
        if "Text" in item:
            results.append(item["Text"])

    return "\n".join(results[:num_results]) or "No results found."
