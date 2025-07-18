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

import { ApolloClient, InMemoryCache } from '@apollo/client';

// console.log(`process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL=${process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL}, process.env.NEXT_PUBLIC_WEB_CLIENT_URL=${process.env.NEXT_PUBLIC_WEB_CLIENT_URL}`)

export const apolloClient = new ApolloClient({
    uri: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL,
    cache: new InMemoryCache({
        possibleTypes: {
            IBaseEntity: [
                "Claim",
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
                "EntityLinkPage",
                "DeclarationPage",
                "GroupPage",
                "JournalPage",
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
});