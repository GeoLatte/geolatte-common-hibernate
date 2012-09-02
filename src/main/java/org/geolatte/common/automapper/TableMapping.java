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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper that associates a mapped class together with metadata about the source table, and information
 * on how the table columns have been mapped to properties.
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/26/12
 */
class TableMapping {

    final private TableMetaData tableMetaData;
    final private Map<ColumnMetaData, ColumnMapping> mappedColumns = new HashMap<ColumnMetaData, ColumnMapping>();

    private Class<?> generatedClass;

    TableMapping(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    Class<?> getGeneratedClass() {
        return generatedClass;
    }

    TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    public ColumnMetaData getIdentifierColumn() {
        for (ColumnMetaData cmd : mappedColumns.keySet()) {
            if (cmd.isIdentifier()) {
                return cmd;
            }
        }
        //If this is thrown, it is because of a programming error. Because Hibernate requires an Id-property,
        // one must be mapped.
        throw new IllegalStateException("No mapped identifier property");
    }

    Collection<ColumnMetaData> getMappedColumns() {
        return mappedColumns.keySet();
    }

    ColumnMapping getColumnMapping(ColumnMetaData col) {
        return mappedColumns.get(col);
    }

    String getSimpleName() {
        return getGeneratedClass().getSimpleName();
    }

    void addColumnMapping(ColumnMetaData ai, String propertyName, String hibernateType, Class<?> javaType) {
        if (ai == null) {
            throw new IllegalArgumentException("Illegal Null argument during column mapping registration.");
        }
        mappedColumns.put(ai, new ColumnMapping(propertyName, hibernateType, javaType));
    }

    void setGeneratedClass(Class<?> generatedClass) {
        this.generatedClass = generatedClass;
    }
}
