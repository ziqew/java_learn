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
package de.forsthaus.webui.security.right;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecTyp;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.security.right.model.SecRightListModelItemRenderer;
import de.forsthaus.webui.security.right.model.SecRightSecTypListModelItemRenderer;
import de.forsthaus.webui.security.right.report.SecRightSimpleDJReport;
import de.forsthaus.webui.util.GFCBaseListCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/sec_right/secRightList.zul file.<br>
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
public class SecRightListCtrl extends GFCBaseListCtrl<SecRight> implements Serializable {

	private static final long serialVersionUID = -6139454778139881103L;
	private static final Logger logger = Logger.getLogger(SecRightListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window secRightListWindow; // autowired
	protected Panel panel_SecRightList; // autowired

	// filter components
	protected Checkbox checkbox_SecRightList_ShowAll; // autowired
	protected Textbox tb_SecRightList_rigName; // aurowired
	protected Listbox lb_secRight_RightType; // aurowired

	// listbox secRightList
	protected Borderlayout borderLayout_secRightsList; // autowired
	protected Paging paging_SecRightList; // aurowired
	protected Listbox listBoxSecRights; // aurowired
	protected Listheader listheader_SecRightList_rigName; // autowired
	protected Listheader listheader_SecRightList_rigType; // autowired

	// row count for listbox
	private int countRows;

	// ServiceDAOs / Domain Classes
	private transient SecurityService securityService;

	/**
	 * default constructor.<br>
	 */
	public SecRightListCtrl() {
		super();
	}

	public void onCreate$secRightListWindow(Event event) throws Exception {
		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		int panelHeight = 25;
		// TODO put the logic for working with panel in the ApplicationWorkspace
		final boolean withPanel = false;
		if (withPanel == false) {
			panel_SecRightList.setVisible(false);
		} else {
			panel_SecRightList.setVisible(true);
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

		borderLayout_secRightsList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// init, show all rights
		checkbox_SecRightList_ShowAll.setChecked(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_SecRightList_rigName.setSortAscending(new FieldComparator("rigName", true));
		listheader_SecRightList_rigName.setSortDescending(new FieldComparator("rigName", false));
		listheader_SecRightList_rigType.setSortAscending(new FieldComparator("rigType", true));
		listheader_SecRightList_rigType.setSortDescending(new FieldComparator("rigType", false));

		// ++++++++++++++ DropDown ListBox ++++++++++++++++++ //
		// set listModel and itemRenderer for the dropdown listbox
		lb_secRight_RightType.setModel(new ListModelList(getSecurityService().getAllTypes()));
		lb_secRight_RightType.setItemRenderer(new SecRightSecTypListModelItemRenderer());

		ListModelList lml = (ListModelList) lb_secRight_RightType.getModel();
		// we added an empty SecType simulated the (-1) for showing all records.
		lml.add(0, SecTyp.EMPTY_SECTYP);

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecRight> soSecRight = new HibernateSearchObject<SecRight>(SecRight.class, getCountRows());
		soSecRight.addSort("rigName", false);

		// set the paging params
		paging_SecRightList.setPageSize(getCountRows());
		paging_SecRightList.setDetailed(true);

		// Set the ListModel.
		getPagedListWrapper().init(soSecRight, listBoxSecRights, paging_SecRightList);
		// set the itemRenderer
		listBoxSecRights.setItemRenderer(new SecRightListModelItemRenderer());

	}

	/**
	 * Call the SecRight dialog with the selected entry. <br>
	 * <br>
	 * This methode is forwarded from the listboxes item renderer. <br>
	 * see: de.forsthaus.webui.branch.model.BranchListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDoubleClickedRightItem(Event event) throws Exception {

		// get the selected object
		Listitem item = this.listBoxSecRights.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			SecRight aRight = (SecRight) item.getAttribute("data");

			showDetailView(aRight);
		}
	}

	/**
	 * Call the SecRight dialog with a new empty entry. <br>
	 */
	public void onClick$button_SecRightList_NewRight(Event event) throws Exception {

		// create a new right object
		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		SecRight aRight = getSecurityService().getNewSecRight();
		aRight.setRigType(1);

		showDetailView(aRight);

	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param aRight
	 * @throws Exception
	 */
	private void showDetailView(SecRight aRight) throws Exception {

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("right", aRight);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxSecRights", listBoxSecRights);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/sec_right/secRightDialog.zul", null, map);
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

		Events.postEvent("onCreate", secRightListWindow, event);
		secRightListWindow.invalidate();
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_SecRightList_ShowAll(Event event) {

		// empty the text search boxes
		tb_SecRightList_rigName.setValue(""); // clear
		lb_secRight_RightType.clearSelection(); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecRight> soSecRight = new HibernateSearchObject<SecRight>(SecRight.class, getCountRows());
		soSecRight.addSort("rigName", false);

		// Set the ListModel.
		getPagedListWrapper().init(soSecRight, listBoxSecRights, paging_SecRightList);

	}

	/**
	 * when the print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecRightList_PrintRightList(Event event) throws InterruptedException {
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		new SecRightSimpleDJReport(win);
	}

	/**
	 * Filter the rights list with 'like' RightName'. <br>
	 * We check additionally if something is selected in the right type listbox <br>
	 * for including in the search statement.<br>
	 */
	public void onClick$button_SecRightList_SearchRightName(Event event) throws Exception {

		// if not empty
		if (!tb_SecRightList_rigName.getValue().isEmpty()) {
			checkbox_SecRightList_ShowAll.setChecked(false); // clear

			// ++ create the searchObject and init sorting ++//
			final HibernateSearchObject<SecRight> soSecRight = new HibernateSearchObject<SecRight>(SecRight.class, getCountRows());
			soSecRight.addSort("rigName", false);

			soSecRight.addFilter(new Filter("rigName", "%" + tb_SecRightList_rigName.getValue() + "%", Filter.OP_ILIKE));

			// check if we must include a selected RightType item
			Listitem item = lb_secRight_RightType.getSelectedItem();

			if (item != null) {

				// casting to the needed object
				SecTyp type = (SecTyp) item.getAttribute("data");

				if (type.getStpId() > -1) {
					soSecRight.addFilter(new Filter("rigType", type.getStpId(), Filter.OP_EQUAL));
				}

				// Set the ListModel.
				getPagedListWrapper().init(soSecRight, listBoxSecRights, paging_SecRightList);

			} else {
				// Set the ListModel.
				getPagedListWrapper().init(soSecRight, listBoxSecRights, paging_SecRightList);
			}

		}

	}

	/**
	 * Filter the rights list with 'like RightType'. <br>
	 * We check additionally if something is standing in the textbox <br>
	 * for including in the search statement.
	 */
	public void onClick$button_SecRightList_SearchRightType(Event event) throws Exception {

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<SecRight> soSecRight = null;

		// get the selected item
		Listitem item = lb_secRight_RightType.getSelectedItem();

		if (item != null) {
			checkbox_SecRightList_ShowAll.setChecked(false); // clear
			// casting to the needed object
			SecTyp type = (SecTyp) item.getAttribute("data");

			if (type.getStpId() > -1) {

				soSecRight = new HibernateSearchObject<SecRight>(SecRight.class, getCountRows());
				soSecRight.addSort("rigName", false);

				soSecRight.addFilter(new Filter("rigType", type.getStpId(), Filter.OP_EQUAL));

				if (!tb_SecRightList_rigName.getValue().isEmpty()) {

					// mixed search statement -> like RightName + RightType
					soSecRight.addFilter(new Filter("rigName", "%" + tb_SecRightList_rigName.getValue() + "%", Filter.OP_ILIKE));
				}
				// Set the ListModel.
				getPagedListWrapper().init(soSecRight, listBoxSecRights, paging_SecRightList);
			}

		}

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Getter / Setter +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
