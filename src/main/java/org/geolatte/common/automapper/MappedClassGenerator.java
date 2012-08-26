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


import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.ProtectionDomain;

class MappedClassGenerator {

    final protected static Logger LOGGER = LoggerFactory.getLogger(MappedClassGenerator.class);

    final private String packageName;
    final private NamingStrategy naming;
    final private TypeMapper typeMapper;

    private final static ClassPool pool = ClassPool.getDefault();

    public MappedClassGenerator(String packageName, NamingStrategy naming, TypeMapper typeMapper) {
        this.packageName = packageName;
        this.naming = naming;
        this.typeMapper = typeMapper;
    }

    public TableMapping generate(TableMetaData tableMetaData, ClassLoader classLoader) {

        try {
            String className = packageName + "." + naming.createClassName(tableMetaData.getTableRef());
            LOGGER.info(String.format("Mapping table %s to %s", tableMetaData.getTableRef(), className));
            TableMapping result = new TableMapping(tableMetaData);
            CtClass ctClass = pool.makeClass(className);
            for (Attribute ai : tableMetaData.getAttributes()) {
                generatePropertyForAttribute(result, ctClass, ai);
            }
            loadClass(classLoader, result, ctClass);
            return result;
        } catch (CannotCompileException e) {
            throw new RuntimeException("Problem generating class for table " + tableMetaData.getTableRef(), e);
        }

    }

    private void loadClass(ClassLoader classLoader, TableMapping result, CtClass ctClass) throws CannotCompileException {
        ProtectionDomain pd = ctClass.getClass().getProtectionDomain();
        Class<?> clazz = ctClass.toClass(classLoader, pd);
        ctClass.detach();
        result.setGeneratedClass(clazz);
    }

    private void generatePropertyForAttribute(TableMapping tableMapping, CtClass pojo, Attribute ai) {
        try {
            CtClass ctClass = typeMapper.getCtClass(ai.getDbTypeName(), ai.getSqlType());
            String propertyName = naming.createPropertyName(ai.getColumnName());
            CtField field = new CtField(ctClass, propertyName, pojo);
            CtMethod getter = createGetterMethod(field);
            CtMethod setter = createSetterMethod(field);
            pojo.addField(field);
            pojo.addMethod(getter);
            pojo.addMethod(setter);
            tableMapping.addColumnMapping(ai, propertyName, typeMapper.getHibernateType(ai.getDbTypeName(), ai.getSqlType()), ctClass);
            return;
        } catch (CannotCompileException e) {
            LOGGER.warn("Error compiling getter/setter methods for column: " + ai.getColumnName(), e);
        } catch (TypeNotFoundException e) {
            LOGGER.warn(String.format("Cannot match type for column %s (sql-type %d).", ai.getColumnName(), ai.getSqlType()));
        }
        LOGGER.warn("No property included in mapped class corresponding to column " + ai.getColumnName());
    }

    private CtMethod createGetterMethod(CtField field) throws CannotCompileException {
        String fn = field.getName();
        return CtNewMethod.getter(this.naming.createGetterName(fn), field);
    }

    private CtMethod createSetterMethod(CtField field)
            throws CannotCompileException {
        String fn = field.getName();
        return CtNewMethod.setter(naming.createSetterName(fn), field);
    }

}
