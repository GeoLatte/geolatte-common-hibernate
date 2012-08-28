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

import javassist.CtClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper that associates a mapped class together with metadata about the source table, and information
 * on how the tables columns have been mapped to .
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/26/12
 */
class TableMapping {

    final private TableMetaData tableMetaData;
    final private Map<Attribute, FieldInfo> mappedFields = new HashMap<Attribute, FieldInfo>();

    private Class<?> generatedClass;

    TableMapping(TableMetaData tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    String getHibernateType(Attribute attribute) {
        FieldInfo f = mappedFields.get(attribute);
        return f == null ? null : f.hibernateType;
    }

    String getPropertyName(Attribute attribute) {
        FieldInfo f = mappedFields.get(attribute);
        return f == null ? null : f.propertyName;
    }

    Class<?> getGeneratedClass() {
        return generatedClass;
    }

    TableMetaData getTableMetaData() {
        return tableMetaData;
    }

    Attribute getIdentifer() {
        for (Attribute attribute : getMappedAttributes()) {
            if (attribute.isIdentifier()) {
                return attribute;
            }
        }
        return null;
    }

    Collection<Attribute> getMappedAttributes() {
        return mappedFields.keySet();
    }

    String getSimpleName() {
        return getGeneratedClass().getSimpleName();
    }

    void addColumnMapping(Attribute ai, String propertyName, String hibernateType, CtClass ctClass) {
        mappedFields.put(ai, new FieldInfo(propertyName, hibernateType, ctClass));
    }

    void setGeneratedClass(Class<?> generatedClass) {
        this.generatedClass = generatedClass;
    }

    CtClass getType(Attribute attribute) {
        FieldInfo f = mappedFields.get(attribute);
        return f == null ? null : f.ctClass;
    }


    private static class FieldInfo {
        final String hibernateType;
        final CtClass ctClass;
        final String propertyName;

        FieldInfo(String propertyName, String hibernateType, CtClass ctClass) {
            this.propertyName = propertyName;
            this.hibernateType = hibernateType;
            this.ctClass = ctClass;
        }
    }
}
