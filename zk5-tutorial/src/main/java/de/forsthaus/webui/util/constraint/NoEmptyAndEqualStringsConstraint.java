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
package de.forsthaus.webui.util.constraint;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

/**
 * Constraint for comparing the value from a textbox with a string.<br>
 * Throws an error message if not equals or is empty. Used in the userDialog for
 * checking that the reTyped password is same as first written password.<br>
 * 
 * <pre>
 * call from java: 
 * usrPassword.setConstraint("NO EMPTY");
 * usrPasswordRetype.setConstraint(new NoEmptyAndEqualStringsConstraint(txtbox_usrPassword));
 * </pre>
 * 
 * <pre>
 * declaration in zuml: 
 * < zscript >	
 * packageName NoEmptyAndEqualStringsConstraint cc = new packageName.NoEmptyAndEqualStringsConstraint(  txtb_User_Password  ); <==
 * < / zscript >
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class NoEmptyAndEqualStringsConstraint implements Constraint, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private final Component compareComponent;

	public NoEmptyAndEqualStringsConstraint(Component compareComponent) {
		super();
		this.compareComponent = compareComponent;
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {

		if (comp instanceof Textbox) {

			final String enteredValue = (String) value;

			// Skip, if disabled
			if (((Textbox) comp).isDisabled()) {
				return;
			}

			if (compareComponent instanceof Textbox) {

				if (StringUtils.isEmpty(enteredValue)) {
					throw new WrongValueException(comp, Labels.getLabel("message.Error.CannotBeEmpty"));
				}

				final String comparedValue = ((Textbox) compareComponent).getValue();
				if (!enteredValue.equals(comparedValue)) {
					throw new WrongValueException(comp, Labels.getLabel("message.Error.RetypedPasswordMustBeSame"));
				}
			}
		}
	}
}
