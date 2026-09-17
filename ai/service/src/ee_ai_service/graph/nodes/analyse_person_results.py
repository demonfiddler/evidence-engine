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

from logging import getLogger
from dataclasses import dataclass
import regex

from ee_ai_service.graph.state.complete_person_state import CompletePersonState
from ee_ai_service.models.enums.entity_kind import EntityKind
from ee_ai_service.models.inputs.linkable_entity_query_filter import LinkableEntityQueryFilter
from ee_ai_service.models.person_facts import PersonFacts
from ee_ai_service.models.person_info import PersonInfo
from ee_ai_service.runtime.runtime_state import RuntimeState
from ee_ai_service.utils.citation import cite
from ee_ai_service.utils.name import MATCH_THRESHOLD, Name
from ee_ai_service.utils.string import jaccard


logger = getLogger(__name__)


@dataclass
class DisambiguationHints:
    topics: list[str]
    publications: list[str]
    affiliations: list[str]
    countries: list[str]
    dois: list[str]


# ---------------------------------------------------------------------------
# Canonicalisation utilities
# ---------------------------------------------------------------------------

LTWA_STOPWORDS: set[str] = {
    # For illustration, I include a minimal subset:
    # "the", "of", "and", "in", "on", "for", "journal", "letters", "review",
    # "international", "transactions", "communications", "environment",
    # "earth", "system", "dynamics", "climate", "model", "development",
    "&",
    "@",
    "a",
    "aboard",
    "about",
    "above",
    "abreast",
    "abroad",
    "absent",
    "absolutely",
    "abt.",
    "accordingly",
    "across",
    "additionally",
    "adrift",
    "aft",
    "after",
    "afterward",
    "afterwards",
    "against",
    "ago",
    "ahead",
    "aloft",
    "along",
    "alongside",
    "also",
    "although",
    "amid",
    "amidst",
    "among",
    "amongst",
    "an",
    "and",
    "anti",
    "apart",
    "apropos",
    "around",
    "as",
    "ashore",
    "aside",
    "aslant",
    "astride",
    "at",
    "atop",
    "away",
    "back",
    "backward",
    "backwards",
    "bar",
    "barring",
    "because",
    "before",
    "beforehand",
    "behind",
    "below",
    "beneath",
    "beside",
    "besides",
    "between",
    "beyond",
    "but",
    "by",
    "c.",
    "ca.",
    "certainly",
    "chapter",
    "chez",
    "circa",
    "come",
    "concerning",
    "consequently",
    "considering",
    "contra",
    "counting",
    "cum",
    "despite",
    "down",
    "downhill",
    "downstage",
    "downstairs",
    "downstream",
    "downward",
    "downwards",
    "downwind",
    "during",
    "east",
    "eastward",
    "eastwards",
    "effective",
    "either-or",
    "ere",
    "eventually",
    "except",
    "excepting",
    "excluding",
    "failing",
    "finally",
    "following",
    "for",
    "forth",
    "fortunately",
    "forward",
    "forwards",
    "from",
    "furthermore",
    "given",
    "granted",
    "heavenward",
    "heavenwards",
    "hence",
    "henceforth",
    "here",
    "hereby",
    "herein",
    "hereof",
    "hereto",
    "herewith",
    "home",
    "homeward",
    "homewards",
    "however",
    "if",
    "in",
    "including",
    "indeed",
    "indoors",
    "inside",
    "instead",
    "into",
    "inward",
    "inwards",
    "leftward",
    "leftwards",
    "less",
    "lest",
    "like",
    "meanwhile",
    "mid",
    "midst",
    "minus",
    "mod",
    "modulo",
    "moreover",
    "naturally",
    "near",
    "nearer",
    "nearest",
    "neath",
    "nevertheless",
    "next",
    "nonetheless",
    "nor",
    "north",
    "northeast",
    "northward",
    "northwards",
    "northwest",
    "notwithstanding",
    "now",
    "of",
    "off",
    "offshore",
    "on",
    "once",
    "onto",
    "onward",
    "onwards",
    "opposite",
    "or",
    "otherwise",
    "out",
    "outdoors",
    "outside",
    "outward",
    "outwards",
    "over",
    "overall",
    "overboard",
    "overhead",
    "overland",
    "overseas",
    "pace",
    "paragraph",
    "part",
    "past",
    "pending",
    "per",
    "plus",
    "post",
    "pre",
    "pro",
    "provided",
    "providing",
    "qua",
    "rather",
    "re",
    "regarding",
    "respecting",
    "rightward",
    "rightwards",
    "round",
    "sans",
    "save",
    "saving",
    "seaward",
    "seawards",
    "section",
    "seeing",
    "series",
    "short",
    "since",
    "skyward",
    "skywards",
    "so",
    "south",
    "southeast",
    "southward",
    "southwards",
    "southwest",
    "specifically",
    "spite",
    "still",
    "sub",
    "supposing",
    "surely",
    "surprisingly",
    "than",
    "that",
    "the",
    "then",
    "thence",
    "thenceforth",
    "there",
    "thereby",
    "therefore",
    "therein",
    "thereof",
    "thereto",
    "therewith",
    "though",
    "through",
    "throughout",
    "thus",
    "till",
    "times",
    "to",
    "together",
    "touching",
    "toward",
    "towards",
    "under",
    "underfoot",
    "underground",
    "underneath",
    "undoubtedly",
    "unfortunately",
    "unless",
    "unlike",
    "until",
    "unto",
    "up",
    "uphill",
    "upon",
    "upstage",
    "upstairs",
    "upstream",
    "upward",
    "upwards",
    "upwind",
    "v.",
    "versus",
    "via",
    "vice",
    "vis-à-vis",
    "vs.",
    "w.",
    "w/",
    "w/o",
    "wanting",
    "west",
    "westward",
    "westwards",
    "when",
    "whence",
    "whenever",
    "where",
    "whereas",
    "whereby",
    "wherein",
    "whereto",
    "wherever",
    "wherewith",
    "whether",
    "while",
    "whilst",
    "why",
    "with",
    "within",
    "without",
    "worth",
    "yet",
}


