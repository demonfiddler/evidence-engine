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

package io.github.demonfiddler.ee.server.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.server.model.Country;
import io.github.demonfiddler.ee.server.model.CountryFormatKind;
import io.github.demonfiddler.ee.server.repository.CountryRepository;
import jakarta.annotation.Resource;

/**
 * A bean for working with {@code Country} objects.
 */
@Component
public class CountryUtils {

    private static final Map<String, Country> ALPHA_2_TO_COUNTRY = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Country> COMMON_NAME_TO_COUNTRY = Collections.synchronizedMap(new HashMap<>());

    @Resource
    CountryRepository countryRepository;

    /**
     * Returns the requested field of the country with specified country code.
     * @param code The ISO-3166-1 alpha_2 country code.
     * @param format The field required.
     * @return The value of the requested field.
     */
    public String formatCountry(String code, CountryFormatKind format) {
        if (format == null)
            format = CountryFormatKind.COMMON_NAME;
        if (code != null && format != CountryFormatKind.ALPHA_2) {
            Country country = findCountry(code, CountryFormatKind.ALPHA_2);
            switch (format) {
                case ALPHA_2:
                    // No action required; we already have the value.
                    break;
                case ALPHA_3:
                    code = country.getAlpha_3();
                    break;
                case COMMON_NAME:
                    code = country.getCommonName();
                    break;
                case ISO_NAME:
                    code = country.getIsoName();
                    break;
                case NUMERIC:
                    code = country.getNumeric();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown format: " + format);
            }
        }
        return code;
    }

    /**
     * Returns the {@link Country} object for the specified country code or name.
     * @param code The ISO-3166-1 alpha_2 country code.
     * @param format The field to use for lookup. Must be either {@code ALPHA_2} or {@code COMMON_NAME}
     * @return The corresponding {@link Country} object.
     */
    private Country findCountry(String code, CountryFormatKind format) {
        Country country;
        Map<String, Country> map;
        switch (format) {
            case ALPHA_2:
                map = ALPHA_2_TO_COUNTRY;
                break;
            case COMMON_NAME:
                map = COMMON_NAME_TO_COUNTRY;
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
        country = map.get(code);

        if (country == null) {
            Optional<Country> countryOpt;
            switch (format) {
                case ALPHA_2:
                    countryOpt = countryRepository.findById(code);
                    break;
                case COMMON_NAME:
                    countryOpt = countryRepository.findByCommonName(code);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
            if (countryOpt.isEmpty())
                throw new IllegalArgumentException("Could not find country with " + format + " = " + code);
            country = countryOpt.get();
            ALPHA_2_TO_COUNTRY.put(code, country);
            COMMON_NAME_TO_COUNTRY.put(country.getCommonName(), country);
        }

        return country;
    }

    /**
     * Returns the common name for the specified country code.
     * @param code The ISO-3166-1 alpha_2 country code.
     * @return The common name for the country or {@code null} if unknown.
     */
    public String getCommonName(String code) {
        Country country = findCountry(code, CountryFormatKind.ALPHA_2);
        return country == null ? null : country.getCommonName();
    }

    /**
     * Returns the country code for the specified country.
     * @param code The common name for the country.
     * @return The ISO-3166-1 alpha_2 country code for the country or {@code null} if unknown.
     */
    public String getAlpha_2(String commonName) {
        Country country = findCountry(commonName, CountryFormatKind.COMMON_NAME);
        return country == null ? null : country.getAlpha_2();
    }

}
