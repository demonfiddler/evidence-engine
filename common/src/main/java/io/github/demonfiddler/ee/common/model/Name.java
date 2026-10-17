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

package io.github.demonfiddler.ee.common.model;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static io.github.demonfiddler.ee.common.util.StringUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a personal name.
 */
public class Name {

    private static final Pattern TITLE = Pattern.compile(
        "^(?:Ambassador|Amb\\.?|Baroness|Baronet|Bart\\.?|Bt\\.?|Brigadier|Brig\\.?|Captain|Capt?\\.?|Councillor|" +
        "Cllr\\.?|Colonel|Col\\.?|Commander|Cdr\\.?|Dame|Doctor|Dr\\.?|Fr\\.?|General|Gen\\.?|King|Lady|Lord|" +
        "Lieutenant|Lieut\\.?|Lt\\.?|Major|Maj\\.?|Miss|Mrs?\\.?|Ms\\.?|nat\\.?|Prince|Princess|Professor|Prof\\.?|" +
        "Queen|Rabbi|Rab\\.?|rer\\.?|\\(?[Rr]et\\.?\\)?|Reverend|Rev\\.?|Senator|Sen\\.?|Sergeant|Sgt\\?|Sir)$",
        CASE_INSENSITIVE);
    private static final Pattern INITIALS = Pattern.compile(
        "[\\p{javaUpperCase}](?![\\p{javaLowerCase}])" +
        "[\\p{javaUpperCase}. ]*(?![\\p{javaLowerCase}])"
    );
    private static final Pattern NICKNAME = Pattern.compile("([\"'])([\\p{IsAlphabetic}]+)\\1");
    private static final Pattern PREFIX = Pattern.compile(
        "^(?:[aA]l|[bB]in|[dD]e[nlr]?|[dD]u|[lL][ae]|[vV][ao]n|[zZ]u|[sS]t\\.?|[tT]e)$");
    private static final Pattern LAST_NAME = Pattern.compile("^[\\p{IsAlphabetic}'’.-]+$");
    private static final Pattern SUFFIX = Pattern.compile("^(?:I{1,3}|I?V|VI{1,3}|I?X|Jn?r\\.?|Sn?r\\.?)$",
        CASE_INSENSITIVE);
    private static final Pattern ALIAS = Pattern.compile("\\((?![Rr]et\\.?\\))([\\p{IsAlphabetic}\s]+)\\)");

    // Explicit post‑nominals only, to avoid clashing with initials.
    private static final Pattern POST_NOMINAL = Pattern.compile( //
        "^(?:" + //
        "[CDGKMO]BE|" + //
        "Ph\\.?D\\.?|D\\.?Phil\\.?|" + //
        "[BM]\\.?A\\.?|[BM]\\.?Sc?\\.?|" + //
        "F\\.?R\\.?S\\.?|[QK]\\.?C\\.?" + //
        "LL\\.?[BM]\\.?" + //
        ")$", //
        CASE_INSENSITIVE //
    );
    private static final Pattern PERIOD = Pattern.compile("\\.");
    private static final Pattern PERIOD_NOSPACE = Pattern.compile("\\.(?=[^ ])");
    private static final Pattern SPACES = Pattern.compile(" +");

    private static final float LAST_NAME_SCORE = 0.6f;
    private static final float FIRST_NAMES_SCORE = 0.25f;
    private static final float INITIALS_SCORE = 0.2f;
    private static final float TITLE_SCORE = 0.025f;
    private static final float NICKNAME_SCORE = 0.025f;
    private static final float PREFIX_SCORE = 0.025f;
    private static final float SUFFIX_SCORE = 0.025f;
    private static final float ALIAS_SCORE = 0.025f;
    private static final float POST_NOMINALS_SCORE = 0.025f;

    private static final float MATCH_THRESHOLD = LAST_NAME_SCORE + INITIALS_SCORE;
    private static final String NAME_SEP = " .";
    private static final int MAX_INITIALS = 4;

