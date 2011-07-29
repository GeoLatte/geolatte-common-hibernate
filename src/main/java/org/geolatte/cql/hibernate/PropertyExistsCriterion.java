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
 * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.cql.hibernate;

import org.geolatte.cql.hibernate.AbstractExistenceCriterion;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.criterion.CriteriaQuery;

/**
 * <p>
 * Hibernate criterion that evaluates to <tt>TRUE</tt> if a given property exists and to <tt>FALSE</tt> if the property is not present.
 * </p>
 * <p>
 * <i>Creation-Date</i>: 31-May-2010<br>
 * <i>Creation-Time</i>:  18:04:30<br>
 * </p>
 *
 * @author Bert Vanhooff
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 * @since SDK1.5
 */
public class PropertyExistsCriterion extends AbstractExistenceCriterion {

    /**
     * Constructs a PropertyExistsCriterion
     * @param propertyName The property name that is checked for existence
     */
    public PropertyExistsCriterion(String propertyName) {
        super(propertyName);
    }

    /**
	 * Render the SQL fragment that corresponds to this criterion.
	 *
	 * @param criteria The local criteria
	 * @param criteriaQuery The overal criteria query
	 *
	 * @return The generated SQL fragment
	 * @throws org.hibernate.HibernateException Problem during rendering.
	 */
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {

        String[] columns;
        try
        {
            columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
        }
        catch (QueryException e)
        {
            columns = new String[0];
        }

        // if there are columns that map the given property.. the property exists, so we don't need to add anything to the sql
        return columns.length > 0 ? "TRUE" : "FALSE";
    }
}
