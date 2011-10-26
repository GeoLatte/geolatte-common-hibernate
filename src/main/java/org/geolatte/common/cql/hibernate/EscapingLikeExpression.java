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
 * Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.common.cql.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.TypedValue;

/**
 * <p>
 * Hibernate like/ilike criterion which allows the following (cql) escape sequences:
 * ' = ''
 * % = \%
 * _ = \_
 * \ = \\
 * </p>
 *
 * <p>
 *     We do not subclass from LikeExpression/IlikeExpression because the first one has a bug: always converts to lower-
 *     case and the second one does not support escape characters. Furthermore, we would need to keep local copies of several
 *     (private) fields.
 * </p>
 *
 * @author Bert Vanhooff
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 * @since SDK1.5
 */
public class EscapingLikeExpression implements Criterion {

    private boolean ignoreCase = false;
    private String value;
    private boolean valueConstainsEscapes;
    private String propertyName;
    private final char escapeChar = '\\'; // escape char = single backslash

    /**
     * Constructs a like expression as "propertyName LIKE value". Default escape character is '\'
     * @param propertyName The string property to match.
     * @param value        The value to match.
     * @param ignoreCase   Whether to do a case-insensitive search (ilike).
     */
    public EscapingLikeExpression(String propertyName, String value, boolean ignoreCase) {

        this.ignoreCase = ignoreCase;
        this.value = escapeString(value);
        this.propertyName = propertyName;
    }

    /**
     * Constructs a like expression as propertyName LIKE value.
     * @param propertyName The string property to match.
     * @param value        The value to match.
     */
    public EscapingLikeExpression(String propertyName, String value) {

        this(propertyName, value, false);
    }

    private String escapeString(String inputString) {

        // remove quote escape, apparently this is not needed when binding the value afterwards instead of giving it in the sql directly
        inputString = inputString.replace("''", "'");

        valueConstainsEscapes = inputString.contains(new String(new char[] {escapeChar}));

        return inputString;
    }

    public String toSqlString(
			Criteria criteria,
			CriteriaQuery criteriaQuery) throws HibernateException {
		Dialect dialect = criteriaQuery.getFactory().getDialect();
		String[] columns = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
		if ( columns.length != 1 ) {
			throw new HibernateException( "Like may only be used with single-column properties" );
		}

        String lhsAndOperator;
        if (ignoreCase) { // case insensitive: use 'ilike ?' for postgres or 'lowercaseFunction(value) like ?' for others
            if ( dialect instanceof PostgreSQLDialect) {
                lhsAndOperator = columns[0] + " ilike ?";
            }
            else {
                lhsAndOperator = dialect.getLowercaseFunction() + '(' + columns[0] + ')' + " like ?";
            }
        }
        else { // case sensitive: use ordinary like
            lhsAndOperator = columns[0] + " like ?";
        }

		return lhsAndOperator + ( valueConstainsEscapes ? " escape \'" + escapeChar + "\'" : "");

	}

    public TypedValue[] getTypedValues(
			Criteria criteria,
			CriteriaQuery criteriaQuery) throws HibernateException {
		return new TypedValue[] {
				criteriaQuery.getTypedValue( criteria, propertyName, ignoreCase ? value.toLowerCase() : value)
		};
	}

}
