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

/**
 * Determines how to derive class and
 * member names from table and column names.
 *
 * @author Karel Maesen, Geovise BVBA (http://www.geovise.com/)
 *
 */
public interface NamingStrategy {

    /**
     * Create a valid name for a member variable based on the specified input
     * name.
     *
     * @param base the input name.
     * @return a valid java identifier for a member variable.
     */
    public String createPropertyName(String base);

    /**
     * Create a valid name for a setter for the property
     *
     * @param propertyName the Java property name
     * @return valid java identifier for a property setter
     */
    public String createSetterName(String propertyName);

    /**
     * Create a valid name for a getter for the property
     *
     * @param propertyName the Java property name
     * @return valid java identifier for a property getter
     */
    public String createGetterName(String propertyName);

    /**
     * Create a valid name for a Java class based on the specified input.
     *
     * @param tableRef the <code>TableRef</code> for which to construct a name.
     * @return a valid Java identifier for a class.
     */
    public String createClassName(TableRef tableRef);

}
