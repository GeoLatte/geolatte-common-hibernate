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

import java.security.ProtectionDomain;

public class FeatureClassGenerator {

	private final String packageName;
	private final NamingStrategy naming;
	private final static ClassPool pool = ClassPool.getDefault();

	public FeatureClassGenerator(String packageName, NamingStrategy naming) {
		this.packageName = packageName;
		this.naming = naming;
	}

	public Class<?> generate(ClassInfo classInfo) {

		try {
			String classname = packageName + "." + classInfo.getClassName();
			CtClass pojo = pool.makeClass(classname);
			for (AttributeInfo ai : classInfo.getAttributes()) {
				CtField field = createField(ai, pojo);
				CtMethod getter = createGetterMethod(field);
				CtMethod setter = createSetterMethod(field);
				pojo.addField(field);
				pojo.addMethod(getter);
				pojo.addMethod(setter);
			}
			ProtectionDomain pd = pojo.getClass().getProtectionDomain();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Class<?> clazz = pojo.toClass(cl, pd);
			pojo.detach();
			return clazz;
		} catch (CannotCompileException e){
			throw new RuntimeException("Problem generating class for table " + classInfo.getTableName(), e);
		}
		
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

	private CtField createField(AttributeInfo ai, CtClass declaring)
			throws CannotCompileException {
			CtField f = new CtField(ai.getCtClass(), ai.getFieldName(), declaring);
		return f;
	}

}
