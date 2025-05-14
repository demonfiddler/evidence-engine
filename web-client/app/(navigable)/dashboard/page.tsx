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

'use client'

// import type { Metadata } from "next";
import TopicsAndRecords from "@/app/ui/dashboard/topics-records";
import DatabaseStatistics from "@/app/ui/dashboard/database-statistics";
import ChartBarIcon from "@heroicons/react/24/outline/ChartBarIcon";

// export const metadata: Metadata = {
//   title: "Dashboard",
//   description: "Overview and database statistics",
// };

export default function Dashboard() {
  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ChartBarIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Dashboard</h1>
      </div>
      <hgroup>
        <h2>Topics & Records</h2>
        <div className="shrink flex-auto shadow-lg">
          <TopicsAndRecords headerClassName="border bg-cyan-50" className="border" />
        </div>
      </hgroup>
      <hgroup>
        <h2>Database Statistics</h2>
        <DatabaseStatistics />
      </hgroup>
    </main>
  );
}
