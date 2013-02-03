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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * EN: Sets the real path of the started application in the
 * ApplicationWorkspace. it's been declared as a ServletListener in the web.xml
 * configuration file. <br>
 * Starts a Scheduler who resets periodically the admin name/password. <br>
 * DE: Setzt den RealPath der gestarteten Applikation in der
 * ApplicationWorkspace Klasse. Wird als ServletListener in der web.xml
 * Konfigurationsdatei deklariert.<br>
 * Startet periodisch einen Scheduler, der den admin name/passwort zuruecksetzt.
 * 
 * @see de.forsthaus.ApplicationWorkspace.java
 * @see de.forsthaus.ApplicationMessageQueue.java
 * @author Stephan Gerth
 */
// @WebListener --> no need for declaration in web.xml
public class InitApplicationWorkspace implements ServletContextListener {

	// Scheduler for periodically starts a db cleaning job
	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		// init the ApplicationWorkspace
		String s = sce.getServletContext().getRealPath("/");
		ApplicationWorkspace.getInstance().setApplicationRealPath(s);
		System.out.println("###### ==> InitApplicationWorkspace -> RealPath=" + s);

		// scheduler period
		int period = new Integer(1);

		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new CleanDemoDataParsingJob(), 0, period, TimeUnit.HOURS);
		System.out.println("###### ==> Scheduler for db cleaing jobs started every " + period + " hour.");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		scheduler.shutdownNow();
	}

}
