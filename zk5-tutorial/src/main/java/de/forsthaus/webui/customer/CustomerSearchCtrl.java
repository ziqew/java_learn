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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.CustomerService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.customer.model.CustomerBrancheListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.pagging.PagedListWrapper;
import de.forsthaus.webui.util.searching.SearchOperatorListModelItemRenderer;
import de.forsthaus.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/customer/customerSearchDialog.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 07/24/2009:sge changings for clustering.<br>
 *          11/07/2009:bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          03/18/2010:sge added a result counter next buttons.<br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class CustomerSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6320398861070378344L;
	private final static Logger logger = Logger.getLogger(CustomerSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window customerSearchWindow; // autowired
	protected Listbox sortOperator_kunNr; // autowired
	protected Textbox kunNr; // autowired
	protected Listbox sortOperator_kunMatchcode; // autowired
	protected Textbox kunMatchcode; // autowired
	protected Listbox sortOperator_kunName1; // autowired
	protected Textbox kunName1; // autowired
	protected Listbox sortOperator_kunName2; // autowired
	protected Textbox kunName2; // autowired
	protected Listbox sortOperator_kunOrt; // autowired
	protected Textbox kunOrt; // autowired
	protected Listbox sortOperator_kunBranch; // autowired
	protected Listbox kunBranche; // autowired
	protected Label labelCustomerSearchResult; // autowired

	// not auto wired vars
	private transient CustomerListCtrl customerCtrl; // overhanded per param

	private transient BrancheService brancheService;
	private transient CustomerService customerService;

	/**
	 * constructor
	 */
	public CustomerSearchCtrl() {
		super();
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$customerSearchWindow(Event event) throws Exception {
		// doOnCreateCommon(customerSearchWindow, event); // autowire the vars

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customerCtrl")) {
			customerCtrl = (CustomerListCtrl) args.get("customerCtrl");
		} else {
			customerCtrl = null;
		}

		// TODO chnagwed to ListPagedWrapper
		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		// set listModel and itemRenderer for the Branch dropdown listbox
		kunBranche.setModel(new ListModelList(getBrancheService().getAllBranches()));
		kunBranche.setItemRenderer(new CustomerBrancheListModelItemRenderer());

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
		// set listModel and itemRenderer for the search operator type listboxes
		sortOperator_kunNr.setModel(new ListModelList(new SearchOperators().getAllOperators(), true));
		sortOperator_kunNr.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunMatchcode.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunMatchcode.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunName1.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunName1.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunName2.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunName2.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunOrt.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunOrt.setItemRenderer(new SearchOperatorListModelItemRenderer());
		sortOperator_kunBranch.setModel(new ListModelList(new SearchOperators().getAllOperators()));
		sortOperator_kunBranch.setItemRenderer(new SearchOperatorListModelItemRenderer());

		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final HibernateSearchObject<Customer> searchObj = (HibernateSearchObject<Customer>) args.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
				if (filter.getProperty().equals("kunNr")) {
					restoreOperator(sortOperator_kunNr, filter);
					kunNr.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunMatchcode")) {
					restoreOperator(sortOperator_kunMatchcode, filter);
					kunMatchcode.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunName1")) {
					restoreOperator(sortOperator_kunName1, filter);
					kunName1.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunName2")) {
					restoreOperator(sortOperator_kunName2, filter);
					kunName2.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("kunOrt")) {
					restoreOperator(sortOperator_kunOrt, filter);
					kunOrt.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("branche")) {
					restoreOperator(sortOperator_kunBranch, filter);
					ListModelList lml = (ListModelList) this.kunBranche.getModel();
					// get and select the customers branch
					Branche branche = (Branche) filter.getValue();
					kunBranche.setSelectedIndex(lml.indexOf(branche));
				}
			}
		}

		showCustomerSeekDialog();

	}

	/**
	 * Restore the operator sign in the operator listbox by comparing the <br>
	 * value of the filter. <br>
	 * 
	 * @param listbox
	 *            Listbox that shows the operator signs.
	 * @param filter
	 *            Filter that corresponds to the operator listbox.
	 */
	private void restoreOperator(Listbox listbox, Filter filter) {
		if (filter.getOperator() == Filter.OP_EQUAL) {
			listbox.setSelectedIndex(1);
		} else if (filter.getOperator() == Filter.OP_NOT_EQUAL) {
			listbox.setSelectedIndex(2);
		} else if (filter.getOperator() == Filter.OP_LESS_THAN) {
			listbox.setSelectedIndex(3);
		} else if (filter.getOperator() == Filter.OP_GREATER_THAN) {
			listbox.setSelectedIndex(4);
		} else if (filter.getOperator() == Filter.OP_LESS_OR_EQUAL) {
			listbox.setSelectedIndex(5);
		} else if (filter.getOperator() == Filter.OP_GREATER_OR_EQUAL) {
			listbox.setSelectedIndex(6);
		} else if (filter.getOperator() == Filter.OP_ILIKE) {
			// Delete used '%' signs if the operator is like or iLike
			String str = StringUtils.replaceChars(filter.getValue().toString(), "%", "");
			filter.setValue(str);
			listbox.setSelectedIndex(7);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug(event.toString());

		doSearch();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		doClose();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		customerSearchWindow.onClose();
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showCustomerSeekDialog() throws InterruptedException {

		try {
			// open the dialog in modal mode
			customerSearchWindow.doModal();
		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textbox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */
	@SuppressWarnings("unchecked")
	public void doSearch() {

		HibernateSearchObject<Customer> so = new HibernateSearchObject(Customer.class);

		if (StringUtils.isNotEmpty(kunNr.getValue())) {

			// get the search operator
			Listitem item = this.sortOperator_kunNr.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunNr", "%" + kunNr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunNr", kunNr.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(kunMatchcode.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunMatchcode.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunMatchcode", "%" + kunMatchcode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunMatchcode", kunMatchcode.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(kunName1.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunName1.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunName1", "%" + kunName1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunName1", kunName1.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.kunName2.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunName2.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunName2", "%" + kunName2.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunName2", kunName2.getValue(), searchOpId));
				}
			}
		}

		if (StringUtils.isNotEmpty(this.kunOrt.getValue())) {

			// get the search operator
			Listitem item = sortOperator_kunOrt.getSelectedItem();

			if (item != null) {
				int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_ILIKE) {
					so.addFilter(new Filter("kunOrt", "%" + kunOrt.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("kunOrt", kunOrt.getValue(), searchOpId));
				}
			}
		}

		if (this.kunBranche.getSelectedCount() > 0) {

			// check if it the default empty item
			Listitem itemB = kunBranche.getSelectedItem();
			Branche branche = (Branche) itemB.getAttribute("data");

			if (!StringUtils.isEmpty(branche.getBraBezeichnung())) {

				// get the search operator
				Listitem item = this.sortOperator_kunBranch.getSelectedItem();

				if (item != null) {
					int searchOpId = ((SearchOperators) item.getAttribute("data")).getSearchOperatorId();

					if (searchOpId == Filter.OP_ILIKE) {
						so.addFilter(new Filter("branche", branche, searchOpId));
					} else if (searchOpId == -1) {
						// do nothing
					} else {
						so.addFilter(new Filter("branche", branche, searchOpId));
					}
				}
			}
		}

		if (logger.isDebugEnabled()) {
			List<Filter> lf = so.getFilters();
			for (Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		customerCtrl.setSearchObj(so);

		Listbox listBox = customerCtrl.listBoxCustomer;
		Paging paging = customerCtrl.pagingCustomerList;
		int ps = customerCtrl.pagingCustomerList.getPageSize();

		// set the model to the listbox with the initial resultset get by the
		// DAO method.
		((PagedListWrapper<Customer>) listBox.getModel()).init(so, listBox, paging);

		labelCustomerSearchResult.setValue(Labels.getLabel("labelCustomerSearchResult.value") + " " + String.valueOf(paging.getTotalSize()));
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

}
