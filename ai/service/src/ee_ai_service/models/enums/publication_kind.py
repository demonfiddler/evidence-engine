# ----------------------------------------------------------------------------------------------------------------------
#  Evidence Engine: A system for managing evidence on arbitrary scientific topics.
#  Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
#  Copyright © 2024-26 Adrian Price. All rights reserved.
#
#  This file is part of Evidence Engine.
#
#  Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
#  GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
#  or (at your option) any later version.
#
#  Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
#  If not, see <https://www.gnu.org/licenses/>. 
# ----------------------------------------------------------------------------------------------------------------------

from enum import StrEnum

class PublicationKind(StrEnum):
    ABSTRACT = "ABST"
    AUDIO_VISUAL = "ADVS"
    AGGREGATED_DATABASE = "AGGR"
    ANCIENT_TEXT = "ANCIENT"
    ARTWORK = "ART"
    BILL_RESOLUTION = "BILL"
    BLOG = "BLOG"
    BOOK_WHOLE = "BOOK"
    CASE = "CASE"
    BOOK_SECTION = "CHAP"
    CHART = "CHART"
    CLASSICAL_WORK = "CLSWK"
    COMPUTER_PROGRAM = "COMP"
    CONFERENCE_PROCEEDING = "CONF"
    CONFERENCE_PAPER = "CPAPER"
    CATALOGUE = "CTLG"
    DATASET = "DATA"
    ONLINE_DATABASE = "DBASE"
    DICTIONARY = "DICT"
    ELECTRONIC_BOOK = "EBOOK"
    ELECTRONIC_BOOK_SECTION = "ECHAP"
    EDITED_BOOK = "EDBOOK"
    ELECTRONIC_ARTICLE = "EJOUR"
    ELECTRONIC_CITATION = "ELEC"
    ENCYCLOPAEDIA_ARTICLE = "ENCYC"
    EQUATION = "EQUA"
    FIGURE = "FIGURE"
    GENERIC = "GEN"
    GOVERNMENT_DOCUMENT = "GOVDOC"
    GRANT = "GRANT"
    HEARING = "HEAR"
    INTERNET_COMMUNICATION = "ICOMM"
    IN_PRESS = "INPR"
    JOURNAL_FULL = "JFULL"
    JOURNAL = "JOUR"
    LEGAL_RULE_REGULATION = "LEGAL"
    MANUSCRIPT = "MANSCPT"
    MAP = "MAP"
    MAGAZINE_ARTICLE = "MGZN"
    MOTION_PICTURE = "MPCT"
    ONLINE_MULTIMEDIA = "MULTI"
    MUSIC_SCORE = "MUSIC"
    NEWSPAPER_ARTICLE = "NEWS"
    PAMPHLET = "PAMP"
    PATENT = "PAT"
    PERSONAL_COMMUNICATION = "PCOMM"
    REPORT = "RPRT"
    SERIAL_PUBLICATION = "SER"
    SLIDE_PRESENTATION = "SLIDE"
    SOUND_RECORDING = "SOUND"
    STANDARD = "STAND"
    STATUTE = "STAT"
    THESIS_DISSERTATION = "THES"
    UNENACTED_BILL_RESOLUTION = "UNBILL"
    UNPUBLISHED_WORK = "UNPB"
    VIDEO_RECORDING = "VIDEO"
    WEB_PAGE = "WEB"
