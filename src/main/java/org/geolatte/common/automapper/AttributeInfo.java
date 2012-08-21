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

import org.hibernatespatial.GeometryUserType;

/**
 * @author Karel Maesen
 * 
 * 
 */
public class AttributeInfo {

	private String columnName;

	private String fieldName;

	private String hibernateType;
	
	private CtClass ctClass;

	private boolean isIdentifier;

	public boolean isGeometry() {
		return this.hibernateType.equalsIgnoreCase(GeometryUserType.class
				.getCanonicalName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeInfo other = (AttributeInfo) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		return true;
	}

	public CtClass getCtClass() {
		return ctClass;
	}

	public void setCtClass(CtClass ctType) {
		this.ctClass = ctType;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getHibernateType() {
		return hibernateType;
	}

	public void setHibernateType(String hibernateType) {
		this.hibernateType = hibernateType;
	}

	public boolean isIdentifier() {
		return isIdentifier;
	}

	public void setIdentifier(boolean isIdentifier) {
		this.isIdentifier = isIdentifier;
	}

	

}
