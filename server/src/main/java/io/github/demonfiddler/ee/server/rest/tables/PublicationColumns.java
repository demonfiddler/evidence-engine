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

package io.github.demonfiddler.ee.server.rest.tables;

import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.FULL;
import static io.github.demonfiddler.ee.server.rest.tables.Column.ColumnSpan.SINGLE;

import java.util.List;

import io.github.demonfiddler.ee.common.util.CompositeList;
import io.github.demonfiddler.ee.server.model.IBaseEntity;
import io.github.demonfiddler.ee.server.model.Publication;
import io.github.demonfiddler.ee.server.model.PublicationKind;
import io.github.demonfiddler.ee.server.rest.tables.Column.ColumnType;
import io.github.demonfiddler.ee.server.rest.util.RenderUtils;

public class PublicationColumns {

    private static final Column<Publication> TITLE = new Column<>("title", "Title", ColumnType.STRING, FULL, null);
    private static final Column<Publication> KIND = new Column<>("kind", "Kind", ColumnType.STRING, SINGLE,
        (rec, raw) -> PublicationKind.valueOf(rec.getKind()).label());
    private static final Column<Publication> AUTHORS =
        new Column<>("authors", "Authors", ColumnType.STRING, FULL, null);
    private static final Column<Publication> JOURNAL = new Column<>("journal", "Journal", ColumnType.STRING, SINGLE,
        (rec, raw) -> rec.getJournal() != null ? rec.getJournal().getAbbreviation() != null
            ? rec.getJournal().getAbbreviation() : rec.getJournal().getTitle() : "");
    private static final Column<Publication> DATE = new Column<>("date", "Date", ColumnType.DATE, SINGLE, null);
    private static final Column<Publication> YEAR = new Column<>("year", "Year", ColumnType.NUMBER, SINGLE,
        (rec, raw) -> rec.getYear() != null ? rec.getYear().toString() : "");
    private static final Column<Publication> ABSTRACT =
        new Column<>("abstract", "Abstract", ColumnType.STRING, FULL, null);
    private static final Column<Publication> DOI = new Column<>("doi", "DOI", ColumnType.STRING, SINGLE, (rec, raw) -> {
        String doi = rec.getDoi() != null ? rec.getDoi() : "";
        return raw ? doi : !doi.isEmpty() ? RenderUtils.instance.renderUrl("https://doi.org/" + doi, doi, raw) : "";
    });
    private static final Column<Publication> ISBN =
        new Column<>("isbn", "ISBN", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String isbn = rec.getIsbn() != null ? rec.getIsbn() : "";
            return raw ? isbn : !isbn.isEmpty()
                ? RenderUtils.instance.renderUrl("https://isbnsearch.org/isbn/" + isbn, isbn, raw) : "";
        });
    private static final Column<Publication> PMID =
        new Column<>("pmid", "PubMed ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String pmid = rec.getPmid() != null ? rec.getPmid() : "";
            return raw ? pmid : !pmid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://pubmed.ncbi.nlm.nih.gov/" + pmid, pmid, raw) : "";
        });
    private static final Column<Publication> HSID = new Column<>("hsid", "HS ID", ColumnType.STRING, SINGLE, null);
    private static final Column<Publication> ARXIVID =
        new Column<>("arxivid", "ArXiv ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String arxivid = rec.getArxivid() != null ? rec.getArxivid() : "";
            return raw ? arxivid : !arxivid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://arxiv.org/abs/" + arxivid, arxivid, raw) : "";
        });
    private static final Column<Publication> BIORXIVXID =
        new Column<>("biorxivid", "BioRxiv ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String biorxivid = rec.getBiorxivid() != null ? rec.getBiorxivid() : "";
            return raw ? biorxivid : !biorxivid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://www.biorxiv.org/content/" + biorxivid, biorxivid, raw) : "";
        });
    private static final Column<Publication> MEDRXIVID =
        new Column<>("medrxivid", "MedRxiv ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String medrxivid = rec.getMedrxivid() != null ? rec.getMedrxivid() : "";
            return raw ? medrxivid : !medrxivid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://www.medrxiv.org/content/" + medrxivid, medrxivid, raw) : "";
        });
    private static final Column<Publication> ERICID =
        new Column<>("ericid", "ERIC ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String ericid = rec.getEricid() != null ? rec.getEricid() : "";
            return raw ? ericid : !ericid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://eric.ed.gov/?id=" + ericid, ericid, raw) : "";
        });
    private static final Column<Publication> IHEPID =
        new Column<>("ihepid", "IHEP ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String ihepid = rec.getIhepid() != null ? rec.getIhepid() : "";
            return raw ? ihepid : !ihepid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://inspirehep.net/literature/" + ihepid, ihepid, raw) : "";
        });
    private static final Column<Publication> OAIPMHID =
        new Column<>("oaipmhid", "OAI-PMH ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String oaipmhid = rec.getOaipmhid() != null ? rec.getOaipmhid() : "";
            return raw ? oaipmhid
                : !oaipmhid.isEmpty() ? RenderUtils.instance.renderUrl(
                    "https://www.openarchives.org/OAI/2.0?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + oaipmhid,
                    oaipmhid, raw) : "";
        });
    private static final Column<Publication> HALID =
        new Column<>("halid", "HAL ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String halid = rec.getHalid() != null ? rec.getHalid() : "";
            return raw ? halid : !halid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://hal.archives-ouvertes.fr/" + halid, halid, raw) : "";
        });
    private static final Column<Publication> ZENODOID =
        new Column<>("zenodoid", "Zenodo ID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String zenodoid = rec.getZenodoid() != null ? rec.getZenodoid() : "";
            return raw ? zenodoid : !zenodoid.isEmpty()
                ? RenderUtils.instance.renderUrl("https://zenodo.org/record/" + zenodoid, zenodoid, raw) : "";
        });
    private static final Column<Publication> SCOPUSEID =
        new Column<>("scopuseid", "Scopus EID", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String scopuseid = rec.getScopuseid() != null ? rec.getScopuseid() : "";
            return raw ? scopuseid : !scopuseid.isEmpty() ? RenderUtils.instance
                .renderUrl("https://www.scopus.com/record/display.uri?eid=" + scopuseid, scopuseid, raw) : "";
        });
    private static final Column<Publication> WSAN =
        new Column<>("wsan", "WSAN", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String wsan = rec.getWsan() != null ? rec.getWsan() : "";
            return raw ? wsan : !wsan.isEmpty() ? RenderUtils.instance
                .renderUrl("https://www.webofscience.com/wos/woscc/full-record/" + wsan, wsan, raw) : "";
        });
    private static final Column<Publication> PINFOAN =
        new Column<>("pinfoan", "PInfoAN", ColumnType.STRING, SINGLE, (rec, raw) -> {
            String pinfoan = rec.getPinfoan() != null ? rec.getPinfoan() : "";
            return raw ? pinfoan : !pinfoan.isEmpty()
                ? RenderUtils.instance.renderUrl("https://psycnet.apa.org/record/" + pinfoan, pinfoan, raw) : "";
        });
    private static final Column<Publication> URL = new Column<>("url", "URL", ColumnType.URL, FULL, null);
    private static final Column<Publication> ACCESSED =
        new Column<>("accessed", "Accessed", ColumnType.DATE, SINGLE, null);
    private static final Column<Publication> NOTES = new Column<>("notes", "Notes", ColumnType.STRING, FULL, null);
    private static final Column<Publication> CACHED =
        new Column<>("cached", "Cached", ColumnType.BOOLEAN, SINGLE, null);
    private static final Column<Publication> PEER_REVIEWED =
        new Column<>("peerReviewed", "Peer Reviewed", ColumnType.BOOLEAN, SINGLE, null);

    public static final List<Column<Publication>> OWN_COLUMNS = List.of(TITLE, KIND, AUTHORS, JOURNAL, DATE, YEAR,
        ABSTRACT, DOI, ISBN, PMID, HSID, ARXIVID, BIORXIVXID, MEDRXIVID, ERICID, IHEPID, OAIPMHID, HALID, ZENODOID,
        SCOPUSEID, WSAN, PINFOAN, URL, ACCESSED, NOTES, CACHED, PEER_REVIEWED);
    public static final List<Column<Publication>> OWN_DEFAULT_COLUMNS = List.of(TITLE, KIND, YEAR);
    public static final List<Column<? extends IBaseEntity>> ALL_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_COLUMNS, OWN_COLUMNS));
    public static final List<Column<? extends IBaseEntity>> ALL_DEFAULT_COLUMNS =
        new CompositeList<>(List.of(TrackedEntityColumns.ALL_DEFAULT_COLUMNS, OWN_DEFAULT_COLUMNS));

    /** Private ctor prevents instantiation. */
    private PublicationColumns() {
        throw new UnsupportedOperationException();
    }

}
