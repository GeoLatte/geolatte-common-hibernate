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
import org.geolatte.common.testDb.GeoDBWrapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.geolatte.common.testDb.GeoDBWrapper.*;
import static org.junit.Assert.*;

/**
* @author Karel Maesen, Geovise BVBA
*         creation-date: 8/23/12
*/
public class AutoMapperTest {

    private static Logger LOGGER = LoggerFactory.getLogger(AutoMapperTest.class);
    private static GeoDBWrapper server;

    @BeforeClass
    static public void beforeClass() throws SQLException {
        server = new GeoDBWrapper();
    }

    @Before
    public void before() throws SQLException {
        initGeoDB();
    }

    @Test
    public void testStandard() throws Exception {
        doWithinConnection("create table testautomap (id integer primary key, name varchar, num int, price double, geometry geometry)");
        doWithinConnection("insert into testautomap values (1, 'test', 2, 2.43, ST_GeomFromText('POINT(1 1)', 4326))");

        AutoMapConfiguration cfg = new AutoMapConfiguration(new TypeMapper("BLOB"));
        TableRef tblRef = TableRef.valueOf("TESTAUTOMAP");
        cfg.addTable(tblRef);

        //Test that the configuration is successful
        final AutoMapper autoMapper = new AutoMapper(cfg, disposableCL());
        Document mapping = runAutoMapper(autoMapper);
        assertNotNull(mapping);
        LOGGER.debug("Mapping file:\n" + mapping.asXML());
        assertEquals(cfg.getPackageName(), mapping.selectSingleNode("//hibernate-mapping/@package").getText());
        assertEquals(cfg.getPackageName() + ".Testautomap", autoMapper.getTableMapping(TableRef.valueOf("TESTAUTOMAP")).getGeneratedClass().getCanonicalName());
        assertEquals("Testautomap", mapping.selectSingleNode("//hibernate-mapping/class/@name").getText());
        assertEquals("TESTAUTOMAP", mapping.selectSingleNode("//hibernate-mapping/class/@table").getText());
        assertEquals("id", mapping.selectSingleNode("//hibernate-mapping/class/id/@name").getText());
        assertEquals("integer", mapping.selectSingleNode("//hibernate-mapping/class/id/@type").getText());
        assertEquals("GEOMETRY", mapping.selectSingleNode("//hibernate-mapping/class/property[@name='geometry']/@column").getText());
        assertEquals("org.hibernatespatial.GeometryUserType", mapping.selectSingleNode("//hibernate-mapping/class/property[@name='geometry']/@type").getText());
        assertEquals("geometry", autoMapper.getGeometryProperty(TableRef.valueOf("TESTAUTOMAP")));
        assertEquals("id", autoMapper.getIdProperty(TableRef.valueOf("TESTAUTOMAP")));

        //check if information can be retrieved
        final SessionFactory factory = buildSessionFactory(mapping);
        doWithinTransaction(factory, new TxOp(){
            public void execute(Session session){
                Criteria criteria = session.createCriteria(autoMapper.getGeneratedClass(TableRef.valueOf("TESTAUTOMAP")));
                List list = criteria.list();
                assertTrue(list.size() == 1);
            }
        });
        factory.close();
    }

    @Test
    public void testConfiguredId() throws Exception {
        doWithinConnection("create table testautomap (id integer, name varchar, num int, price double, geometry geometry)");
        doWithinConnection("insert into testautomap values (1, 'test', 2, 2.43, ST_GeomFromText('POINT(1 1)', 4326))");
        doWithinConnection("insert into testautomap values (1, 'test 2', 3, 2.43, ST_GeomFromText('POINT(2 2)', 4326))");

        AutoMapConfiguration cfg = new AutoMapConfiguration(new TypeMapper("BLOB"));
        TableRef tblRef = TableRef.valueOf("TESTAUTOMAP");
        cfg.addTableConfiguration(new TableConfiguration.Builder(tblRef).withId("ID").result());

        //Test that the configuration is successful
        final AutoMapper autoMapper = new AutoMapper(cfg, disposableCL());
        Document mapping = runAutoMapper(autoMapper);
        assertNotNull(mapping);
        LOGGER.debug("Mapping file:\n" + mapping.asXML());
        assertEquals("Testautomap", mapping.selectSingleNode("//hibernate-mapping/class/@name").getText());
        assertEquals("TESTAUTOMAP", mapping.selectSingleNode("//hibernate-mapping/class/@table").getText());
        assertEquals("id", mapping.selectSingleNode("//hibernate-mapping/class/id/@name").getText());
        assertEquals("id", autoMapper.getIdProperty(TableRef.valueOf("TESTAUTOMAP")));

        //check if information can be retrieved
        final SessionFactory factory = buildSessionFactory(mapping);
        doWithinTransaction(factory, new TxOp(){
            public void execute(Session session){
                Criteria criteria = session.createCriteria(autoMapper.getGeneratedClass(TableRef.valueOf("TESTAUTOMAP")));
                List list = criteria.list();
                assertTrue(list.size() == 2);
            }
        });
        factory.close();
    }

