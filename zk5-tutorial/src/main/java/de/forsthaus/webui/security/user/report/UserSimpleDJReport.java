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
package de.forsthaus.webui.security.user.report;

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
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.webui.util.ZksampleDateFormat;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * A report implemented with the DynamicJasper framework.<br>
 * <br>
 * This report shows a list of Users.<br>
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
public class UserSimpleDJReport extends Window implements Serializable {

	private static final long serialVersionUID = 1L;

	private Iframe iFrame;
	private ByteArrayOutputStream output;
	private InputStream mediais;
	private AMedia amedia;
	private final String zksample2title = "[Zksample2] DynamicJasper Report Sample";

	public UserSimpleDJReport(Component parent) throws InterruptedException {
		super();
		this.setParent(parent);

		try {
			doPrint();
		} catch (final Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.toString());
		}
	}

	public void doPrint() throws JRException, ColumnBuilderException, ClassNotFoundException, IOException {

		// Localized column headers
		String usrLoginname = Labels.getLabel("common.Loginname");
		String usrLastname = Labels.getLabel("common.Lastname");
		String usrFirstname = Labels.getLabel("common.Firstname");
		String usrEmail = Labels.getLabel("common.Email");
		String usrEnabled = Labels.getLabel("common.Enabled");

		// Styles: Title
		Style titleStyle = new Style();
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		titleStyle.setFont(Font.VERDANA_BIG_BOLD);

		// Styles: Subtitle
		Style subtitleStyle = new Style();
		subtitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		subtitleStyle.setFont(Font.VERDANA_MEDIUM_BOLD);

		/**
		 * Set the styles. In a report created with DynamicReportBuilder we do
		 * this in an other way.
		 */
		// Header Style Text (left)
		Style columnHeaderStyleText = new Style();
		columnHeaderStyleText.setFont(Font.VERDANA_MEDIUM_BOLD);
		columnHeaderStyleText.setHorizontalAlign(HorizontalAlign.LEFT);
		columnHeaderStyleText.setBorderBottom(Border.PEN_1_POINT);

		// Header Style Text (left)
		Style columnHeaderStyleNumber = new Style();
		columnHeaderStyleNumber.setFont(Font.VERDANA_MEDIUM_BOLD);
		columnHeaderStyleNumber.setHorizontalAlign(HorizontalAlign.RIGHT);
		columnHeaderStyleNumber.setBorderBottom(Border.PEN_1_POINT);

		// Rows content
		Style columnDetailStyleText = new Style();
		columnDetailStyleText.setFont(Font.VERDANA_SMALL);
		columnDetailStyleText.setHorizontalAlign(HorizontalAlign.LEFT);

		// Rows content
		Style columnDetailStyleNumbers = new Style();
		columnDetailStyleNumbers.setFont(Font.VERDANA_SMALL);
		columnDetailStyleNumbers.setHorizontalAlign(HorizontalAlign.RIGHT);

		DynamicReportBuilder drb = new DynamicReportBuilder();
		DynamicReport dr;

		// Sets the Report Columns, header, Title, Groups, Etc Formats
		// DynamicJasper documentation
		drb.setTitle(this.zksample2title);
		drb.setSubtitle("List of Users: " + ZksampleDateFormat.getDateFormater().format(new Date()));
		drb.setSubtitleStyle(subtitleStyle);
		drb.setDetailHeight(10);
		drb.setMargins(20, 20, 30, 15);
		drb.setDefaultStyles(titleStyle, subtitleStyle, columnHeaderStyleText, columnDetailStyleText);
		drb.setPrintBackgroundOnOddRows(true);

		/**
		 * Columns Definitions. A new ColumnBuilder instance for each column.
		 */
		// Login name
		AbstractColumn colLoginName = ColumnBuilder.getNew().setColumnProperty("usrLoginname", String.class.getName()).build();
		colLoginName.setTitle(usrLoginname);
		colLoginName.setWidth(30);
		colLoginName.setHeaderStyle(columnHeaderStyleText);
		colLoginName.setStyle(columnDetailStyleText);
		// Last name
		AbstractColumn colLastName = ColumnBuilder.getNew().setColumnProperty("usrLastname", String.class.getName()).build();
		colLastName.setTitle(usrLastname);
		colLastName.setWidth(50);
		colLastName.setHeaderStyle(columnHeaderStyleText);
		colLastName.setStyle(columnDetailStyleText);
		// First name
		AbstractColumn colFirstName = ColumnBuilder.getNew().setColumnProperty("usrFirstname", String.class.getName()).build();
		colFirstName.setTitle(usrFirstname);
		colFirstName.setWidth(50);
		colFirstName.setHeaderStyle(columnHeaderStyleText);
		colFirstName.setStyle(columnDetailStyleText);
		// Email address
		AbstractColumn colEmail = ColumnBuilder.getNew().setColumnProperty("usrEmail", String.class.getName()).build();
		colEmail.setTitle(usrEmail);
		colEmail.setWidth(50);
		colEmail.setHeaderStyle(columnHeaderStyleText);
		colEmail.setStyle(columnDetailStyleText);
		// Account enabled
		AbstractColumn colEnabled = ColumnBuilder.getNew().setCustomExpression(getMyBooleanExpression()).build();
		colEnabled.setTitle(usrEnabled);
		colEnabled.setWidth(10);
		colEnabled.setHeaderStyle(columnHeaderStyleText);
		colEnabled.setStyle(columnDetailStyleText);

		// Add the columns to the report in the whished order
		drb.addColumn(colLoginName);
		drb.addColumn(colLastName);
		drb.addColumn(colFirstName);
		drb.addColumn(colEmail);
		drb.addColumn(colEnabled);

		// Add the usrEnabled field to the report.
		drb.addField("usrEnabled", Boolean.class.getName());

		drb.setUseFullPageWidth(true); // use full width of the page
		dr = drb.build(); // build the report

		// Get information from database
		UserService sv = (UserService) SpringUtil.getBean("userService");
		List<SecUser> resultList = sv.getAllUsers();

		// Create Datasource and put it in Dynamic Jasper Format
		List data = new ArrayList(resultList.size());

		for (SecUser obj : resultList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("usrLoginname", obj.getUsrLoginname());
			map.put("usrLastnusrEnabledame", obj.getUsrLastname());
			map.put("usrFirstname", String.valueOf(obj.getUsrFirstname()));
			map.put("usrEmail", String.valueOf(obj.getUsrEmail()));
			// map.put("usrEnabled", String.valueOf(obj.isUsrEnabled()));
			map.put("usrEnabled", obj.isUsrEnabled());
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

	/**
	 * A CustomExpression that checks a boolean value and writes a 'T' as true
	 * and a 'F' as false.<br>
	 * 
	 * @return
	 */
	private CustomExpression getMyBooleanExpression() {
		return new CustomExpression() {

			@Override
			public Object evaluate(Map fields, Map variables, Map parameters) {
				String result = "";

				final boolean enabled = (Boolean) fields.get("usrEnabled");

				if (enabled == true) {
					result = Labels.getLabel("common.Yes");
				} else
					result = Labels.getLabel("common.No");

				return result;
			}

			@Override
			public String getClassName() {
				return String.class.getName();
			}
		};
	}

	private void callReportWindow(AMedia aMedia, String format) {
		final boolean modal = true;

		this.setTitle("Dynamic JasperReports. Sample Report for ZKoss");
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

		this.onClose();

	}

}
