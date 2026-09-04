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

from pydantic import BaseModel
from regex import compile, Pattern, split, sub
from StrTokenizer import StrTokenizer

_TITLE = compile(
    "^Ambassador|Baroness|Baronet|Bart\\.?|Bt\\.?|Captain|Capt?\\.?|Cllr\\.?|Col\\.?|Commander|Cdr\\.?|Dame|Dr\\.?|Fr\\.?|Gen\\.?|King|Lady|Lord|Lt\\.?|Lieutenant|Lt\\.?|Major|Maj\\.?|Miss|Mr\\.?|Mrs\\.?|Ms\\.?|nat\\.?|Prince|Princess|Prof\\.?|Queen|Rabbi|rer\\.?|\\(?[Rr]et\\.?\\)?|Rev\\.?|Sen\\.?|Sergeant|Sgt\\?|Sir$")
_FIRST_NAME = compile("^[\\p{IsAlphabetic}()'.-]+$")
_NICKNAME = compile("^[(\"']([\\p{IsAlphabetic}]+)[)\"']$")
_PREFIX = compile("^[dD]e[nlr]?|[dD]u|[lL][ae]|[vV][ao]n|[zZ]u|St\\.?|[tT]e$")
_LAST_NAME = compile("^[\\p{IsAlphabetic}'’.-]+$")
_SUFFIX = compile("^I{1,3}|IV|VI{0,3}|I?X|Jn?r\\.?|Sn?r\\.?$")
_POST_NOMINAL = compile("^[A-Z]{2,}|[B|M]\\.?A\\.?|[B|M|D]\\.?Sc\\.?|Ph\\.?D\\.?|D\\.?Phil\\.?$")
_PERIOD_NOSPACE = compile("\\.(?=[^ ])")
_SPACES = compile(" +")

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
        match = match.group(1 if match.groupCount() == 1 else 0)
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

class Name(BaseModel):
    """Represents a person's name."""

    @classmethod
    def parse(cls, namestr: str) -> "Name":
        """
        Parses a string into a Name object.
        Args:
            namestr: The name string to parse.
        Returns:
            A corresponding object or None if parsing failed.
        """

        # Make sure all periods are followed by a space, to facilitate tokenisation on spaces without using the period
        # as a separator character.
        namestr = sub(_PERIOD_NOSPACE, ". ", namestr)
        try:
            comma_pos = namestr.index(",")
        except ValueError:
            comma_pos = -1
        last_name_first = comma_pos != -1

        # TODO: handle the LASTNAME INITIALS case if there is no comma
        # TODO: handle the LASTNAME INITIALS case if initials are not space- or period-delimited
        # TODO: handle nicknames: 'Nickname' or (Nickname)
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
        name.title = _join(title)
        name.first_names = _join(first_names)
        name.nickname = _join(nickname)
        name.prefix = _join(prefix)
        name.last_name = _join(last_name)
        name.suffix = _join(suffix)
        name.post_nominals = _join(post_nominals)

        return name

    def __init__(self, *, title: str | None, first_names: str | None, nickname: str | None, prefix: str | None, last_name: str | None, suffix: str | None, post_nominals: str | None):
        self.title = title
        self.first_names = first_names
        self.nickname = nickname
        self.prefix = prefix
        self.last_name = last_name
        self.suffix = suffix
        self.post_nominals = post_nominals

    def get_initials(self, with_period: bool = True, with_space: bool = True) -> str | None:
        if self.first_names is None:
            return None

        result: list[str] = []
        tok = StrTokenizer(self.first_names, " .")
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

    # @Override
    def to_string(self) -> str:
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

    def format(self, fmt: str) -> str:
        """
        Returns a string representation of the object, using a custom format. The format string consists of a sequence of
        field specifiers, each of which starts with a % character followed by a field type character. Valid
        field types are as follows:
        t: The title field
        f: The firstNames field
        n: The nickame field
        p: The prefix field
        l: The lastName field
        s: The suffix field
        z: The postNominals field
        i: The firstNames field represented as initials, each with a trailing period and space
        I: The firstNames field represented as initials, each with neither trailing period nor space
        J: The firstNames field represented as initials, each with a trailing space
        K: The firstNames field represented as initials, each with a trailing period
        Each of these field values is automatically space-separated from the preceding value, so there is no need to
        include spaces between field specifiers. Other non-field-specifier characters in the format string are emitted
        verbatim.

        Args:
            fmt The format to use.
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
                        _append(result, self.self.title)
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
                    case _:
                        raise SyntaxError(f"%{c} is not a legal format specifier")
                seen_percent = False
            else:
                result.append(c)
            i += 1

        return "".join(result)

    def matches(self, other: "Name") -> bool:
        """
        Tests for equality based on last_name and either first_names or initials.

        Args:
            other: The other name to compare with self.
        Returns:
            True if other matches on last_name and either first_names or initials.
        """
        return self.last_name == other.last_name and (self.first_names == other.first_names or self.get_initials() == other.get_initials())
