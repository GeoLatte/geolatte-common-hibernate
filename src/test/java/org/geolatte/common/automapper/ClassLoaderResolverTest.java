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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/26/12
 */
public class ClassLoaderResolverTest {

    @Test
    public void testBasicCase() {
        ClassLoaderResolver resolver = new ClassLoaderResolver(DisposableClassLoader.class.getCanonicalName());
        ClassLoader classLoader = resolver.newInstance(ClassLoader.getSystemClassLoader());
        assertNotNull(classLoader);
        assertEquals(ClassLoader.getSystemClassLoader(), classLoader.getParent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArgument() {
        new ClassLoaderResolver(null);
    }

    @Test(expected = IllegalStateException.class)
    public  void testClassNotOnClassPath(){
        new ClassLoaderResolver("Foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public  void testNoClassLoaderClassName(){
        new ClassLoaderResolver("java.lang.Thread");
    }

}
