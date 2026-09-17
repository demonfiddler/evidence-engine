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

from json import loads
from fast_json_repair import repair_json
from logging import getLogger
import regex
from textwrap import dedent

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.models.person_facts import PersonFacts
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.name import Name
from ee_ai_service.utils.string import prefix_lines, truncate
from pydantic import BaseModel, Field

logger = getLogger(__name__)

def _normalise_text(text: str) -> str:
    """Prepares text for extraction by the LLM."""

    # Reduce entropy by removing URL, doi and numeric strings.
    text = regex.sub(r'https?://\S+', '', text)
    text = regex.sub(r'doi:\S+', '', text)
    text = regex.sub(r'\b\d{2,}\b', '', text)

    # Collapse excessive whitespace.
    text = regex.sub(r'\s+', ' ', text).strip()

    # Remove citation parentheses.
    text = regex.sub(r'\([^)]*\)', '', text)

    return text

def _repair_json(content: str) -> str:
    """Repair a JSON string as a single object."""

    # First use the fast-json-repair library function.
    data = repair_json(content)
    # Strip JSON markup to leave a single object.
    i = 0
    j = len(data) - 1
    while data[i] != '{' and i < j:
        i += 1
    while data[j] != '}' and j > i:
        j -= 1
    return data[i:j+1]

async def _extract(
        runtime: RuntimeState,
        target: str,
        prompt_template: str,
        person_name: str,
        text: str,
        ) -> dict[str, any]:
    logger.info(f"Extracting {target} facts")
    data = None

    # For reasons unknown, the LLM sometimes returns invalid JSON.
    # Retry up to 5 times if this happens, halving the formatted search result token count each time.
    token_count = 4000
    for retry in range(5):
        try:
            text = truncate(text, token_count=token_count, split=0.8, use_ellipsis=True)
            formatted_search_result = f"{prefix_lines(text, prefix='    > ', prefix_first=False)}"
            prompt = dedent(prompt_template.format(person_name, formatted_search_result))
            ai_message = await runtime.inference_client.ainvoke(prompt)
            data = _repair_json(ai_message.content)
            data = loads(data)
            break
        except Exception as e:
            logger.warning(f"Attempt {retry + 1} failed to extract {target} facts: {e}.")
            token_count /= 2
            if retry == 4:
                # raise e
                logger.warning(f"Failed prompt:\n{prompt}")
                break
    return data

class NamesAndEmails(BaseModel):
    names: list[str] = Field(default_factory=list)
    emails: list[str] = Field(default_factory=list)

async def _extract_names_and_emails(
        runtime: RuntimeState,
        person_name: str,
        text: str,
        ) -> NamesAndEmails:
    prompt_template = """
    Extract candidate facts about the person {} from the text below.
    Extract ANY name-like or email-like strings but do not return pronouns.
    Do not decide relevance. Do not merge facts. Do not infer identity. Do not include empty strings.
    Do not include comments on or justifications for your choices.

    Return ONLY a JSON object with the following fields:
    - names: JSON array of strings, including title or honorific if known, empty if none were found.
    - emails: JSON array of strings, empty if none were found.

    Text:
    {}
    """
    data = await _extract(runtime, "name and email", prompt_template, person_name, text)
    try:
        return NamesAndEmails.model_validate(data)
    except:
        logger.error(f"Failed to convert {data} to NamesAndEmails")
        return None

class AffiliationsAndCountries(BaseModel):
    affiliations: list[str] = Field(default_factory=list)
    countries: list[str] = Field(default_factory=list)

async def _extract_affiliations_and_countries(
        runtime: RuntimeState,
        person_name: str,
        text: str,
        ) -> AffiliationsAndCountries:
    prompt_template = """
    Extract candidate facts about the person {}'s institutional affiliations from the text below.
    Extract ANY affiliation-like or country-like strings.
    Do not decide relevance. Do not merge facts. Do not infer identity. Do not include empty strings.
    Do not include comments on or justifications for your choices.

    Return ONLY a JSON object with the following fields:
    - affiliations: JSON array of strings, empty if none were found.
    - countries: JSON array of strings, empty if none were found.

    Text:
    {}
    """
    data = await _extract(runtime, "affiliation and country", prompt_template, person_name, text)
    try:
        return AffiliationsAndCountries.model_validate(data)
    except:
        logger.error(f"Failed to convert {data} to AffiliationsAndCountries")
        return None

class TopicsAndPublications(BaseModel):
    topics: list[str] = Field(default_factory=list)
    publications: list[str] = Field(default_factory=list)

async def _extract_topics_and_publications(
        runtime: RuntimeState,
        person_name: str,
        text: str,
        ) -> TopicsAndPublications:
    prompt_template = """
    Extract candidate facts about the person {}'s research areas and publications from the text below.
    Extract ANY topic-like or publication-like strings.
    Do not decide relevance. Do not merge facts. Do not infer identity. Do not include empty strings.
    Do not include comments on or justifications for your choices.

    Return ONLY a JSON object with the following fields:
    - topics: JSON array of strings, empty if none were found.
    - publications: JSON array of strings, empty if none were found.

    Text:
    {}
    """
    data = await _extract(runtime, "topic and publication", prompt_template, person_name, text)
    try:
        return TopicsAndPublications.model_validate(data)
    except:
        logger.error(f"Failed to convert {data} to TopicsAndPublications")
        return None

async def extract_person_facts(state: CompletePersonState, *, config) -> dict[str, any]:
    """Extract person facts from search results using an LLM call. N.B. This can be non-deterministic with smaller models."""

    runtime: RuntimeState = config["metadata"]["runtime"]
    person_name = state.person_info.name.format("%t%f%l")

    facts_list: list[PersonFacts] = []
    for search_result in state.search_results:
        url = search_result.url
        text = search_result.text
        # text = _normalise_text(text)
        # formatted_search_result = f"{prefix_lines(text, prefix='    > ', prefix_first=False)}"

        logger.info(f"Extracting person facts from '{url}'.")
        # logger.debug(f"Search result:\n{formatted_search_result}")

        # Call LLM to extract various types of fact.
        ne: NamesAndEmails = await _extract_names_and_emails(runtime, person_name, text)
        ac: AffiliationsAndCountries = await _extract_affiliations_and_countries(runtime, person_name, text)
        tp: TopicsAndPublications = await _extract_topics_and_publications(runtime, person_name, text)

        # Merge discovered facts into a single object.
        pf = PersonFacts(search_result=search_result)
        if ne:
            pf.names.extend(ne.names)
            pf.emails.extend(ne.emails)
        if ac:
            pf.affiliations.extend(ac.affiliations)
            pf.countries.extend(ac.countries)
        if tp:
            pf.topics.extend(tp.topics)
            pf.publications.extend(tp.publications)
        facts_list.append(pf)

    # Parse returned name strings into Name objects for later use.
    for facts in facts_list:
        names = []
        for name in facts.names:
            name_obj = Name.parse(name) if isinstance(name, str) else name
            if (name_obj):
                names.append(name_obj)
        facts.names.clear()
        facts.names.extend(names)

    return {"facts_per_result": facts_list}
