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

package org.geolatte.common.cql.hibernate;

import org.geolatte.common.reflection.EntityClassReader;
import org.geolatte.common.cql.AbstractBuilder;
import org.geolatte.common.cql.node.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * <p>
 * Treewalker that builds a Hibernate Criteria based on a given CQL AST. Use as follows:
 * <pre>
 * {@code
 * HibernateCriteriaBuilder builder = new HibernateCriteriaBuilder(clazz); // builder for the given class
 * tree.apply(builder); // with tree, the root element of the AST as returned by the parser.
 * }
 * </pre>
 *
 * </p>
 * <p>
 * <i>Creation-Date</i>: 31-May-2010<br>
 * <i>Creation-Time</i>:  09:47:51<br>
 * </p>
 *
 * @author Bert Vanhooff
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 * @since SDK1.5
 */
class HibernateCriteriaBuilder extends AbstractBuilder {

    // The criteria that is built incrementally by walking the tree
    private DetachedCriteria criteria;
    EntityClassReader reader;

    // A map of all translated nodes as they are visited depth first.
    private HashMap<Node, Criterion> translatedExpressions = new HashMap<Node, Criterion>();
    

    public HibernateCriteriaBuilder(Class clazz) {
        criteria = DetachedCriteria.forClass(clazz);
        reader = EntityClassReader.getClassReaderFor(clazz);
    }

    public DetachedCriteria getCriteria() {
        return criteria;
    }

    @Override
    public void caseStart(Start node) {

        node.getPExpr().apply(this);

        criteria.add(translatedExpressions.get(node.getPExpr()));
    }

    @Override
    public void outAAndExpr(AAndExpr node) {

        translatedExpressions.put(node, Restrictions.and(translatedExpressions.get(node.getLeft()), translatedExpressions.get(node.getRight())));
    }

    @Override
    public void outAOrExpr(AOrExpr node) {

        translatedExpressions.put(node, Restrictions.or(translatedExpressions.get(node.getLeft()), translatedExpressions.get(node.getRight())));
    }

    @Override
    public void outANotExpr(ANotExpr node) {

        translatedExpressions.put(node, Restrictions.not(translatedExpressions.get(node.getExpr())));
    }


    @Override
    public void outAGtExpr(AGtExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        translatedExpressions.put(node, Restrictions.gt(propertyAlias, reader.parseAsPropertyType(translatedLiterals.get(node.getRight()).toString(), getPropertyPath(node.getLeft()))));
    }

    @Override
    public void outAGteExpr(AGteExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        translatedExpressions.put(node, Restrictions.ge(propertyAlias, reader.parseAsPropertyType(translatedLiterals.get(node.getRight()).toString(), getPropertyPath(node.getLeft()))));
    }

    @Override
    public void outALtExpr(ALtExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        translatedExpressions.put(node, Restrictions.lt(propertyAlias, reader.parseAsPropertyType(translatedLiterals.get(node.getRight()).toString(), getPropertyPath(node.getLeft()))));
    }

    @Override
    public void outALteExpr(ALteExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        translatedExpressions.put(node, Restrictions.le(propertyAlias, reader.parseAsPropertyType(translatedLiterals.get(node.getRight()).toString(), getPropertyPath(node.getLeft()))));
    }

    @Override
    public void outAEqExpr(AEqExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        translatedExpressions.put(node, Restrictions.eq(propertyAlias, reader.parseAsPropertyType(translatedLiterals.get(node.getRight()).toString(), getPropertyPath(node.getLeft()))));
    }

    @Override
    public void outANeqExpr(ANeqExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        translatedExpressions.put(node, Restrictions.ne(propertyAlias, reader.parseAsPropertyType(translatedLiterals.get(node.getRight()).toString(), getPropertyPath(node.getLeft()))));
    }

    @Override
    public void outALikeExpr(ALikeExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        EscapingLikeExpression likeExpression = new EscapingLikeExpression(propertyAlias, translatedLiterals.get(node.getRight()).toString());
        translatedExpressions.put(node, likeExpression);
    }

    @Override
    public void outANotLikeExpr(ANotLikeExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        EscapingLikeExpression likeExpression = new EscapingLikeExpression(propertyAlias, translatedLiterals.get(node.getRight()).toString());
        translatedExpressions.put(node, Restrictions.not(likeExpression));
    }

    @Override
    public void outAIlikeExpr(AIlikeExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        EscapingLikeExpression likeExpression = new EscapingLikeExpression(propertyAlias, translatedLiterals.get(node.getRight()).toString(), true);
        translatedExpressions.put(node, likeExpression);
    }

    @Override
    public void outANotIlikeExpr(ANotIlikeExpr node) {

        String propertyAlias = createAlias(node.getLeft());
        EscapingLikeExpression likeExpression = new EscapingLikeExpression(propertyAlias, translatedLiterals.get(node.getRight()).toString(), true);
        translatedExpressions.put(node, Restrictions.not(likeExpression));
    }

    @Override
    public void outAIsNullExpr(AIsNullExpr node) {

        String propertyAlias = createAlias(node.getAttr());
        translatedExpressions.put(node, Restrictions.isNull(propertyAlias));
    }

