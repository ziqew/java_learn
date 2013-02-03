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
package de.forsthaus.gui.service;

import java.util.List;

import de.forsthaus.backend.model.SecLoginlog;

/**
 * Service methods that depends on logging user countries and updating
 * ipToCountry and geo data from web services.<br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public interface GuiLoginLoggingService {

	public void fillIp2CountryOnceForAppUpdate();

	public int updateIp2CountryFromLookUpHost(List<SecLoginlog> list);

	/**
	 * Main routine for updating the secLoginLog table with the geo data from a
	 * webservice.<br>
	 * So we have the records that we get commonly limited to 2000 we do the
	 * update with a paging from the table.<br>
	 */
	public int updateFromHostLookUpMain();

	public int importIP2CountryCSV();

	/**
	 * Not used ! Imported about 9.000.000 records !!! <br>
	 * This is not a good job for the user application. <br>
	 * If needed do such huge data things in special applications.<br>
	 * 
	 * @return
	 */
	public int importIP4CountryCSV();
}
