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

import Logo from '@/app/ui/logo'
import Link from 'next/link'
import Image from 'next/image'
// import { Metadata } from 'next';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import Autoplay from "embla-carousel-autoplay"
import { Carousel, CarouselApi, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import { GithubIcon, UserIcon, } from 'lucide-react'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { useEffect, useState } from 'react'

// export const metadata: Metadata = {
//   title: 'Dashboard',
// };

export default function Page() {
  const [api, setApi] = useState<CarouselApi>()
  const [item, setItem] = useState(0)

  // Bidirectionally sync carousel scroll state with item state
  useEffect(() => {
    if (!api)
      return
    const onSelect = () => setItem(api.selectedScrollSnap())
    api.on("select", onSelect)
    return () => {
      api.off("select", onSelect)
    }
  }, [api])

  useEffect(() => {
    api?.scrollTo(item)
  }, [api, item])

  return (
    <div className="flex flex-col">
      <header className="fixed top-0 left-0 right-0 z-10 grid grid-cols-3 items-center shrink-0 w-full h-16 text-white bg-blue-500">
        <div></div>
        <p className="justify-self-center"><b>The Evidence Engine</b></p>
        <div className="justify-self-end flex items-center mr-2">
          <UserIcon className="justify-self-end mr-2 size-6" />
          <a href="https://github.com/demonfiddler/evidence-engine" target="_blank" title="Source code on GitHub"><GithubIcon className="justify-self-end inline size-6 text-white"/></a>
        </div>
      </header>
      <footer className="fixed bottom-0 left-0 right-0 z-10 grid grid-cols-3 items-center shrink-0 w-full h-12 text-xs text-white bg-blue-500">
        <p>&nbsp;Copyright &copy; 2024-25 Adrian Price. All rights reserved.</p>
        <p className="justify-self-center">A <a href="https://campaign-resources.org" target="_blank" className="text-white"><i>Campaign Resources</i></a> application</p>
      </footer>
      <div className="fixed top-16 bottom-12 flex gap-4">
        <div className="flex flex-col justify-center items-center gap-6 rounded-lg bg-gray-50 px-6 py-10 md:min-w-1/4 md:px-20">
          <p className="antialiased italic text-xl text-center text-gray-800 md:text-3xl md:leading-normal">
            <strong>Welcome to the</strong>
          </p>
          <Logo />
          from&nbsp;<a href="https://campaign-resources.org" target="_blank">
            <Image src="/cr-logo.svg" width="132" height="132" alt="Campaign Resources logo" />
          </a>
          <Link
            className="justify-self-center flex items-center gap-5 rounded-lg bg-blue-500 px-6 py-3 text-sm font-medium text-white transition-colors hover:bg-blue-400 md:text-base"
            href="/dashboard"
          >
            <span>Explore</span>
          </Link>
        </div>
        <div className="flex flex-col items-center h-full">
          <div className="flex items-center justify-center p-6 md:w-5/6 md:px-28 md:py-12">
            <Image
              src="/hero-desktop.png"
              width={1000}
              height={760}
              className="hidden md:block shadow-xl"
              alt="Screenshots of the Evidence Engine showing desktop version"
            />
            {/* <Image
              src="/hero-mobile.png"
              width={560}
              height={620}
              className="block md:hidden"
              alt="Screenshots of the Evidence Engine showing mobile version"
            /> */}
          </div>
          <Carousel
            className="w-2/3"
            plugins={[
              Autoplay({
                delay: 8000,
              }),
            ]}
            opts={{
              loop: true
            }}
            setApi={setApi}
          >
            <CarouselContent>
              <CarouselItem>
                <Card className="size-full bg-red-50 shadow-red-200 shadow-xl transition-transform duration-300 ease-in-out transform hover:scale-105">
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
              </CarouselItem>
              <CarouselItem>
                <Card className="size-full bg-green-50 shadow-green-200 shadow-xl transition-transform duration-300 ease-in-out transform hover:scale-105">
                  <CardHeader className="justify-center">
                    <CardTitle>Mission Statement</CardTitle>
                  </CardHeader>
                  <CardContent className="grid grid-cols-1 gap-2 overflow-y-auto">
                    <p><b>Refute</b> anti-truth, anti-human, anti-freedom mainstream narratives.</p>
                    <p><b>Focus</b> on truth, health, justice and freedom.</p>
                    <p><b>Curate</b> high-quality contrarian scientific and other evidence, making it readily accessible to researchers and activists.</p>
                  </CardContent>
                </Card>
              </CarouselItem>
              <CarouselItem>
                <Card className="size-full bg-blue-50 shadow-blue-200 shadow-xl transition-transform duration-300 ease-in-out transform hover:scale-105">
                  <CardHeader className="justify-center">
                    <CardTitle>Description</CardTitle>
                  </CardHeader>
                  <CardContent className="grid grid-cols-1 gap-2 overflow-y-auto">
                    <p>This online database details scientific claims, publications, declarations and quotations from leading scientists, organised within an extensible topic hierarchy.</p>
                    <p>Information in these categories is presented in searchable, sortable, pageable lists that can be exported to various formats.</p>
                    <p>The lists can be linked in various ways to show the relationships between records in each information category.</p>
                  </CardContent>
                </Card>
              </CarouselItem>
            </CarouselContent>
            <CarouselPrevious />
            <CarouselNext />
          </Carousel>
          <RadioGroup
            className="flex mt-2"
            value={item.toString()}
            onValueChange={(value) => setItem(Number.parseInt(value))}
          >
            <RadioGroupItem value="0" title="Challenge"></RadioGroupItem>
            <RadioGroupItem value="1" title="Mission"></RadioGroupItem>
            <RadioGroupItem value="2" title="Description"></RadioGroupItem>
          </RadioGroup>
        </div>
      </div>
    </div>
  );
}