    @Override
    public void outAIsNotNullExpr(AIsNotNullExpr node) {

        String propertyAlias = createAlias(node.getAttr());
        translatedExpressions.put(node, Restrictions.isNotNull(propertyAlias));
    }

    @Override
    public void outAExistsExpr(AExistsExpr node) {

        String propertyAlias = createAlias(node.getAttr());
        translatedExpressions.put(node, new PropertyExistsCriterion(propertyAlias) );
    }

    @Override
    public void outADoesNotExistExpr(ADoesNotExistExpr node) {

        String propertyAlias = createAlias(node.getAttr());
        translatedExpressions.put(node, new PropertyDoesNotExistCriterion(propertyAlias) );
    }

    @Override
    public void outABeforeExpr(ABeforeExpr node) {

        String propertyAlias = createAlias(node.getAttr());
        translatedExpressions.put(node, Restrictions.lt(propertyAlias, parseDate(node.getDateTime().toString().trim())));
    }

    @Override
    public void outAAfterExpr(AAfterExpr node) {

        String propertyAlias = createAlias(node.getAttr());
        translatedExpressions.put(node, Restrictions.gt(propertyAlias, parseDate(node.getDateTime().toString().trim())));
    }

    @Override
    public void outADuringExpr(ADuringExpr node) {

        PTimespanLiteral timespan = node.getTimeSpan();

        Criterion greaterThan;
        Criterion lowerThan;

        if (timespan instanceof AFromToTimespanLiteral ) {

            AFromToTimespanLiteral fromToTimespan = (AFromToTimespanLiteral)timespan;
            greaterThan = Restrictions.gt(node.getAttr().toString().trim(), parseDate(fromToTimespan.getFrom().getText().trim()));
            lowerThan = Restrictions.lt(node.getAttr().toString().trim(), parseDate(fromToTimespan.getTo().getText().trim()));
        }
        else if (timespan instanceof AFromDurationTimespanLiteral) {

            AFromDurationTimespanLiteral fromDurationTimespan = (AFromDurationTimespanLiteral)timespan;
            Date fromDate = parseDate(fromDurationTimespan.getFrom().getText().trim());
            Duration duration = (Duration)translatedLiterals.get(fromDurationTimespan.getDuration());

            // Calculate to 'to' date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDate);
            calendar.add(Calendar.YEAR, duration.getYears());
            calendar.add(Calendar.MONTH, duration.getMonths());
            calendar.add(Calendar.DATE, duration.getDays());
            calendar.add(Calendar.HOUR, duration.getHours());
            calendar.add(Calendar.MINUTE, duration.getMinutes());
            calendar.add(Calendar.SECOND, duration.getSeconds());
            Date toDate = calendar.getTime();

            greaterThan = Restrictions.gt(node.getAttr().toString().trim(), fromDate);
            lowerThan = Restrictions.lt(node.getAttr().toString().trim(), toDate);

        } else { // if (timespan instanceof ADurationToTimespanLiteral)

            greaterThan = null;
            lowerThan = null;
        }

        Criterion combined = Restrictions.and(greaterThan, lowerThan);
        translatedExpressions.put(node, combined);
    }

    // List of property paths for which aliasses are created.
    Map<String, String> createdAliasses = new HashMap<String, String>();

    /**
     * Creates an alias for the property's parent if necessary (the property is compound)
     * E.g. for a property path "address.street.number", an alias is created for "address.street", which can be used to
     * access the number property.
     *
     * @param attr The attribute/property node to create an alias for.
     * @return The aliased Property name
     */
    private String createAlias(PAttr attr) {

        // This is a simple property -> should not create an alias
        if (attr instanceof AIdAttr)
            return ((AIdAttr) attr).getIdentifier().getText().trim();

        // In the other case, we have a compound property -> must create an alias (or reuse an existing one) and return the alias name to the property path

        // Loop through all parts and create aliasses along the way.
        List<String> propertyParts = getPropertyParts(attr);
        String currentPropertyPath = ""; // the current property path
        String currentAlias = "";
        for (int i = 0; i < propertyParts.size() - 1; i++) { // last part is the final property itself.. must not create an alias for that one

            String currentPropertyPart = propertyParts.get(i);
            currentPropertyPath += currentPropertyPart;

            if (createdAliasses.containsKey(currentPropertyPath)) // already have an alias
                currentAlias = createdAliasses.get(currentPropertyPath);
            else {

                String newAlias = currentAlias + currentPropertyPart + "01";
                criteria = criteria.createAlias((currentAlias.length() == 0 ? "" :(currentAlias + ".")) + currentPropertyPart, newAlias);
                createdAliasses.put(currentPropertyPath, newAlias);
                currentAlias = newAlias;
            }

            currentPropertyPath += ".";
        }

        String lastPropertyPart = propertyParts.get(propertyParts.size()-1); // Last (missing) part of the property path.
        return currentAlias.length() == 0 ? lastPropertyPart : currentAlias + "." + lastPropertyPart;
    }
}
