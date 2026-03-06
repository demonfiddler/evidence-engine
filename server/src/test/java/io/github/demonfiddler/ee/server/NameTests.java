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

package io.github.demonfiddler.ee.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.demonfiddler.ee.server.model.Name;

class NameTests {

    private static record TestName(String string, Name name) {
    }

    private static final Name BOB_ZYBACH = new Name("Bob", "Zybach");
    private static final Name JOSE_CONTI = new Name("José Bueno", "Conti");
    private static final Name HORTENSE_COTE = new Name("Hortense", "Côté");
    private static final Name JOZSEF_BALLA = new Name("József", "Balla");
    private static final Name REMY_PRUDHOMME = new Name("Rémy", "Prud’homme");
    private static final Name HENRI_KERKDIJK_OTTEN = new Name("Henri G.", "Kerkdijk-Otten");
    private static final Name PATRICK_MICHAELS = new Name("Patrick J.", "Michaels");
    private static final Name PAUL_KNAPPENBERGER = new Name("Paul C.", "Knappenberger");
    private static final Name G_FIVOS_SARGENTIS = new Name("G. -Fivos", "Sargentis");
    private static final Name IAN_PLIMER = new Name("Ian Rutherford", "Plimer");
    private static final Name JEAN_PIERRE_BARDINET = new Name("Jean-Pierre", "Bardinet");
    private static final Name PAUL_OKEEFFE = new Name("Paul John", "O’Keeffe");
    private static final Name GEORGE_RYAN = new Name(null, "George", "Rob", null, "Ryan", null, null);
    private static final Name ROBERT_BALLING = new Name(null, "Robert C.", null, null, "Balling", "Jr", null);
    private static final Name DANA_SAYLOR = new Name(null, "Dana H.", null, null, "Saylor", "Snr.", null);
    private static final Name JOSE_BRECHNER = new Name("Ambassador", "José", null, null, "Brechner", null, null);
    private static final Name MICHAEL_CREECH = new Name("Dr.", "Michael", null, null, "Creech", null, null);
    private static final Name STEPHEN_ENGLISH = new Name("Dr.", "Stephen David", null, null, "English", null, null);
    private static final Name JONHSON_ANGELO = new Name("Dr.", "Jonhson Delibero", null, null, "Angelo", null, null);
    private static final Name KEVIN_LOUGHREY = new Name("Lt. Col. (ret)", "Kevin A.", null, null, "Loughrey", null, null);
    private static final Name AUGUSTINUS_BERKHOUT =
        new Name("Prof.", "Augustinus Johannes", "Guus", null, "Berkhout", null, null);
    private static final Name RICARDO_FELICIO = new Name("Prof.", "Ricardo Augusto", null, null, "Felicio", null, null);
    private static final Name CHRISTOPHE_BROUWER = new Name(null, "Christophe", null, "de", "Brouwer", null, null);
    private static final Name PATRICK_CASANOVE = new Name(null, "Patrick", null, "de", "Casanove", null, null);
    // private static final Name MARIO_NETO = new Name(null, "Mario", "de", "Carvalho Fontes Neto", null);
    // private static final Name DANIELA_ONCA = new Name(null, "Daniela", "de", "Souza Onça", null);
    private static final Name FRANCIS_GAILLARD = new Name(null, "Francis", null, "le", "Gaillard", null, null);
    private static final Name CORNELIS_PAIR = new Name("Dr.", "Cornelis", null, "le", "Pair", null, null);
    private static final Name THI_DINH = new Name("Dr.", "Thi Thuy", null, "Van", "Dinh", null, null);
    private static final Name EMIEL_BROEKHOVEN = new Name(null, "Emiel Jozef Jan", null, "van", "Broekhoven", null, null);
    private static final Name APPO_WIEL = new Name(null, "Appo", null, "van der", "Wiel", null, null);
    private static final Name CORNELIS_KOOTEN = new Name(null, "G. Cornelis", "Kees", "van", "Kooten", null, null);
    private static final Name FRANK_VEGGEL = new Name("Prof.", "Frank C. J. M.", null, "van", "Veggel", null, null);
    private static final Name WILLIAM_WIJNGAARDEN = new Name(null, "William", null, "van", "Wijngaarden", null, null);
    private static final Name BRIGITTE_VLIET_LANOE = new Name(null, "Brigitte", null, "van", "Vliet-Lanoë", null, null);
    private static final Name TOM_HOEVEN = new Name("Dr.", "Tom", null, "van der", "Hoeven", null, null);
    private static final Name EVERT_GRAAFF = new Name(null, "W. J. Evert", null, "van de", "Graaff", null, null);
    private static final Name ADELINO_SANTI = new Name("Mr.", "Adelino", null, "De", "Santi", "Jnr.", null);
    private static final Name TERRY_DUNLEAVY = new Name(null, "Terry", null, null, "Dunleavy", null, "MBE");

