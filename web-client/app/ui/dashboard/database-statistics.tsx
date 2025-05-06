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

import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  ExclamationCircleIcon,
  EnvelopeOpenIcon,
  AcademicCapIcon,
  BeakerIcon,
  ChatBubbleBottomCenterTextIcon,
  Bars3BottomRightIcon,
  UserIcon,
} from "@heroicons/react/24/outline";
import {
    SchoolTeacherIcon,
    NobelPrizeIcon
} from "@/app/ui/icons";

const stats = {
  "topics": "4/7",
  "claims": 248,
  "declarations": 11,
  "persons": 2977,
  "publications": 147,
  "quotations": 21,
  "nobels": 9,
  "professors": 735,
  "doctorates": 620,
};
const items = [
  { heading: "Topics", description: "Top-level / nested topics", icon: Bars3BottomRightIcon, property: "topics"},
  { heading: "Claims", description: "Total claims of fact", icon: ExclamationCircleIcon, property: "claims"},
  { heading: "Declarations", description: "Total declarations, public letters, etc.", icon: EnvelopeOpenIcon, property: "declarations"},
  { heading: "Persons", description: "Total scientists, professionals, etc.", icon: UserIcon, property: "persons"},
  { heading: "Publications", description: "Total scientific publications, papers, etc.", icon: BeakerIcon, property: "publications"},
  { heading: "Quotations", description: "Total quotations", icon: ChatBubbleBottomCenterTextIcon, property: "quotations"},
  { heading: "Nobel Prizes", description: "Total Nobel Laureates", icon: NobelPrizeIcon, property: "nobels"},
  { heading: "Professors", description: "Total university professors (past and present)", icon: SchoolTeacherIcon, property: "professors"},
  { heading: "Doctorates", description: "Total qualified to doctoral level", icon: AcademicCapIcon, property: "doctorates"},
];

export default function DatabaseStatistics() {
  return (
    <div className="flex flex-wrap gap-4">
      {
        items.map(item => (
          <Card key={item.property} className="w-64 h-64 bg-cyan-50">
            <CardHeader className="flex justify-center">
              <item.icon className="w-8" />
              <CardTitle className="text-2xl">{item.heading}</CardTitle>
              {/* <CardDescription>
                <p>Card description</p>
              </CardDescription> */}
            </CardHeader>
            <CardContent className="grid justify-items-center text-4xl">
              {`${stats[item.property]}`}
            </CardContent>
            <CardFooter className="text-center">
              <p className="flex-grow text-center">{item.description}</p>
            </CardFooter>
          </Card>
        ))
      }
    </div>
  )
}