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
package de.forsthaus.webui.debug;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Paging;

import de.forsthaus.backend.model.HibernateStatistics;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.gui.service.GuiHibernateStatisticsService;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.pagging.PagedGridWrapper;

/**
 * @author bbruhns
 * 
 */
public class HibernateStatisticsCtrl extends GFCBaseCtrl {
	private static final long serialVersionUID = 1L;

	private GuiHibernateStatisticsService guiHibernateStatisticsService;
	private PagedGridWrapper<HibernateStatistics> gridPagedListWrapper;

	protected Grid grid;

	protected Paging gridPaging;

	public GuiHibernateStatisticsService getGuiHibernateStatisticsService() {
		return this.guiHibernateStatisticsService;
	}

	public void setGuiHibernateStatisticsService(GuiHibernateStatisticsService guiHibernateStatisticsService) {
		this.guiHibernateStatisticsService = guiHibernateStatisticsService;
	}

	public void onCreate$window_HibernateStatisticList(Event event) throws Exception {

		final HibernateSearchObject<HibernateStatistics> searchObj = new HibernateSearchObject<HibernateStatistics>(HibernateStatistics.class);
		searchObj.addSort("id", true);
		this.gridPagedListWrapper.init(searchObj, this.grid, this.gridPaging);

	}

	public PagedGridWrapper<HibernateStatistics> getGridPagedListWrapper() {
		return this.gridPagedListWrapper;
	}

	public void setGridPagedListWrapper(PagedGridWrapper<HibernateStatistics> gridPagedListWrapper) {
		this.gridPagedListWrapper = gridPagedListWrapper;
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		final String message = Labels.getLabel("message.Not_Implemented_Yet");
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
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
		// Events.postEvent("onCreate", this.self, event);
		this.gridPagedListWrapper.refreshModel();
		this.self.invalidate();
	}
}
