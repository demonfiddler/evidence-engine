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
from regex import IGNORECASE, compile, Pattern, split, sub
from StrTokenizer import StrTokenizer

# TODO: support Name.alias
_TITLE = compile(
    "^Ambassador|Baroness|Baronet|Bart\\.?|Bt\\.?|Captain|Capt?\\.?|Cllr\\.?|Col\\.?|Commander|Cdr\\.?|Dame|Dr\\.?|Fr\\.?|Gen\\.?|King|Lady|Lord|Lt\\.?|Lieutenant|Lt\\.?|Major|Maj\\.?|Miss|Mrs?\\.?|Ms\\.?|nat\\.?|Prince|Princess|Prof\\.?|Queen|Rabbi|rer\\.?|\\(?[Rr]et\\.?\\)?|Rev\\.?|Sen\\.?|Sergeant|Sgt\\?|Sir$", IGNORECASE)
_FIRST_NAME = compile("^[\\p{IsAlphabetic}()'.-]+$")
_NICKNAME = compile("^[(\"']([\\p{IsAlphabetic}]+)[)\"']$")
_PREFIX = compile("^[dD]e[nlr]?|[dD]u|[lL][ae]|[vV][ao]n|[zZ]u|St\\.?|[tT]e$")
_LAST_NAME = compile("^[\\p{IsAlphabetic}'’.-]+$")
_SUFFIX = compile("^I{1,3}|IV|VI{0,3}|I?X|Jn?r\\.?|Sn?r\\.?$")
_ALIAS = compile("^\(([\\p{IsAlphabetic}\\s]+)\)$")
_POST_NOMINAL = compile("^[A-Z]{2,}|[B|M]\\.?A\\.?|[B|M|D]\\.?Sc\\.?|Ph\\.?D\\.?|D\\.?Phil\\.?$")
_PERIOD_NOSPACE = compile("\\.(?=[^ ])")
_SPACES = compile(" +")

# These scores must sum to unity, the highest possible match score if all fields match exactly.
# Thus, a minimum match score of 0.8 ensures a match on at least last name and initials.
# N.B. FIRST_NAMES_SCORE and INITIALS_SCORE are mutually exclusive.
LAST_NAME_SCORE     = 0.6
FIRST_NAMES_SCORE   = 0.25
INITIALS_SCORE      = 0.2
TITLE_SCORE         = 0.025
NICKNAME_SCORE      = 0.025
PREFIX_SCORE        = 0.025
SUFFIX_SCORE        = 0.025
ALIAS_SCORE         = 0.025
POST_NOMINALS_SCORE = 0.025

MATCH_THRESHOLD = LAST_NAME_SCORE + INITIALS_SCORE
NAME_SEP = " ."
_MAX_INITIALS = 4

def _parse(token : str, pattern: Pattern, result: list[str], prepend: bool) -> bool:
    """
    Attempts to match a token against a pattern. If successful, inserts the token into a buffer with, if necessary,
    a separator space character.

    Args:
        token: The token to test.
        pattern: The pattern to match.
        result: The result buffer.
        prepend: True to prepend token to result, False to append it.

    Returns:
        True if token matched pattern, otherwise False.
    """
    match = pattern.match(token)
    if match:
        match = match.group(1 if len(match.groups()) == 1 else 0)
        if prepend:
            if len(result) != 0 and result[0] != ' ':
                result.insert(0, ' ')
            result.insert(0, match)
        else:
            if len(result) != 0 and result[-1] != ' ':
                result.append(' ')
            result.append(match)
        return True
    return False

def _append(sb: list[str], s: str | None, open_delim: str = None, close_delim: str = None):
    """
    Appends a string to a buffer, prepending a space if the buffer is not empty.

    Args:
        sb: The buffer.
        s: The string to append.
        openDelim: The character to prepend, if any.
        closeDelim: The character to append, if any.
    """
    if s is not None:
        if len(sb) != 0 and sb[-1] != ' ':
            sb.append(' ')
        if open_delim != None:
            sb.append(open_delim)
        sb.append(s)
        if close_delim != None:
            sb.append(close_delim)

def _join(sb: list[str]) -> str | None:
    """
    Converts a string buffer to a string.

    Args:
        sb: The buffer.
    Returns:
        None if sb is empty, otherwise sb joined as a single string.
    """
    return None if len(sb) == 0 else "".join(sb)

def _to_initials(name: str) -> list[str]:
    """Converts a name to space-separated initials if all characters are uppercase"""
    if len(name) <= _MAX_INITIALS and name.isupper():
        name = " ".join(name)
    return name

def _first_to_upper(str: str) -> str:
    if len(str) == 0 or str[0].isupper():
        return str
    return str[0].upper() + str[1:]

