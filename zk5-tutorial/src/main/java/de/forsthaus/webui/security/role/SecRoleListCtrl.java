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
package de.forsthaus.webui.security.role;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
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
import de.forsthaus.backend.model.SecRole;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.security.role.model.SecRoleListModelItemRenderer;
import de.forsthaus.webui.security.role.report.SecRoleSimpleDJReport;
import de.forsthaus.webui.util.GFCBaseListCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/sec_role/secRoleList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class SecRoleListCtrl extends GFCBaseListCtrl<SecRole> implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private static final Logger logger = Logger.getLogger(SecRoleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window secRoleListWindow; // autowired
	protected Panel panel_SecRoleList; // autowired

	// filter components
	protected Checkbox checkbox_SecRoleList_ShowAll; // autowired
	protected Textbox tb_SecRole_RoleName; // autowired

	// Listbox SecRole
	protected Borderlayout borderLayout_secRolesList; // autowired
	protected Listbox listBoxSecRoles; // aurowired
	protected Paging paging_SecRoleList; // aurowired
	protected Listheader listheader_SecRoleList_rolShortdescription; // aurowired
	protected Listheader listheader_SecRoleList_rolLongdescription; // aurowired

	// row count for listbox
	private int countRows;

	private transient SecurityService securityService;

	/**
	 * default constructor.<br>
	 */
	public SecRoleListCtrl() {
		super();
	}

	public void onCreate$secRoleListWindow(Event event) throws Exception {
		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		int panelHeight = 50;
		// TODO put the logic for working with panel in the ApplicationWorkspace
		final boolean withPanel = false;
		if (withPanel == false) {
			panel_SecRoleList.setVisible(false);
		} else {
			panel_SecRoleList.setVisible(true);
			panelHeight = 0;
		}

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		height = height + panelHeight;
		final int maxListBoxHeight = height - 175;
		setCountRows(Math.round(maxListBoxHeight / 22));
		// System.out.println("MaxListBoxHeight : " + maxListBoxHeight);
		// System.out.println("==========> : " + getCountRows());

		borderLayout_secRolesList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecRoleList_ShowAll.setChecked(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecRoleList_rolShortdescription.setSortAscending(new FieldComparator("rolShortdescription", true));
		listheader_SecRoleList_rolShortdescription.setSortDescending(new FieldComparator("rolShortdescription", false));
		listheader_SecRoleList_rolLongdescription.setSortAscending("");
		listheader_SecRoleList_rolLongdescription.setSortDescending("");

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecRole> soSecRole = new HibernateSearchObject<SecRole>(SecRole.class, getCountRows());
		soSecRole.addSort("rolShortdescription", false);

		// set the paging params
		paging_SecRoleList.setPageSize(getCountRows());
		paging_SecRoleList.setDetailed(true);

		// Set the ListModel.
		getPagedListWrapper().init(soSecRole, listBoxSecRoles, paging_SecRoleList);
		// set the itemRenderer
		listBoxSecRoles.setItemRenderer(new SecRoleListModelItemRenderer());

	}

	/**
	 * Call the SecRole dialog with the selected entry. <br>
	 * <br>
	 * This methode is forwarded from the listboxes item renderer. <br>
	 * see: de.forsthaus.webui.security.role.model.SecRoleListModelItemRenderer.
	 * java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRoleItemDoubleClicked(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxSecRoles.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			SecRole aRole = (SecRole) item.getAttribute("data");

			showDetailView(aRole);
		}
	}

	/**
	 * Call the SecRole dialog with a new empty entry. <br>
	 */
	public void onClick$button_SecRoleList_NewSecRole(Event event) throws Exception {

		// create a new branch object
		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		SecRole aRole = getSecurityService().getNewSecRole();

		showDetailView(aRole);

	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param aRole
	 * @throws Exception
	 */
	private void showDetailView(SecRole aRole) throws Exception {

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("role", aRole);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxSecRoles", listBoxSecRoles);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/sec_role/secRoleDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
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

		Events.postEvent("onCreate", secRoleListWindow, event);
		secRoleListWindow.invalidate();
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecRoleList_ShowAll(Event event) {

		// empty the text search boxes
		tb_SecRole_RoleName.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecRole> soSecRole = new HibernateSearchObject<SecRole>(SecRole.class, getCountRows());
		soSecRole.addSort("rolShortdescription", false);

		// Set the ListModel.
		getPagedListWrapper().init(soSecRole, listBoxSecRoles, paging_SecRoleList);

	}

	/**
	 * when the "print" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecRoleList_PrintSecRole(Event event) throws InterruptedException {
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		new SecRoleSimpleDJReport(win);
	}

	/**
	 * Filter the role list with 'like RoleName'. <br>
	 */
	public void onClick$button_SecRoleList_rolShortdescription(Event event) throws Exception {

		// if not empty
		if (!tb_SecRole_RoleName.getValue().isEmpty()) {
			checkbox_SecRoleList_ShowAll.setChecked(false); // unCheck

			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<SecRole> soSecRole = new HibernateSearchObject<SecRole>(SecRole.class, getCountRows());
			soSecRole.addSort("rolShortdescription", false);

			soSecRole.addFilter(new Filter("rolShortdescription", "%" + tb_SecRole_RoleName.getValue() + "%", Filter.OP_ILIKE));

			// Set the ListModel.
			getPagedListWrapper().init(soSecRole, listBoxSecRoles, paging_SecRoleList);
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
