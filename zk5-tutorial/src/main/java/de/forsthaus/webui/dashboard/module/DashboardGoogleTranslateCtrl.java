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

import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Script;
import org.zkoss.zul.Window;

/**
 * EN: <b>Google Translate starter</b> controller for the dashboard.<br>
 * <hr>
 * DE: <b>Google Uebersetzer</b> Controller fuer die SystemUebersicht. <br>
 * 
 * <pre>
 * call: Div div = DashboardGoogleTranslateCtrl.show(80);
 * </pre>
 * 
 * <!-- Google Translate Element --><br>
 * <h:div id="google_translate_element" style="display:block"></h:div><br>
 * <h:script> <br>
 * function googleTranslateElementInit() { new <br>
 * google.translate.TranslateElement({pageLanguage: "af"},<br>
 * "google_translate_element"); }; <br>
 * </h:script> <br>
 * <h:script src="http://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"
 * ><br>
 * </h:script> <br>
 * 
 * 
 * @author Stephan Gerth
 * @thanks to 'gekkio' from zk forum.
 */
public class DashboardGoogleTranslateCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;

	/**
	 * Module dependend
	 */
	private Iframe anIFrame;
	// The google icon path for the groupbox
	private String iconPath = "/images/framework_icons/translate_logo_sm.png";
	// The url to the google translator
	private String urlString = "http://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit";

	/**
	 * The static call method.
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @param scrollling
	 *            scrolling "true", "false", "yes" or "no" or "auto", "auto" by
	 *            default If null, "auto" is assumed
	 * @return the module as DIV.
	 */
	public static Div show(int modulHeight) {
		return new DashboardGoogleTranslateCtrl(modulHeight);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardGoogleTranslateCtrl(int modulHeight) {
		super();

		setModulHeight(modulHeight);
		createComponents();
	}

	/**
	 * Creates the components.<br>
	 */
	private void createComponents() {

		/**
		 * !! Windows as NameSpaceContainer to prevent not unique id's error
		 * from other dashboard module buttons or other used components.
		 */
		Window win = new Window();
		win.setBorder("none");
		win.setParent(this);

		Groupbox gb = new Groupbox();
		gb.setMold("3d");
		gb.setClosable(false);
		gb.setParent(win);
		Caption cap = new Caption();
		cap.setImage(iconPath);
		cap.setLabel("Translator");
		cap.setStyle("padding: 0px;");
		cap.setParent(gb);

		// body
		Borderlayout bl = new Borderlayout();
		bl.setHeight(getModulHeight() + "px");
		bl.setParent(gb);
		Center ct = new Center();
		ct.setSclass("FDCenterNoBorder");
		ct.setStyle("background-color: white");
		ct.setParent(bl);

		Div container = new Div();
		container.setStyle("padding: 4px;");
		ct.appendChild(container);

		Script init = new Script();
		init.setContent("function googleTranslateElementInit() { new google.translate.TranslateElement({pageLanguage: 'af'}, '" + container.getUuid() + "'); };");
		win.appendChild(init);

		Script translate = new Script();
		translate.setSrc("http://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit");
		win.appendChild(translate);

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setAnIFrame(Iframe anIFrame) {
		this.anIFrame = anIFrame;
	}

	public Iframe getAnIFrame() {
		return anIFrame;
	}

	public void setModulHeight(int modulHeight) {
		this.modulHeight = modulHeight;
	}

	public int getModulHeight() {
		return modulHeight;
	}

}
