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

import type { NextConfig } from "next"

// Generate & export static assets thus: $ pnpm run build
// to export the static files to the 'out' directory.
const nextConfig: NextConfig = {
  output: "export",
  images: { unoptimized: true },

  // Output page to /path/index.html. Page URIs are /path/, so Tomcat seamlessly returns /path/index.html
  // without browser having to process the /path -> /path/ redirect that plagues the trailingSlash: false configuration.
  trailingSlash: true,
  skipTrailingSlashRedirect: false,

  // Output page to /path.html (the default).
  // Production build requests directory /path but receives 302 redirect to /path/, which gets 404.
  // Browser refresh/bookmark requires a Tomcat RewriteRule to append .html but I couldn't get it to work.
  // trailingSlash: false,
  // skipTrailingSlashRedirect: true,
}

export default nextConfig