def canonicalise(text: str) -> list[str]:
    """Canonicalise any publication/affiliation/topic string."""
    if not text:
        return []

    # Lowercase
    t = text.lower()

    # Remove punctuation
    t = regex.sub(r"[^\w\s]", " ", t)

    # Remove numbers (years, volume numbers, DOIs handled separately)
    t = regex.sub(r"\b\d+\b", " ", t)

    # Tokenise
    tokens = t.split()

    # Remove LTWA stopwords
    tokens = [tok for tok in tokens if tok not in LTWA_STOPWORDS]

    # Deduplicate + sort for deterministic behaviour
    return sorted(set(tokens))


# ---------------------------------------------------------------------------
# DOI extraction + matching
# ---------------------------------------------------------------------------

DOI_REGEX = regex.compile(r"10\.\d{4,9}/[-._;()/:A-Za-z0-9]+")

def extract_dois(text: str) -> list[str]:
    return DOI_REGEX.findall(text)


def doi_match(hint_dois: list[str], extracted_dois: list[str]) -> float:
    """Exact DOI match → score 1.0."""
    if not hint_dois or not extracted_dois:
        return 0.0
    hs = {d.lower().strip() for d in hint_dois}
    es = {d.lower().strip() for d in extracted_dois}
    return 1.0 if hs & es else 0.0


# ---------------------------------------------------------------------------
# Publication similarity (DOI-aware)
# ---------------------------------------------------------------------------

def publication_similarity(hints: list[str], extracted: list[str],
                           hint_dois: list[str], extracted_dois: list[str]) -> float:
    """Hybrid DOI + canonicalised title matching."""
    # DOI exact match
    doi_score = doi_match(hint_dois, extracted_dois)
    if doi_score == 1.0:
        return 1.0

    # Canonicalised title matching
    scores = []
    for h in hints:
        h_can = canonicalise(h)
        for e in extracted:
            e_can = canonicalise(e)
            scores.append(jaccard(h_can, e_can))

    return max(scores) if scores else 0.0


# ---------------------------------------------------------------------------
# Affiliation similarity
# ---------------------------------------------------------------------------

def affiliation_similarity(hints: list[str], extracted: list[str]) -> float:
    scores = []
    for h in hints:
        h_can = canonicalise(h)
        for e in extracted:
            e_can = canonicalise(e)
            scores.append(jaccard(h_can, e_can))
    return max(scores) if scores else 0.0


