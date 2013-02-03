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
package de.forsthaus.common.menu.domain;

import java.lang.ref.SoftReference;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class MetaMenuFactory {

	static private SoftReference<RootMenuDomain> referenceRootMenuDomain = new SoftReference<RootMenuDomain>(null);
	static String menuXMLRootPath = "/";

	static public RootMenuDomain getRootMenuDomain() {
		RootMenuDomain rootMenuDomain = referenceRootMenuDomain.get();
		if (rootMenuDomain == null) {
			try {
				Unmarshaller unmarshaller = JAXBContext.newInstance(RootMenuDomain.class).createUnmarshaller();
				rootMenuDomain = (RootMenuDomain) unmarshaller.unmarshal(MetaMenuFactory.class.getResource(menuXMLRootPath + "mainmenu.xml"));
				referenceRootMenuDomain = new SoftReference<RootMenuDomain>(rootMenuDomain);

//				final Log LOGGER = LogFactory.getLog(MetaMenuFactory.class);
//				if (LOGGER.isDebugEnabled()) {
//					LOGGER.debug("Menu-Metamodel is successfully loaded. ");
//				}

			} catch (JAXBException e) {
				throw new RuntimeException(e);
			}
		}
		return rootMenuDomain;
	}
}
