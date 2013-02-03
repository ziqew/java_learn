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
package de.forsthaus.webui.guestbook.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.GuestBook;
import de.forsthaus.webui.util.ZksampleDateFormat;

/**
 * Item renderer for listitems in the listbox.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class GuestBookListtemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(GuestBookListtemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final GuestBook guestBook = (GuestBook) data;

		Listcell lc = null;

		lc = new Listcell(getFormattedDateTime(guestBook.getGubDate()));
		lc.setParent(item);
		lc = new Listcell(guestBook.getGubUsrname());
		lc.setParent(item);
		lc = new Listcell(guestBook.getGubSubject());
		lc.setParent(item);

		item.setValue(data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedGuestBookItem");
	}

	/**
	 * Format the date/time. <br>
	 * 
	 * @return String of date/time
	 */
	private String getFormattedDateTime(Date date) {
		if (date != null) {
			return ZksampleDateFormat.getDateTimeLongFormater().format(date);
		}
		return "";
	}
}
