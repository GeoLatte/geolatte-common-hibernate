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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a ClassLoader (using reflection).
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 8/26/12
 */
//TODO -- should be  moved to o.g.featureserver
public class ClassLoaderResolver {


    private final Constructor constructor;

    public ClassLoaderResolver(String classLoaderClassName) {
        if (classLoaderClassName == null) {
            throw new IllegalArgumentException("Null classname is not allowed.");
        }
        constructor = findConstructor(classLoaderClass(classLoaderClassName));
        //test the method -- so user finds out early whether it works or not.
        newInstance(null);
    }

    private Class<? extends ClassLoader> classLoaderClass(String classLoaderClassName) {
        Class<?> clClass;
        try {
            clClass = Class.forName(classLoaderClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    String.format("ClassLoader %s not on classpath.", classLoaderClassName), e);
        }
        if (ClassLoader.class.isAssignableFrom(clClass)) {
            return (Class<? extends ClassLoader>) clClass;
        }
        throw new IllegalArgumentException(String.format("Class %s is not a subclass of ClassLoader", classLoaderClassName));
    }

    private Constructor findConstructor(Class<? extends ClassLoader> clClass) {
        for (Constructor<?> constructor : clClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 1 &&
                    ClassLoader.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                return constructor;
            }
        }
        throw new IllegalStateException("No single-parameter constructor found with type ClassLoader in class "
                + clClass.getCanonicalName());
    }

    public ClassLoader newInstance(ClassLoader parent) {
        String msg = "Cannot create a classloader.";
        try {
            return (ClassLoader) constructor.newInstance(parent);
        } catch (InstantiationException e) {
            throw new IllegalStateException(msg, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(msg, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(msg, e);
        }

    }

}