    @Test
    public void testNoPKeyThereClassIsNotMapped() throws SQLException {
        doWithinConnection("create table testautomap (id integer, name varchar, num int, price double, geometry geometry)");
        doWithinConnection("insert into testautomap values (1, 'test', 2, 2.43, ST_GeomFromText('POINT(1 1)', 4326))");

        AutoMapConfiguration cfg = new AutoMapConfiguration(new TypeMapper("BLOB"));
        TableRef tblRef = TableRef.valueOf("TESTAUTOMAP");
        cfg.addTable(tblRef);

        //Test that the configuration is successful
        final AutoMapper autoMapper = new AutoMapper(cfg, disposableCL());
        Document mapping = runAutoMapper(autoMapper);
        assertNotNull(mapping);
        assertNull(mapping.selectSingleNode("//hibernate-mapping/class/@name"));
        assertNull(autoMapper.getIdProperty(TableRef.valueOf("TESTAUTOMAP")));
        assertNull(autoMapper.getTableMapping(TableRef.valueOf("TESTAUTOMAP")));
        LOGGER.debug("Mapping file:\n" + mapping.asXML());
    }

    @Test
    public void testExcludedColumnsAreNotMapped() throws Exception {
        doWithinConnection("create table testautomap (id integer primary key, name varchar, num int, price double, geometry geometry)");
        doWithinConnection("insert into testautomap values (1, 'test', 2, 2.43, ST_GeomFromText('POINT(1 1)', 4326))");

        TableConfiguration tableCfg = new TableConfiguration.Builder(TableRef.valueOf("TESTAUTOMAP")).exclude("PRICE").result();
        AutoMapConfiguration cfg = new AutoMapConfiguration(new TypeMapper("BLOB"));
        cfg.addTableConfiguration(tableCfg);

        //Test that the configuration is successful
        final AutoMapper autoMapper = new AutoMapper(cfg, disposableCL());
        Document mapping = runAutoMapper(autoMapper);
        assertNotNull(mapping);
        LOGGER.debug("Mapping file:\n" + mapping.asXML());
        assertEquals(cfg.getPackageName(), mapping.selectSingleNode("//hibernate-mapping/@package").getText());
        assertEquals(cfg.getPackageName() + ".Testautomap", autoMapper.getTableMapping(TableRef.valueOf("TESTAUTOMAP")).getGeneratedClass().getCanonicalName());
        assertEquals("Testautomap", mapping.selectSingleNode("//hibernate-mapping/class/@name").getText());
        assertEquals("TESTAUTOMAP", mapping.selectSingleNode("//hibernate-mapping/class/@table").getText());
        assertEquals("id", mapping.selectSingleNode("//hibernate-mapping/class/id/@name").getText());
        assertEquals("integer", mapping.selectSingleNode("//hibernate-mapping/class/id/@type").getText());
        assertEquals("GEOMETRY", mapping.selectSingleNode("//hibernate-mapping/class/property[@name='geometry']/@column").getText());
        assertEquals("org.hibernatespatial.GeometryUserType", mapping.selectSingleNode("//hibernate-mapping/class/property[@name='geometry']/@type").getText());
        assertEquals("id", autoMapper.getIdProperty(TableRef.valueOf("TESTAUTOMAP")));
        assertNull(mapping.selectSingleNode("//hibernate-mapping/class/property[@name='price']"));

    }


    private DisposableClassLoader disposableCL() {
        DisposableClassLoader cl =  new DisposableClassLoader(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(cl);
        return cl;
    }

    private SessionFactory buildSessionFactory(Document mapping) {
        Configuration config = new Configuration().configure();
        config.addXML(mapping.asXML());
        return config.buildSessionFactory();
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

    private void doWithinTransaction(final SessionFactory sf, TxOp op) throws Exception {
         Transaction tx = null;
        try {
            tx = sf.getCurrentSession().beginTransaction();
            op.execute(sf.getCurrentSession());
            tx.commit();
        } catch(Exception e) {
            LOGGER.warn(e.getMessage(), e);
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @After
    public void after() throws SQLException {
        cleanDatabase();
    }

    @AfterClass
    public static void afterClass() {
        server.stop();
    }

    static interface TxOp{
        void execute(Session session);
    }
}
