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

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;

/**
 * Ensures that all table and column names are quoted, as required by some databases.
 */
public class PhysicalNamingStrategyQuotedSnakeCaseImpl extends PhysicalNamingStrategySnakeCaseImpl {

    @Override
	protected Identifier unquotedIdentifier(Identifier name) {
        if (name == null)
            return null;
        name = super.unquotedIdentifier(name);
		return name.quoted();
    }

    @Override
    protected Identifier quotedIdentifier(Identifier quotedName) {
        return super.quotedIdentifier(quotedName);
    }

    
}
