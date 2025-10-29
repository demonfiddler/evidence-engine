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

import {
  Bars3BottomRightIcon,
  BeakerIcon,
  BuildingOfficeIcon,
  ChartBarIcon,
  ChatBubbleBottomCenterTextIcon,
  ChatBubbleLeftRightIcon,
  EnvelopeOpenIcon,
  ExclamationCircleIcon,
  HomeIcon,
  InformationCircleIcon,
  ListBulletIcon,
  NewspaperIcon,
  QuestionMarkCircleIcon,
  ShieldCheckIcon,
  UserIcon,
} from '@heroicons/react/24/outline';

export const appItems = [
  { label: "Home", href: "/", icon: HomeIcon },
  { label: "Dashboard", href: "/dashboard", icon: ChartBarIcon },
  { label: "Claims", href: "/claims", icon: ExclamationCircleIcon },
  { label: "Publications", href: "/publications", icon: BeakerIcon },
  { label: "Persons", href: "/persons", icon: UserIcon },
  { label: "Declarations", href: "/declarations", icon: EnvelopeOpenIcon },
  { label: "Quotations", href: "/quotations", icon: ChatBubbleBottomCenterTextIcon },
  { label: "Comments", href: "/comments", icon: ChatBubbleLeftRightIcon },
  { label: "Log", href: "/log", icon: ListBulletIcon },
  { label: "Help", href: "/help", icon: QuestionMarkCircleIcon },
  { label: "About", href: "/about", icon: InformationCircleIcon },
]
export const adminItems = [
  { label: "Topics", href: "/admin/topics", icon: Bars3BottomRightIcon },
  { label: "Journals", href: "/admin/journals", icon: NewspaperIcon },
  { label: "Publishers", href: "/admin/publishers", icon: BuildingOfficeIcon },
  { label: "Security", href: "/admin/security", icon: ShieldCheckIcon },
]
