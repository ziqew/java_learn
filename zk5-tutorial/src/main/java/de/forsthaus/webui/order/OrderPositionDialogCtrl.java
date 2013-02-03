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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
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
import de.forsthaus.backend.model.Article;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.model.Order;
import de.forsthaus.backend.model.Orderposition;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.CustomerService;
import de.forsthaus.backend.service.OrderService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.order.model.SearchArticleListModelItemRenderer;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;
import de.forsthaus.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/order/orderPositionDialog.zul file.<br>
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
 * 
 */
public class OrderPositionDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(OrderPositionDialogCtrl.class);

	private transient PagedListWrapper<Orderposition> plwOrderpositions;
	private transient PagedListWrapper<Article> plwArticles;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window orderPositionDialogWindow; // autowired

	// input area
	protected Textbox artNr; // autowired
	protected Textbox artKurzbezeichnung; // autowired
	protected Decimalbox aupMenge; // autowired
	protected Decimalbox aupEinzelwert; // autowired
	protected Decimalbox aupGesamtwert; // autowired

	// bandbock searchArticle
	protected Bandbox bandbox_OrderPositionDialog_ArticleSearch; // autowired
	protected Textbox tb_OrderPosition_SearchArticlelNo; // autowired
	protected Textbox tb_OrderPosition_SearchArticleDesc; // autowired

	// listbox articlesearch in bandbox
	private transient int pageSizeArticleSearch;
	protected Paging paging_ListBoxArticleSearch; // autowired
	protected Listbox listBoxArticleSearch; // autowired
	protected Listheader listheader_ArticleSearch_artNr; // autowired
	protected Listheader listheader_ArticleSearch_artKurzbezeichnung; // autowired
	protected Listheader listheader_ArticleSearch_aupEinzelwert; // autowired

	// search bandbox customer
	protected Bandbox bbox_Orders_CustomerSearch; // autowired
	protected Textbox tb_Orders_SearchCustNo; // autowired
	protected Textbox tb_Orders_CustSearchMatchcode; // autowired
	protected Textbox tb_Orders_SearchCustName1; // autowired
	protected Textbox tb_Orders_SearchCustCity; // autowired

	// overhanded vars from parent controller
	private transient Listbox listBoxOrderOrderPositions; // overhanded
	private transient Order order; // overhanded
	private transient Orderposition orderposition; // overhanded
	private transient OrderDialogCtrl orderDialogCtrl; // overhanded
	private transient OrderListCtrl orderListCtrl; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_artNr;
	private transient String oldVar_artKurzbezeichnung;
	private transient BigDecimal oldVar_aupMenge;
	private transient BigDecimal oldVar_aupEinzelwert;
	private transient BigDecimal oldVar_aupGesamtwert;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_RightPrefix = "button_OrderPositionDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire

	protected Button btnHelp; // autowire

	// ServiceDAOs / Domain Classes
	private transient Customer customer;
	private transient Article article;
	private transient OrderService orderService;
	private transient CustomerService customerService;
	private transient BrancheService brancheService;

	/**
	 * default constructor.<br>
	 */
	public OrderPositionDialogCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected user object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$orderPositionDialogWindow(Event event) throws Exception {
		/* set components visible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_RightPrefix, true, btnNew, btnEdit, btnDelete, btnSave, btnCancel, btnClose);

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("order")) {
			order = (Order) args.get("order");
		} else {
			setOrder(null);
		}

		if (args.containsKey("orderposition")) {
			orderposition = (Orderposition) args.get("orderposition");
			setOrderposition(orderposition);
			// we must addionally check if there is NO order object in the
			// orderPosition, so its new.
			if (orderposition.getOrder() != null) {
				setOrder(this.order);
				setArticle(this.orderposition.getArticle());
				setCustomer(this.order.getCustomer());
			}
		} else {
			setOrderposition(null);
		}

		// we get the listBox Object for the offices list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete orderPositions here.
		if (args.containsKey("listBoxOrderOrderPositions")) {
			listBoxOrderOrderPositions = (Listbox) args.get("listBoxOrderOrderPositions");
		} else {
			listBoxOrderOrderPositions = null;
		}

		if (args.containsKey("orderDialogCtrl")) {
			orderDialogCtrl = (OrderDialogCtrl) args.get("orderDialogCtrl");
		} else {
			orderDialogCtrl = null;
		}

		if (args.containsKey("orderListCtrl")) {
			orderListCtrl = (OrderListCtrl) args.get("orderListCtrl");
		} else {
			orderListCtrl = null;
		}

		setPageSizeArticleSearch(20);
		paging_ListBoxArticleSearch.setPageSize(getPageSizeArticleSearch());
		paging_ListBoxArticleSearch.setDetailed(true);

		/* Sorting Comparator for search bandbox article list */
		listheader_ArticleSearch_artNr.setSortAscending(new FieldComparator("artNr", true));
		listheader_ArticleSearch_artNr.setSortDescending(new FieldComparator("artNr", true));

		listheader_ArticleSearch_artKurzbezeichnung.setSortAscending(new FieldComparator("article.artKurzbezeichnung", true));
		listheader_ArticleSearch_artKurzbezeichnung.setSortDescending(new FieldComparator("article.artKurzbezeichnung", true));

		listBoxArticleSearch.setItemRenderer(new SearchArticleListModelItemRenderer());

		doShowDialog(getOrderposition());

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		orderPositionDialogWindow.setVisible(workspace.isAllowed("orderPositionDialogWindow"));

		btnHelp.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_OrderPositionDialog_btnClose"));

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
	public void onClose$orderPositionDialogWindow(Event event) throws Exception {
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
			orderPositionDialogWindow.onClose();
			// Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

		orderPositionDialogWindow.onClose();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		doResetInitValues();

		aupMenge.setReadonly(true);
		aupEinzelwert.setReadonly(true);
		aupGesamtwert.setReadonly(true);

		btnCtrl.setInitEdit();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param anOrderposition
	 *            Orderposition
	 */
	public void doWriteBeanToComponents(Orderposition anOrderposition) {

		artNr.setValue(anOrderposition.getArticle().getArtNr());
		artKurzbezeichnung.setValue(anOrderposition.getArticle().getArtKurzbezeichnung());
		aupMenge.setValue(anOrderposition.getAupMenge());
		aupEinzelwert.setValue(anOrderposition.getAupEinzelwert());
		aupGesamtwert.setValue(anOrderposition.getAupGesamtwert());

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param anOrderposition
	 */
	public void doWriteComponentsToBean(Orderposition anOrderposition) {

		Order anOrder = getOrder();
		Article anArticle = getArticle();

		anOrderposition.setOrder(anOrder);
		anOrderposition.setArticle(anArticle);
		anOrderposition.setAupMenge(aupMenge.getValue());
		anOrderposition.setAupEinzelwert(aupEinzelwert.getValue());
		anOrderposition.setAupGesamtwert(aupGesamtwert.getValue());

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param anOrderposition
	 * @throws InterruptedException
	 */
	public void doShowDialog(Orderposition anOrderposition) throws InterruptedException {

		// if aBranche == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (anOrderposition == null) {

			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			anOrderposition = getOrderService().getNewOrderposition();
		}

		try {

			if (anOrderposition.getOrder() != null) {
				// fill the components with the data
				doWriteBeanToComponents(anOrderposition);

			}

			// set Readonly mode accordingly if the object is new or not.
			if (anOrderposition.isNew()) {
				btnCtrl.setInitNew();
				doEdit();
			} else {
				btnCtrl.setInitEdit();
				doReadOnly();
			}

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			orderPositionDialogWindow.doModal(); // open the dialog in
			// modal
			// mode
		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {

		oldVar_artNr = artNr.getValue();
		oldVar_artKurzbezeichnung = artKurzbezeichnung.getValue();
		oldVar_aupMenge = aupMenge.getValue();
		oldVar_aupEinzelwert = aupEinzelwert.getValue();
		oldVar_aupGesamtwert = aupGesamtwert.getValue();
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {

		artNr.setValue(oldVar_artNr);
		artKurzbezeichnung.setValue(oldVar_artKurzbezeichnung);
		aupMenge.setValue(oldVar_aupMenge);
		aupEinzelwert.setValue(oldVar_aupEinzelwert);
		aupGesamtwert.setValue(oldVar_aupGesamtwert);
	}

	/**
	 * Calculates all necessary values new.
	 */
	private void doCalculate() {

		if (!(aupMenge.getValue() == null) && !(aupEinzelwert.getValue() == null)) {
			if (!aupMenge.getValue().equals(new BigDecimal(0)) && !aupEinzelwert.getValue().equals(new BigDecimal(0))) {

				BigDecimal count = aupMenge.getValue();
				BigDecimal singlePrice = aupEinzelwert.getValue();
				BigDecimal amount = count.multiply(singlePrice);

				aupGesamtwert.setValue(amount);
			}
		}
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_artNr != artNr.getValue()) {
			changed = true;
		}
		if (oldVar_artKurzbezeichnung != artKurzbezeichnung.getValue()) {
			changed = true;
		}

		if (oldVar_aupMenge != aupMenge.getValue()) {
			changed = true;
		}

		if (oldVar_aupEinzelwert != aupEinzelwert.getValue()) {
			changed = true;
		}

		if (oldVar_aupGesamtwert != aupGesamtwert.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		artNr.setConstraint(new SimpleConstraint("NO EMPTY"));
		aupMenge.setConstraint("NO EMPTY, NO ZERO");
		aupEinzelwert.setConstraint("NO EMPTY, NO ZERO");
		aupGesamtwert.setConstraint("NO EMPTY, NO ZERO");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		artNr.setConstraint("");
		aupMenge.setConstraint("");
		aupEinzelwert.setConstraint("");
		aupGesamtwert.setConstraint("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a orderPosition object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {

		// Show a confirm box
		String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + this.orderposition.getArticle().getArtKurzbezeichnung();
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
					getOrderService().delete(orderposition);
				} catch (DataAccessException e) {
					ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
				}

				// now synchronize the listBox in the parent zul-file
				final ListModelList lml = (ListModelList) listBoxOrderOrderPositions.getListModel();
				// Check if the orderPosition object is new or updated
				// -1 means that the obj is not in the list, so it's
				// new.
				if (lml.indexOf(orderposition) == -1) {
				} else {
					lml.remove(lml.indexOf(orderposition));
				}

				// +++++++ now synchronize the listBox in the parent
				// zul-file
				// +++ //
				Listbox listBoxOrderArticle = orderListCtrl.getListBoxOrderArticle();
				// now synchronize the orderposition listBox
				ListModelList lml3 = (ListModelList) listBoxOrderArticle.getListModel();
				// Check if the orderPosition object is new or updated
				// -1 means that the obj is not in the list, so it's
				// new.
				if (lml3.indexOf(orderposition) == -1) {
				} else {
					lml3.remove(lml3.indexOf(orderposition));
				}

				orderPositionDialogWindow.onClose(); // close
				// the
				// dialog
			}
		}

		) == MultiLineMessageBox.YES) {
		}

	}

	/**
	 * Create a new orderPosition object. <br>
	 */
	private void doNew() {

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		Orderposition anOrderposition = getOrderService().getNewOrderposition();
		setOrderposition(anOrderposition);
		anOrderposition.setOrder(order);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		// artNr + description are only be filled by searchBox
		artNr.setReadonly(true);
		artKurzbezeichnung.setReadonly(true);
		bandbox_OrderPositionDialog_ArticleSearch.setDisabled(false);

		aupMenge.setReadonly(false);
		aupEinzelwert.setReadonly(false);
		aupGesamtwert.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();

		// remember the old vars
		doStoreInitValues();
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {

		// artNr + description are only be filled by searchBox
		artNr.setReadonly(true);
		artKurzbezeichnung.setReadonly(true);
		bandbox_OrderPositionDialog_ArticleSearch.setDisabled(true);

		aupMenge.setReadonly(true);
		aupEinzelwert.setReadonly(true);
		aupGesamtwert.setReadonly(true);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {

		// remove validation, if there are a save before
		doRemoveValidation();

		artNr.setValue("");
		artKurzbezeichnung.setValue("");
		bandbox_OrderPositionDialog_ArticleSearch.setValue("");
		aupMenge.setValue(new BigDecimal(0));
		aupEinzelwert.setValue(new BigDecimal(0));
		aupGesamtwert.setValue(new BigDecimal(0));

	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		Orderposition anOrderposition = getOrderposition();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		artNr.getValue();

		// additionally calculate new
		if (isDataChanged()) {
			doCalculate();
		}

		// fill the objects with the components data
		doWriteComponentsToBean(anOrderposition);

		// save it to database
		try {
			getOrderService().saveOrUpdate(anOrderposition);
		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetInitValues();

			doReadOnly();
			btnCtrl.setBtnStatus_Save();
			return;
		}

		/** Synchronize the listbox in the OrderDialog */

		HibernateSearchObject<Orderposition> soOrderPosition = new HibernateSearchObject<Orderposition>(Orderposition.class, orderDialogCtrl.getPageSizeOrderPosition());
		soOrderPosition.addFilter(new Filter("order", getOrder(), Filter.OP_EQUAL));
		// deeper loading of a relation to prevent the lazy
		// loading problem.
		soOrderPosition.addFetch("article");

		// Set the ListModel.
		getPlwOrderpositions().init(soOrderPosition, orderDialogCtrl.listBoxOrderOrderPositions, orderDialogCtrl.paging_ListBoxOrderOrderPositions);

		/** Synchronize the OrderList */
		// Listbox listBoxOrderArticle = orderListCtrl.getListBoxOrderArticle();
		// listBoxOrderArticle.setModel(orderDialogCtrl.listBoxOrderOrderPositions.getModel());
		orderListCtrl.getListBoxOrderArticle().setModel(orderDialogCtrl.listBoxOrderOrderPositions.getModel());

		// synchronize the TotalCount from the paging component
		orderListCtrl.paging_OrderArticleList.setTotalSize(orderDialogCtrl.paging_ListBoxOrderOrderPositions.getTotalSize());

		doReadOnly();
		btnCtrl.setBtnStatus_Save();
		// init the old values vars new
		doStoreInitValues();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search Customer +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$bt_Orders_CustomerSearchClose(Event event) {
		// logger.debug(event.toString());

		bbox_Orders_CustomerSearch.close();
	}

	/**
	 * when the "new" button is clicked.
	 * 
	 * Calls the Customer dialog.
	 * 
	 * @param event
	 */
	public void onClick$bt_Orders_CustomerNew(Event event) {
		// logger.debug(event.toString());

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		Customer aCustomer = getCustomerService().getNewCustomer();
		aCustomer.setOffice(getUserWorkspace().getOffice()); // init
		// customer.setBranche(Workspace.getBranche()); // init
		// TODO get the init values from a setup configuration
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ bandbox search article +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * when the "close" button of the search bandbox is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_ArticleSearch_Close(Event event) {
		// logger.debug(event.toString());

		bandbox_OrderPositionDialog_ArticleSearch.close();
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$button_bbox_ArticleSearch_Search(Event event) {
		// logger.debug(event.toString());

		doSearchArticle();
	}

	public void onOpen$bandbox_OrderPositionDialog_ArticleSearch(Event event) throws Exception {
		// logger.debug(event.toString());

		// ++ create the searchObject and init sorting ++//
		// only in sample app init with all orders
		HibernateSearchObject<Article> soArticle = new HibernateSearchObject<Article>(Article.class, getPageSizeArticleSearch());
		soArticle.addSort("artNr", false);

		// Set the ListModel.
		getPlwArticles().init(soArticle, listBoxArticleSearch, paging_ListBoxArticleSearch);

	}

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Count how many textboxes are filled. <br>
	 * 2. Create a map with the count entries. <br>
	 * 3. Store the propertynames(must corresponds to the domain classes
	 * properties) and values to the map. <br>
	 * 4. Call the ServiceDAO method with the map as parameter. <br>
	 */
	private void doSearchArticle() {

		// ++ create the searchObject and init sorting ++//
		// only in sample app init with all orders
		HibernateSearchObject<Article> soArticle = new HibernateSearchObject<Article>(Article.class, getPageSizeArticleSearch());
		soArticle.addSort("artNr", false);

		if (StringUtils.isNotEmpty(tb_OrderPosition_SearchArticlelNo.getValue())) {
			soArticle.addFilter(new Filter("artNr", "%" + tb_OrderPosition_SearchArticlelNo.getValue() + "%", Filter.OP_ILIKE));
		}

		if (StringUtils.isNotEmpty(tb_OrderPosition_SearchArticleDesc.getValue())) {
			soArticle.addFilter(new Filter("artKurzbezeichnung", "%" + tb_OrderPosition_SearchArticleDesc.getValue() + "%", Filter.OP_ILIKE));
		}

		// Set the ListModel.
		getPlwArticles().init(soArticle, listBoxArticleSearch, paging_ListBoxArticleSearch);

	}

	/**
	 * when doubleClick on a item in the bandbox search list.<br>
	 * <br>
	 * Select the customer and search all orders for him.
	 * 
	 * @param event
	 */
	public void onDoubleClickedArticleItem(Event event) {
		// logger.debug(event.toString());

		// get the customer
		Listitem item = this.listBoxArticleSearch.getSelectedItem();
		if (item != null) {

			// get and cast the selected object
			setArticle((Article) item.getAttribute("data"));

			artNr.setValue(article.getArtNr());
			artKurzbezeichnung.setValue(article.getArtKurzbezeichnung());
			aupEinzelwert.setValue(article.getArtPreis());
		}

		// clear old stuff at end, because the NO EMPTY validation
		aupMenge.setValue(new BigDecimal(0));
		aupGesamtwert.setValue(new BigDecimal(0));

		// close the bandbox
		bandbox_OrderPositionDialog_ArticleSearch.close();
	}

	/**
	 * when click on button calculate. <br>
	 * <br>
	 * Calculate the count x singlePrice = amount
	 * 
	 * @param event
	 */
	public void onClick$button_OrderPositionDialog_Calculate(Event event) {
		// logger.debug(event.toString());

		doCalculate();
	}

	/**
	 * close the article search bandbox. <br>
	 * 
	 * @param event
	 */
	public void onClick$btn_ArticleSearchClose(Event event) {
		// logger.debug(event.toString());

		// close the bandbox
		bandbox_OrderPositionDialog_ArticleSearch.close();
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Article getArticle() {
		return this.article;
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

	public void setOrder(Order order) {
		this.order = order;
	}

	public Order getOrder() {
		return this.order;
	}

	public void setOrderposition(Orderposition orderposition) {
		this.orderposition = orderposition;
	}

	public Orderposition getOrderposition() {
		return this.orderposition;
	}

	public void setPageSizeArticleSearch(int pageSizeArticleSearch) {
		this.pageSizeArticleSearch = pageSizeArticleSearch;
	}

	public int getPageSizeArticleSearch() {
		return this.pageSizeArticleSearch;
	}

	public void setPlwOrderpositions(PagedListWrapper<Orderposition> plwOrderpositions) {
		this.plwOrderpositions = plwOrderpositions;
	}

	public PagedListWrapper<Orderposition> getPlwOrderpositions() {
		return this.plwOrderpositions;
	}

	public void setPlwArticles(PagedListWrapper<Article> plwArticles) {
		this.plwArticles = plwArticles;
	}

	public PagedListWrapper<Article> getPlwArticles() {
		return this.plwArticles;
	}
}
