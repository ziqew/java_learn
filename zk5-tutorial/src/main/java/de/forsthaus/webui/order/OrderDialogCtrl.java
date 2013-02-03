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
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.SimpleConstraint;
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
import de.forsthaus.webui.order.model.OrderSearchCustomerListModelItemRenderer;
import de.forsthaus.webui.order.report.OrderDJReport;
import de.forsthaus.webui.orderposition.model.OrderpositionListModelItemRenderer;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;
import de.forsthaus.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/order/orderDialog.zul
 * file. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
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
public class OrderDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(OrderDialogCtrl.class);

	private transient PagedListWrapper<Orderposition> plwOrderpositions;
	private transient PagedListWrapper<Customer> plwCustomers;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window orderDialogWindow; // autowired
	protected Textbox kunNr; // autowired
	protected Textbox kunName1; // autowired
	protected Textbox aufNr; // autowired
	protected Textbox aufBezeichnung; // autowired

	protected Paging paging_ListBoxOrderOrderPositions; // autowired
	protected Listbox listBoxOrderOrderPositions; // autowired

	protected Listheader listheader_OrderPosList2_Orderpos_No; // autowired
	protected Listheader listheader_OrderPosList2_Shorttext; // autowired
	protected Listheader listheader_OrderPosList2_Count; // autowired
	protected Listheader listheader_OrderPosList2_SinglePrice; // autowired
	protected Listheader listheader_OrderPosList2_WholePrice; // autowired

	// search components
	// bandbox for searchCustomer
	protected Bandbox bandbox_OrderDialog_CustomerSearch; // autowired
	protected Textbox tb_Orders_SearchCustNo; // autowired
	protected Textbox tb_Orders_CustSearchMatchcode; // autowired
	protected Textbox tb_Orders_SearchCustName1; // autowired
	protected Textbox tb_Orders_SearchCustCity; // autowired
	protected Paging paging_OrderDialog_CustomerSearchList; // autowired
	protected Listbox listBoxCustomerSearch; // autowired
	protected Listheader listheader_CustNo_2; // autowired
	protected Listheader listheader_CustMatchcode_2; // autowired
	protected Listheader listheader_CustName1_2; // autowired
	protected Listheader listheader_CustCity_2; // autowired

	// not wired vars
	private transient Order order; // overhanded per param
	private transient Listbox listBoxOrder; // overhanded per param
	private transient OrderListCtrl orderListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_kunNr;
	private transient String oldVar_kunName1;
	private transient String oldVar_aufNr;
	private transient String oldVar_aufBezeichnung;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_OrderDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire

	protected Button btnHelp; // autowire
	protected Button button_OrderDialog_PrintOrder; // autowire
	protected Button button_OrderDialog_NewOrderPosition; // autowire

	private int pageSizeOrderPosition;
	private int pageSizeSearchCustomer;

	// ServiceDAOs / Domain Classes
	private transient Customer customer;
	private transient OrderService orderService;
	private transient CustomerService customerService;
	private transient BrancheService brancheService;

	/**
	 * default constructor.<br>
	 */
	public OrderDialogCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected user object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$orderDialogWindow(Event event) throws Exception {
		setPageSizeOrderPosition(10);
		setPageSizeSearchCustomer(20);

		/* set components visible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, btnNew, btnEdit, btnDelete, btnSave, btnCancel, btnClose);

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("order")) {
			Order anOrder = (Order) args.get("order");
			setOrder(anOrder);
			// we must addionally check if there is NO customer object in
			// the order, so its new.
			if (anOrder.getCustomer() != null) {
				setCustomer(anOrder.getCustomer());
			}
		} else {
			setOrder(null);
		}

		// we get the listBox Object for the offices list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete offices here.
		if (args.containsKey("listBoxOrder")) {
			listBoxOrder = (Listbox) args.get("listBoxOrder");
		} else {
			listBoxOrder = null;
		}

		if (args.containsKey("orderListCtrl")) {
			orderListCtrl = (OrderListCtrl) args.get("orderListCtrl");
		} else {
			orderListCtrl = null;
		}

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_OrderPosList2_Orderpos_No.setSortAscending(new FieldComparator("aupId", true));
		listheader_OrderPosList2_Orderpos_No.setSortDescending(new FieldComparator("aupId", false));
		listheader_OrderPosList2_Shorttext.setSortAscending(new FieldComparator("article.artKurzbezeichnung", true));
		listheader_OrderPosList2_Shorttext.setSortDescending(new FieldComparator("article.artKurzbezeichnung", false));
		listheader_OrderPosList2_Count.setSortAscending(new FieldComparator("aupMenge", true));
		listheader_OrderPosList2_Count.setSortDescending(new FieldComparator("aupMenge", false));
		listheader_OrderPosList2_SinglePrice.setSortAscending(new FieldComparator("aupEinzelwert", true));
		listheader_OrderPosList2_SinglePrice.setSortDescending(new FieldComparator("aupEinzelwert", false));
		listheader_OrderPosList2_WholePrice.setSortAscending(new FieldComparator("aupGesamtwert", true));
		listheader_OrderPosList2_WholePrice.setSortDescending(new FieldComparator("aupGesamtwert", false));

		paging_ListBoxOrderOrderPositions.setPageSize(pageSizeOrderPosition);
		paging_ListBoxOrderOrderPositions.setDetailed(true);

		// Set the ListModel for the orderPositions.
		if (getOrder() != null) {
			if (!getOrder().isNew()) {
				HibernateSearchObject<Orderposition> soOrderPosition = new HibernateSearchObject<Orderposition>(Orderposition.class, pageSizeOrderPosition);
				soOrderPosition.addFilter(new Filter("order", getOrder(), Filter.OP_EQUAL));
				// deeper loading of the relation to prevent the lazy
				// loading problem.
				soOrderPosition.addFetch("article");

				// Set the ListModel.
				getPlwOrderpositions().init(soOrderPosition, listBoxOrderOrderPositions, paging_ListBoxOrderOrderPositions);

			}
		}
		listBoxOrderOrderPositions.setItemRenderer(new OrderpositionListModelItemRenderer());

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getOrder());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		orderDialogWindow.setVisible(workspace.isAllowed("orderDialogWindow"));

		btnHelp.setVisible(workspace.isAllowed("button_OrderDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_OrderDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_OrderDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_OrderDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_OrderDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_OrderDialog_btnClose"));

		button_OrderDialog_PrintOrder.setVisible(workspace.isAllowed("button_OrderDialog_PrintOrder"));
		button_OrderDialog_NewOrderPosition.setVisible(workspace.isAllowed("button_OrderDialog_NewOrderPosition"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$orderDialogWindow(Event event) throws Exception {
		// logger.debug(event.toString());

		doClose();

	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		doSave();
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		// logger.debug(event.toString());

		doEdit();
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		// logger.debug(event.toString());

		doNew();
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
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		doDelete();
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		// logger.debug(event.toString());

		doCancel();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		try {
			doClose();
		} catch (final Exception e) {
			// close anyway
			orderDialogWindow.onClose();
			// Messagebox.show(e.toString());
		}
	}

	/**
	 * when 'print order' is clicked.<br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_OrderDialog_PrintOrder(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		// doPrintReport();
		doPrintOrderReport(event);
	}

	private void doPrintOrderReport(Event event) throws InterruptedException {

		Order anOrder = getOrder();

		if (anOrder == null || anOrder.isNew()) {
			ZksampleMessageUtils.showErrorMessage(Labels.getLabel("message.error.PleaseFirstCreateOrder"));
			return;
		}

		Window win = (Window) Path.getComponent("/outerIndexWindow");

		try {
			new OrderDJReport(win, anOrder);
		} catch (final InterruptedException e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}

	}

	/**
	 * when the "new order position" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_OrderDialog_NewOrderPosition(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		if (getOrder() == null || getOrder().isNew()) {
			ZksampleMessageUtils.showErrorMessage(Labels.getLabel("message.error.PleaseFirstCreateOrder"));
			doCancel();
			return;
		}

		// create a new orderPosition object
		Orderposition anOrderposition = getOrderService().getNewOrderposition();

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("orderListCtrl", orderListCtrl);
		map.put("order", getOrder());
		map.put("orderposition", anOrderposition);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */
		map.put("listBoxOrderOrderPositions", listBoxOrderOrderPositions);
		map.put("orderDialogCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/order/orderPositionDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());

			// Show a error box
			String msg = e.getMessage();
			String title = Labels.getLabel("message.Error");

			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "ERROR", true);

		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		doResetInitValues();
		doReadOnly();
		btnCtrl.setInitEdit();
	}

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 */
	private void doClose() throws Exception {

		if (isDataChanged()) {

			// Show a confirm box
			String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true, new EventListener() {
				@Override
				public void onEvent(Event evt) {
					switch (((Integer) evt.getData()).intValue()) {
					case MultiLineMessageBox.YES:
						try {
							doSave();
						} catch (final InterruptedException e) {
							throw new RuntimeException(e);
						}
					case MultiLineMessageBox.NO:
						break; //
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}
		}

		orderDialogWindow.onClose();
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param anOrder
	 * @throws InterruptedException
	 */
	public void doShowDialog(Order anOrder) throws InterruptedException {

		// if anOrder == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (anOrder == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			anOrder = getOrderService().getNewOrder();
		}

		// set Readonly mode accordingly if the object is new or not.
		if (anOrder.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			if (anOrder.getCustomer() != null) {
				kunNr.setValue(anOrder.getCustomer().getKunNr());
				String substr = anOrder.getCustomer().getKunName1() + " " + anOrder.getCustomer().getKunName2() + ", " + anOrder.getCustomer().getKunOrt();
				substr = StringUtils.substring(substr, 0, 49);
				kunName1.setValue(substr);

			}
			aufNr.setValue(anOrder.getAufNr());
			aufBezeichnung.setValue(anOrder.getAufBezeichnung());

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			orderDialogWindow.doModal(); // open the dialog in modal mode
		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		kunNr.setMaxlength(20);
		kunName1.setMaxlength(150);
		aufNr.setMaxlength(20);
		aufBezeichnung.setMaxlength(50);
	}

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		oldVar_kunNr = kunNr.getValue();
		oldVar_kunName1 = kunName1.getValue();
		oldVar_aufNr = aufNr.getValue();
		oldVar_aufBezeichnung = aufBezeichnung.getValue();
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		kunNr.setValue(oldVar_kunNr);
		kunName1.setValue(oldVar_kunName1);
		aufNr.setValue(oldVar_aufNr);
		aufBezeichnung.setValue(oldVar_aufBezeichnung);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_kunNr != kunNr.getValue()) {
			changed = true;
		}
		if (oldVar_kunName1 != kunName1.getValue()) {
			changed = true;
		}
		if (oldVar_aufNr != aufNr.getValue()) {
			changed = true;
		}
		if (oldVar_aufBezeichnung != aufBezeichnung.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		kunNr.setConstraint(new SimpleConstraint("NO EMPTY"));
		kunName1.setConstraint("NO EMPTY");
		aufNr.setConstraint("NO EMPTY");
		aufBezeichnung.setConstraint("NO EMPTY");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		kunNr.setConstraint("");
		kunName1.setConstraint("");
		aufNr.setConstraint("");
		aufBezeichnung.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a order object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {

		final Order order = getOrder();

		// Show a confirm box
		String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
		String title = Labels.getLabel("message.Deleting.Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true, new EventListener() {
			@Override
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case MultiLineMessageBox.YES:
					try {
						delete();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				case MultiLineMessageBox.NO:
					break; //
				}
			}

			// delete from database
			private void delete() throws InterruptedException {

				try {
					getOrderService().delete(order);
				} catch (DataAccessException e) {
					ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
				}

				// now synchronize the listBox in the parent zul-file
				ListModelList lml = (ListModelList) listBoxOrder.getListModel();

				// Check if the branch object is new or updated
				// -1 means that the obj is not in the list, so it's
				// new.
				if (lml.indexOf(order) == -1) {
				} else {
					lml.remove(lml.indexOf(order));
				}

				orderDialogWindow.onClose(); // close
				// the
				// dialog
			}
		}

		) == MultiLineMessageBox.YES) {
		}

	}

	/**
	 * Create a new order object. <br>
	 */
	private void doNew() {

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		setOrder(getOrderService().getNewOrder());

		doClear(); // clear all commponents
		doEdit(); // edit mode

		// btnCtrl.setBtnStatus_New();
		btnCtrl.setInitNew();

		// remember the old vars
		doStoreInitValues();
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		// kunNr only be filled by searchBandBox
		kunNr.setDisabled(true);
		bandbox_OrderDialog_CustomerSearch.setDisabled(false);
		// kunName1.setReadonly(false);
		aufNr.setReadonly(false);
		aufBezeichnung.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {

		kunNr.setDisabled(true);
		bandbox_OrderDialog_CustomerSearch.setDisabled(true);
		kunName1.setReadonly(true);
		aufNr.setReadonly(true);
		aufBezeichnung.setReadonly(true);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		kunNr.setValue("");
		bandbox_OrderDialog_CustomerSearch.setValue("");
		kunName1.setValue("");
		aufNr.setValue("");
		aufBezeichnung.setValue("");

		// clear the listbox if data exists
		ListModelList lml = (ListModelList) this.listBoxOrderOrderPositions.getModel();
		if (lml != null)
			lml.clear();

		// doSetValidation();
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		Order anOrder = getOrder();
		Customer aCustomer = getCustomer();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		kunNr.getValue();
		kunName1.getValue();
		// bbox_Orders_CustomerSearch.getValue();

		// fill the order object with the components data
		anOrder.setCustomer(aCustomer);
		anOrder.setAufNr(aufNr.getValue());
		anOrder.setAufBezeichnung(aufBezeichnung.getValue());

		// save it to database
		try {
			getOrderService().saveOrUpdate(anOrder);
		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetInitValues();

			doReadOnly();
			btnCtrl.setBtnStatus_Save();
			return;
		}

		// now synchronize the offices listBox
		ListModelList lml = (ListModelList) listBoxOrder.getListModel();

		// Check if the object is new or updated
		// -1 means that the object is not in the list, so its new.
		if (lml.indexOf(anOrder) == -1) {
			lml.add(anOrder);
		} else {
			lml.set(lml.indexOf(anOrder), anOrder);
		}

		// bind the vars new for updating the components
		// officeCtrl.doBindNew();

		doReadOnly();
		btnCtrl.setBtnStatus_Save();
		// init the old values vars new
		doStoreInitValues();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++ Order Positions operations ++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when an item in the order positions list is double clicked. <br>
	 * 
	 * @param event
	 */
	public void onDoubleClickedOrderPositionItem(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		// get the selected object
		Listitem item = listBoxOrderOrderPositions.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			Orderposition anOrderposition = (Orderposition) item.getValue();

			/*
			 * We can call our Dialog zul-file with parameters. So we can call
			 * them with a object of the selected item. For handed over these
			 * parameter only a Map is accepted. So we put the object in a
			 * HashMap.
			 */
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("orderListCtrl", orderListCtrl);
			map.put("order", getOrder());
			map.put("orderposition", anOrderposition);
			/*
			 * we can additionally handed over the listBox, so we have in the
			 * dialog access to the listbox Listmodel. This is fine for
			 * syncronizing the data in the customerListbox from the dialog when
			 * we do a delete, edit or insert a customer.
			 */
			map.put("listBoxOrderOrderPositions", listBoxOrderOrderPositions);
			map.put("orderDialogCtrl", this);

			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/order/orderPositionDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());

				// Show a error box
				String msg = e.getMessage();
				String title = Labels.getLabel("message.Error");
				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, "ERROR", true);

			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search Customer +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Close(Event event) {
		// logger.debug(event.toString());

		bandbox_OrderDialog_CustomerSearch.close();
	}

	/**
	 * when the "new" button is clicked.
	 * 
	 * Calls the Customer dialog.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_NewCustomer(Event event) {
		// logger.debug(event.toString());

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		Customer aCustomer = getCustomerService().getNewCustomer();
		aCustomer.setOffice(getUserWorkspace().getOffice()); // init
		// customer.setBranche(Workspace.getBranche()); // init
		aCustomer.setBranche(getBrancheService().getBrancheById(new Integer(1033).longValue())); // init
		aCustomer.setKunMahnsperre(false); // init

		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customer", aCustomer);
		/*
		 * we can additionally handed over the listBox, so we have in the dialog
		 * access to the listbox Listmodel. This is fine for syncronizing the
		 * data in the customerListbox from the dialog when we do a delete, edit
		 * or insert a customer.
		 */

		// call the zul-file with the parameters packed in a map
		Executions.createComponents("/WEB-INF/pages/customer/customerDialog.zul", null, map);
	}

	/**
	 * when the "search/filter" button in the bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_CustomerSearch_Search(Event event) {
		// logger.debug(event.toString());

		doSearch();
	}

	public void onOpen$bandbox_OrderDialog_CustomerSearch(Event event) throws Exception {
		// logger.debug(event.toString());

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Customer> soCustomer = new HibernateSearchObject<Customer>(Customer.class);
		soCustomer.addSort("kunName1", false);

		// set the paging params
		paging_OrderDialog_CustomerSearchList.setPageSize(pageSizeSearchCustomer);
		paging_OrderDialog_CustomerSearchList.setDetailed(true);

		// Set the ListModel.
		getPlwCustomers().init(soCustomer, listBoxCustomerSearch, paging_OrderDialog_CustomerSearchList);
		// set the itemRenderer
		listBoxCustomerSearch.setItemRenderer(new OrderSearchCustomerListModelItemRenderer());
	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Create a map with the count entries. <br>
	 * 2. Store the propertynames and values to the map. <br>
	 * 3. Call the ServiceDAO method with the map as parameter. <br>
	 */
	private void doSearch() {

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Customer> soCustomer = new HibernateSearchObject<Customer>(Customer.class);

		if (StringUtils.isNotEmpty(tb_Orders_SearchCustNo.getValue())) {
			soCustomer.addFilter(new Filter("kunNr", tb_Orders_SearchCustNo.getValue(), Filter.OP_EQUAL));
		}
		if (StringUtils.isNotEmpty(tb_Orders_CustSearchMatchcode.getValue())) {
			soCustomer.addFilter(new Filter("kunMatchcode", "%" + tb_Orders_CustSearchMatchcode.getValue() + "%", Filter.OP_ILIKE));
		}
		if (StringUtils.isNotEmpty(tb_Orders_SearchCustName1.getValue())) {
			soCustomer.addFilter(new Filter("kunName1", "%" + tb_Orders_SearchCustName1.getValue() + "%", Filter.OP_ILIKE));
		}
		if (StringUtils.isNotEmpty(tb_Orders_SearchCustCity.getValue())) {
			soCustomer.addFilter(new Filter("kunOrt", "%" + tb_Orders_SearchCustCity.getValue() + "%", Filter.OP_ILIKE));
		}
		soCustomer.addSort("kunName1", false);

		// set the paging params
		paging_OrderDialog_CustomerSearchList.setPageSize(pageSizeSearchCustomer);
		paging_OrderDialog_CustomerSearchList.setDetailed(true);

		// Set the ListModel.
		getPlwCustomers().init(soCustomer, listBoxCustomerSearch, paging_OrderDialog_CustomerSearchList);
		// set the itemRenderer
		listBoxCustomerSearch.setItemRenderer(new OrderSearchCustomerListModelItemRenderer());

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
		Listitem item = listBoxCustomerSearch.getSelectedItem();
		if (item != null) {

			Customer aCustomer = (Customer) item.getAttribute("data");
			setCustomer(aCustomer);

			kunNr.setValue(getCustomer().getKunNr());
			bandbox_OrderDialog_CustomerSearch.setValue(getCustomer().getKunNr());
			kunName1.setValue(getCustomer().getKunName1() + " " + getCustomer().getKunName2() + ", " + getCustomer().getKunOrt());
		}

		// close the bandbox
		bandbox_OrderDialog_CustomerSearch.close();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public OrderService getOrderService() {
		return this.orderService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return this.customerService;
	}

	public BrancheService getBrancheService() {
		return this.brancheService;
	}

	public void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Listbox getListBoxOrderOrderPositions() {
		return this.listBoxOrderOrderPositions;
	}

	public void setListBoxOrderOrderPositions(Listbox listBoxOrderOrderPositions) {
		this.listBoxOrderOrderPositions = listBoxOrderOrderPositions;
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

	public int getPageSizeOrderPosition() {
		return this.pageSizeOrderPosition;
	}

	public void setPageSizeOrderPosition(int pageSizeOrderPosition) {
		this.pageSizeOrderPosition = pageSizeOrderPosition;
	}

	public int getPageSizeSearchCustomer() {
		return this.pageSizeSearchCustomer;
	}

	public void setPageSizeSearchCustomer(int pageSizeSearchCustomer) {
		this.pageSizeSearchCustomer = pageSizeSearchCustomer;
	}
}
