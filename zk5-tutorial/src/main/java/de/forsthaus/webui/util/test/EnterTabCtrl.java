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
package de.forsthaus.webui.util.test;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

/**
 * Controller for let the focus jump from textbox to textbox by press the ENTER
 * key.
 * 
 * zul-file: WEB-INF/pages/test/enterTab.zul
 * @see http
 *      ://www.zkoss.org/forum/listComment/20977-Enter-next-Tabindex-component
 * @author Stephan Gerth
 * 
 */
public class EnterTabCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;

	private Textbox textboxa; // autowired
	private Textbox textboxb; // autowired
	private Textbox textboxc; // autowired

	/**
	 * Default constructor.
	 */
	public EnterTabCtrl() {
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

	}

	public void onOKTextbox(Event event) {

		if (((Textbox) ((ForwardEvent) event).getOrigin().getTarget()).getId().equalsIgnoreCase("textboxa")) {
			textboxb.focus();
		} else if (((Textbox) ((ForwardEvent) event).getOrigin().getTarget()).getId().equalsIgnoreCase("textboxb")) {
			textboxc.focus();
		} else if (((Textbox) ((ForwardEvent) event).getOrigin().getTarget()).getId().equalsIgnoreCase("textboxc")) {
			textboxa.focus();
		}

	}
}
