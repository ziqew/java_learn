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
package de.forsthaus.common.menu;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.common.menu.dropdown.ZkossDropDownMenuFactory;
import de.forsthaus.common.menu.tree.ZkossTreeMenuFactory;
import de.forsthaus.webui.index.IndexCtrl;
import de.forsthaus.webui.util.WindowBaseCtrl;

/**
 * 
 * Main menu controller. <br>
 * <br>
 * Added the buttons for expanding/closing the menu tree. Calls the menu
 * factory.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 * 
 */
public class MainMenuCtrl extends WindowBaseCtrl implements Serializable {

	private static final long serialVersionUID = -909795057747345551L;
	private static final Logger logger = Logger.getLogger(MainMenuCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private Window mainMenuWindow; // autowire the IDSpace

	private Tree mainMenuTree;

	private static String bgColorInner = "white";

	// Controllers
	private IndexCtrl indexCtrl;

	public void onCreate$mainMenuWindow(Event event) throws Exception {

		doOnCreateCommon(this.mainMenuWindow, event); // do the autowire stuff

		/*
		 * Overhand this controller to the caller.
		 */
		if (args.containsKey("indexController")) {
			setIndexCtrl((IndexCtrl) args.get("indexController"));
			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getIndexCtrl().setMainMenuCtrl(this);
		}

		createMenu();
	}

	/**
	 * Creates the mainMenu. <br>
	 * 
	 * @throws InterruptedException
	 */
	private void createMenu() throws InterruptedException {

		Toolbarbutton toolbarbutton;

		final Panelchildren gb = (Panelchildren) getMainMenuWindow().getFellowIfAny("panelChildren_menu");

		Space space = new Space();
		space.setHeight("5px");
		space.setParent(gb);

		// Hbox for the expand/collapse buttons
		final Hbox hbox = new Hbox();
		// hbox.setStyle("backgound-color: " + bgColorInner);
		hbox.setParent(gb);

		// ToolbarButton for expanding the menutree
		toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuExpandAll");
		toolbarbutton.setImage("/images/icons/folder_open_16x16.gif");
		toolbarbutton.setTooltiptext(Labels.getLabel("btnFolderExpand.tooltiptext"));
		toolbarbutton.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuExpandAll(event);
			}
		});
		toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuCollapseAll");
		toolbarbutton.setImage("/images/icons/folder_closed2_16x16.gif");
		toolbarbutton.setTooltiptext(Labels.getLabel("btnFolderCollapse.tooltiptext"));
		toolbarbutton.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuCollapseAll(event);
			}
		});

		toolbarbutton = new Toolbarbutton();
		hbox.appendChild(toolbarbutton);
		toolbarbutton.setId("btnMainMenuChange");

		toolbarbutton.setImage("/images/icons/menu_16x16.gif");
		// toolbarbutton.setImage("/images/icons/combobox_16x16.gif");
		toolbarbutton.setTooltiptext(Labels.getLabel("btnMainMenuChange.tooltiptext"));
		toolbarbutton.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				onClick$btnMainMenuChange(event);
			}
		});

		// toolbarbutton = new Toolbarbutton();
		// hbox.appendChild(toolbarbutton);
		// toolbarbutton.setId("btnMainMenuDocumentation");
		//
		// toolbarbutton.setImage("/images/icons/icon-pdf_16x16.png");
		// toolbarbutton.setTooltiptext(Labels.getLabel("btnMainMenuDocumentation.tooltiptext"));
		// toolbarbutton.addEventListener("onClick", new EventListener() {
		// @Override
		// public void onEvent(Event event) throws Exception {
		// //
		// Executions.getCurrent().sendRedirect("the_url_generated_of_report","_blank");
		// //
		// Executions.getCurrent().sendRedirect("http://sunet.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf",
		// // "_blank");
		//
		// final String url1 =
		// "http://sunet.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf";
		// Clients.evalJavaScript("window.open('" + url1
		// +
		// "','','top=100,left=200,height=600,width=800,scrollbars=1,resizable=1')");
		// }
		// });

		Separator separator = createSeparator(false);
		separator.setWidth("97%");
		separator.setStyle("background-color: " + bgColorInner);
		separator.setBar(false);
		separator.setParent(gb);

		separator = createSeparator(false);
		separator.setWidth("97%");
		separator.setBar(true);
		separator.setParent(gb);

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

		// the menuTree
		mainMenuTree = new Tree();
		// tree.setSizedByContent(true);
		// tree.setZclass("z-dottree");
		mainMenuTree.setStyle("overflow:auto; border: none;");
		mainMenuTree.setParent(gb);

		final Treechildren treechildren = new Treechildren();
		mainMenuTree.appendChild(treechildren);

		// generate the treeMenu from the menuXMLFile
		ZkossTreeMenuFactory.addMainMenu(treechildren);

		final Separator sep1 = new Separator();
		sep1.setWidth("97%");
		sep1.setBar(false);
		sep1.setParent(gb);

		/* as standard, call the dashboard page */
		showPage("/WEB-INF/pages/dashboard.zul", "menu_Item_Home", Labels.getLabel("menu_Item_Home"));
	}

	/**
	 * Creates a seperator. <br>
	 * 
	 * @param withBar
	 * <br>
	 *            true=with Bar <br>
	 *            false = without Bar <br>
	 * @return
	 */
	private static Separator createSeparator(boolean withBar) {

		final Separator sep = new Separator();
		sep.setStyle("backgound-color: " + bgColorInner);
		sep.setBar(withBar);

		return sep;
	}

	/**
	 * Inner class for the GuestBookListener.
	 */
	public final class GuestBookListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			showPage("/WEB-INF/pages/guestbook/guestBookList.zul", "Guestbook", null);
		}
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout. Checks if the tab is opened before. If yes than it selects
	 * this tab, if not than create it new.<br>
	 * 
	 * @param zulFilePathName
	 *            The ZulFile Name with path.
	 * @param tabID
	 *            The tab name ID.
	 * @param differentLabel
	 *            needed for the home/start/dashboard page
	 * @throws InterruptedException
	 */
	private void showPage(String zulFilePathName, String tabID, String tabLabel) throws InterruptedException {

		try {
			// TODO get the parameter for working with tabs from the application
			// params
			final int workWithTabs = 1;

			if (workWithTabs == 1) {

				/* get an instance of the borderlayout defined in the zul-file */
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				/* get an instance of the searched CENTER layout area */
				final Center center = bl.getCenter();
				// get the tabs component
				final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter");

				/**
				 * Check if the tab is already opened than select them and<br>
				 * go out of here. If not than create them.<br>
				 */

				Tab checkTab = null;
				try {
					// checkTab = (Tab) tabs.getFellow(tabName);
					checkTab = (Tab) tabs.getFellow("tab_" + tabID.trim());
					checkTab.setSelected(true);
				} catch (final ComponentNotFoundException ex) {
					// Ignore if can not get tab.
				}

				if (checkTab == null) {

					final Tab tab = new Tab();
					tab.setId("tab_" + tabID.trim());

					if (tabLabel != null) {
						tab.setLabel(tabLabel.trim());
					} else {
						tab.setLabel(tabLabel.trim());
					}
					tab.setClosable(true);
					tab.setParent(tabs);

					final Tabpanels tabpanels = (Tabpanels) center.getFellow("divCenter").getFellow("tabBoxIndexCenter").getFellow("tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
					final Tabpanel tabpanel = new Tabpanel();
					tabpanel.setHeight("100%");
					tabpanel.setStyle("padding: 0px;");
					tabpanel.setParent(tabpanels);

					/*
					 * create the page and put it in the tabs area
					 */
					Executions.createComponents(zulFilePathName, tabpanel, null);
					tab.setSelected(true);
				}
			} else {
				/* get an instance of the borderlayout defined in the zul-file */
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				/* get an instance of the searched CENTER layout area */
				final Center center = bl.getCenter();
				/* clear old center child comps */
				center.getChildren().clear();
				/*
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

	/**
	 * Expand the menuTree.<br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnMainMenuExpandAll(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		doCollapseExpandAll(getMainMenuWindow(), true);
	}

	/**
	 * Collapse the menuTree.<br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnMainMenuCollapseAll(Event event) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("--> " + event.toString());
		}
		doCollapseExpandAll(getMainMenuWindow(), false);
	}

	/**
	 * Changed the TreeMenu to a BarMenu depending on the same 'mainmenu.xml'
	 * file.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnMainMenuChange(Event event) throws Exception {

		// set the MenuOffset for correct calculating content height.
		final Checkbox cb = (Checkbox) Path.getComponent("/outerIndexWindow/CBtreeMenu");
		cb.setChecked(false);
		UserWorkspace.getInstance().setTreeMenu(false);

		// get an instance of the borderlayout defined in the index.zul-file
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		// get an instance of the searched west layout area
		final West west = bl.getWest();
		west.setVisible(false);

		final North north = bl.getNorth();
		north.setHeight(north.getHeight() + "px");

		final Div div = (Div) north.getFellow("divDropDownMenu");

		final Menubar menuBar = (Menubar) div.getFellow("mainMenuBar");
		menuBar.setVisible(true);

		// generate the menu from the menuXMLFile
		ZkossDropDownMenuFactory.addDropDownMenu(menuBar);

		final Menuitem changeToTreeMenu = new Menuitem();
		changeToTreeMenu.setLabel(Labels.getLabel("menu_Item_backToTree"));
		changeToTreeMenu.setImage("/images/icons/refresh2_yellow_16x16.gif");
		changeToTreeMenu.setParent(menuBar);
		changeToTreeMenu.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				// get an instance of the borderlayout defined in the
				// index.zul-file
				final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
				// get an instance of the searched west layout area
				final West west = bl.getWest();
				west.setVisible(true);

				final North north = bl.getNorth();

				final Div div = (Div) north.getFellow("divDropDownMenu");

				final Menubar menuBar = (Menubar) div.getFellow("mainMenuBar");
				menuBar.getChildren().clear();
				menuBar.setVisible(false);

				// set the MenuOffset for correct calculating content height.
				final Checkbox cb = (Checkbox) Path.getComponent("/outerIndexWindow/CBtreeMenu");
				cb.setChecked(true);
				UserWorkspace.getInstance().setTreeMenu(true);

				// Refresh the whole page for setting correct sizes of the
				// components
				final Window win = (Window) Path.getComponent("/outerIndexWindow");
				win.invalidate();

			}
		});

		// Refresh the whole page for setting correct sizes of the
		// components
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		win.invalidate();
	}

	private void doCollapseExpandAll(Component component, boolean aufklappen) {
		if (component instanceof Treeitem) {
			final Treeitem treeitem = (Treeitem) component;
			treeitem.setOpen(aufklappen);
		}
		final Collection<?> com = component.getChildren();
		if (com != null) {
			for (final Iterator<?> iterator = com.iterator(); iterator.hasNext();) {
				doCollapseExpandAll((Component) iterator.next(), aufklappen);

			}
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public Window getMainMenuWindow() {
		return this.mainMenuWindow;
	}

	public void setMainMenuWindow(Window mainMenuWindow) {
		this.mainMenuWindow = mainMenuWindow;
	}

	public void setIndexCtrl(IndexCtrl indexCtrl) {
		this.indexCtrl = indexCtrl;
	}

	public IndexCtrl getIndexCtrl() {
		return indexCtrl;
	}

}
