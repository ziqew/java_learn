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
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Window;

/**
 * EN: <b>BBC News from an URL</b> controller for the dashboard.<br>
 * <hr>
 * DE: <b>BBC News von einer URL</b> Controller fuer die SystemUebersicht. <br>
 * 
 * <pre>
 * call: Div div = DashboardStockNewsCtrl(400, "no");
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardStockNewsCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = "Stock News";
	// The icon path for the groupbox
	private String iconPath = "";

	/**
	 * Module dependend
	 */
	private Iframe anIFrame;
	// scrolling mode
	private String scrolling;
	// The URL to the news;
	private final String urlString = "http://www.gmodules.com/ig/ifr?url=http://hosting.gmodules.com/ig/gadgets/file/109974367101812108674/Stock-Advisor-Search.xml";

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
	public static Div show(int modulHeight, String scolling) {
		return new DashboardStockNewsCtrl(modulHeight, scolling);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardStockNewsCtrl(int modulHeight, String scrolling) {
		super();

		setModulHeight(modulHeight);
		setScrolling(scrolling);
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

		// body
		Borderlayout bl = new Borderlayout();
		bl.setHeight(getModulHeight() + "px");
		bl.setParent(plc);
		Center ct = new Center();
		ct.setSclass("FDCenterNoBorder");
		ct.setStyle("background-color: white");
		ct.setParent(bl);

		anIFrame = new Iframe();
		anIFrame.setHeight("100%");
		anIFrame.setWidth("100%");
		anIFrame.setScrolling(getScrolling());
		anIFrame.setParent(ct);

		doReadData();
	}

	/**
	 * Reads the data.
	 */
	private void doReadData() {
		anIFrame.setSrc(urlString);
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

	public void setScrolling(String scrolling) {
		this.scrolling = scrolling;
	}

	public String getScrolling() {
		return scrolling;
	}
}
