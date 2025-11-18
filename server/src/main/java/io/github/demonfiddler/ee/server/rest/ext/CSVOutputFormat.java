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

package io.github.demonfiddler.ee.server.rest.ext;

import java.io.IOException;
import java.io.Writer;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.template.TemplateModelException;

/**
 * Represents the CSV output format (MIME type "text/csv", name "CSV"). This format escapes by default (via
 * {@link StringUtil#CSVEnc(String)}). The {@code ?csv} built-in silently bypasses
 * template output values of the type produced by this output format ({@link TemplateCSVOutputModel}).
 */
public class CSVOutputFormat extends CommonMarkupOutputFormat<TemplateCSVOutputModel> {

    /**
     * The only instance (singleton) of this {@link OutputFormat}.
     */
    public static final CSVOutputFormat INSTANCE = new CSVOutputFormat();

    protected CSVOutputFormat() {
    }
    
    @Override
    public String getName() {
        return "CSV";
    }

    @Override
    public String getMimeType() {
        return "text/csv";
    }

    // fromPlainTextByEscaping(String) and then output(TemplateMarkupOutputModel, Writer)
    @Override
    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        out.write(escapePlainText(textToEsc));
    }

    //  fromPlainTextByEscaping(String) and then getMarkupString(TemplateMarkupOutputModel)
    @Override
    public String escapePlainText(String plainTextContent) {
        return plainTextContent.replace("\"", "\"\"");
    }

    @Override
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("csv");
    }

    @Override
    protected TemplateCSVOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateCSVOutputModel(plainTextContent, markupContent);
    }

}
