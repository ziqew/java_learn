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
package de.forsthaus.webui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Hr;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Tablechildren;
import org.zkoss.zkmax.zul.Tablelayout;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.DialModel;
import org.zkoss.zul.DialModelScale;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.North;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.CommonService;
import de.forsthaus.backend.service.CustomerService;
import de.forsthaus.example.RandomDataEngine;
import de.forsthaus.statistic.FDStatistic;
import de.forsthaus.webui.dashboard.module.DashboardApplicationNewsListCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.WindowBaseCtrl;
import de.forsthaus.webui.util.ZksampleDateFormat;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the prePage /index.zul file.<br>
 * <br>
 * This page is unSecured as the entry page for the application.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changings for clustering.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          11/20/2009: sge added recordcount for new table ip"country.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class InitApplicationCtrl extends WindowBaseCtrl implements Serializable {

	private final static Logger logger = Logger.getLogger(InitApplicationCtrl.class);
	private static final long serialVersionUID = 1L;

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window startWindow; // autowired
	protected North bl_north; // autowire
	protected South bl_south; // autowire
	protected Center bl_center; // autowire

	private Tablelayout tableLayout;
	private Tablechildren tableChildrenRecords;
	private Tablechildren tableChildrenStatistic;
	private Tablechildren tableChildrenButtons;
	private Div div_Buttons;
	private Vbox Vbox_Buttons;
	private Panelchildren panelChildren_Buttons;

	private Button btn100;
	private Button btn1000;

	// ServiceDAOs / Domain Classes
	private transient CustomerService customerService;
	private transient BrancheService brancheService;
	private transient CommonService commonService;

	private String orientation;

	/**
	 * default constructor.<br>
	 */
	public InitApplicationCtrl() {
		super();
	}

	public void onCreate$startWindow(Event event) throws Exception {

		doOnCreateCommon(this.startWindow); // do the autowire stuff

		// test ZksampleCookieUtils.getAllCookies();
		// test System.out.println("Used Theme = " +
		// ZksampleCookieUtils.getCookie("Theme"));

		createMainGrid();

		// countDemoData();

		showApplicationNews();

		/* Monitor the application */
		showStatistic();

		createButtons();

		showUsersOnlineChart();

		bl_center.invalidate();
	}

	/**
	 * Creates the main grid for showing the table record counts and <br>
	 * the applications statistic data.<br>
	 */
	private void createMainGrid() {

		Div div = new Div();
		div.setParent(this.bl_center);

		final Hr hr = new Hr();
		hr.setParent(div);

		/*
		 * Borderlayout around the grid for make it scrollable to see all table
		 * records if the browser window are to small.
		 */
		final Borderlayout bl = new Borderlayout();
		bl.setParent(div);
		final Center ct = new Center();
		ct.setAutoscroll(true);
		ct.setStyle("background-color: #EBEBEB");
		ct.setBorder("none");
		ct.setFlex(true);
		ct.setParent(bl);
		final Div divCt = new Div();
		divCt.setParent(ct);

		this.tableLayout = new Tablelayout();
		this.tableLayout.setColumns(3);
		this.tableLayout.setParent(divCt);

		this.tableChildrenRecords = new Tablechildren();
		this.tableChildrenRecords.setRowspan(1);
		this.tableChildrenRecords.setWidth("600px");
		this.tableChildrenRecords.setStyle("padding-left: 5px;");
		this.tableChildrenRecords.setParent(this.tableLayout);

		this.tableChildrenStatistic = new Tablechildren();
		this.tableChildrenStatistic.setRowspan(1);
		this.tableChildrenStatistic.setWidth("450px");
		this.tableChildrenStatistic.setStyle("padding-left: 5px;");
		this.tableChildrenStatistic.setParent(this.tableLayout);

		this.tableChildrenButtons = new Tablechildren();
		this.tableChildrenButtons.setRowspan(1);
		this.tableChildrenButtons.setWidth("240px");
		this.tableChildrenButtons.setStyle("padding-left: 5px;");
		this.tableChildrenButtons.setParent(this.tableLayout);

		final Panel pb = new Panel();
		pb.setWidth("240px");
		pb.setBorder("none");
		pb.setStyle("align:left; color:red");
		pb.setParent(this.tableChildrenButtons);

		this.panelChildren_Buttons = new Panelchildren();
		this.panelChildren_Buttons.setParent(pb);

		final Separator sep = new Separator();
		sep.setParent(divCt);
		final Separator sep2 = new Separator();
		sep2.setParent(divCt);

		final Div divFooter = new Div();
		divFooter.setAlign("center");
		divFooter.setParent(this.bl_south);

		final Hr hr2 = new Hr();
		hr2.setParent(divFooter);

		final Label footerLabel = new Label();
		footerLabel.setValue(" Help to prevent the global warming by writing cool software.");
		footerLabel.setStyle("align:center; padding-top:0px; font-family:Verdana;  font-size: 0.6em; ");
		footerLabel.setParent(divFooter);
	}

	/**
	 * Shows the application news.<br>
	 */
	private void showApplicationNews() {

		final Panel panel = new Panel();
		panel.setTitle("");
		panel.setBorder("none");
		panel.setParent(this.tableChildrenRecords);

		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);
		panelchildren.setStyle("padding: 0px; background-color: #EBEBEB;");

		panelchildren.appendChild(DashboardApplicationNewsListCtrl.show(350, false, 600000));
	}

	/**
	 * Shows the count of records of all tables.<br>
	 */
	private void countDemoData() {

		final Panel panel = new Panel();
		panel.setTitle("");
		panel.setWidth("260px");
		panel.setBorder("none");
		panel.setStyle("align:left; color:red; ");
		panel.setParent(this.tableChildrenRecords);

		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);
		panelchildren.setStyle("background-color: #EBEBEB;");

		final Groupbox gb = new Groupbox();
		gb.setMold("3d");
		gb.setContentStyle("padding: 2px");
		gb.setParent(panelchildren);

		final Caption caption = new Caption();
		caption.setImage("/images/icons/database_blue_16x16.gif");
		caption.setLabel("Demo-Data in PostgreSQL v9.0.2");
		caption.setStyle("color: #000000;font-weight:bold; text-align:left ");
		caption.setParent(gb);

		final Grid grid = new Grid();
		grid.setWidth("100%");
		// grid.setParent(panelchildren);
		grid.setParent(gb);

		final Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);

		final Column column1 = new Column();
		column1.setWidth("65%");
		column1.setLabel("Table");
		column1.setParent(columns);
		final Column column2 = new Column();
		column2.setWidth("35%");
		column2.setLabel("records");
		column2.setParent(columns);

		final Rows rows = new Rows();
		rows.setParent(grid);

		/**
		 * For performance boosting, we get now all the table recordCounts out
		 * from ONE Service Call and get back the results in a map.
		 */
		final Map<String, Object> map = getCommonService().getAllTablesRecordCounts();

		if (map.containsKey("HibernateStatistics")) {
			addNewRow(rows, Labels.getLabel("table.hibernate_entity_statistics"), map.get("HibernateStatistics"));
		}
		if (map.containsKey("Customer")) {
			addNewRow(rows, "Customer", map.get("Customer"));
			// addNewRow(rows, "Customer1", 12345678);
		}
		if (map.containsKey("Branch")) {
			addNewRow(rows, "Branch", map.get("Branch"));
		}
		if (map.containsKey("Offices")) {
			addNewRow(rows, "Offices", map.get("Offices"));
		}
		if (map.containsKey("Article")) {
			addNewRow(rows, "Article", map.get("Article"));
		}
		if (map.containsKey("Order")) {
			addNewRow(rows, "Order", map.get("Order"));
		}
		if (map.containsKey("Orderposition")) {
			addNewRow(rows, "Orderposition", map.get("Orderposition"));
		}
		if (map.containsKey("GuestBook")) {
			addNewRow(rows, "GuestBook", map.get("GuestBook"));
		}
		if (map.containsKey("SecGroup")) {
			addNewRow(rows, "SecGroup", map.get("SecGroup"));
		}
		if (map.containsKey("SecGroupright")) {
			addNewRow(rows, "SecGroupright", map.get("SecGroupright"));
		}
		if (map.containsKey("SecRight")) {
			addNewRow(rows, "SecRight", map.get("SecRight"));
		}
		if (map.containsKey("SecRole")) {
			addNewRow(rows, "SecRole", map.get("SecRole"));
		}
		if (map.containsKey("SecRolegroup")) {
			addNewRow(rows, "SecRolegroup", map.get("SecRolegroup"));
		}
		if (map.containsKey("SecUserrole")) {
			addNewRow(rows, "SecUserrole", map.get("SecUserrole"));
		}
		if (map.containsKey("SecUser")) {
			addNewRow(rows, "SecUser", map.get("SecUser"));
		}
		if (map.containsKey("SecLoginlog")) {
			addNewRow(rows, "SecLoginlog", map.get("SecLoginlog"));
		}
		if (map.containsKey("CountryCode")) {
			addNewRow(rows, "CountryCode", map.get("CountryCode"));
		}
		if (map.containsKey("IpToCountry")) {
			addNewRow(rows, "IpToCountry", map.get("IpToCountry"));
		}
		if (map.containsKey("CalendarEvents")) {
			addNewRow(rows, "CalendarEvents", map.get("CalendarEvents"));
		}
		if (map.containsKey("YouTubeLinks")) {
			addNewRow(rows, "YouTubeLinks", map.get("YouTubeLinks"));
		}

	}

	/**
	 * Shows the zk application statistic data. <br>
	 */
	private void showStatistic() {

		/**
		 * These Statistic Class is activated in the zk.xml
		 */
		// Statistic stat = de.forsthaus.statistic.Statistic.getStatistic();
		// new Statistic class since 20.08.2010
		final FDStatistic stat = de.forsthaus.statistic.FDStatistic.getStatistic();

		final Panel panel = new Panel();
		panel.setWidth("400px");
		panel.setBorder("normal");
		panel.setStyle("align:left; color:red;");
		panel.setParent(this.tableChildrenStatistic);

		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);
		panelchildren.setStyle("background-color: #EBEBEB;");

		final Caption caption = new Caption();
		caption.setParent(panel);
		caption.setImage("/images/icons/monitorView.gif");
		caption.setLabel("Application Statistic");
		caption.setStyle("color: #000000;font-weight:bold; text-align:left ");

		final Grid grid = new Grid();
		grid.setWidth("100%");
		grid.setParent(panelchildren);

		final Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);

		final Column column1 = new Column();
		column1.setWidth("65%");
		column1.setLabel("Subject");
		column1.setParent(columns);
		final Column column2 = new Column();
		column2.setWidth("35%");
		column2.setLabel("value");
		column2.setParent(columns);

		final Rows rows = new Rows();
		rows.setParent(grid);

		// short date
		String date = ZksampleDateFormat.getDateTimeFormater().format(stat.getStartTime());

		addNewRow(rows, "Application Start-Time", date);
		addNewRow(rows, "Application runing hours", getRoundedDouble(stat.getRuningHours()));

		addNewRow(rows, "Count of active Desktops", String.valueOf(stat.getActiveDesktopCount()));
		addNewRow(rows, "Count of active Sessions", String.valueOf(stat.getActiveSessionCount()));
		addNewRow(rows, "Count of active Updates", String.valueOf(stat.getActiveUpdateCount()));

		addNewRow(rows, "Average Count of active Desktops/hour", getRoundedDouble(stat.getAverageDesktopCount())); // String.valueOf(stat.getAverageDesktopCount()));
		addNewRow(rows, "Average Count of active Sessions/hour", getRoundedDouble(stat.getAverageSessionCount()));
		addNewRow(rows, "Average Count of active Updates/hour", getRoundedDouble(stat.getAverageUpdateCount()));

		addNewRow(rows, "Count of total Desktops since start", String.valueOf(stat.getTotalDesktopCount()));
		addNewRow(rows, "Count of total Sessions since start", String.valueOf(stat.getTotalSessionCount()));
		addNewRow(rows, "Count of total Updates since start", String.valueOf(stat.getTotalUpdateCount()));

		// Get the number of processors that are available for the JAVA VM
		final int countCPU = Runtime.getRuntime().availableProcessors();
		addNewRow(rows, "available CPUs to the JAVA VM", countCPU, "red");
	}

	/**
	 * Round a double value to a string with two digits.
	 * 
	 * @param d
	 * @return
	 */
	private String getRoundedDouble(double d) {
		String result = "";

		final DecimalFormat df = new DecimalFormat("0.00");
		result = df.format(d);

		return result;
	}

	/**
	 * Creates and shows the buttons for creating additionally <br>
	 * customer demo data.<br>
	 */
	private void createButtons() {

		final Panel panel = new Panel();
		// panel.setTitle("Demo Customers");
		panel.setWidth("240px");
		panel.setBorder("normal");
		panel.setStyle("align:left; color:red;");
		panel.setParent(this.panelChildren_Buttons);

		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);
		panelchildren.setStyle("background-color: #EBEBEB;");

		final Caption caption = new Caption();
		caption.setParent(panel);
		caption.setImage("/images/icons/advice_16x16.gif");
		caption.setLabel("Demo Customers");
		caption.setStyle("color: #000000;font-weight:bold; text-align:left ");

		final Grid grid = new Grid();
		grid.setWidth("100%");
		grid.setParent(panelchildren);

		final Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);

		final Column column1 = new Column();
		column1.setWidth("100%");
		column1.setLabel("values are randomly created");
		column1.setParent(columns);

		final Rows rows = new Rows();
		rows.setParent(grid);

		final Row row = new Row();
		row.setParent(rows);

		this.Vbox_Buttons = new Vbox();
		this.Vbox_Buttons.setParent(panelchildren);

		this.div_Buttons = new Div();
		this.div_Buttons.setWidth("100%");
		this.div_Buttons.setHeight("100%");
		this.div_Buttons.setStyle("padding: 10px;");
		this.div_Buttons.setParent(this.Vbox_Buttons);

		/* 100. Button */
		final Div divBtn1 = new Div();
		divBtn1.setStyle("align: center");
		divBtn1.setParent(this.div_Buttons);

		this.btn100 = new Button();
		this.btn100.setId("btn100");
		this.btn100.setLabel("insert 100");
		this.btn100.setImage("/images/icons/import_16x16.gif");
		this.btn100.setTooltiptext("Insert 100 randomly created customer records");
		this.btn100.setParent(divBtn1);

		this.btn100.addEventListener("onClick", new OnClick100Eventlistener());

		/* Separator */
		createNewSeparator(this.div_Buttons, "horizontal", false, "5", "");
		createNewSeparator(this.div_Buttons, "horizontal", false, "5", "");

		/* 1.000 Button */
		final Div divBtn2 = new Div();
		divBtn2.setStyle("align: center;");
		divBtn2.setParent(this.div_Buttons);

		this.btn1000 = new Button();
		this.btn1000.setId("btn1000");
		this.btn1000.setLabel("insert 1.000");
		this.btn1000.setImage("/images/icons/import_16x16.gif");
		this.btn1000.setTooltiptext("Insert 1.000 randomly created customer records");
		this.btn1000.setParent(divBtn2);

		this.btn1000.addEventListener("onClick", new OnClick1000Eventlistener());

		createNewSeparator(this.div_Buttons, "horizontal", false, "5", "");

		this.Vbox_Buttons.setParent(row);

		createNewSeparator(this.panelChildren_Buttons, "horizontal", false, "5", "#EBEBEB");

	}

	/**
	 * Creates and shows the Chart for Users online .<br>
	 * The number of users are randomly generated.<br>
	 */
	private void showUsersOnlineChart() {

		final Panel panel = new Panel();
		panel.setWidth("240px");
		panel.setHeight("265px");
		panel.setBorder("normal");
		panel.setParent(this.panelChildren_Buttons);

		final Panelchildren panelchildren = new Panelchildren();
		panelchildren.setParent(panel);
		panelchildren.setStyle("background-color: #EBEBEB;");

		final Caption caption = new Caption();
		caption.setParent(panel);
		caption.setImage("/images/icons/console_view.gif");
		caption.setLabel("Users online");
		caption.setStyle("color: #000000;font-weight:bold; text-align:left ");

		final Div div = new Div();
		div.setStyle("padding: 4px;");
		div.setWidth("100%");
		div.setHeight("100%");
		div.setParent(panelchildren);

		// Chart Dial
		final Random random = new Random();
		final int val = random.nextInt(100);

		final DialModel dialmodel = new DialModel();
		final DialModelScale scale = dialmodel.newScale(0.0, 500.0, -120.0, -300.0, 100.0, 4);// scale's

		// configuration data
		scale.setText("Users");
		scale.newRange(450, 500, "#FF0000", 0.83, 0.89);
		scale.newRange(360, 450, "#FFC426", 0.83, 0.89);
		scale.setValue(val);

		final Chart chart = new Chart();
		chart.setType("dial");
		chart.setWidth("228px");
		chart.setHeight("200px");
		chart.setThreeD(true);
		chart.setFgAlpha(128);
		chart.setBgColor("#FFFFFF");
		chart.setModel(dialmodel);
		chart.setParent(div);

	}

	/**
	 * Creates a new separator to a parent component.<br>
	 * 
	 * @param parent
	 * @param orientation
	 * @param isBarVisible
	 * @param spacing
	 * @param bkgrColor
	 * @return
	 */
	private Separator createNewSeparator(Component parent, String orientation, boolean isBarVisible, String spacing, String bkgrColor) {

		final Separator sep = new Separator();

		sep.setOrient(orientation);
		sep.setBar(isBarVisible);

		if (!StringUtils.trim(bkgrColor).isEmpty()) {
			sep.setStyle("background-color:" + bkgrColor);
		}

		if (StringUtils.isEmpty(spacing)) {
			sep.setSpacing(0 + "px");
		} else {
			sep.setSpacing(spacing + "px");
		}

		sep.setParent(parent);

		return sep;
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
			// Format the value
			NumberFormat formatter = new DecimalFormat("###,###.###");
			String formattedInteger = formatter.format(value);
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
	 */
	private void addNewRow(Rows rowParent, String tableName, Object value, String color) {

		Row row = new Row();

		Html html_TableName = new Html(tableName);
		html_TableName.setStyle("padding-left: 5px; color: " + color + ";");
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
			// Format the value
			NumberFormat formatter = new DecimalFormat("###,###.###");
			String formattedInteger = formatter.format(value);
			html_RecordCount = new Html(String.valueOf(value));
		} else
			html_RecordCount = new Html(String.valueOf(value));

		html_RecordCount.setStyle("padding-right: 5px; color: " + color + ";");
		Div divValue = new Div();
		divValue.setAlign("right");
		divValue.appendChild(html_RecordCount);

		row.appendChild(divKey);
		row.appendChild(divValue);
		row.setParent(rowParent);

	}

	/**
	 * EventListener for the inserted 1000 customers Button.<br>
	 * The creation of the records runs in an Echo-event that shows the user a
	 * busy message.
	 */
	public final class OnClick1000Eventlistener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {
			// we create the records in an Echo-event to show a message to the
			// user for this long running operation.
			Clients.showBusy(Labels.getLabel("message.Information.LongOperationIsRunning"), true);
			Events.echoEvent("onCreate1000Customers", InitApplicationCtrl.this.startWindow, null);
		}
	}

	/**
	 * Calls the long running method that creates the records and closes the
	 * echo event message if ready.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate1000Customers(Event event) throws Exception {

		createDemoCustomers(1000); // the long running process
		Clients.clearBusy(); // showBusy("", false); // close the message
	}

	/**
	 * EventListener for the inserted 100 customers Button.<br>
	 * The creation of the records runs in an Echo-event that shows the user a
	 * busy message.
	 */
	public final class OnClick100Eventlistener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			// we create the records in an Echo-event to show a message to the
			// user for this long running operation.
			Clients.showBusy(Labels.getLabel("message.Information.LongOperationIsRunning"), true);
			Events.echoEvent("onCreate100Customers", InitApplicationCtrl.this.startWindow, null);
		}
	}

	/**
	 * Calls the long running method that creates the records and closes the
	 * echo event message if ready.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate100Customers(Event event) throws Exception {

		createDemoCustomers(100);
		Clients.clearBusy(); // showBusy("", false); // close the message
	}

	/**
	 * Gets the total records of customers table.<br>
	 * 
	 * @return total count of customer records.
	 */
	private int getTotalCountRecordsForCustomer() {

		int recCount = 0;
		recCount = getCustomerService().getCountAllCustomers();

		return recCount;
	}

	/**
	 * Creates new demo customer records with randoms values.<br>
	 * 
	 * @param newRecords
	 *            Number of records to insert.
	 * @throws InterruptedException
	 */
	public void createDemoCustomers(int newRecords) throws InterruptedException {

		/* check if over 200.000 records in DB */
		if (getTotalCountRecordsForCustomer() >= 250000) {

			/* close the echo event bussy message */
			Clients.showBusy("", false);

			final String message = Labels.getLabel("Demo.not_more_than_250000_records");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);

			return;
		}

		/* fix demo branch with id=1000 */
		final Branche branche = getBrancheService().getBrancheById(1000);

		final int countRecords = newRecords;

		final RandomDataEngine randomDataEngine = new RandomDataEngine();

		for (int j = 0; j < countRecords; j++) {
			final Customer customer = getCustomerService().getNewCustomer();

			customer.setKunName1(randomDataEngine.getRandomManFirstname());
			customer.setKunName2(randomDataEngine.getRandomLastname());
			customer.setKunMatchcode(customer.getKunName2().toUpperCase());
			customer.setKunOrt(randomDataEngine.getRandomCity());
			customer.setBranche(branche);
			customer.setKunMahnsperre(false);

			// if no customer no. is set by user than take
			// the max PrimaryKey + 1
			if (customer.getKunNr().isEmpty()) {
				customer.setKunNr(String.valueOf(getCustomerService().getMaxCustomerId() + 1));
			}

			getCustomerService().saveOrUpdate(customer);
		}

		tableChildrenRecords.getChildren().clear();
		countDemoData();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		if (this.customerService == null) {
			this.customerService = (CustomerService) SpringUtil.getBean("customerService");
			setCustomerService(this.customerService);
		}
		return this.customerService;
	}

	public void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	public BrancheService getBrancheService() {
		if (this.brancheService == null) {
			this.brancheService = (BrancheService) SpringUtil.getBean("brancheService");
			setBrancheService(this.brancheService);
		}
		return this.brancheService;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

	public CommonService getCommonService() {
		if (this.commonService == null) {
			this.commonService = (CommonService) SpringUtil.getBean("commonService");
			setCommonService(this.commonService);
		}
		return this.commonService;
	}

}
