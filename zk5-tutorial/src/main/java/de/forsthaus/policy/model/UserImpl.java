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
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import de.daibutsu.token.Md5Token;
import de.forsthaus.backend.model.SecUser;

/**
 * The user implementation of spring-security framework user class. <br>
 * Extends for our simulation of a one-time-password .<br>
 * <br>
 * Extended for usrToken, our simulation of a one-time-password .<br>
 * Extended for a usrId, userID (type long). <br>
 * Extended for SecUser. <br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class UserImpl extends User implements Serializable, de.forsthaus.policy.User {

	private static final long serialVersionUID = 7682359879431168931L;

	// Token for En-/decrypting the users password
	final private String usrToken;

	// The user ID
	final private long userId;

	// The user object
	private SecUser secUser;

	/**
	 * Constructor
	 * 
	 * @param username
	 * @param password
	 * @param enabled
	 * @param accountNonExpired
	 * @param credentialsNonExpired
	 * @param accountNonLocked
	 * @param authorities
	 * @throws IllegalArgumentException
	 */
	public UserImpl(SecUser user, Collection<GrantedAuthority> grantedAuthorities) throws IllegalArgumentException {

		super(user.getUsrLoginname(), user.getUsrPassword(), user.isUsrEnabled(), user.isUsrAccountnonexpired(), user.isUsrCredentialsnonexpired(), user.isUsrAccountnonlocked(), grantedAuthorities);

		this.usrToken = user.getUsrToken();
		this.userId = user.getId();
		this.secUser = user;
	}

	/*
	 * gets the token for our simulating of a one-time-password tokenizer.
	 */
	public Md5Token getToken() {
		if (StringUtils.isBlank(getUsrToken())) {
			return null;
		}
		return new Md5Token(getUsrToken());
	}

	private String getUsrToken() {
		return this.usrToken;
	}

	@Override
	public long getUserId() {
		return this.userId;
	}

	@Override
	public SecUser getSecUser() {
		return this.secUser;
	}

}
