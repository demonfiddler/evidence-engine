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

from bs4 import BeautifulSoup
from copy import copy
from httpx import Response
from logging import getLogger
from readability.readability import Document

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.graph.tools.searxng import web_search
from ee_ai_service.graph.workflows.workflow import RuntimeState
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.person_info import PersonInfo
from ee_ai_service.models.search_result import SearchResult
from ee_ai_service.utils.safe_get import safe_get
from ee_ai_service.utils.name import MATCH_THRESHOLD, Name

logger = getLogger(__name__)

_HEADERS = {"Accept": "text/plain, text/html"}

async def research_person(state: CompletePersonState, *, config) -> dict[str, any]:
    """Research a person using web searches."""

    topics = " ".join([
        link.fromEntity.label.lower()
        for link in state.person.toEntityLinks.content
        if link.fromEntity.entityKind == EntityKind.TOPIC
    ])
    person_name = state.person.name()
    person_info = PersonInfo(name=person_name)

    # Search orchid.org first to get ORCID and possibly some biographical/affiliation info?
    query = f"!orc {person_name}"
    result = await web_search.ainvoke(input={"query": query, "num_results": 10})
    orcid_results: list[SearchResult] = result["results"]
    orcid_records: list[dict] = []

    # Work through the ORCID results, fetching each full record in turn and assessing the degree to which it matches the contextual person.
    runtime: RuntimeState = config["metadata"]["runtime"]
    i = 0
    max_similarity = 0
    while i < len(orcid_results):
        # Fetch the full ORCID record. N.B. returns XML unless JSON is requested.
        orcid = orcid_results[i].title
        i += 1
        orcid_response: Response = await runtime.web_client.get(f"https://pub.orcid.org/v3.0/{orcid}/record", headers = {"Accept": "application/json"})
        orcid_record = orcid_response.raise_for_status().json()
        # orcid-identifier
        #   path (e.g., "0000-0003-3922-9833")
        # person
        #   name
        #     given-names
        #     family-name
        #   emails
        #     email[]
        #       email
        #   external-identifiers
        #     external-identifier[]
        #       source
        #         source-name
        #           value (e.g, The Australian National University)
        #       external-id-type (e.g., "Scopus Author ID")
        #       external-id-value (e.g., "56324505100")
        #       external-id-relationship (e.g., "self")
        # activities-summary
        #   educations
        #     affiliation-group[]
        #       summaries[]
        #         education-summary
        #           department-name (e.g., "Climate Change Research Centre")
        #           role-title (e.g., "PhD")
        #           start-date
        #             year
        #               value (e.g., "2012")
        #           end-date
        #             year
        #               value (e.g., "2016")
        #           organization
        #             name (e.g. "University of New South Wales")
        #             address
        #               city (e.g., "Sydney")
        #               region (e.g., "NSW")
        #               country (e.g., "AU")
        #             disambiguated-organization
        #               disambiguated-organization-identifier (e.g., "7800")
        #               disambiguation-source (e.g., "RINGGOLD")
        #   employments
        #     affiliation-group[]
        #       summaries[]
        #         employment-summary
        #           source
        #             source-name
        #               value": "The Australian National University"
        #           department-name
        #           role-title
        #           start-date
        #           end-date
        #           organization
        #             name (e.g., "The Australian National University")
        #             address
        #               city (e.g., "Canberra")
        #               region (e.g., "Australian Capital Territory")
        #               country (e.g., "AU")
        #             disambiguated-organization
        #               disambiguated-organization-identifier (e.g., "https://ror.org/019wvm592")
        #               disambiguation-source (e.g, "ROR")
        orcid_person_name = safe_get(orcid_record, ["person", "name"])
        given_names = safe_get(orcid_person_name, ["given-names", "value"])
        family_name = safe_get(orcid_person_name, ["family-name", "value"])
        if not family_name:
            continue
        name = Name(first_names = given_names, last_name = family_name)
        similarity = name.match_score(person_name)
        if similarity < MATCH_THRESHOLD:
            continue
        orcid_record["similarity"] = similarity
        orcid_record["name"] = name
        if max_similarity == 0:
            max_similarity = similarity
        if similarity >= max_similarity:
            orcid_records.append(orcid_record)
        else:
            break

    # If there was just one optimal match, we will use that.
    if len(orcid_records) == 1:
        orcid_record = orcid_records[0]
        orcid = safe_get(orcid_record, ["orcid-identifier", "path"])
        emails = [email["email"] for email in safe_get(orcid_record, ["person", "emails", "email"])]
        qualifications = []
        affiliations = []
        affiliation_groups = safe_get(orcid_record, ["activities-summary", "educations", "affiliation-group"])
        if affiliation_groups:
            for affiliation_group in affiliation_groups:
                summaries = affiliation_group["summaries"]
                if summaries:
                    for text in summaries:
                        education_summary = text["education-summary"]
                        if education_summary:
                            department_name = education_summary["department-name"]
                            role_title = education_summary["role-title"]
                            end_year = safe_get(education_summary, ["end-date", "year", "value"])
                            organization = education_summary["organization"]
                            if organization:
                                name = organization["name"]
                                address = organization["address"]
                                if address:
                                    city = address["city"]
                                    region = address["region"]
                                    country = address["country"]
                                    qualification = f"{f'{role_title} from ' if role_title else ''}{f'{department_name} at ' if department_name else ''}{f'{name}: ' if name else ''}{f'{city}, ' if city else ''}{f'{region}, ' if region else ''}{country if country else ''}{f' ({end_year})' if end_year else ''}."
                                    qualifications.append(qualification.strip())
        affiliation_groups = safe_get(orcid_record, ["activities-summary", "employments", "affiliation-group"])
        if affiliation_groups:
            for affiliation_group in affiliation_groups:
                summaries = affiliation_group["summaries"]
                if summaries:
                    for text in summaries:
                        employment_summary = text["employment-summary"]
                        if employment_summary:
                            department_name = employment_summary["department-name"]
                            role_title = employment_summary["role-title"]
                            start_year = safe_get(employment_summary, ["start-date", "year", "value"])
                            end_year = safe_get(employment_summary, ["end-date", "year", "value"])
                            organization = employment_summary["organization"]
                            if organization:
                                name = organization["name"]
                                address = organization["address"]
                                if address:
                                    city = address["city"]
                                    region = address["region"]
                                    country = address["country"]
                                    note = f"{f'{role_title} in ' if role_title else ''}{f'{department_name} at ' if department_name else ''}{f'{name}: ' if name else ''}{f'{city}, ' if city else ''}{f'{region}, ' if region else ''}{country if country else ''}{f' ({start_year if start_year else '?'}–{end_year if end_year else "present"})' if start_year or end_year else ''}."
                                    affiliations.append(note.strip())
        person_info.orcid = orcid
        person_info.emails = emails
        person_info.qualifications = qualifications
        person_info.affiliations = affiliations
        logger.info(f"Found ORCID for {person_name}: {orcid}")
    elif len(orcid_records) > 1:
        # Multiple equal matches - human disambiguation required.
        orcids = [safe_get(orcid_record, ["orcid-identifier", "path"]) for orcid_record in orcid_records]
        person_info.notes = f"Possible ORCID matches: {orcids}"
        logger.info(f"Found {len(orcid_records)} ORCID records matching {person_name}: {orcids}")
    else:
        logger.warning(f"Failed to obtain ORCID record for {person_name}")

    # Search generally for biographical/affiliation info.
    query = f"!general {person_name} {topics} biography affiliations"
    general_response = await web_search.ainvoke({"query": query, "num_results": 10})
    general_results: list[SearchResult] = general_response["results"]

    search_results: list[SearchResult] = []
    for general_result in general_results:
        # Fetch the full page of the search result.
        url = general_result.url
        try:
            detail_response: Response = await runtime.web_client.get(url, headers = _HEADERS)
        except Exception as e:
            logger.warning(f"Exception {str(e)} fetching {url}")
            continue
        if not detail_response.is_success:
            logger.warning(f"{detail_response.status_code} Failed to fetch {url}")
            continue
        raw = detail_response.text
        text = None
        content_type = detail_response.headers.get("Content-Type").split(';')[0]
        match content_type:
            case "text/html":
                # Convert the HTML markup to plain text.
                try:
                    doc = Document(raw)
                    html = doc.summary()
                    soup = BeautifulSoup(html, "html.parser")
                    text = soup.get_text(separator = " ", strip = True)
                except Exception as e:
                    logger.warning(f"Exception '{e}' when parsing result from {url}")
                    continue
            case "text/plain":
                text = raw
            case _:
                logger.warning(f"Received unsupported Content-Type '{content_type}' from {url}")
                continue
        if not text:
            continue

        search_result = copy(general_result)
        search_result.text = text
        search_results.append(search_result)

    return {"person_info": person_info, "search_results": search_results}
