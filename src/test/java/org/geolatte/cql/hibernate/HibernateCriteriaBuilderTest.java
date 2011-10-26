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
 * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.cql.hibernate;

import org.geolatte.common.cql.AbstractCriteriaBuilderTest;
import org.geolatte.testobjects.FilterableObject;
import org.h2.tools.Server;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * Tests the HibernateCriteriaBuilder class. Created Hibernate Criteria are tested against the H2 memory database.
 * </p>
 * <p>
 * <i>Creation-Date</i>: 31-May-2010<br>
 * <i>Creation-Time</i>:  10:47:33<br>
 * </p>
 *
 * @author Bert Vanhooff
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 * @since SDK1.5
 */
@SuppressWarnings({"ParameterizedParametersStaticCollection"}) // @Parameterized.Parameters is defined in super class
@RunWith(Parameterized.class)
public class HibernateCriteriaBuilderTest extends AbstractCriteriaBuilderTest {

    private static final String tcpPort = "4321";
    private static final String dbLocation = "~/geolattetest";
    private static Server server;
    private static Server webServer;

    /**
     * Constructor
     *
     * @param cqlString      The CQL string to test.
     * @param testObject     The object to give to the filter.
     * @param expectedResult The expected outcome of the test.
     */
    public HibernateCriteriaBuilderTest(String cqlString, Object testObject, boolean expectedResult) {
        super(cqlString, testObject, expectedResult);
    }

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {

        // start the server, allows to access the database remotly
        server = Server.createTcpServer("-tcpPort", tcpPort);
        webServer = Server.createWebServer("-webPort", "8123");
        server.start();
        webServer.start();
        System.out.println("H2 Database started on port " + tcpPort );
        // now use the database in your application in embedded mode
        Class.forName("org.h2.Driver");
        System.out.println("Connecting to database " + dbLocation);
        Connection conn = DriverManager.getConnection("jdbc:h2:" + dbLocation, "sa", "sa");
        // Execute a random query to check if everything works
        Statement stat = conn.createStatement();
        System.out.println("Dropping table FilterableObject");
        stat.execute("DROP TABLE azerty IF EXISTS");
        conn.close();
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {

        // stop the server
        server.stop();
        server.stop();
    }

    @Before
    public void setUp() throws Exception {

        cleanDatabase();
    }

    /**
     * Verifies a single test case.
     */
    @Test
    public void parameterizedTest() throws Exception {

        HibernateUtil hibernateUtil = new HibernateUtil();
        hibernateUtil.createDatabase();

        System.out.println("Verifying that \"" + cqlString + "\" is " + expectedResult + " with ");
        System.out.println("    " + testObject);

        try {

            // save the test object
            save(testObject, hibernateUtil);
            System.out.println("Total saved objects = " + loadAll(hibernateUtil).size());


            // verify whether we can load it back from the database using the given query
            DetachedCriteria criteria = HibernateCQLAdapter.toCriteria(cqlString, FilterableObject.class);
            List<FilterableObject> loadedObjects = load(criteria, hibernateUtil);
            if (expectedResult) { // if expectedresult = true, the test object should be loaded
                Assert.assertTrue("Number of objects loaded does not equal 1", loadedObjects.size() == 1);
                Assert.assertEquals("Loaded object does not equal what was expected", testObject, loadedObjects.get(0));
            }
            else
                Assert.assertTrue("At least one object was loaded, while expecting none.", loadedObjects.isEmpty());
        }
        catch (AssertionError e) {

            throw new AssertionError(e.getMessage() + " / CQL=" + cqlString + " | Expected=" + expectedResult + " | object=" + testObject.toString());
        }
        catch (ParseException e) {
            Assert.fail("Parsing failed");
            throw e;
        }
        catch (Exception e) {
            Assert.fail("Exception occured.");
            throw e;
        }
    }

    /**
     * Helper to save an object to the database.
     * @param obj The object to save
     * @param hibernateUtil Hibernate utility class used to access the database.
     */
    private void save(Object obj, HibernateUtil hibernateUtil) {

        Session session = hibernateUtil.getSessionFactory().openSession();

        Transaction transaction = session.beginTransaction();
        session.save(obj);
        session.flush();
        transaction.commit();

        session.close();
    }

    /**
     * Helper to load a collection of objects from the database, according to the given criteria.
     * @param detachedCriteria Criteria used to load the objects.
     * @param hibernateUtil Hibernate utility class used to access the database.
     * @return The list of loaded objects.
     */
    private List<FilterableObject> load(DetachedCriteria detachedCriteria, HibernateUtil hibernateUtil) {

        Session session = hibernateUtil.getSessionFactory().openSession();
        Criteria criteria = detachedCriteria == null ? null : detachedCriteria.getExecutableCriteria(session);

        HibernateHqlAndCriteriaToSqlTranslator trans = new HibernateHqlAndCriteriaToSqlTranslator();
        trans.setSessionFactory(hibernateUtil.getSessionFactory());
        System.out.println("SQL executed = " + trans.toSql(criteria));

        return criteria.list();

    }

    private List<FilterableObject> loadAll(HibernateUtil hibernateUtil) {

        Session session = hibernateUtil.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(FilterableObject.class);
        return criteria.list();
    }

    /**
     * Helper to remove all objects from the database.
     * @throws java.sql.SQLException When clean database failed due to an database error.
     */
    private void cleanDatabase() throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:h2:" + dbLocation, "sa", "sa");
        // Execute a random query to check if everything works
        Statement stat = conn.createStatement();
        System.out.println("RESET DATABASE - Dropping all database objects");
        stat.execute("DROP ALL OBJECTS");
        conn.close();

    }
}
