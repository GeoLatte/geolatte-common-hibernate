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

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.hibernatespatial.GeometryUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>TypeMapper</code> maps a pair consisting of java.sql.Type, and a
 * database type name to a CtClass (a representation of a java type used by the
 * javassist class building tools) and to a Hibernate type (used when creating a
 * mapping file).
 *
 * @author Karel Maesen, Geovise BVBA (http://www.geovise.com/)
 */
public class TypeMapper {

    protected final static Logger logger = LoggerFactory.getLogger(TypeMapper.class);

    private final static String GEOMETRY_USER_TYPE = GeometryUserType.class
            .getCanonicalName();

    private List<TMEntry> entries = new ArrayList<TMEntry>();

    private String dbGeomType = "";

    private CtClass ctGeom;

    public TypeMapper(String dbGeomType) {

        // first set the type to use for the geometry
        this.dbGeomType = dbGeomType;

        ClassPool pool = ClassPool.getDefault();
        // ensure that we can load the JTS classes.
        pool.insertClassPath(new ClassClassPath(this.getClass()));

        try {
            ctGeom = pool.get("com.vividsolutions.jts.geom.Geometry");
            entries.add(new TMEntry(Types.BIGINT, "long", pool.get("java.lang.Long")));
            entries.add(new TMEntry(Types.BINARY, "binary", pool.get("byte[]")));
            entries.add(new TMEntry(Types.BIT, "boolean", pool.get("java.lang.Boolean")));
            entries.add(new TMEntry(Types.BLOB, "blob", pool.get("byte[]")));
            entries.add(new TMEntry(Types.BOOLEAN, "boolean", pool.get("java.lang.Boolean")));
            entries.add(new TMEntry(Types.CHAR, "character", pool.get("java.lang.Character")));
            entries.add(new TMEntry(Types.CLOB, "text", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.DATE, "date", pool.get("java.util.Date")));
            entries.add(new TMEntry(Types.DECIMAL, "big_decimal", pool.get("java.math.BigDecimal")));
            entries.add(new TMEntry(Types.DOUBLE, "double", pool.get("java.lang.Double")));
            entries.add(new TMEntry(Types.FLOAT, "float", pool.get("java.lang.Float")));
            entries.add(new TMEntry(Types.INTEGER, "integer", pool.get("java.lang.Integer")));
            entries.add(new TMEntry(Types.LONGNVARCHAR, "text", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.LONGVARBINARY, "blob", pool.get("byte[]")));
            entries.add(new TMEntry(Types.LONGVARCHAR, "text", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.NCHAR, "string", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.NCLOB, "text", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.NUMERIC, "big_decimal", pool.get("java.math.BigDecimal")));
            entries.add(new TMEntry(Types.NVARCHAR, "string", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.REAL, "double", pool.get("java.lang.Double")));
            entries.add(new TMEntry(Types.SMALLINT, "short", pool.get("java.lang.Short")));
            entries.add(new TMEntry(Types.TIMESTAMP, "timestamp", pool.get("java.util.Date")));
            entries.add(new TMEntry(Types.TIME, "time", pool.get("java.util.Date")));
            entries.add(new TMEntry(Types.TINYINT, "byte", pool.get("java.lang.Byte")));
            entries.add(new TMEntry(Types.VARCHAR, "binary", pool.get("java.lang.String")));
            entries.add(new TMEntry(Types.VARCHAR, "string", pool.get("byte[]")));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public CtClass getCtClass(String dbType, int sqlType) throws TypeNotFoundException {
        if (dbType.equalsIgnoreCase(this.dbGeomType)) {
            return this.ctGeom;
        }
        for (TMEntry entry : entries) {
            if (entry.javaType == sqlType) {
                return entry.ctClass;
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

    public int[] getMappedSQLTypes() {
        int l = this.entries.size();
        int[] types = new int[l];
        for (int i = 0; i < this.entries.size(); i++) {
            types[i] = this.entries.get(i).javaType;
        }
        return types;
    }

    public void addTypeMapping(int sqlType, String hibernateType,
                               CtClass ctClass) {
        this.entries.add(new TMEntry(sqlType, hibernateType, ctClass));
    }

    public void removeTypeMapping(int sqlType) {
        TMEntry tm = null;
        for (TMEntry t : this.entries) {
            if (t.javaType == sqlType) {
                tm = t;
                break;
            }
        }
        if (tm != null) {
            this.entries.remove(tm);
        }
    }

    private static class TMEntry {
        protected int javaType = 0;

        protected String hibernateTypeName = "";

        protected CtClass ctClass;

        protected TMEntry(int jt, String ht, CtClass jc) {
            this.javaType = jt;
            this.hibernateTypeName = ht;
            this.ctClass = jc;
        }
    }

}
