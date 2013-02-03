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
package de.forsthaus.webui.security.group;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.security.group.model.SecGroupListModelItemRenderer;
import de.forsthaus.webui.security.group.report.SecGroupSimpleDJReport;
import de.forsthaus.webui.util.GFCBaseListCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/sec_group/secGroupList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          03/09/2009: sge changed for allow repainting after resizing.<br>
 *          with the refresh button.<br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class SecGroupListCtrl extends GFCBaseListCtrl<SecGroup> implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private static final Logger logger = Logger.getLogger(SecGroupListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window secGroupListWindow; // autowired
	protected Panel panel_SecGroupList; // autowired

	// filter components
	protected Checkbox checkbox_SecGroupList_ShowAll; // autowired
	protected Textbox tb_SecGroup_GroupName; // autowired

	// listbox secGroupList
	protected Borderlayout borderLayout_secGroupsList; // autowired
	protected Paging paging_SecGroupList; // aurowired
	protected Listbox listBoxSecGroups; // aurowired
	protected Listheader listheader_SecGroupList_grpShortdescription; // autowired
	protected Listheader listheader_SecGroupList_grpLongdescription; // autowired

	// row count for listbox
	private int countRows;

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;

	/**
	 * default constructor.<br>
	 */
	public SecGroupListCtrl() {
		super();
	}

	public void onCreate$secGroupListWindow(Event event) throws Exception {
		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		int panelHeight = 25;
		// TODO put the logic for working with panel in the ApplicationWorkspace
		final boolean withPanel = false;
		if (withPanel == false) {
			panel_SecGroupList.setVisible(false);
		} else {
			panel_SecGroupList.setVisible(true);
			panelHeight = 0;
		}

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		height = height + panelHeight;
		final int maxListBoxHeight = height - 148;
		setCountRows(Math.round(maxListBoxHeight / 17));
		// System.out.println("MaxListBoxHeight : " + maxListBoxHeight);
		// System.out.println("==========> : " + getCountRows());

		borderLayout_secGroupsList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecGroupList_ShowAll.setChecked(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecGroupList_grpShortdescription.setSortAscending(new FieldComparator("grpShortdescription", true));
		listheader_SecGroupList_grpShortdescription.setSortDescending(new FieldComparator("grpShortdescription", false));
		listheader_SecGroupList_grpLongdescription.setSortAscending("");
		listheader_SecGroupList_grpLongdescription.setSortDescending("");

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecGroup> soSecGroup = new HibernateSearchObject<SecGroup>(SecGroup.class, getCountRows());
		soSecGroup.addSort("grpShortdescription", false);

		// set the paging params
		paging_SecGroupList.setPageSize(getCountRows());
		paging_SecGroupList.setDetailed(true);

		// Set the ListModel.
		getPagedListWrapper().init(soSecGroup, listBoxSecGroups, paging_SecGroupList);
		// set the itemRenderer
		listBoxSecGroups.setItemRenderer(new SecGroupListModelItemRenderer());

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Call the SecGroup dialog with the selected entry. <br>
	 * <br>
	 * This methode is forwarded from the listboxes item renderer. <br>
	 * see:
	 * de.forsthaus.webui.security.group.model.SecGroupListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDoubleClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecGroups.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			SecGroup aGroup = (SecGroup) item.getAttribute("data");

			showDetailView(aGroup);
		}
	}

	/**
	 * Call the SecGroup dialog with a new empty entry. <br>
	 */
	public void onClick$button_SecGroupList_NewGroup(Event event) throws Exception {

		// create a new customer object
		SecGroup aGroup = getSecurityService().getNewSecGroup();

		showDetailView(aGroup);

	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param aGroup
	 * @throws Exception
	 */
	private void showDetailView(SecGroup aGroup) throws Exception {

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("group", aGroup);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxSecGroups", listBoxSecGroups);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/sec_group/secGroupDialog.zul", secGroupListWindow, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			// Show a error box
			String msg = e.getMessage();
			String title = Labels.getLabel("message.Error");

			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "ERROR", true);

		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotAllowedInDemoModeMessage();
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {

		Events.postEvent("onCreate", secGroupListWindow, event);
		secGroupListWindow.invalidate();
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecGroupList_ShowAll(Event event) {

		// empty the text search boxes
		tb_SecGroup_GroupName.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecGroup> soSecGroup = new HibernateSearchObject<SecGroup>(SecGroup.class);
		soSecGroup.addSort("grpShortdescription", false);

		// Set the ListModel.
		getPagedListWrapper().init(soSecGroup, listBoxSecGroups, paging_SecGroupList);

	}

	/**
	 * when the "print group list" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecGroupList_PrintGroupList(Event event) throws InterruptedException {
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		new SecGroupSimpleDJReport(win);
	}

	/**
	 * Filter the group list with 'like GroupName'. <br>
	 */
	public void onClick$button_SecGroupList_SearchGroupName(Event event) throws Exception {

		// if not empty
		if (!tb_SecGroup_GroupName.getValue().isEmpty()) {
			checkbox_SecGroupList_ShowAll.setChecked(false); // unCheck

			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<SecGroup> soSecGroup = new HibernateSearchObject<SecGroup>(SecGroup.class);
			soSecGroup.addFilter(new Filter("grpShortdescription", "%" + tb_SecGroup_GroupName.getValue() + "%", Filter.OP_ILIKE));
			soSecGroup.addSort("grpShortdescription", false);

			// Set the ListModel.
			getPagedListWrapper().init(soSecGroup, listBoxSecGroups, paging_SecGroupList);
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public SecurityService getSecurityService() {
		return this.securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

}
