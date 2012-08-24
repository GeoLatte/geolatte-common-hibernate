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
 * This Exception is thrown when the POJO Utility cannot locate a primary key.
 * 
 * @author Karel Maesen, Geovise BVBA
 * 
 */
public class MissingIdentifierException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String basemsg = "No suitable identifier found in table";

//	public MissingIdentifierException() {
//		super(basemsg);
//	}

	public MissingIdentifierException(String msg) {
		super(basemsg + ":" + msg);
	}

//	public MissingIdentifierException(Throwable cause) {
//		super(cause);
//	}
//
//	public MissingIdentifierException(String msg, Throwable cause) {
//		super(basemsg + ":" + msg, cause);
//	}

}
