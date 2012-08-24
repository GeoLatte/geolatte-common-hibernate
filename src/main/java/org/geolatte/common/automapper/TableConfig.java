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
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class TableConfig {

    final private TableRef tableRef;
    private String idColumn;
    private String geomColumn;
    private List<String> excludeCols = new ArrayList<String>();
    private List<String> includeCols = new ArrayList<String>();

    public static class Builder{

        public static TableConfig emptyConfig(TableRef tableRef) {
            return new TableConfig(tableRef);
        }

        final private TableConfig underConstruction;
        Builder(TableRef tableRef) {
            underConstruction = new TableConfig(tableRef);
        }

        public Builder withGeometry(String geomColumn){
            underConstruction.geomColumn = geomColumn;
            return this;
        }

        public Builder withId(String idColumn){
            underConstruction.idColumn = idColumn;
            return this;
        }

        public Builder exclude(String excluded) {
                underConstruction.excludeCols.add(excluded);
                return this;
        }

        public Builder include(String included) {
            underConstruction.includeCols.add(included);
            return this;
        }

        public TableConfig result() {
            return underConstruction;
        }
    }

    private TableConfig(TableRef tableRef) {
        if (tableRef == null ) throw new IllegalArgumentException("TableRef cannot be null.)");
        this.tableRef = tableRef;
    }

    public String getTableName() {
        return tableRef.getTableName();
    }

    public String getCatalog(){
        return tableRef.getCatalog();
    }

    public String getSchema() {
        return tableRef.getSchema();
    }

    TableRef getTableRef() {
        return tableRef;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public String getGeomColumn() {
        return geomColumn;
    }

    public List<String> getIncludedColumns(){
        return includeCols;
    }

    public List<String> getExcludedColumns(){
        return excludeCols;
    }


}
