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

package io.github.demonfiddler.ee.server.rest.tables;

import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType.BOOLEAN;
import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType.DATE;
import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType.DATETIME;
import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType.NUMBER;

import java.lang.reflect.InvocationTargetException;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.beanutils2.PropertyUtils;

import io.github.demonfiddler.ee.server.rest.util.RenderUtils;

public record Column<T>(String id, String header, ColumnType type, ColumnSpan span, BiFunction<T, Boolean, String> renderFn) {

    static <T> Map<String, Column<T>> map(List<Column<T>> columns) {
        Map<String, Column<T>> map = new LinkedHashMap<>();
        columns.forEach(c -> map.put(c.id(), c));
        return Collections.unmodifiableMap(map);
    }

    public enum ColumnType {
        STRING, //
        BOOLEAN, //
        DATE, //
        DATETIME, //
        ID, //
        NUMBER, //
        URL, //
    }

    public enum ColumnSpan {
        /** Spans a single column. */
        SINGLE,
        /** Spans full page width. */
        FULL
    }

    private String defaultRenderFn(T record, boolean raw) {
        try {
            Object value = type == BOOLEAN || type == DATE || type == DATETIME || type == NUMBER //
                ? PropertyUtils.getProperty(record, id) //
                : BeanUtils.getProperty(record, id);
            switch (type) {
                case BOOLEAN:
                    return RenderUtils.instance.renderBoolean((Boolean)value, raw);
                case DATE:
                    return RenderUtils.instance.renderDate((TemporalAccessor)value);
                case DATETIME:
                    return RenderUtils.instance.renderDateTime((TemporalAccessor)value);
                case NUMBER:
                    return RenderUtils.instance.renderNumber((Number)value, raw);
                case URL:
                    if (value instanceof java.net.URL)
                        return RenderUtils.instance.renderUrl((java.net.URL)value, raw);
                    else
                        return RenderUtils.instance.renderUrl((String)value, raw);
                default:
                    return (String)value;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return "";
        }
    }

    public String render(T record, boolean raw) {
        String result = renderFn != null ? renderFn.apply(record, raw) : defaultRenderFn(record, raw);
        return result != null ? result : "";
    }

}
