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
package de.forsthaus.gui.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import de.forsthaus.backend.model.Ip2Country;
import de.forsthaus.backend.model.IpToCountry;
import de.forsthaus.backend.model.SecLoginlog;
import de.forsthaus.backend.service.CountryCodeService;
import de.forsthaus.backend.service.Ip2CountryService;
import de.forsthaus.backend.service.IpToCountryService;
import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.gui.service.GuiLoginLoggingPolicService;

/**
 * Implementation of the login tries. Failed and successfully logins are saved
 * in database.
 * 
 * @changes: 10/07/2010: sge added the users browserType.
 * @author bbruhns
 * @author Stephan Gerth
 */
public class GuiLoginLoggingPolicServiceImpl implements GuiLoginLoggingPolicService {

	private final static Logger logger = Logger.getLogger(GuiLoginLoggingPolicServiceImpl.class);
	private transient LoginLoggingService loginLoggingService;
	private transient CountryCodeService countryCodeService;
	private transient Ip2CountryService ip2CountryService;
	private transient IpToCountryService ipToCountryService;

	@Override
	public void logAuthFail(String userName, String clientAddress, String sessionId) {

		if (logger.isInfoEnabled()) {
			logger.info("Login failed for: " + userName + " Host:" + clientAddress + " SessionId: " + sessionId);
		}

		// Get the users BrowserType
		String browserType = "";

		// if (Executions.getCurrent().getUserAgent() != null) {
		// browserType = Executions.getCurrent().getUserAgent();
		// }

		final SecLoginlog log = this.loginLoggingService.saveLog(userName, clientAddress, sessionId, browserType, 0);
		/** For testing on a local tomcat */
		// log.setLglIp("95.111.227.104");
		saveSimpleIPDataFromTable(log);
		/**
		 * For the complete geo-data use this with care about the case of a
		 * service down
		 */
		// saveIPDataFromLookUpHost(log);
	}

	@Override
	public void logAuthPass(String userName, long userId, String clientAddress, String sessionId) {

		if (logger.isDebugEnabled()) {
			logger.debug("Login ok for: " + userName + " -> UserID: " + userId + " Host:" + clientAddress + " SessionId: " + sessionId);
		}

		// Get the users BrowserType
		String browserType = "";

		// if (Executions.getCurrent().getUserAgent() != null) {
		// browserType = Executions.getCurrent().getUserAgent();
		// }

		final SecLoginlog log = this.loginLoggingService.saveLog(userName, clientAddress, sessionId, browserType, 1);

		/** For testing on a local tomcat */
		// log.setLglIp("95.111.227.104");
		saveSimpleIPDataFromTable(log);
		/**
		 * For the complete geo-data use this with care about the case of a
		 * service down
		 */
		// saveCompleteIPDataFromLookUpHost(log);
	}

	/**
	 * Get the IP data from the local IpToCountry table. <br>
	 * This data can later be extended by a update routine against the HostIP
	 * web service for geo data.
	 * 
	 * @param log
	 */
	private void saveSimpleIPDataFromTable(SecLoginlog log) {

		// Get the Country
		// local Database for loacting the ip to a country
		IpToCountry ipToCountry = null;

		try {
			ipToCountry = getIpToCountryService().getIpToCountry(InetAddress.getByName(log.getLglIp()));

			if (ipToCountry != null) {

				// Log data for locating ip to country and geo-data
				final Ip2Country ip2c = getIp2CountryService().getNewIp2Country();
				ip2c.setI2cCity("");
				// ip2c.setSecLoginlog(log);
				ip2c.setCountryCode(getCountryCodeService().getCountryCodeByCode2(ipToCountry.getIpcCountryCode2()));

				getIp2CountryService().saveOrUpdate(ip2c);

				// update the LoginLog with a relation to Ip2Country
				log.setIp2Country(ip2c);
				this.loginLoggingService.update(log);
			}
		} catch (final UnknownHostException e) {
			logger.warn("Update fehlgeschlagen für Country in " + log.getClass().getSimpleName() + " mit ID: " + log.getId() + " [" + e + "]");
		}

	}

	// #########################################################################
	// ############################# Getter / Setter ###########################
	// #########################################################################

	public LoginLoggingService getLoginLoggingService() {
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

	public CountryCodeService getCountryCodeService() {
		return countryCodeService;
	}

	public void setCountryCodeService(CountryCodeService countryCodeService) {
		this.countryCodeService = countryCodeService;
	}

	public Ip2CountryService getIp2CountryService() {
		return this.ip2CountryService;
	}

	public void setIp2CountryService(Ip2CountryService ip2CountryService) {
		this.ip2CountryService = ip2CountryService;
	}

	public IpToCountryService getIpToCountryService() {
		return this.ipToCountryService;
	}

	public void setIpToCountryService(IpToCountryService ipToCountryService) {
		this.ipToCountryService = ipToCountryService;
	}
}
