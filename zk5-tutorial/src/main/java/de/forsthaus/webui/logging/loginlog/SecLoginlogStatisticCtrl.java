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
package de.forsthaus.webui.logging.loginlog;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

import de.forsthaus.backend.bean.DummyBean;
import de.forsthaus.backend.bean.ListLongSumBean;
import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.webui.logging.loginlog.model.SecLoginlogStatisticTotalListModelItemRenderer;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/sec_loginlog/secLoginLogStatistic.zul file.<br>
 * <br>
 * This class creates the tabs + panels for the list and statistic panel.
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 12/19/2009: sge Splitted the controller for each panel. <br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class SecLoginlogStatisticCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4249471372492633246L;
	private static final Logger logger = Logger.getLogger(SecLoginlogStatisticCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowTabPanelLoginStatistic; // autowired
	protected Panel panelSecLoginLogStatistikCenter; // autowired
	protected Panelchildren panelchildrenSecLoginLogStatistikCenter; // autowired
	protected Hbox boxSecLoginLogStatistikCenter; // autowired
	// protected Box boxSecLoginLogStatistikCenter; // autowired

	protected Listbox lbTotalCount;
	protected Listfooter lfTotalCount;
	protected Listbox lbMonthlyCount;
	protected Listfooter lfMonthlyCount;
	protected Listbox lbDailyCount;
	protected Listfooter lfDailyCount;

	protected int maxPanelHeight;
	protected int maxlistBoxHeight;
	// ServiceDAOs / Domain Classes
	private transient LoginLoggingService loginLoggingService;

	/**
	 * default constructor.<br>
	 */
	public SecLoginlogStatisticCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$windowTabPanelLoginStatistic(Event event) throws Exception {
		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		final int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		this.maxPanelHeight = height - 140;
		this.maxlistBoxHeight = this.maxPanelHeight - 25;

		this.panelSecLoginLogStatistikCenter.setHeight(String.valueOf(this.maxPanelHeight) + "px");

		final Calendar aDate = Calendar.getInstance();
		aDate.setTime(new Date());
		final int currentYear = aDate.get(Calendar.YEAR);
		final int currentMonth = aDate.get(Calendar.MONTH);

		this.boxSecLoginLogStatistikCenter.appendChild(doGetTotalCountByCountries());
		this.boxSecLoginLogStatistikCenter.appendChild(doGetMonthlyCountByCountries(currentMonth, currentYear));
		this.boxSecLoginLogStatistikCenter.appendChild(doGetDailyCountByCountries(new Date()));
	}

	/**
	 * when the "help" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "print" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_SecLoginlogList_PrintLoginList(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "RefreshTotalCountByCountries" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$buttonSecLoginListStatisticTotalCountByCountries(Event event) throws InterruptedException {
		doRefreshTotalCount();
	}

	/**
	 * when the "RefreshMonthlyCountByCountries" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$buttonSecLoginListStatisticMonthlyCountByCountries(Event event) throws InterruptedException {

		final Calendar aDate = Calendar.getInstance();
		aDate.setTime(new Date());
		final int currentYear = aDate.get(Calendar.YEAR);
		final int currentMonth = aDate.get(Calendar.MONTH);

		doRefreshMonthlyCount(currentMonth, currentYear);
	}

	/**
	 * when the "RefreshDailyCountByCountries" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$buttonSecLoginListStatisticDailyCountByCountries(Event event) throws InterruptedException {
		doRefreshDailyCount(new Date());
	}

	/**
	 * Generates a listBox who is showing the total logins by country.<br>
	 * 
	 * @return
	 */
	private Cell doGetTotalCountByCountries() {

		final List<DummyBean> list = getLoginLoggingService().getTotalCountByCountries();
		final int recCount = getLoginLoggingService().getTotalCountOfLogs();

		final Div div = new Div();
		div.setHeight("100%");
		div.setWidth("100%");

		final Panel panel = new Panel();
		panel.setTitle(Labels.getLabel("panelTotalCount.Title"));
		panel.setBorder("none");
		panel.setHeight("100%");
		panel.setWidth("100%");
		panel.setParent(div);
		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);

		final Borderlayout bl = new Borderlayout();
		bl.setHeight(this.maxlistBoxHeight + "px");
		bl.setParent(panelchildren);
		final Center center = new Center();
		center.setParent(bl);
		center.setBorder("0");

		this.lbTotalCount = new Listbox();
		this.lbTotalCount.setVflex(true);
		this.lbTotalCount.setMultiple(false);
		this.lbTotalCount.setWidth("99.5%");
		this.lbTotalCount.setHeight("99.5%");
		this.lbTotalCount.setParent(center);

		final Listhead listhead = new Listhead();
		listhead.setSizable(true);
		listhead.setParent(this.lbTotalCount);
		final Listheader lh1 = new Listheader();
		lh1.setSclass("FDListBoxHeader1");
		lh1.setWidth("10%");
		lh1.setSortAscending(new FieldComparator("country", true));
		lh1.setSortDescending(new FieldComparator("country", false));
		lh1.setParent(listhead);
		final Listheader lh2 = new Listheader(Labels.getLabel("listheader_SecLoginlogList_CountryCode2.label"));
		lh2.setSclass("FDListBoxHeader1");
		lh2.setWidth("60%");
		lh2.setSortAscending(new FieldComparator("countryName", true));
		lh2.setSortDescending(new FieldComparator("countryName", false));
		lh2.setParent(listhead);
		final Listheader lh3 = new Listheader(Labels.getLabel("ListheaderStatisticTotalCountByCountries.TotalCount.Label"));
		lh3.setSclass("FDListBoxHeader1");
		lh3.setWidth("30%");
		lh3.setSortAscending(new FieldComparator("totalCount", true));
		lh3.setSortDescending(new FieldComparator("totalCount", false));
		lh3.setSortDirection("descending");
		lh3.setParent(listhead);

		final Listfoot listfoot = new Listfoot();
		listfoot.setHeight("20px");
		listfoot.setParent(this.lbTotalCount);

		final Listfooter lf1 = new Listfooter();
		lf1.setParent(listfoot);
		lf1.setWidth("10%");
		lf1.setStyle("font-weight:bold");
		lf1.setLabel(Labels.getLabel("message_Sum"));

		final Listfooter lf2 = new Listfooter();
		lf2.setParent(listfoot);
		lf2.setWidth("60%");

		this.lfTotalCount = new Listfooter();
		this.lfTotalCount.setParent(listfoot);
		this.lfTotalCount.setWidth("30%");
		this.lfTotalCount.setStyle("font-weight:bold; text-align: right");

		this.lbTotalCount.setItemRenderer(new SecLoginlogStatisticTotalListModelItemRenderer());
		this.lbTotalCount.setModel(new ListModelList(list));

		/** +++ get the SUM of all logs for the ListFooter +++ */
		if (!StringUtils.isEmpty(String.valueOf(recCount))) {
			this.lfTotalCount.setLabel(String.valueOf(recCount));
		} else
			this.lfTotalCount.setLabel("0");

		Cell cell = new Cell();
		cell.setWidth("33%");
		cell.setStyle("padding: 0px;");
		cell.setHflex("1");
		cell.appendChild(div);

		return cell;
	}

	private void doRefreshTotalCount() {

		final List<DummyBean> list = getLoginLoggingService().getTotalCountByCountries();
		final int recCount = getLoginLoggingService().getTotalCountOfLogs();

		this.lbTotalCount.setItemRenderer(new SecLoginlogStatisticTotalListModelItemRenderer());
		this.lbTotalCount.setModel(new ListModelList(list));

		/** +++ get the SUM of all logs for the ListFooter +++ */
		if (!StringUtils.isEmpty(String.valueOf(recCount))) {
			this.lfTotalCount.setLabel(String.valueOf(recCount));
		} else
			this.lfTotalCount.setLabel("0");

	}

	/**
	 * Generates a listBox who is showing the monthly logins by country.<br>
	 * 
	 * @param aMonth
	 * @param aYear
	 * @return
	 */
	private Cell doGetMonthlyCountByCountries(int aMonth, int aYear) {

		final ListLongSumBean<DummyBean> listIntegerSumBean = getLoginLoggingService().getMonthlyCountByCountries(aMonth, aYear);

		final List<DummyBean> list = listIntegerSumBean.getList();
		final long recCount = listIntegerSumBean.getSum();

		final Div div = new Div();
		div.setHeight("100%");
		div.setWidth("100%");

		final Panel panel = new Panel();
		panel.setTitle(Labels.getLabel("panelMonthlyCount.Title") + " " + (aMonth + 1) + "/" + aYear);
		panel.setBorder("none");
		panel.setHeight("100%");
		panel.setWidth("100%");
		panel.setParent(div);
		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);

		final Borderlayout bl = new Borderlayout();
		bl.setHeight(this.maxlistBoxHeight + "px");
		bl.setParent(panelchildren);
		final Center center = new Center();
		center.setParent(bl);
		center.setBorder("0");

		this.lbMonthlyCount = new Listbox();
		this.lbMonthlyCount.setVflex(true);
		this.lbMonthlyCount.setMultiple(false);
		this.lbMonthlyCount.setWidth("99.5%");
		this.lbMonthlyCount.setHeight("99.5%");
		// lb.setParent(panelchildrenSecLoginLogStatistikCenter);
		this.lbMonthlyCount.setParent(center);

		final Listhead listhead = new Listhead();
		listhead.setSizable(true);
		listhead.setParent(this.lbMonthlyCount);
		final Listheader lh1 = new Listheader();
		lh1.setSclass("FDListBoxHeader1");
		lh1.setWidth("10%");
		lh1.setSortAscending(new FieldComparator("country", true));
		lh1.setSortDescending(new FieldComparator("country", false));
		lh1.setParent(listhead);
		final Listheader lh2 = new Listheader(Labels.getLabel("listheader_SecLoginlogList_CountryCode2.label"));
		lh2.setSclass("FDListBoxHeader1");
		lh2.setWidth("60%");
		lh2.setSortAscending(new FieldComparator("countryName", true));
		lh2.setSortDescending(new FieldComparator("countryName", false));
		lh2.setParent(listhead);
		final Listheader lh3 = new Listheader(Labels.getLabel("ListheaderStatisticTotalCountByCountries.TotalCount.Label"));
		lh3.setSclass("FDListBoxHeader1");
		lh3.setWidth("30%");
		lh3.setSortAscending(new FieldComparator("totalCount", true));
		lh3.setSortDescending(new FieldComparator("totalCount", false));
		lh3.setSortDirection("descending");
		lh3.setParent(listhead);

		final Listfoot listfoot = new Listfoot();
		listfoot.setHeight("20px");
		listfoot.setParent(this.lbMonthlyCount);

		final Listfooter lf1 = new Listfooter();
		lf1.setParent(listfoot);
		lf1.setWidth("10%");
		lf1.setStyle("font-weight:bold");
		lf1.setLabel(Labels.getLabel("message_Sum"));

		final Listfooter lf2 = new Listfooter();
		lf2.setParent(listfoot);
		lf2.setWidth("60%");

		this.lfMonthlyCount = new Listfooter();
		this.lfMonthlyCount.setParent(listfoot);
		this.lfMonthlyCount.setWidth("30%");
		this.lfMonthlyCount.setStyle("font-weight:bold; text-align: right");

		this.lbMonthlyCount.setItemRenderer(new SecLoginlogStatisticTotalListModelItemRenderer());
		this.lbMonthlyCount.setModel(new ListModelList(list));

		/** +++ get the SUM of all logs for the ListFooter +++ */
		this.lfMonthlyCount.setLabel(String.valueOf(recCount));

		Cell cell = new Cell();
		cell.setWidth("33%");
		cell.setStyle("padding: 0px;");
		cell.setHflex("1");
		cell.appendChild(div);

		return cell;
	}

	private void doRefreshMonthlyCount(int aMonth, int aYear) {

		final ListLongSumBean<DummyBean> listIntegerSumBean = getLoginLoggingService().getMonthlyCountByCountries(aMonth, aYear);

		final List<DummyBean> list = listIntegerSumBean.getList();
		final long recCount = listIntegerSumBean.getSum();

		this.lbMonthlyCount.setModel(new ListModelList(list));

		/** +++ get the SUM of all logs for the ListFooter +++ */
		this.lfMonthlyCount.setLabel(String.valueOf(recCount));
	}

	/**
	 * Generates a listBox who is showing the logins by country <br>
	 * for a special Date.<br>
	 * 
	 * @param aDate
	 * @return div
	 */
	private Cell doGetDailyCountByCountries(Date aDate) {

		final ListLongSumBean<DummyBean> listIntegerSumBean = getLoginLoggingService().getDailyCountByCountries(aDate);

		final List<DummyBean> list = listIntegerSumBean.getList();
		final long recCount = listIntegerSumBean.getSum();

		final Div div = new Div();
		div.setHeight("100%");
		div.setWidth("100%");

		final Panel panel = new Panel();
		panel.setTitle(Labels.getLabel("panelDailyCount.Title") + ": " + getDateTime(aDate));
		panel.setBorder("none");
		panel.setHeight("100%");
		panel.setWidth("100%");
		panel.setParent(div);

		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);

		final Borderlayout bl = new Borderlayout();
		bl.setHeight(this.maxlistBoxHeight + "px");
		bl.setParent(panelchildren);
		final Center center = new Center();
		center.setParent(bl);
		center.setBorder("0");

		this.lbDailyCount = new Listbox();
		this.lbDailyCount.setVflex(true);
		this.lbDailyCount.setMultiple(false);
		this.lbDailyCount.setWidth("99.5%");
		this.lbDailyCount.setHeight("99.5%");
		// lb.setParent(panelchildrenSecLoginLogStatistikCenter);
		this.lbDailyCount.setParent(center);

		final Listhead listhead = new Listhead();
		listhead.setSizable(true);
		listhead.setParent(this.lbDailyCount);
		final Listheader lh1 = new Listheader();
		lh1.setSclass("FDListBoxHeader1");
		lh1.setWidth("10%");
		lh1.setSortAscending(new FieldComparator("country", true));
		lh1.setSortDescending(new FieldComparator("country", false));
		lh1.setParent(listhead);
		final Listheader lh2 = new Listheader(Labels.getLabel("listheader_SecLoginlogList_CountryCode2.label"));
		lh2.setSclass("FDListBoxHeader1");
		lh2.setWidth("60%");
		lh2.setSortAscending(new FieldComparator("countryName", true));
		lh2.setSortDescending(new FieldComparator("countryName", false));
		lh2.setParent(listhead);
		final Listheader lh3 = new Listheader(Labels.getLabel("ListheaderStatisticTotalCountByCountries.TotalCount.Label"));
		lh3.setSclass("FDListBoxHeader1");
		lh3.setWidth("30%");
		lh3.setSortAscending(new FieldComparator("totalCount", true));
		lh3.setSortDescending(new FieldComparator("totalCount", false));
		lh3.setSortDirection("descending");
		lh3.setParent(listhead);

		final Listfoot listfoot = new Listfoot();
		listfoot.setHeight("20px");
		listfoot.setParent(this.lbDailyCount);

		final Listfooter lf1 = new Listfooter();
		lf1.setParent(listfoot);
		lf1.setWidth("60%");
		lf1.setStyle("font-weight:bold");
		lf1.setLabel(Labels.getLabel("message_Sum"));

		final Listfooter lf2 = new Listfooter();
		lf2.setParent(listfoot);
		lf2.setWidth("100%");

		this.lfDailyCount = new Listfooter();
		this.lfDailyCount.setParent(listfoot);
		this.lfDailyCount.setWidth("30%");
		this.lfDailyCount.setStyle("font-weight:bold; text-align: right");

		this.lbDailyCount.setItemRenderer(new SecLoginlogStatisticTotalListModelItemRenderer());
		this.lbDailyCount.setModel(new ListModelList(list));

		/** +++ get the SUM of all logs for the ListFooter +++ */
		this.lfDailyCount.setLabel(String.valueOf(recCount));

		Cell cell = new Cell();
		cell.setWidth("33%");
		cell.setStyle("padding: 0px;");
		cell.setHflex("1");
		cell.appendChild(div);

		return cell;
	}

	private void doRefreshDailyCount(Date aDate) {

		final ListLongSumBean<DummyBean> listIntegerSumBean = getLoginLoggingService().getDailyCountByCountries(aDate);

		final List<DummyBean> list = listIntegerSumBean.getList();
		final long recCount = listIntegerSumBean.getSum();

		this.lbDailyCount.setItemRenderer(new SecLoginlogStatisticTotalListModelItemRenderer());
		this.lbDailyCount.setModel(new ListModelList(list));

		/** +++ get the SUM of all logs for the ListFooter +++ */
		this.lfDailyCount.setLabel(String.valueOf(recCount));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Helpers ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Format the date/time. <br>
	 * 
	 * @return String of date/time
	 */
	private String getDateTime(Date date) {
		if (date != null) {
			return ZksampleDateFormat.getDateFormater().format(date);
		}
		return "";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public LoginLoggingService getLoginLoggingService() {
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

}
