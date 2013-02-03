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
package de.forsthaus.policy;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import de.daibutsu.token.Md5Token;

/**
 * @author bbruhns
 * 
 */
public class PasswordEncoderImpl implements PasswordEncoder, Serializable {

	private static final long serialVersionUID = 1L;

	public PasswordEncoderImpl() {
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object token) {
		/*
		 * encPass -> Passwort aus UserDetais rawPass -> Passwort aus dem
		 * Eingabefels token -> Ist null oder ein Md5Token und kommt aus
		 * User.getToken()
		 */

		if (encPass == null) {
			/*
			 * Passwort auf der DB ist null Da null nicht im Webformular
			 * eingegeben werden kann, wird ein false geliefert. (vergleich ist
			 * nicht notwendig!)
			 */
			return false;
		}

		if (token == null) {
			/*
			 * Kein Token Verwenden. Einfach nur die Passwörter vergleichen.
			 */
			return encPass.equals(rawPass);
		}

		/*
		 * Ab hier wird ein Token verwendet! Wenn man in Debugger wartet, kann
		 * der eingegebene Token Ablaufen!
		 */

		if (rawPass.length() <= Md5Token.TOKEN_LENGTH) {
			/*
			 * Passwort ist zu klein, um einen Token zu enthalten!
			 */
			return false;
		}

		/*
		 * Passwort ohne Token ermitteln
		 */
		String newRawPass = StringUtils.left(rawPass, rawPass.length() - Md5Token.TOKEN_LENGTH);
		if (encPass.equals(newRawPass)) {
			/*
			 * eingegebenes Token ermitteln
			 */
			String rawToken = StringUtils.right(rawPass, Md5Token.TOKEN_LENGTH);
			Md5Token md5Token = (Md5Token) token;
			/*
			 * Token prüfen
			 */
			return md5Token.isEqualsToken(rawToken);
		}
		return false;
	}

	@Override
	public String encodePassword(String rawPass, Object token) throws DataAccessException {
		throw new RuntimeException("Methode wird nicht unterstützt!");
	}
}
