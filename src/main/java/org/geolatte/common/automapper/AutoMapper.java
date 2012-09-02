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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps a set of tables to POJO Classes.
 * <p/>
 * <p>The central method of the <code>AutoMapper</code> is the <code>map()</code> method. It generates for each table
 * listed in its <code>AutoMapConfiguration</code> a POJO Class, and describes the mapping between the tables and the
 * generated POJO Classes in <code>DatabaseMapping</code> instance.</p>
 * <p/>
 * <p>A table can be mapped by the <code>AutoMapper</code> if the following condition is met: </p>
 * <ul>
 * <li>The table must have an identifier column; i.e. either the table has a primary key or the <code>
 * TableConfiguration</code> for the table names a column that functions as identifier.</li>
 * </ul>
 * <p>It may happen that only a subset of the columns of a table can be mapped to class properties. This happens if: </p>
 * <ul>
 * <li>the <code>TypeMapper</code> cannot handle the column type</li>
 * <li>the <code>NamingStrategy</code> generates a name for a property getter/setter that fails to compile</li>
 * </ul>
 * <p/>
 * <p>This class is thread-safe, but note that it has significant side-effects in that runtime-generated classes are loaded
 * into the ClassLoader specified in its constructor.</p>
 *
 * @author Karel Maesen, Geovise BVBA
 */
public class AutoMapper {

    private final static Logger LOGGER = LoggerFactory.getLogger(AutoMapper.class);

    final private AutoMapConfiguration configuration;

    final private ClassLoader classLoader;

    /**
     * Creates an <code>AutoMapper</code> using the specified configuration and <code>ClassLoader</code>.
     * <p/>
     * <p>The classes created by this <code>AutoMapper</code> will be loaded in the
     * specified <code>ClassLoader</code>.
     *
     * @param configuration the configuration for the instance being constructed
     * @param classLoader   the <code>ClassLoader</code> to use during the map() operation.
     */
    public AutoMapper(AutoMapConfiguration configuration, ClassLoader classLoader) {
        this.configuration = configuration;
        this.classLoader = classLoader;
    }

    /**
     * Returns the <code>DatabaseMapping</code> that describes the mapping for all tables listed in the
     * <code>AutoMapConfiguration</code> of this instance.
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
     * @return the XML mapping document that maps the tables listed in the <code>AutoMapConfiguration</code> of this instance.
     * @throws SQLException          if the JDBC <code>DataBaseMetaData</code> cannot be retrieved from the specified <code>Connection</code>
     * @throws IllegalStateException if this method is run twice on the same instance.
     */
    public DatabaseMapping map(Connection conn) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();
        Map<TableRef, TableMapping> mappedTables = new HashMap<TableRef, TableMapping>();
        TableMetaDataReader metaDataReader = new TableMetaDataReader(new DefaultGeometryColumnTest(typeMapper()));
        MappedClassGenerator mappedClassGenerator = new MappedClassGenerator(packageName(), naming(), typeMapper());

        for (TableRef tableRef : configuredTables()) {
            if (isAlreadyMapped(tableRef, mappedTables)) continue;
            try {
                TableMetaData tableMetaData = metaDataReader.read(getTableConfig(tableRef), dmd);
                TableMapping tableMapping = mappedClassGenerator.generate(tableMetaData, classLoader);
                mappedTables.put(tableRef, tableMapping);
            } catch (TableNotFoundException e) {
                LOGGER.warn(e.getMessage());
            } catch (MissingIdentifierException e) {
                LOGGER.warn(e.getMessage());
            }
        }
        return new DatabaseMapping(mappedTables, packageName());
    }

    private boolean isAlreadyMapped(TableRef tableRef, Map<TableRef, TableMapping> tableMapping) {
        return (tableMapping.keySet().contains(tableRef));
    }

    private NamingStrategy naming() {
        return this.configuration.getNaming();
    }

    private TypeMapper typeMapper() {
        return this.configuration.getTypeMapper();
    }

    private TableConfiguration getTableConfig(TableRef tableRef) {
        return this.configuration.getTableConfiguration(tableRef);
    }

    private Collection<TableRef> configuredTables() {
        return this.configuration.getTableRefs();
    }

    private String packageName() {
        return this.configuration.getPackageName();
    }
}
