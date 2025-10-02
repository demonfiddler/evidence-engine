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

'use client';

import {
  HomeIcon,
  ArrowTrendingUpIcon,
  ExclamationCircleIcon,
  EnvelopeOpenIcon,
  AcademicCapIcon,
  BeakerIcon,
  ChatBubbleBottomCenterTextIcon,
  Bars3BottomRightIcon,
  NewspaperIcon,
  BuildingOfficeIcon,
  UserIcon,
  QuestionMarkCircleIcon,
  InformationCircleIcon,
} from '@heroicons/react/24/outline';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import clsx from 'clsx'

const links = [
  { name: 'Home', href: '/', icon: HomeIcon },
  { name: 'Dashboard', href: '/dashboard', icon: ArrowTrendingUpIcon },
  { name: 'Claims', href: '/claims', icon: ExclamationCircleIcon },
  { name: 'Science', href: '/publications', icon: BeakerIcon },
  { name: 'Scientists', href: '/persons', icon: AcademicCapIcon },
  { name: 'Declarations', href: '/declarations', icon: EnvelopeOpenIcon },
  { name: 'Quotations', href: '/quotations', icon: ChatBubbleBottomCenterTextIcon },
  { name: 'Help', href: '/help', icon: QuestionMarkCircleIcon },
  { name: 'About', href: '/about', icon: InformationCircleIcon },
];
const adminLinks = [
  { name: 'Topics', href: '/admin/topics', icon: Bars3BottomRightIcon },
  { name: 'Journals', href: '/admin/journals', icon: NewspaperIcon },
  { name: 'Publishers', href: '/admin/publishers', icon: BuildingOfficeIcon },
  { name: 'Security', href: '/admin/security', icon: UserIcon },
];

export default function NavLinks() {
  const pathname = usePathname();

  return (
    <>
      {links.map((link) => {
        const LinkIcon = link.icon;
        return (
          <Link
            key={link.name}
            href={link.href}
            className={clsx(
              'flex h-[48px] grow items-center justify-center gap-2 rounded-md bg-gray-50 p-3 text-sm font-medium hover:bg-sky-100 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
              {
                'bg-sky-100 text-blue-600': pathname === link.href,
              },
            )}
          >
            <LinkIcon className="w-6" />
            <p className="hidden md:block">{link.name}</p>
          </Link>
        );
      })}
      {/* TODO: Consider using ShadCN Sidebar instead */}
      <Collapsible>
        <CollapsibleTrigger>Administration</CollapsibleTrigger>
        <CollapsibleContent>
        {adminLinks.map((link) => {
            const LinkIcon = link.icon;
            return (
              <Link
                key={link.name}
                href={link.href}
                className={clsx(
                  'flex h-[48px] grow items-center justify-center gap-2 rounded-md bg-gray-50 p-3 text-sm font-medium hover:bg-sky-100 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
                  {
                    'bg-sky-100 text-blue-600': pathname === link.href,
                  },
                )}
              >
                <LinkIcon className="w-6" />
                <p className="hidden md:block">{link.name}</p>
              </Link>
            );
          })}
        </CollapsibleContent>
      </Collapsible>
    </>
  )
}

NavLinks.whyDidYouRender = true