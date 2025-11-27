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

package io.github.demonfiddler.ee.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.demonfiddler.ee.server.model.Abbreviation;

public interface AbbreviationRepository extends ListCrudRepository<Abbreviation, String> {

    /**
     * Retrieves all abbreviations matching a given word. The list includes exact matches, prefix matches and suffix matches.
     * Compare with {@code findById()}, which only returns exact matches.
     * @param word The word whose abbreviations are required.
     * @return The registered abbreviations if any exist, otherwise an empty list.
     */
    @NativeQuery("SELECT * FROM \"abbreviation\"\n" //
        + "WHERE LOWER(\"word\") = :word\n" //
        + "OR \"is_prefix\" AND LOWER(:word) LIKE CONCAT(LOWER(SUBSTRING(\"word\", 1, LENGTH(\"word\") - 1)), '%')\n" //
        + "OR \"is_suffix\" AND LOWER(:word) LIKE CONCAT('%', LOWER(SUBSTRING(\"word\", 2)));")
    List<Abbreviation> findByWord(@Param("word") String word);

    // TODO: handle prefixes and suffixes
    /**
     * Tests whether a given string is an ISO 4 abbreviation (per ISSN LTWA).
     * @param abbrev The abbreviation, which should include a trailing period.
     * @return {@code true} if {@code s} is a registered abbreviation.
     */
    @Query("SELECT COUNT(*) <> 0 FROM Abbreviation a WHERE LOWER(a.abbreviation) = LOWER(:abbrev)")
    boolean existsByAbbreviation(@Param("abbrev") String abbrev);

    // TODO: handle prefixes and suffixes
    /**
     * Returns the specified ISO 4 abbreviation (per ISSN LTWA).
     * @param abbrev The abbreviation, which should include a trailing period.
     * @return The registered abbreviations if any exist, otherwise an empty list.
     */
    @Query("SELECT a FROM Abbreviation a WHERE LOWER(a.abbreviation) = LOWER(:abbrev)")
    List<Abbreviation> findByAbbreviation(@Param("abbrev") String abbrev);

}
