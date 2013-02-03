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
package de.forsthaus.webui.security.right.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DJBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.webui.reports.template.DJLetterHead;
import de.forsthaus.webui.reports.template.DJReportStyles;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * A simple report implemented with the DynamicJasper framework.<br>
 * <br>
 * This report shows a list of Security Single Rights.<br>
 * <br>
 * The report uses the DynamicReportBuilder that allowed more control over the
 * columns. Additionally the report uses a CustomExpression for showing how to
 * work with it. The CustomExpression checks a boolean field and writes only a
 * 'T' for 'true and 'F' as 'False.<br>
 * 
 * @author bbruhns
 * @author sge
 * 
 */
public class TestDJReport extends Window implements Serializable {

	private static final long serialVersionUID = 1L;

	private Iframe iFrame;
	private ByteArrayOutputStream output;
	private InputStream mediais;
	private AMedia amedia;
	private String zksample2title = Labels.getLabel("print.Title.Security_single_rights_list");

	private SecurityService securityService;
	private DJReportStyles djReportStyles;

	public TestDJReport(Component parent) throws InterruptedException {
		super();
		this.setParent(parent);

		try {
			doPrint();
		} catch (Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	public void doPrint() throws JRException, ColumnBuilderException, ClassNotFoundException, IOException, DJBuilderException {

		// Localized column headers
		String rigName = Labels.getLabel("listheader_SecRightList_rigName.label");
		String rigType = Labels.getLabel("listheader_SecRightList_rigType.label");

		DynamicReportBuilder drbContent = new DynamicReportBuilder();

		// get the styles
		DJReportStyles djrst = getDjReportStyles();

		DynamicReport drHeaderSubreport = new DJLetterHead();
		drbContent.addSubreportInGroupHeader(1, drHeaderSubreport, new ClassicLayoutManager(), "", DJConstants.DATA_SOURCE_ORIGIN_FIELD, DJConstants.DATA_SOURCE_TYPE_COLLECTION);

		// Sets the Report Columns, header, Title, Groups, Etc Formats
		// DynamicJasper documentation
		drbContent.setTitle(zksample2title);
		drbContent.setTitleStyle(djrst.getTitleStyle());
		drbContent.setWhenNoDataAllSectionNoDetail();
		// drb.setSubtitle("DynamicJasper Sample");
		drbContent.setSubtitleStyle(djrst.getSubtitleStyle());

		drbContent.setHeaderHeight(20);
		drbContent.setDetailHeight(15);
		drbContent.setFooterVariablesHeight(10);
		drbContent.setMargins(20, 20, 30, 15);

		drbContent.setDefaultStyles(djrst.getTitleStyle(), djrst.getSubtitleStyle(), djrst.getHeaderStyleText(), djrst.getColumnStyleText());
		drbContent.setPrintBackgroundOnOddRows(true);

		/**
		 * Columns Definitions. A new ColumnBuilder instance for each column.
		 */
		// Right name
		AbstractColumn colRightName = ColumnBuilder.getNew().setColumnProperty("rigName", String.class.getName()).build();
		colRightName.setTitle(rigName);
		colRightName.setWidth(60);
		colRightName.setHeaderStyle(djrst.getHeaderStyleText());
		colRightName.setStyle(djrst.getColumnStyleText());
		// Right type
		AbstractColumn colRightType = ColumnBuilder.getNew().setCustomExpression(getMyRightTypExpression()).build();
		colRightType.setTitle(rigType);
		colRightType.setWidth(40);
		colRightType.setHeaderStyle(djrst.getHeaderStyleText());
		colRightType.setStyle(djrst.getColumnStyleText());

		// Add the columns to the report in the whished order
		drbContent.addColumn(colRightName);
		drbContent.addColumn(colRightType);

		// // TEST
		// Style atStyle = new
		// StyleBuilder(true).setFont(Font.COMIC_SANS_SMALL).setTextColor(Color.red).build();
		// /**
		// * Adding many autotexts in the same position (header/footer and
		// * aligment) makes them to be one on top of the other
		// */
		//
		// AutoText created = new AutoText(Labels.getLabel("common.Created") +
		// ": " + ZksampleDateFormat.getDateTimeFormater().format(new Date()),
		// AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
		// created.setWidth(new Integer(120));
		// created.setStyle(atStyle);
		// drb.addAutoText(created);
		//
		// AutoText autoText = new AutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y,
		// AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
		// autoText.setWidth(new Integer(20));
		// autoText.setStyle(atStyle);
		// drb.addAutoText(autoText);
		//
		// AutoText name1 = new AutoText("The Zksample2 Ltd.",
		// AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		// name1.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		// AutoText name2 = new AutoText("Software Consulting",
		// AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		// name2.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		// AutoText street = new AutoText("256, ZK Direct RIA Street ",
		// AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		// street.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		// AutoText city = new AutoText("ZKoss City", AutoText.POSITION_HEADER,
		// HorizontalBandAlignment.LEFT);
		// city.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		// drb.addAutoText(name1).addAutoText(name2).addAutoText(street).addAutoText(city);
		// // Footer
		// AutoText footerText = new
		// AutoText("Help to prevent the global warming by writing cool software.",
		// AutoText.POSITION_FOOTER, HorizontalBandAlignment.CENTER);
		// footerText.setStyle(djrst.getFooterStyle());
		// drb.addAutoText(footerText);

		// ADD ALL USED FIELDS to the report.
		drbContent.addField("rigType", Integer.class.getName());

		drbContent.setUseFullPageWidth(true); // use full width of the page

		DynamicReport drContent;
		drContent = drbContent.build(); // build the report

		// Get information from database
		List<SecRight> resultList = getSecurityService().getAllRights();

		// Create Datasource and put it in Dynamic Jasper Format
		List data = new ArrayList(resultList.size());

		for (SecRight obj : resultList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("rigName", obj.getRigName());
			map.put("rigType", obj.getRigType());
			data.add(map);
		}

		// Generate the Jasper Print Object
		JRDataSource ds = new JRBeanCollectionDataSource(data);

		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(drContent, new ClassicLayoutManager(), ds);

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

	/**
	 * A CustomExpression that checks a boolean value and writes a 'T' as true
	 * and a 'F' as false.<br>
	 * 
	 * @return
	 */
	@SuppressWarnings("serial")
	private CustomExpression getMyRightTypExpression() {
		return new CustomExpression() {

			public Object evaluate(Map fields, Map variables, Map parameters) {

				String result = "";

				/**
				 * Int | Type <br>
				 * --------------------------<br>
				 * 0 | Page <br>
				 * 1 | Menu Category <br>
				 * 2 | Menu Item <br>
				 * 3 | Method/Event <br>
				 * 4 | DomainObject/Property <br>
				 * 5 | Tab <br>
				 * 6 | Component <br>
				 */

				int rigType = (Integer) fields.get("rigType");

				if (rigType == 0) {
					result = "Page";
				} else if (rigType == 1) {
					result = "Menu Category";
				} else if (rigType == 2) {
					result = "Menu Item";
				} else if (rigType == 3) {
					result = "Method/Event";
				} else if (rigType == 4) {
					result = "DomainObject/Property";
				} else if (rigType == 5) {
					result = "Tab";
				} else if (rigType == 6) {
					result = "Component";
				}
				return result;
			}

			public String getClassName() {
				return String.class.getName();
			}
		};
	}

	private void callReportWindow(AMedia aMedia, String format) {
		boolean modal = true;

		this.setTitle("Dynamic JasperReports. Sample Report for the zk framework.");
		this.setId("ReportWindow");
		this.setVisible(true);
		this.setMaximizable(true);
		this.setMinimizable(true);
		this.setSizable(true);
		this.setClosable(true);
		this.setHeight("100%");
		this.setWidth("80%");
		this.addEventListener("onClose", new OnCloseReportEventListener());

		iFrame = new Iframe();
		iFrame.setId("jasperReportId");
		iFrame.setWidth("100%");
		iFrame.setHeight("100%");
		iFrame.setContent(aMedia);
		iFrame.setParent(this);

		if (modal == true) {
			try {
				this.doModal();
			} catch (SuspendNotAllowedException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		this.removeEventListener("onClose", new OnCloseReportEventListener());

		// TODO check this
		try {
			amedia.getStreamData().close();
			output.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		this.onClose();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public SecurityService getSecurityService() {
		if (securityService == null) {
			securityService = (SecurityService) SpringUtil.getBean("securityService");
		}
		return securityService;
	}

	public void setDjReportStyles(DJReportStyles djReportStyles) {
		this.djReportStyles = djReportStyles;
	}

	public DJReportStyles getDjReportStyles() {
		if (djReportStyles == null) {
			djReportStyles = (DJReportStyles) SpringUtil.getBean("djReportStyles");
		}
		return djReportStyles;
	}

}
