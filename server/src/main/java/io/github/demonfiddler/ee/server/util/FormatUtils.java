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

package io.github.demonfiddler.ee.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.demonfiddler.ee.server.model.DeclarationKind;
import io.github.demonfiddler.ee.server.model.EntityKind;
import io.github.demonfiddler.ee.server.model.FormatKind;
import io.github.demonfiddler.ee.server.model.PermissionKind;
import io.github.demonfiddler.ee.server.model.PublicationKind;
import io.github.demonfiddler.ee.server.model.StatusKind;
import io.github.demonfiddler.ee.server.model.TransactionKind;

/**
 * A bean for formatting enumerations.
 */
@Component
public class FormatUtils {

    private static final Map<DeclarationKind, String> DECLARATION_KIND_LONG = new HashMap<>();
    private static final Map<EntityKind, String> ENTITY_KIND_LONG = new HashMap<>();
    private static final Map<PermissionKind, String> PERMISSION_KIND_LONG = new HashMap<>();
    private static final Map<PublicationKind, String> PUBLICATION_KIND_LONG = new HashMap<>();
    private static final Map<StatusKind, String> STATUS_KIND_LONG = new HashMap<>();
    private static final Map<TransactionKind, String> TRANSACTION_KIND_LONG = new HashMap<>();

    static {
        DECLARATION_KIND_LONG.put(DeclarationKind.DECL, "Declaration");
        DECLARATION_KIND_LONG.put(DeclarationKind.OPLE, "Open Letter");
        DECLARATION_KIND_LONG.put(DeclarationKind.PETN, "Petition");

        ENTITY_KIND_LONG.put(EntityKind.CLA, "Claim");
        ENTITY_KIND_LONG.put(EntityKind.COU, "Country");
        ENTITY_KIND_LONG.put(EntityKind.DEC, "Declaration");
        ENTITY_KIND_LONG.put(EntityKind.JOU, "Journal");
        ENTITY_KIND_LONG.put(EntityKind.LNK, "EntityLink");
        ENTITY_KIND_LONG.put(EntityKind.PBR, "Publisher");
        ENTITY_KIND_LONG.put(EntityKind.PER, "Person");
        ENTITY_KIND_LONG.put(EntityKind.PUB, "Publication");
        ENTITY_KIND_LONG.put(EntityKind.QUO, "Quotation");
        ENTITY_KIND_LONG.put(EntityKind.TOP, "Topic");
        ENTITY_KIND_LONG.put(EntityKind.USR, "User");

        PERMISSION_KIND_LONG.put(PermissionKind.ADM, "Use administrative functions");
        PERMISSION_KIND_LONG.put(PermissionKind.CRE, "Insert new record");
        PERMISSION_KIND_LONG.put(PermissionKind.DEL, "Delete existing record");
        PERMISSION_KIND_LONG.put(PermissionKind.LNK, "Link/unlink existing records");
        PERMISSION_KIND_LONG.put(PermissionKind.REA, "Read existing record");
        PERMISSION_KIND_LONG.put(PermissionKind.UPD, "Update existing record");
        PERMISSION_KIND_LONG.put(PermissionKind.UPL, "Manage uploaded files");

        PUBLICATION_KIND_LONG.put(PublicationKind.ABST, "Abstract");
        PUBLICATION_KIND_LONG.put(PublicationKind.ADVS, "Audiovisual material");
        PUBLICATION_KIND_LONG.put(PublicationKind.AGGR, "Aggregated database");
        PUBLICATION_KIND_LONG.put(PublicationKind.ANCIENT, "Ancient text");
        PUBLICATION_KIND_LONG.put(PublicationKind.ART, "Art work");
        PUBLICATION_KIND_LONG.put(PublicationKind.BILL, "Bill/resolution");
        PUBLICATION_KIND_LONG.put(PublicationKind.BLOG, "Blog");
        PUBLICATION_KIND_LONG.put(PublicationKind.BOOK, "Book, whole");
        PUBLICATION_KIND_LONG.put(PublicationKind.CASE, "Case");
        PUBLICATION_KIND_LONG.put(PublicationKind.CHAP, "Book section");
        PUBLICATION_KIND_LONG.put(PublicationKind.CHART, "Chart");
        PUBLICATION_KIND_LONG.put(PublicationKind.CLSWK, "Classical work");
        PUBLICATION_KIND_LONG.put(PublicationKind.COMP, "Computer program");
        PUBLICATION_KIND_LONG.put(PublicationKind.CONF, "Conference proceeding");
        PUBLICATION_KIND_LONG.put(PublicationKind.CPAPER, "Conference paper");
        PUBLICATION_KIND_LONG.put(PublicationKind.CTLG, "Catalogue");
        PUBLICATION_KIND_LONG.put(PublicationKind.DATA, "Dataset");
        PUBLICATION_KIND_LONG.put(PublicationKind.DBASE, "Online database");
        PUBLICATION_KIND_LONG.put(PublicationKind.DICT, "Dictionary");
        PUBLICATION_KIND_LONG.put(PublicationKind.EBOOK, "Electronic book");
        PUBLICATION_KIND_LONG.put(PublicationKind.ECHAP, "Electronic book section");
        PUBLICATION_KIND_LONG.put(PublicationKind.EDBOOK, "Edited book");
        PUBLICATION_KIND_LONG.put(PublicationKind.EJOUR, "Electronic article");
        PUBLICATION_KIND_LONG.put(PublicationKind.ELEC, "Electronic citation");
        PUBLICATION_KIND_LONG.put(PublicationKind.ENCYC, "Encyclopaedia article");
        PUBLICATION_KIND_LONG.put(PublicationKind.EQUA, "Equation");
        PUBLICATION_KIND_LONG.put(PublicationKind.FIGURE, "Figure");
        PUBLICATION_KIND_LONG.put(PublicationKind.GEN, "Generic");
        PUBLICATION_KIND_LONG.put(PublicationKind.GOVDOC, "Government document");
        PUBLICATION_KIND_LONG.put(PublicationKind.GRANT, "Grant");
        PUBLICATION_KIND_LONG.put(PublicationKind.HEAR, "Hearing");
        PUBLICATION_KIND_LONG.put(PublicationKind.ICOMM, "Internet communication");
        PUBLICATION_KIND_LONG.put(PublicationKind.INPR, "In Press");
        PUBLICATION_KIND_LONG.put(PublicationKind.JFULL, "Journal (full)");
        PUBLICATION_KIND_LONG.put(PublicationKind.JOUR, "Journal");
        PUBLICATION_KIND_LONG.put(PublicationKind.LEGAL, "Legal rule or regulation");
        PUBLICATION_KIND_LONG.put(PublicationKind.MANSCPT, "Manuscript");
        PUBLICATION_KIND_LONG.put(PublicationKind.MAP, "Map");
        PUBLICATION_KIND_LONG.put(PublicationKind.MGZN, "Magazine article");
        PUBLICATION_KIND_LONG.put(PublicationKind.MPCT, "Motion picture");
        PUBLICATION_KIND_LONG.put(PublicationKind.MULTI, "Online multimedia");
        PUBLICATION_KIND_LONG.put(PublicationKind.MUSIC, "Music score");
        PUBLICATION_KIND_LONG.put(PublicationKind.NEWS, "Newspaper");
        PUBLICATION_KIND_LONG.put(PublicationKind.PAMP, "Pamphlet");
        PUBLICATION_KIND_LONG.put(PublicationKind.PAT, "Patent");
        PUBLICATION_KIND_LONG.put(PublicationKind.PCOMM, "Personal communication");
        PUBLICATION_KIND_LONG.put(PublicationKind.RPRT, "Report");
        PUBLICATION_KIND_LONG.put(PublicationKind.SER, "Serial publication");
        PUBLICATION_KIND_LONG.put(PublicationKind.SLIDE, "Slide presentation");
        PUBLICATION_KIND_LONG.put(PublicationKind.SOUND, "Sound recording");
        PUBLICATION_KIND_LONG.put(PublicationKind.STAND, "Standard");
        PUBLICATION_KIND_LONG.put(PublicationKind.STAT, "Statute");
        PUBLICATION_KIND_LONG.put(PublicationKind.THES, "Thesis/dissertation");
        PUBLICATION_KIND_LONG.put(PublicationKind.UNBILL, "Unenacted bill/resolution");
        PUBLICATION_KIND_LONG.put(PublicationKind.UNPB, "Unpublished work");
        PUBLICATION_KIND_LONG.put(PublicationKind.VIDEO, "Video recording");
        PUBLICATION_KIND_LONG.put(PublicationKind.WEB, "Web page");

        STATUS_KIND_LONG.put(StatusKind.DEL, "Deleted");
        STATUS_KIND_LONG.put(StatusKind.DRA, "Draft");
        STATUS_KIND_LONG.put(StatusKind.PUB, "Published");
        STATUS_KIND_LONG.put(StatusKind.SUS, "Suspended");

        TRANSACTION_KIND_LONG.put(TransactionKind.CRE, "Created");
        TRANSACTION_KIND_LONG.put(TransactionKind.DEL, "Deleted");
        TRANSACTION_KIND_LONG.put(TransactionKind.LNK, "Linked");
        TRANSACTION_KIND_LONG.put(TransactionKind.UNL, "Unlinked");
        TRANSACTION_KIND_LONG.put(TransactionKind.UPD, "Updated");
    }

