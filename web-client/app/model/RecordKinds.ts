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

import { BookOpenCheckIcon, Building2Icon, CircleSlash2Icon, FileClockIcon, FolderTreeIcon, LibraryIcon, LinkIcon, LucideProps, MessageSquareQuoteIcon, MessagesSquareIcon, NewspaperIcon, ScrollIcon, UserIcon, UsersIcon } from "lucide-react";
// import { MessageSquareQuoteIconEx } from "../ui/icons";
import { ForwardRefExoticComponent, JSX, RefAttributes } from "react";

// Utility type: ensures A is assignable to B
type AssertSubset<A extends B, B> = true;

// Compile-time check: LinkableEntityKind ⊆ TrackedEntityKind
type _check1 = AssertSubset<LinkableEntityKind, TrackedEntityKind>;

// Compile-time check: TrackedEntityKind ⊆ RecordKind
type _check2 = AssertSubset<TrackedEntityKind, RecordKind>;

export const LinkableEntityKindKeys = ["Claim", "Declaration", "Person", "Publication", "Quotation", "Topic"] as const
export type LinkableEntityKind = typeof LinkableEntityKindKeys[number]

export const TrackedEntityKindKeys = [...LinkableEntityKindKeys, "Comment", "RecordLink", "Group", "Journal", "Publisher", "User"] as const
export type TrackedEntityKind = typeof TrackedEntityKindKeys[number]

export const RecordKindKeys = [...TrackedEntityKindKeys, "None", "Log"] as const
export type RecordKind = typeof RecordKindKeys[number]

export const RecordIcons : {[K in RecordKind]: ForwardRefExoticComponent<Omit<LucideProps, "ref"> & RefAttributes<SVGSVGElement>>} = {
  Claim: BookOpenCheckIcon, //
  Declaration: ScrollIcon, //
  Person: UserIcon, //
  Publication: LibraryIcon, //
  Quotation: MessageSquareQuoteIcon, // MessageSquareQuoteIconEx,
  Topic: FolderTreeIcon, //
  Comment: MessagesSquareIcon, //
  RecordLink: LinkIcon, //
  Group: UsersIcon, //
  Journal: NewspaperIcon, //
  Publisher: Building2Icon, //
  User: UserIcon, //
  None: CircleSlash2Icon, //
  Log: FileClockIcon,
}