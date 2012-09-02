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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds the information on how database tables are mapped to classes by an <code>AutoMapper</code>.
 *
 * @author Karel Maesen, Geovise BVBA (2012-09-02)
 *
 */
public class DatabaseMapping {

    final private String packageName;
    final private Map<TableRef, TableMapping> mappedClasses;

    DatabaseMapping(Map<TableRef, TableMapping> mappedClasses, String packageName) {
        this.mappedClasses = mappedClasses;
        this.packageName = packageName;

    }

    /**
     * Returns the name of the package that holds all generated classes.
     *
     * @return the name of the package that holds all generated classes.
     */
    public String getPackageName() {
        return packageName;
    }


    /**
     * Generates the mapping XML document for Hibernate ORM
     *
     * @return a DOM4j <code>Document</code> representing the Hibernate ORM mapping document.
     */
    public Document generateHibernateMappingDocument() {
        MappingsGenerator mappingGenerator = new MappingsGenerator(this);
        return mappingGenerator.getMappingsDocument();
    }

    /**
     * Returns the <code>TableMapping</code> for the specified table.
     *
     * @return the <code>TableMapping</code> for the table specified by the <code>tableRef</code> parameter.
     * @throws IllegalStateException if the map() method has not been invoked first.
     * @tableRef the <code>TableRef</code> that determines a table in the database
     */
    TableMapping getTableMapping(TableRef tableRef) {
        return mappedClasses.get(tableRef);

    }

    /**
     * Returns the <code>Class</code> object to which the specified table is mapped.
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return the <code>Class</code> object generated from the table specifed by the <code>tableRef</code> parameter,
     *         or null if no such class has been generated.
     * @throws IllegalStateException if the map() method has not been invoked first.
     */
    public Class<?> getGeneratedClass(TableRef tableRef) {
        TableMapping mc = mappedClasses.get(tableRef);
        return mc == null ? null : mc.getGeneratedClass();
    }

    /**
     * Returns the <code>TableRef</code>s to all tables mapped by this <code>AutoMapper</code>.
     *
     * @return the <code>TableRef</code>s to all the tables mapped by this <code>AutoMapper</code>.
     * @throws IllegalStateException if the map() method has not been invoked first.
     */
    public List<TableRef> getMappedTables() {
        List<TableRef> list = new ArrayList<TableRef>();
        for (TableRef tbn : mappedClasses.keySet()) {
            list.add(tbn);
        }
        return list;
    }

    /**
     * Returns the property names of the POJO Class to which the specified table is mapped
     * <p/>
     * <p>If the specified table is not mapped by this instance, it returns an empty <code>List</code>.</p>
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return list of properties of the class that corresponds with the table identified by the arguments
     * @throws IllegalStateException if the map() method has not been invoked first.
     */
    public List<String> getProperties(TableRef tableRef) {
        List<String> result = new ArrayList<String>();
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) return result;
        for (ColumnMetaData columnMetaData : tableMapping.getMappedColumns()) {
            ColumnMapping cm = tableMapping.getColumnMapping(columnMetaData);
            result.add(cm.getPropertyName());
        }
        return result;
    }

    /**
     * Returns the name of the Identifier property
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return the name of the identifier property, or null if the table specified by the <code>tableRef</code>
     *         parameter is not mapped
     * @throws IllegalStateException if the map() method has not been invoked first.
     */
    public String getIdProperty(TableRef tableRef) {
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) {
            return null;
        }
        ColumnMetaData identifierColumn = tableMapping.getIdentifierColumn();
        return tableMapping.getColumnMapping(identifierColumn).getPropertyName();
    }

    /**
     * Returns the name of the primary geometry property.
     *
     * <p>A geometry property is primary when it represents the location and shape of the object.</p>
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return the name of the primary geometry property, or null if the table specified by the <code>tableRef</code>
     *         parameter is not mapped or has no primary geometry.
     * @throws IllegalStateException if the map() method has not been invoked first.
     */
    public String getGeometryProperty(TableRef tableRef) {
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) {
            return null;
        }
        for (ColumnMetaData columnMetaData : tableMapping.getMappedColumns()) {
            if (columnMetaData.isGeometry()) {
                ColumnMapping cm = tableMapping.getColumnMapping(columnMetaData);
                return cm.getPropertyName();
            }
        }
        return null;
    }
}