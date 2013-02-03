/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of openTruuls™. http://www.opentruuls.org/ and 
 * have the permission to be integrated in the zksample2 demo application.
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
package de.forsthaus.webui.dashboard.module;

import java.io.Serializable;
import java.util.List;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.South;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import de.forsthaus.backend.bean.ResultObject;
import de.forsthaus.backend.model.ApplicationNews;
import de.forsthaus.backend.service.ApplicationNewsService;
import de.forsthaus.webui.util.ZksampleDateFormat;

/**
 * EN: <b>History of changes </b> controller for the dashboard.<br>
 * Shows the the history of the changes of this application.
 * <hr>
 * DE: <b>History der Aenderungen</b> controller fuer die SystemUebersicht.<br>
 * Zeigt die Historie der Aenderungen fuer diese Applikation.
 * 
 * <pre>
 * call: Div div = DashboardApplicationNewsListCtrl.show(200);
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardApplicationNewsListCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = Labels.getLabel("common.Application.History");
	// The title icon path for the groupbox
	private String iconPath = "/images/icons/new_icons_10.gif";

	// flag for using a timer
	private boolean timerControl;
	// delay of the timer
	private int delay;
	// ZK Timer component
	private Timer moduleTimer;
	// Listbox
	private Listbox listbox;
	// the model for the listbox
	private ListModelList listModelList;
	// Paging
	private Paging paging;
	// PageSize
	private int pageSize = 12;

	// the Service
	private ApplicationNewsService applicationNewsService;

	/**
	 * The static call method.
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @return the module as DIV.
	 */
	public static Div show(int modulHeight) {
		return new DashboardApplicationNewsListCtrl(modulHeight);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardApplicationNewsListCtrl(int modulHeight) {
		super();

		setModulHeight(modulHeight);
		createComponents();
	}

	/**
	 * The static call method.
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @param timer
	 *            true or false
	 * @param delay
	 *            in milliseconds
	 * @return the module as DIV.
	 */
	public static Div show(int modulHeight, boolean timer, int delay) {
		return new DashboardApplicationNewsListCtrl(modulHeight, timer, delay);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @param timer
	 *            true or false
	 * @param delay
	 *            in milliseconds
	 */
	private DashboardApplicationNewsListCtrl(int modulHeight, boolean timer, int delay) {
		super();

		setModulHeight(modulHeight);
		setTimerControl(timer);
		setDelay(delay);

		if (isTimerControl()) {
			createServerPushTimer();
		}

		createComponents();
	}

	/**
	 * Creates the Timer for the serverPush mechanism. In it we call to
	 * different DB methods for showing different result sets.
	 */
	private void createServerPushTimer() {

		this.moduleTimer = new Timer();
		// timer doesn't work without a page as parent
		Window win = (Window) Path.getComponent("/outerIndexWindow");
		this.moduleTimer.setPage(win.getPage());

		this.moduleTimer.setDelay(getDelay());
		this.moduleTimer.setRepeats(true);
		this.moduleTimer.addEventListener("onTimer", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				doReadData();
			}
		});

		// start the timer
		if (this.moduleTimer != null) {
			this.moduleTimer.setRunning(true);
		}
	}

	/**
	 * Creates the components..<br>
	 */
	private void createComponents() {

		/**
		 * !! Windows as NameSpaceContainer to prevent not unique id's error
		 * from other dashboard module buttons or other used components.
		 */
		Window win = new Window();
		win.setBorder("none");
		win.setSclass("OT-DashboardWindow");
		win.setParent(this);

		Panel pl = new Panel();
		pl.setBorder("normal");
		pl.setClosable(false);
		pl.setParent(win);
		Caption cap = new Caption();
		cap.setImage(iconPath);
		cap.setLabel(title);
		cap.setStyle("padding: 0px;");
		cap.setParent(pl);
		Panelchildren plc = new Panelchildren();
		plc.setParent(pl);

		// Buttons Toolbar/Timer
		Div div = new Div();
		div.setStyle("padding: 0px");
		div.setParent(cap);
		Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setWidth("100%");
		hbox.setParent(div);
		Toolbar toolbarRight = new Toolbar();
		toolbarRight.setStyle("float:right; border-style: none;");
		toolbarRight.setParent(hbox);

		Hbox hboxSameButtonsHeight = new Hbox();
		hboxSameButtonsHeight.setPack("center");
		hboxSameButtonsHeight.setParent(toolbarRight);

		if (isTimerControl()) {
			Button btnTimer = new Button();
			btnTimer.setId("btnTimer");
			btnTimer.setSclass("oT_ButtonForPanelWithIcon");
			btnTimer.setImage("/images/icons/clock1_16x16.gif");

			// convert to seconds
			int seconds = (int) Math.round(getDelay() / 1000);

			if (seconds > 60) {
				// convert to minutes and set localization to minutes
				int minutes = (int) Math.round((getDelay() / 1000) / 60);
				btnTimer.setTooltiptext(Labels.getLabel("btnTimer.tooltiptext") + " " + minutes + " " + Labels.getLabel("common.Minutes"));
			} else
				// set localization to seconds
				btnTimer.setTooltiptext(Labels.getLabel("btnTimer.tooltiptext") + " " + seconds + " " + Labels.getLabel("common.Seconds"));

			btnTimer.addEventListener("onClick", new BtnClickListener());
			btnTimer.setParent(hboxSameButtonsHeight);
		}
		Button btnRefresh = new Button();
		btnRefresh.setId("btnSelectYoutubeSong");
		btnRefresh.setSclass("oT_ButtonForPanelWithIcon");
		btnRefresh.setImage("/images/icons/refresh_green_16x16.gif");
		btnRefresh.setTooltiptext(Labels.getLabel("btnRefresh.tooltiptext"));
		btnRefresh.addEventListener("onClick", new BtnClickListener());
		btnRefresh.setParent(hboxSameButtonsHeight);
		// END Buttons Toolbar/Timer

		// body
		Borderlayout bl = new Borderlayout();
		bl.setHeight(getModulHeight() + "px");
		bl.setParent(plc);

		Center ct = new Center();
		ct.setSclass("FDCenterNoBorder");
		ct.setStyle("background-color: white");
		ct.setFlex(true);
		ct.setParent(bl);

		South south = new South();
		south.setBorder("none");
		south.setParent(bl);

		// Listbox
		listbox = new Listbox();
		// listbox.setStyle("border: none;");
		listbox.setStyle("border-top-width: 0px; border-left-width: 0px; border-right-width: 0px; border-bottom-width: 0px;");
		listbox.setHeight("100%");
		listbox.setVflex("1");
		listbox.setVisible(true);
		listbox.setParent(ct);
		listbox.setItemRenderer(new ItemRenderer());

		Listhead listhead = new Listhead();
		listhead.setParent(listbox);

		Listheader listheader1 = new Listheader();
		listheader1.setWidth("100px");
		listheader1.setHeight("0px");
		listheader1.setParent(listhead);

		Listheader listheader2 = new Listheader();
		listheader2.setWidth("100%");
		listheader1.setHeight("0px");
		listheader2.setParent(listhead);

		// Paging
		paging = new Paging();
		paging.setDetailed(true);
		paging.setPageSize(getPageSize());
		paging.setParent(south);
		paging.addEventListener("onPaging", new OnPagingEventListener());

		doReadData();
	}

	/**
	 * Init. Reads the data.
	 */
	private void doReadData() {

		// clear old stuff

		/**
		 * init the model.<br>
		 * The ResultObject is a helper class that holds the generic list and
		 * the totalRecord count as int value.
		 */
		ResultObject ro = getApplicationNewsService().getAllApplicationNews(0, getPageSize());
		List<ApplicationNews> resultList = (List<ApplicationNews>) ro.getList();
		paging.setTotalSize(ro.getTotalCount());

		// set the model
		setListModelList(new ListModelList(resultList));
		this.listbox.setModel(getListModelList());
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class ItemRenderer implements ListitemRenderer {

		@Override
		public void render(Listitem item, Object data) throws Exception {

			ApplicationNews applicationNews = (ApplicationNews) data;

			Listcell lc;
			Label lb;

			lc = new Listcell();
			lc.setStyle("text-align: left; padding-left: 3px;");
			lb = new Label();
			lb.setParent(lc);
			lb.setValue(ZksampleDateFormat.getDateFormater().format(applicationNews.getDate()));
			lc.setParent(item);

			lc = new Listcell();
			lc.setStyle("text-align: left; padding-left: 3px;");
			lb = new Label();
			lb.setParent(lc);
			lb.setValue(applicationNews.getText().toString());
			lc.setParent(item);

			item.setValue(data);
			// ComponentsCtrl.applyForward(item,
			// "onDoubleClick=onDoubleClicked");
		}
	}

	/**
	 * Inner onBtnClick Listener class.<br>
	 * 
	 * @author sGerth
	 */
	private final class BtnClickListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			// check which button is pressed
			if (event.getTarget().getId().equalsIgnoreCase("btnRefresh")) {
				doReadData();
			}
		}
	}

	/**
	 * Refreshes the list by calling the DAO method with the modified search
	 * object. <br>
	 * 
	 * @param so
	 *            SearchObject, holds the entity and properties to search. <br>
	 * @param start
	 *            Row to start. <br>
	 */
	@SuppressWarnings("unchecked")
	void refreshModel(int start) {

		// clear old data
		getListModelList().clear();

		// init the model
		ResultObject ro = getApplicationNewsService().getAllApplicationNews(start, getPageSize());
		List<ApplicationNews> resultList = (List<ApplicationNews>) ro.getList();
		this.paging.setTotalSize(ro.getTotalCount());

		// set the model
		setListModelList(new ListModelList(resultList));
		this.listbox.setModel(getListModelList());
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

			PagingEvent pe = (PagingEvent) event;
			int pageNo = pe.getActivePage();
			int start = pageNo * getPageSize();
			// refresh the list
			refreshModel(start);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setModulHeight(int modulHeight) {
		this.modulHeight = modulHeight;
	}

	public int getModulHeight() {
		return modulHeight;
	}

	public ApplicationNewsService getApplicationNewsService() {
		if (applicationNewsService == null) {
			applicationNewsService = (ApplicationNewsService) SpringUtil.getBean("applicationNewsService");
		}
		return applicationNewsService;
	}

	public void setApplicationNewsService(ApplicationNewsService applicationNewsService) {
		this.applicationNewsService = applicationNewsService;
	}

	// Timer stuff
	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public void setTimerControl(boolean timerControl) {
		this.timerControl = timerControl;
	}

	public boolean isTimerControl() {
		return timerControl;
	}

	public void setModuleTimer(Timer moduleTimer) {
		this.moduleTimer = moduleTimer;
	}

	public Timer getModuleTimer() {
		return moduleTimer;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setListModelList(ListModelList listModelList) {
		this.listModelList = listModelList;
	}

	public ListModelList getListModelList() {
		return this.listModelList;
	}
}
