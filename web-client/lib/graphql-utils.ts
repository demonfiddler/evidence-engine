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

import { ApolloClient, createHttpLink, DocumentNode, InMemoryCache } from '@apollo/client'
import { FieldNode, Kind, OperationDefinitionNode, OperationTypeNode } from 'graphql'
import { LoggerEx, utility } from './logger';

const logger = new LoggerEx(utility, "[graphql-utils] ")

logger.trace("process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL='%s', process.env.NEXT_PUBLIC_WEB_CLIENT_URL='%s'",
  process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL, process.env.NEXT_PUBLIC_WEB_CLIENT_URL)

const link = createHttpLink({
  uri: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL,
  credentials: 'include'
});

export const apolloClient = new ApolloClient({
  // uri: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL,
  link,
  cache: new InMemoryCache({
    possibleTypes: {
      IBaseEntity: [
        "Claim",
        "Comment",
        "EntityLink",
        "Declaration",
        "Group",
        "Journal",
        "Log",
        "Person",
        "Publication",
        "Publisher",
        "Quotation",
        "Topic",
        "User",
      ],
      ITrackedEntity: [
        "Claim",
        "Comment",
        "EntityLink",
        "Declaration",
        "Group",
        "Journal",
        "Person",
        "Publication",
        "Publisher",
        "Quotation",
        "Topic",
        "User",
      ],
      ILinkableEntity: [
        "Claim",
        "Declaration",
        "Person",
        "Publication",
        "Quotation",
        "Topic"
      ],
      IPage: [
        "ClaimPage",
        "CommentPage",
        "EntityLinkPage",
        "DeclarationPage",
        "GroupPage",
        "JournalPage",
        "LogPage",
        "PersonPage",
        "PublicationPage",
        "PublisherPage",
        "QuotationPage",
        "TopicPage",
        "UserPage",
      ]
    },
    typePolicies: {
      // See https://www.apollographql.com/docs/react/caching/cache-configuration#typepolicy-fields
      // __TYPENAME: {...}
    }
  }),
})

/**
 * Introspects a GraphQL operation to determine the result field and variable names.
 * @param node The GraphQL document to introspect.
 * @param operationKind The operation kind to look for.
 * @returns An array containing the name of the first selection node, followed by the names of all bind variables for
 * the operation. The array is empty if no `node` was supplied.
 * @throws `Error` if `node` is specified but no matching operation could be found.
 */
export function introspect(node: DocumentNode | undefined, operationKind: OperationTypeNode) : string[] {
  if (!node)
    return []
  const opDef = node?.definitions.find(
    d => d.kind == Kind.OPERATION_DEFINITION && d.operation == operationKind) as OperationDefinitionNode | undefined
  const fieldNode = opDef?.selectionSet.selections[0] as FieldNode | undefined
  const fieldName = fieldNode?.alias?.value ?? fieldNode?.name.value
  if (!fieldName)
    throw new Error("Unable to determine field name")
  const result = [fieldName]
  const varnames = opDef?.variableDefinitions?.map(n => n.variable.name.value)
  if (varnames)
    result.push(...varnames)
  return result
}
