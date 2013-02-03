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
 * EN: <b>BBC News from an URL</b> controller for the dashboard.<br>
 * <hr>
 * DE: <b>BBC News von einer URL</b> Controller fuer die SystemUebersicht. <br>
 * 
 * <pre>
 * call: Div div = DashboardBBCNewsCtrl.show(500, "no");
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardBBCNewsCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = " News";
	// The title icon path for the groupbox
	private String iconPath = "/images/icons/bbc_news_45x16.gif";

	/**
	 * Module dependend
	 */
	private Iframe anIFrame;
	// scrolling mode
	private String scrolling;
	// The BBC URL to the news;
	private final String urlString = "http://www.gmodules.com/ig/ifr?url=http://nvmodules.netvibes.com/widget/gspec%3FuwaUrl%3Dhttp%253A%252F%252Fwww.netvibes.com%252Fmodules%252FmultipleFeeds%252FmultipleFeeds.php%253Fprovider%253Dcustom%2526url%253Dhttp%25253A%25252F%25252Feco.netvibes.com%25252Fuwa%25252Fmultifeed%25252F306%25252F394%26title%3DBBC%2BFinance%2BNews%26description%3DMultFeed%2BWidget%2B-%2BBBC%2BBusiness%2BNews%2BUK%2B-%2BBusiness%252CPersonal%2BFinance%252CEconomy%2Band%2BCompanies%2BNews%26author%3Dkdhuk%26email%3Dkdharrowmailbox-netvibes%2540yahoo.co.uk&up_category=&up_view=&up_nbTitles=4&up_details=1&up_showDate=0&up_openOutside=0&up_videoAutoPlay=false&up_numberTabs=1&up_selectedTab=0&up_title=MultipleFeeds&up_lookForHtmlThumbnail=true&up_provider=google&up_url=&up_lastSearch=__undefined__&up_showTweet=1&up_setNbTitles=true&synd=open&w=350&h=400&title=&border=%23ffffff%7C3px%2C1px+solid+%23999999&source=http%3A%2F%2Fwww.gmodules.com%2Fig%2Fcreator%3Fsynd%3Dopen%26url%3Dhttp%253A%2F%2Fnvmodules.netvibes.com%2Fwidget%2Fgspec%253FuwaUrl%253Dhttp%25253A%25252F%25252Fwww.netvibes.com%25252Fmodules%25252FmultipleFeeds%25252FmultipleFeeds.php%25253Fprovider%25253Dcustom%252526url%25253Dhttp%2525253A%2525252F%2525252Feco.netvibes.com%2525252Fuwa%2525252Fmultifeed%2525252F306%2525252F394%2526title%253DBBC%252BFinance%252BNews%2526description%253DMultFeed%252BWidget%252B-%252BBBC%252BBusiness%252BNews%252BUK%252B-%252BBusiness%25252CPersonal%252BFinance%25252CEconomy%252Band%252BCompanies%252BNews%2526author%253Dkdhuk%2526email%253Dkdharrowmailbox-netvibes%252540yahoo.co.uk%26pt%3D%2526context%253Dd%2526type%253Dgadgets%2526synd%253Dig%2526lang%253Dzh-tw%2526.lang%253Dzh-tw%2526country%253Dtw%2526.country%253Dtw%2526start%253D0%2526num%253D1%2526target%253D3758%2526objs%253D%26sn%3D3758";
	private final String urlStringOld = "http://j8a42gb7441j01aqe70u9lr2o9hb32dn.open.gmodules.com/ig/ifr?url=http://bbcnewsgadget.googlecode.com/svn/trunk/Gadget/bbc_igoogle_ukedition12.xml&synd=open&w=320&h=200&title=BBC+News+-+Official+UK+Edition&border=%23ffffff|3px%2C1px+solid+%23999999&source=http%3A%2F%2Fj8a42gb7441j01aqe70u9lr2o9hb32dn.open.gmodules.com%2Fig%2Fcreator%3Fsynd%3Dopen%26url%3Dhttp%253A%2F%2Fbbcnewsgadget.googlecode.com%2Fsvn%2Ftrunk%2FGadget%2Fbbc_igoogle_ukedition12.xml%26pt%3D%2526context%253Dd%2526type%253Dgadgets%2526synd%253Dig%2526lang%253Dzh-tw%2526.lang%253Dzh-tw%2526country%253Dtw%2526.country%253Dtw%2526start%253D0%2526num%253D1%2526target%253DUKUF%2526objs%253D%26sn%3DUKUF";

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
		return new DashboardBBCNewsCtrl(modulHeight, scolling);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardBBCNewsCtrl(int modulHeight, String scrolling) {
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