# ---------------------------------------------------------------------------
# Topic similarity
# ---------------------------------------------------------------------------

def topic_similarity(hints: list[str], extracted: list[str]) -> float:
    scores = []
    for h in hints:
        h_can = canonicalise(h)
        for e in extracted:
            e_can = canonicalise(e)
            scores.append(jaccard(h_can, e_can))
    return max(scores) if scores else 0.0


# ---------------------------------------------------------------------------
# Country matching
# ---------------------------------------------------------------------------

# TODO: reimplement using ISO-3166-1 country codes
def country_match(hints: list[str], extracted: list[str]) -> float:
    if not hints or not extracted:
        return 0.0
    hs = {h.upper().strip() for h in hints}
    es = {e.upper().strip() for e in extracted}
    return 1.0 if hs & es else 0.0


# ---------------------------------------------------------------------------
# Name matching (your existing logic)
# ---------------------------------------------------------------------------

def best_name(target: "Name", names: list[Name]) -> Name | None:
    if not names:
        return None
    best = max(names, key=lambda n: target.match_score(n))
    return best if target.match_score(best) >= MATCH_THRESHOLD else None


# ---------------------------------------------------------------------------
# Candidate scoring
# ---------------------------------------------------------------------------

def score_candidate(target_name: Name,
                    hints: DisambiguationHints,
                    facts: "PersonFacts") -> float:

    # Name score
    name_score = max((target_name.match_score(n) for n in facts.names), default=0.0)

    # Topic score
    topic_score = topic_similarity(hints.topics, facts.topics)

    # Publication score (DOI-aware)
    pub_score = publication_similarity(
        hints.publications,
        facts.publications,
        hints.dois,
        facts.dois if hasattr(facts, "dois") else []
    )

    # Affiliation score
    aff_score = affiliation_similarity(hints.affiliations, facts.affiliations)

    # Country score
    country_score = country_match(hints.countries, facts.countries)

    # Weighted score
    score = (
        0.40 * name_score +
        0.20 * topic_score +
        0.20 * pub_score +
        0.10 * aff_score +
        0.10 * country_score
    )

    # Hard filters
    if name_score < MATCH_THRESHOLD:
        return 0.0
    if topic_score == 0.0 and pub_score == 0.0:
        return 0.0
    if score < 0.3:
        return 0.0

    return score


# ---------------------------------------------------------------------------
# Main Node B: analyse_person_node
# ---------------------------------------------------------------------------

