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

from unittest import TestCase

from ee_ai_service.utils.name import Name


class TestName:
    def __init__(self, string: str, name: Name):
        self.string = string
        self.name = name


BOB_ZYBACH = Name(first_names="Bob", last_name="Zybach")
JOSE_CONTI = Name(first_names="José Bueno", last_name="Conti")
HORTENSE_COTE = Name(first_names="Hortense", last_name="Côté")
JOZSEF_BALLA = Name(first_names="József", last_name="Balla")
REMY_PRUDHOMME = Name(first_names="Rémy", last_name="Prud’Homme")
HENRI_KERKDIJK_OTTEN = Name(first_names="Henri G.", last_name="Kerkdijk-Otten")
PATRICK_MICHAELS = Name(first_names="Patrick J.", last_name="Michaels")
PAUL_KNAPPENBERGER = Name(first_names="Paul C.", last_name="Knappenberger")
G_FIVOS_SARGENTIS = Name(first_names="G. -Fivos", last_name="Sargentis")
IAN_PLIMER = Name(first_names="Ian Rutherford", last_name="Plimer")
JEAN_PIERRE_BARDINET = Name(first_names="Jean-Pierre", last_name="Bardinet")
PAUL_OKEEFFE = Name(first_names="Paul John", last_name="O’Keeffe")
GEORGE_RYAN = Name(first_names="George", nickname="Rob", last_name="Ryan")
ROBERT_BALLING = Name(first_names="Robert C.", last_name="Balling", suffix="Jr")
DANA_SAYLOR = Name(first_names="Dana H.", last_name="Saylor", suffix="Snr.")
JOSE_BRECHNER = Name(title="Ambassador", first_names="José", last_name="Brechner")
MICHAEL_CREECH = Name(title="Dr.", first_names="Michael", last_name="Creech")
STEPHEN_ENGLISH = Name(title="Dr.", first_names="Stephen David", last_name="English")
JONHSON_ANGELO = Name(title="Dr.", first_names="Jonhson Delibero", last_name="Angelo")
KEVIN_LOUGHREY = Name(
    title="Lt. Col. (ret)", first_names="Kevin A.", last_name="Loughrey"
)
AUGUSTINUS_BERKHOUT = Name(
    title="Prof.",
    first_names="Augustinus Johannes",
    nickname="Guus",
    last_name="Berkhout",
)
RICARDO_FELICIO = Name(
    title="Prof.", first_names="Ricardo Augusto", last_name="Felicio"
)
CHRISTOPHE_BROUWER = Name(first_names="Christophe", prefix="de", last_name="Brouwer")
PATRICK_CASANOVE = Name(first_names="Patrick", prefix="de", last_name="Casanove")
# MARIO_NETO = Name(first_names="Mario", prefix="de", last_name="Carvalho Fontes Neto")
# DANIELA_ONCA = Name(first_names="Daniela", prefix="de", last_name="Souza Onça")
FRANCIS_GAILLARD = Name(first_names="Francis", prefix="le", last_name="Gaillard")
CORNELIS_PAIR = Name(title="Dr.", first_names="Cornelis", prefix="le", last_name="Pair")
THI_DINH = Name(title="Dr.", first_names="Thi Thuy", prefix="van", last_name="Dinh")
EMIEL_BROEKHOVEN = Name(
    first_names="Emiel Jozef Jan", prefix="van", last_name="Broekhoven"
)
APPO_WIEL = Name(first_names="Appo", prefix="van der", last_name="Wiel")
CORNELIS_KOOTEN = Name(
    first_names="G. Cornelis", nickname="Kees", prefix="van", last_name="Kooten"
)
FRANK_VEGGEL = Name(
    title="Prof.", first_names="Frank C. J. M.", prefix="van", last_name="Veggel"
)
WILLIAM_WIJNGAARDEN = Name(first_names="William", prefix="van", last_name="Wijngaarden")
BRIGITTE_VLIET_LANOE = Name(
    first_names="Brigitte", prefix="van", last_name="Vliet-Lanoë"
)
TOM_HOEVEN = Name(title="Dr.", first_names="Tom", prefix="van der", last_name="Hoeven")
EVERT_GRAAFF = Name(first_names="W. J. Evert", prefix="van de", last_name="Graaff")
ADELINO_SANTI = Name(
    title="Mr.", first_names="Adelino", prefix="de", last_name="Santi", suffix="Jnr."
)
TERRY_DUNLEAVY = Name(first_names="Terry", last_name="Dunleavy", post_nominals="MBE")
A_T_J_LAAT = Name(title="Dr.", first_names="A. T. J.", last_name="Laat")
CHRISTOPHER_MONCTON = Name(title="Lord", first_names="Christopher", last_name="Moncton")
OSAMA_LADEN = Name(first_names="Osama", prefix="bin", last_name="Laden")
LIU_TUNGSHENG = Name(
    title="Prof.", first_names="Liu", last_name="Tungsheng", alias="Liu Dongsheng"
)
ANTONIS_FOSKOLOS = Name(
    title="Prof.",
    first_names="Antonis",
    nickname="Anthony",
    last_name="Foskolos",
    alias="Foscolos",
)


