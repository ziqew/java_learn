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
package de.forsthaus.webui.article.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
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
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import de.forsthaus.backend.model.Article;
import de.forsthaus.backend.service.ArticleService;
import de.forsthaus.webui.reports.template.DJReportStyles;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * A simple report implemented with the DynamicJasper framework.<br>
 * <br>
 * This report shows a list of articles.<br>
 * <br>
 * The report uses the FastReportBuilder that have many parameters defined as
 * defaults, so it's very easy to create a simple report with it.<br>
 * 
 * @author bbruhns
 * @author sge
 * 
 */
public class ArticleSimpleDJReport extends Window implements Serializable {

	private static final long serialVersionUID = 1L;

	private Iframe iFrame;
	private ByteArrayOutputStream output;
	private InputStream mediais;
	private AMedia amedia;
	private final String zksample2title = "[Zksample2] DynamicJasper Report Sample";
	// ReportStyles
	private DJReportStyles djReportStyles;

	public ArticleSimpleDJReport(Component parent) throws InterruptedException {
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

		// get the styles
		DJReportStyles djrst = getDjReportStyles();

		/**
		 * Set the styles. In a report created with DynamicReportBuilder we do
		 * this in an other way.
		 */

		// // Rows content
		// Style columnStyleNumbers = new Style();
		// columnStyleNumbers.setFont(Font.VERDANA_SMALL);
		// columnStyleNumbers.setHorizontalAlign(HorizontalAlign.RIGHT);
		//
		// // Header for number row content
		// Style columnStyleNumbersBold = new Style();
		// columnStyleNumbersBold.setFont(Font.VERDANA_MEDIUM_BOLD);
		// columnStyleNumbersBold.setHorizontalAlign(HorizontalAlign.RIGHT);
		// columnStyleNumbersBold.setBorderBottom(Border.PEN_1_POINT);
		//
		// // Rows content
		// Style columnStyleText = new Style();
		// columnStyleText.setFont(Font.VERDANA_SMALL);
		// columnStyleText.setHorizontalAlign(HorizontalAlign.LEFT);
		//
		// // Header for String row content
		// Style columnStyleTextBold = new Style();
		// columnStyleTextBold.setFont(Font.VERDANA_MEDIUM_BOLD);
		// columnStyleTextBold.setHorizontalAlign(HorizontalAlign.LEFT);
		// columnStyleTextBold.setBorderBottom(Border.PEN_1_POINT);
		//
		// // Subtitle
		// Style subtitleStyle = new Style();
		// subtitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		// subtitleStyle.setFont(Font.VERDANA_MEDIUM_BOLD);

		// Localized column headers
		String artNo = Labels.getLabel("common.Article.No");
		String artShortText = Labels.getLabel("common.Description.Short");
		String artPrice = Labels.getLabel("common.Price");

		drb.addColumn(artNo, "artNr", String.class.getName(), 20, djrst.getColumnStyleText(), djrst.getHeaderStyleText());
		drb.addColumn(artShortText, "artKurzbezeichnung", String.class.getName(), 50, djrst.getColumnStyleText(), djrst.getHeaderStyleText());
		drb.addColumn(artPrice, "artPreis", BigDecimal.class.getName(), 20, djrst.getColumnStyleNumbers(), djrst.getHeaderStyleNumbers());

		// Sets the Report Columns, header, Title, Groups, Etc Formats
		// DynamicJasper documentation
		drb.setTitle(this.zksample2title);
		drb.setSubtitle("Article-List: " + ZksampleDateFormat.getDateFormater().format(new Date()));
		drb.setTitleStyle(djrst.getTitleStyle());
		drb.setPrintBackgroundOnOddRows(true);
		drb.setUseFullPageWidth(true);
		dr = drb.build();

		// Get information from database
		ArticleService as = (ArticleService) SpringUtil.getBean("articleService");
		List<Article> resultList = as.getAllArticles();

		// Create Datasource and put it in Dynamic Jasper Format
		List data = new ArrayList(resultList.size());

		for (Article obj : resultList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("artNr", obj.getArtNr());
			map.put("artKurzbezeichnung", obj.getArtKurzbezeichnung());
			map.put("artPreis", obj.getArtPreis());
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

			callReportWindow(amedia, "RTF-DOC");
		}
	}

	private void callReportWindow(AMedia aMedia, String format) {
		boolean modal = true;

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
