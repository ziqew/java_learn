/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2.
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
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.forsthaus.webui.reports.util;

import java.io.InputStream;
import java.util.Collection;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.log4j.Logger;

/**
 * Compiling a Jasper Report.
 * 
 * @author sgerth
 */
public class JRreportCompiler {

	public JRreportCompiler() {

	}

	public boolean compileReport(String aReportName) {

		boolean result = false;

		try {

			final InputStream inputStream = getClass().getResourceAsStream(aReportName);

			final JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
			final Collection collection = JasperCompileManager.verifyDesign(jasperDesign);
			for (final Object object : collection) {
				object.toString();
			}

			final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

			if (jasperReport != null) {
				result = true;
			}

		} catch (final JRException ex) {
			final String connectMsg = "JasperReports: Could not create the report " + ex.getMessage() + " "
					+ ex.getLocalizedMessage();
			Logger.getLogger(getClass()).error(connectMsg, ex);
		} catch (final Exception ex) {
			final String connectMsg = "Could not create the report " + ex.getMessage() + " " + ex.getLocalizedMessage();
			Logger.getLogger(getClass()).error(connectMsg, ex);
		}
		return result;
	}
}
