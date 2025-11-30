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

import {SVGProps} from "react"

export function SchoolTeacherIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      width="100%"
      height="100%"
      strokeWidth="1.5"
      stroke="currentColor"
      aria-hidden="true"
      data-slot="icon"
      {...props}
    >
      <path
        fill="none"
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M2.25,3.75c0,1.646 1.354,3 3,3c1.646,0 3,-1.354 3,-3c0,-1.646 -1.354,-3 -3,-3c-1.646,-0 -3,1.354 -3,3m5.25,19.5l0.75,-7.5l1.5,0l0,-3c0,-2.469 -2.031,-4.5 -4.5,-4.5c-2.469,0 -4.5,2.031 -4.5,4.5l0,3l1.5,0l0.75,7.5l4.5,0Zm5.25,-6.75l9,0c0.823,0 1.5,-0.677 1.5,-1.5l0,-12.75c0,-0.823 -0.677,-1.5 -1.5,-1.5l-10.5,0"
      />
    </svg>
  )
}

export function NobelPrizeIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      height="100%"
      width="100%"
      strokeWidth="1.5"
      aria-hidden="true"
      data-slot="icon"
      {...props}
    >
      <g>
        <path d="M12,0c-6.617,0 -12,5.383 -12,12c0,6.617 5.383,12 12,12c6.617,-0 12,-5.383 12,-12c-0,-6.617 -5.383,-12 -12,-12Zm-10.179,12c-0,-5.613 4.566,-10.179 10.179,-10.179c5.613,-0 10.179,4.566 10.179,10.179c0,1.893 -0.521,3.665 -1.424,5.185l-4.397,-4.398c1.586,-2.203 1.389,-5.302 -0.591,-7.283c-2.2,-2.199 -5.78,-2.2 -7.98,0c-2.2,2.2 -2.2,5.779 -0.001,7.979c0,-0 0.001,0 0.001,0.001l1.247,1.247l-4.343,4.343c-1.775,-1.833 -2.87,-4.327 -2.87,-7.074Zm10.179,10.179c-2.21,0 -4.257,-0.71 -5.927,-1.911l4.893,-4.893c0.222,-0.222 0.305,-0.531 0.25,-0.818c-0.034,-0.172 -0.117,-0.336 -0.25,-0.469l-1.891,-1.892c-0,0 -0.001,0 -0.001,0c-1.49,-1.49 -1.489,-3.914 0.001,-5.404c1.49,-1.49 3.914,-1.49 5.404,-0c1.49,1.49 1.49,3.914 0,5.405c-0.355,0.355 -0.355,0.932 0,1.287l5.181,5.181c0.004,0.004 0.009,0.008 0.013,0.012c-1.867,2.144 -4.614,3.502 -7.673,3.502Z" />
        <path d="M6.556,15.642c0.502,-0 0.91,-0.408 0.91,-0.911c0,-0.502 -0.408,-0.91 -0.91,-0.91l-2.268,-0c-0.503,-0 -0.911,0.408 -0.911,0.91c0,0.503 0.408,0.911 0.911,0.911l2.268,-0Z" />
        <path d="M18.475,13.751l1.991,-0c0.503,-0 0.911,-0.408 0.911,-0.911c-0,-0.503 -0.408,-0.91 -0.911,-0.91l-1.991,-0c-0.503,-0 -0.911,0.407 -0.911,0.91c0,0.503 0.408,0.911 0.911,0.911Z" />
        <path d="M18.817,10.837l1.792,0c0.503,0 0.911,-0.407 0.911,-0.91c-0,-0.503 -0.408,-0.91 -0.911,-0.91l-1.792,-0c-0.503,-0 -0.91,0.407 -0.91,0.91c-0,0.503 0.407,0.91 0.91,0.91Z" />
      </g>
    </svg>
  )
}

export function TickIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width="100%"
      height="100%"
      {...props}
    >
      <path
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.5"
        d="m5 14l3.5 3.5L19 6.5"
        color="currentColor"
      ></path>
    </svg>
  )
}

export function SearchIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width="100%"
      height="100%"
      {...props}
    >
      <g
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.5"
      >
        <circle cx="11" cy="11" r="8" />
        <path d="m21 21l-4.3-4.3" />
      </g>
    </svg>
  )
}

export function ArrowLeftStartIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width="100%"
      height="100%"
      {...props}
    >
      <g
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.5"
      >
        <path d="M12,19.5l-7.5,-7.5m0,0l7.5,-7.5m-7.5,7.5l18,0"/>
        <path d="M1.5,4.5l0.016,15"/>
      </g>
    </svg>
  )
}

export function ArrowRightEndIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width="100%"
      height="100%"
      {...props}
    >
      <g
        fill="none"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.5"
      >
        <path d="M12,4.5l7.5,7.5m0,0l-7.5,7.5m7.5,-7.5l-18,0" />
        <path d="M22.5,4.5l-0,15" />
      </g>
    </svg>
  )
}

/**
 * A transformed version of lucide-react's MessageSquareQuoteIcon. For some strange reason the Lucide version uses
 * closing quotes in a speech bubble; this one uses opening quotes.
 */
export function MessageSquareQuoteIconEx(props: SVGProps<SVGSVGElement>) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      >
        <g transform="matrix(-1,0,0,1,30,0)">
          <path d="M14 14a2 2 0 0 0 2-2V8h-2"/>
        </g>
        <path d="M22 17a2 2 0 0 1-2 2H6.828a2 2 0 0 0-1.414.586l-2.202 2.202A.71.71 0 0 1 2 21.286V5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2z"/>
        <g transform="matrix(-1,0,0,1,18,0)">
          <path d="M8 14a2 2 0 0 0 2-2V8H8"/>
        </g>
    </svg>
  )
}
