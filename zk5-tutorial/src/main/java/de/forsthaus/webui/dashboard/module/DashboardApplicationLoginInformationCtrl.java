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
 * EN: <b>Zksample2 login informations </b> controller for the dashboard.<br>
 * Shows common informations about the default users.
 * <hr>
 * DE: <b>Zksample2 Login Informationen</b> controller fuer die
 * SystemUebersicht.<br>
 * Zeigt Info's zu den vordefinierten Usern zur Zksample2 Demo Applikation.
 * 
 * <pre>
 * call: Div div = DashboardApplicationLoginInformationCtrl.show(200);
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardApplicationLoginInformationCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = Labels.getLabel("title.users.login.sources");
	// The title icon path for the groupbox
	private String iconPath = "/images/icons/about1_16x16.gif";

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
		return new DashboardApplicationLoginInformationCtrl(modulHeight);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardApplicationLoginInformationCtrl(int modulHeight) {
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

		str = str + "If you need commercial help on your zk projects please contact us under zk(at)forsthaus(dot)de ." + lf;
		str = str + "All Zksample2 sources are available on SourceForge.net as Eclipse Projects. " + lf;
		str = str + "Read this thread: http://zkoss.org/forum/listComment/10986 . " + lf;
		str = str + " " + lf;
		str = str + "Stored users are  (username/password): " + lf;
		str = str + "- admin / admin (full access) " + lf;
		str = str + "- user1 / user1 (full access to the office data) " + lf;
		str = str + "- user2 / user2 (restricted access to the office data. Only view mode.) " + lf;
		str = str + "- headoffice / headoffice (full access to the main data. " + lf;

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
