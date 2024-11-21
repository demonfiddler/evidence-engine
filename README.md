# Evidence Engine
A client-server application for managing evidence on arbitrary scientific topics.

This application is a generalised rewrite of the Climate Science [Client](../climate-science-client/) and [Server](../climate-science-server/) that will be able to address any arbitrary topics for which evidence can be evinced. The server project is based on Spring Boot with GraphQL-Java and manages a relational database containing scientific evidence of various categories. The API uses GraphQL over HTTP. The web client is based on React and ... (a React framework yet to be chosen).

This document provides an initial high-level vision of intended features and functions.

# Lists

The application supports lists of the following entities:

- **Claim**: an assertion or factual statement that is supported by evidence, generally of a scientific nature
- **Declaration**: a public declaration or open letter signed by multiple individuals
- **Quotation**: a spoken or written statement taken verbatim from a declaration, publication or person
- **Publication**: A publication containing evidence, for example an article in a scientific journal, book, newspaper, etc.
- **Person**: an individual who makes or endorses claims, signs declarations, makes public statements or is listed as a publication author 

## Filtering

The lists can be filtered by any combination of topic, master record and keyword, the latter being a simple search that performs a case-insensitive match against all searchable text fields for the list's entity type.

## Sorting
Each list can be individually sorted on any of its displayed columns, in either ascending (the default) or descending order.

## Ordering

The entity lists themselves can be manually reordered relative to each other by drag-and-drop.
When a list is designated the master list, it is automatically moved to the first position in the UI.

# Topics

List entities are categorised according to a hierarchical tree of topics, of arbitrary depth. Top-level topics have no parent topic, whereas sub-topics have a parent topic (which may itself be a sub-topic). An entity must be associated with at least one topic.

## Topic Filter

The topic filter is always active and applies to all lists. It allows the selection of a target topic, which can be at any level in the hierarchy. Lists show only those entities which are associated with the target topic or, recursively, any of its sub-topics.
This makes it easy to drill down into a specific sub-topic of interest in order to see just those entities which pertain directly to that sub-topic.

# Relationships

The user can designate one of the lists as the **master**, whereupon the other lists will reflect the following relationships with respect to the **currently selected record** in the **master list**:

| Master | Claim | Declaration | Quotation | Publication | Person |
| ------ | ----- | ------ | ----------- | ----------- | --------- |
| None | (all) | (all) | (all) | (all) | (all) |
| Claim | (all) | decl'ns making claim | quotes making claim | pub'ns supporting claim | claimants |
| Declaration | claims by decl'n | (all) | quotes from decl'n | n/a | signatories of decl'n |
| Quotation | claims by quot'n | source decl'n | (all) | source pub? | quotee(s) |
| Publication | claims by pub'n | n/a | quotes from pub | (all) | authors of pub |
| Person | claims by person | decl'ns signed by person | quotes from person | pubn's authored by person | (all) |

## Master

When a master list is designated, the other lists are filtered where applicable by the unique foreign key for the currently selected record in the master list:

| Master | Claim | Declaration | Quotation | Publication | Person |
| ------ | ----- | ------ | ----------- | ----------- | --------- |
| None | (null) | (null) | (null) | (null) | (null) |
| Claim | (null) | claim_id | claim_id | claim_id | claim_id |
| Declaration | declaration_id | (null) | declaration_id | n/a | declaration_id |
| Quotation | quotation_id | quotation_id | (null) | quotation_id | quotation_id |
| Publication | publication_id | n/a | publication_id | (null) | publication_id |
| Person | person_id | person_id | person_id | person_id | (null) |

# Operations

The system supports the usual CRUD operations (create, read, update, delete) for each entity type. Deletion is implemented by flagging an entity record as deleted rather than physical deletion. This is to avoid the possibility of inadvertent catastrophic data loss.

It will also be possible to export the filtered/sorted list data in both CSV format for inclusion in spreadsheets, etc., and in PDF format as standalone documents.

# Authentication and authorisation

A credentialled user can sign into the system. Doing so activates any permissions they have, such as:

- view log entries
- create record
- update record
- delete record
- link/unlink records

Q: Should we insist on two-way SSL (client certificates)? Doing so would add considerable complexity to both server and client, including keystore and key management UI.

# Preferences

User preferences persist between sessions and include:
- the order of the entity lists for each master setting
- whether toolbars are visible
- expanded/collapsed state for each list
- expanded/collapsed state for each details panel
- the sort order for each list
- the filter for each list

There is a means to clear all such preferences back to their default setting.

There is the possibility of allowing users to define and manage custom *profiles*, each consisting of a collection of such settings. This would assist specific research efforts. We could have standard system-defined profiles as well.

# Logging

The system maintains a log of all inserts, updates and deletes, and log entries can be retrieved for any specific entity record. The system allows deleted records to be restored. (Inserts and updates show the new field values : maybe?)