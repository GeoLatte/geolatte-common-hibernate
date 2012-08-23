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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.geolatte.common.testDb.TestDb.cleanDatabase;
import static org.geolatte.common.testDb.TestDb.doWithinConnection;
import static org.geolatte.common.testDb.TestDb.initGeoDB;
import static org.junit.Assert.assertEquals;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class AutoMapperTest {

    final private static String sql1 = "create table testautomap (id integer primary key, name varchar, num int, price double, geometry geometry)";


    private static TestDb server;

    private static Document mapping = null;

    @BeforeClass
    static public void before() throws SQLException {
        server = new TestDb();
        initGeoDB();
        doWithinConnection(sql1);
        final List<String> tableNames = new ArrayList<String>();
        tableNames.add("TESTAUTOMAP");
        mapping = (Document)server.doWithinConnection (
                new TestDb.DbOp (){
                    @Override
                    public Object execute(Connection conn) throws SQLException {
                        return AutoMapper.map(conn, null, null, tableNames);
                    }
                }
        );

    }

    @Test
    public void test() throws GeometryNotFoundException {
        assertNotNull(mapping);
        System.out.println(mapping.asXML());
        assertEquals("geometry", AutoMapper.getGeometryAttribute(null, null, "testautomap"));

    }

    @AfterClass
    public static void after() throws SQLException {
        cleanDatabase();
        server.stop();
    }

}
