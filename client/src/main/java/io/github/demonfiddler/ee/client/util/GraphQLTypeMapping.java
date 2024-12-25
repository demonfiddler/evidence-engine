/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
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

package io.github.demonfiddler.ee.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ClassUtils;

public class GraphQLTypeMapping {

	private final static Map<String, Class<?>> map = new HashMap<>();

	static {
		String line;
		Class<?> clazz;
		ClassLoader classLoader = GraphQLTypeMapping.class.getClassLoader();

		final String RESOURCE_PATH = "typeMapping.csv";

		try (BufferedReader reader =
			new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(RESOURCE_PATH)))) {
			while ((line = reader.readLine()) != null) {
				String[] keyValue = line.split(",");
				if (keyValue.length != 2) {
					throw new RuntimeException("Invalid line in typeMapping.csv: " + line);
				}
				try {
					clazz = ClassUtils.forName(keyValue[1], classLoader);
				} catch (Exception e) {
					throw new RuntimeException("Error while looking for the class of the type '" + keyValue[0] + "': "
						+ e.getClass().getSimpleName() + "-" + e.getMessage(), e);
				}
				map.put(keyValue[0], clazz);
			} // while
		} catch (NullPointerException e) {
			throw new RuntimeException("NullPointerException while reading type mapping : " + RESOURCE_PATH);
		} catch (IOException e) {
			throw new RuntimeException("Error while reading type mapping (" + RESOURCE_PATH + "): " + e.getMessage(),
				e);
		}
	}

	public static Class<?> getJavaClass(String typeName) {
		return map.get(typeName);
	}
}
