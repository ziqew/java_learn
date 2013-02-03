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
package de.forsthaus.webui.logging.loginlog;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/sec_loginlog/secLoginLogMain.zul file.<br>
 * <br>
 * This class creates the tabs + panels for the list and statistic panel.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 12/19/2009: sge Splitted the controller for each panel. <br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class SecLoginlogMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7659364091196012899L;
	private static final Logger logger = Logger.getLogger(SecLoginlogListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window secLoginlogMainWindow; // autowired
	protected Panel panel_SecLoginlogList; // autowired

	protected Tab tabLoginList; // autowired
	protected Tabpanel tabPanelLoginList; // autowired
	protected Tabpanel tabPanelLoginStatistic; // autowired

	// ServiceDAOs / Domain Classes
	private transient LoginLoggingService loginLoggingService;

	/**
	 * default constructor.<br>
	 */
	public SecLoginlogMainCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$secLoginlogMainWindow(Event event) throws Exception {
		int panelHeight = 25;
		// TODO put the logic for working with panel in the ApplicationWorkspace
		final boolean withPanel = false;
		if (withPanel == false) {
			this.panel_SecLoginlogList.setVisible(false);
		} else {
			this.panel_SecLoginlogList.setVisible(true);
			panelHeight = 0;
		}

		/**
		 * We select the list tab and create the components from the zul-file.
		 */
		this.tabLoginList.setSelected(true);

		final Tabpanel listTab = (Tabpanel) Path
				.getComponent("/outerIndexWindow/secLoginlogMainWindow/tabPanelLoginList");
		listTab.getChildren().clear();

		final Panel panel = new Panel();
		final Panelchildren pChildren = new Panelchildren();
		panel.appendChild(pChildren);
		listTab.appendChild(panel);

		// call the zul-file and put it in the panelChildren on the tab.
		Executions.createComponents("/WEB-INF/pages/sec_loginlog/secLoginLogList.zul", pChildren, null);

	}

	/**
	 * When the tab 'list' is selected.<br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabLoginList(Event event) throws IOException {
		logger.debug(event.toString());

		final Tabpanel listTab = (Tabpanel) Path
				.getComponent("/outerIndexWindow/secLoginlogMainWindow/tabPanelLoginList");
		listTab.getChildren().clear();

		final Panel panel = new Panel();
		final Panelchildren pChildren = new Panelchildren();

		panel.appendChild(pChildren);
		listTab.appendChild(panel);

		// call the zul-file and put it on the tab.
		Executions.createComponents("/WEB-INF/pages/sec_loginlog/secLoginLogList.zul", pChildren, null);

	}

	/**
	 * When the tab 'statistic' is selected.<br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabLoginStatistic(Event event) throws IOException {
		logger.debug(event.toString());

		// try {
		// ZksampleUtils.doShowOutOfOrderMessage();
		// tabLoginList.setSelected(true);
		// return;
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		final Tabpanel listTab = (Tabpanel) Path
				.getComponent("/outerIndexWindow/secLoginlogMainWindow/tabPanelLoginStatistic");
		listTab.getChildren().clear();

		final Panel panel = new Panel();
		final Panelchildren pChildren = new Panelchildren();

		panel.appendChild(pChildren);
		listTab.appendChild(panel);

		// call the zul-file and put it on the tab.
		Executions.createComponents("/WEB-INF/pages/sec_loginlog/secLoginLogStatistic.zul", pChildren, null);

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public LoginLoggingService getLoginLoggingService() {
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

}
