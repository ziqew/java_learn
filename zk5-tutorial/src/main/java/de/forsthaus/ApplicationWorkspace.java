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

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zul.Timer;

/**
 * Workspace for the application. One workspace per app instance. <br>
 * Stored several properties for the application. <br>
 * <br>
 * 
 * @author Stephan Gerth
 */
public class ApplicationWorkspace implements Serializable {

	private static ApplicationWorkspace instance = new ApplicationWorkspace();

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(ApplicationWorkspace.class);

	private static String appName = "OT_Base";
	private static String serverIp = "1.2.3.4.5.6";

	/* Application real http-path on server. Filled on app startup. */
	/* @see org.opentruuls.InitApplication */
	private String applicationRealPath = "";

	private Timer timer;

	/**
	 * Default Constructor, cannot invoked from outer this class. <br>
	 */
	private ApplicationWorkspace() {
	}

	public static ApplicationWorkspace getInstance() {
		return instance;
	}

	public String getApplicationRealPath() {
		return applicationRealPath;
	}

	public void setApplicationRealPath(String applicationRealPath) {
		this.applicationRealPath = applicationRealPath;
	}

	public String getAppName() {
		return appName;
	}

	public String getServerip() {
		return serverIp;
	}

}
