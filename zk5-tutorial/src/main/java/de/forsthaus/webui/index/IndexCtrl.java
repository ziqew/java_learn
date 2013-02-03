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
package de.forsthaus.webui.index;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.data.time.Day;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Timer;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import de.forsthaus.ApplicationMessageQueue;
import de.forsthaus.backend.model.Office;
import de.forsthaus.backend.service.OfficeService;
import de.forsthaus.common.menu.MainMenuCtrl;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;
import fi.jawsy.jawwa.zk.gritter.Gritter;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/index.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class IndexCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3407055074703929527L;
	private final static Logger logger = Logger.getLogger(IndexCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Menubar mainMenuBar; // autowired
	protected Label label_AppName; // autowired

	protected Intbox currentDesktopHeight; // autowired
	protected Intbox currentDesktopWidth; // autowired
	protected Checkbox CBtreeMenu; // autowired

	protected Tabs tabsIndexCenter; // autowired
	protected Timer timer;

	protected Checkbox chkbNotifications; // autowired

	private transient OfficeService officeService;

	private final int centerAreaHeightOffset = 50;

	private final String appName = "Zksample2";
	private final String appVersion = " v5_0_1112 | ";
	/**
	 * public Day(int day, int month, int year)Constructs a new one day time
	 * period.
	 * 
	 * Parameters:<br>
	 * day - the day-of-the-month (1 - 31).<br>
	 * month - the month (1 to 12).<br>
	 * year - the year (1900 <= year <= 9999).<br>
	 */
	private final Date buildDate = new Day(30, 11, 2012).getStart();

	// Controllers
	private MainMenuCtrl mainMenuCtrl;

	public IndexCtrl() {
		super();
	}

	public void onCreate$outerIndexWindow(Event event) throws Exception {
		this.mainMenuBar.setVisible(false);

		createMainTreeMenu(event);

		doDemoMode();

		final String zkVersion = doGetZkVersion();
		final String _version = this.appName + appVersion + ZksampleDateFormat.getDateFormater().format(buildDate);

		final String userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		final String version = zkVersion + " | " + _version;
		final String tenantId = "4711";
		final String officeID = "39";
		final String tableSchemaName = "public";

		EventQueues.lookup("userNameEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeUser", null, userName));

		EventQueues.lookup("tenantIdEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeTenant", null, tenantId));

		EventQueues.lookup("officeIdEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeOfficeId", null, officeID));

		EventQueues.lookup("appVersionEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeAppVersion", null, version));

		EventQueues.lookup("tableSchemaEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeTableSchema", null, tableSchemaName));

		/**
		 * Creates the ApplicationMessageQueue
		 */
		new ApplicationMessageQueue();

		doCreateDemoTimerForNotifications(event);
	}

	/**
	 * Gets the current desktop height and width and <br>
	 * stores it in two hidden intboxes components. <br>
	 * We use these values for calculating the count of rows in the listboxes. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClientInfo(ClientInfoEvent event) throws Exception {

		// logger.debug("Current desktop height :" + event.getDesktopHeight() +
		// "  - width  :" + event.getDesktopWidth());

		setCurrentDesktopHeight(event.getDesktopHeight() - this.centerAreaHeightOffset);
		setCurrentDesktopWidth(event.getDesktopWidth());
	}

	/**
	 * Creates a timer who creates periodically notifications from gekkio's
	 * ZK-Gritter implementation.
	 * 
	 * @see https://github.com/Gekkio/jawwa setting up in the pom.xml and
	 *      download and copied the 'lombok.jar' in WEB-INF/lib folder
	 * 
	 * @param event
	 */
	private void doCreateDemoTimerForNotifications(Event event) {

		if (timer == null) {
			this.timer = new Timer();

			// timer doesn't work without a page as parent
			Window outerIndexWindow = (Window) Path.getComponent("/outerIndexWindow");
			this.timer.setPage(outerIndexWindow.getPage());
			// 60000 = 60 sec.
			this.timer.setDelay(20000);
			this.timer.setRepeats(true);
			this.timer.addEventListener("onTimer", new EventListener() {

				@Override
				public void onEvent(Event event) throws Exception {
					// set new to 5 minutes
					timer.setDelay(300000);

					/**
					 * The Notifications. 1. Outcommented the ZK-Gritter direct
					 * call.<br>
					 * 2. Using the ZK EventQueues.<br>
					 */
					// String img = "/images/sge.jpg";
					// // Notification 1
					// Gritter.notification().withSticky(false).withText("Hello i'm a Notification").withTitle("Zksample2 Notification").show();
					// // Notification 1
					// Gritter.notification().withImage(img).withSticky(true).withText("Hello i'm the second notification.<br> You can close me by clicking the 'x' shown when mouse is over.")
					// .withTitle("Zksample2 Notification").show();

					Map<String, Object> map = new HashMap<String, Object>(0);

					// notification with title + message + selfClosing
					map.put("title", "Zksample2 Notification");
					map.put("message", "<br>Hello i'm a notification based on ZK-Gritter.<br><br>Many thanks to gekkio for writting the implementation.");
					map.put("autoClosing", false);
					EventQueue eq;
					eq = EventQueues.lookup("ApplicationEventQueue", EventQueues.APPLICATION, false);
					eq.publish(new Event("APPLICATION_NOTIFICATION", null, map));

					// notification with title + message + image
					map = new HashMap<String, Object>(0);
					map.put("title", "WOW !!!");
					map.put("message",
							"The notifications can hold a little image.<br><br> You can close me by clicking the 'x' in the left top corner shown when mouse is over.<br><br>Further you can stop/start the notifications with the checkbox <br>EN: 'stop notifications' <br>DE: 'Stoppe Benachrichtigungen'<br> on top right of the application.");
					map.put("autoClosing", true);
					map.put("image", "/images/sge.jpg");
					// EventQueues.lookup("ApplicationEventQueue",
					// EventQueues.APPLICATION, true).publish(new
					// Event("APPLICATION_NOTIFICATION", null, map));
					eq.publish(new Event("APPLICATION_NOTIFICATION", null, map));

				}
			});
			this.timer.setRunning(true);
		}
	}

	public void onCheck$chkbNotifications(Event event) {

		if (chkbNotifications.isChecked()) {
			try {
				Gritter.removeAll();
				timer.setRunning(false);
				timer = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			timer = null;
			doCreateDemoTimerForNotifications(event);
		}

	}

	/**
	 * Returns the used ZK framework version and build number.<br>
	 * 
	 * @return
	 */
	private String doGetZkVersion() {

		final String version = Executions.getCurrent().getDesktop().getWebApp().getVersion();
		final String build = Executions.getCurrent().getDesktop().getWebApp().getBuild();
		return "ZK " + version + " EE" + " / build : " + build;
	}

	/**
	 * Returns the spring-security managed logged in user.<br>
	 */
	public String doGetLoggedInUser() {

		final String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		return userName;
	}

	/**
	 * When the 'Logout' button is clicked.<br>
	 * 
	 * @throws IOException
	 */
	public void onClick$btnLogout() throws IOException {
		getUserWorkspace().doLogout(); // logout.
	}

	/**
	 * When the 'close all tabs' button is clicked.<br>
	 * 1. Get a list of all open 'Tab'.<br>
	 * 2. Iterate through it and close the Tab if it's not the Dashboard.<br>
	 * 3. The Dashboard itself is modified after creating to not closable.<br>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Stephan Gerth
	 */
	public void onClick$btnCloseAllTabs() throws IOException, InterruptedException {

		List<AbstractComponent> list = tabsIndexCenter.getChildren();

		try {
			while (!list.isEmpty()) {

				// get the sum of all Tab
				int i = list.size();

				// close all tabs, beginning with the last
				// because Dashboard is all times the first
				if (list.get(i - 1) instanceof Tab) {
					if (StringUtils.equals(((Tab) list.get(i - 1)).getId(), "tab_menu_Item_Home")) {
						break;
					} else {
						((Tab) list.get(i - 1)).onClose();
					}
				}
			}
		} catch (Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	/**
	 * For DEMO MODE we set the office number manually.<br>
	 */
	private void doDemoMode() {

		Office office = getOfficeService().getOfficeByID(Long.valueOf(1));
		getUserWorkspace().setOffice(office);

	}

	/**
	 * Creates the MainMenu as TreeMenu as default. <br>
	 */
	private void createMainTreeMenu(Event event) {

		// get an instance of the borderlayout defined in the index.zul-file
		Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");

		// get an instance of the searched west layout area
		West west = bl.getWest();
		west.setFlex(true);
		// clear the WEST child comps
		west.getChildren().clear();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("indexController", this);

		// create the components from the src/main/resources/mainmenu.xml and
		// put it in the WEST layout area
		// Overhand this controller self in a map
		Executions.createComponents("/WEB-INF/pages/mainTreeMenu.zul", west, map);

	}

	/**
	 * Shows the welcome page in the borderlayouts CENTER area.<br>
	 * 
	 * @throws InterruptedException
	 */
	public void showWelcomePage() throws InterruptedException {
		// get an instance of the borderlayout defined in the zul-file
		Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		// get an instance of the searched CENTER layout area
		Center center = bl.getCenter();
		// clear the center child comps
		center.getChildren().clear();
		// call the zul-file and put it in the center layout area
		Executions.createComponents("/WEB-INF/pages/welcome.zul", center, null);
	}

	/**
	 * When the 'My Settings' toolbarButton is clicked.<br>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void onClick$btnUserSettings() throws IOException, InterruptedException {
		Window win = null;
		Window parentWin = (Window) Path.getComponent("/outerIndexWindow");

		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/sec_user/mySettings.zul", parentWin, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ZksampleMessageUtils.showErrorMessage(e.getLocalizedMessage());
			win.detach();
		}
	}

	/**
	 * When the 'My Settings' toolbarButton is clicked.<br>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void onClick$btnTestMain() throws InterruptedException {
		Window win = null;
		Window parentWin = (Window) Path.getComponent("/outerIndexWindow");

		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/test/testMain.zul", parentWin, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ZksampleMessageUtils.showErrorMessage(e.getLocalizedMessage());
			win.detach();
		}
	}

	/**
	 * When the 'about' toolbarButton is clicked.<br>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void onClick$btnAbout(Event event) throws IOException, InterruptedException {

		/* get an instance of the borderlayout defined in the zul-file */
		Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		/* get an instance of the searched CENTER layout area */
		Center center = bl.getCenter();

		Executions.createComponents("/WEB-INF/pages/about/aboutZksample2.zul", null, null);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout. Checks if the tab is opened before. If yes than it selects
	 * this tab.
	 * 
	 * @param zulFilePathName
	 *            The ZulFile Name with path.
	 * @param tabName
	 *            The tab name.
	 * @throws InterruptedException
	 */
	private void showPage(String zulFilePathName, String tabName) throws InterruptedException {

		try {
			// TODO get the parameter for working with tabs from the application
			// params
			final int workWithTabs = 1;

			if (workWithTabs == 1) {

				/* get an instance of the borderlayout defined in the zul-file */
				Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				/* get an instance of the searched CENTER layout area */
				Center center = bl.getCenter();
				// get the tabs component
				Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter");

				/**
				 * Check if the tab is already opened than select them and<br>
				 * go out of here. If not than create them.<br>
				 */

				Tab checkTab = null;
				try {
					// checkTab = (Tab) tabs.getFellow(tabName);
					checkTab = (Tab) tabs.getFellow("tab_" + tabName.trim());
					checkTab.setSelected(true);
				} catch (final ComponentNotFoundException ex) {
					// Ignore if can not get tab.
				}

				if (checkTab == null) {

					Tab tab = new Tab();
					tab.setId("tab_" + tabName.trim());
					tab.setLabel(tabName.trim());
					tab.setClosable(true);

					tab.setParent(tabs);

					Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
					Tabpanel tabpanel = new Tabpanel();
					tabpanel.setHeight("100%");
					tabpanel.setStyle("padding: 0px;");
					tabpanel.setParent(tabpanels);

					/**
					 * Create the page and put it in the tabs area. If zul-file
					 * is not found, detach the created tab
					 */
					try {
						Executions.createComponents(zulFilePathName, tabpanel, null);
						tab.setSelected(true);
					} catch (final Exception e) {
						tab.detach();
					}

				}
			} else {
				/* get an instance of the borderlayout defined in the zul-file */
				Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				/* get an instance of the searched CENTER layout area */
				Center center = bl.getCenter();
				/* clear the center child comps */
				center.getChildren().clear();
				/**
				 * create the page and put it in the center layout area
				 */
				Executions.createComponents(zulFilePathName, center, null);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("--> calling zul-file: " + zulFilePathName);
			}
		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setOfficeService(OfficeService officeService) {
		this.officeService = officeService;
	}

	public OfficeService getOfficeService() {
		return this.officeService;
	}

	public void setCurrentDesktopHeight(int desktopHeight) {
		if (isTreeMenu() == true) {
			this.currentDesktopHeight.setValue(Integer.valueOf(desktopHeight));
		} else {
			this.currentDesktopHeight.setValue(Integer.valueOf(desktopHeight - 30));
		}
	}

	public int getCurrentDesktopHeight() {
		return this.currentDesktopHeight.getValue().intValue();
	}

	public void setCurrentDesktopWidth(int currentDesktopWidth) {
		this.currentDesktopWidth.setValue(Integer.valueOf(currentDesktopWidth));
	}

	public int getCurrentDesktopWidth() {
		return this.currentDesktopWidth.getValue().intValue();
	}

	public void setTreeMenu(boolean treeMenu) {
		this.CBtreeMenu.setChecked(treeMenu);
	}

	public boolean isTreeMenu() {
		return this.CBtreeMenu.isChecked();
	}

	public void setMainMenuCtrl(MainMenuCtrl mainMenuCtrl) {
		this.mainMenuCtrl = mainMenuCtrl;
	}

	public MainMenuCtrl getMainMenuCtrl() {
		return mainMenuCtrl;
	}

}
