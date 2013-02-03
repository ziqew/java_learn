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
import org.zkoss.zul.Window;

/**
 * EN: <b>The Wall Street Journal News from an URL</b> controller for the
 * dashboard.<br>
 * <hr>
 * DE: <b>The Wall Street Journal News von einer URL</b> Controller fuer die
 * SystemUebersicht. <br>
 * 
 * <pre>
 * call: Div div = DashboardTheWallStreetJournalCtrl(500, "no");
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardTheWallStreetJournalCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = "The Wall Street Journal";
	// The icon path for the groupbox
	private String iconPath = "";

	/**
	 * Module dependend
	 */
	private Iframe anIFrame;
	// scrolling mode
	private String scrolling;
	// The Wall Street Journal URL to the news;
	private final String urlString = "http://www.gmodules.com/ig/ifr?url=http://hosting.gmodules.com/ig/gadgets/file/100674619146546250953/wsj.xml&amp;up_entries=20&amp;up_refresh=60&amp;synd=open&amp;w=250&amp;h=480&amp;title=&amp;border=%23ffffff%7C3px%2C1px+solid+%23999999&amp;source=http%3A%2F%2Fwww.gmodules.com%2Fig%2Fcreator%3Fsynd%3Dopen%26url%3Dhttp%253A%2F%2Fhosting.gmodules.com%2Fig%2Fgadgets%2Ffile%2F100674619146546250953%2Fwsj.xml%26pt%3D%2526context%253Dd%2526type%253Dgadgets%2526synd%253Dig%2526lang%253Den-gb%2526.lang%253Den-gb%2526country%253Dau%2526.country%253Dau%2526start%253D0%2526num%253D1%2526target%253DASsh%2526objs%253D%26sn%3DASsh";

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
		return new DashboardTheWallStreetJournalCtrl(modulHeight, scolling);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardTheWallStreetJournalCtrl(int modulHeight, String scrolling) {
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
		win.setParent(this);

		Groupbox gb = new Groupbox();
		gb.setMold("3d");
		gb.setClosable(false);
		gb.setParent(win);
		Caption cap = new Caption();
		cap.setImage(iconPath);
		cap.setLabel(title);
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
