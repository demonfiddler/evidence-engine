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

'use client'

import {
  CircleQuestionMarkIcon,
  GithubIcon,
  LogInIcon,
  LogOutIcon,
  PanelRightCloseIcon,
  SettingsIcon,
  ShieldAlertIcon,
  UserIcon,
  UserPenIcon
} from "lucide-react"
import Image from "next/image"
import Link from "next/link"

export default function LayoutDocPage() {
  return (
    <article className="prose">
      <h2>Screen Layout</h2>
      <Image src="/doc/layout.png" alt="Screen-shot with annotated layout" width={640} height={400} />
      <h3>Address Bar</h3>
      The browser's address bar shows the URL (Uniform Resource Locator) of the current page, including any filter settings.
      You can bookmark and share the page URL to enable others to view the information in the same way.
      <h3>Header</h3>
      The page header contains several items:
      <h4><PanelRightCloseIcon className="inline" />&nbsp;Sidebar Toggle</h4>
      This button controls the visibility of the Navigator (side bar).
      <h4><LogInIcon className="inline" />&nbsp;Sign In / Username</h4>
      This button triggers the 'Sign-in' dialog, or if you are already signed in, displays your username.
      <h4><UserIcon className="inline" />&nbsp;My Account</h4>
      This icon triggers the 'My Account' menu, containing the following items:
      <Image src="/doc/my-account-menu.png" alt="Screen-shot of the My Account drop-down menu" width={238} height={234} />
      <dl>
        <dt><UserPenIcon className="inline" />&nbsp;Profile...</dt>
        <dd>Displays the <Link href="/doc/profile/">Profile Dialog</Link></dd>
        <dt><ShieldAlertIcon className="inline" />&nbsp;Change Password...</dt>
        <dd>Displays the <Link href="/doc/password/">Password Dialog</Link></dd>
        <dt><SettingsIcon className="inline" />&nbsp;Settings...</dt>
        <dd>Displays the <Link href="/doc/settings/">Settings Dialog</Link></dd>
        <dt><LogOutIcon className="inline" />&nbsp;Sign out</dt>
        <dd>Signs out of the system and continues unauthenticated</dd>
      </dl>
      <h4><GithubIcon className="inline" />&nbsp;GitHub Source Link</h4>
      This link displays the GitHub Evidence Engine project page containing the source code.
      <h3>Footer</h3>
      The footer contains a copyright message, a link to the parent Campaign Resources website, and the Evidence Engine version.
      <h3>Navigator</h3>
      The Navigator presents a list of clickable links to pages within the Evidence Engine application.
      <h4>Home Link</h4>
      The Evidence Engine logo, linked to the Home page.
      <h4>Page Links</h4>
      Links to various pages in the application.
      <h4>Campaign Resources Link</h4>
      A link to the parent Campaign Resources website.
      <h3>Page Title</h3>
      <p>Every page has a title at the top ('Claims' in the example shown), followed by a{" "}
        <CircleQuestionMarkIcon className="inline w-6 h-6 text-green-600" /> context help icon button. Clicking the
        button opens the documentation for the current page in another browser tab that is reused for subsequent help
        requests.</p>
      <h3>Master Filter</h3>
      <p>The master filter section is described <Link href="/doc/filter/">here</Link>.</p>
      <h3>Table</h3>
      <p>The table section is described <Link href="/doc/tables/">here</Link>.</p>
      <h3>Details</h3>
      <p>The details section is described <Link href="/doc/details/">here</Link>.</p>
      <p></p>
    </article>
  )
}