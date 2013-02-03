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
package de.forsthaus.webui.bbruhns;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkex.zul.Jasperreport;

import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * @author bbruhns
 * 
 */
public class BbruhnsCtrl extends GFCBaseCtrl implements Serializable {

	protected Jasperreport report;

	public BbruhnsCtrl() {
		super();
	}

	public void onClick$test(Event event) {

		JRDataSource dataSource;

		System.err.println("hallo");

		// Preparing parameters
		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("DataFile", "CustomDataSource from java");

		// report.setSrc("/userguide/data/jasperreport.jasper");
		report.setParameters(parameters);
		report.setDatasource(new JREmptyDataSource());
		// report.setType((String) format.getSelectedItem().getValue());

	}

}
