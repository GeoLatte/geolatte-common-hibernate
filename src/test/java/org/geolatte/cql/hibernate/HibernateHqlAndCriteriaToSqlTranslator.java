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
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * No comment provided yet for this class.
 * <p/>
 * <p>
 * <i>Creation-Date</i>: 19-Aug-2010<br>
 * <i>Creation-Time</i>:  17:40:03<br>
 * </p>
 *
 * @author Bert Vanhooff
 * @author <a href="http://www.qmino.com">Qmino bvba</a>
 * @since SDK1.5
 */
public class HibernateHqlAndCriteriaToSqlTranslator {

    private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory){
    this.sessionFactory = sessionFactory;
  }

  public String toSql(Criteria criteria){
    try{
      CriteriaImpl c = (CriteriaImpl) criteria;
      SessionImpl s = (SessionImpl)c.getSession();
      SessionFactoryImplementor factory = (SessionFactoryImplementor)s.getSessionFactory();
      String[] implementors = factory.getImplementors( c.getEntityOrClassName() );
      CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable)factory.getEntityPersister(implementors[0]),
        factory, c, implementors[0], s.getLoadQueryInfluencers());
      Field f = OuterJoinLoader.class.getDeclaredField("sql");
      f.setAccessible(true);
      return (String) f.get(loader);
    }
    catch(Exception e){
      throw new RuntimeException(e); 
    }
  }

  public String toSql(String hqlQueryText){
    if (hqlQueryText!=null && hqlQueryText.trim().length()>0){
      final QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
      final SessionFactoryImplementor factory =
        (SessionFactoryImplementor) sessionFactory;
      final QueryTranslator translator = translatorFactory.
        createQueryTranslator(
          hqlQueryText,
          hqlQueryText,
          Collections.EMPTY_MAP, factory
        );
      translator.compile(Collections.EMPTY_MAP, false);
      return translator.getSQLString();
    }
    return null;
  }








}
