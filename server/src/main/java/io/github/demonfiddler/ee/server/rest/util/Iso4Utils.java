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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.common.util.StringUtils;
import io.github.demonfiddler.ee.server.model.Abbreviation;
import io.github.demonfiddler.ee.server.repository.AbbreviationRepository;

@Component
public class Iso4Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iso4Utils.class);
    /** Words to omit from ISO 4 abbreviations. Includes articles, conjunctions, prepositions and other words. */
    public static final Set<String> ISO4_OMIT_WORDS;

    static {
        Set<String> words = new HashSet<>();
        try (BufferedReader in =
            new BufferedReader(new InputStreamReader(Iso4Utils.class.getResourceAsStream("/misc/iso4-omit-words.txt"),
                StandardCharsets.UTF_8))) {

            String word;
            while ((word = in.readLine()) != null)
                words.add(word);
        } catch (IOException e) {
            LOGGER.error("Error reading resource /misc/iso4-omit-words.txt", e);
        }
        ISO4_OMIT_WORDS = Collections.unmodifiableSet(words);
    }

    private final AbbreviationRepository abbreviationRepository;

    @Autowired
    public Iso4Utils(AbbreviationRepository abbreviationRepository) {
        this.abbreviationRepository = abbreviationRepository;
    }

    /**
     * Abbreviates a journal title using ISO 4 abbreviation rules.
     * @param title The unabbreviated title.
     * @return The ISO 4 abbreviated title.
     */
    public String abbreviate(String title) {
        StringBuilder buf = new StringBuilder();
        abbreviate(buf, title);
        return buf.toString();
    }

    /**
     * Abbreviates a journal title using ISO 4 abbreviation rules.
     * @param title The unabbreviated title.
     * @return The ISO 4 abbreviated title.
     */
    private void abbreviate(StringBuilder buf, String title) {
        StringTokenizer tok = new StringTokenizer(title, " ");
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
            if (ISO4_OMIT_WORDS.contains(word.toLowerCase()))
                continue;
            if (buf.length() != 0)
                buf.append(' ');
            Abbreviation abbreviation = abbreviationFor(word);
            if (abbreviation != null) {
                String abbrev = abbreviation.getAbbreviation();
                if (abbrev == null) {
                    buf.append(word);
                } else if (abbreviation.isPrefix() && abbreviation.isSuffix()) {
                    // TODO: handle combined prefix+suffix correctly.
                    // e.g. "Orthographic" matches prefix+suffix: {-graph-, -gr.} -> "Orthogr."
                    // e.g. "Graphology" matches prefix+suffix: {-graph-, -gr.} -> "Gr."
                } else if (abbreviation.isPrefix()) {
                    // e.g. "Science" matches prefix: {scienc-, sci.} -> "Sci."
                    // String prefix = abbreviation.getWord().replace("-", "");
                    // String root = word.substring(prefix.length());
                    if (abbrev.charAt(0) == '-')
                        abbrev = abbrev.substring(1);
                    if (Character.isUpperCase(word.charAt(0)))
                        abbrev = StringUtils.firstToUpper(abbrev);
                    buf.append(abbrev);
                    if (!abbrev.endsWith("."))
                        buf.append('.');
                    // abbreviate(buf, root);
                } else if (abbreviation.isSuffix()) {
                    // e.g. "Dorsetshire" matches suffix: {-shire, -sh.} -> "Dorsetsh."
                    String suffix = abbreviation.getWord().replace("-", "");
                    String root = word.substring(0, word.lastIndexOf(suffix));
                    abbreviate(buf, root);
                    if (abbrev.charAt(0) == '-')
                        abbrev = abbrev.substring(1);
                    if (!abbrev.endsWith("."))
                        buf.append('.');
                    buf.append(abbrev);
                } else {
                    // e.g. "Blueprint" matches exact: {blueprint, bluepr.} -> "Bluepr."
                    if (Character.isUpperCase(word.charAt(0)))
                        abbrev = StringUtils.firstToUpper(abbrev);
                    buf.append(abbrev);
                    if (!abbrev.endsWith("."))
                        buf.append('.');
                }
            } else {
                buf.append(word);
            }
        }
    }

    /**
     * Normalizes an abbreviated title according to ISO 4 abbreviation rules.
     * @param value The ISO 4 abbreviation.
     * @return The normalized abbreviation, with first letters uppercase and a trailing period.
     */
    public String normalizeAbbreviation(String value) {
        StringBuilder buf = new StringBuilder();
        StringTokenizer tok = new StringTokenizer(value, " ");
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
            if (isAbbreviation(word)) {
                word = StringUtils.firstToUpper(word);
                buf.append(word);
                // KLUDGE: If there are more tokens, ensure trailing period.
                if (!word.endsWith("."))
                    buf.append('.');
            } else {
                buf.append(word);
            }
            if (tok.hasMoreTokens())
                buf.append(' ');
        }
        return buf.toString();
    }

    private boolean isAbbreviation(String word) {
        word = word.toLowerCase();
        if (!word.endsWith("."))
            word += '.';
        return abbreviationRepository.existsByAbbreviation(word);
    }

    /**
     * Returns the longest matching abbreviation from the LTWA.
     * @param word The word to abbreviate.
     * @return The longest matching abbreviation, in priority order: exact, combined prefix/suffix, prefix, suffix.
     */
    private Abbreviation abbreviationFor(String word) {
        List<Abbreviation> abbreviations = abbreviationRepository.findByWord(word);
        Abbreviation exact = null;
        Abbreviation psfix = null;
        Abbreviation prefix = null;
        Abbreviation suffix = null;
        for (Abbreviation abbreviation : abbreviations) {
            if (abbreviation.isPrefix() && abbreviation.isSuffix()) {
                if (psfix == null || abbreviation.getWord().length() > psfix.getWord().length())
                    psfix = abbreviation;
            } else if (abbreviation.isPrefix()) {
                if (prefix == null || abbreviation.getWord().length() > prefix.getWord().length())
                    prefix = abbreviation;
            } else if (abbreviation.isSuffix()) {
                if (suffix == null || abbreviation.getWord().length() > suffix.getWord().length())
                    suffix = abbreviation;
            } else {
                if (exact == null || abbreviation.getWord().length() > exact.getWord().length())
                    exact = abbreviation;
            }
        }
        return exact != null ? exact : psfix != null ? psfix : prefix != null ? prefix : suffix;
    }

}
