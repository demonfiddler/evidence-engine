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

package io.github.demonfiddler.ee.common.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.StringTokenizer;

import io.github.demonfiddler.ee.common.model.Country;

/**
 * Various string manipulation utilities.
 */
public final class StringUtils {

    public static final String NL = System.getProperty("line.separator");

    /**
     * Counts the number of lines in a string.
     * @param s The string.
     * @return The number of lines in {@code s}.
     */
    public static int countLines(String s) {
        if (s == null || s.isEmpty())
            return 0;
        StringTokenizer st = new StringTokenizer(s, "\r\n");
        return st.countTokens();
    }

    /**
     * Uppercases the first character of a string and lowercases the rest.
     * @param s The string.
     * @return A copy of {@code s} with the first character uppercased and the remainder lowercased.
     */
    public static String firstToUpper(String s) {
        if (s == null)
            return s;

        char[] c = s.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        for (int i = 1; i < c.length; i++)
            c[i] = Character.toLowerCase(c[i]);
        return new String(c);
    }

    /**
     * Returns whether a string is {@code null} or blank.
     * @param s The string to test.
     * @return {@code true} if {@code s} is {@code null} or contains only spaces
     */
    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /**
     * Strips leading and trailing double quote characters from a string.
     * @param s The string to strip.
     * @return The input string with leading and trailing double quote characters stripped.
     */
    public static String unquote(String s) {
        if (s == null || s.isEmpty())
            return s;
        int start = s.charAt(0) == '"' ? 1 : 0;
        int end = s.charAt(s.length() - 1) == '"' ? s.length() - 1 : s.length();
        return s.substring(start, end);
    }

    /**
     * Parses a {@code Boolean} from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code Boolean} value of {@code s}, with {@code &quot;0&quot;} yielding {@code false} and all other values yielding {@code true}.
     */
    public static Boolean parseBoolean(String s) {
        return isBlank(s) ? null : !s.equals("0");
    }

    /**
     * Parses an ISO-3166-1 {@code alpha_2} country code from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code alpha_2} value of {@code s}.
     */
    public static String parseCountry(String s) {
        Country country = Country.BY_COMMON_NAME.get(s);
        return country != null ? country.alpha_2() : null;
    }

    /**
     * Parses a {@code LocalDate} from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code LocalDate} value of {@code s}.
     */
    public static LocalDate parseLocalDate(String s) {
        return isBlank(s) ? null : LocalDate.parse(s);
    }

    /**
     * Parses a {@code Long} from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code Long} value of {@code s}.
     */
    public static Long parseLong(String s) {
        return isBlank(s) ? null : Long.valueOf(s);
    }

    /**
     * Parses a {@code Integer} from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code Integer} value of {@code s}.
     */
    public static Integer parseInteger(String s) {
        return isBlank(s) ? null : Integer.valueOf(s);
    }

    /**
     * Parses a {@code Short} from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code Short} value of {@code s}.
     */
    public static Short parseShort(String s) {
        return isBlank(s) ? null : Short.valueOf(s);
    }

    /**
     * Parses a {@code URL} from a {@code String}.
     * @param s The string to parse, or {@code null}.
     * @return {@code null} if {@code s} is {@code null} or blank, otherwise the {@code URL} value of {@code s}.
     */
    public static URL parseUrl(String s) throws MalformedURLException {
        return isBlank(s) ? null : URI.create(s).toURL();
    }

    /** Private ctor prevents instantiation. */
    private StringUtils() {
    }    

}
