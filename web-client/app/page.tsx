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

import Logo from '@/app/ui/logo';
import Link from 'next/link';
import Image from 'next/image';
// import { Metadata } from 'next';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import UserIcon from '@heroicons/react/24/outline/UserIcon';

// export const metadata: Metadata = {
//   title: 'Dashboard',
// };

export default function Page() {
  return (
    <main className="flex min-h-screen flex-col p-6">
      <header className="grid grid-cols-3 items-center w-screen h-8 text-white bg-blue-500">
        <p className="col-start-2 justify-self-center">Evidence Engine</p>
        <UserIcon className="justify-self-end size-4" />
      </header>
      <div className="mt-4 flex grow flex-col gap-4 md:flex-row">
        <div className="flex flex-col justify-center items-center gap-6 rounded-lg bg-gray-50 px-6 py-10 md:min-w-1/4 md:px-20">
          {/* <p className={`${lusitana.className} antialiased text-xl text-center text-gray-800 md:text-3xl md:leading-normal`}> */}
          <p className="antialiased italic text-xl text-center text-gray-800 md:text-3xl md:leading-normal">
            <strong>Welcome to the</strong>
          </p>
          <Logo />
          from&nbsp;<a href="https://campaign-resources.org" target="_blank"><i>Campaign Resources</i></a>
          <Image src="/cr-logo-favicon.svg" width="32" height="32" alt="Campaign Resources favicon" />
          <Link
            className="justify-self-center flex items-center gap-5 rounded-lg bg-blue-500 px-6 py-3 text-sm font-medium text-white transition-colors hover:bg-blue-400 md:text-base"
            href="/dashboard"
          >
            <span>Explore</span>
          </Link>
        </div>
        <div>
          <div className="flex items-center justify-center p-6 md:w-5/6 md:px-28 md:py-12">
            <Image
              src="/hero-desktop.png"
              width={1000}
              height={760}
              className="hidden md:block"
              alt="Screenshots of the Evidence Engine showing desktop version"
            />
            {/* <Image
              src="/hero-mobile.png"
              width={560}
              height={620}
              className="block md:hidden"
              alt="Screenshots of the Evidence Engine showing desktop version"
            /> */}
          </div>
          <div className="flex gap-8 rounded-lg bg-gray-50 px-6 py-10 md:min-w-1/6 md:px-20">
            <Card className="size-1/3 aspect-square flex-none bg-red-50 shadow-red-200 shadow-xl">
              <CardHeader className="justify-center">
                <CardTitle>The Challenge</CardTitle>
                {/* <CardDescription>
                  <p>Card description</p>
                </CardDescription> */}
              </CardHeader>
              <CardContent className="grid grid-cols-1 gap-2 overflow-y-auto">
                <p>Mainstream narratives on key topics are distorted by powerful vested interests. Scientists, researchers, commentators, journalists and public figures contradicting the official story are vilified, cancelled, defunded, suspended or fired.</p>
                <p>Key human endeavours such as scientific research, education, policy making, regulation, journalism are captured and controlled by these vested interests.</p>
                <p>Consequently, truth, health and freedom are under threat on a global scale as never before.</p>
              </CardContent>
              {/* <CardFooter>
                <p>Card Footer</p>
              </CardFooter> */}
            </Card>
            <Card className="size-1/3 aspect-square flex-none bg-green-50 shadow-green-200 shadow-xl">
              <CardHeader className="justify-center">
                <CardTitle>Mission Statement</CardTitle>
              </CardHeader>
              <CardContent className="grid grid-cols-1 gap-2 overflow-y-auto">
                <p>Refute anti-truth, anti-human, anti-freedom mainstream narratives.</p>
                <p>Focus on truth, health, justice and freedom.</p>
                <p>Curate high-quality contrarian scientific and other evidence, making it readily accessible to researchers and activists.</p>
              </CardContent>
            </Card>
            <Card className="size-1/3 aspect-square flex-none bg-blue-50 shadow-blue-200 shadow-xl">
              <CardHeader className="justify-center">
                <CardTitle>Description</CardTitle>
              </CardHeader>
              <CardContent className="grid grid-cols-1 gap-2 overflow-y-auto">
                <p>This online database details scientific claims, publications, declarations and quotations from leading scientists, organised in an extensible hierarchy of topics.</p>
                <p>Information in these categories is presented in searchable, sortable, pageable lists that can be exported to various formats.</p>
                <p>The lists can be linked in various ways to show the relationships between records in each information category.</p>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
      <div className="grow"></div>
      <footer className="grid grid-cols-3 items-center w-screen h-8 text-xs text-white bg-blue-500">
        <p>&nbsp;Copyright &copy; 2024-25 Adrian Price. All rights reserved.</p>
        <p className="justify-self-center">A <a href="https://campaign-resources.org" target="_blank" className="text-white"><i>Campaign Resources</i></a> application</p>
      </footer>
    </main>
  );
}
