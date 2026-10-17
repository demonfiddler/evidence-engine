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

from __future__ import annotations
from pydantic import BaseModel
from regex import IGNORECASE, Pattern, compile, split, sub
from StrTokenizer import StrTokenizer

_TITLE = compile(
    r"^(?:Ambassador|Amb\.?|Baroness|Baronet|Bart\.?|Bt\.?|Brigadier|Brig\.?|"
    r"Captain|Capt?\.?|Councillor|Cllr\.?|Colonel|Col\.?|Commander|Cdr\.?|"
    r"Dame|Doctor|Dr\.?|Fr\.?|General|Gen\.?|King|Lady|Lord|Lieutenant|"
    "Lieut\.?|Lt\.?|Major|Maj\.?|Miss|Mrs?\.?|Ms\.?|nat\.?|Prince|Princess"
    r"|Professor|Prof\.?|Queen|Rabbi|Rab\.?|rer\.?|\(?[Rr]et\.?\)?|Reverend|"
    "Rev\.?|Senator|Sen\.?|Sergeant|Sgt\?|Sir)$",
    IGNORECASE,
)
_INITIALS = compile(
    r"[\p{Uppercase_Letter}](?![\p{Lowercase_Letter}])"
    r"[\p{Uppercase_Letter}. ]*(?![\p{Lowercase_Letter}])"
)
_NICKNAME = compile("([\"'])([\\p{IsAlphabetic}]+)\\1")
_PREFIX = compile(
    r"^(?:[aA]l|[bB]in|[dD]e[nlr]?|[dD]u|[lL][ae]|[vV][ao]n|[zZ]u|[sS]t\.?|[tT]e)$"
)
_LAST_NAME = compile(r"^[\p{IsAlphabetic}'’.-]+$")
_SUFFIX = compile(
    r"^(?:I{1,3}|I?V|VI{1,3}|I?X|Jn?r\.?|Sn?r\.?)$",
    IGNORECASE,
)
_ALIAS = compile(r"\((?![Rr]et\.?\))([\p{IsAlphabetic}\s]+)\)")

# Explicit post‑nominals only, to avoid clashing with initials.
_POST_NOMINAL = compile(
    r"^(?:"
    r"[CDGKMO]BE|"
    r"Ph\.?D\.?|D\.?Phil\.?|"
    r"[BM]\.?A\.?|[BM]\.?Sc?\.?|"
    r"F\.?R\.?S\.?|[QK]\.?C\.?|"
    r"LL\.?[BM]\.?"
    ")$",
    IGNORECASE,
)
_PERIOD_NOSPACE = compile(r"\.(?=[^ ])")
_SPACES = compile(" +")

LAST_NAME_SCORE = 0.6
FIRST_NAMES_SCORE = 0.25
INITIALS_SCORE = 0.2
TITLE_SCORE = 0.025
NICKNAME_SCORE = 0.025
PREFIX_SCORE = 0.025
SUFFIX_SCORE = 0.025
ALIAS_SCORE = 0.025
POST_NOMINALS_SCORE = 0.025

MATCH_THRESHOLD = LAST_NAME_SCORE + INITIALS_SCORE
NAME_SEP = " ."
_MAX_INITIALS = 4


def _match(token: str, pattern: Pattern, result: list[str], prepend: bool):
    """
    Attempts to match a token against a pattern. If successful, inserts the token into a list.
    token: The token to test.
    pattern: The pattern to match.
    result: The result buffer.
    prepend: {@code true} to prepend {@code token} to {@code result}, {@code false} to append it.
    Returns:
    True if token matched pattern, otherwise False.
    """
    matcher = pattern.match(token)
    if matcher:
        match = matcher.group(1 if len(matcher.groups()) == 1 else 0)
        if prepend:
            result.insert(0, match)
        else:
            result.append(match)
        return True
    return False


def _split(s: str) -> list[str]:
    return [t for t in split(_SPACES, s.strip()) if t]


def _append(
    sb: list[str], s: str | None, open_delim: str = None, close_delim: str = None
):
    if s is not None:
        if len(sb) != 0 and sb[-1] != " ":
            sb.append(" ")
        if open_delim is not None:
            sb.append(open_delim)
        sb.append(s)
        if close_delim is not None:
            sb.append(close_delim)


def _to_initials(token: str) -> str:
    """
    Converts an all‑uppercase, letters‑only token to space‑separated,
    period-terminated initials. Leaves tokens with periods or spaces
    unchanged.
    """
    if (
        len(token) <= _MAX_INITIALS
        and token.isupper()
        and token.isalpha()  # no periods, no spaces
    ):
        token = ". ".join(token)
        if len(token) > 0 and token[-1] != ".":
            token += "."
    return token


