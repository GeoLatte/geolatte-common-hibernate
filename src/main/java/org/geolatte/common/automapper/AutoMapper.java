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
 * Maps a set of tables to POJO Classes.
 * <p>The central method of the <code>AutoMapper</code> is the <code>map()</code> method. It generates for each table
 * listed in its <code>AutoMapConfig</code> a POJO Class, and generates a mapping document for Hibernate ORM that describes
 * the mapping between the tables and the generated POJO Classes.</p>
 * <p>A table can be mapped by the <code>AutoMapper</code> if the following condition is met: </p>
 * <ul>
 * <li>The table must have an identifier column; i.e. either the table has a primary key or the <code>
 * TableConfig</code> for the table names a column that functions as identifier.</li>
 * </ul>
 * <p>It may happen that only a subset of the columns of a table can be mapped to class properties. This happens if: </p>
 * <ul>
 * <li>the <code>TypeMapper</code> cannot handle the column type</li>
 * <li>the <code>NamingStrategy</code> generates a name for a property getter/setter that fails to compile</li>
 * </ul>
 *
 * @author Karel Maesen, Geovise BVBA
 */
//TODO -- ensures that mapping is only once performed, and that methods that depend on having first run a mapping throw
// IllegalStateException
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
     * Returns the Hibernate mapping document that describes the mapping for all tables listed in the
     * <code>AutoMapConfig</code> of this instance.
     * <p/>
     * <p>As a side-effect, the POJO classes corresponding to the tables are generated and loaded in the
     * <code>ClassLoader</code> associated with this instance.</p>
     * <p/>
     * <p>To create the mapping, a <code>Connection</code> object must be
     * provided to provide access to the specified tables. This connection will <em>not</em> be closed on return.</p>
     * <p/>
     * <p>If for some reason the mapping operation for a table fails (e.g. no identifier), then a warning will be written
     * to the log, and the operation will continue with the next table.</p>
     *
     * @param conn JDBC <code>Connection</code> used during mapping
     * @return the XML mapping document that maps the tables listed in the <code>AutoMapConfig</code> of this instance.
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

    /**
     * Returns the name of the package that holds all generated classes.
     *
     * @return the name of the package that holds all generated classes.
     */
    public String getPackageName() {
        return this.config.getPackageName();
    }

    /**
     * Returns the <code>TableMapping</code> for the specified table.
     *
     * @return the <code>TableMapping</code> for the table specified by the <code>tableRef</code> parameter.
     * @tableRef the <code>TableRef</code> that determines a table in the database
     */
    TableMapping getTableMapping(TableRef tableRef) {
        return mappedClasses.get(tableRef);

    }

    /**
     * Returns the <code>Class</code> object to which the specified table is mapped.
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return the <code>Class</code> object generated from the table specifed by the <code>tableRef</code> parameter,
     *         or null if no such class has been generated.
     */
    public Class<?> getGeneratedClass(TableRef tableRef) {
        TableMapping mc = mappedClasses.get(tableRef);
        return mc == null ? null : mc.getGeneratedClass();
    }

    /**
     * Returns the <code>TableRef</code>s to all tables mapped by this <code>AutoMapper</code>.
     *
     * @return the <code>TableRef</code>s to all the tables mapped by this <code>AutoMapper</code>.
     */
    public List<TableRef> getMappedTables() {
        List<TableRef> list = new ArrayList<TableRef>();
        for (TableRef tbn : mappedClasses.keySet()) {
            list.add(tbn);
        }
        return list;
    }

    /**
     * Returns the property names of the POJO Class to which the specified table is mapped
     * <p/>
     * <p>If the specified table is not mapped by this instance, it returns an empty <code>List</code>.</p>
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return list of properties of the class that corresponds with the table identified by the arguments
     */
    public List<String> getProperties(TableRef tableRef) {
        List<String> result = new ArrayList<String>();
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) return result;
        for ( ColumnMetaData columnMetaData : tableMapping.getMappedColumns()) {
            ColumnMapping cm = tableMapping.getColumnMapping(columnMetaData);
            result.add(cm.getPropertyName());
        }
        return result;
    }

    /**
     * Returns the name of the Identifier property
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return the name of the identifier property, or null if the table specified by the <code>tableRef</code>
     *         parameter is not mapped
     */
    public String getIdProperty(TableRef tableRef) {
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) {
            return null;
        }
        ColumnMetaData identifierColumn = tableMapping.getIdentifierColumn();
        return tableMapping.getColumnMapping(identifierColumn).getPropertyName();
    }

    /**
     * Returns the name of the primary geometry property.
     * <p/>
     * <p>For the meaning of Primary, see {@link ColumnMetaData#isGeometry()}.</p>
     *
     * @param tableRef the <code>TableRef</code> for the table
     * @return the name of the primary geometry property, or null if the table specified by the <code>tableRef</code>
     *         parameter is not mapped or has no primary geometry.
     */
    public String getGeometryProperty(TableRef tableRef) {
        TableMapping tableMapping = mappedClasses.get(tableRef);
        if (tableMapping == null) {
            return null;
        }
        for (ColumnMetaData columnMetaData : tableMapping.getMappedColumns()) {
            if (columnMetaData.isGeometry()) {
                ColumnMapping cm = tableMapping.getColumnMapping(columnMetaData);
                return cm.getPropertyName();
            }
        }
        return null;
    }


    private Document generateMappingXML() {
        LOGGER.info("Generating Hibernate Mapping file");
        MappingsGenerator mappingGenerator = new MappingsGenerator();
        mappingGenerator.load(this);
        return mappingGenerator.getMappingsDoc();
    }

    private boolean isAlreadyMapped(TableRef tableRef) {
        if (getTableMapping(tableRef) != null) {
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
