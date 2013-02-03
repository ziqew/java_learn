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
package de.forsthaus.webui.util.pagging;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
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
 * 
 * All not used Listheaders must me declared as: <br>
 * listheader.setSortAscending(""); <br>
 * listheader.setSortDescending(""); <br>
 * 
 * <br>
 * zkoss 3.6.0 or greater (by using FieldComparator) <br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 07/24/2009: sge changes for clustering.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class PagedListWrapper<E> extends ListModelList implements Serializable {

	private static final long serialVersionUID = -7399762307122148637L;
	static final Logger logger = Logger.getLogger(PagedListWrapper.class);

	// Service that calls the DAO methods
	private PagedListService pagedListService;

	// param. The listboxes paging component
	private Paging paging;

	// param. The SearchObject, holds the entity and properties to search. <br>
	private HibernateSearchObject<E> hibernateSearchObject;

	/**
	 * default constructor.<br>
	 */
	public PagedListWrapper() {
		super();
	}

	public void init(HibernateSearchObject<E> hibernateSearchObject1, Listbox listBox, Paging paging1) {
		setPaging(paging1);
		setListeners(listBox);

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
	 * Calls the method for refreshing the data with the new ordering. and the
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

	public int getPageSize() {
		return getPaging().getPageSize();
	}

	Paging getPaging() {
		return this.paging;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private void setPaging(Paging paging) {
		this.paging = paging;
	}

	public void setSearchObject(HibernateSearchObject<E> hibernateSearchObject1) {
		this.hibernateSearchObject = hibernateSearchObject1;
		initModel();
	}

}
