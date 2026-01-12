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

import static io.github.demonfiddler.ee.common.util.StringUtils.parseShort;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about a country.
 * @param alpha_2 The ISO-3166-1 two-character alphabetic country code.
 * @param alpha_3 The ISO-3166-1 three-character alphabetic country code.
 * @param numeric The ISO-3166-1 three-digit numeric country code.
 * @param isoName The ISO-3166-1 official country name.
 * @param commonName The common country code.
 * @param year The registration year.
 * @param ccTld The country's official DNS top level domain.
 * @param notes Notes about the country.
 */
public record Country(String alpha_2, String alpha_3, String numeric, String isoName, String commonName, Short year,
    String ccTld, String notes) {

    private static final Logger LOGGER = LoggerFactory.getLogger(Country.class);

    /** Countries indexed by {@code alpha_2}. */
    public static final Map<String, Country> BY_ALPHA_2;
    /** Countries indexed by {@code alpha_3}. */
    public static final Map<String, Country> BY_ALPHA_3;
    /** Countries indexed by {@code numeric}. */
    public static final Map<String, Country> BY_NUMERIC;
    /** Countries indexed by {@code isoName}. */
    public static final Map<String, Country> BY_ISO_NAME;
    /** Countries indexed by {@code commonName}. */
    public static final Map<String, Country> BY_COMMON_NAME;

    static {
        Map<String, Country> byAlpha_2 = new LinkedHashMap<>();
        Map<String, Country> byAlpha_3 = new LinkedHashMap<>();
        Map<String, Country> byNumeric = new LinkedHashMap<>();
        Map<String, Country> byIsoName = new LinkedHashMap<>();
        Map<String, Country> byCommonName = new LinkedHashMap<>();

        try (Reader in = new InputStreamReader(Country.class.getResourceAsStream("/country.csv"), "UTF-8")) {
            CSVFormat fmt = CSVFormat.Builder.create() //
                .setDelimiter(",") //
                .setSkipHeaderRecord(false) //
                .setHeader() //
                .setCommentMarker(null) //
                .setEscape('"') //
                .setQuoteMode(QuoteMode.ALL) //
                .setTrim(true) //
                .get();
            Iterable<CSVRecord> it = fmt.parse(in);
            it.forEach(rec -> {
                String alpha_2 = rec.get("alpha_2");
                String alpha_3 = rec.get("alpha_3");
                String numeric = rec.get("numeric");
                String isoName = rec.get("iso_name");
                String commonName = rec.get("common_name");
                Country country = new Country(alpha_2, alpha_3, numeric, isoName, commonName,
                    parseShort(rec.get("year")), rec.get("cc_tld"), rec.get("notes"));

                byAlpha_2.put(alpha_2, country);
                byAlpha_3.put(alpha_3, country);
                byNumeric.put(numeric, country);
                byIsoName.put(isoName, country);
                byCommonName.put(commonName, country);
            });
        } catch (IOException e) {
            LOGGER.error("Error initializing countries list", e);
        }

        BY_ALPHA_2 = Collections.unmodifiableMap(byAlpha_2);
        BY_ALPHA_3 = Collections.unmodifiableMap(byAlpha_3);
        BY_NUMERIC = Collections.unmodifiableMap(byNumeric);
        BY_ISO_NAME = Collections.unmodifiableMap(byIsoName);
        BY_COMMON_NAME = Collections.unmodifiableMap(byCommonName);
    }

}
