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

/**
 * @author bbruhns
 * 
 */
public interface GuiLoginLoggingPolicService {

	/**
	 * Saves the login data for a succesfully login.<br>
	 * 
	 * @param userName
	 *            Username, who is succesfully logged in.
	 * @param userId
	 *            ID des Users.
	 * @param sessionId
	 *            ZK Session Id.
	 * @param clientAddress
	 *            Remote Ip from the client.
	 */
	void logAuthPass(String userName, long userId, String clientAddress, String sessionId);

	/**
	 * Saves the login data for a failed login.<br>
	 * 
	 * @param userName
	 *            Username, who is failed to login.
	 * @param sessionId
	 *            ZK Session Id.
	 * @param clientAddress
	 *            Remote Ip from the client.
	 */
	void logAuthFail(String userName, String clientAddress, String sessionId);

}
