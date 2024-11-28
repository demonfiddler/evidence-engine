/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server and web client.
 * Copyright © 2024 Adrian Price. All rights reserved.
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

import java.util.StringTokenizer;

/**
 * Various string manipulation utilities.
 */
public final class StringUtils {

    /** Private ctor prevents instantiation. */
    private StringUtils() {
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
     * Counts the number of lines in a string.
     * @param s The string.
     * @return The number of lines in {@code s}.
     */
    public static int countLines(String s) {
        if (s == null || s.isEmpty())
            return 0;
        StringTokenizer st = new StringTokenizer(s, "\n\r");
        return st.countTokens();
    }

}
