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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The configuration for the
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/23/12
 */
public class AutoMapConfig {

    /**
     * The default package name
     */
    final public static String DEFAULT_PACKAGE_NAME = "org.geolatte.common.features.generated";

    final private String packageName;
    final private NamingStrategy naming;
    final private TypeMapper typeMapper;
    final private Map<TableRef, TableConfig> tableConfigs = new HashMap<TableRef, TableConfig>();

    /**
     * Constructs an instance
     * @param packageName the package name for all generated classes
     * @param naming the <code>NamingStrategy</code> to use
     * @param typeMapper the <code>TypeMapper</code> to use
     */
    public AutoMapConfig(String packageName, NamingStrategy naming, TypeMapper typeMapper) {
        this.packageName = packageName;
        this.naming = naming;
        this.typeMapper = typeMapper;
    }

    /**
     * Constructs an instance with default package name and a simple naming strategy.
     *
     * @param typeMapper the <code>TypeMapper</code> instance to use.
     */
    public AutoMapConfig(TypeMapper typeMapper) {
        this(DEFAULT_PACKAGE_NAME, new SimpleNamingStrategy(), typeMapper);
    }


    public String getPackageName() {
        return packageName;
    }

    public NamingStrategy getNaming() {
        return naming;
    }

    public TypeMapper getTypeMapper() {
        return typeMapper;
    }

    public void addTable(TableRef tableRef) {
        tableConfigs.put(tableRef, TableConfig.Builder.emptyConfig(tableRef));
    }

    public void addTableConfig(TableConfig config) {
        tableConfigs.put(config.getTableRef(), config);
    }


    public TableConfig getTableConfig(TableRef tableRef) {
        return tableConfigs.get(tableRef);
    }

    public Collection<TableRef> getTableRefs() {
        return this.tableConfigs.keySet();
    }

}
