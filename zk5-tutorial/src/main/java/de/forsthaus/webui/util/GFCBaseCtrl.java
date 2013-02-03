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
package de.forsthaus.webui.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.security.access.annotation.Secured;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;

/**
 * Base controller for creating the controllers of the zul files with the spring
 * framework.
 * 
 * @changes 05/18/2010 sge cleaned up from old stuff.
 * 
 * @author bbruhns
 * @author sgerth
 */
abstract public class GFCBaseCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = -1171206258809472640L;

	protected transient Map<String, Object> args;

	/**
	 * Get the params map that are overhanded at creation time. <br>
	 * Reading the params that are binded to the createEvent.<br>
	 * 
	 * @param event
	 * @return params map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCreationArgsMap(Event event) {
		final CreateEvent ce = (CreateEvent) ((ForwardEvent) event).getOrigin();
		return ce.getArg();
	}

	@SuppressWarnings("unchecked")
	public void doOnCreateCommon(Window w, Event fe) throws Exception {
		final CreateEvent ce = (CreateEvent) ((ForwardEvent) fe).getOrigin();
		this.args = ce.getArg();
	}

	private transient UserWorkspace userWorkspace;

	/**
	 * Workaround! Do not use it otherwise!
	 */
	@Override
	public void onEvent(Event evt) throws Exception {
		final Object controller = getController();
		final Method mtd = ComponentsCtrl.getEventMethod(controller.getClass(), evt.getName());

		if (mtd != null) {
			isAllowed(mtd);
		}
		super.onEvent(evt);
	}

	/**
	 * With this method we get the @Secured Annotation for a method.<br>
	 * Captured the method call and check if it's allowed. <br>
	 * sample: @Secured({"rightName"}) <br>
	 * <pre>
	 * @Secured({ "button_BranchMain_btnNew" })
	 * public void onClick$btnNew(Event event) throws Exception {
	 *   [...]
	 * }
	 * </pre>
	 * 
	 * @param mtd
	 * @exception SecurityException
	 */
	private void isAllowed(Method mtd) {
		final Annotation[] annotations = mtd.getAnnotations();
		for (final Annotation annotation : annotations) {
			if (annotation instanceof Secured) {
				final Secured secured = (Secured) annotation;
				for (final String rightName : secured.value()) {
					if (!this.userWorkspace.isAllowed(rightName)) {
						throw new SecurityException("Call of this method is not allowed! Missing right: \n\n" + "needed RightName: " + rightName + "\n\n" + "Method: " + mtd);
					}
				}
				return;
			}
		}
	}

	final protected UserWorkspace getUserWorkspace() {
		return this.userWorkspace;
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}
}
