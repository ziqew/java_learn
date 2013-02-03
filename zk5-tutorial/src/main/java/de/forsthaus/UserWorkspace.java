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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Office;

/**
 * Workspace for the user. One workspace per userSession. <br>
 * <br>
 * Every logged in user have his own workspace. <br>
 * Here are stored several properties for the user. <br>
 * <br>
 * 1. Access the rights that the user have. <br>
 * 2. The office for that the user are logged in. <br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 * 
 */
public class UserWorkspace implements Serializable, DisposableBean {

	private static final long serialVersionUID = -3936210543827830197L;
	private final static Logger logger = Logger.getLogger(UserWorkspace.class);

	static private Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * Get a logged-in users WorkSpace which holds all necessary vars. <br>
	 * 
	 * @return the users WorkSpace
	 * @deprecated Sollte gegen Spring getauscht werden also Konfiguriert und
	 *             nicht über diese Methode!
	 */
	@Deprecated
	public static UserWorkspace getInstance() {
		return SpringUtil.getBean("userWorkspace", UserWorkspace.class);
	}

	private String userLanguage;
	private String browserType;

	/**
	 * Indicates that as mainMenu the TreeMenu is used, otherwise BarMenu.
	 * 
	 * true = init.
	 */
	private boolean treeMenu = true;

	/**
	 * difference in the height between TreeMenu and BarMenu.
	 */
	private final int menuOffset = 32;

	/**
	 * Not used yet.
	 */
	private Office office;

	private Set<String> grantedAuthoritySet = null;

	/**
	 * Default Constructor
	 */
	public UserWorkspace() {
		if (logger.isDebugEnabled()) {
			logger.debug("create new UserWorkspace [" + this + "]");
		}

		// speed up the ModalDialogs while disabling the animation
		Window.setDefaultActionOnShow("");
	}

	/**
	 * Logout with the spring-security logout action-URL.<br>
	 * Therefore we make a sendRedirect() to the logout uri we <br>
	 * have configured in the spring-config.br>
	 */
	public void doLogout() {
		destroy();

		/* ++++++ Kills the Http session ++++++ */
		// HttpSession s = (HttpSession)
		// Sessions.getCurrent().getNativeSession();
		// s.invalidate();
		/* ++++++ Kills the zk session +++++ */
		// Sessions.getCurrent().invalidate();

		Executions.sendRedirect("/j_spring_logout");

	}

	/**
	 * Copied the grantedAuthorities to a Set of strings <br>
	 * for a faster searching in it.
	 * 
	 * @return String set of GrantedAuthorities (rightNames)
	 */
	private Set<String> getGrantedAuthoritySet() {

		if (this.grantedAuthoritySet == null) {

			final Collection<GrantedAuthority> list = (Collection<GrantedAuthority>)getAuthentication().getAuthorities();
			this.grantedAuthoritySet = new HashSet<String>(list.size());

			for (final GrantedAuthority grantedAuthority : list) {
				this.grantedAuthoritySet.add(grantedAuthority.getAuthority());
			}
		}
		return this.grantedAuthoritySet;
	}

	/**
	 * Checks if a right is in the <b>granted rights</b> that the logged in user
	 * have. <br>
	 * 
	 * @param rightName
	 * @return true, if the right is in the granted user rights.<br>
	 *         false, if the right is not granted to the user.<br>
	 */
	public boolean isAllowed(String rightName) {
		return getGrantedAuthoritySet().contains(rightName);
	}

	public Properties getUserLanguageProperty() {

		// // TODO only for testing. we must get the language from
		// // the users table filed
		// userLanguageProperty =
		// ApplicationWorkspace.getInstance().getPropEnglish();
		// userLanguageProperty =
		// ApplicationWorkspace.getInstance().getPropGerman();
		//
		// return userLanguageProperty;
		return null;
	}

	@Override
	public void destroy() {
		this.grantedAuthoritySet = null;
		SecurityContextHolder.clearContext();

		if (logger.isDebugEnabled()) {
			logger.debug("destroy Workspace [" + this + "]");
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setOffice(Office office) {
		this.office = office;
	}

	public Office getOffice() {
		return this.office;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public String getUserLanguage() {
		return this.userLanguage;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public String getBrowserType() {
		return this.browserType;
	}

	public int getMenuOffset() {

		int result = 0;

		if (isTreeMenu())
			result = 0;
		else
			result = menuOffset;

		return result;
	}

	public void setTreeMenu(boolean treeMenu) {
		this.treeMenu = treeMenu;
	}

	public boolean isTreeMenu() {
		return treeMenu;
	}

}
