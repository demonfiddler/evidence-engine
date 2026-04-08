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

package io.github.demonfiddler.ee.server.util;

import static io.github.demonfiddler.ee.server.util.Constants.PROFILE_DEVELOPMENT;
import static io.github.demonfiddler.ee.server.util.Constants.PROFILE_INTEGRATION_TEST;
import static io.github.demonfiddler.ee.server.util.Constants.PROFILE_PRODUCTION;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * A bean for working with Spring profiles.
 */
@Component
public class ProfileUtils {

    @Autowired
    Environment env;

    /**
     * Indicates whether the specified profile is active.
     * @param profile The profile of interest.
     * @return {@code true} if {@code profile} is active.
     */
    public boolean isProfileActive(String profile) {
        return ArrayUtils.contains(env.getActiveProfiles(), profile);
    }

    /**
     * Indicates whether the server is running in development mode.
     * @return {@code true} if the {@code development} profile is active.
     */
    public boolean isDevelopment() {
        return isProfileActive(PROFILE_DEVELOPMENT);
    }

    /**
     * Indicates whether the server is running in integration test mode.
     * @return {@code true} if the {@code integration-test} profile is active.
     */
    public boolean isIntegrationTesting() {
        return isProfileActive(PROFILE_INTEGRATION_TEST);
    }

    /**
     * Indicates whether the server is running in production mode.
     * @return {@code true} if the {@code production} profile is active.
     */
    public boolean isProduction() {
        return isProfileActive(PROFILE_PRODUCTION);
    }

}