class Name(BaseModel):
    """Represents a person's name."""

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
        Parses a string into a Name object.
        Args:
            namestr: The name string to parse.
        Returns:
            A corresponding object or None if parsing failed.
        """

        # Make sure all periods are followed by a space, to facilitate tokenisation on spaces without using the period
        # as a separator character. [FIXME] this fails for names of the form '<Lastname> <INITIALS>'
        namestr = sub(_PERIOD_NOSPACE, ". ", namestr)
        try:
            comma_pos = namestr.index(",")
        except ValueError:
            comma_pos = -1
        last_name_first = comma_pos != -1

        # TODO: handle the Lastname INITIALS case if there is no comma
        # TODO: handle the Lastname INITIALS case if initials are not space- or period-delimited
        # TODO: handle nickname: 'Nickname'
        # TODO: handle alias: (Fred Bloggs)
        # TODO: handle post-nominal letters (all uppercase or degree abbreviation)

        title = []
        first_names = []
        nickname = []
        prefix = []
        last_name = []
        suffix = []
        post_nominals = []

        if last_name_first:
            # Possible name formats:
            # - prefix? lastName, title? firstName+ nickname? suffix? postNominals?
            # - prefix? lastName suffix?, title? firstName+ nickname? postNominals?

            # chunk1: prefix? lastName suffix?
            chunk1 = namestr[0 : comma_pos]
            tokens = split(_SPACES, chunk1)

            # Parse any suffixes first, so that they don't get consumed by lastName.
            i = len(tokens) - 1
            while i > 0 and _parse(tokens[i], _SUFFIX, suffix, True):
                i -= 1

            max = i
            i = 0
            while i <= max:
                if _parse(tokens[i], _PREFIX, prefix, False):
                    i += 1
                elif _parse(tokens[i], _LAST_NAME, last_name, False):
                    i += 1
                else:
                    break

            # chunk2: title? firstName+ nickname? suffix? postNominals?
            chunk2 = namestr[comma_pos + 1 :].strip() if comma_pos < len(namestr) - 2 else ""
            tokens = split(_SPACES, chunk2)

            i = len(tokens) - 1
            while i > 0 and _parse(tokens[i], _POST_NOMINAL, post_nominals, True):
                i -= 1

            while i > 0 and _parse(tokens[i], _SUFFIX, suffix, True):
                i -= 1

            while i > 0 and _parse(tokens[i], _NICKNAME, nickname, True):
                i -= 1

            max = i;
            i = 0;
            while i < max and _parse(tokens[i], _TITLE, title, False):
                i += 1

            while i <= max and _parse(tokens[i], _FIRST_NAME, first_names, False):
                i += 1
        else:
            # - title? firstName+ nickname? prefix? lastName suffix? postNominals?
            tokens = split(_SPACES, namestr)

            # Parse any suffixes first, so that they don't get consumed by lastName.
            i = len(tokens) - 1
            while i > 0 and _parse(tokens[i], _POST_NOMINAL, post_nominals, True):
                i -= 1

            while i > 0 and _parse(tokens[i], _SUFFIX, suffix, True):
                i -= 1

            max = i
            i = 0
            while i < max and _parse(tokens[i], _TITLE, title, False):
                i += 1

            while i < max:
                if _parse(tokens[i], _PREFIX, prefix, False):
                    i += 1
                elif _parse(tokens[i], _NICKNAME, nickname, False):
                    i += 1
                elif _parse(tokens[i], _FIRST_NAME, first_names, False):
                    i += 1
                else:
                    break

            if i > max or not _parse(tokens[max], _LAST_NAME, last_name, False):
                return None

        name = Name()
        name.title = _join([_first_to_upper(t) for t in title])
        name.first_names = _join([_first_to_upper(_to_initials(fn)) for fn in first_names])
        name.nickname = _join([_first_to_upper(n) for n in nickname])
        name.prefix = _join(prefix)
        name.last_name = _join([_first_to_upper(ln) for ln in last_name])
        name.suffix = _join(suffix)
        name.post_nominals = _join(post_nominals)

        return name

    def __str__(self) -> str:
        """
        Returns a string representation of the object. The result is equivalent to calling format("%t%f%n%p%l%s%z").
        Returns:
            The string representation.
        """
        result: list[str] = []
        _append(result, self.title)
        _append(result, self.first_names)
        _append(result, self.nickname, '\'', '\'')
        _append(result, self.prefix)
        _append(result, self.last_name)
        _append(result, self.suffix)
        _append(result, self.post_nominals)
        return "".join(result)

    def get_initials(self, with_period: bool = True, with_space: bool = True) -> str | None:
        if self.first_names is None:
            return None

        result: list[str] = []
        tok = StrTokenizer(self.first_names, NAME_SEP)
        while tok.hasMoreTokens():
            initial = tok.nextToken()[0].upper()
            result.append(initial)
            if with_period:
                result.append('.')
            if with_space and tok.hasMoreTokens():
                result.append(' ')
        return "".join(result)

    # @Override
    # public int hashCode() {
    #     final int prime = 31;
    #     int result = 1;
    #     result = prime * result + ((title == null) ? 0 : title.hashCode());
    #     result = prime * result + ((firstNames == null) ? 0 : firstNames.hashCode());
    #     result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
    #     result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
    #     result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    #     result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
    #     result = prime * result + ((postNominals == null) ? 0 : postNominals.hashCode());
    #     return result;
    # }

    # @Override
    # public boolean equals(Object obj) {
    #     if (this == obj)
    #         return true;
    #     if (obj == null)
    #         return false;
    #     if (getClass() != obj.getClass())
    #         return false;
    #     Name other = (Name)obj;
    #     if (title == null) {
    #         if (other.title != null)
    #             return false;
    #     } else if (!title.equals(other.title))
    #         return false;
    #     if (firstNames == null) {
    #         if (other.firstNames != null)
    #             return false;
    #     } else if (!firstNames.equals(other.firstNames))
    #         return false;
    #     if (nickname == null) {
    #         if (other.nickname != null)
    #             return false;
    #     } else if (!nickname.equals(other.nickname))
    #         return false;
    #     if (prefix == null) {
    #         if (other.prefix != null)
    #             return false;
    #     } else if (!prefix.equals(other.prefix))
    #         return false;
    #     if (lastName == null) {
    #         if (other.lastName != null)
    #             return false;
    #     } else if (!lastName.equals(other.lastName))
    #         return false;
    #     if (suffix == null) {
    #         if (other.suffix != null)
    #             return false;
    #     } else if (!suffix.equals(other.suffix))
    #         return false;
    #     if (postNominals == null) {
    #         if (other.postNominals != null)
    #             return false;
    #     } else if (!postNominals.equals(other.postNominals))
    #         return false;
    #     return true;
    # }

    def format(self, fmt: str) -> str:
        """
        Returns a string representation of the object, using a custom format. The format string consists of a sequence of
        field specifiers, each of which starts with a % character followed by a field type character.

        Field Specifiers
        ----------------
        t : The title field
        f : The firstNames field
        n : The nickame field, wrapped in single quotes
        p : The prefix field
        l : The lastName field
        s : The suffix field
        a : The alias field, wrapped in parentheses
        z : The postNominals field
        i : The firstNames field represented as initials, each with a trailing period and space
        I : The firstNames field represented as initials, each with neither trailing period nor space
        J : The firstNames field represented as initials, each with a trailing space
        K : The firstNames field represented as initials, each with a trailing period
        Each of these field values is automatically space-separated from the preceding value, so there is no need to
        include spaces between field specifiers. Other non-field-specifier characters in the format string are emitted
        verbatim.

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
            if c == '%':
                seen_percent = True
            elif seen_percent:
                match c:
                    case 't':
                        _append(result, self.title)
                    case 'f':
                        _append(result, self.first_names)
                    case 'n':
                        _append(result, self.nickname, "'", "'")
                    case 'p':
                        _append(result, self.prefix)
                    case 'l':
                        _append(result, self.last_name)
                    case 's':
                        _append(result, self.suffix)
                    case 'a':
                        _append(result, self.alias, "(", ")")
                    case 'z':
                        _append(result, self.post_nominals)
                    case 'i':
                        _append(result, self.get_initials())
                    case 'I':
                        _append(result, self.get_initials(False, False))
                    case 'J':
                        _append(result, self.get_initials(False, True))
                    case 'K':
                        _append(result, self.get_initials(True, False))
                    case '%':
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
            s_fn_tok = StrTokenizer(self.first_names if self.first_names else "", NAME_SEP)
            o_fn_tok = StrTokenizer(other.first_names if other.first_names else "", NAME_SEP)

            fn_count_to_match = min(s_fn_tok.countTokens(), o_fn_tok.countTokens())
            for _ in range(fn_count_to_match):
                s_fn = s_fn_tok.nextToken()
                o_fn = o_fn_tok.nextToken()

                # if names match exactly, so far so good.
                if s_fn == o_fn:
                    continue

                # If the initials don't match or neither name is an initial, there is no match.
                if s_fn[0] != o_fn[0] or not(len(s_fn) == 1 or len(o_fn) == 1):
                    break
            else:
                score += INITIALS_SCORE
        # TODO: Consider whether non-None mismatches should reset the score.
        if (self.title is not None and self.title == other.title):
            score += TITLE_SCORE
        if (self.nickname is not None and self.nickname == other.nickname):
            score += NICKNAME_SCORE
        if (self.prefix is not None and self.prefix == other.prefix):
            score += PREFIX_SCORE
        if (self.suffix is not None and self.suffix == other.suffix):
            score += SUFFIX_SCORE
        if (self.alias is not None and self.alias == other.alias):
            score += ALIAS_SCORE
        if (self.post_nominals is not None and self.post_nominals == other.post_nominals):
            score += POST_NOMINALS_SCORE

        return score