FIRST_NAMES_LAST_NAME = [
    TestName("Bob Zybach", BOB_ZYBACH),
    TestName("Hortense Côté", HORTENSE_COTE),
    TestName("José Bueno Conti", JOSE_CONTI),
    TestName("József Balla", JOZSEF_BALLA),
    TestName("Rémy Prud’homme", REMY_PRUDHOMME),
    TestName("rémy prud’homme", REMY_PRUDHOMME),
    TestName("Henri G. Kerkdijk-Otten", HENRI_KERKDIJK_OTTEN),
    TestName("henri g. kerkdijk-otten", HENRI_KERKDIJK_OTTEN),
    TestName("Patrick J. Michaels", PATRICK_MICHAELS),
    TestName("Paul C. Knappenberger", PAUL_KNAPPENBERGER),
    TestName("G.-Fivos Sargentis", G_FIVOS_SARGENTIS),
    TestName("Ian Rutherford Plimer", IAN_PLIMER),
    TestName("Jean-Pierre Bardinet", JEAN_PIERRE_BARDINET),
    TestName("Paul John O’Keeffe", PAUL_OKEEFFE),
    TestName("George 'Rob' Ryan", GEORGE_RYAN),
    TestName("Robert C. Balling Jr", ROBERT_BALLING),
    TestName("Dana H. Saylor Snr.", DANA_SAYLOR),
    TestName("Ambassador José Brechner", JOSE_BRECHNER),
    TestName("Dr. Michael Creech", MICHAEL_CREECH),
    TestName("Dr. Stephen David English", STEPHEN_ENGLISH),
    TestName("Dr. Jonhson Delibero Angelo", JONHSON_ANGELO),
    TestName("Lt.Col.(ret) Kevin A. Loughrey", KEVIN_LOUGHREY),
    TestName("Prof. Augustinus Johannes 'Guus' Berkhout", AUGUSTINUS_BERKHOUT),
    TestName("Prof. Ricardo Augusto Felicio", RICARDO_FELICIO),
    TestName("Christophe de Brouwer", CHRISTOPHE_BROUWER),
    TestName("Patrick de Casanove", PATRICK_CASANOVE),
    # FIXME: multi-token last_name
    # TestName("Mario de Carvalho Fontes Neto", MARIO_NETO),
    # TestName("Daniela de Souza Onça", DANIELA_ONCA),
    TestName("Francis le Gaillard", FRANCIS_GAILLARD),
    TestName("Dr. Cornelis le Pair", CORNELIS_PAIR),
    TestName("Dr. Thi Thuy Van Dinh", THI_DINH),
    TestName("Emiel Jozef Jan van Broekhoven", EMIEL_BROEKHOVEN),
    TestName("Appo van der Wiel", APPO_WIEL),
    TestName("G. Cornelis 'Kees' van Kooten", CORNELIS_KOOTEN),
    TestName("Prof. Frank C.J.M. van Veggel", FRANK_VEGGEL),
    TestName("William van Wijngaarden", WILLIAM_WIJNGAARDEN),
    TestName("Brigitte van Vliet-Lanoë", BRIGITTE_VLIET_LANOE),
    TestName("Dr. Tom van der Hoeven", TOM_HOEVEN),
    TestName("W.J. Evert van de Graaff", EVERT_GRAAFF),
    TestName("Mr. Adelino De Santi Jnr.", ADELINO_SANTI),
    TestName("Terry Dunleavy MBE", TERRY_DUNLEAVY),
    TestName("Terry Dunleavy mbe", TERRY_DUNLEAVY),
    # FIXME: certain post-nominals with periods.
    # TestName("Terry Dunleavy M.B.E.", TERRY_DUNLEAVY),
    # TestName("Terry Dunleavy m.b.e.", TERRY_DUNLEAVY),
    TestName("Dr. A. T. J. Laat", A_T_J_LAAT),
    TestName("Dr. A.T.J. Laat", A_T_J_LAAT),
    TestName("Dr. ATJ Laat", A_T_J_LAAT),
    # FIXME: 'title last_name, first_names' doesn't work:
    # TestName("Lord Moncton, Christopher", CHRISTOPHER_MONCTON),
    TestName("Lord Christopher Moncton", CHRISTOPHER_MONCTON),
    TestName("Osama bin Laden", OSAMA_LADEN),
    TestName("Prof. Liu Tungsheng (Liu Dongsheng)", LIU_TUNGSHENG),
    TestName("Prof. Antonis 'Anthony' Foskolos (Foscolos)", ANTONIS_FOSKOLOS),
    # TODO: add more 'last-name-last' examples
]