    /**
     * Parses a string into a {@code Name} object.
     * @param namestr The name string to parse.
     * @return A corresponding {@code Name} object or {@code null} if parsing failed.
     */
    public static Name parse(String namestr) {
        // Normalise periods so tokenisation on spaces works consistently.
        namestr = replaceAll(namestr, PERIOD_NOSPACE, ". ");
        if (namestr.isBlank())
            return null;

        // Extract nickname (in quotes).
        Matcher nickname_matcher = NICKNAME.matcher(namestr);
        String nickname = nickname_matcher.find() ? nickname_matcher.group(2) : null;
        if (nickname != null)
            namestr = namestr.replace(nickname_matcher.group(0), "").trim();

        // Extract alias (in parentheses).
        Matcher alias_matcher = ALIAS.matcher(namestr);
        String alias = alias_matcher.find() ? alias_matcher.group(1) : null;
        if (alias != null)
            namestr = namestr.replace(alias_matcher.group(0), "").trim();

        // Split tokens.
        String[] tokens = split(namestr);
        if (tokens.length == 0)
            return null;

        // Check for comma after last name.
        int commaPos = namestr.indexOf(",");

        List<String> title_tokens = new ArrayList<>();
        List<String> first_tokens = new ArrayList<>();
        List<String> prefix_tokens = new ArrayList<>();
        List<String> last_tokens = new ArrayList<>();
        List<String> suffix_tokens = new ArrayList<>();
        List<String> post_tokens = new ArrayList<>();

        // ------------------------------------------------------------
        // CASE 1 — Comma present: LAST, FIRST
        // ------------------------------------------------------------
        if (commaPos != -1) {
            // Possible name formats:
            // - prefix? lastName, title? firstName+ nickname? suffix? postNominals?
            // - prefix? lastName suffix?, title? firstName+ nickname? postNominals?

            String[] left = split(namestr.substring(0, commaPos).trim());
            String[] right = split(namestr.substring(commaPos + 1).trim());

            // LEFT: prefix? lastName suffix?
            int i = left.length - 1;
            while (i > 0 && match(left[i], SUFFIX, suffix_tokens, true))
                i--;
            if (i < 0)
                return null;

            last_tokens.add(0, left[i]);
            i--;

            while (i >= 0 && match(left[i], PREFIX, prefix_tokens, true))
                i--;

            // RIGHT: title? firstNames suffix? postnominals?
            int j = right.length - 1;
            while (j >= 0 && match(right[j], POST_NOMINAL, post_tokens, true))
                j--;

            while (j >= 0 && match(right[j], SUFFIX, suffix_tokens, true))
                j--;

            int k = 0;
            while (k <= j && match(right[k], TITLE, title_tokens, false))
                k++;

            while (k <= j) {
                first_tokens.add(right[k]);
                k++;
            }

        // ------------------------------------------------------------
        // CASE 2 — No comma: FIRST ... LAST
        // ------------------------------------------------------------
        } else {
            // Step 1: strip post-nominals and suffixes from end.
            int i = tokens.length - 1;
            while (i >= 0 && match(tokens[i], POST_NOMINAL, post_tokens, true))
                i--;

            while (i >= 0 && match(tokens[i], SUFFIX, suffix_tokens, true))
                i--;

            if (i < 0)
                return null;

            // Step 2: last_name = last remaining token.
            last_tokens.add(tokens[i]);
            i--;

            // Step 3: prefixes immediately before lastName.
            while (i >= 0 && match(tokens[i], PREFIX, prefix_tokens, true))
                i--;

            // Step 4: titles at start.
            int k = 0;
            while (k <= i && match(tokens[k], TITLE, title_tokens, false))
                k++;

            // Step 5: everything between title and prefix = first_names.
            while (k <= i) {
                first_tokens.add(tokens[k]);
                k++;
            }

            // --------------------------------------------------------
            // SPECIAL CASE: UPPERCASE last_name + title + initials
            // e.g. "LAAT Dr. ATJ"
            // --------------------------------------------------------
            if (tokens.length >= 3
                && isUpperCase(tokens[0])
                && LAST_NAME.matcher(tokens[0]).matches()
                && TITLE.matcher(tokens[1]).matches()
                && INITIALS.matcher(tokens[tokens.length - 1]).matches()
            ) {
                last_tokens.clear();
                last_tokens.add(toTitleCase(tokens[0].toLowerCase()));
                title_tokens.clear();
                title_tokens.add(tokens[1]);
                first_tokens.clear();
                for (i = 2; i < tokens.length; i++)
                    first_tokens.add(tokens[i]);
            }
        }

        // ------------------------------------------------------------
        // Build Name object
        // ------------------------------------------------------------
        Name name = new Name();
        name.title = join(title_tokens, Name::toTitleCase);
        name.firstNames = join(first_tokens, t -> toTitleCase(toInitials(t)));
        name.nickname = toTitleCase(nickname);
        name.prefix = toLowerCase(join(prefix_tokens));
        name.lastName = join(last_tokens, Name::toTitleCase);
        name.suffix = toTitleCase(join(suffix_tokens));
        name.alias = toTitleCase(alias);
        name.postNominals = replaceAll(toUpperCase(join(post_tokens)), PERIOD, "");

        if (name.lastName == null)
            return null;

        // Debug specific failing cases
        // if namestr.indexOf("Onça") != -1:
        //     print("DEBUG namestr:", namestr)
        //     print("  title      :", name.title)
        //     print("  first_names:", name.first_names)
        //     print("  prefix     :", name.prefix)
        //     print("  last_name  :", name.last_name)
        //     print("  suffix     :", name.suffix)
        //     print("  alias     :", name.alias)
        //     print("  post_nom   :", name.post_nominals)

        return name;
    }

