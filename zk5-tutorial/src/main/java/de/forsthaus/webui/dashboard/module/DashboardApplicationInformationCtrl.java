/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of openTruuls™. http://www.opentruuls.org/ and 
 * have the permission to be integrated in the zksample2 demo application.
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
package de.forsthaus.webui.dashboard.module;

import java.io.Serializable;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * EN: <b>Zksample2 Informations </b> controller for the dashboard.<br>
 * Shows common informations about this application.
 * <hr>
 * DE: <b>Zksample2 Informationen</b> controller fuer die SystemUebersicht.<br>
 * Zeigt allgemeine Informationen zur Zksample2 Demo Applikation.
 * 
 * <pre>
 * call: Div div = DashboardApplicationInformationCtrl.show(200);
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardApplicationInformationCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = Labels.getLabel("common.Application.Information");
	// The title icon path for the groupbox
	private String iconPath = "/images/icons/method_16x16.gif";

	// Textbox holds the information
	private Textbox textbox;

	/**
	 * The static call method.
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @return the module as DIV.
	 */
	public static Div show(int modulHeight) {
		return new DashboardApplicationInformationCtrl(modulHeight);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardApplicationInformationCtrl(int modulHeight) {
		super();

		setModulHeight(modulHeight);
		createComponents();
	}

	/**
	 * Creates the components..<br>
	 */
	private void createComponents() {

		/**
		 * !! Windows as NameSpaceContainer to prevent not unique id's error
		 * from other dashboard module buttons or other used components.
		 */
		Window win = new Window();
		win.setBorder("none");
		win.setSclass("OT-DashboardWindow");
		win.setParent(this);

		Panel pl = new Panel();
		pl.setBorder("normal");
		pl.setClosable(false);
		pl.setParent(win);
		Caption cap = new Caption();
		cap.setImage(iconPath);
		cap.setLabel(title);
		cap.setStyle("padding: 0px;");
		cap.setParent(pl);
		Panelchildren plc = new Panelchildren();
		plc.setParent(pl);

		textbox = new Textbox();
		textbox.setReadonly(true);
		textbox.setStyle("font-family: verdana; border: none; background-color: white;");
		textbox.setMultiline(true);
		textbox.setRows(10);
		textbox.setHflex("true");
		textbox.setVflex("true");
		textbox.setParent(plc);

		doReadData();
	}

	/**
	 * Reads the data.
	 */
	private void doReadData() {

		// init: line feed
		String lf = "\n";
		// init: string for the text
		String str = "";

		str = str + "- CRUD operations on all modules with spring managed services and DAO classes which are working with hibernate." + lf;
		str = str + "- Uses ButtonControllers for showing next possible CRUD operations." + lf;
		str = str + "- checks by onClose if there are unsaved modified data." + lf;
		str = str + "- single and mixed searching for data." + lf;
		str = str + "- sortable lists." + lf;
		str = str + "- localization (english/german) language." + lf;
		str = str + "- Reporting with the JasperReports. The reports are created in pure JAVA with the DynamicJasper lib." + lf;
		str = str + "- Full customizable Role based security management wich is extended for groups." + lf;
		str = str + "- Rights data are stored in database tables. It's build on top of the spring-security framework." + lf;
		str = str + "- Most dialogs you can reach with doubleClick on an item in the lists." + lf;
		str = str + "- Customer No. 20 + 21 have predefiened data for orders and order positions. " + lf;
		str = str + "- Data for orders and order-positions are available for customer No. 20 + 21." + lf;
		str = str + "" + lf;
		str = str + "- ======================= used frameworks =================================" + lf;
		str = str + "- As ORM (Object relational mapper) we use the Hibernate Framework. Used version = 3.3.1 GA." + lf;
		str = str + "- We used the search object from the Hibernate-Generic-DAO framework and the hql code " + lf;
		str = str + "  generator for our PagedListWrapper. Version = 0.5.1." + lf;
		str = str + "- Integration of the spring-framework with spring managed Services and DAOs in the backend " + lf;
		str = str + "  and managed controllers/composers in the frontend. Used version = 3.0.1." + lf;
		str = str + "- Access management with spring-security.  We extended it for a group and group-rights. " + lf;
		str = str + "  All security dependent entries are stored in the database. Userroles/Roles/RoleGroups are " + lf;
		str = str + "  customizable on runtime.  Used version = 3.0.2." + lf;
		str = str + "- We use spring-aop for the database transaction management." + lf;
		str = str + "- For reporting we use JasperReports(v3.5.0). + DynamicJasper(v3.0.13)." + lf;

		textbox.setValue(str);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setModulHeight(int modulHeight) {
		this.modulHeight = modulHeight;
	}

	public int getModulHeight() {
		return modulHeight;
	}

}
