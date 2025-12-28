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
  BookOpenCheckIcon,
  BookOpenTextIcon,
  Building2Icon,
  ChartColumnIcon,
  CircleQuestionMarkIcon,
  DatabaseBackupIcon,
  DatabaseZapIcon,
  FileClockIcon,
  FolderTreeIcon,
  HouseIcon,
  InfoIcon,
  LibraryIcon,
  LockIcon,
  MessagesSquareIcon,
  NewspaperIcon,
  ScrollTextIcon,
  ShieldCheckIcon,
  TableOfContentsIcon,
  UserIcon
} from 'lucide-react'
import { MessageSquareQuoteIconEx } from '../icons'
import { AuthorityKind } from '@/app/model/schema'

export const categories = [
  {
    label: "General",
    icon: TableOfContentsIcon,
    items: [
      { label: "Home", href: "/", icon: HouseIcon },
      { label: "Dashboard", href: "/dashboard", icon: ChartColumnIcon },
      { label: "Comments", href: "/comments", icon: MessagesSquareIcon },
      { label: "Log", href: "/log", icon: FileClockIcon },
      { label: "Help", href: "/help", icon: CircleQuestionMarkIcon },
      { label: "About", href: "/about", icon: InfoIcon },
    ],
  }, {
    label: "Records",
    icon: DatabaseZapIcon,
    items: [
      { label: "Claims", href: "/claims", icon: BookOpenCheckIcon },
      { label: "Declarations", href: "/declarations", icon: ScrollTextIcon },
      { label: "Persons", href: "/persons", icon: UserIcon },
      { label: "Publications", href: "/publications", icon: LibraryIcon },
      { label: "Quotations", href: "/quotations", icon: MessageSquareQuoteIconEx },
      { label: "Topics", href: "/topics", icon: FolderTreeIcon },
    ],
  }, {
    label: "Sources",
    icon: BookOpenTextIcon,
    items: [
      { label: "Journals", href: "/journals", icon: NewspaperIcon },
      { label: "Publishers", href: "/publishers", icon: Building2Icon },
    ],
  }, {
    label: "Administration",
    icon: LockIcon,
    authority: 'ADM' as AuthorityKind,
    items: [
      { label: "Security", href: "/admin/security", icon: ShieldCheckIcon },
      { label: "Backup", href: "/admin/backup", icon: DatabaseBackupIcon },
    ],
  },
]