    /**
     * Attempts to match a token against a pattern. If successful, inserts the token into a list.
     * @param token The token to test.
     * @param pattern The pattern to match.
     * @param result The result buffer.
     * @param prepend {@code true} to prepend {@code token} to {@code result}, {@code false} to append it.
     * @return {@code true} if {@code token} matched {@code pattern}, otherwise {@code false}.
     */
    private static boolean match(String token, Pattern pattern, List<String> result, boolean prepend) {
        Matcher matcher = pattern.matcher(token);
        if (matcher.matches()) {
            String match = matcher.group(matcher.groupCount() == 1 ? 1 : 0);
            if (prepend) {
                // if (!result.isEmpty() && result.charAt(0) != ' ')
                    // result.insert(0, ' ');
                result.add(0, match);
            } else {
                // if (!result.isEmpty() && result.charAt(result.length() - 1) != ' ')
                //     result.append(' ');
                result.add(match);
            }
            return true;
        }
        return false;
    }

    /**
     * Replaces all regular expression matches in a given string with a specified replacement.
     * @param s The string to search.
     * @param pattern
     * @param repl The replacement string.
     * @return The transformed string.
     */
    private static String replaceAll(String s, Pattern pattern, String repl) {
        if (s == null)
            return null;

        StringBuilder sb = new StringBuilder();
        Matcher m = pattern.matcher(s);
        while (m.find())
            m.appendReplacement(sb, repl);
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Appends a string to a buffer, prepending a space if the buffer is not empty.
     * @param sb The buffer.
     * @param s The string to append.
     */
    private static void append(StringBuilder sb, String s) {
        append(sb, s, null, null);
    }

    /**
     * Appends a string to a buffer, prepending a space if the buffer is not empty.
     * @param sb The buffer.
     * @param s The string to append.
     */
    private static void append(StringBuilder sb, String s, Character openDelim, Character closeDelim) {
        if (s != null) {
            if (!sb.isEmpty() && sb.charAt(sb.length() - 1) != ' ')
                sb.append(' ');
            if (openDelim != null)
                sb.append(openDelim);
            sb.append(s);
            if (closeDelim != null)
                sb.append(closeDelim);
        }
    }

    /**
     * Tokenizes a string on space characters
     * @param s The string to tokenize.
     * @return An array of tokens.
     */
    private static String[] split(String s) {
        String[] tokens = SPACES.split(s);
        List<String> a = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            if (token != null && !token.isBlank())
                a.add(token);
        }
        return a.toArray(new String[a.size()]);
    }