LAST_NAME_FIRST_NAMES = [
    TestName("Zybach, Bob", BOB_ZYBACH),
    TestName("Côté, Hortense", HORTENSE_COTE),
    TestName("Conti, José Bueno", JOSE_CONTI),
    TestName("Balla, József", JOZSEF_BALLA),
    TestName("Prud’homme, Rémy", REMY_PRUDHOMME),
    TestName("prud’homme, rémy", REMY_PRUDHOMME),
    TestName("Kerkdijk-Otten, Henri G.", HENRI_KERKDIJK_OTTEN),
    TestName("kerkdijk-otten, henri g.", HENRI_KERKDIJK_OTTEN),
    TestName("Michaels, Patrick J.", PATRICK_MICHAELS),
    TestName("Knappenberger, Paul C.", PAUL_KNAPPENBERGER),
    TestName("Sargentis, G.-Fivos", G_FIVOS_SARGENTIS),
    TestName("Plimer, Ian Rutherford", IAN_PLIMER),
    TestName("Bardinet, Jean-Pierre", JEAN_PIERRE_BARDINET),
    TestName("O’Keeffe, Paul John", PAUL_OKEEFFE),
    TestName("Ryan, George 'Rob'", GEORGE_RYAN),
    TestName("Balling Jr, Robert C.", ROBERT_BALLING),
    TestName("Saylor Snr., Dana H.", DANA_SAYLOR),
    TestName("Brechner, Ambassador José", JOSE_BRECHNER),
    TestName("Creech, Dr. Michael", MICHAEL_CREECH),
    TestName("English, Dr. Stephen David", STEPHEN_ENGLISH),
    TestName("Angelo, Dr. Jonhson Delibero", JONHSON_ANGELO),
    TestName("Loughrey, Lt.Col.(ret) Kevin A.", KEVIN_LOUGHREY),
    TestName("Berkhout, Prof. Augustinus Johannes 'Guus'", AUGUSTINUS_BERKHOUT),
    TestName("Felicio, Prof. Ricardo Augusto", RICARDO_FELICIO),
    TestName("de Brouwer, Christophe", CHRISTOPHE_BROUWER),
    TestName("de Casanove, Patrick", PATRICK_CASANOVE),
    # FIXME: multi-token last_name
    # TestName("de Carvalho Fontes Neto, Mario", MARIO_NETO),
    # TestName("de Souza Onça, Daniela", DANIELA_ONCA),
    TestName("le Gaillard, Francis", FRANCIS_GAILLARD),
    TestName("le Pair, Dr. Cornelis", CORNELIS_PAIR),
    TestName("Van Dinh, Dr. Thi Thuy", THI_DINH),
    TestName("van Broekhoven, Emiel Jozef Jan", EMIEL_BROEKHOVEN),
    TestName("van der Wiel, Appo", APPO_WIEL),
    TestName("van Kooten, G. Cornelis 'Kees'", CORNELIS_KOOTEN),
    TestName("van Veggel, Prof. Frank C.J.M.", FRANK_VEGGEL),
    TestName("van Wijngaarden, William", WILLIAM_WIJNGAARDEN),
    TestName("van Vliet-Lanoë, Brigitte", BRIGITTE_VLIET_LANOE),
    TestName("van der Hoeven, Dr. Tom", TOM_HOEVEN),
    TestName("van de Graaff, W.J. Evert", EVERT_GRAAFF),
    TestName("De Santi Jnr., Mr. Adelino", ADELINO_SANTI),
    TestName("Dunleavy, Terry MBE", TERRY_DUNLEAVY),
    TestName("Dunleavy, Terry mbe", TERRY_DUNLEAVY),
    # FIXME: certain post-nominals with periods.
    # TestName("Dunleavy, Terry M.B.E.", TERRY_DUNLEAVY),
    # TestName("Dunleavy, Terry m.b.e.", TERRY_DUNLEAVY),
    TestName("Laat, Dr. A. T. J.", A_T_J_LAAT),
    TestName("Laat, Dr. A.T.J.", A_T_J_LAAT),
    TestName("Laat, Dr. ATJ", A_T_J_LAAT),
    TestName("LAAT Dr. ATJ", A_T_J_LAAT),
    TestName("LAAT Dr. A.T.J.", A_T_J_LAAT),
    TestName("LAAT Dr. A. T. J.", A_T_J_LAAT),
    # FIXME: 'title last_name, first_names' doesn't work:
    # TestName("Lord Moncton, Christopher", CHRISTOPHER_MONCTON),
    TestName("Moncton, Lord Christopher", CHRISTOPHER_MONCTON),
    TestName("bin Laden, Osama", OSAMA_LADEN),
    TestName("Tungsheng, Prof. Liu (Liu Dongsheng)", LIU_TUNGSHENG),
    TestName("Foskolos, Prof. Antonis 'Anthony' (Foscolos)", ANTONIS_FOSKOLOS),
    # TODO: add more 'last-name-first' examples
]


