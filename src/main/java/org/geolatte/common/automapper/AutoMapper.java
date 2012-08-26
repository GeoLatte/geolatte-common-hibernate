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

    final private Map<TableRef, TableMapping> mappedClasses = new HashMap<TableRef, TableMapping>();

    final private ClassLoader classLoader;

    /**
     * Creates an <code>AutoMapper</code> using the specified configuration and <code>ClassLoader</code>.
     * <p/>
     * <p>The classes created by this <code>AutoMapper</code> will be loaded in the
     * specified <code>ClassLoader</code>.
     *
     * @param config
     * @param classLoader
     */
    public AutoMapper(AutoMapConfig config, ClassLoader classLoader) {
        this.config = config;
        this.classLoader = classLoader;
    }

    /**
     * Returns the Hibernate mapping document for the specified tables
     * <p/>
     * <p>To create the mapping, a <code>Connection</code> object must be
     * provided to provide access to the specified tables.
     * This connection will not be closed on return.</p>
     *
     * @param conn JDBC <code>Connection</code> used during mapping
     * @return the XML mapping document that maps the tables specified by the catalog, schema and tablenames arguments.
     * @throws SQLException
     */
    public Document map(Connection conn) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();
        TableMetaDataReader metaDataReader = new TableMetaDataReader(new DefaultGeometryColumnTest(typeMapper()));
        MappedClassGenerator mappedClassGenerator = new MappedClassGenerator(getPackageName(), naming(), typeMapper());

        for (TableRef tableRef : configuredTables()) {
            if (isAlreadyMapped(tableRef)) continue;
            try {
                TableMetaData tableMetaData = metaDataReader.read(getTableConfig(tableRef), dmd);
                TableMapping tableMapping = mappedClassGenerator.generate(tableMetaData, classLoader);
                mappedClasses.put(tableRef, tableMapping);
            } catch (TableNotFoundException e) {
                LOGGER.warn(e.getMessage());
            } catch (MissingIdentifierException e) {
                LOGGER.warn(e.getMessage());
            }
        }
        return generateMappingXML();
    }

    public String getPackageName() {
        return this.config.getPackageName();
    }

    /**
     * Returns the <code>Class</code> object to which the specified table is mapped
     *
     * @return class to which the table specified by the argument is mapped
     * @tableRef the <code>TableRef</code>
     */
    TableMapping getMappedClass(TableRef tableRef) {
        return mappedClasses.get(tableRef);

    }

    public Class<?> getGeneratedClass(TableRef tableRef) {
        TableMapping mc = mappedClasses.get(tableRef);
        return mc == null ? null : mc.getGeneratedClass();
    }

    /**
     * Returns the tables mapped by this automapper.
     *
     * @return a List of mapped tables. Each table is represented by a String array with the first
     *         component the catalog, the second the schema, and the third the table name.
     */
    public List<TableRef> getMappedTables() {
        List<TableRef> list = new ArrayList<TableRef>();
        for (TableRef tbn : mappedClasses.keySet()) {
            list.add(tbn);
        }
        return list;
    }

    /**
     * Returns the attribute metadata for the class to which the specified table is mapped
     *
     * @param tableRef
     * @return list of attribute (field) names of the class that corresponds with the table identified by the arguments
     */
    public Collection<Attribute> getAttributes(TableRef tableRef) {
        TableMapping mc = mappedClasses.get(tableRef);
        if (mc == null) return new ArrayList<Attribute>();
        return mc.getMappedAttributes();
    }

    private Document generateMappingXML() {
        LOGGER.info("Generating Hibernate Mapping file");
        MappingsGenerator mappingGenerator = new MappingsGenerator();
        mappingGenerator.load(this);
        return mappingGenerator.getMappingsDoc();
    }

    /**
     * Returns the name of the Identifier property
     *
     * @param tableRef
     * @return the attribute name which functions as a unique identifier for the objects corresponding
     *         to rows in the specified table
     */
    public String getIdAttribute(TableRef tableRef) {
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) {
            return null;
        }
        for (Attribute attribute : tableMapping.getMappedAttributes()) {
            if (attribute.isIdentifier()) {
                return tableMapping.getPropertyName(attribute);
            }
        }
        return null;
    }


    /**
     * Returns the (default) <code>Geometry</code>-valued attribute
     *
     * @return the name of the <code>Geometry</code>-valued attribute
     */
    public String getGeometryAttribute(TableRef tableRef) {
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) {
            return null;
        }
        for (Attribute attribute : tableMapping.getMappedAttributes()) {
            if (attribute.isGeometry()) {
                return tableMapping.getPropertyName(attribute);
            }
        }
        return null;
    }

    private boolean isAlreadyMapped(TableRef tableRef) {
        if (getMappedClass(tableRef) != null) {
            LOGGER.info("Class info for table " + tableRef + " has already been mapped.");
            return true;
        }
        return false;
    }

    private NamingStrategy naming() {
        return this.config.getNaming();
    }

    private TypeMapper typeMapper() {
        return this.config.getTypeMapper();
    }

    private TableConfig getTableConfig(TableRef tableRef) {
        return this.config.getTableConfig(tableRef);
    }

    private Collection<TableRef> configuredTables() {
        return this.config.getTableRefs();
    }
}
