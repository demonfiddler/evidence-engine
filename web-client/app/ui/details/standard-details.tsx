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

import ILinkableEntity from "@/app/model/ILinkableEntity";
import TrackingDetails from "./tracking-details";
import LinkingDetails from "./linking-details";
import { useContext } from "react";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible";
import { Button } from "@/components/ui/button";
import ITrackedEntity from "@/app/model/ITrackedEntity";
import { ChevronDownIcon, ChevronUpIcon } from "@heroicons/react/24/outline";
import RecordKind from "@/app/model/RecordKind";
import { DetailState } from "./detail-actions";
import { GlobalContext } from "@/lib/context";

export default function StandardDetails(
  {recordKind, record, state, showLinkingDetails}:
  {recordKind: RecordKind, record: ITrackedEntity | undefined, state: DetailState, showLinkingDetails: boolean}
) {

  const {trackingDetailsOpen, setTrackingDetailsOpen} = useContext(GlobalContext)

  return (
    <Collapsible
      open={trackingDetailsOpen}
      onOpenChange={setTrackingDetailsOpen}
      className="ml-2 mr-2 space-y-2"
    >
      <span>
        { showLinkingDetails ? "Tracking & Linking" : "Tracking"}
      </span>
      <CollapsibleTrigger asChild>
        <Button variant="ghost" size="sm">
          {
            trackingDetailsOpen
            ? <ChevronUpIcon className="h-4 w-4" />
            : <ChevronDownIcon className="h-4 w-4" />
          }
          <span className="sr-only">Toggle</span>
        </Button>
      </CollapsibleTrigger>
      <CollapsibleContent className="flex flex-col w-full space-y-2">
        <TrackingDetails recordKind={recordKind} record={record} state={state} />
        {
          showLinkingDetails
          ? <>
              <hr />
              <LinkingDetails recordKind={recordKind} record={record as ILinkableEntity} state={state} />
            </>
          : <></>
        }
      </CollapsibleContent>
      <hr className="border-1" />
    </Collapsible>
  )
}