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
public class TableConfig {

    final private TableRef tableRef;
    private String idColumn;
    private String geomColumn;
    final private List<String> excludeCols = new ArrayList<String>();
    final private List<String> includeCols = new ArrayList<String>();

    /**
     * A builder for a <code>TableConfig</code>
     */
    public static class Builder {

        /**
         * Creates an empty <code>TableConfig</code>, i.e. one with no information save to the <code>TableRef</code>.
         * @param tableRef the <code>TableRef</code> for the created <code>TableConfig</code>.
         * @return an empty <code>TableConfig</code> for the table specified by the <code>tableRef</code> parameter.
         */
        public static TableConfig emptyConfig(TableRef tableRef) {
            return new TableConfig(tableRef);
        }

        final private TableConfig underConstruction;

        /**
         * Constructs an instance of the specified table.
         * @param tableRef the <code>TableRef</code> for the table for which a <code>TableConfig</code> is to be built.
         */
        public Builder(TableRef tableRef) {
            underConstruction = new TableConfig(tableRef);
        }

        /**
         * Sets the primary geometry column of the table.
         *
         * <p>For a definition of primary, see {@link ColumnMetaData#isGeometry()}.</p>
         *
         * <p>If this is not configured, the <code>AutoMapper</code> will select a random column of type <code>Geometry</code>.
         *
         * @param geomColumn the name of the geometry column
         * @return this instance
         */
        public Builder withGeometry(String geomColumn) {
            underConstruction.geomColumn = geomColumn;
            return this;
        }

        /**
         * Sets the identifier column of the table.
         *
         * <p>If this is not configured, the <code>AutoMapper</code> will select the primary key of the table</code>
         *
         * @param idColumn the name of the identifier column
         * @return this instance
         */
        public Builder withId(String idColumn) {
            underConstruction.idColumn = idColumn;
            return this;
        }

        /**
         * Excludes a column from the mapping process, i.e. no Class member will be generated for the specified column.
         *
         * <p>If a column is both included and excluded, the inclusion has priority.</p>
         *
         * @param excluded the name of the column in the table to exclude
         * @return this instance.
         */
        public Builder exclude(String excluded) {
            underConstruction.excludeCols.add(excluded);
            return this;
        }

        /**
         * Includes a column from the mapping process, i.e. a Class member will be generated for the specified column.
         *
         * <p>If a column is both included and excluded, the inclusion has priority.</p>
         *
         * @param included the name of the column in the table to include
         * @return this instance.
         */
        public Builder include(String included) {
            underConstruction.includeCols.add(included);
            return this;
        }

        /**
         * Returns the <code>TableConfig</code> that is built by this instance.
         * @return the constructed <code>TableConfig</code>
         */
        public TableConfig result() {
            return underConstruction;
        }

    }

    private TableConfig(TableRef tableRef) {
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
     * Returns the name of column that can be used as identifier.
     *
     * @return the name of column that can be used as identifier, or null if none is configured.
     */
    public String getIdColumn() {
        return idColumn;
    }

    /**
     * Returns the name of the column that provides the primary geometry.
     *
     * @return the name of the column that provides the primary geometry, or null if none is configured.
     */
    public String getGeomColumn() {
        return geomColumn;
    }

    /**
     * Returns the list of columns that need to be included by the <code>AutoMapper</code>
     *
     * @return the (possibly empty) list of columns that need to be included by the <code>AutoMapper</code>
     */
    public List<String> getIncludedColumns() {
        return includeCols;
    }

    /**
     * Returns the list of columns that need to be excluded by the <code>AutoMapper</code>
     *
     * @return the (possibly empty) list of columns that need to be excluded by the <code>AutoMapper</code>
     */
    public List<String> getExcludedColumns() {
        return excludeCols;
    }


}