    /**
     * Joins a list of tokens into a single string.
     * @param tokens The tokens to join.
     * @return The space-separated concatenation of all elements of {@code tokens}.
     */
    private static String join(List<String> tokens) {
        return join(tokens, null);
    }

    /**
     * Joins a list of tokens into a single string.
     * @param tokens The tokens to join.
     * @param fn Optional transformation to apply to elements of {@code tokens}.
     * @return The space-separated concatenation of the transformed elements of {@code tokens}.
     */
    private static String join(List<String> tokens, Function<String, String> fn) {
        if (tokens == null || tokens.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (fn != null)
                token = fn.apply(token);
            if (isBlank(token))
                continue;
            if (!sb.isEmpty())
                sb.append(' ');
            sb.append(token);
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    /**
     * Converts an all‑uppercase, letters‑only token to space‑separated, period-terminated initials. Leaves tokens with
     * periods or spaces unchanged.
     * 
     * @param token The token to convert.
     * @return {@code token} converted to space‑separated, period-terminated initials.
     */
    private static String toInitials(String token) {
        int len = token.length();
        if (len > 0 && len <= MAX_INITIALS && isUpperCase(token) && isAlpha(token)) {
            char[] chars = new char[len * 3 - 1];
            for (int i = 0, j = 0; i < len; i++, j += 3) {
                chars[j] = token.charAt(i);
                chars[j + 1] = '.';
                if (i < len - 1)
                    chars[j + 2] = ' ';
            }
            token = new String(chars);
        }
        return token;
    }

    /**
     * Converts a string to title case. The first letter and any letter following any of {@code  }, {@code '},
     * {@code ’}, {@code .} or {@code -} is converted to uppercase; all other letters are converted to lowercase.
     * @param s The input string to convert.
     * @return The input string converted to title case.
     */
    private static String toTitleCase(String s) {
        if (s == null)
            return null;

        char[] buf = new char[s.length()];
        boolean atStart = true;
        for (int i = 0; i < buf.length; i++) {
            char c = s.charAt(i);
            buf[i] = atStart? Character.toUpperCase(c) : Character.toLowerCase(c);
            atStart = " '’.-".indexOf(c) != -1;
        }
        return new String(buf);
    }

    private String title;
    private String firstNames;
    private String nickname;
    private String prefix;
    private String lastName;
    private String suffix;
    private String alias;
    private String postNominals;

    public Name() {
    }

    public Name(String firstNames, String lastName) {
        this.firstNames = firstNames;
        this.lastName = lastName;
    }

    public Name(String title, String firstNames, String nickname, String prefix, String lastName, String suffix,
        String alias, String postNominals) {

        this.title = title;
        this.firstNames = firstNames;
        this.nickname = nickname;
        this.prefix = prefix;
        this.lastName = lastName;
        this.suffix = suffix;
        this.alias = alias;
        this.postNominals = postNominals;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
    
    public String getAlias() {
        return alias;
    }

    
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPostNominals() {
        return postNominals;
    }

    public void setPostNominals(String postNominals) {
        this.postNominals = postNominals;
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
        result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((postNominals == null) ? 0 : postNominals.hashCode());
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
        if (nickname == null) {
            if (other.nickname != null)
                return false;
        } else if (!nickname.equals(other.nickname))
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
        if (alias == null) {
            if (other.alias != null)
                return false;
        } else if (!alias.equals(other.alias))
            return false;
        if (postNominals == null) {
            if (other.postNominals != null)
                return false;
        } else if (!postNominals.equals(other.postNominals))
            return false;
        return true;
    }

    /**
     * Returns a string representation of the object. The result is equivalent to calling {@code format("%t%f%n%p%l%s%z")}.
     * @return The string representation.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        append(result, title);
        append(result, firstNames);
        append(result, nickname, '\'', '\'');
        append(result, prefix);
        append(result, lastName);
        append(result, suffix);
        append(result, alias, '(', ')');
        append(result, postNominals);
        return result.toString();
    }

    /**
     * Returns a string representation of the object, using a custom format. The format string consists of a sequence of
     * field specifications, each of which starts with a {@code %} character followed by a field type character. Valid
     * field types are as follows:<br>
     * <b>{@code t}</b>: The {@code title} field<br>
     * <b>{@code f}</b>: The {@code firstNames} field<br>
     * <b>{@code n}</b>: The {@code nickame} field<br>
     * <b>{@code p}</b>: The {@code prefix} field<br>
     * <b>{@code l}</b>: The {@code lastName} field<br>
     * <b>{@code s}</b>: The {@code suffix} field<br>
     * <b>{@code z}</b>: The {@code postNominals} field<br>
     * <b>{@code i}</b>: The {@code firstNames} field represented as initials, each with a trailing period and space<br>
     * <b>{@code I}</b>: The {@code firstNames} field represented as initials, each with neither trailing period nor
     * space<br>
     * <b>{@code J}</b>: The {@code firstNames} field represented as initials, each with a trailing space<br>
     * <b>{@code K}</b>: The {@code firstNames} field represented as initials, each with a trailing period<br>
     * <b>{@code %}</b>: A literal percent character<br>
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
                    case 'n':
                        append(result, nickname, '\'', '\'');
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
                    case 'z':
                        append(result, postNominals);
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
                    case '%':
                        result.append(c);
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

    /**
     * Tests for equality based on last_name and either first_names or initials, plus the other fields.
     * @param other The other name to compare with this.
     * @return {@code true} if {@code other} matches on {@code lastName} and either {@code firstNames} or {@code initials}.
     */
    public boolean matches(Name other) {
        return matchScore(other) >= MATCH_THRESHOLD;
    }

    /**
     * Computes the strength of the match between this name and another.
     * 
     * @param other The other name to compare with this.
     * @return A float between 0.0 and 1.0 indicating the strength of the match.
     */
    public float matchScore(Name other) {
        float score = 0.0f;
        if (Objects.equals(lastName, other.lastName))
            score += LAST_NAME_SCORE;
        if (Objects.equals(firstNames, other.firstNames))
            score += FIRST_NAMES_SCORE;
        else {
            // First names don't match exactly, so check for a partial match on name and/or initials.
            StringTokenizer s_fn_tok = new StringTokenizer(
                firstNames != null ? firstNames : "", NAME_SEP
            );
            StringTokenizer o_fn_tok = new StringTokenizer(
                other.firstNames != null ? other.firstNames : "", NAME_SEP
            );

            int i, fn_count_to_match = Math.min(s_fn_tok.countTokens(), o_fn_tok.countTokens());
            for (i = 0; i < fn_count_to_match; i++) {
                String s_fn = s_fn_tok.nextToken();
                String o_fn = o_fn_tok.nextToken();

                // If names match exactly, so far so good.
                if (s_fn.equals(o_fn))
                    continue;

                // If the initials don't match or neither name is an initial, there is no match.
                if (s_fn.charAt(0) != o_fn.charAt(0) || !(s_fn.length() == 1 || o_fn.length() == 1))
                    break;
            }
            if (i == fn_count_to_match)
                score += INITIALS_SCORE;
        }
        // TODO: Consider whether non-null mismatches should void/reset the score.
        if (title != null && title.equals(other.title))
            score += TITLE_SCORE;
        if (nickname != null && nickname.equals(other.nickname))
            score += NICKNAME_SCORE;
        if (prefix != null && prefix.equals(other.prefix))
            score += PREFIX_SCORE;
        if (suffix != null && suffix.equals(other.suffix))
            score += SUFFIX_SCORE;
        if (alias != null && alias.equals(other.alias))
            score += ALIAS_SCORE;
        if (postNominals != null && postNominals.equals(other.postNominals))
            score += POST_NOMINALS_SCORE;

        return score;
    }

}
