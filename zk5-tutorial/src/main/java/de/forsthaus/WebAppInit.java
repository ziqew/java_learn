/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Zksample2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package de.forsthaus;

import org.zkoss.zk.ui.WebApp;

/**
 * At time only used for debuging to find an error while Tomcat is crashed.
 * 
 * This clas must be declared in the zk.xml.
 * 
 * @author Stephan Gerth
 */
public class WebAppInit implements org.zkoss.zk.ui.util.WebAppInit {

	@Override
	public void init(WebApp arg0) throws Exception {
		// TODO Auto-generated method stub

		// Turn on the debug level for serialization errors
		org.zkoss.util.logging.Log.lookup("org.zkoss.io.serializable").setLevel("DEBUG");

		System.out.println("#### Logger enabled for serialization errors!");

	}

}
