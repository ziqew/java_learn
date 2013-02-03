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
package de.forsthaus.webui.office;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Office;
import de.forsthaus.backend.service.OfficeService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/office/officeList.zul
 * file.<br>
 * <b>WORKS with the annotated databinding mechanism.</b><br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changings for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          03/09/2009: sge changed for allow repainting after resizing.<br>
 *          with the refresh button.<br>
 *          07/03/2010: sge modified for zk5.x with complete Annotated
 *          Databinding.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class OfficeListCtrl extends GFCBaseListCtrl<Office> implements Serializable {

	private static final long serialVersionUID = -2170565288232491362L;
	private static final Logger logger = Logger.getLogger(OfficeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowOfficeList; // autowired
	protected Panel panelOfficeList; // autowired

	protected Borderlayout borderLayout_officeList; // autowired
	protected Paging paging_OfficeList; // autowired
	protected Listbox listBoxOffice; // autowired
	protected Listheader listheader_OfficeList_No; // autowired
	protected Listheader listheader_OfficeList_Name1; // autowired
	protected Listheader listheader_OfficeList_Name2; // autowired
	protected Listheader listheader_OfficeList_City; // autowired

	// NEEDED for ReUse in the SearchWindow
	private HibernateSearchObject<Office> searchObj;

	// row count for listbox
	private int countRows;

	// Databinding
	private AnnotateDataBinder binder;
	private OfficeMainCtrl officeMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient OfficeService officeService;

	/**
	 * default constructor.<br>
	 */
	public OfficeListCtrl() {
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

		/**
		 * 1. Get the overhanded MainController.<br>
		 * 2. Set this controller in the MainController.<br>
		 * 3. Check if a 'selectedObject' exists yet in the MainController.<br>
		 */
		if (arg.containsKey("ModuleMainController")) {
			setOfficeMainCtrl((OfficeMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getOfficeMainCtrl().setOfficeListCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getOfficeMainCtrl().getSelectedOffice() != null) {
				setSelectedOffice(getOfficeMainCtrl().getSelectedOffice());
			} else
				setSelectedOffice(null);
		} else {
			setSelectedOffice(null);
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

	public void onCreate$windowOfficeList(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_OfficeList.setPageSize(getCountRows());
		paging_OfficeList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OfficeList_No.setSortAscending(new FieldComparator("filNr", true));
		listheader_OfficeList_No.setSortDescending(new FieldComparator("filNr", false));
		listheader_OfficeList_Name1.setSortAscending(new FieldComparator("filName1", true));
		listheader_OfficeList_Name1.setSortDescending(new FieldComparator("filName1", false));
		listheader_OfficeList_Name2.setSortAscending(new FieldComparator("filName2", true));
		listheader_OfficeList_Name2.setSortDescending(new FieldComparator("filName2", false));
		listheader_OfficeList_City.setSortAscending(new FieldComparator("filOrt", true));
		listheader_OfficeList_City.setSortDescending(new FieldComparator("filOrt", false));

		// ++ create the searchObject and init sorting ++//
		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<Office>(Office.class, getCountRows());
		searchObj.addSort("filName1", false);
		setSearchObj(searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxOffice(), paging_OfficeList);
		BindingListModelList lml = (BindingListModelList) getListBoxOffice().getModel();
		setOffices(lml);

		// check if first time opened and init databinding for selectedBean
		if (getSelectedOffice() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxOffice().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedOffice((Office) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxOffice(), getSelectedOffice()));
			}
		}

	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedOfficeItem(Event event) {
		// logger.debug(event.toString());

		Office anOffice = getSelectedOffice();

		if (anOffice != null) {
			setSelectedOffice(anOffice);
			setOffice(anOffice);

			// check first, if the tabs are created
			if (getOfficeMainCtrl().getOfficeDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getOfficeMainCtrl().tabOfficeDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getOfficeMainCtrl().getOfficeDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getOfficeMainCtrl().tabOfficeDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getOfficeMainCtrl().tabOfficeDetail, anOffice));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxOffice(Event event) {
		// logger.debug(event.toString());

		// selectedOffice is filled by annotated databinding mechanism
		Office anOffice = getSelectedOffice();

		if (anOffice == null) {
			return;
		}

		// check first, if the tabs are created
		if (getOfficeMainCtrl().getOfficeDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getOfficeMainCtrl().tabOfficeDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getOfficeMainCtrl().getOfficeDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getOfficeMainCtrl().tabOfficeDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getOfficeMainCtrl().getOfficeDetailCtrl().setSelectedOffice(anOffice);
		getOfficeMainCtrl().getOfficeDetailCtrl().setOffice(anOffice);

		// store the selected bean values as current
		getOfficeMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		String str = Labels.getLabel("common.Office") + ": " + anOffice.getFilBezeichnung();
		EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Recalculates the container size for this controller and resize them.
	 * 
	 * Calculate how many rows have been place in the listbox. Get the
	 * currentDesktopHeight from a hidden Intbox from the index.zul that are
	 * filled by onClientInfo() in the indexCtroller.
	 */
	public void doFitSize() {

		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 5;

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - specialSize - 148;
		setCountRows((int) Math.round(maxListBoxHeight / 17.7));
		borderLayout_officeList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowOfficeList.invalidate();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Best Pratice Hint:<br>
	 * The setters/getters for the local annotated data binded Beans/Sets are
	 * administered in the module's mainController. Working in this way you have
	 * clean line to share this beans/sets with other controllers.
	 */
	/* Master BEANS */
	public Office getOffice() {
		// STORED IN THE module's MainController
		return getOfficeMainCtrl().getSelectedOffice();
	}

	public void setOffice(Office anOffice) {
		// STORED IN THE module's MainController
		getOfficeMainCtrl().setSelectedOffice(anOffice);
	}

	public void setSelectedOffice(Office selectedOffice) {
		// STORED IN THE module's MainController
		getOfficeMainCtrl().setSelectedOffice(selectedOffice);
	}

	public Office getSelectedOffice() {
		// STORED IN THE module's MainController
		return getOfficeMainCtrl().getSelectedOffice();
	}

	public void setOffices(BindingListModelList offices) {
		// STORED IN THE module's MainController
		getOfficeMainCtrl().setOffices(offices);
	}

	public BindingListModelList getOffices() {
		// STORED IN THE module's MainController
		return getOfficeMainCtrl().getOffices();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setOfficeMainCtrl(OfficeMainCtrl officeMainCtrl) {
		this.officeMainCtrl = officeMainCtrl;
	}

	public OfficeMainCtrl getOfficeMainCtrl() {
		return this.officeMainCtrl;
	}

	/* SERVICES */
	public void setOfficeService(OfficeService officeService) {
		this.officeService = officeService;
	}

	public OfficeService getOfficeService() {
		return this.officeService;
	}

	/* COMPONENTS and OTHERS */
	public void setSearchObj(HibernateSearchObject<Office> searchObj) {
		this.searchObj = searchObj;
	}

	public HibernateSearchObject<Office> getSearchObj() {
		return this.searchObj;
	}

	public Listbox getListBoxOffice() {
		return this.listBoxOffice;
	}

	public void setListBoxOffice(Listbox listBoxOffice) {
		this.listBoxOffice = listBoxOffice;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}
