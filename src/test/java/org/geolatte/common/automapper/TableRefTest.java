/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010 - 2012 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.common.automapper;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/1/12
 */
public class TableRefTest {

    TableRef tr;

    @Test
    public void testValidNames(){
        tr = TableRef.valueOf("cat.schem.tab");
        assertEquals("cat", tr.getCatalog());
        assertEquals("schem", tr.getSchema());
        assertEquals("tab", tr.getTableName());

        tr = TableRef.valueOf("schem.tab");
        assertNull(tr.getCatalog());
        assertEquals("schem", tr.getSchema());
        assertEquals("tab", tr.getTableName());


        tr = TableRef.valueOf("tab");
        assertNull(tr.getCatalog());
        assertNull(tr.getSchema());
        assertEquals("tab", tr.getTableName());


        String str = tr.toString();
        assertEquals(tr, TableRef.valueOf(str));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInValidEmptyName(){
        tr = TableRef.valueOf("");
     }

    @Test (expected = IllegalArgumentException.class)
    public void testInValidNullName(){
        tr = TableRef.valueOf((String)null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInValidNumComponentsInName(){
        tr = TableRef.valueOf("extrra.cat.schem.table");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInValidZeroComponentsInName(){
        tr = TableRef.valueOf(new String[0]);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInValidNullComponentArray(){
        tr = TableRef.valueOf((String[])null);
    }

    @Test
    public void testEmptyComponents(){
        tr = TableRef.valueOf("..table");
        assertNull(tr.getCatalog());
        assertNull(tr.getSchema());
        assertEquals("table", tr.getTableName());

        tr = new TableRef("", "", "table");
        assertNull(tr.getCatalog());
        assertNull(tr.getSchema());
        assertEquals("table", tr.getTableName());
    }

    @Test
    public void testToString(){
        tr = TableRef.valueOf("cat.schem.tab");
        assertEquals("cat.schem.tab", tr.toString());

        tr = TableRef.valueOf("schem.tab");
        assertEquals("schem.tab", tr.toString());

        tr = TableRef.valueOf("tab");
        assertEquals("tab", tr.toString());

        tr = TableRef.valueOf(null, "tab");
        assertEquals("tab", tr.toString());

        tr = TableRef.valueOf(null, null, "tab");
        assertEquals("tab", tr.toString());

        tr = TableRef.valueOf("tab");
        assertEquals("tab", tr.toString());


        tr = TableRef.valueOf("cat", null, "tab");
        assertEquals("cat.*.tab", tr.toString());

    }

}
