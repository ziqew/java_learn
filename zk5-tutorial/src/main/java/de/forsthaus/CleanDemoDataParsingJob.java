/**
 * Copyright (C) 2010 - 2012 Forsthaus IT Consulting GbR.
 * 
 * This file is part of openTruuls™. http://www.opentruuls.org/
 *
 * openTruuls™ community edition is free software: 
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 * If you need a commercial license please write us under info@opentruuls.org
 */
package de.forsthaus;

import org.springframework.context.ApplicationContext;

import de.forsthaus.backend.nonwebrequestdbservice.NonWebRequestDBAction;
import de.forsthaus.util.ApplicationContextProvider;

/**
 * EN: Class for calling database cleaning jobs.<br>
 * DE: Klasse die Aufraeumarbeiten in der Datenbank aufruft.<br>
 * <br>
 * 
 * @author Stephan Gerth
 */
public class CleanDemoDataParsingJob implements Runnable {

	@Override
	public void run() {

		try {
			doResetAdminUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the admin user name and password because some guys are changing
	 * these names and forgot to reset to the original values. :-(
	 */
	private void doResetAdminUser() {
		System.out.println("###### ==> Run Job for resetting the demo data.");

		ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();

		if (ctx != null && ctx.containsBean("userService")) {
			NonWebRequestDBAction nonWebRequestDBAction = (NonWebRequestDBAction) ctx.getBean("nonWebRequestDBAction");
			if (nonWebRequestDBAction != null) {
				System.out.println("###### ==> Reset admin name/password.");
				nonWebRequestDBAction.resetAdminPassword();
			}
		}
	}

}
