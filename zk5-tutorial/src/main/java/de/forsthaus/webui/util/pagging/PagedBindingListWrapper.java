package de.forsthaus.webui.util.pagging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;

import com.trg.search.SearchResult;

import de.forsthaus.backend.service.PagedListService;
import de.forsthaus.backend.util.HibernateSearchObject;

/**
 * Helper class for getting a paged record list that are be sorted on the
 * database and reacts on clicking the listheaders. <br>
 * Look at the <b>OnSortEventListener</b> and <b>OnPagingEventListener</b>.<br>
 * <br>
 * This class works with a BindingListModelList for working with the zk's
 * databinding mechanism. <br>
 * 
 * All not used Listheaders must me declared as: <br>
 * listheader.setSortAscending(""); <br>
 * listheader.setSortDescending(""); <br>
 * 
 * <br>
 * zkoss 3.6.0 or greater (by using FieldComparator) <br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/02/2010: sge Changed to BindingListModelList.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class PagedBindingListWrapper<E> extends BindingListModelList implements Serializable {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(PagedBindingListWrapper.class);

	// Service that calls the DAO methods
	private PagedListService pagedListService;

	// param. The listboxes paging component
	private Paging paging;

	// The Listbox component
	private Listbox listbox;

	// param. The SearchObject, holds the entity and properties to search. <br>
	private HibernateSearchObject<E> hibernateSearchObject;

	/**
	 * default constructor.<br>
	 */
	public PagedBindingListWrapper() {
		super(new ArrayList<E>(), true);
	}

	public void init(HibernateSearchObject<E> hibernateSearchObject1, Listbox listBox, Paging paging1) {
		setPaging(paging1);
		setListeners(listBox);
		setListbox(listBox);

		setSearchObject(hibernateSearchObject1);
	}

	private void initModel() {
		getSearchObject().setFirstResult(0);
		getSearchObject().setMaxResults(getPageSize());

		// clear old data
		clear();

		final SearchResult<E> searchResult = getPagedListService().getSRBySearchObject(getSearchObject());
		getPaging().setTotalSize(searchResult.getTotalCount());
		addAll(searchResult.getResult());
	}

	/**
	 * Refreshes the list by calling the DAO methode with the modified search
	 * object. <br>
	 * 
	 * @param start
	 *            Row to start. <br>
	 */
	void refreshModel(int start) {
		getSearchObject().setFirstResult(start);
		getSearchObject().setMaxResults(getPageSize());

		// clear old data
		clear();

		addAll(getPagedListService().getBySearchObject(getSearchObject()));
	}

	public void clearFilters() {
		getSearchObject().clearFilters();
		initModel();
	}

	/**
	 * Sets the listeners. <br>
	 * <br>
	 * 1. "onPaging" for the paging component. <br>
	 * 2. "onSort" for all listheaders that have a sortDirection declared. <br>
	 * All not used Listheaders must me declared as:
	 * listheader.setSortAscending(""); listheader.setSortDescending(""); <br>
	 */
	private void setListeners(Listbox listBox) {

		// Add 'onPaging' listener to the paging component
		getPaging().addEventListener("onPaging", new OnPagingEventListener());

		final Listhead listhead = listBox.getListhead();
		final List<?> list = listhead.getChildren();

		final OnSortEventListener onSortEventListener = new OnSortEventListener();
		for (final Object object : list) {
			if (object instanceof Listheader) {
				final Listheader lheader = (Listheader) object;

				if (lheader.getSortAscending() != null || lheader.getSortDescending() != null) {
					lheader.addEventListener("onSort", onSortEventListener);
				}
			}
		}
		listBox.setModel(this);
	}

	/**
	 * "onPaging" EventListener for the paging component. <br>
	 * <br>
	 * Calculates the next page by currentPage and pageSize values. <br>
	 * Calls the method for refreshing the data with the new rowStart and
	 * pageSize. <br>
	 */
	public final class OnPagingEventListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			// deselect selectedBean
			// TODO don't work in expected way
			// evtl. mittels Reflection den zu uebergebenen selectedItem bean
			// mit setXXXXX(null) setzen.
			final Listitem li = getListbox().getSelectedItem();
			if (li != null) {
				li.setSelected(false);
			}

			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();

			// refresh the list
			refreshModel(start);
		}
	}

	/**
	 * "onSort" eventlistener for the listheader components. <br>
	 * <br>
	 * Checks wich listheader is clicked and checks which orderDirection must be
	 * set. <br>
	 * 
	 * Calls the methode for refreshing the data with the new ordering. and the
	 * remembered rowStart and pageSize. <br>
	 */
	public final class OnSortEventListener implements EventListener, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void onEvent(Event event) throws Exception {
			final Listheader lh = (Listheader) event.getTarget();
			final String sortDirection = lh.getSortDirection();

			if ("ascending".equals(sortDirection)) {
				final Comparator<?> cmpr = lh.getSortDescending();
				if (cmpr instanceof FieldComparator) {
					String orderBy = ((FieldComparator) cmpr).getOrderBy();
					orderBy = StringUtils.substringBefore(orderBy, "DESC").trim();

					// update SearchObject with orderBy
					getSearchObject().clearSorts();
					getSearchObject().addSort(orderBy, true);
				}
			} else if ("descending".equals(sortDirection) || "natural".equals(sortDirection) || Strings.isBlank(sortDirection)) {
				final Comparator<?> cmpr = lh.getSortAscending();
				if (cmpr instanceof FieldComparator) {
					String orderBy = ((FieldComparator) cmpr).getOrderBy();
					orderBy = StringUtils.substringBefore(orderBy, "ASC").trim();

					// update SearchObject with orderBy
					getSearchObject().clearSorts();
					getSearchObject().addSort(orderBy, false);
				}
			}

			/**
			 * A changing of the sort order implies that the list starts new. So
			 * we set the startpage to '0' and refresh the list.
			 */
			getPaging().setActivePage(0);
			refreshModel(0);

		}
	}

	public PagedListService getPagedListService() {
		return this.pagedListService;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	HibernateSearchObject<E> getSearchObject() {
		return this.hibernateSearchObject;
	}

	public void setSearchObject(HibernateSearchObject<E> hibernateSearchObject1) {
		this.hibernateSearchObject = hibernateSearchObject1;
		initModel();
	}

	public int getPageSize() {
		return getPaging().getPageSize();
	}

	private void setPaging(Paging paging) {
		this.paging = paging;
	}

	Paging getPaging() {
		return this.paging;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setListbox(Listbox listbox) {
		this.listbox = listbox;
	}

	public Listbox getListbox() {
		return this.listbox;
	}

}
