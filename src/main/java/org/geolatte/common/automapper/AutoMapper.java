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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Karel Maesen, Geovise BVBA
 */
public class AutoMapper {

    protected final static Logger LOGGER = LoggerFactory.getLogger(AutoMapper.class);

    final private AutoMapConfig config;

    final private Map<TableRef, Class<?>> tableClassMap = new HashMap<TableRef, Class<?>>();

    final private Map<TableRef, ClassInfo> tableClassInfoMap = new HashMap<TableRef, ClassInfo>();



    public AutoMapper (AutoMapConfig config){
        this.config = config;
    }

    /**
     * Returns the Hibernate mapping document for the specified tables
     * <p/>
     * <p>To create the mapping, a <code>Connection</code> object must be
     * provided to provide access to the specified tables.
     * This connection will not be closed on return.</p>
     *
     * @param conn       JDBC <code>Connection</code> used during mapping
     * @return the XML mapping document that maps the tables specified by the catalog, schema and tablenames arguments.
     * @throws SQLException
     */
    public Document map(Connection conn) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();
        FeatureMapper fMapper = new FeatureMapper(naming(), typeMapper());
        FeatureClassGenerator fGenerator = new FeatureClassGenerator(packageName(), naming());

        for (TableRef tableRef : tableRefs()) {
            if (isAlreadyMapped(tableRef)) continue;
            LOGGER.info("Generating class info for table " + tableRef);
            ClassInfo cInfo;
            try {
                cInfo = fMapper.createClassInfo(tableConfig(tableRef), dmd);
                LOGGER.info("Generating class " + cInfo.getClassName() + " for table " + tableRef);
                Class<?> clazz = fGenerator.generate(cInfo);
                tableClassMap.put(tableRef, clazz);
                tableClassInfoMap.put(tableRef, cInfo);

            } catch (TableNotFoundException e) {
                LOGGER.warn(e.getMessage());
            }
        }
        LOGGER.info("Generating Hibernate Mapping file");
        MappingsGenerator mappingGenerator = new MappingsGenerator(packageName());

        mappingGenerator.load(tableClassInfoMap);
        return mappingGenerator.getMappingsDoc();
    }

    private boolean isAlreadyMapped(TableRef tableRef) {
        if (tableClassInfoMap.get(tableRef) != null) {
            LOGGER.info("Class info for table " + tableRef + " has already been mapped.");
            return true;
        }
        return false;
    }

    /**
     * Returns the <code>Class</code> object to which the specified table is mapped
     *
     * @tableRef the <code>TableRef</code>
     * @return class to which the table specified by the argument is mapped
     */
    public Class<?> getClass(TableRef tableRef) {
        return tableClassMap.get(tableRef);
    }

    /**
     * Returns the tables mapped by this automapper.
     *
     * @return a List of mapped tables. Each table is represented by a String array with the first
     *         component the catalog, the second the schema, and the third the table name.
     */
    public List<TableRef> getMappedTables() {
        List<TableRef> list = new ArrayList<TableRef>();
        for (TableRef tbn : tableClassMap.keySet()) {
            list.add(tbn);
        }
        return list;
    }

    /**
     * Returns the attribute names of the class to with the specified table is mapped
     *
     * @param tableRef
     * @return list of attribute (field) names of the class that corresponds with the table identified by the arguments
     */
    public List<String> getAttributes(TableRef tableRef) {
        List<AttributeInfo> attributes = getAttributeInfos(tableRef);
        List<String> result = new ArrayList<String>();
        for (AttributeInfo attributeInfo : attributes) {
            result.add(attributeInfo.getFieldName());
        }
        return result;
    }

    private List<AttributeInfo> getAttributeInfos(TableRef tableRef) {
        ClassInfo cInfo = tableClassInfoMap.get(tableRef);
        if (cInfo == null) return new ArrayList<AttributeInfo>();
        return cInfo.getAttributes();
    }

    /**
     * Returns the Identifier attribute
     *
     * @param tableRef
     * @return the attribute name which functions as a unique identifier for the objects corresponding
     * to rows in the specified table
     */
    public String getIdAttribute(TableRef tableRef) {
        ClassInfo cInfo = tableClassInfoMap.get(tableRef);
        return cInfo.getIdAttribute().getFieldName();
    }

    /**
     * Returns the (default) <code>Geometry</code>-valued attribute
     *
     * @return the name of the <code>Geometry</code>-valued attribute
     */
    public String getGeometryAttribute(TableRef tableRef) {
        ClassInfo cInfo = tableClassInfoMap.get(tableRef);
        AttributeInfo geomAttribute = cInfo.getGeomAttribute();
        return geomAttribute != null ? geomAttribute.getFieldName() : null;
    }

    /**
     * Returns the name of the setter-method for the attribute
     *
     * @param tableRef
     * @param attribute name of the attribute of the class to which this class is mapped
     * @return the name of the setter-method of the attribute specified by the arguments
     */
    public String getAttributeSetterName(TableRef tableRef, String attribute) {
        getAttributeInfo(tableRef, attribute);
        return naming().createSetterName(attribute);
    }

    /**
     * Returns the name of the getter-method for the attribute
     *
     * @param tableRef
     * @param attribute name of the attribute of the class to which this class is mapped
     * @return the name of the getter-method of the attribute specified by the arguments
     *  */
    public String getAttributeGetterName(TableRef tableRef, String attribute) {
        getAttributeInfo(tableRef, attribute);
        return naming().createGetterName(attribute);
    }

    private AttributeInfo getAttributeInfo(TableRef tableRef, String attribute) {
        if (attribute == null) throw new IllegalArgumentException("Null attribute received.");
        for (AttributeInfo candidate : getAttributeInfos(tableRef)) {
            if (candidate.getFieldName().equals(attribute)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException(String.format("%s is not an attribute of the class to which table %s is mapped.", attribute, tableRef));
    }

    private NamingStrategy naming(){
        return this.config.getNaming();
    }

    private TypeMapper typeMapper(){
        return this.config.getTypeMapper();
    }

    private String packageName() {
        return this.config.getPackageName();
    }

    private TableConfig tableConfig(TableRef tableRef) {
        return this.config.getTableConfig(tableRef);
    }

    private Collection<TableRef> tableRefs(){
        return this.config.getTableRefs();
    }
}
