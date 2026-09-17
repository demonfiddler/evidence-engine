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


def indent_lines(text: str, *, num_spaces = 4, indent_first = True) -> str:
    """
    Indent each line of the given text by the specified number of spaces.

    Args:
        text: the text whose lines are to be indented.
        num_spaces: the number of spaces by which to indent.
        indent_first: whether to indent the first line.

    Returns:
        text, with lines indented by the specified number of space characters.
    """
    indentation = " " * num_spaces
    return prefix_lines(text, prefix = indentation, prefix_first = indent_first)


def prefix_lines(text: str, *, prefix: str, prefix_first = True) -> str:
    """
    Indent each line of the given text by the specified number of spaces.

    Args:
        text: the text whose lines are to be prefixed.
        prefix: the prefix to prepend to each line.
        prefix_first: whether top prefix the first line.

    Returns:
        text, with lines prefixed by the specified prefix.
    """
    return "\n".join(prefix + line if i > 0 or prefix_first else line for i, line in enumerate(text.splitlines()))


def jaccard(a: list[str], b: list[str]) -> float:
    """Token-set Jaccard similarity."""
    if not a or not b:
        return 0.0
    sa, sb = set(a), set(b)
    return len(sa & sb) / len(sa | sb)


CHARS_PER_TOKEN = 6.54

def truncate(text: str,
             *,
             token_count: int | None = None,
             length: int | None = None,
             split: float = 1.0,
             use_ellipsis: bool = False) -> str:
    """Truncate a string to a given length or token count.
    Optionally split between a leading section from the start of the text and a trailing section from the end of the text.

    Args:
        text: the string to truncate.
        token_count: the approximate number of tokens to include.
        length: the exact length of the truncated text to return.
        split: the fraction of leading text to include, in the range 0.0 to 1.0, default is 1.0.
        use_ellipsis: whether to include an ellipsis at the elision point.
    Returns:
        The truncated string or the original text if it is None or not longer than the requested length.
    """
    if text is None:
        return text
    use_tc = isinstance(token_count, int)
    use_len = isinstance(length, int)
    if not (use_tc ^ use_len):
        raise Exception("Exactly ONE of token_count OR length must be specified, as an int.")
    if use_tc and token_count < 0:
        raise Exception("token_count must be a non-negative integer.")
    elif use_len and length < 0:
        raise Exception("length must be a non-negative integer.")
    if use_tc:
        length = int(token_count * CHARS_PER_TOKEN)
    text_len = len(text)
    if text_len <= length:
        return text
    if use_ellipsis:
        length -= 3
        ellipsis = "\n…\n"
    else:
        ellipsis = ""
    text_len = len(text)
    leading_len = int(length * split)
    trailing_len = length - leading_len
    leading = text[:leading_len]
    trailing = text[-trailing_len:]
    result = leading + ellipsis + trailing
    return result
