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
import Journal from "@/app/model/Journal"
import StandardDetails from "./standard-details"
import DetailActions, { DetailMode, DetailState } from "./detail-actions"
import { Dispatch, SetStateAction, useCallback, useMemo, useState } from "react"
import { useFormContext, useWatch } from "react-hook-form"
import { PublicationFieldValues } from "../validators/publication"
import { FormActionHandler } from "@/hooks/use-page-logic"
import InputEx from "../ext/input-ex"
import TextareaEx from "../ext/textarea-ex"
import ButtonEx from "../ext/button-ex"
import LinkEx from "../ext/link-ex"
import CheckboxEx from "../ext/checkbox-ex"
import StarRatingBasicEx from "../ext/star-rating-ex"
import { detail, LoggerEx } from "@/lib/logger"
import { useQuery } from "@apollo/client/react"
import { READ_JOURNALS, READ_PUBLISHERS } from "@/lib/graphql-queries"
import IPage from "@/app/model/IPage"
import { QueryResult } from "@/lib/graphql-utils"
import { ArrowRight, CalendarIcon, NotebookTabsIcon, RotateCwIcon } from "lucide-react"
import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "@/components/ui/combobox"
import { publicationKinds, publicationKindsByKind } from "@/data/publication-kinds"
import { InputGroupAddon } from "@/components/ui/input-group"
import Help from "../misc/help"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { AddOnLink } from "../ext/addon-link"
import Publisher from "@/app/model/Publisher"

