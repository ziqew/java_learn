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
package de.forsthaus.webui.spreadsheet;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.zkoss.calendar.Calendars;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Main controller for the calendar module.<br>
 * <br>
 * zul-file: /WEB-INF/pages/calendar/calendar.zul.<br>
 * <br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class SpreadsheetMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SpreadsheetMainCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowSpreadsheet; // autowired

	protected Borderlayout borderLayout_Spreadsheet; // autowired
	// protected Div divCenter; // autowired
	protected Calendars cal; // autowired

	protected Button btn_Show1Day; // autowired
	protected Button btn_Show5Days; // autowired
	protected Button btn_ShowWeek; // autowired
	protected Button btn_Show2Weeks; // autowired
	protected Button btn_ShowMonth; // autowired

	private String btnOriginColor = "color: black; font-weight: normal;";
	private String btnPressedColor = "color: red; font-weight: bold;";

	// ServiceDAOs / Domain Classes

	/**
	 * default constructor.<br>
	 */
	public SpreadsheetMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$windowSpreadsheet(Event event) throws Exception {
		doFitSize();
	}

	/**
	 * When the "help" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		doHelp(event);
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException, ParseException {
		doFitSize();
	}

	/**
	 * when the "print calendar" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_PrintSpreadsheet(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "resize to full screen" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnFullScreen(Event event) throws InterruptedException {
		doViewInFullScreen(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Changes the view for full width screen mode by collapsing the west
	 * borderlayout are, means the menu.
	 * 
	 * @param event
	 */
	public void doViewInFullScreen(Event event) {

		final Borderlayout bl = ((Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain"));
		final West west = bl.getWest();

		if (west != null) {
			try {
				if (west.isOpen()) {
					west.setOpen(false);
				} else
					west.setOpen(true);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens the help screen for the current module.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doHelp(Event event) throws InterruptedException {

		ZksampleMessageUtils.doShowNotImplementedMessage();

		// we stop the propagation of the event, because zk will call ALL events
		// with the same name in the namespace and 'btnHelp' is a standard
		// button in this application and can often appears.
		// Events.getRealOrigin((ForwardEvent) event).stopPropagation();
		event.stopPropagation();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Recalculates the container size for this controller and resize them.
	 * 
	 * Calculate how many rows have been place in the listbox. Get the
	 * currentDesktopHeight from a hidden Intbox from the index.zul that are
	 * filled by onClientInfo() in the indexCtroller.
	 * 
	 * @throws ParseException
	 */
	public void doFitSize() throws ParseException {
		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 0;
		final int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		final int maxListBoxHeight = height - specialSize - 119;
		// setCountRows((int) Math.round((maxListBoxHeight) / 17.7));
		this.borderLayout_Spreadsheet.setHeight(String.valueOf(maxListBoxHeight) + "px");

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

}
