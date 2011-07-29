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

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.ByteArrayInputStream;

class HibernateUtil {

    private SessionFactory sessionFactory;
    private Configuration configuration;
    private String[] xmlMappings;

    public HibernateUtil(String[] xmlMappings) {

        this.xmlMappings = xmlMappings;
        sessionFactory = buildSessionFactory();
    }

    public HibernateUtil() {
        this(new String[]{});
    }

    private SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            configuration = new Configuration().configure();
            for (String mapping : xmlMappings)
                configuration.addInputStream(new ByteArrayInputStream(mapping.getBytes("UTF-8")));
            return configuration.buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {

        return sessionFactory;
    }

    public void dispose() {

        sessionFactory.close();
    }

    public void createDatabase() {

        SchemaExport e = new SchemaExport(configuration);
        e.drop(true, true);
        e.create(true, true);
    }

}
