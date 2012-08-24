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

import org.dom4j.Document;
import org.geolatte.common.testDb.TestDb;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static junit.framework.Assert.assertNotNull;
import static org.geolatte.common.testDb.TestDb.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class AutoMapperTest {

    private static Logger LOGGER = LoggerFactory.getLogger(AutoMapperTest.class);
    private static TestDb server;

    @BeforeClass
    static public void before() throws SQLException {
        server = new TestDb();
        initGeoDB();
    }

    @Test
    public void test() throws SQLException {
        String sql1 = "create table testautomap (id integer primary key, name varchar, num int, price double, geometry geometry)";
        doWithinConnection(sql1);

        AutoMapConfig cfg = new AutoMapConfig(new TypeMapper("BLOB"));
        TableRef tblRef = TableRef.valueOf("TESTAUTOMAP");
        cfg.addTable(tblRef);

        final AutoMapper autoMapper = new AutoMapper(cfg);
        Document mapping = runAutoMapper(autoMapper);
        assertNotNull(mapping);
        LOGGER.debug("Mapping file:\n" + mapping.asXML());
        assertEquals(cfg.getPackageName(), mapping.selectSingleNode("//hibernate-mapping/@package").getText());
        assertEquals(cfg.getPackageName() + ".Testautomap", autoMapper.getClass(TableRef.valueOf("TESTAUTOMAP")).getCanonicalName());
        assertEquals("Testautomap", mapping.selectSingleNode("//hibernate-mapping/class/@name").getText());
        assertEquals("TESTAUTOMAP", mapping.selectSingleNode("//hibernate-mapping/class/@table").getText());
        assertEquals("id", mapping.selectSingleNode("//hibernate-mapping/class/id/@name").getText());
        assertEquals("integer", mapping.selectSingleNode("//hibernate-mapping/class/id/@type").getText());
        assertEquals("GEOMETRY", mapping.selectSingleNode("//hibernate-mapping/class/property[@name='geometry']/@column").getText());
        assertEquals("org.hibernatespatial.GeometryUserType", mapping.selectSingleNode("//hibernate-mapping/class/property[@name='geometry']/@type").getText());
        assertEquals("geometry", autoMapper.getGeometryAttribute(TableRef.valueOf("TESTAUTOMAP")));
        assertEquals("id", autoMapper.getIdAttribute(TableRef.valueOf("TESTAUTOMAP")));
    }

    private Document runAutoMapper(final AutoMapper autoMapper) throws SQLException {
        return (Document)server.doWithinConnection (
                    new DbOp (){
                        @Override
                        public Object execute(Connection conn) throws SQLException {
                            return autoMapper.map(conn);
                        }
                    }
            );
    }

    @After
    public void cleanUp() throws SQLException {
        cleanDatabase();
    }

    @AfterClass
    public static void after() {
        server.stop();
    }

}
