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
package de.forsthaus.webui.order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.model.Order;
import de.forsthaus.backend.model.Orderposition;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.CustomerService;
import de.forsthaus.backend.service.OrderService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.order.model.OrderListModelItemRenderer;
import de.forsthaus.webui.order.model.OrderSearchCustomerListModelItemRenderer;
import de.forsthaus.webui.orderposition.model.OrderpositionListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;
import de.forsthaus.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/order/orderList.zul file.<br>
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
public class OrderListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5710086946825179284L;
	private static final Logger logger = Logger.getLogger(OrderListCtrl.class);

	private PagedListWrapper<Order> plwOrders;
	private PagedListWrapper<Orderposition> plwOrderpositions;
	private PagedListWrapper<Customer> plwCustomers;
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window orderListWindow; // autowired
	// Listbox orders
	protected Paging paging_OrderList; // autowired
	protected Listbox listBoxOrder; // autowired
	protected Listheader listheader_OrderList_OrderNo; // autowired
	protected Listheader listheader_OrderList_OderDescr; // autowired
	// Listbox orderPositions
	protected Paging paging_OrderArticleList; // autowire
	protected Listbox listBoxOrderArticle; // autowired
	protected Listheader listheader_OrderPosList_Orderpos_No; // autowired
	protected Listheader listheader_OrderPosList_Shorttext; // autowired
	protected Listheader listheader_OrderPosList_Count; // autowired
	protected Listheader listheader_OrderPosList_SinglePrice; // autowired
	protected Listheader listheader_OrderPosList_WholePrice; // autowired

	protected Listfooter listfooter_OrderPosList_Count; // autowired
	protected Listfooter listfooter_OrderPosList_WholePrice; // autowired

	protected Hbox hBoxCustomerSearch; // autowired

	// bandbox searchCustomer
	protected Bandbox bandbox_OrderList_CustomerSearch;
	protected Textbox tb_Orders_SearchCustNo; // autowired
	protected Textbox tb_Orders_CustSearchMatchcode; // autowired
	protected Textbox tb_Orders_SearchCustName1; // autowired
	protected Textbox tb_Orders_SearchCustCity; // autowired
	// listbox searchCustomer
	protected Paging paging_OrderList_CustomerSearchList; // autowired
	protected Listbox listBoxCustomerSearch; // autowired
	transient protected Listheader listheader_CustNo; // autowired
	protected Listheader listheader_CustMatchcode; // autowired
	protected Listheader listheader_CustName1; // autowired
	protected Listheader listheader_CustCity; // autowired

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_OrderList_NewOrder; // autowired

	private transient HibernateSearchObject<Customer> searchObjCustomer;

	private transient int pageSizeOrders;
	private int pageSizeOrderPositions;
	private final int pageSizeSearchCustomers = 20;

	// ServiceDAOs / Domain Classes
	private Customer customer;
	private Order order;
	private transient OrderService orderService;
	private transient CustomerService customerService;
	private transient BrancheService brancheService;

	/**
	 * default constructor.<br>
	 */
	public OrderListCtrl() {
		super();
	}

	public void onCreate$orderListWindow(Event event) throws Exception {
		/* autowire comps the vars */
		// doOnCreateCommon(orderListWindow, event);

		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		// check if the orderList is called with a customer param
		if (args.containsKey("customerDialogCtrl")) {
			hBoxCustomerSearch.setVisible(false);
		} else {
			hBoxCustomerSearch.setVisible(true);
		}

		// check if the orderList is called with a customer param
		if (args.containsKey("customer")) {
			setCustomer((Customer) args.get("customer"));
		} else {
			setCustomer(null);
		}

		// check if the orderList is called from the Customer Dialog
		// and set the pageSizes
		if (args.containsKey("rowSizeOrders")) {
			int rowSize = (Integer) args.get("rowSizeOrders");
			setPageSizeOrders(rowSize);
		} else {
			setPageSizeOrders(15);
		}
		if (args.containsKey("rowSizeOrderPositions")) {
			int rowSize = (Integer) args.get("rowSizeOrderPositions");
			setPageSizeOrderPositions(rowSize);
		} else {
			setPageSizeOrderPositions(15);
		}

		paintComponents();

	}

	private void paintComponents() {

		// set the bandbox to readonly
		bandbox_OrderList_CustomerSearch.setReadonly(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OrderList_OrderNo.setSortAscending(new FieldComparator("aufNr", true));
		listheader_OrderList_OrderNo.setSortDescending(new FieldComparator("aufNr", false));
		listheader_OrderList_OderDescr.setSortAscending(new FieldComparator("aufBezeichnung", true));
		listheader_OrderList_OderDescr.setSortDescending(new FieldComparator("aufBezeichnung", false));

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OrderPosList_Orderpos_No.setSortAscending(new FieldComparator("aupPosition", true));
		listheader_OrderPosList_Orderpos_No.setSortDescending(new FieldComparator("aupPosition", false));
		listheader_OrderPosList_Shorttext.setSortAscending(new FieldComparator("article.artKurzbezeichnung", true));
		listheader_OrderPosList_Shorttext.setSortDescending(new FieldComparator("article.artKurzbezeichnung", false));
		listheader_OrderPosList_Count.setSortAscending(new FieldComparator("aupMenge", true));
		listheader_OrderPosList_Count.setSortDescending(new FieldComparator("aupMenge", false));
		listheader_OrderPosList_SinglePrice.setSortAscending(new FieldComparator("aupEinzelwert", true));
		listheader_OrderPosList_SinglePrice.setSortDescending(new FieldComparator("aupEinzelwert", false));
		listheader_OrderPosList_WholePrice.setSortAscending(new FieldComparator("aupGesamtwert", true));
		listheader_OrderPosList_WholePrice.setSortDescending(new FieldComparator("aupGesamtwert", false));

		// ++ create the searchObject and init sorting ++//
		// only in sample app init with all orders
		HibernateSearchObject<Order> soOrder = new HibernateSearchObject<Order>(Order.class, getPageSizeOrders());
		soOrder.addSort("aufNr", false);

		// set the paging params
		paging_OrderList.setPageSize(getPageSizeOrders());
		paging_OrderList.setDetailed(true);

		paging_OrderArticleList.setPageSize(getPageSizeOrderPositions());
		paging_OrderArticleList.setDetailed(true);

		// return if the customer bean is transient, because if not save we get
		// an exception by trying to load data for it.
		// if (customer.isNew())
		// return;

		// Set the ListModel for the orders.
		// Check if the is a customer bean and
		// check too if it's new.
		if (customer == null) {
			getPlwOrders().init(soOrder, listBoxOrder, paging_OrderList);
		} else if (!customer.isNew()) {
			soOrder.addFilter(new Filter("customer", customer, Filter.OP_EQUAL));
			getPlwOrders().init(soOrder, listBoxOrder, paging_OrderList);
		} else if (customer.isNew()) {
			return;
		}

		listBoxOrder.setItemRenderer(new OrderListModelItemRenderer());

		listBoxOrderArticle.setItemRenderer(new OrderpositionListModelItemRenderer());

		// init the first entry
		ListModelList lml = (ListModelList) listBoxOrder.getModel();

		// Now we would show the corresponding detail list of the first
		// selected entry of the MASTER Table
		// We became not the first item FROM the list because it's not
		// rendered at this time.
		// So we take the first entry in the ListModelList and set as
		// selected.
		if (lml.getSize() > 0) {
			int rowIndex = 0;
			listBoxOrder.setSelectedIndex(rowIndex);
			// get the first entry and cast them to the needed object
			Order anOrder = (Order) lml.get(rowIndex);
			if (anOrder != null) {
				// get the related order positions
				HibernateSearchObject<Orderposition> soOrderPosition = new HibernateSearchObject<Orderposition>(Orderposition.class, getPageSizeOrderPositions());
				soOrderPosition.addFilter(new Filter("order", anOrder, Filter.OP_EQUAL));
				// deeper loading of the relation to prevent the lazy
				// loading problem.
				soOrderPosition.addFetch("article");

				// Set the ListModel.
				getPlwOrderpositions().init(soOrderPosition, listBoxOrderArticle, paging_OrderArticleList);

				/** +++ get the SUM of the orderpositions for the ListFooter +++ */
				String s = String.valueOf(getOrderService().getOrderSum(anOrder));
				if (s != "null") {
					listfooter_OrderPosList_WholePrice.setLabel(s);
					// listfooter_OrderPosList_WholePrice.setLabel(String.valueOf(getOrderService().getOrderSum(anOrder)));
				} else
					listfooter_OrderPosList_WholePrice.setLabel("0.00");

			}
		}
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		orderListWindow.setVisible(workspace.isAllowed("orderListWindow"));

		btnHelp.setVisible(workspace.isAllowed("button_OrderList_btnHelp"));
		button_OrderList_NewOrder.setVisible(workspace.isAllowed("button_OrderList_NewOrder"));

	}

	public void onSelect$listBoxOrder(Event event) throws Exception {
		// logger.debug(event.toString());

		Listitem item = this.listBoxOrder.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Order order = (Order) item.getAttribute("data");

			if (order != null) {
				// Set the ListModel and the itemRenderer for the order
				// articles.g

				HibernateSearchObject<Orderposition> soOrderPosition = new HibernateSearchObject<Orderposition>(Orderposition.class, getPageSizeOrderPositions());
				soOrderPosition.addFilter(new Filter("order", order, Filter.OP_EQUAL));
				// deeper loading of the relation to prevent the lazy loading
				// problem.
				soOrderPosition.addFetch("article");

				// Set the ListModel.
				getPlwOrderpositions().init(soOrderPosition, listBoxOrderArticle, paging_OrderArticleList);

				// +++ get the SUM of the orderpositions +++ //
				String s = String.valueOf(getOrderService().getOrderSum(order));
				if (s != "null") {
					listfooter_OrderPosList_WholePrice.setLabel(String.valueOf(getOrderService().getOrderSum(order)));
				} else
					listfooter_OrderPosList_WholePrice.setLabel("0.00");

			}
		}
	}

	/**
	 * Call the Order dialog with the selected entry. <br>
	 * <br>
	 * This methode is forwarded from the listboxes item renderer. <br>
	 * see: de.forsthaus.webui.order.model.OrderListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDoubleClickedOrderItem(Event event) throws Exception {

		// get the selected object
		Listitem item = listBoxOrder.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Order anOrder = (Order) item.getAttribute("data");

			showDetailView(anOrder);

		}
	}

	/**
	 * Call the order dialog with a new empty entry. <br>
	 */
	public void onClick$button_OrderList_NewOrder(Event event) throws Exception {
		// logger.debug(event.toString());

		// create a new order object
		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		Order anOrder = getOrderService().getNewOrder();

		showDetailView(anOrder);
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param branche
	 * @throws Exception
	 */
	private void showDetailView(Order anOrder) throws Exception {
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("order", anOrder);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxOrder", listBoxOrder);
		map.put("orderListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/order/orderDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			ZksampleMessageUtils.showErrorMessage(e.toString());
		}

	}

	/**
	 * when the "Order search" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_OrderList_OrderNameSearch(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		// logger.debug(event.toString());

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

		paintComponents();
		orderListWindow.invalidate();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search Customer +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button of the search bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Close(Event event) {
		// logger.debug(event.toString());

		bandbox_OrderList_CustomerSearch.close();
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Search(Event event) {
		// logger.debug(event.toString());

		doSearch();
	}

	public void onOpen$bandbox_OrderList_CustomerSearch(Event event) throws Exception {
		// logger.debug(event.toString());

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_CustNo.setSortAscending(new FieldComparator("kunNr", true));
		listheader_CustNo.setSortDescending(new FieldComparator("kunNr", false));
		listheader_CustMatchcode.setSortAscending(new FieldComparator("kunMatchcode", true));
		listheader_CustMatchcode.setSortDescending(new FieldComparator("kunMatchcode", false));
		listheader_CustName1.setSortAscending(new FieldComparator("kunName1", true));
		listheader_CustName1.setSortDescending(new FieldComparator("kunName1", false));
		listheader_CustCity.setSortAscending(new FieldComparator("kunOrt", true));
		listheader_CustCity.setSortDescending(new FieldComparator("kunOrt", false));

		// set the paging params
		paging_OrderList_CustomerSearchList.setPageSize(pageSizeSearchCustomers);
		paging_OrderList_CustomerSearchList.setDetailed(true);

		// ++ create the searchObject and init sorting ++ //
		if (getSearchObjCustomer() == null) {
			setSearchObjCustomer(new HibernateSearchObject<Customer>(Customer.class, pageSizeSearchCustomers));
			getSearchObjCustomer().addSort("kunMatchcode", false);
			setSearchObjCustomer(searchObjCustomer);
		}

		// Set the ListModel.
		getPlwCustomers().init(getSearchObjCustomer(), listBoxCustomerSearch, paging_OrderList_CustomerSearchList);
		// set the itemRenderer
		listBoxCustomerSearch.setItemRenderer(new OrderSearchCustomerListModelItemRenderer());
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	private void doSearch() {

		searchObjCustomer = new HibernateSearchObject<Customer>(Customer.class, pageSizeSearchCustomers);

		// check which field have input
		if (StringUtils.isNotEmpty(tb_Orders_SearchCustNo.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunNr", tb_Orders_SearchCustNo.getValue(), Filter.OP_EQUAL));
		}

		if (StringUtils.isNotEmpty(tb_Orders_CustSearchMatchcode.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunMatchcode", "%" + tb_Orders_CustSearchMatchcode.getValue().toUpperCase() + "%", Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_Orders_SearchCustName1.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunName1", "%" + tb_Orders_SearchCustName1.getValue() + "%", Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_Orders_SearchCustCity.getValue())) {
			searchObjCustomer.addFilter(new Filter("kunOrt", "%" + tb_Orders_SearchCustCity.getValue() + "%", Filter.OP_ILIKE));
		}

		setSearchObjCustomer(this.searchObjCustomer);

		// Set the ListModel.
		getPlwCustomers().init(getSearchObjCustomer(), listBoxCustomerSearch, paging_OrderList_CustomerSearchList);

	}

	/**
	 * when doubleClick on a item in the customerSearch listbox.<br>
	 * <br>
	 * Select the customer and search all orders for him.
	 * 
	 * @param event
	 */
	public void onDoubleClickedCustomerItem(Event event) {
		// logger.debug(event.toString());

		// get the customer
		Listitem item = this.listBoxCustomerSearch.getSelectedItem();
		if (item != null) {

			/* clear the listboxes from older stuff */
			if ((ListModelList) listBoxOrder.getModel() != null) {
				((ListModelList) listBoxOrder.getModel()).clear();
			}
			if ((ListModelList) listBoxOrderArticle.getModel() != null) {
				((ListModelList) listBoxOrderArticle.getModel()).clear();
			}

			Customer customer = (Customer) item.getAttribute("data");

			if (customer != null)
				setCustomer(customer);

			bandbox_OrderList_CustomerSearch.setValue(customer.getKunName1() + ", " + customer.getKunOrt());

			// get all orders for the selected customer
			HibernateSearchObject<Order> soOrder = new HibernateSearchObject<Order>(Order.class, getPageSizeOrders());
			soOrder.addSort("aufNr", false);
			soOrder.addFilter(new Filter("customer", customer, Filter.OP_EQUAL));

			// Set the ListModel.
			getPlwOrders().init(soOrder, listBoxOrder, paging_OrderList);

			// get the first object and poll and show the orderpositions
			ListModelList lml = (ListModelList) listBoxOrder.getModel();

			if (lml.size() > 0) {

				Order anOrder = (Order) lml.get(0);

				if (anOrder != null) {
					HibernateSearchObject<Orderposition> soOrderPosition = new HibernateSearchObject<Orderposition>(Orderposition.class, getPageSizeOrderPositions());
					soOrderPosition.addFilter(new Filter("order", anOrder, Filter.OP_EQUAL));
					// deeper loading of the relation to prevent the lazy
					// loading problem.
					soOrderPosition.addFetch("article");

					getPlwOrderpositions().init(soOrderPosition, listBoxOrderArticle, paging_OrderArticleList);
				}
			} else {
				// get a new Order for searching that the resultList is cleared
				// Order anOrder = getOrderService().getNewOrder();
				// HibernateSearchObject<Orderposition> soOrderPosition = new
				// HibernateSearchObject<Orderposition>(Orderposition.class,
				// getPageSizeOrderPositions());
				// soOrderPosition.addFilter(new Filter("order", anOrder,
				// Filter.OP_EQUAL));
				// // deeper loading of the relation to prevent the lazy
				// // loading problem.
				// soOrderPosition.addFetch("article");
				//
				// getPlwOrderpositions().init(soOrderPosition,
				// listBoxOrderArticle, paging_OrderArticleList);

				// Bugfix: sge 18/11/2011
				ListModelList lml2 = (ListModelList) listBoxOrder.getModel();
				lml2.clear();

			}
		}

		// close the bandbox
		bandbox_OrderList_CustomerSearch.close();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setOrder(Order order) {
		this.order = order;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return this.customer;
	}

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

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public OrderService getOrderService() {
		return this.orderService;
	}

	public void setSearchObjCustomer(HibernateSearchObject<Customer> searchObjCustomer) {
		this.searchObjCustomer = searchObjCustomer;
	}

	public HibernateSearchObject<Customer> getSearchObjCustomer() {
		return this.searchObjCustomer;
	}

	public void setPageSizeOrders(int pageSizeOrders) {
		this.pageSizeOrders = pageSizeOrders;
	}

	public int getPageSizeOrders() {
		return this.pageSizeOrders;
	}

	public void setPageSizeOrderPositions(int pageSizeOrderPositions) {
		this.pageSizeOrderPositions = pageSizeOrderPositions;
	}

	public int getPageSizeOrderPositions() {
		return this.pageSizeOrderPositions;
	}

	public Window getOrderListWindow() {
		return this.orderListWindow;
	}

	public Listbox getListBoxOrderArticle() {
		return this.listBoxOrderArticle;
	}

	public void setPlwOrders(PagedListWrapper<Order> plwOrders) {
		this.plwOrders = plwOrders;
	}

	public PagedListWrapper<Order> getPlwOrders() {
		return this.plwOrders;
	}

	public void setPlwOrderpositions(PagedListWrapper<Orderposition> plwOrderpositions) {
		this.plwOrderpositions = plwOrderpositions;
	}

	public PagedListWrapper<Orderposition> getPlwOrderpositions() {
		return this.plwOrderpositions;
	}

	public void setPlwCustomers(PagedListWrapper<Customer> plwCustomers) {
		this.plwCustomers = plwCustomers;
	}

	public PagedListWrapper<Customer> getPlwCustomers() {
		return this.plwCustomers;
	}

}
