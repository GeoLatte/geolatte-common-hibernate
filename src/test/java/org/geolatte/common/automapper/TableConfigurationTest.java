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
 *         creation-date: 8/24/12
 */
public class TableConfigurationTest {

    @Test
    public void testNormalConstruction() {
        TableConfiguration.Builder builder = new TableConfiguration.Builder(TableRef.valueOf("catalog", "schema", "table"));
        TableConfiguration configuration =
                builder
                        .identifier("id")
                        .geometry("geom")
                        .exclude("excl")
                        .result();

        assertEquals("catalog", configuration.getCatalog());
        assertEquals("schema", configuration.getSchema());
        assertEquals("table", configuration.getTableName());
        assertEquals("id", configuration.getIdentifierColumn());
        assertEquals("excl", configuration.getExcludedColumns().get(0));
        assertEquals("geom", configuration.getGeometryColumn());
    }

    @Test
    public void testNoCatalogOrSchema() {
        TableConfiguration cfg = new TableConfiguration.Builder(TableRef.valueOf("table")).result();
        assertNull(cfg.getSchema());
        assertNull(cfg.getCatalog());
        assertEquals("table", cfg.getTableName());
    }

    @Test
    public void testNoIdOrGeomConstruction() {
        TableConfiguration cfg = new TableConfiguration.Builder(TableRef.valueOf("table")).result();
        assertNull(cfg.getGeometryColumn());
        assertNull(cfg.getIdentifierColumn());
        assertNotNull(cfg.getExcludedColumns());
        assertTrue(cfg.getExcludedColumns().isEmpty());
    }

}
