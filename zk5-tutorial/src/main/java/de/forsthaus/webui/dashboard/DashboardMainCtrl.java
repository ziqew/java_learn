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
package de.forsthaus.webui.dashboard;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import de.forsthaus.webui.dashboard.module.DashboardApplicationInformationCtrl;
import de.forsthaus.webui.dashboard.module.DashboardApplicationLoginInformationCtrl;
import de.forsthaus.webui.dashboard.module.DashboardApplicationNewsListCtrl;
import de.forsthaus.webui.dashboard.module.DashboardGermanSoccerStatsCtrl;
import de.forsthaus.webui.dashboard.module.DashboardGoogleNewsCtrl;
import de.forsthaus.webui.dashboard.module.DashboardStockNewsCtrl;
import de.forsthaus.webui.dashboard.module.DashboardTableRecordsCounterCtrl;
import de.forsthaus.webui.dashboard.module.DashboardYoutubeVideoCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * EN: Controller for the Dashboard .<br>
 * DE: Controller fuer die System Uebersicht.<br>
 * <br>
 * zul-file: /WEB-INF/pages/dashboard.zul.<br>
 * <br>
 * 
 * @author Stephan Gerth
 */
public class DashboardMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient static final Logger logger = Logger.getLogger(DashboardMainCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowDashboard; // autowired
	protected Div divDashboardCenter; // autowired
	protected Div divDashboardEast; // autowired

	/**
	 * default constructor.<br>
	 */
	public DashboardMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * Set an 'alias' for this composer name in the zul file for access.<br>
		 * Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		self.setAttribute("controller", this, false);

		// Set the Dashboard unClosable
		try {
			Component cmp = (Component) windowDashboard.getParent().getParent().getParent();
			Tab menuTab = (Tab) cmp.getFellowIfAny("tab_menu_Item_Home", false);
			menuTab.setClosable(false);
		} catch (Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
		}

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
	public void onCreate$windowDashboard(Event event) throws Exception {

		/**
		 * CENTER area
		 */
		divDashboardCenter.appendChild(DashboardApplicationNewsListCtrl.show(250, true, 600000));

		Hbox hbox = new Hbox();
		hbox.setSclass("FDCenterNoBorder");
		hbox.setAlign("stretch");
		hbox.setPack("stretch");
		hbox.setWidth("100%");

		Cell cellLeft = new Cell();
		cellLeft.setId("cellLeft");
		// cellLeft.setWidth("100%");
		cellLeft.setStyle("padding: 0px;");
		cellLeft.setParent(hbox);

		Cell cellRight = new Cell();
		cellRight.setId("cellRight");
		cellRight.setWidth("280px");
		cellRight.setStyle("padding: 0px;");
		cellRight.setParent(hbox);

		cellLeft.appendChild(DashboardTableRecordsCounterCtrl.show(200, true, 600000));
		cellRight.appendChild(DashboardYoutubeVideoCtrl.show(198));

		// hbox.appendChild(DashboardTableRecordsCounterCtrl.show(200, true,
		// 600000));
		// hbox.appendChild(DashboardYoutubeVideoCtrl.show(198));

		divDashboardCenter.appendChild(hbox);
		divDashboardCenter.appendChild(DashboardApplicationInformationCtrl.show(200));
		divDashboardCenter.appendChild(DashboardApplicationLoginInformationCtrl.show(200));

		/**
		 * EAST area
		 */
		// divDashboardEast.appendChild(DashboardCalendarCtrl.show(146));
		// divDashboardEast.appendChild(DashboardGoogleTranslateCtrl.show(65));
		// divDashboardEast.appendChild(DashboardYoutubeVideoCtrl.show(198));
		// divDashboardEast.appendChild(DashboardBBCNewsCtrl.show(480, "no"));
		divDashboardEast.appendChild(DashboardGoogleNewsCtrl.show(400, "no"));
		divDashboardEast.appendChild(DashboardStockNewsCtrl.show(390, "no"));
		divDashboardEast.appendChild(DashboardGermanSoccerStatsCtrl.show(460, "no"));

	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

}
