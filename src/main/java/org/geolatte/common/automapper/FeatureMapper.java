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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FeatureMapper {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeatureMapper.class);

    private static enum SCol {ID, GEOM};  //"special" columns

    private final NamingStrategy naming;
    private final TypeMapper typeMapper;

    public FeatureMapper(NamingStrategy naming, TypeMapper typeMapper) {
        this.naming = naming;
        this.typeMapper = typeMapper;
    }

    public ClassInfo createClassInfo(TableConfig cfg, DatabaseMetaData dmd) throws TableNotFoundException {
        String className = naming.createClassName(cfg.getTableName());
        ClassInfo cInfo = new ClassInfo(cfg.getTableName(), className);
        readColums(cfg, dmd, cInfo);
        determine(SCol.ID, cfg, dmd, cInfo);
        determine(SCol.GEOM, cfg, dmd, cInfo);
        return cInfo;
    }

    private void determine(SCol fld, TableConfig cfg, DatabaseMetaData dmd, ClassInfo cInfo) {
        String configuredColumn = getColumn(fld, cfg);
        if (configuredColumn != null) {
            setAs(fld, cInfo, cfg.getIdColumn());
            return;
        }
        String pkn = determine(fld, cfg.getTableRef(), cInfo, dmd);
        if (pkn != null) {
            setAs(fld, cInfo, pkn);
            return;
        }
        LOGGER.warn("Failed to determine an identifier for " + cfg.getTableRef());
    }

    private String getColumn(SCol fld, TableConfig cfg) {
        if (fld == SCol.GEOM) {
            return cfg.getGeomColumn();
        }
        if (fld == SCol.ID) {
            return cfg.getIdColumn();
        }
        return null;
    }

    private String determine(SCol fld, TableRef tableRef, ClassInfo cInfo, DatabaseMetaData dmd) {
        return fld == SCol.ID ?
                determinePrimaryKey(tableRef, dmd) :
                determineGeometry(tableRef, cInfo);
    }

    private String determineGeometry(TableRef tableRef, ClassInfo cInfo) {
        for (AttributeInfo ai : cInfo.getAttributes()) {
            if (ai.getHibernateType().equalsIgnoreCase(GeometryUserType.class.getCanonicalName())) {
                return ai.getColumnName();
            }
        }
        return null;
    }

    private String determinePrimaryKey(TableRef tableRef, DatabaseMetaData dmd) {
        String pkn = null;
        ResultSet rs = null;
        try {
            rs = dmd.getPrimaryKeys(tableRef.getCatalog(), tableRef.getSchema(), tableRef.getTableName());
            if (!rs.next()) return null;
            pkn = rs.getString("COLUMN_NAME");
            //check whether the primary key is non-composite
            if (rs.next()) return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                //do nothing
            }
        }
        return pkn;
    }

    private void readColums(TableConfig cfg, DatabaseMetaData dmd, ClassInfo cInfo) throws TableNotFoundException {
        ResultSet rs = null;
        boolean empty = true;
        try {
            rs = dmd.getColumns(cfg.getCatalog(), cfg.getSchema(), cfg.getTableName(), null);
            while (rs.next()) {
                empty = false;
                String colName = rs.getString("COLUMN_NAME");
                String dbType = rs.getString("TYPE_NAME");
                int javaType = rs.getInt("DATA_TYPE");
                addAttribute(cInfo, colName, dbType, javaType);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                // do nothing
            }
        }
        if (empty) {
            throw new TableNotFoundException(cfg.getTableRef());
        }
    }

    private boolean setAs(SCol sCol, ClassInfo cInfo, String column) {
        for (AttributeInfo ai : cInfo.getAttributes()) {
            if (ai.getColumnName().equals(column)) {
                if (sCol == SCol.ID) {
                    ai.setIdentifier(true);
                }
                if (sCol == SCol.GEOM) {
                    ai.setGeometry(true);
                }
                return true;
            }
        }
        LOGGER.warn("Attempted to set columns " + column + " as identifier, but no corresponding field in class found.");
        return false;
    }

    private void addAttribute(ClassInfo cInfo, String colName, String dbType, int javaType) {
        String hibernateType = null;
        try {
            hibernateType = typeMapper.getHibernateType(dbType, javaType);
            AttributeInfo ai = new AttributeInfo();
            ai.setColumnName(colName);
            ai.setFieldName(naming.createPropertyName(colName));
            ai.setHibernateType(hibernateType);
            ai.setCtClass(typeMapper.getCtClass(dbType, javaType));
            cInfo.addAttribute(ai);
        } catch (TypeNotFoundException e) {
            LOGGER.warn("No property generated for attribute " + colName + ": " + e.getMessage());
        }
    }

}
