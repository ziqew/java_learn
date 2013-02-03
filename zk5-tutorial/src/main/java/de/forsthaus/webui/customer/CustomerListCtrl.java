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
package de.forsthaus.webui.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.annotation.Secured;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Calendar;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.CustomerService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.customer.model.CustomerListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseListCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/customer/customerList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009:sge Migrating the list models for paging. <br>
 *          07/24/2009:sge changes for clustering.<br>
 *          10/12/2009:sge changings in the saving routine.<br>
 *          11/07/2009:bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          03/09/2009: sge changed for allow repainting after resizing.<br>
 *          with the refresh button.<br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class CustomerListCtrl extends GFCBaseListCtrl<Customer> implements Serializable {

	private static final long serialVersionUID = 6787508590585436872L;
	private final static Logger logger = Logger.getLogger(CustomerListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_customerList; // autowired
	protected Panel panel_CustomerList; // autowired
	// listbox customerList
	protected Borderlayout borderLayout_customerList; // autowired
	protected Paging pagingCustomerList; // autowired
	protected Listbox listBoxCustomer; // autowired
	protected Listheader listheader_CustNo; // autowired
	protected Listheader listheader_CustMatchcode; // autowired
	protected Listheader listheader_CustName1; // autowired
	protected Listheader listheader_CustName2; // autowired
	protected Listheader listheader_CustCity; // autowired

	protected Panel customerSeekPanel; // autowired
	protected Panel customerListPanel; // autowired

	// checkRights
	protected Button btnHelp;
	protected Button button_CustomerList_NewCustomer;
	protected Button button_CustomerList_CustomerFindDialog;
	protected Button button_CustomerList_PrintList;

	// NEEDED for the ReUse in the SearchWindow
	protected HibernateSearchObject<Customer> searchObj;

	// row count for listbox
	private int countRows;

	private transient CustomerService customerService;
	private transient BrancheService brancheService;

	// Test Popup on a listcell
	private Popup popup = null;
	private Label popLabel = null;
	private Vbox vbox = null;
	private Hbox titleBox = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerListCtrl() {
		super();
	}

	public void onCreate$window_customerList(Event event) throws Exception {
		/* autowire comps the vars */
		// doOnCreateCommon(window_customerList, event);

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		int panelHeight = 25;
		// TODO put the logic for working with panel in the ApplicationWorkspace
		final boolean withPanel = false;
		if (withPanel == false) {
			panel_CustomerList.setVisible(false);
		} else {
			panel_CustomerList.setVisible(true);
			panelHeight = 0;
		}

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;

		height = height + panelHeight;

		final int maxListBoxHeight = height - 145;
		setCountRows(Math.round(maxListBoxHeight / 24));
		// System.out.println("MaxListBoxHeight : " + maxListBoxHeight);
		// System.out.println("==========> : " + getCountRows());

		borderLayout_customerList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		// set the paging params
		pagingCustomerList.setPageSize(getCountRows());
		pagingCustomerList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_CustNo.setSortAscending(new FieldComparator("kunNr", true));
		listheader_CustNo.setSortDescending(new FieldComparator("kunNr", false));
		listheader_CustMatchcode.setSortAscending(new FieldComparator("kunMatchcode", true));
		listheader_CustMatchcode.setSortDescending(new FieldComparator("kunMatchcode", false));
		listheader_CustName1.setSortAscending(new FieldComparator("kunName1", true));
		listheader_CustName1.setSortDescending(new FieldComparator("kunName1", false));
		listheader_CustName2.setSortAscending(new FieldComparator("kunName2", true));
		listheader_CustName2.setSortDescending(new FieldComparator("kunName2", false));
		listheader_CustCity.setSortAscending(new FieldComparator("kunOrt", true));
		listheader_CustCity.setSortDescending(new FieldComparator("kunOrt", false));

		// ++ create the searchObject and init sorting ++//
		searchObj = new HibernateSearchObject<Customer>(Customer.class, getCountRows());
		searchObj.addSort("kunName1", false);
		setSearchObj(this.searchObj);

		// Set the ListModel for the articles.
		getPagedListWrapper().init(searchObj, listBoxCustomer, pagingCustomerList);
		// set the itemRenderer
		listBoxCustomer.setItemRenderer(new CustomerListModelItemRenderer());
	}

	/**
	 * Method for showing how to popup on a listcell.<br>
	 * Extended. Now, it get the ListItem's parent and iterate through a list of
	 * its listcells and show the value for each.
	 * 
	 * @param event
	 */
	public void onMouseOverListCell(MouseEvent event) {

		// System.out.println("current target : " + event.getTarget());
		if (popup == null) {
			popup = new Popup();
			popup.setSclass("myPopup");
			popup.setParent(window_customerList);
			// popup.setWidth("200px");
			vbox = new Vbox();
			vbox.setStyle("padding: 0px;");
			vbox.setParent(popup);
			// clear old stuff
			vbox.getChildren().clear();
			titleBox = new Hbox();
			titleBox.setHeight("15px");
			titleBox.setWidth("100%");
			titleBox.setStyle("background-color: red;");
			titleBox.setParent(vbox);

			popLabel = new Label();
			popLabel.setParent(titleBox);
			popLabel.setValue("Customer Details");
			popLabel.setStyle("color: white; ");

		} else {
			// clear old stuff
			vbox.getChildren().clear();
			titleBox = new Hbox();
			titleBox.setHeight("15px");
			titleBox.setWidth("100%");
			titleBox.setStyle("background-color: red;");
			titleBox.setParent(vbox);
			popLabel = new Label();
			popLabel.setParent(titleBox);
			popLabel.setValue("Customer Details");
			popLabel.setStyle("color: white; ");
			popup.close();
		}

		Component comp = event.getTarget();

		if (comp instanceof Listcell) {

			// System.out.println("1: " + comp.toString());
			// System.out.println("2: " + ((Listcell)
			// comp).getLabel().toString());
			// System.out.println("3: " + comp.getParent().toString());

			// get the parent object of the listcell, should be a listitem
			Component parentComponent = comp.getParent();
			if (parentComponent instanceof Listitem) {

				// get a list of listcell that resides in the listItem
				List list = parentComponent.getChildren();

				// for all listcells we add a label with its value
				for (Object object : list) {
					if (object instanceof Listcell) {
						popLabel = new Label();
						popLabel.setParent(vbox);
						popLabel.setValue(((Listcell) object).getLabel().toString());
					}

				}
			}

		}

		Calendar calendar = new Calendar();
		calendar.setId("cal");
		calendar.setStyle("border: none;");
		// calendar.setWidth("180px");
		// calendar.setWidth("180px");
		calendar.setParent(vbox);

		popup.open(window_customerList);

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		window_customerList.setVisible(workspace.isAllowed("window_customerList"));
		btnHelp.setVisible(workspace.isAllowed("button_CustomerList_btnHelp"));
		button_CustomerList_NewCustomer.setVisible(workspace.isAllowed("button_CustomerList_NewCustomer"));
		button_CustomerList_CustomerFindDialog.setVisible(workspace.isAllowed("button_CustomerList_CustomerFindDialog"));
		button_CustomerList_PrintList.setVisible(workspace.isAllowed("button_CustomerList_PrintList"));

	}

	/**
	 * This methode is forwarded from the listboxes item renderer. <br>
	 * see: de.forsthaus.webui.customer.model.CustomerListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	@Secured({ "CustomerList_listBoxCustomer.onDoubleClick" })
	public void onCustomerItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected customer object
		Listitem item = this.listBoxCustomer.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Customer aCustomer = (Customer) item.getAttribute("data");

			showDetailView(aCustomer);
		}
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 */
	public void onClick$button_CustomerList_NewCustomer(Event event) throws Exception {
		logger.debug(event.toString());

		// create a new customer object
		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		try {
			Customer aCustomer = getCustomerService().getNewCustomer();

			// init
			aCustomer.setOffice(getUserWorkspace().getOffice()); // init
			// init Default empty Branch
			aCustomer.setBranche(getBrancheService().getBrancheById(new Integer(1033).longValue())); // init
			aCustomer.setKunMahnsperre(false); // init

			showDetailView(aCustomer);
		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
		} catch (Exception e) {
			e.printStackTrace();
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param branche
	 * @throws Exception
	 */
	private void showDetailView(Customer aCustomer) throws Exception {
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customer", aCustomer);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the customerListbox from the
		 * dialog when we do a delete, edit or insert a customer.
		 */
		map.put("customerListCtrl", this);

		// call the zul-file with the parameters packed in a map
		Window win = null;
		try {
			win = (Window) Executions.createComponents("/WEB-INF/pages/customer/customerDialog.zul", null, map);
		} catch (final Exception e) {
			e.printStackTrace();
			ZksampleMessageUtils.showErrorMessage(e.toString());
			if (win != null) {
				win.detach();
			}
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		// ListModelList lml = (ListModelList) listBoxCustomer.getModel();
		// if (lml.getSize() > 0) {
		// listBoxCustomer.setSelectedIndex(0);
		// // Clients.scrollIntoView(listBoxCustomer);
		// listBoxCustomer.getTop();
		// }

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
		// logger.debug(event.toString());

		Events.postEvent("onCreate", this.window_customerList, event);
		window_customerList.invalidate();
	}

	/*
	 * call the customer dialog
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_CustomerList_CustomerFindDialog(Event event) throws Exception {
		// logger.debug(event.toString());

		/*
		 * we can call our customerDialog zul-file with parameters. So we can
		 * call them with a object of the selected customer. For handed over
		 * these parameter only a Map is accepted. So we put the customer object
		 * in a HashMap.
		 */
		final HashMap map = new HashMap();
		map.put("customerCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/customer/customerSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}

	}

	/**
	 * when the "xxxxxxxxx" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CustomerList_PrintList(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public BrancheService getBrancheService() {
		return this.brancheService;
	}

	public void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return this.customerService;
	}

	public HibernateSearchObject<Customer> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(HibernateSearchObject<Customer> searchObj) {
		this.searchObj = searchObj;
	}

	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

}