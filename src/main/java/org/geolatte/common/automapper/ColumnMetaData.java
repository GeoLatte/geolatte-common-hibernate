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
 * Represents table column metadata.
 *
 * @author Karel Maesen
 */
class ColumnMetaData {

    final private String columnName;
    final private int sqlType;
    final private String dbType;
    private boolean isIdentifier;
    private boolean isGeometry;

    /**
     * Constructs an instance.
     *
     * @param columnName the  name of the column
     * @param sqlType    the <code>java.sql.Type</code> code
     * @param dbType     the name of the database type.
     * @throws IllegalArgumentException if any of the parameters is null
     */
    ColumnMetaData(String columnName, int sqlType, String dbType) {
        if (columnName == null || dbType == null) {
            throw new IllegalArgumentException("Null values not allowed in this constructor");
        }
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.dbType = dbType;
    }

    /**
     * Returns the column name of this <code>ColumnMetaData</code>.
     *
     * @return the column name of this <code>ColumnMetaData</code>.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns the {@link java.sql.Types} code for the type of this <code>ColumnMetaData</code>
     *
     * @return the column name of this <code>ColumnMetaData</code>.
     */
    public int getSqlType() {
        return sqlType;
    }

    /**
     * Returns the database name of the type of this <code>ColumnMetaData</code>
     *
     * @return the database name of the type of this <code>ColumnMetaData</code>
     */
    public String getDbTypeName() {
        return dbType;
    }

    void setAsIdentifier(boolean isIdentifier) {
        this.isIdentifier = isIdentifier;
    }

    /**
     * Returns true if this <code>ColumnMetaData</code> is an identifier.
     *
     * @return true if this <code>ColumnMetaData</code> is an identifier.
     */
    public boolean isIdentifier() {
        return isIdentifier;
    }

    void setAsGeometry(boolean isGeometry) {
        this.isGeometry = isGeometry;
    }

    /**
     * Returns true if this <code>ColumnMetaData</code> corresponds to the primary geometry of the table.
     * <p/>
     * <p>The primary geometry is the geometry which determines the location and shape of the object represented
     * by the table row.</p>
     *
     * @return true if this <code>ColumnMetaData</code> is the primary geometry of the table.
     */
    public boolean isGeometry() {
        return this.isGeometry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnMetaData that = (ColumnMetaData) o;

        if (sqlType != that.sqlType) return false;
        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
        if (dbType != null ? !dbType.equals(that.dbType) : that.dbType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + sqlType;
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        return result;
    }
}