    private <T extends Enum<T>> String formatEnum(T kind, FormatKind format, Map<T, String> map) {
        String result;
        if (format == null)
            format = FormatKind.LONG;
        switch (format) {
            case LONG:
                result = map.get(kind);
                break;
            case SHORT:
                result = kind == null ? null : kind.name();
                break;
            default:
                throw new IllegalArgumentException("Unknown FormatKind: " + format);
        }
        return result;
    }

    private <T extends Enum<T>> List<String> formatEnums(List<T> kinds, FormatKind format, Map<T, String> map) {
        return kinds.stream().map(k -> formatEnum(k, format, map)).toList();
    }

    public String formatDeclarationKind(DeclarationKind kind, FormatKind format) {
        return formatEnum(kind, format, DECLARATION_KIND_LONG);
    }

    public String formatEntityKind(EntityKind kind, FormatKind format) {
        return formatEnum(kind, format, ENTITY_KIND_LONG);
    }

    public String formatPermissionKind(PermissionKind kind, FormatKind format) {
        return formatEnum(kind, format, PERMISSION_KIND_LONG);
    }

    public List<String> formatPermissionKinds(List<PermissionKind> permissions, FormatKind format) {
        return formatEnums(permissions, format, PERMISSION_KIND_LONG);
    }

    public String formatPublicationKind(PublicationKind kind, FormatKind format) {
        return formatEnum(kind, format, PUBLICATION_KIND_LONG);
    }

    public String formatStatusKind(StatusKind kind, FormatKind format) {
        return formatEnum(kind, format, STATUS_KIND_LONG);
    }

    public Object formatTransactionKind(TransactionKind kind, FormatKind format) {
        return formatEnum(kind, format, TRANSACTION_KIND_LONG);
    }

}
