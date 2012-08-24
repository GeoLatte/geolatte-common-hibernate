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

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class TableRef {
    final private String tableName;
    final private String schema;
    final private String catalog;

    public static TableRef valueOf(String catalog, String schema, String tableName) {
        return new TableRef(catalog, schema, tableName);
    }

    public static TableRef valueOf(String schema, String tableName) {
        return new TableRef(null, schema, tableName);
    }

    public static TableRef valueOf(String tableName) {
        return new TableRef(null, null, tableName);
    }

    public TableRef(String catalog, String schema, String tableName) {
        if (tableName == null) throw new IllegalArgumentException("TableName cannot be null.");
        this.tableName = tableName;
        this.schema = schema;
        this.catalog = catalog;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchema() {
        return schema;
    }

    public String getCatalog() {
        return catalog;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (catalog != null) {
            builder.append(catalog).append('.');
        }
        if (schema != null) {
            if (catalog != null) {
                builder.append('*').append('.');
            } else {
                builder.append(schema).append('.');
            }
        }
        builder.append(tableName);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableRef tableRef = (TableRef) o;

        if (catalog != null ? !catalog.equals(tableRef.catalog) : tableRef.catalog != null) return false;
        if (schema != null ? !schema.equals(tableRef.schema) : tableRef.schema != null) return false;
        return tableName.equals(tableRef.tableName);

    }

    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (catalog != null ? catalog.hashCode() : 0);
        return result;
    }
}