    private static final List<TestName> FIRST_NAMES_LAST_NAME = List.of( //
        new TestName("Bob Zybach", BOB_ZYBACH), //
        new TestName("Hortense Côté", HORTENSE_COTE), //
        new TestName("José Bueno Conti", JOSE_CONTI), //
        new TestName("József Balla", JOZSEF_BALLA), //
        new TestName("Rémy Prud’homme", REMY_PRUDHOMME), //
        new TestName("Henri G. Kerkdijk-Otten", HENRI_KERKDIJK_OTTEN), //
        new TestName("Patrick J. Michaels", PATRICK_MICHAELS), //
        new TestName("Paul C. Knappenberger", PAUL_KNAPPENBERGER), //
        new TestName("G.-Fivos Sargentis", G_FIVOS_SARGENTIS), //
        new TestName("Ian Rutherford Plimer", IAN_PLIMER), //
        new TestName("Jean-Pierre Bardinet", JEAN_PIERRE_BARDINET), //
        new TestName("Paul John O’Keeffe", PAUL_OKEEFFE), //
        new TestName("George 'Rob' Ryan", GEORGE_RYAN), //
        new TestName("Robert C. Balling Jr", ROBERT_BALLING), //
        new TestName("Dana H. Saylor Snr.", DANA_SAYLOR), //
        new TestName("Ambassador José Brechner", JOSE_BRECHNER), //
        new TestName("Dr. Michael Creech", MICHAEL_CREECH), //
        new TestName("Dr. Stephen David English", STEPHEN_ENGLISH), //
        new TestName("Dr. Jonhson Delibero Angelo", JONHSON_ANGELO), //
        new TestName("Lt.Col.(ret) Kevin A. Loughrey", KEVIN_LOUGHREY), //
        new TestName("Prof. Augustinus Johannes 'Guus' Berkhout", AUGUSTINUS_BERKHOUT), //
        new TestName("Prof. Ricardo Augusto Felicio", RICARDO_FELICIO), //
        new TestName("Christophe de Brouwer", CHRISTOPHE_BROUWER), //
        new TestName("Patrick de Casanove", PATRICK_CASANOVE), //
        // new TestName("Mario de Carvalho Fontes Neto", MARIO_NETO), //
        // new TestName("Daniela de Souza Onça", DANIELA_ONCA), //
        new TestName("Francis le Gaillard", FRANCIS_GAILLARD), //
        new TestName("Dr. Cornelis le Pair", CORNELIS_PAIR), //
        new TestName("Dr. Thi Thuy Van Dinh", THI_DINH), //
        new TestName("Emiel Jozef Jan van Broekhoven", EMIEL_BROEKHOVEN), //
        new TestName("Appo van der Wiel", APPO_WIEL), //
        new TestName("G. Cornelis 'Kees' van Kooten", CORNELIS_KOOTEN), //
        new TestName("Prof. Frank C.J.M. van Veggel", FRANK_VEGGEL), //
        new TestName("William van Wijngaarden", WILLIAM_WIJNGAARDEN), //
        new TestName("Brigitte van Vliet-Lanoë", BRIGITTE_VLIET_LANOE), //
        new TestName("Dr. Tom van der Hoeven", TOM_HOEVEN), //
        new TestName("W.J. Evert van de Graaff", EVERT_GRAAFF), //
        new TestName("Mr. Adelino De Santi Jnr.", ADELINO_SANTI), //
        new TestName("Terry Dunleavy MBE", TERRY_DUNLEAVY) //
    );