const logger = new LoggerEx(detail, "[PublicationDetails] ")
const EMPTY_JOURNALS = [] as Journal[]
const EMPTY_PUBLISHERS = [] as Publisher[]
const JOURNAL_OPTIONS = {variables: {pageSort: {sort: {orders: [{property: "title"}]}}}}
const PUBLISHER_OPTIONS = {variables: {pageSort: {sort: {orders: [{property: "name"}]}}}}

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

  const form = useFormContext<PublicationFieldValues>()
  const [dateOpen, setDateOpen] = useState(false)
  const [accessedOpen, setAccessedOpen] = useState(false)
  const { updating } = state

  const journalsResult = useQuery(READ_JOURNALS, JOURNAL_OPTIONS)
  const journalsData = (journalsResult.loading
    ? journalsResult.previousData
    : journalsResult.data) as QueryResult<IPage<Journal>>
  const journals = journalsData?.journals?.content ?? EMPTY_JOURNALS
  const journalsById = useMemo(() => {
    return Object.fromEntries(
      journals.map(j => [j.id, j])
    ) as {[id: string]: Journal}
  }, [journals])
  const journalId = useWatch({name: "journalId"})
  const selectedJournal = journalId ? journalsById[journalId] ?? null : null
  const getJournalUri = useCallback(() => `/journals/?recordId=${journalId ?? ''}`, [journalId])
  logger.trace("render: journalId: %s", journalId)

  const publishersResult = useQuery(READ_PUBLISHERS, PUBLISHER_OPTIONS)
  const publishersData = (publishersResult.loading
    ? publishersResult.previousData
    : publishersResult.data) as QueryResult<IPage<Publisher>>
  const publishers = publishersData?.publishers?.content ?? EMPTY_PUBLISHERS
  const publishersById = useMemo(() => {
    return Object.fromEntries(
      publishers.map(p => [p.id, p])
    ) as {[id: string]: Publisher}
  }, [publishers])
  const publisherId = useWatch({name: "publisherId"})
  const selectedPublisher = publisherId ? publishersById[publisherId] ?? null : null
  const getPublisherUri = useCallback(() => `/publishers/?recordId=${publisherId ?? ''}`, [publisherId])
  logger.trace("render: publisherId: %s", publisherId)

  const handleJournalChange = useCallback((journal: Journal | null) => {
    logger.trace("handleJournalChange(%s)", journal?.id ?? null)

    // If there is no publisher currently set, or if the current publisher matches that of the current journal,
    // update the publication's publisher to match that of the newly selected journal.
    if (journal?.publisher && !selectedPublisher || selectedJournal?.publisher?.id === selectedPublisher?.id) {
      logger.trace("handleJournalChange: setting publisherId = %s", journal?.publisher?.id ?? '')

      form.setValue("publisherId", journal?.publisher?.id ?? null)
    }
  }, [form, selectedJournal, selectedPublisher])

  const kind = form.getValues().kind
  const selectedPublicationKind = kind ? publicationKindsByKind[kind] ?? null : null
  
  return (
    <fieldset className="border shadow-lg rounded-md">
      <legend className="text-lg">&nbsp;Publication Details&nbsp;</legend>
      <StandardDetails recordKind="Publication" record={record} state={state} showLinkingDetails={true} />
      <Form {...form}>
        <form>
          <FormDescription>
            <span className="text-black text-lg ml-2"><NotebookTabsIcon className="inline" />&nbsp;Details</span>
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
                      disabled={!updating}
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
              className="col-start-4 row-span-15"
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
                  <Combobox
                    disabled={!updating}
                    items={publicationKinds}
                    itemToStringValue={k => k.kind}
                    itemToStringLabel={k => k.label}
                    value={selectedPublicationKind}
                    onValueChange={k => field.onChange(k?.kind ?? null)}
                  >
                    <ComboboxInput
                      id="kind"
                      placeholder="Select a publication kind"
                      readOnly={!updating}
                      showClear
                    >
                      <InputGroupAddon align="inline-end">
                        <Help text="The kind of publication (corresponds to the RIS 'TY' field)" />
                      </InputGroupAddon>
                    </ComboboxInput>
                    <ComboboxContent>
                      <ComboboxEmpty>-No publication kinds found-</ComboboxEmpty>
                      <ComboboxList>
                        {k => (
                          <ComboboxItem key={k.kind} value={k}>
                            {k.label}
                          </ComboboxItem>
                        )}
                      </ComboboxList>
                    </ComboboxContent>
                  </Combobox>
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
                          type="button"
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
              name="journalId"
              render={({ field }) => (
                <FormItem className="col-span-3">
                  <FormLabel htmlFor="journal">Journal</FormLabel>
                  <Combobox
                    disabled={!updating}
                    items={journals}
                    itemToStringValue={j => j?.id ?? ''}
                    itemToStringLabel={j => j?.title ?? ''}
                    value={selectedJournal}
                    onValueChange={j => {field.onChange(j?.id ?? null); handleJournalChange(j)}}
                  >
                    <ComboboxInput
                      id="journalId"
                      placeholder="-Select a journal-"
                      readOnly={!updating}
                      showClear
                    >
                      <InputGroupAddon className="gap-1" align="inline-end">
                        <AddOnLink
                          href={getJournalUri()}
                          disabled={!journalId}
                          title="Go to the selected journal"
                        >
                          <ArrowRight className="w-6 h-6 text-gray-400" />
                        </AddOnLink>
                        <Badge variant="outline" title="The number of journals">{journals.length.toLocaleString()}</Badge>
                        <Button
                          className="w-6 h-6"
                          type="button"
                          variant="ghost"
                          onClick={() => journalsResult.refetch()}
                          title="Refresh the list of journals"
                        >
                          <RotateCwIcon className="w-6 h-6" />
                        </Button>
                        <Help text="The journal or series containing the publication" />
                      </InputGroupAddon>
                    </ComboboxInput>
                    <ComboboxContent>
                      <ComboboxEmpty>-No journals found-</ComboboxEmpty>
                      <ComboboxList>
                        {j => (
                          <ComboboxItem key={j.id} value={j}>
                            {j.title}
                          </ComboboxItem>
                        )}
                      </ComboboxList>
                    </ComboboxContent>
                  </Combobox>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="publisherId"
              render={({ field }) => (
                <FormItem className="col-span-3">
                  <FormLabel htmlFor="publisher">Publisher</FormLabel>
                  <Combobox
                    disabled={!updating}
                    items={publishers}
                    itemToStringValue={p => p?.id ?? ''}
                    itemToStringLabel={p => p?.name ?? ''}
                    value={selectedPublisher}
                    onValueChange={p => field.onChange(p?.id ?? null)}
                  >
                    <ComboboxInput
                      id="publisherId"
                      placeholder="-Select a publisher-"
                      readOnly={!updating}
                      showClear
                    >
                      <InputGroupAddon className="gap-1" align="inline-end">
                        <AddOnLink
                          href={getPublisherUri()}
                          disabled={!publisherId}
                          title="Go to the selected publisher"
                        >
                          <ArrowRight className="w-6 h-6 text-gray-400" />
                        </AddOnLink>
                        <Badge variant="outline" title="The number of publishers">{publishers.length.toLocaleString()}</Badge>
                        <Button
                          className="w-6 h-6"
                          type="button"
                          variant="ghost"
                          onClick={() => publishersResult.refetch()}
                          title="Refresh the list of publishers"
                        >
                          <RotateCwIcon className="w-6 h-6" />
                        </Button>
                        <Help text="The publisher of the publication" />
                      </InputGroupAddon>
                    </ComboboxInput>
                    <ComboboxContent>
                      <ComboboxEmpty>-No publishers found-</ComboboxEmpty>
                      <ComboboxList>
                        {p => (
                          <ComboboxItem key={p.id} value={p}>
                            {p.name}
                          </ComboboxItem>
                        )}
                      </ComboboxList>
                    </ComboboxContent>
                  </Combobox>
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
              name="keywords"
              render={({field}) => (
                <FormItem className="col-span-3">
                  <FormLabel htmlFor="keywords">Keywords</FormLabel>
                  <FormControl>
                    <InputEx
                      id="keywords"
                      disabled={!record && !updating}
                      readOnly={!updating}
                      {...field}
                      help="Keywords per publication metadata"
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
                      : record?.url
                      ? <LinkEx
                        id="url"
                        href={record.url}
                        target="_blank"
                        help="The online web address"
                      >
                        {record.url}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                          type="button"
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
                      : record?.doi
                      ? <LinkEx
                          href={`https://doi.org/${record.doi}`}
                          target="_blank"
                          help="The Digital Object Identifier (DOI)"
                      >
                        {record.doi}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.isbn
                      ? <LinkEx
                          id="isbn"
                          href={`https://isbnsearch.org/isbn/${record.isbn}`}
                          target="_blank"
                          help="The International Standard Book Number (ISBN)"
                      >
                        {record.isbn}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
                    }
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="pmcid"
              render={({field}) => (
                <FormItem>
                  <FormLabel htmlFor="pmcid">PubMedCentral ID</FormLabel>
                  <FormControl>
                    {
                      updating
                      ? <InputEx
                          id="pmcid"
                          {...field}
                          help="The U.S. NIH National Library of Medicine PubMed Central ID"
                      />
                      : record?.pmcid
                      ? <LinkEx
                          id="pmcid"
                          href={`https://pmc.ncbi.nlm.nih.gov/articles/${record.pmcid}`}
                          target="_blank"
                          help="The U.S. NIH National Library of Medicine PubMed Central ID"
                      >
                        {record.pmcid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                          help="The U.S. NIH National Library of Medicine PubMed ID"
                      />
                      : record?.pmid
                      ? <LinkEx
                          id="pmid"
                          href={`https://pubmed.ncbi.nlm.nih.gov/${record.pmid}`}
                          target="_blank"
                          help="The U.S. NIH National Library of Medicine PubMed ID"
                      >
                        {record.pmid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.hsid
                      ? <LinkEx
                          id="hsid"
                          href={`https://hdl.handle.net/${record.hsid}`}
                          target="_blank"
                          help="The Corporation for National Research Initiatives Handle System ID"
                      >
                        {record?.hsid ?? 'n/a'}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.arxivid
                      ? <LinkEx
                          id="arxivid"
                          href={`https://arxiv.org/abs/${record.arxivid}`}
                          target="_blank"
                          help="The Cornell University Library arXiv.org ID"
                      >
                        {record.arxivid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.biorxivid
                      ? <LinkEx
                          id="biorxivid"
                          href={`https://www.biorxiv.org/content/${record.biorxivid}v1`}
                          target="_blank"
                          help="The Cold Spring Harbor Laboratory bioRxiv.org ID"
                      >
                        {record.biorxivid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.medrxivid
                      ? <LinkEx
                          id="medrxivid"
                          href={`https://www.medrxiv.org/content/${record.medrxivid}v1`}
                          target="_blank"
                          help="The Cold Spring Harbor Laboratory medRxiv.org ID"
                      >
                        {record.medrxivid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.ericid
                      ? <LinkEx
                          id="ericid"
                          href={`https://eric.ed.gov/?id=${record.ericid}`}
                          target="_blank"
                          help="The U.S. Department of Education ERIC database ID"
                      >
                        {record.ericid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.ihepid
                      ? <LinkEx
                          id="ihepid"
                          href={`https://inspirehep.net/literature/${record.ihepid}`}
                          target="_blank"
                          help="The CERN INSPIRE-HEP ID"
                      >
                        {record.ihepid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.oaipmhid
                      ? <LinkEx
                          id="oaipmhid"
                          href={`https://www.openarchives.org/OAI/2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=${record?.oaipmhid}`}
                          target="_blank"
                          help="The Open Archives Initiative OAI-PMH ID"
                      >
                        {record.oaipmhid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.halid
                      ? <LinkEx
                          id="halid"
                          href={`https://hal.archives-ouvertes.fr/${record.halid}`}
                          target="_blank"
                          help="The CNRS (France) HAL ID"
                      >
                        {record.halid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.zenodoid
                      ? <LinkEx
                          id="zenodoid"
                          href={`https://zenodo.org/record/${record.zenodoid}`}
                          target="_blank"
                          help="The CERN Zenodo Record ID"
                      >
                        {record.zenodoid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.scopuseid
                      ? <LinkEx
                          id="scopuseid"
                          href={`https://www.scopus.com/record/display.uri?eid=${record.scopuseid}`}
                          target="_blank"
                          help="The Elsevier SCOPUS database EID"
                      >
                        {record.scopuseid}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.wsan
                      ? <LinkEx
                          id="wsan"
                          href={`https://www.webofscience.com/wos/woscc/full-record/${record.wsan}`}
                          target="_blank"
                          help="The Clarivate Web of Science Accession Number (UT)"
                      >
                        {record.wsan}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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
                      : record?.pinfoan
                      ? <LinkEx
                          id="pinfoan"
                          href={`https://psycnet.apa.org/record/${record.pinfoan}`}
                          target="_blank"
                          help="The American Psychological Association PsycINFO Accession Number"
                      >
                        {record.pinfoan}
                      </LinkEx>
                      : <span className="text-gray-400">-Not set-</span>
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