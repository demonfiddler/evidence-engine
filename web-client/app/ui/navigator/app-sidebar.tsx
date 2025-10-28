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

import Link from 'next/link'
import Logo from '@/app/ui/logo'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
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
import useAuth from '@/hooks/use-auth'
import { component, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(component, "[AppSidebar] ")

const appItems = [
  { title: 'Home', url: '/', icon: HomeIcon },
  { title: 'Dashboard', url: '/dashboard', icon: ChartBarIcon },
  { title: 'Claims', url: '/claims', icon: ExclamationCircleIcon },
  { title: 'Publications', url: '/publications', icon: BeakerIcon },
  { title: 'Persons', url: '/persons', icon: UserIcon },
  { title: 'Declarations', url: '/declarations', icon: EnvelopeOpenIcon },
  { title: 'Quotations', url: '/quotations', icon: ChatBubbleBottomCenterTextIcon },
  { title: 'Comments', url: '/comments', icon: ChatBubbleLeftRightIcon },
  { title: 'Log', url: '/log', icon: ListBulletIcon },
  { title: 'Help', url: '/help', icon: QuestionMarkCircleIcon },
  { title: 'About', url: '/about', icon: InformationCircleIcon },
];
const adminItems = [
  { title: 'Topics', url: '/admin/topics', icon: Bars3BottomRightIcon },
  { title: 'Journals', url: '/admin/journals', icon: NewspaperIcon },
  { title: 'Publishers', url: '/admin/publishers', icon: BuildingOfficeIcon },
  { title: 'Security', url: '/admin/security', icon: ShieldCheckIcon },
];

export function AppSidebar() {
  logger.debug("render")

  const {hasAuthority} = useAuth()
  const allowAdmin = hasAuthority("ADM")

  return (
    <Sidebar>
      <SidebarHeader>
        <Link
          className="mb-2 flex items-end justify-start rounded-md bg-blue-500 p-4 h-20 md:h-40"
          href="/"
          title="Navigate to the Home page"
        >
          <div className="text-white">
            <Logo />
          </div>
        </Link>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Application</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {appItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <Link href={item.url} title={`Navigate to the ${item.title} page`}>
                      <item.icon />
                      <span>{item.title}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
        {
          allowAdmin ?
          <SidebarGroup>
            <SidebarGroupLabel>Adminstration</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {adminItems.map((item) => (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton asChild>
                      <Link href={item.url} title={`Navigate to the ${item.title} page`}>
                        <item.icon />
                        <span>{item.title}</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
          : <></>
        }
        </SidebarContent>
      <SidebarFooter />
    </Sidebar>
  )
}

AppSidebar.whyDidYouRender = true