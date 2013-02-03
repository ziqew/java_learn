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
package de.forsthaus.policy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.UserService;

/**
 * This class implements the spring-security UserDetailService Interface.<br>
 * It's been configured in the 'springSecurityContext.xml'.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 * @see de.forsthaus.policy
 */
public class PolicyManager implements UserDetailsService, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PolicyManager.class);

	// the service from which we get the data
	private transient UserService userService;

	@Override
	public UserDetails loadUserByUsername(String userId) {

		SecUser user = null;
		Collection<GrantedAuthority> grantedAuthorities = null;

		try {
			user = getUserByLoginname(userId);

			if (user == null) {
				throw new UsernameNotFoundException("Invalid User");
			}

			grantedAuthorities = getGrantedAuthority(user);

		} catch (final NumberFormatException e) {
			throw new DataRetrievalFailureException("Cannot loadUserByUsername userId:" + userId + " Exception:" + e.getMessage(), e);
		}

		// Create the UserDetails object for a specified user with
		// their grantedAuthorities List.
		final UserDetails userDetails = new UserImpl(user, grantedAuthorities);

		if (logger.isDebugEnabled()) {
			logger.debug("Rights for '" + user.getUsrLoginname() + "' (ID: " + user.getId() + ") evaluated. [" + this + "]");
		}

		return userDetails;
	}

	/**
	 * Gets the User object by his stored userName.<br>
	 * 
	 * @param userName
	 * @return
	 */
	public SecUser getUserByLoginname(final String userName) {
		return getUserService().getUserByLoginname(userName);
	}

	/**
	 * Fills the GrantedAuthorities List for a specified user.<br>
	 * 1. Gets a unique list of rights that a user have.<br>
	 * 2. Creates GrantedAuthority objects from all rights. <br>
	 * 3. Creates a GrantedAuthorities list from all GrantedAuthority objects.<br>
	 * 
	 * @param user
	 * @return
	 */
	private Collection<GrantedAuthority> getGrantedAuthority(SecUser user) {

		// get the list of rights for a specified user from db.
		final Collection<SecRight> rights = getUserService().getRightsByUser(user);

		// create the list for the spring grantedRights
		final ArrayList<GrantedAuthority> rightsGrantedAuthorities = new ArrayList<GrantedAuthority>(rights.size());

		// now create for all rights a GrantedAuthority entry
		// and fill the GrantedAuthority List with these authorities.
		for (final SecRight right : rights) {
			rightsGrantedAuthorities.add(new GrantedAuthorityImpl(right.getRigName()));
		}

		return rightsGrantedAuthorities;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