def _to_title_case(s: str) -> str:
    """
    Converts a string to title case. The first letter and any letter following
    any of ' ’ . or - is converted to uppercase; all other letters are
    converted to lowercase.

    Args:
        s: The input string to convert.
    Returns:
        The input string converted to title case.
    """
    if not s:
        return None

    buf: list[str] = []
    at_start = True
    for c in s:
        buf.append(c.upper() if at_start else c.lower())
        at_start = c in " '’.-"
    return "".join(buf)


class Name(BaseModel):
    """Represents a personal name."""

    title: str | None = None
    first_names: str | None = None
    nickname: str | None = None
    prefix: str | None = None
    last_name: str | None = None
    suffix: str | None = None
    alias: str | None = None
    post_nominals: str | None = None

    @classmethod
    def parse(cls, namestr: str) -> Name | None:
        """
        Deterministic parser for personal names.

        Args:
            namestr: The name string to parse.
        Returns:
            The parsed Name object or None if namestr is unparseable.
        """

        # Normalise periods so tokenisation on spaces works consistently.
        namestr = sub(_PERIOD_NOSPACE, ". ", namestr).strip()
        if not namestr:
            return None

        # Extract nickname (in quotes).
        nickname_match = _NICKNAME.search(namestr)
        nickname = nickname_match.group(2) if nickname_match else None
        if nickname_match:
            namestr = namestr.replace(nickname_match.group(0), "").strip()

        # Extract alias (in parentheses).
        alias_match = _ALIAS.search(namestr)
        alias = alias_match.group(1) if alias_match else None
        if alias_match:
            namestr = namestr.replace(alias_match.group(0), "").strip()

        # Split tokens.
        tokens = _split(namestr)
        if not tokens:
            return None

        # Check for comma after last name.
        try:
            comma_pos = namestr.index(",")
        except ValueError:
            comma_pos = -1

        title_tokens: list[str] = []
        first_tokens: list[str] = []
        prefix_tokens: list[str] = []
        last_tokens: list[str] = []
        suffix_tokens: list[str] = []
        post_tokens: list[str] = []

        # ------------------------------------------------------------
        # CASE 1 — Comma present: LAST, FIRST
        # ------------------------------------------------------------
        if comma_pos != -1:
            # Possible name formats:
            # - prefix? lastName, title? firstName+ nickname? suffix? postNominals?
            # - prefix? lastName suffix?, title? firstName+ nickname? postNominals?

            left = _split(namestr[:comma_pos])
            right = _split(namestr[comma_pos + 1 :])

            # LEFT: prefix? last_name suffix?
            i = len(left) - 1
            while i >= 0 and _match(left[i], _SUFFIX, suffix_tokens, True):
                i -= 1
            if i < 0:
                return None

            last_tokens.insert(0, left[i])
            i -= 1

            while i >= 0 and _match(left[i], _PREFIX, prefix_tokens, True):
                i -= 1

            # RIGHT: title? first_names suffix? postnominals?
            j = len(right) - 1
            while j >= 0 and _match(right[j], _POST_NOMINAL, post_tokens, True):
                j -= 1

            while j >= 0 and _match(right[j], _SUFFIX, suffix_tokens, True):
                j -= 1

            k = 0
            while k <= j and _match(right[k], _TITLE, title_tokens, False):
                k += 1

            while k <= j:
                first_tokens.append(right[k])
                k += 1

        # ------------------------------------------------------------
        # CASE 2 — No comma: FIRST ... LAST
        # ------------------------------------------------------------
        else:
            # Step 1: strip post-nominals and suffixes from end.
            i = len(tokens) - 1
            while i >= 0 and _match(tokens[i], _POST_NOMINAL, post_tokens, True):
                i -= 1

            while i >= 0 and _match(tokens[i], _SUFFIX, suffix_tokens, True):
                i -= 1

            if i < 0:
                return None

            # Step 2: last_name = last remaining token.
            last_tokens.append(tokens[i])
            i -= 1

            # Step 3: prefixes immediately before last_name.
            while i >= 0 and _match(tokens[i], _PREFIX, prefix_tokens, True):
                i -= 1

            # Step 4: titles at start.
            k = 0
            while k <= i and _match(tokens[k], _TITLE, title_tokens, False):
                k += 1

            # Step 5: everything between title and prefix = first_names.
            while k <= i:
                first_tokens.append(tokens[k])
                k += 1

            # --------------------------------------------------------
            # SPECIAL CASE: UPPERCASE last_name + title + initials
            # e.g. "LAAT Dr. ATJ"
            # --------------------------------------------------------
            if (
                len(tokens) >= 3
                and tokens[0].isupper()
                and _LAST_NAME.match(tokens[0])
                and _TITLE.match(tokens[1])
                and _INITIALS.match(tokens[-1])
            ):
                last_tokens = [_to_title_case(tokens[0])]
                title_tokens = [tokens[1]]
                # fn_tokens = tokens[2:]
                # first_tokens = fn_tokens
                first_tokens = tokens[2:]

        # ------------------------------------------------------------
        # Build Name object
        # ------------------------------------------------------------
        name = Name()
        name.title = " ".join(_to_title_case(t) for t in title_tokens) or None
        name.first_names = (
            " ".join(_to_title_case(_to_initials(t)) for t in first_tokens)
            if first_tokens
            else None
        )
        name.nickname = _to_title_case(nickname)
        name.prefix = " ".join(prefix_tokens).lower() or None
        name.last_name = (
            " ".join(_to_title_case(t) for t in last_tokens) if last_tokens else None
        )
        name.suffix = _to_title_case(" ".join(suffix_tokens)) or None
        name.alias = _to_title_case(alias)
        name.post_nominals = " ".join(post_tokens).upper().replace(".", "") or None

        if not name.last_name:
            return None

        # Debug specific failing cases
        # if namestr.find("Onça") != -1:
        #     print("DEBUG namestr:", namestr)
        #     print("  title      :", name.title)
        #     print("  first_names:", name.first_names)
        #     print("  prefix     :", name.prefix)
        #     print("  last_name  :", name.last_name)
        #     print("  suffix     :", name.suffix)
        #     print("  alias     :", name.alias)
        #     print("  post_nom   :", name.post_nominals)

        return name

    def __str__(self) -> str:
        result: list[str] = []
        _append(result, self.title)
        _append(result, self.first_names)
        _append(result, self.nickname, "'", "'")
        _append(result, self.prefix)
        _append(result, self.last_name)
        _append(result, self.suffix)
        _append(result, self.alias, "(", ")")
        _append(result, self.post_nominals)
        return "".join(result)

    def __hash__(self):
        prime = 31
        result = 1
        result = prime * result + (0 if self.title is None else hash(self.title))
        result = prime * result + (
            0 if self.first_names is None else hash(self.first_names)
        )
        result = prime * result + (0 if self.nickname is None else hash(self.nickname))
        result = prime * result + (0 if self.prefix is None else hash(self.prefix))
        result = prime * result + (
            0 if self.last_name is None else hash(self.last_name)
        )
        result = prime * result + (0 if self.suffix is None else hash(self.suffix))
        result = prime * result + (0 if self.alias is None else hash(self.alias))
        result = prime * result + (
            0 if self.post_nominals is None else hash(self.post_nominals)
        )
        return result

    def __eq__(self, obj):
        if self is obj:
            return True
        if obj is None:
            return False
        if self.__class__ is not obj.__class__:
            return False
        other: Name = obj
        if self.title is None:
            if other.title is not None:
                return False
        elif self.title != other.title:
            return False
        if self.first_names is None:
            if other.first_names is not None:
                return False
        elif self.first_names != other.first_names:
            return False
        if self.nickname is None:
            if other.nickname is not None:
                return False
        elif self.nickname != other.nickname:
            return False
        if self.prefix is None:
            if other.prefix is not None:
                return False
        elif self.prefix != other.prefix:
            return False
        if self.last_name is None:
            if other.last_name is not None:
                return False
        elif self.last_name != other.last_name:
            return False
        if self.suffix is None:
            if other.suffix is not None:
                return False
        elif self.suffix != other.suffix:
            return False
        if self.alias is None:
            if other.alias is not None:
                return False
        elif self.alias != other.alias:
            return False
        if self.post_nominals is None:
            if other.post_nominals is not None:
                return False
        elif self.post_nominals != other.post_nominals:
            return False
        return True

    def initials(self, with_period: bool = True, with_space: bool = True) -> str | None:
        if self.first_names is None:
            return None

        result: list[str] = []
        tok = StrTokenizer(self.first_names, NAME_SEP)
        while tok.hasMoreTokens():
            initial = tok.nextToken()[0].upper()
            result.append(initial)
            if with_period:
                result.append(".")
            if with_space and tok.hasMoreTokens():
                result.append(" ")
        return "".join(result)

    def format(self, fmt: str) -> str:
        """
        Returns a string representation of the object, using a custom format.
        The format string consists of a sequence of field specifiers, each of
        which consiists of a % character followed by a field type character.

        Field Specifiers
        ----------------
        %t : The title field
        %f : The firstNames field
        %n : The nickame field, wrapped in single quotes
        %p : The prefix field
        %l : The last_name field
        %s : The suffix field
        %a : The alias field, wrapped in parentheses
        %z : The post_nominals field
        %i : The firstNames field represented as initials, each with a
            trailing period and space
        %I : The firstNames field represented as initials, each with neither
            trailing period nor space
        %J : The firstNames field represented as initials, each with a
            trailing space
        %K : The firstNames field represented as initials, each with a
            trailing period
        %% : A literal percent character

        Each of these field values is automatically space-separated from the
        preceding value, so there is no need to include spaces between field
        specifiers. Other non-field-specifier characters in the format string
        are emitted verbatim.

        Args:
            fmt: The format to use.
        Returns:
            The name as a string in the specified format.
        """

        result: list[str] = []
        seen_percent = False
        i = 0
        while i < len(fmt):
            c = fmt[i]
            if c == "%":
                seen_percent = True
            elif seen_percent:
                match c:
                    case "t":
                        _append(result, self.title)
                    case "f":
                        _append(result, self.first_names)
                    case "n":
                        _append(result, self.nickname, "'", "'")
                    case "p":
                        _append(result, self.prefix)
                    case "l":
                        _append(result, self.last_name)
                    case "s":
                        _append(result, self.suffix)
                    case "a":
                        _append(result, self.alias, "(", ")")
                    case "z":
                        _append(result, self.post_nominals)
                    case "i":
                        _append(result, self.initials())
                    case "I":
                        _append(result, self.initials(False, False))
                    case "J":
                        _append(result, self.initials(False, True))
                    case "K":
                        _append(result, self.initials(True, False))
                    case "%":
                        result.append(c)
                    case _:
                        raise SyntaxError(f"%{c} is not a legal field specifier")
                seen_percent = False
            else:
                result.append(c)
            i += 1

        return "".join(result)

    def matches(self, other: Name) -> bool:
        """
        Tests for equality based on last_name and either first_names or initials, plus the other fields.

        Args:
            other: The other name to compare with self.
        Returns:
            True if other matches on last_name and either first_names or initials.
        """
        return self.match_score(other) >= MATCH_THRESHOLD

    def match_score(self, other: Name) -> float:
        """
        Computes the strength of the match between this name and another.

        Args:
            other: The other name to compare with self.
        Returns:
            A float between 0.0 and 1.0 indicating the strength of the match.
        """

        score = 0.0
        if self.last_name == other.last_name:
            score += LAST_NAME_SCORE
        if self.first_names == other.first_names:
            score += FIRST_NAMES_SCORE
        else:
            # First names don't match exactly, so check for a partial match on name and/or initials.
            s_fn_tok = StrTokenizer(
                self.first_names if self.first_names else "", NAME_SEP
            )
            o_fn_tok = StrTokenizer(
                other.first_names if other.first_names else "", NAME_SEP
            )

            fn_count_to_match = min(s_fn_tok.countTokens(), o_fn_tok.countTokens())
            for _ in range(fn_count_to_match):
                s_fn = s_fn_tok.nextToken()
                o_fn = o_fn_tok.nextToken()

                # if names match exactly, so far so good.
                if s_fn == o_fn:
                    continue

                # If the initials don't match or neither name is an initial, there is no match.
                if s_fn[0] != o_fn[0] or not (len(s_fn) == 1 or len(o_fn) == 1):
                    break
            else:
                score += INITIALS_SCORE
        # TODO: Consider whether non-None mismatches should reset the score.
        if self.title is not None and self.title == other.title:
            score += TITLE_SCORE
        if self.nickname is not None and self.nickname == other.nickname:
            score += NICKNAME_SCORE
        if self.prefix is not None and self.prefix == other.prefix:
            score += PREFIX_SCORE
        if self.suffix is not None and self.suffix == other.suffix:
            score += SUFFIX_SCORE
        if self.alias is not None and self.alias == other.alias:
            score += ALIAS_SCORE
        if self.post_nominals is not None and self.post_nominals == other.post_nominals:
            score += POST_NOMINALS_SCORE

        return score
