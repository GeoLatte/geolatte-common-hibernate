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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

/**
 * <p>
 * Base class for hibernate existence criteria (e.g., property exists/does not exist)
 * </p>
 * <p>
 * <i>Creation-Date</i>: 01-Jun-2010<br>
 * <i>Creation-Time</i>:  01:03:40<br>
 * </p>
 *
 * @author Bert Vanhooff
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 * @since SDK1.5
 */
public abstract class AbstractExistenceCriterion implements Criterion {

    protected String propertyName;

    /**
     * Creates an AbstractExistenceCriterion for the given property name
     * @param propertyName The property name
     */
    public AbstractExistenceCriterion(String propertyName) {

        if (propertyName == null)
            throw new IllegalArgumentException("propertyName cannot be null");

        this.propertyName = propertyName;
    }

    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
