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

package org.geolatte.common.testDb;

import geodb.GeoDB;
import org.h2.tools.Console;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */

public class GeoDBWrapper {

    private static final String tcpPort = "4321";
    private static final String webPort = "8123";
//    private static final String dbLocation = System.getProperty("java.io.tmpdir") + "/geolattetest";
    private static Server server;
    private static Server webServer;

    public GeoDBWrapper() {
        try {
            server = Server.createTcpServer("-tcpPort", tcpPort);
            webServer = Server.createWebServer("-webPort", webPort);
            server.start();
            webServer.start();
            System.out.println("H2 Database started on port " + tcpPort );
            // now use the database in your application in embedded mode
            Class.forName("org.h2.Driver");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "sa");
    }

    public static void initGeoDB() throws SQLException {
        doWithinConnection(
                new DbOp(){
                    @Override
                    public Object execute(Connection conn) throws SQLException {
                        GeoDB.InitGeoDB(conn);
                        return true;
                    }
                }
        );
    }

    public void stop() {
        webServer.stop();
        server.stop();
    }

     /**
     * Helper to remove all objects from the database.
     * @throws java.sql.SQLException When clean database failed due to an database error.
     */
    public static void cleanDatabase() throws SQLException {
        doWithinConnection("DROP ALL OBJECTS");
    }


    public static Object doWithinConnection(final String sql) throws SQLException {
         DbOp op = new DbOp(){
             @Override
             public Object execute(Connection conn) throws SQLException {
                 Statement stmt = null;
                 try {
                     stmt = conn.createStatement();
                     System.out.println("Executing: " + sql);
                     return stmt.execute(sql);
                 } finally {
                     if (stmt != null) stmt.close();
                 }
             }
         };
         return doWithinConnection(op);
    }

    public static Object doWithinConnection(DbOp op) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        try {
            conn = getConnection();
            return op.execute(conn);
        } finally{
            if (conn != null) conn.close();
        }
    }

    public interface DbOp {
        Object execute(Connection conn) throws SQLException;
    }

    public static void main(String[] args) throws SQLException {
        new Console().run(new String[]{"-tcpPort", tcpPort, "-webPort", webPort});
        initGeoDB();
    }
}