class NameTests(TestCase):

    def parse_names(self, tests: list[TestName]):
        for test in tests:
            name = Name.parse(test.string)
            self.assertIsNotNone(name, f"'{test.string}' parsed to None")
            self.assertEqual(test.name, name, f"'{test.string}' did not match")

    def check_format(self, name: Name, formats: list[str], expected: list[str]):
        for i, format in enumerate(formats):
            namestr = name.format(format)
            self.assertEqual(expected[i], namestr)

    def test_parse_first_names_last_name(self):
        self.parse_names(FIRST_NAMES_LAST_NAME)

    def test_parse_last_name_first_names(self):
        self.parse_names(LAST_NAME_FIRST_NAMES)

    def test_format(self):
        nameFormats = [
            "%f%l%z",  # firstNames lastName postNominals
            "%t%f%p%l%s%z",  # title firstNames prefix lastName suffix postNominals
            "%t%i%p%l%s%z",  # title initials prefix lastName suffix postNominals
            "%p%l%s,%t%f%z",  # prefix lastName suffix, title firstNames postNominals
            "%p%l%s,%i",  # prefix lastName suffix, initials
        ]
        expected = [
            "Adelino Santi",
            "Mr. Adelino de Santi Jnr.",
            "Mr. A. de Santi Jnr.",
            "de Santi Jnr., Mr. Adelino",
            "de Santi Jnr., A.",
        ]
        self.check_format(ADELINO_SANTI, nameFormats, expected)

        expected = [
            "Terry Dunleavy MBE",
            "Terry Dunleavy MBE",
            "T. Dunleavy MBE",
            "Dunleavy, Terry MBE",
            "Dunleavy, T.",
        ]
        self.check_format(TERRY_DUNLEAVY, nameFormats, expected)

        initialFormats = [
            "%i",
            "%I",
            "%J",
            "%K",
        ]
        expected = [
            "F. C. J. M.",
            "FCJM",
            "F C J M",
            "F.C.J.M.",
        ]
        self.check_format(FRANK_VEGGEL, initialFormats, expected)

    def test_initials(self):
        name = FRANK_VEGGEL
        self.assertEqual("F. C. J. M.", name.initials())
        self.assertEqual("F. C. J. M.", name.initials(True, True))
        self.assertEqual("FCJM", name.initials(False, False))
        self.assertEqual("F C J M", name.initials(False, True))
        self.assertEqual("F.C.J.M.", name.initials(True, False))
