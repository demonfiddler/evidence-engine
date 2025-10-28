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

import Publication from "@/app/model/Publication"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { cn, formatDate } from "@/lib/utils"
import { CalendarIcon } from "@heroicons/react/24/outline"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select"
import rawJournals from "@/data/journals.json" assert {type: 'json'}
import rawPublicationKinds from "@/data/publication-kinds.json" assert {type: 'json'}
import Journal from "@/app/model/Journal"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction, useState } from "react"
import { useFormContext } from "react-hook-form"
import { PublicationFieldValues } from "../validators/publication"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import SelectTriggerEx from "../ext/select-ex"
import TextareaEx from "../ext/textarea-ex"
import ButtonEx from "../ext/button-ex"
import LinkEx from "../ext/link-ex"
import CheckboxEx from "../ext/checkbox-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"

const logger = new LoggerEx(detail, "[PublicationDetails] ")

type PublicationKind = {
  kind: string
  label: string
}
const journals = rawJournals.content as unknown as Journal[]
const publicationKinds = rawPublicationKinds as unknown as PublicationKind[]

export default function PublicationDetails(
  {
    record,
    state,
    setMode,
    onFormAction
  } : {
    record?: Publication
    state: DetailState
    setMode: Dispatch<SetStateAction<DetailMode>>
    onFormAction: FormActionHandler<PublicationFieldValues>
  }) {
  logger.debug("render")

  const form = useFormContext<PublicationFieldValues>()
  const [dateOpen, setDateOpen] = useState(false)
  const [accessedOpen, setAccessedOpen] = useState(false)
  const { updating } = state

  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails recordKind="Publication" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-lg pt-2 pb-4">
              &nbsp;&nbsp;{state.mode == "create"
                ? "Details for new Publication"
                : record
                  ? `Details for selected Publication #${record?.id}`
                  : "-Select a Publication in the list above to see its details-"
            }</span>
          </FormDescription>
          <div className="grid grid-cols-4 ml-2 mr-2 mt-4 mb-4 gap-4 items-start">
            <FormField
              control={form.control}
              name="rating"
              render={({field}) => (
                <FormItem>
                  <FormLabel id="rating-label">Rating</FormLabel>
                  <FormControl>
                    <StarRatingBasicEx
                      id="rating"
                      ariaLabelledby="rating-label"
                      readOnly={!updating}
                      maxStars={5}
                      iconSize={18}
                      className="ml-2 w-full"
                      value={field.value ?? 0}
                      onChange={field.onChange}
                      help="A five-star rating for the publication, indicative of impact, quality, credibility, citations, etc."
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="peerReviewed"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="peerReviewed">Peer reviewed</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="peerReviewed"
                      className="col-span-1"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the publication was peer-reviewed"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="cached"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="cached">Cached</FormLabel>
                  <FormControl>
                    <CheckboxEx
                      id="cached"
                      disabled={!updating}
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      help="Whether the publication content is cached on this server"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DetailActions
              className="col-start-4 row-span-14"
              recordKind="Publication"
              record={record}
              form={form}
              state={state}
              setMode={setMode}
              onFormAction={onFormAction}
            />
            <FormField
              control={form.control}
              name="title"
              render={({field}) => (
                <FormItem className="col-span-3">
                  <FormLabel htmlFor="title">Title</FormLabel>
                  <FormControl>
                    <InputEx
                      id="title"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The publication name/title"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="kind"
              render={({ field }) => (
                <FormItem className="col-start-1">
                  <FormLabel htmlFor="kind">Kind</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="kind"
                        className="w-full"
                        help="The kind of publication (corresponds to the RIS 'TY' field)"
                      >
                        <SelectValue placeholder="Specify kind" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        publicationKinds.map(kind =>
                          <SelectItem
                            key={kind.kind?.toString() ?? ''}
                            value={kind.kind?.toString() ?? ''}>
                            {kind.label}
                          </SelectItem>)
                      }
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="journalId"
              render={({ field }) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="journal">Journal</FormLabel>
                  <Select
                    disabled={!updating}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <FormControl>
                      <SelectTriggerEx
                        id="journal"
                        className="w-full"
                        help="The journal or series containing the publication">
                        <SelectValue className="w-full" placeholder="Specify journal" />
                      </SelectTriggerEx>
                    </FormControl>
                    <SelectContent>
                      {
                        journals.map(journal => (
                          <SelectItem
                            key={journal.id?.toString() ?? ''}
                            value={journal.id?.toString() ?? ''}>
                            {journal.title}
                          </SelectItem>
                        ))
                      }
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="authors"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="authors">Authors</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="authors"
                      className=" h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The author(s) of the publication, one per line"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="date"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="date">Publication date</FormLabel>
                  <Popover open={dateOpen} onOpenChange={setDateOpen}>
                    <PopoverTrigger id="date" asChild>
                      <FormControl>
                        <ButtonEx
                          variant={"outline"}
                          disabled={!updating}
                          className={cn("grow justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}
                          help="The date on which the publication was first published"
                        >
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value)
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </ButtonEx>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        captionLayout="dropdown"
                        weekStartsOn={1}
                        selected={field.value}
                        onSelect={(e) => {
                          setDateOpen(false)
                          field.onChange(e)
                        }}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="year"
              render={({ field }) => (
                <FormItem>
                  <FormLabel htmlFor="year">Publication year</FormLabel>
                  <FormControl>
                    <InputEx
                      id="year"
                      type="number"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="The year in which the publication was first published"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="abstract"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-3">
                  <FormLabel htmlFor="abstract">Abstract</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="abstract"
                      className="h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Concise summary of the publication"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="notes"
              render={({field}) => (
                <FormItem className="col-start-1 col-span-3">
                  <FormLabel htmlFor="notes">Notes</FormLabel>
                  <FormControl>
                    <TextareaEx
                      id="notes"
                      className="col-span-4 h-40 overflow-y-auto"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Contributor notes about the publication"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <hr className="col-span-3" />
            <p className="col-span-3">Online index identifiers</p>
            <FormField
              control={form.control}
              name="url"
              render={({field}) => (
                <FormItem className="col-span-2">
                  <FormLabel htmlFor="url">URL</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                        id="url"
                        type="url"
                        {...field}
                        help="The online web address"
                      />
                      : <LinkEx
                        id="url"
                        href={record?.url ?? ''}
                        target="_blank"
                        help="The online web address"
                      >
                        {record?.url ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="accessed"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="accessed">Accessed</FormLabel>
                  <Popover open={accessedOpen} onOpenChange={setAccessedOpen}>
                    <PopoverTrigger id="accessed" asChild>
                      <FormControl>
                        <ButtonEx
                          className={cn("grow justify-start text-left font-normal",
                            (!record || !record.date) && "text-muted-foreground")}
                          variant={"outline"}
                          disabled={!updating}
                          help="The date the publication was last accessed by contributor"
                        >
                          <CalendarIcon />
                          {field.value ? (
                            formatDate(field.value)
                          ) : (
                            <span>Pick a date</span>
                          )}
                        </ButtonEx>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        captionLayout="dropdown"
                        weekStartsOn={1}
                        selected={field.value}
                        onSelect={(e) => {
                          setAccessedOpen(false)
                          field.onChange(e)
                        }}
                      />
                    </PopoverContent>
                  </Popover>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="doi"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="doi">DOI</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="doi"
                          {...field}
                          help="The Digital Object Identifier (DOI)"
                      />
                      : <LinkEx
                          href={record?.doi ? `https://doi.org/${record?.doi ?? ''}` : ''}
                          target="_blank"
                          help="The Digital Object Identifier (DOI)"
                      >
                        {record?.doi ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="isbn"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="isbn">ISBN</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="isbn"
                          {...field}
                          help="The International Standard Book Number (ISBN)"
                      />
                      : <LinkEx
                          id="isbn"
                          href={record?.isbn ? `https://isbnsearch.org/isbn/${record?.isbn ?? ''}` : ''}
                          target="_blank"
                          help="The International Standard Book Number (ISBN)"
                      >
                        {record?.isbn ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="pmid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="pmid">PubMed ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="pmid"
                          {...field}
                          help="The U.S. National Library of Medicine PubMed ID"
                      />
                      : <LinkEx
                          id="pmid"
                          href={record?.pmid ? `https://pubmed.ncbi.nlm.nih.gov/${record?.pmid ?? ''}` : ''}
                          target="_blank"
                          help="The U.S. National Library of Medicine PubMed ID"
                      >
                        {record?.pmid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="hsid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="hsid">Handle System ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="hsid"
                          {...field}
                          help="The Corporation for National Research Initiatives Handle System ID"
                      />
                      : <LinkEx
                          id="hsid"
                          href={record?.hsid ? `${record?.hsid ?? ''}` : ''}
                          target="_blank"
                          help="The Corporation for National Research Initiatives Handle System ID"
                      >
                        {record?.hsid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="arxivid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="arxivid">arXiv ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="arxivid"
                          {...field}
                          help="The Cornell University Library arXiv.org ID"
                      />
                      : <LinkEx
                          id="arxivid"
                          href={record?.arxivid ? `https://arxiv.org/abs/${record?.arxivid ?? ''}` : ''}
                          target="_blank"
                          help="The Cornell University Library arXiv.org ID"
                      >
                        {record?.arxivid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="biorxivid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="biorxivid">bioRxiv ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="biorxivid"
                          {...field}
                          help="The Cold Spring Harbor Laboratory bioRxiv.org ID"
                      />
                      : <LinkEx
                          id="biorxivid"
                          href={record?.biorxivid ? `https://www.biorxiv.org/content/${record?.biorxivid ?? ''}v1` : ''}
                          target="_blank"
                          help="The Cold Spring Harbor Laboratory bioRxiv.org ID"
                      >
                        {record?.biorxivid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="medrxivid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="medrxivid">medRxiv ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="medrxivid"
                          {...field}
                          help="The Cold Spring Harbor Laboratory medRxiv.org ID"
                      />
                      : <LinkEx
                          id="medrxivid"
                          href={record?.medrxivid ? `https://www.medrxiv.org/content/${record?.medrxivid ?? ''}v1` : ''}
                          target="_blank"
                          help="The Cold Spring Harbor Laboratory medRxiv.org ID"
                      >
                        {record?.medrxivid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="ericid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="ericid">ERIC ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="ericid"
                          {...field}
                          help="The U.S. Department of Education ERIC database ID"
                      />
                      : <LinkEx
                          id="ericid"
                          href={record?.ericid ? `https://eric.ed.gov/?id=${record?.ericid ?? ''}` : ''}
                          target="_blank"
                          help="The U.S. Department of Education ERIC database ID"
                      >
                        {record?.ericid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="ihepid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="ihepid">INSPIRE-HEP ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="ihepid"
                          {...field}
                          help="The CERN INSPIRE-HEP ID"
                      />
                      : <LinkEx
                          id="ihepid"
                          href={record?.ihepid ? `https://inspirehep.net/literature/${record?.ihepid ?? ''}` : ''}
                          target="_blank"
                          help="The CERN INSPIRE-HEP ID"
                      >
                        {record?.ihepid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="oaipmhid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="oaipmhid">OAI-PMH ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="oaipmhid"
                          {...field}
                          help="The Open Archives Initiative OAI-PMH ID"
                      />
                      : <LinkEx
                          id="oaipmhid"
                          href={record?.oaipmhid ? `https://www.openarchives.org/OAI/2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=${record?.oaipmhid ?? ''}` : ''}
                          target="_blank"
                          help="The Open Archives Initiative OAI-PMH ID"
                      >
                        {record?.oaipmhid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="halid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="halid">HAL ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="halid"
                          {...field}
                          help="The CNRS (France) HAL ID"
                      />
                      : <LinkEx
                          id="halid"
                          href={record?.halid ? `https://hal.archives-ouvertes.fr/${record?.halid ?? ''}` : ''}
                          target="_blank"
                          help="The CNRS (France) HAL ID"
                      >
                        {record?.halid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="zenodoid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="zenodoid">Zenodo ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="zenodoid"
                          {...field}
                          help="The CERN Zenodo Record ID"
                      />
                      : <LinkEx
                          id="zenodoid"
                          href={record?.zenodoid ? `https://zenodo.org/record/${record?.zenodoid ?? ''}` : ''}
                          target="_blank"
                          help="The CERN Zenodo Record ID"
                      >
                        {record?.zenodoid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="scopuseid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="scopuseid">SCOPUS EID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="scopuseid"
                          {...field}
                          help="The Elsevier SCOPUS database EID"
                      />
                      : <LinkEx
                          id="scopuseid"
                          href={record?.scopuseid ? `https://www.scopus.com/record/display.uri?eid=${record?.scopuseid ?? ''}` : ''}
                          target="_blank"
                          help="The Elsevier SCOPUS database EID"
                      >
                        {record?.scopuseid ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="wsan"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="wsan">WS Accession Number</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="wsan"
                          {...field}
                          help="The Clarivate Web of Science Accession Number (UT)"
                      />
                      : <LinkEx
                          id="wsan"
                          href={record?.wsan ? `https://www.webofscience.com/wos/woscc/full-record/${record?.wsan ?? ''}` : ''}
                          target="_blank"
                          help="The Clarivate Web of Science Accession Number (UT)"
                      >
                        {record?.wsan ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="pinfoan"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="pinfoan">PsycINFO Accession Number</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="pinfoan"
                          {...field}
                          help="The American Psychological Association PsycINFO Accession Number"
                      />
                      : <LinkEx
                          id="pinfoan"
                          href={record?.pinfoan ? `https://psycnet.apa.org/record/${record?.pinfoan ?? ''}` : ''}
                          target="_blank"
                          help="The American Psychological Association PsycINFO Accession Number"
                      >
                        {record?.pinfoan ?? 'n/a'}
                      </LinkEx>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
        </form>
      </Form>
    </fieldset>
  )
}

PublicationDetails.whyDidYouRender = true