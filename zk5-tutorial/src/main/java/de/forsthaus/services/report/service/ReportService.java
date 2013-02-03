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
package de.forsthaus.services.report.service;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import de.forsthaus.backend.model.Order;

/**
 * Servcive for printing reports.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public interface ReportService {

	/**
	 * Druckt einen Auftrag mit seinen AuftragsPositionen.
	 * 
	 * @param auftrag
	 *            das zu druckende Auftragobjekt
	 * @param repParams
	 *            zu Übergebende Report Parameter
	 */
	void printAuftragsPositionen(Order anOrder, HashMap repParams);

	public JRDataSource getBeanCollectionByAuftrag(Order anOrder);

	public void compileReport(String aReportPathName);
}