    private static final List<TestName> LAST_NAME_FIRST_NAMES = List.of( //
        new TestName("Zybach, Bob", BOB_ZYBACH), //
        new TestName("Côté, Hortense", HORTENSE_COTE), //
        new TestName("Conti, José Bueno", JOSE_CONTI), //
        new TestName("Balla, József", JOZSEF_BALLA), //
        new TestName("Prud’homme, Rémy", REMY_PRUDHOMME), //
        new TestName("Kerkdijk-Otten, Henri G.", HENRI_KERKDIJK_OTTEN), //
        new TestName("Michaels, Patrick J.", PATRICK_MICHAELS), //
        new TestName("Knappenberger, Paul C.", PAUL_KNAPPENBERGER), //
        new TestName("Sargentis, G.-Fivos", G_FIVOS_SARGENTIS), //
        new TestName("Plimer, Ian Rutherford", IAN_PLIMER), //
        new TestName("Bardinet, Jean-Pierre", JEAN_PIERRE_BARDINET), //
        new TestName("O’Keeffe, Paul John", PAUL_OKEEFFE), //
        new TestName("Ryan, George 'Rob'", GEORGE_RYAN), //
        new TestName("Balling Jr, Robert C.", ROBERT_BALLING), //
        new TestName("Saylor Snr., Dana H.", DANA_SAYLOR), //
        new TestName("Brechner, Ambassador José", JOSE_BRECHNER), //
        new TestName("Creech, Dr. Michael", MICHAEL_CREECH), //
        new TestName("English, Dr. Stephen David", STEPHEN_ENGLISH), //
        new TestName("Angelo, Dr. Jonhson Delibero", JONHSON_ANGELO), //
        new TestName("Loughrey, Lt.Col.(ret) Kevin A.", KEVIN_LOUGHREY), //
        new TestName("Berkhout, Prof. Augustinus Johannes 'Guus'", AUGUSTINUS_BERKHOUT), //
        new TestName("Felicio, Prof. Ricardo Augusto", RICARDO_FELICIO), //
        new TestName("de Brouwer, Christophe", CHRISTOPHE_BROUWER), //
        new TestName("de Casanove, Patrick", PATRICK_CASANOVE), //
        // new TestName("de Carvalho Fontes Neto, Mario", MARIO_NETO), //
        // new TestName("de Souza Onça, Daniela", DANIELA_ONCA), //
        new TestName("le Gaillard, Francis", FRANCIS_GAILLARD), //
        new TestName("le Pair, Dr. Cornelis", CORNELIS_PAIR), //
        new TestName("Van Dinh, Dr. Thi Thuy", THI_DINH), //
        new TestName("van Broekhoven, Emiel Jozef Jan", EMIEL_BROEKHOVEN), //
        new TestName("van der Wiel, Appo", APPO_WIEL), //
        new TestName("van Kooten, G. Cornelis 'Kees'", CORNELIS_KOOTEN), //
        new TestName("van Veggel, Prof. Frank C.J.M.", FRANK_VEGGEL), //
        new TestName("van Wijngaarden, William", WILLIAM_WIJNGAARDEN), //
        new TestName("van Vliet-Lanoë, Brigitte", BRIGITTE_VLIET_LANOE), //
        new TestName("van der Hoeven, Dr. Tom", TOM_HOEVEN), //
        new TestName("van de Graaff, W.J. Evert", EVERT_GRAAFF), //
        new TestName("De Santi Jnr., Mr. Adelino", ADELINO_SANTI), //
        new TestName("Dunleavy, Terry MBE", TERRY_DUNLEAVY) //
    );

    @Test
    void parseFirstNamesLastName() {
        parseNames(FIRST_NAMES_LAST_NAME);
    }

    @Test
    void parseLastNameFirstNames() {
        parseNames(LAST_NAME_FIRST_NAMES);
    }

    @Test
    void format() {
        String[] nameFormats = { //
            "%f%l%z", // firstNames lastName postNominals
            "%t%f%p%l%s%z", // title firstNames prefix lastName suffix postNominals
            "%t%i%p%l%s%z", // title initials prefix lastName suffix postNominals
            "%p%l%s,%t%f%z", // prefix lastName suffix, title firstNames postNominals
            "%p%l%s,%i", // prefix lastName suffix, initials
        };
        String[] expected = { //
            "Adelino Santi", //
            "Mr. Adelino De Santi Jnr.", //
            "Mr. A. De Santi Jnr.", //
            "De Santi Jnr., Mr. Adelino", //
            "De Santi Jnr., A.", //
        };
        checkFormat(ADELINO_SANTI, nameFormats, expected);

        expected = new String[] { //
            "Terry Dunleavy MBE", //
            "Terry Dunleavy MBE", //
            "T. Dunleavy MBE", //
            "Dunleavy, Terry MBE", //
            "Dunleavy, T.", //
        };
        checkFormat(TERRY_DUNLEAVY, nameFormats, expected);

        String[] initialFormats = { //
            "%i", //
            "%I", //
            "%J", //
            "%K", //
        };
        expected = new String[] { //
            "F. C. J. M.", //
            "FCJM", //
            "F C J M", //
            "F.C.J.M.", //
        };
        checkFormat(FRANK_VEGGEL, initialFormats, expected);
    }

    @Test
    void initials() {
        Name name = FRANK_VEGGEL;
        assertEquals("F. C. J. M.", name.getInitials());
        assertEquals("F. C. J. M.", name.getInitials(true, true));
        assertEquals("FCJM", name.getInitials(false, false));
        assertEquals("F C J M", name.getInitials(false, true));
        assertEquals("F.C.J.M.", name.getInitials(true, false));
    }

    private void parseNames(List<TestName> tests) {
        for (TestName test : tests) {
            Name name = Name.parse(test.string);
            assertNotNull(name, test.string + " parsed to null");
            assertEquals(test.name, name);
        }
    }

    private void checkFormat(Name name, String[] formats, String[] expected) {
        for (int i = 0; i < formats.length; i++) {
            String namestr = name.format(formats[i]);
            assertEquals(expected[i], namestr);
        }
    }

}
