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

import java.util.ArrayList;
import java.util.List;

/**
 * Configures how a database table is to be mapped to a POJO class.
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class TableConfiguration {

    final private TableRef tableRef;
    private String idColumn;
    private String geomColumn;
    final private List<String> excludeCols = new ArrayList<String>();

    /**
     * A builder for a <code>TableConfiguration</code>
     */
    public static class Builder {

        /**
         * Creates an empty <code>TableConfiguration</code> (with no information save to the <code>TableRef</code>).
         * @param tableRef the <code>TableRef</code> for the created <code>TableConfiguration</code>.
         * @return an empty <code>TableConfiguration</code> for the table specified by the <code>tableRef</code> parameter.
         */
        public static TableConfiguration emptyConfig(TableRef tableRef) {
            return new TableConfiguration(tableRef);
        }

        final private TableConfiguration underConstruction;

        /**
         * Constructs an instance for the specified table.
         * @param tableRef the <code>TableRef</code> for the table for which a <code>TableConfiguration</code> is to be built.
         */
        public Builder(TableRef tableRef) {
            underConstruction = new TableConfiguration(tableRef);
        }

        /**
         * Sets the primary geometry column of the table.
         *
         * <p>For a definition of primary, see {@link ColumnMetaData#isGeometry()}.</p>
         *
         * <p>If this is not configured, the <code>AutoMapper</code> will select a random column of type <code>Geometry</code>.
         *
         * @param geometryColumn the name of the geometry column
         * @return this instance
         */
        public Builder geometry(String geometryColumn) {
            underConstruction.geomColumn = geometryColumn;
            return this;
        }

        /**
         * Sets the identifier column of the table.
         *
         * <p>If this is not configured, the <code>AutoMapper</code> will select the primary key of the table</code>
         *
         * @param identifierColumn the name of the identifier column
         * @return this instance
         */
        public Builder identifier(String identifierColumn) {
            underConstruction.idColumn = identifierColumn;
            return this;
        }

        /**
         * Excludes a column from the mapping process
         *
         * <p>No class member will be generated for the specified column.</p>
         *
         * @param excluded the name of the column in the table to exclude
         * @return this instance.
         */
        public Builder exclude(String excluded) {
            underConstruction.excludeCols.add(excluded);
            return this;
        }

        /**
         * Returns the <code>TableConfiguration</code> that is built by this instance.
         * @return the constructed <code>TableConfiguration</code>
         */
        public TableConfiguration result() {
            return underConstruction;
        }

    }

    private TableConfiguration(TableRef tableRef) {
        if (tableRef == null) throw new IllegalArgumentException("TableRef cannot be null.)");
        this.tableRef = tableRef;
    }

    /**
     * Returns the name of the configured database table
     *
     * @return the name of the configured database table
     */
    public String getTableName() {
        return tableRef.getTableName();
    }

    /**
     * Returns the catalog of the configured database table
     * @return the catalog of the configured database table
     */
    public String getCatalog() {
        return tableRef.getCatalog();
    }

    /**
     * Returns the schema of the configured database table
     * @return the schema of the configured database table
     */
    public String getSchema() {
        return tableRef.getSchema();
    }

    TableRef getTableRef() {
        return tableRef;
    }

    /**
     * Returns the name of the column that can be used as identifier.
     *
     * @return the name of the column that can be used as identifier, or null if none is configured.
     */
    public String getIdentifierColumn() {
        return idColumn;
    }

    /**
     * Returns the name of the column that provides the primary geometry.
     *
     * @return the name of the column that provides the primary geometry, or null if none is configured.
     */
    public String getGeometryColumn() {
        return geomColumn;
    }

    /**
     * Returns the list of columns that the <code>AutoMapper</code> should ignore.
     *
     * @return the (possibly empty) list of columns that the <code>AutoMapper</code> should ignore.
     */
    public List<String> getExcludedColumns() {
        return excludeCols;
    }


}
