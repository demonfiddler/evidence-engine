/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

package io.github.demonfiddler.ee.server.model;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Represents a person's name.
 */
public class Name {

    private static final Pattern TITLE = Pattern.compile(
        "^Ambassador|Baroness|Baronet|Captain|Capt\\.?|Cllr\\.?|Col\\.?|Dame|Dr\\.?|Fr\\.?|Gen\\.?|King|Lady|Lord|Lt\\.?|Lt\\.?|Maj\\.?|Miss|Mr\\.?|Mrs\\.?|Ms\\.?|nat\\.?|Prince|Princess|Prof\\.?|Queen|Rabbi|rer\\.?|\\(?[Rr]et\\.?\\)?|Rev\\.?|Sen\\.?|Sir$");
    private static final Pattern FIRST_NAME = Pattern.compile("^[\\p{IsAlphabetic}()'.-]+$");
    private static final Pattern PREFIX = Pattern.compile("^[dD]e[nlr]?|[dD]u|[lL][ae]|[vV][ao]n|[zZ]u|St\\.?$");
    private static final Pattern LAST_NAME = Pattern.compile("^[\\p{IsAlphabetic}'’.-]+$");
    private static final Pattern SUFFIX = Pattern.compile("^I{1,3}|IV|VI{0,3}|I?X|Jn?r\\.?|Sn?r\\.?$");

    /**
     * Parses a string into a {@code Name} object.
     * @param namestr The name string to parse.
     * @return A corresponding {@code Name} object or {@code null} if parsing failed.
     */
    public static Name parse(String namestr) {
        // Make sure all periods are followed by a space, to facilitate tokenisation on spaces without using the period
        // as a separator character.
        namestr = namestr.replaceAll("\\.(?=[^ ])", ". ");
        int commapos = namestr.indexOf(",");
        // TODO: handle the LASTNAME INITIALS case if there is no comma
        // TODO: handle the LASTNAME INITIALS case if initials are not space- or period-delimited
        // TODO: handle nicknames: 'Nickname' or (Nickname)
        boolean lastNameFirst = commapos != -1;

        StringBuilder title = new StringBuilder();
        StringBuilder firstNames = new StringBuilder();
        StringBuilder prefix = new StringBuilder();
        StringBuilder lastName = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        if (lastNameFirst) {
            // Possible name formats:
            // - prefix? lastName, title? firstName+ suffix?
            // - prefix? lastName suffix?, title? firstName+

            // chunk1: prefix? lastName suffix?
            String chunk1 = namestr.substring(0, commapos);
            String[] tokens = chunk1.split(" +");

            // Parse any suffixes first, so that they don't get consumed by lastName.
            int i = tokens.length - 1;
            while (i > 0 && parse(tokens[i], SUFFIX, suffix, true))
                i--;

            int max = i;
            i = 0;
            while (i <= max) {
                if (parse(tokens[i], PREFIX, prefix, false))
                    i++;
                else if (parse(tokens[i], LAST_NAME, lastName, false))
                    i++;
                else
                    break;
            }

            // chunk2: title? firstName+ suffix?
            String chunk2 = commapos < namestr.length() - 2 ? namestr.substring(commapos + 1).trim() : "";
            tokens = chunk2.split(" +");

            i = tokens.length - 1;
            while (i > 0 && parse(tokens[i], SUFFIX, suffix, true))
                i--;

            max = i;
            i = 0;
            while (i < max && parse(tokens[i], TITLE, title, false))
                i++;

            while (i <= max && parse(tokens[i], FIRST_NAME, firstNames, false))
                i++;
        } else {
            // - title? firstName+ prefix? lastName suffix?
            String[] tokens = namestr.split(" +");

            // Parse any suffixes first, so that they don't get consumed by lastName.
            int i = tokens.length - 1;
            while (i > 0 && parse(tokens[i], SUFFIX, suffix, true))
                i--;

            int max = i;
            i = 0;
            while (i < max && parse(tokens[i], TITLE, title, false))
                i++;

            while (i < max) {
                if (parse(tokens[i], PREFIX, prefix, false))
                    i++;
                else if (parse(tokens[i], FIRST_NAME, firstNames, false))
                    i++;
                else
                    break;
            }

            if (i > max || !parse(tokens[max], LAST_NAME, lastName, false))
                return null;
        }

        Name name = new Name();
        name.title = toString(title);
        name.firstNames = toString(firstNames);
        name.prefix = toString(prefix);
        name.lastName = toString(lastName);
        name.suffix = toString(suffix);

        return name;
    }

    /**
     * Attempts to match a token against a pattern. If successful, inserts the token into a buffer with, if necessary,
     * a separator space character.
     * @param token The token to test.
     * @param pattern The pattern to match.
     * @param result The result buffer.
     * @param prepend {@code true} to prepend {@code token} to {@code result}, {@code false} to append it.
     * @return {@code true} if {@code token} matched {@code pattern}, otherwise {@code false}.
     */
    private static boolean parse(String token, Pattern pattern, StringBuilder result, boolean prepend) {
        if (pattern.matcher(token).matches()) {
            if (prepend) {
                if (!result.isEmpty() && result.charAt(0) != ' ')
                    result.insert(0, ' ');
                result.insert(0, token);
            } else {
                if (!result.isEmpty() && result.charAt(result.length() - 1) != ' ')
                    result.append(' ');
                result.append(token);
            }
            return true;
        }
        return false;
    }

