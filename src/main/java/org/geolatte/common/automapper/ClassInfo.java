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

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {

	private final String className;

	private final String tableName;

	private final List<AttributeInfo> attributes = new ArrayList<AttributeInfo>();

	public ClassInfo(String tableName, String className) {
		this.className = className;
		this.tableName = tableName;
	}

	public AttributeInfo getIdAttribute() {
		for (AttributeInfo ai : getAttributes()) {
			if (ai.isIdentifier()) {
				return ai;
			}
		}
		return null;
	}

	public AttributeInfo getGeomAttribute() {
		for (AttributeInfo ai : getAttributes()) {
			if (ai.isGeometry()) {
				return ai;
			}
		}
		return null;
	}

	public List<AttributeInfo> getAttributes() {
		return attributes;
	}

	public String getClassName() {
		return className;
	}

	public String getTableName() {
		return tableName;
	}

	public void addAttribute(AttributeInfo ai) {
		this.attributes.add(ai);
	}

	public void removeAttribute(AttributeInfo ai) {
		this.attributes.remove(ai);
	}

	public void clearAttributes() {
		this.attributes.clear();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = PRIME * result
				+ ((className == null) ? 0 : className.hashCode());
		result = PRIME * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
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
		final ClassInfo other = (ClassInfo) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

}
