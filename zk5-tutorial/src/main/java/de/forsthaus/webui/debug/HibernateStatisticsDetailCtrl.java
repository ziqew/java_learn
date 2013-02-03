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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Row;

import de.forsthaus.backend.model.HibernateStatistics;
import de.forsthaus.gui.service.GuiHibernateStatisticsService;

/**
 * Controller for the HibernateStatistic Details, if the user opens a
 * Grid-Detail. <br>
 * Zul: /WEB-INF/pages/debug/HibernateStatisticsDetail.zul <br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class HibernateStatisticsDetailCtrl extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	private transient GuiHibernateStatisticsService guiHibernateStatisticsService;

	public GuiHibernateStatisticsService getGuiHibernateStatisticsService() {
		return this.guiHibernateStatisticsService;
	}

	public void setGuiHibernateStatisticsService(GuiHibernateStatisticsService guiHibernateStatisticsService) {
		this.guiHibernateStatisticsService = guiHibernateStatisticsService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.zk.ui.util.GenericComposer#doBeforeCompose(org.zkoss.zk.ui.
	 * Page, org.zkoss.zk.ui.Component, org.zkoss.zk.ui.metainfo.ComponentInfo)
	 */
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		final Row row = (Row) parent.getParent();
		final HibernateStatistics hibernateStatistics = (HibernateStatistics) row.getValue();

		this.guiHibernateStatisticsService.initDetails(hibernateStatistics);

		parent.setAttribute("hs", hibernateStatistics, false);

		return super.doBeforeCompose(page, parent, compInfo);
	}

	// public void onCreate(Event event) throws Exception {
	// System.out.println(ZkossComponentTreeUtil.getZulTree(this.self));
	// }

}
