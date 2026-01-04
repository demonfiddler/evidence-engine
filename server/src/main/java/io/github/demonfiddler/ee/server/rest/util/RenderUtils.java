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

package io.github.demonfiddler.ee.server.rest.util;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.server.model.Claim;
import io.github.demonfiddler.ee.server.model.Comment;
import io.github.demonfiddler.ee.server.model.CountryFormatKind;
import io.github.demonfiddler.ee.server.model.Declaration;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.Group;
import io.github.demonfiddler.ee.server.model.ITrackedEntity;
import io.github.demonfiddler.ee.server.model.Journal;
import io.github.demonfiddler.ee.server.model.Person;
import io.github.demonfiddler.ee.server.model.Publication;
import io.github.demonfiddler.ee.server.model.Publisher;
import io.github.demonfiddler.ee.server.model.Quotation;
import io.github.demonfiddler.ee.server.model.Topic;
import io.github.demonfiddler.ee.server.model.User;
import io.github.demonfiddler.ee.server.util.CountryUtils;

@Component
public class RenderUtils {

    public static RenderUtils instance;

    private static final DateTimeFormatter RIS_DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private static final String[] EMPTY_STRINGS = {};

    @Autowired
    private CountryUtils countryUtils;

    public RenderUtils() {
        instance = this;
    }

    public String[] split(String s) {
        return s != null ? s.split("(?:\\r|\\n)+") : EMPTY_STRINGS;
    }

    public String renderBoolean(Boolean value, boolean raw) {
        return raw ? Boolean.TRUE.equals(value) ? "yes" : "no" : Boolean.TRUE.equals(value) ? "&#9746;" : "&#9744;";
    }

    public String renderCountry(String country) {
        return countryUtils.formatCountry(country, CountryFormatKind.COMMON_NAME);
    }

    public String renderDate(TemporalAccessor date) {
        return date != null ? DATE.format(date) : "";
    }

    public String renderRisDate(TemporalAccessor date) {
        return date != null ? RIS_DATE.format(date) : "";
    }

    public String renderDateTime(TemporalAccessor datetime) {
        return datetime != null ? DATETIME.format(datetime) : "";
    }

    public String renderNumber(Number number, boolean raw) {
        return number != null ? raw ? number.toString() : NumberFormat.getInstance().format(number) : "";
    }

    public String renderUrl(String url, boolean raw) {
        return renderUrl(url, url, raw);
    }

    public String renderUrl(URL url, boolean raw) {
        return renderUrl(url, url.toExternalForm(), raw);
    }

    public String renderUrl(String url, String text, boolean raw) {
        return url != null
            ? raw ? url : ("<a href=\"" + url + "\" target=\"_blank\">" + (text != null ? text : url) + "</a>") : "";
    }

    public String renderUrl(URL url, String text, boolean raw) {
        return url != null
            ? raw ? url.toExternalForm()
                : ("<a href=\"" + url.toExternalForm() + "\" target=\"_blank\">" + (text != null ? text : url) + "</a>")
            : "";
    }

    private String getPersonName(Person p) {
        return p != null ? p.getFirstName() + ' ' + p.getLastName() : "";
    }

    public String renderEntityLabel(ITrackedEntity obj) {
        if (obj != null) {
            if (obj instanceof HibernateProxy)
                obj = (ITrackedEntity)Hibernate.unproxy(obj);
            EntityKind entityKind = EntityKind.valueOf(obj.getEntityKind());
            String text = switch (entityKind) {
                case CLA -> ((Claim)obj).getText();
                case COM -> ((Comment)obj).getText();
                case DEC -> ((Declaration)obj).getTitle();
                case GRP -> ((Group)obj).getGroupname();
                case JOU -> ((Journal)obj).getTitle();
                case PER -> getPersonName((Person)obj);
                case PUB -> ((Publication)obj).getTitle();
                case PBR -> ((Publisher)obj).getName();
                case QUO -> ((Quotation)obj).getText();
                case TOP -> ((Topic)obj).getLabel();
                case USR -> ((User)obj).getUsername();
                default -> throw new IllegalArgumentException("Unsupported class: " + obj.getEntityKind());
            };
            return entityKind.label() + '#' + obj.getId() + ": " + text;
        } else {
            return "";
        }
    }

    /**
     * Calculates the detail element float width for a given paper size, orientation and font size. The value returned
     * is that necessary to fill the page width with a whole number of floating HTML {@code <div>} elements.
     * @param paper The paper size, "A3" or "A4".
     * @param orientation The paper orientation, "portrait" or "landscape".
     * @param fontSize The font size, between 8 and 16 inclusive.
     * @return The float width as a percentage.
     */
    public int calculateSingleFloatWidth(String paper, String orientation, int fontSize) {
        return switch (paper) {
            case "A4" ->
                switch (orientation) {
                    case "portrait" ->
                        switch (fontSize) {
                            case 8 -> 24; // 4 columns
                            case 9, 10, 11, 12 -> 33; // 3 columns
                            case 13, 14, 15, 16 -> 49; // 2 columns
                            default -> throw new IllegalArgumentException("Unsupported font size: " + fontSize);
                        };
                    case "landscape" ->
                        switch (fontSize) {
                            case 8, 9, 10 -> 19; // 5 columns
                            case 11, 12, 13 -> 24; // 4 columns
                            case 14, 15, 16 -> 33; // 3 columns
                            default -> throw new IllegalArgumentException("Unsupported font size: " + fontSize);
                        };
                    default -> throw new IllegalArgumentException("Unsupported orientation: " + orientation);
                };
            case "A3" ->
                switch (orientation) {
                    case "portrait" ->
                        switch (fontSize) {
                            case 8, 9, 10 -> 19; // 5 columns
                            case 11, 12, 13 -> 24; // 4 columns
                            case 14, 15, 16 -> 33; // 3 columns
                            default -> throw new IllegalArgumentException("Unsupported font size: " + fontSize);
                        };
                    case "landscape" ->
                        switch (fontSize) {
                            case 8, 9 -> 12; // 8 columns
                            case 10, 11 -> 14; // 7 columns
                            case 12 -> 16; // 6 columns
                            case 13, 14 -> 19; // 5 columns
                            case 15, 16 -> 24; // 4 columns
                            default -> throw new IllegalArgumentException("Unsupported font size: " + fontSize);
                        };
                    default -> throw new IllegalArgumentException("Unsupported orientation: " + orientation);
                };
            default -> throw new IllegalArgumentException("Unsupported paper size: " + paper);
        };
    }

}
