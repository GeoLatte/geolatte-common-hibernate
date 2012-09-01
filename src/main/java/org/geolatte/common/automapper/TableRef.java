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
 * Specifies a table in a database
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class TableRef {
    final private String tableName;
    final private String schema;
    final private String catalog;

    /**
     * Creates an instance from catalog, schema and table names
     *
     * <p>if the catalog or schema parameter is an empty String, the catalog or schema will be set to
     * <code>null</code>.</p>
     *
     * @param catalog the table catalog
     * @param schema the table schema
     * @param tableName the table name
     * @return a <code>TableRef</code> for the table identified by the specified catalog, schema and table name.
     * @throws IllegalArgumentException if the tableName parameter is null
     */
    public static TableRef valueOf(String catalog, String schema, String tableName) {
        return new TableRef(catalog, schema, tableName);
    }

    /**
     * Creates an instance from catalog and table names
     *
     * <p>if the schema parameter is an empty String, the catalog or schema will be set to
     * <code>null</code>.</p>
     *
     * @param schema the table schema
     * @param tableName the table name
     * @return a <code>TableRef</code> for the table identified by the specified schema and table name.
     * @throws IllegalArgumentException if the tableName parameter is null
     */
    public static TableRef valueOf(String schema, String tableName) {
        return new TableRef(null, schema, tableName);
    }

    /**
     * Creates an instance from a table name
     *
     *<p>The  table name must have the format [&LT;catalog&GT;.][&LT;schema&GT;.]&LT;tablename&GT;.
     * if the catalog or schema component is empty, the catalog or schema will be set to
     * <code>null</code>.</p>
     *
     * @param tableName the table name
     * @return a <code>TableRef</code> for the table identified by the specified table name.
     * @throws IllegalArgumentException if the tableName parameter is null
     */
    public static TableRef valueOf(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("TableName cannot be null.");
        }
        String[] components = tableName.split("\\.");
        return TableRef.valueOf(components);
    }

    /**
     * Creates an instance from an array of table ref components,
     *
     * <p>The components should be (in order): catalog (optional), schema (optional), table name. if the catalog or schema
     * component is an empty String, the catalog or schema will be set to <code>null</code>.</p>
     *
     * @param components the components of the reference.
     * @return a new instance corresponding to the specified components.
     * @throws IllegalArgumentException if the parameter is null, or has more than 3 elements.
     */
    public static TableRef valueOf(String[] components){
        if (components == null) {
            throw new IllegalArgumentException("Null argument not allowed.");
        }
        switch (components.length) {
            case 1:
                return new TableRef(null, null, components[0]);
            case 2:
                return new TableRef(null, components[0], components[1]);
            case 3:
                return new TableRef(components[0], components[1], components[2]);
            default:
                throw new IllegalArgumentException("String array has more than 3 elements.");
        }
    }

    /**
     * Creates an instance from catalog, schema and table names
     * @param catalog the table catalog
     * @param schema the table schema
     * @param tableName the table name
     * @throws IllegalArgumentException if the tableName parameter is null
     */
    public TableRef(String catalog, String schema, String tableName) {
        if (tableName == null || tableName.isEmpty()){
            throw new IllegalArgumentException("TableName cannot be null.");
        }
        this.tableName = tableName;
        this.schema = toNullIfEmpty(schema);
        this.catalog = toNullIfEmpty(catalog);
    }

    /**
     * Returns the table name component of this instance.
     * @return the table name component of this instance.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns the schema name component of this instance.
     * @return the schema name component of this instance.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Returns the catalog name component of this instance.
     * @return the catalog name component of this instance.
     */
    public String getCatalog() {
        return catalog;
    }

    private String toNullIfEmpty(String str) {
        if (str == null) {
            return null;
        }
        return str.isEmpty() ? null : str;
    }

    /**
     * Generates a representation of this instance.
     *
     * <p>It creates a string with structure: [&LT;catalog&GT;.][&LT;schema&GT;.]&LT;table name&GT;</p>
     *
     * @return a string representation of this instance.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (catalog != null) {
            builder.append(catalog).append('.');
            if( schema == null) {
                builder.append('*').append('.');
            }
        }
        if (schema != null) {
            builder.append(schema).append('.');
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
