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

import org.hibernatespatial.GeometryUserType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps a pair consisting of java.sql.Type, and a
 * database type name to a Java Class and to a Hibernate type name.
 *
 * @author Karel Maesen, Geovise BVBA (http://www.geovise.com/)
 */
public class TypeMapper {

    final private static String GEOMETRY_USER_TYPE = GeometryUserType.class
            .getCanonicalName();

    final private List<TMEntry> entries = new ArrayList<TMEntry>();

    final private String dbGeomType;

    public TypeMapper(String dbGeomType) {
        if (dbGeomType == null) {
            throw new IllegalArgumentException("TypeMapper received null argument.");
        }
        this.dbGeomType = dbGeomType;
        try {
            entries.add(new TMEntry(Types.BIGINT, "long", java.lang.Long.class));
            entries.add(new TMEntry(Types.BINARY, "binary", byte[].class));
            entries.add(new TMEntry(Types.BIT, "boolean", java.lang.Boolean.class));
            entries.add(new TMEntry(Types.BLOB, "blob", byte[].class));
            entries.add(new TMEntry(Types.BOOLEAN, "boolean", java.lang.Boolean.class));
            entries.add(new TMEntry(Types.CHAR, "character", java.lang.Character.class));
            entries.add(new TMEntry(Types.CLOB, "text", java.lang.String.class));
            entries.add(new TMEntry(Types.DATE, "date", java.util.Date.class));
            entries.add(new TMEntry(Types.DECIMAL, "big_decimal", java.math.BigDecimal.class));
            entries.add(new TMEntry(Types.DOUBLE, "double", java.lang.Double.class));
            entries.add(new TMEntry(Types.FLOAT, "float", java.lang.Float.class));
            entries.add(new TMEntry(Types.INTEGER, "integer", java.lang.Integer.class));
            entries.add(new TMEntry(Types.LONGNVARCHAR, "text", java.lang.String.class));
            entries.add(new TMEntry(Types.LONGVARBINARY, "blob", byte[].class));
            entries.add(new TMEntry(Types.LONGVARCHAR, "text", java.lang.String.class));
            entries.add(new TMEntry(Types.NCHAR, "string", java.lang.String.class));
            entries.add(new TMEntry(Types.NCLOB, "text", java.lang.String.class));
            entries.add(new TMEntry(Types.NUMERIC, "big_decimal", java.math.BigDecimal.class));
            entries.add(new TMEntry(Types.NVARCHAR, "string", java.lang.String.class));
            entries.add(new TMEntry(Types.REAL, "double", java.lang.Double.class));
            entries.add(new TMEntry(Types.SMALLINT, "short", java.lang.Short.class));
            entries.add(new TMEntry(Types.TIMESTAMP, "timestamp", java.util.Date.class));
            entries.add(new TMEntry(Types.TIME, "time", java.util.Date.class));
            entries.add(new TMEntry(Types.TINYINT, "byte", java.lang.Byte.class));
            entries.add(new TMEntry(Types.VARCHAR, "string", java.lang.String.class));
            entries.add(new TMEntry(Types.VARBINARY, "binary", byte[].class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getClass(String dbType, int sqlType) throws TypeNotFoundException {
        if (dbType.equalsIgnoreCase(this.dbGeomType)) {
            return com.vividsolutions.jts.geom.Geometry.class;
        }
        for (TMEntry entry : entries) {
            if (entry.javaType == sqlType) {
                return entry.javaClass;
            }
        }
        throw new TypeNotFoundException(String.format("Can't map %s (sql type %d).", dbType, sqlType));
    }

    public String getHibernateType(String dbType, int sqlType) throws TypeNotFoundException {
        if (dbType.equalsIgnoreCase(this.dbGeomType)) {
            return GEOMETRY_USER_TYPE;
        }
        for (TMEntry entry : entries) {
            if (entry.javaType == sqlType) {
                return entry.hibernateTypeName;
            }
        }
        throw new TypeNotFoundException(dbType);

    }

    private static class TMEntry {
        int javaType = 0;

        String hibernateTypeName = "";

        Class javaClass;

        TMEntry(int javaSqlType, String hibernateType, Class javaClass) {
            this.javaType = javaSqlType;
            this.hibernateTypeName = hibernateType;
            this.javaClass = javaClass;
        }
    }

}
