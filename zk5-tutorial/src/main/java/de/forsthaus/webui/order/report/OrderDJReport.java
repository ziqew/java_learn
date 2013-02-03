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
package de.forsthaus.webui.order.report;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ExpressionHelper;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.model.Order;
import de.forsthaus.backend.model.Orderposition;
import de.forsthaus.backend.service.OrderService;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * A report implemented with the DynamicJasper framework.<br>
 * <br>
 * This report shows an Order with its orderPositions.<br>
 * <br>
 * The report uses the DynamicReportBuilder that allowed more control over the
 * columns. <br>
 * This report have a GrandTotal sum displayed at the end.<br>
 * Uses a money Pattern for all money fields.<br>
 * 
 * @author bbruhns
 * @author sge
 * 
 */
public class OrderDJReport extends Window implements Serializable {

	private static final long serialVersionUID = 1L;

	private Iframe iFrame;
	private ByteArrayOutputStream output;
	private InputStream mediais;
	private AMedia amedia;
	private final String zksample2title = Labels.getLabel("print.Title.Order");

	// Data Beans
	private Order order;
	private List<Orderposition> orderpositions;
	private Customer customer;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param anOrder
	 * @throws InterruptedException
	 */
	public OrderDJReport(Component parent, Order anOrder) throws InterruptedException {
		super();
		this.setParent(parent);
		this.setOrder(anOrder);
		this.setCustomer(getOrder().getCustomer());

		try {
			doPrint();
		} catch (final Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.getLocalizedMessage());
			Logger.getLogger(getClass()).error("", e);
		}
	}

	public void doPrint() throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

		// Localized column headers
		String quantity = Labels.getLabel("listheader_OrderPosList2_Count.label");
		String articleText = Labels.getLabel("listheader_OrderPosList2_Shorttext.label");
		String singlePrice = Labels.getLabel("listheader_OrderPosList2_SinglePrice.label");
		String lineSum = Labels.getLabel("listheader_OrderPosList2_WholePrice.label");

		/**
		 * STYLES
		 */
		// Styles: Title
		Style titleStyle = new Style();
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		Font titleFont = Font.VERDANA_BIG_BOLD;
		titleFont.setUnderline(true);
		titleStyle.setFont(titleFont);

		// Styles: Subtitle
		Style subtitleStyle = new Style();
		subtitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		subtitleStyle.setFont(Font.VERDANA_MEDIUM_BOLD);

		// Styles: Subtitle underlined
		Style subtitleStyleUL = new Style();
		subtitleStyleUL.setHorizontalAlign(HorizontalAlign.LEFT);
		Font subtitleULFont = Font.VERDANA_MEDIUM_BOLD;
		subtitleULFont.setUnderline(true);
		subtitleStyleUL.setFont(titleFont);

		// ColumnHeader Style Text (left-align)
		final Style columnHeaderStyleText = new Style();
		columnHeaderStyleText.setFont(Font.VERDANA_MEDIUM_BOLD);
		columnHeaderStyleText.setHorizontalAlign(HorizontalAlign.LEFT);
		columnHeaderStyleText.setBorderBottom(Border.PEN_1_POINT);

		// ColumnHeader Style Text (right-align)
		Style columnHeaderStyleNumber = new Style();
		columnHeaderStyleNumber.setFont(Font.VERDANA_MEDIUM_BOLD);
		columnHeaderStyleNumber.setHorizontalAlign(HorizontalAlign.RIGHT);
		columnHeaderStyleNumber.setBorderBottom(Border.PEN_1_POINT);

		// Footer Style (center-align)
		Style footerStyle = new Style();
		footerStyle.setFont(Font.VERDANA_SMALL);
		footerStyle.getFont().setFontSize(8);
		footerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		footerStyle.setBorderTop(Border.PEN_1_POINT);

		// Rows content Style (left-align)
		final Style columnDetailStyleText = new Style();
		columnDetailStyleText.setFont(Font.VERDANA_SMALL);
		columnDetailStyleText.setHorizontalAlign(HorizontalAlign.LEFT);

		// Rows content Style (right-align)
		Style columnDetailStyleNumbers = new Style();
		columnDetailStyleNumbers.setFont(Font.VERDANA_SMALL);
		columnDetailStyleNumbers.setHorizontalAlign(HorizontalAlign.RIGHT);

		// TotalSum (left-right)
		Style footerStyleTotalSumValue = new Style();
		footerStyleTotalSumValue.setFont(Font.VERDANA_MEDIUM_BOLD);
		footerStyleTotalSumValue.setHorizontalAlign(HorizontalAlign.RIGHT);
		footerStyleTotalSumValue.setBorderTop(Border.PEN_1_POINT);

		DynamicReportBuilder drb = new DynamicReportBuilder();
		DynamicReport dr;

		// Sets the Report Columns, header, Title, Groups, Etc Formats
		// DynamicJasper documentation
		drb.setTitle(this.zksample2title);
		// drb.setSubtitle("DynamicJasper Sample");
		drb.setSubtitleStyle(subtitleStyle);

		drb.setHeaderHeight(20);
		drb.setDetailHeight(15);
		drb.setFooterVariablesHeight(10);
		drb.setMargins(20, 20, 30, 15);

		drb.setDefaultStyles(titleStyle, subtitleStyle, columnHeaderStyleText, columnDetailStyleText);
		drb.setPrintBackgroundOnOddRows(true);

		/**
		 * Adding many autotexts in the same position (header/footer and
		 * aligment) makes them to be one on top of the other
		 */
		Style atStyle = new StyleBuilder(true).setFont(Font.COMIC_SANS_SMALL).setTextColor(Color.red).build();

		AutoText created = new AutoText(Labels.getLabel("common.Created") + ": " + ZksampleDateFormat.getDateTimeFormater().format(new Date()), AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
		created.setWidth(new Integer(120));
		created.setStyle(atStyle);
		drb.addAutoText(created);

		AutoText autoText = new AutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
		autoText.setWidth(new Integer(20));
		autoText.setStyle(atStyle);
		drb.addAutoText(autoText);

		AutoText atCustomerHeader = new AutoText(Labels.getLabel("orderDialogWindow.title") + " :  " + getOrder().getAufBezeichnung(), AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		atCustomerHeader.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		atCustomerHeader.setStyle(subtitleStyleUL);
		AutoText name1 = new AutoText(getCustomer().getKunName1(), AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		name1.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		AutoText name2 = new AutoText(getCustomer().getKunName2(), AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		name2.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		AutoText city = new AutoText(getCustomer().getKunOrt(), AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		city.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		AutoText emptyLine = new AutoText("", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		emptyLine.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		drb.addAutoText(atCustomerHeader).addAutoText(emptyLine).addAutoText(name1).addAutoText(name2).addAutoText(city).addAutoText(emptyLine);

		// Footer
		AutoText footerText = new AutoText("Help to prevent the global warming by writing cool software.", AutoText.POSITION_FOOTER, HorizontalBandAlignment.CENTER);
		footerText.setStyle(footerStyle);
		drb.addAutoText(footerText);

		/**
		 * Columns Definitions. A new ColumnBuilder instance for each column.
		 */
		// Quantity
		AbstractColumn colQuantity = ColumnBuilder.getNew().setColumnProperty("aupMenge", BigDecimal.class.getName()).build();
		colQuantity.setTitle(quantity);
		colQuantity.setWidth(40);
		colQuantity.setPattern("#,##0.00");
		colQuantity.setHeaderStyle(columnHeaderStyleNumber);
		colQuantity.setStyle(columnDetailStyleNumbers);

		// Article Text
		AbstractColumn colArticleText = ColumnBuilder.getNew().setColumnProperty("article.artKurzbezeichnung", String.class.getName()).build();
		colArticleText.setTitle(articleText);
		colArticleText.setWidth(100);
		colArticleText.setHeaderStyle(columnHeaderStyleText);
		colArticleText.setStyle(columnDetailStyleText);

		// Single Price
		AbstractColumn colSinglePrice = ColumnBuilder.getNew().setColumnProperty("aupEinzelwert", BigDecimal.class.getName()).build();
		colSinglePrice.setTitle(singlePrice);
		colSinglePrice.setWidth(40);
		colSinglePrice.setPattern("#,##0.00");
		colSinglePrice.setHeaderStyle(columnHeaderStyleNumber);
		colSinglePrice.setStyle(columnDetailStyleNumbers);

		// Line Sum
		AbstractColumn colLineSum = ColumnBuilder.getNew().setColumnProperty("aupGesamtwert", BigDecimal.class.getName()).build();
		colLineSum.setTitle(lineSum);
		colLineSum.setWidth(40);
		// #,##0. €00
		colLineSum.setPattern("#,##0.00");
		colLineSum.setHeaderStyle(columnHeaderStyleNumber);
		colLineSum.setStyle(columnDetailStyleNumbers);

		// Add the columns to the report in the whished order
		drb.addColumn(colQuantity);
		drb.addColumn(colArticleText);
		drb.addColumn(colSinglePrice);
		drb.addColumn(colLineSum);

		/**
		 * Add a global total sum for the lineSum field.
		 */
		drb.addGlobalFooterVariable(colLineSum, DJCalculation.SUM, footerStyleTotalSumValue);
		drb.setGlobalFooterVariableHeight(new Integer(20));
		drb.setGrandTotalLegend(Labels.getLabel("common.Sum"));

		// ADD ALL USED FIELDS to the report.
		// drb.addField("rigType", Integer.class.getName());

		drb.setUseFullPageWidth(true); // use full width of the page
		dr = drb.build(); // build the report

		// Get information from database
		OrderService sv = (OrderService) SpringUtil.getBean("orderService");
		List<Orderposition> resultList = sv.getOrderpositionsByOrder(getOrder());

		// Generate the Jasper Print Object
		JRDataSource ds = new JRBeanCollectionDataSource(resultList);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);

		String outputFormat = "PDF";

		output = new ByteArrayOutputStream();

		if (outputFormat.equalsIgnoreCase("PDF")) {
			JasperExportManager.exportReportToPdfStream(jp, output);
			mediais = new ByteArrayInputStream(output.toByteArray());
			amedia = new AMedia("FirstReport.pdf", "pdf", "application/pdf", mediais);

			callReportWindow(amedia, "PDF");
		} else if (outputFormat.equalsIgnoreCase("XLS")) {
			JExcelApiExporter exporterXLS = new JExcelApiExporter();
			exporterXLS.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jp);
			exporterXLS.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, output);
			exporterXLS.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporterXLS.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporterXLS.setParameter(JExcelApiExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporterXLS.exportReport();
			mediais = new ByteArrayInputStream(output.toByteArray());
			amedia = new AMedia("FileFormatExcel", "xls", "application/vnd.ms-excel", mediais);

			callReportWindow(amedia, "XLS");
		} else if (outputFormat.equalsIgnoreCase("RTF") || outputFormat.equalsIgnoreCase("DOC")) {
			JRRtfExporter exporterRTF = new JRRtfExporter();
			exporterRTF.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporterRTF.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporterRTF.exportReport();
			mediais = new ByteArrayInputStream(output.toByteArray());
			amedia = new AMedia("FileFormatRTF", "rtf", "application/rtf", mediais);

			callReportWindow(amedia, "RTF-DOC");
		}
	}

	private void callReportWindow(AMedia aMedia, String format) {
		final boolean modal = true;

		setTitle("Dynamic JasperReports. Sample Report for the zk framework.");
		setId("ReportWindow");
		setVisible(true);
		setMaximizable(true);
		setMinimizable(true);
		setSizable(true);
		setClosable(true);
		setHeight("100%");
		setWidth("80%");
		addEventListener("onClose", new OnCloseReportEventListener());

		iFrame = new Iframe();
		iFrame.setId("jasperReportId");
		iFrame.setWidth("100%");
		iFrame.setHeight("100%");
		iFrame.setContent(aMedia);
		iFrame.setParent(this);

		if (modal == true) {
			try {
				doModal();
			} catch (final SuspendNotAllowedException e) {
				throw new RuntimeException(e);
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * EventListener for closing the Report Window.<br>
	 * 
	 * @author sge
	 * 
	 */
	public final class OnCloseReportEventListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			closeReportWindow();
		}
	}

	/**
	 * We must clear something to prevent errors or problems <br>
	 * by opening the report a few times. <br>
	 * 
	 * @throws IOException
	 */
	private void closeReportWindow() throws IOException {

		// TODO check this
		try {
			amedia.getStreamData().close();
			output.close();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		onClose();

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setOrderpositions(List<Orderposition> orderpositions) {
		this.orderpositions = orderpositions;
	}

	public List<Orderposition> getOrderpositions() {
		return this.orderpositions;
	}

}
