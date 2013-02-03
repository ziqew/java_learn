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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.ChartData;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.service.ChartService;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/customer/customerChart.zul file. <br>
 * <br>
 * In this controller we evaluate the pressed buttons for the several<br>
 * charts and fill them with data and shows the result graphical.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * 
 * @changes 11/07/2009:bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class CustomerChartCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3464049954099545446L;
	private final static Logger logger = Logger.getLogger(CustomerChartCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window customerChartWindow; // autowire
	protected Div div_chartArea;

	// Toolbar Buttons
	protected Button button_CustomerChart_PieChart; // autowire
	protected Button button_CustomerChart_PieChart3D; // autowire
	protected Button button_CustomerChart_RingChart; // autowire
	protected Button button_CustomerChart_BarChart; // autowire
	protected Button button_CustomerChart_BarChart3D; // autowire
	protected Button button_CustomerChart_StackedBar; // autowire
	protected Button button_CustomerChart_StackedBar3D; // autowire
	protected Button button_CustomerChart_LineBar; // autowire
	protected Button button_CustomerChart_LineBar3D; // autowire

	// Button controller for the CRUD buttons
	protected Button btnHelp; // autowire

	protected int chartWidth = 785;
	protected int chartHeight = 400;

	// ServiceDAOs / Domain Classes
	private transient Customer customer;
	private transient ChartService chartService;

	/**
	 * default constructor.<br>
	 */
	public CustomerChartCtrl() {
		super();
	}

	public void onCreate$customerChartWindow(Event event) throws Exception {

		/* set components visible dependent of the users rights */
		doCheckRights();

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("customer")) {
			customer = (Customer) args.get("customer");
			setCustomer(customer);
		} else {
			setCustomer(null);
		}

	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		// customerChartWindow.setVisible(workspace.isAllowed(
		// "window_BranchesList"));
		// btnHelp.setVisible(workspace.isAllowed("button_CustomerDialog_btnHelp"
		// ));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * onClick button PieChart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_PieChart(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultPieDataset pieDataset = new DefaultPieDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				pieDataset.setValue(key + " " + amount, new Double(chartData.getChartKunInvoiceAmount().doubleValue()));
			}

			String title = "Monthly amount for year 2009";
			JFreeChart chart = ChartFactory.createPieChart(title, pieDataset, true, true, true);
			PiePlot plot = (PiePlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Pie Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button PieChart 3D. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_PieChart3D(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultPieDataset pieDataset = new DefaultPieDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				pieDataset.setValue(key + " " + amount, new Double(chartData.getChartKunInvoiceAmount().doubleValue()));
			}

			String title = "Monthly amount for year 2009";
			JFreeChart chart = ChartFactory.createPieChart3D(title, pieDataset, true, true, true);
			PiePlot3D plot = (PiePlot3D) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Pie Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(this.div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Ring Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_RingChart(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultPieDataset pieDataset = new DefaultPieDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				pieDataset.setValue(key + " " + amount, new Double(chartData.getChartKunInvoiceAmount().doubleValue()));
			}

			String title = "Monthly amount for year 2009";
			JFreeChart chart = ChartFactory.createRingChart(title, pieDataset, true, true, true);
			RingPlot plot = (RingPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Ring Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(this.div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			final Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Bar Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_BarChart(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createBarChart(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);

			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Bar Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Bar Chart 3D. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_BarChart3D(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createBarChart3D(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Bar Chart 3D", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);
		}
	}

	/**
	 * onClick button Stacked Bar Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_StackedBar(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createStackedBarChart(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Stacked Bar Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Stacked Bar 3D Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_StackedBar3D(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), key + " " + amount, key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createStackedBarChart3D(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Stacked Bar Chart 3D", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Line Bar Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_LineBar(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), "2009", key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createLineChart(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Line Bar Chart", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	/**
	 * onClick button Line Bar 3D Chart. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onClick$button_CustomerChart_LineBar3D(Event event) throws InterruptedException, IOException {
		// logger.debug(event.toString());

		div_chartArea.getChildren().clear();

		// get the customer ID for which we want show a chart
		long kunId = getCustomer().getId();

		// get a list of data
		List<ChartData> kunAmountList = getChartService().getChartDataForCustomer(kunId);

		if (kunAmountList.size() > 0) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (ChartData chartData : kunAmountList) {

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(chartData.getChartKunInvoiceDate());

				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				String key = String.valueOf(month) + "/" + String.valueOf(year);

				BigDecimal bd = chartData.getChartKunInvoiceAmount().setScale(15, 3);
				String amount = String.valueOf(bd.doubleValue());

				// fill the data
				dataset.setValue(new Double(chartData.getChartKunInvoiceAmount().doubleValue()), "2009", key + " " + amount);
			}

			String title = "Monthly amount for year 2009";
			PlotOrientation po = PlotOrientation.VERTICAL;
			JFreeChart chart = ChartFactory.createLineChart3D(title, "Month", "Amount", dataset, po, true, true, true);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setForegroundAlpha(0.5f);
			BufferedImage bi = chart.createBufferedImage(chartWidth, chartHeight, BufferedImage.TRANSLUCENT, null);
			byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);

			AImage chartImage = new AImage("Line Bar Chart 3D", bytes);

			Image img = new Image();
			img.setContent(chartImage);
			img.setParent(div_chartArea);

		} else {

			div_chartArea.getChildren().clear();

			Label label = new Label();
			label.setValue("This customer have no data for showing in a chart!");

			label.setParent(div_chartArea);

		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setChartService(ChartService chartService) {
		this.chartService = chartService;
	}

	public ChartService getChartService() {
		return this.chartService;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return this.customer;
	}
}