    /**
     * Appends a string to a buffer, prepending a space if the buffer is not empty.
     * @param sb The buffer.
     * @param s The string to append.
     */
    private static void append(StringBuilder sb, String s) {
        if (s != null) {
            if (!sb.isEmpty() && sb.charAt(sb.length() - 1) != ' ')
                sb.append(' ');
            sb.append(s);
        }
    }

    /**
     * Converts a string buffer to a string.
     * @param sb The buffer.
     * @return {@code null} if {@code sb} is empty, otherwise {@code sb.toString()}.
     */
    private static final String toString(StringBuilder sb) {
        return sb.isEmpty() ? null : sb.toString();
    }

    private String title;
    private String firstNames;
    private String prefix;
    private String lastName;
    private String suffix;

    public Name() {
    }

    public Name(String firstNames, String lastName) {
        this.firstNames = firstNames;
        this.lastName = lastName;
    }

    public Name(String title, String firstNames, String prefix, String lastName, String suffix) {
        this.title = title;
        this.firstNames = firstNames;
        this.prefix = prefix;
        this.lastName = lastName;
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstName) {
        this.firstNames = firstName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getInitials() {
        return getInitials(true, true);
    }

    public String getInitials(boolean withPeriod, boolean withSpace) {
        if (firstNames == null)
            return null;

        StringBuilder result = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(firstNames, " .");
        while (tok.hasMoreTokens()) {
            char initial = Character.toUpperCase(tok.nextToken().charAt(0));
            result.append(initial);
            if (withPeriod)
                result.append('.');
            if (withSpace && tok.hasMoreTokens())
                result.append(' ');
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((firstNames == null) ? 0 : firstNames.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Name other = (Name)obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (firstNames == null) {
            if (other.firstNames != null)
                return false;
        } else if (!firstNames.equals(other.firstNames))
            return false;
        if (prefix == null) {
            if (other.prefix != null)
                return false;
        } else if (!prefix.equals(other.prefix))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (suffix == null) {
            if (other.suffix != null)
                return false;
        } else if (!suffix.equals(other.suffix))
            return false;
        return true;
    }

    /**
     * Returns a string representation of the object. The result is equivalent to calling {@code format("%t%f%p%l%s")}.
     * @return The string representation.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        append(result, title);
        append(result, firstNames);
        append(result, prefix);
        append(result, lastName);
        append(result, suffix);
        return result.toString();
    }

    /**
     * Returns a string representation of the object, using a custom format. The format string consists of a sequence of
     * field specifications, each of which starts with a {@code %} character followed by a field type character. Valid
     * field types are as follows:<br>
     * <b>{@code t}</b>: The {@code title} field<br>
     * <b>{@code f}</b>: The {@code firstNames} field<br>
     * <b>{@code p}</b>: The {@code prefix} field<br>
     * <b>{@code l}</b>: The {@code lastName} field<br>
     * <b>{@code s}</b>: The {@code suffix} field<br>
     * <b>{@code i}</b>: The {@code firstNames} field represented as initials, each with a trailing period and space<br>
     * <b>{@code I}</b>: The {@code firstNames} field represented as initials, each with neither trailing period nor
     * space<br>
     * <b>{@code J}</b>: The {@code firstNames} field represented as initials, each with a trailing space<br>
     * <b>{@code K}</b>: The {@code firstNames} field represented as initials, each with a trailing period<br>
     * Each of these field values is automatically space-separated from the preceding value, so there is no need to
     * include spaces between field specifications. Other non-field-specification characters in the format string are
     * emitted verbatim.
     * @param fmt The format to use.
     * @return The name as a string in the specified format.
     */
    public String format(String fmt) {
        StringBuilder result = new StringBuilder();

        boolean seenPercent = false;
        int i = 0;
        while (i < fmt.length()) {
            char c = fmt.charAt(i);
            if (c == '%') {
                seenPercent = true;
            } else if (seenPercent) {
                switch (c) {
                    case 't':
                        append(result, title);
                        break;
                    case 'f':
                        append(result, firstNames);
                        break;
                    case 'p':
                        append(result, prefix);
                        break;
                    case 'l':
                        append(result, lastName);
                        break;
                    case 's':
                        append(result, suffix);
                        break;
                    case 'i':
                        append(result, getInitials());
                        break;
                    case 'I':
                        append(result, getInitials(false, false));
                        break;
                    case 'J':
                        append(result, getInitials(false, true));
                        break;
                    case 'K':
                        append(result, getInitials(true, false));
                        break;
                    default:
                        throw new IllegalArgumentException("%" + c + " is not a legal format specification");
                }
                seenPercent = false;
            } else {
                result.append(c);
            }
            i++;
        }

        return result.toString();
    }

}