def analyse_person_node(
    state: CompletePersonState,
    hints: DisambiguationHints,
) -> dict:
    """
    Cluster-based identity resolution:
    - Score all results
    - Select all results within epsilon of best score
    - Check identity consistency across cluster
    - Merge all facts from consistent cluster
    """

    target_name = state.person_info.name

    # ------------------------------------------------------------
    # 1. Score all results
    # ------------------------------------------------------------
    scored = []
    for facts in state.facts_per_result:
        s = score_candidate(target_name, hints, facts)
        if s > 0.0:
            scored.append((facts, s))

    if not scored:
        return {"person_info": {}}

    # Sort by score descending
    scored.sort(key=lambda x: x[1], reverse=True)
    best_score = scored[0][1]

    # ------------------------------------------------------------
    # 2. Select cluster: all results within epsilon of best score
    # ------------------------------------------------------------
    EPSILON = 0.15
    cluster = [
        facts for facts, score in scored
        if score >= best_score - EPSILON
    ]

    if not cluster:
        return {"person_info": {}}

    # ------------------------------------------------------------
    # 3. Identity consistency checks across cluster
    # ------------------------------------------------------------
    def consistent_names(facts_list):
        """All names must match target strongly."""
        for facts in facts_list:
            bn = best_name(target_name, facts.names)
            if bn is None:
                return False
        return True

    def consistent_affiliations(facts_list):
        """Affiliations must not contradict."""
        # Canonicalise all affiliations
        canon_sets = []
        for facts in facts_list:
            canon = [canonicalise(a) for a in facts.affiliations]
            canon_sets.append(canon)

        # If any pair has zero similarity across all tokens → contradiction
        for i in range(len(canon_sets)):
            for j in range(i + 1, len(canon_sets)):
                sim = 0.0
                for a in canon_sets[i]:
                    for b in canon_sets[j]:
                        sim = max(sim, jaccard(a, b))
                if sim == 0.0:
                    return False
        return True

    def consistent_topics(facts_list):
        """Topics must not contradict."""
        canon_sets = []
        for facts in facts_list:
            canon = [canonicalise(t) for t in facts.topics]
            canon_sets.append(canon)

        for i in range(len(canon_sets)):
            for j in range(i + 1, len(canon_sets)):
                sim = 0.0
                for a in canon_sets[i]:
                    for b in canon_sets[j]:
                        sim = max(sim, jaccard(a, b))
                if sim == 0.0:
                    return False
        return True

    def consistent_countries(facts_list):
        """Countries must not contradict."""
        all_countries = set()
        for facts in facts_list:
            for c in facts.countries:
                all_countries.add(c.upper().strip())
        # If more than one distinct country → contradiction
        return len(all_countries) <= 1

    # Run consistency checks
    if not consistent_names(cluster):
        return {"person_info": {}}
    if not consistent_affiliations(cluster):
        return {"person_info": {}}
    if not consistent_topics(cluster):
        return {"person_info": {}}
    if not consistent_countries(cluster):
        return {"person_info": {}}

    # ------------------------------------------------------------
    # 4. Merge facts from all consistent cluster members
    # ------------------------------------------------------------
    merged_names = []
    merged_topics = []
    merged_publications = []
    merged_affiliations = []
    merged_countries = []
    merged_emails = []
    merged_dois = []

    for facts in cluster:
        merged_names.extend(facts.names)
        merged_topics.extend(facts.topics)
        merged_publications.extend(facts.publications)
        merged_affiliations.extend(facts.affiliations)
        merged_countries.extend(facts.countries)
        merged_emails.extend(facts.emails)
        if hasattr(facts, "dois"):
            merged_dois.extend(facts.dois)

    # Deduplicate
    merged_topics = sorted(set(merged_topics))
    merged_publications = sorted(set(merged_publications))
    merged_affiliations = sorted(set(merged_affiliations))
    merged_countries = sorted(set(merged_countries))
    merged_emails = sorted(set(merged_emails))
    merged_dois = sorted(set(merged_dois))

    # Best name from merged set
    bn = best_name(target_name, merged_names)
    if bn is None:
        return {"person_info": {}}

    # Best country (if any)
    best_country = merged_countries[0].upper().strip() if merged_countries else None

    # Notes: merge topics + affiliations
    notes_parts = []
    if merged_topics:
        notes_parts.append("Topics: " + "; ".join(merged_topics))
    if merged_affiliations:
        notes_parts.append("Affiliations: " + "; ".join(merged_affiliations))
    notes = "\n".join(notes_parts) if notes_parts else None

    # Qualifications handled upstream
    qualifications = None

    person_info = PersonInfo(
        name=str(bn),
        orcid=None,
        country=best_country,
        notes=notes,
        qualifications=qualifications,
    )

    return {"person_info": person_info}

async def analyse_person_results(state: CompletePersonState, *, config) -> dict[str, any]:
    """Deterministically analyses person search results."""

    topics = [
        link.fromEntity.label.lower()
        for link in state.person.toEntityLinks.content
        if link.fromEntity.entityKind == EntityKind.TOPIC
    ]

    # NOTE: can't use state.person.toEntityLinks to get linked Publications because EntityLink.fromEntity only has a small subset of Publication fields.
    runtime: RuntimeState = config["metadata"]["runtime"]
    filter = LinkableEntityQueryFilter(toEntityId = state.person.id)
    page = await runtime.graphql_client.publications(filter)
    publications = page.content
    citations = [
        cite(p)
        for p in publications
    ]
    dois = [
        p.doi
        for p in publications
        if p.doi
    ]

    countries = [state.person_info.country] if state.person_info.country else []
    hints = DisambiguationHints(topics, citations, state.person_info.affiliations, countries, dois)
    pi_dict = analyse_person_node(
        state,
        hints,
    )["person_info"]
    result = {"person_info": PersonInfo.model_validate(pi_dict)} if pi_dict else {}

    return result
