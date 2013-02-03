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
package de.forsthaus.webui.security.group.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import de.forsthaus.backend.model.SecGroup;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * A simple report implemented with the DynamicJasper framework.<br>
 * <br>
 * This report shows a list of branches.<br>
 * <br>
 * The report uses the FastReportBuilder that have many parameters defined as
 * defaults, so it's very easy to create a simple report with it.<br>
 * 
 * @author bbruhns
 * @author sge
 * 
 */
public class SecGroupSimpleDJReport extends Window implements Serializable {

	private static final long serialVersionUID = 1L;

	private Iframe iFrame;
	private ByteArrayOutputStream output;
	private InputStream mediais;
	private AMedia amedia;
	private final String zksample2title = "[Zksample2] DynamicJasper Report Sample";

	public SecGroupSimpleDJReport(Component parent) throws InterruptedException {
		super();
		this.setParent(parent);

		try {
			doPrint();
		} catch (final Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	public void doPrint() throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

		FastReportBuilder drb = new FastReportBuilder();
		DynamicReport dr;

		/**
		 * Set the styles. In a report created with DynamicReportBuilder we do
		 * this in an other way.
		 */
		// Rows content
		Style columnStyleNumbers = new Style();
		columnStyleNumbers.setFont(Font.VERDANA_SMALL);
		columnStyleNumbers.setHorizontalAlign(HorizontalAlign.RIGHT);

		// Header for number row content
		Style columnStyleNumbersBold = new Style();
		columnStyleNumbersBold.setFont(Font.VERDANA_MEDIUM_BOLD);
		columnStyleNumbersBold.setHorizontalAlign(HorizontalAlign.RIGHT);
		columnStyleNumbersBold.setBorderBottom(Border.PEN_1_POINT);

		// Rows content
		Style columnStyleText = new Style();
		columnStyleText.setFont(Font.VERDANA_SMALL);
		columnStyleText.setHorizontalAlign(HorizontalAlign.LEFT);

		// Header for String row content
		Style columnStyleTextBold = new Style();
		columnStyleTextBold.setFont(Font.VERDANA_MEDIUM_BOLD);
		columnStyleTextBold.setHorizontalAlign(HorizontalAlign.LEFT);
		columnStyleTextBold.setBorderBottom(Border.PEN_1_POINT);

		// Subtitle
		Style subtitleStyle = new Style();
		subtitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		subtitleStyle.setFont(Font.VERDANA_MEDIUM_BOLD);

		// Localized column headers
		String grpShortdescription = Labels.getLabel("listheader_SecGroupList_grpShortdescription.label");
		String grpLongdescription = Labels.getLabel("listheader_SecGroupList_grpLongdescription.label");

		drb.addColumn(grpShortdescription, "grpShortdescription", String.class.getName(), 40, columnStyleText, columnStyleTextBold);
		drb.addColumn(grpLongdescription, "grpLongdescription", String.class.getName(), 100, columnStyleText, columnStyleTextBold);

		// Sets the Report Columns, header, Title, Groups, Etc Formats
		// DynamicJasper documentation
		drb.setTitle(this.zksample2title);
		drb.setSubtitle("List of security roles: " + ZksampleDateFormat.getDateFormater().format(new Date()));
		drb.setSubtitleStyle(subtitleStyle);
		drb.setPrintBackgroundOnOddRows(true);
		drb.setUseFullPageWidth(true);
		dr = drb.build();

		// Get information from database
		SecurityService as = (SecurityService) SpringUtil.getBean("securityService");
		List<SecGroup> resultList = as.getAllGroups();

		// Create Datasource and put it in Dynamic Jasper Format
		List data = new ArrayList(resultList.size());

		for (SecGroup obj : resultList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("grpShortdescription", obj.getGrpShortdescription());
			map.put("grpLongdescription", obj.getGrpLongdescription());
			data.add(map);
		}

		// Generate the Jasper Print Object
		JRDataSource ds = new JRBeanCollectionDataSource(data);
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

			callReportWindow(this.amedia, "RTF-DOC");
		}
	}

	private void callReportWindow(AMedia aMedia, String format) {
		final boolean modal = true;

		setTitle("Dynamic JasperReports. Sample Report for ZKoss");
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

}
