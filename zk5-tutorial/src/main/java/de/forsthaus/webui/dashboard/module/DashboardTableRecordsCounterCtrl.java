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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import de.forsthaus.backend.service.CommonService;

/**
 * EN: <b>Table record counter </b> controller for the dashboard.<br>
 * Shows the count of records for each table that the tenant use.
 * <hr>
 * DE: <b>Tabellen Datensatz Zaehler</b> controller fuer die SystemUebersicht.<br>
 * Zeigt die Anzahl der Datensaetze pro Tabelle des jeweilige Mandanten.
 * 
 * <pre>
 * call: Div div = DashboardTableRecordsCounterCtrl.show(200);
 * call: Div div = DashboardTableRecordsCounterCtrl.show(200, true, 30000);
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardTableRecordsCounterCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// flag for using a timer
	private boolean timerControl;
	// delay of the timer
	private int delay;
	// ZK Timer component
	private Timer moduleTimer;
	// the title
	private String title = Labels.getLabel("common.Data.Inventory");
	// the icon before the title
	private String iconPath = "/images/icons/database_blue_16x16.gif";

	// holds the data
	private Rows rows;

	/**
	 * The static call method.
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @return the module as DIV.
	 */
	public static Div show(int modulHeight) {
		return new DashboardTableRecordsCounterCtrl(modulHeight);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardTableRecordsCounterCtrl(int modulHeight) {
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
		return new DashboardTableRecordsCounterCtrl(modulHeight, timer, delay);
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
	private DashboardTableRecordsCounterCtrl(int modulHeight, boolean timer, int delay) {
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
		btnRefresh.setId("btnRefresh");
		btnRefresh.setSclass("oT_ButtonForPanelWithIcon");
		btnRefresh.setImage("/images/icons/refresh_green_16x16.gif");
		btnRefresh.setTooltiptext(Labels.getLabel("btnRefresh.tooltiptext"));
		btnRefresh.addEventListener("onClick", new BtnClickListener());
		btnRefresh.setParent(hboxSameButtonsHeight);

		// body
		Borderlayout bl = new Borderlayout();
		bl.setHeight(getModulHeight() + "px");
		bl.setParent(plc);
		Center ct = new Center();
		ct.setSclass("FDCenterNoBorder");
		ct.setStyle("background-color: white");
		ct.setFlex(true);
		ct.setParent(bl);

		Grid grid = new Grid();
		grid.setSclass("GridLayoutSmartBorderDashed");
		grid.setParent(ct);

		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);

		Column columnTableName = new Column();
		columnTableName.setWidth("65%");
		columnTableName.setLabel(Labels.getLabel("common.Table"));
		columnTableName.setStyle("color: red");
		columnTableName.setParent(columns);
		Column columnRecordCount = new Column();
		columnRecordCount.setWidth("35%");
		columnRecordCount.setLabel(Labels.getLabel("common.Count"));
		columnRecordCount.setStyle("color: red");
		columnRecordCount.setParent(columns);

		rows = new Rows();
		rows.setParent(grid);

		doReadData();

	}

	/**
	 * Reads the data.
	 */
	private void doReadData() {

		// clear all old stuff
		rows.getChildren().clear();

		/**
		 * For performance boosting, we get now all the table recordCounts out
		 * from ONE Service Call and get back the results in a map.
		 */
		CommonService service = (CommonService) SpringUtil.getBean("commonService");
		final Map<String, Object> map = service.getAllTablesRecordCounts();

		if (map.containsKey("HibernateStatistics")) {
			addNewRow(rows, Labels.getLabel("table.hibernate_entity_statistics"), map.get("HibernateStatistics"));
		}
		if (map.containsKey("Customer")) {
			addNewRow(rows, Labels.getLabel("table.customer"), map.get("Customer"));
		}
		if (map.containsKey("Branch")) {
			addNewRow(rows, Labels.getLabel("table.branch"), map.get("Branch"));
		}
		if (map.containsKey("Offices")) {
			addNewRow(rows, Labels.getLabel("table.office"), map.get("Offices"));
		}
		if (map.containsKey("Article")) {
			addNewRow(rows, Labels.getLabel("table.article"), map.get("Article"));
		}
		if (map.containsKey("Order")) {
			addNewRow(rows, Labels.getLabel("table.order"), map.get("Order"));
		}
		if (map.containsKey("Orderposition")) {
			addNewRow(rows, Labels.getLabel("table.orderposition"), map.get("Orderposition"));
		}
		if (map.containsKey("GuestBook")) {
			addNewRow(rows, Labels.getLabel("table.guestbook"), map.get("GuestBook"));
		}
		if (map.containsKey("SecRight")) {
			addNewRow(rows, Labels.getLabel("table.sec_right"), map.get("SecRight"));
		}
		if (map.containsKey("SecGroupright")) {
			addNewRow(rows, Labels.getLabel("table.sec_groupright"), map.get("SecGroupright"));
		}
		if (map.containsKey("SecGroup")) {
			addNewRow(rows, Labels.getLabel("table.sec_group"), map.get("SecGroup"));
		}
		if (map.containsKey("SecRolegroup")) {
			addNewRow(rows, Labels.getLabel("table.sec_rolegroup"), map.get("SecRolegroup"));
		}
		if (map.containsKey("SecRole")) {
			addNewRow(rows, Labels.getLabel("table.sec_role"), map.get("SecRole"));
		}
		if (map.containsKey("SecUserrole")) {
			addNewRow(rows, Labels.getLabel("table.sec_userrole"), map.get("SecUserrole"));
		}
		if (map.containsKey("SecUser")) {
			addNewRow(rows, Labels.getLabel("table.sec_user"), map.get("SecUser"));
		}
		if (map.containsKey("SecLoginlog")) {
			addNewRow(rows, Labels.getLabel("table.sec_loginlog"), map.get("SecLoginlog"));
		}
		if (map.containsKey("CountryCode")) {
			addNewRow(rows, Labels.getLabel("table.sys_countrycode"), map.get("CountryCode"));
		}
		if (map.containsKey("IpToCountry")) {
			addNewRow(rows, Labels.getLabel("table.ip_to_country"), map.get("IpToCountry"));
		}
		if (map.containsKey("CalendarEvents")) {
			addNewRow(rows, Labels.getLabel("table.calendar_event"), map.get("CalendarEvents"));
		}
		if (map.containsKey("YouTubeLinks")) {
			addNewRow(rows, Labels.getLabel("table.youtube_links"), map.get("YouTubeLinks"));
		}
		if (map.containsKey("ApplicationNews")) {
			addNewRow(rows, Labels.getLabel("table.app_news"), map.get("ApplicationNews"));
		}

	}

	/**
	 * Add a new row to the grid.<br>
	 * 
	 * @param rowParent
	 * @param tableName
	 * @param value
	 */
	private void addNewRow(Rows rowParent, String tableName, Object value) {

		Row row = new Row();

		Html html_TableName = new Html(tableName);
		html_TableName.setStyle("padding-left: 5px;");
		Div divKey = new Div();
		divKey.setAlign("left");
		divKey.appendChild(html_TableName);

		Html html_RecordCount = null;

		if (value instanceof BigDecimal) {
			BigDecimal myDec = (BigDecimal) value;
			myDec = myDec.setScale(2, BigDecimal.ROUND_HALF_UP);

			// Format the value to money
			NumberFormat formatter = new DecimalFormat("#,##0.00");
			String money = formatter.format(myDec);

			html_RecordCount = new Html(money);
		} else if (value instanceof Integer) {
			html_RecordCount = new Html(String.valueOf(value));
		} else
			html_RecordCount = new Html(String.valueOf(value));

		html_RecordCount.setStyle("padding-right: 5px;");
		Div divValue = new Div();
		divValue.setAlign("right");
		divValue.appendChild(html_RecordCount);

		row.appendChild(divKey);
		row.appendChild(divValue);
		row.setParent(rowParent);
	}

	/**
	 * Add a new row to the grid.<br>
	 * 
	 * @param rowParent
	 * @param tableName
	 * @param value
	 * @param color
	 *            | black, gray, silver, brown, red, yellow, green, blue, brown,
	 *            cyan
	 */
	private void addNewRow(Rows rowParent, String tableName, Object value, String color) {

		// if (!color.equalsIgnoreCase("red") | !color.equalsIgnoreCase("blue")
		// | !color.equalsIgnoreCase("green") |
		// !color.equalsIgnoreCase("yellow") | !color.equalsIgnoreCase("gray")
		// | !color.equalsIgnoreCase("silver") |
		// !color.equalsIgnoreCase("black") | !color.equalsIgnoreCase("cyan") |
		// !color.equalsIgnoreCase("brown")) {
		// color = "black";
		// }

		Row row = new Row();

		Html html_TableName = new Html(tableName);

		html_TableName.setStyle("padding-left: 5px;");

		Div divKey = new Div();
		divKey.setAlign("left");
		divKey.appendChild(html_TableName);

		Html html_RecordCount = null;

		if (value instanceof BigDecimal) {
			BigDecimal myDec = (BigDecimal) value;
			myDec = myDec.setScale(2, BigDecimal.ROUND_HALF_UP);

			// Format the value to money
			NumberFormat formatter = new DecimalFormat("#,##0.00");
			String money = formatter.format(myDec);

			html_RecordCount = new Html(money);
		} else if (value instanceof Integer) {
			html_RecordCount = new Html(String.valueOf(value));
		} else
			html_RecordCount = new Html(String.valueOf(value));

		if (!color.equalsIgnoreCase("black")) {
			html_RecordCount.setStyle("padding-right: 5px;  color: " + color + ";");
		} else
			html_RecordCount.setStyle("padding-right: 5px;");

		Div divValue = new Div();
		divValue.setAlign("right");
		divValue.appendChild(html_RecordCount);

		row.appendChild(divKey);
		row.appendChild(divValue);
		row.setParent(rowParent);
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setModulHeight(int modulHeight) {
		this.modulHeight = modulHeight;
	}

	public int getModulHeight() {
		return modulHeight;
	}

	public void setRows(Rows rows) {
		this.rows = rows;
	}

	public Rows getRows() {
		return rows;
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

}
