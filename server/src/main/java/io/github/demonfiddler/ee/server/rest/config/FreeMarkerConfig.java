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

package io.github.demonfiddler.ee.server.rest.config;

import java.util.List;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import freemarker.template.TemplateExceptionHandler;
import io.github.demonfiddler.ee.server.rest.ext.CSVOutputFormat;

@Configuration
public class FreeMarkerConfig {

    private static final freemarker.template.Configuration CONFIG = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_34);

    static {
        CONFIG.setClassForTemplateLoading(FreeMarkerConfig.class, "/rest/templates");
        CONFIG.setDefaultEncoding("UTF-8");
        // During web page *development* only:
        CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        // CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIG.setLogTemplateExceptions(false);
        CONFIG.setWrapUncheckedExceptions(true);
        CONFIG.setFallbackOnNullLoopVariable(false);
        CONFIG.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        CONFIG.setRegisteredCustomOutputFormats(List.of(CSVOutputFormat.INSTANCE));
    }

    @Bean
    public freemarker.template.Configuration fmConfig() {
        return CONFIG;
    }

}
