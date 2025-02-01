/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
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

import java.util.Arrays;
import java.util.Objects;

/**
 * Various array manipulation methods. It is not intended to be a complete, general array manipulation tool.
 */
public class ArrayUtils {

    /**
     * Computes the intersection of values between two integer arrays. The implementation assumes that elements are
     * unique within each array.
     * @param array1
     * @param array2
     * @return A new araray containing the values which appear in both {@code array1} and  {@code array2}.
     */
    public static int[] intersection(int[] array1, int[] array2) {
        Objects.requireNonNull(array1, "array1 cannot be null");
        Objects.requireNonNull(array2, "array2 cannot be null");

        // Make sure we scan the longer array, so we don't miss any elements.
        if (array1.length < array2.length) {
            int[] tmp = array1;
            array1 = array2;
            array2 = tmp;
        }

        int length = 0;
        int[] result = new int[Math.min(array1.length, array2.length)];
        for (int i = 0; i < array1.length; i++) {
            if (contains(array2, array1[i]))
                result[length++] = array1[i];
        }
        // If necessary, trim result to size.
        if (length < result.length)
            result = Arrays.copyOf(result, length);

        return result;
    }

    /**
     * Returns whether an array contains a value.
     * @param array The array to search.
     * @param value
     * @return
     */
    public static boolean contains(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value)
                return true;
        }
        return false;
    }

    /** Private ctor prevents instantiation. */
    private ArrayUtils() {
    }

}
