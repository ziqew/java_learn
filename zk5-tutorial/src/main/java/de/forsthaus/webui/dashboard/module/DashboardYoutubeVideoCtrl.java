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
package de.forsthaus.webui.dashboard.module;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.South;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import de.forsthaus.backend.bean.ResultObject;
import de.forsthaus.backend.model.YoutubeLink;
import de.forsthaus.backend.service.YoutubeLinkService;

/**
 * EN: <b>YouTube iFrame</b> for the dashboard.<br>
 * Shows a youtube video in an iFrame. The video's url is getting randomly from
 * a table by first starting.<br>
 * <hr>
 * DE: <b>YouTube iFrame</b> fuer die SystemUebersicht.<br>
 * Zeigt ein YouTube Musikvideo in einem iFrame an. Dieser Video Link wird per
 * Zufallsgenerator beim Erststart aus der Tabelle geholt.<br>
 * 
 * <pre>
 * call: Div div = DashboardYoutubeVideoCtrl.show(200);
 * </pre>
 * 
 * @author Stephan Gerth
 */
public class DashboardYoutubeVideoCtrl extends Div implements Serializable {

	private static final long serialVersionUID = 1L;

	// the height of this dashboard module
	private int modulHeight;
	// the title of the dashboard module
	private String title = "Terry's favorite songs";
	// The icon path for the groupbox
	private String iconPath = "/images/youtube_40x16.jpg";

	// the modules main groupbox
	private Panel youTubePanel;
	// holds the data
	private Iframe iFrame;
	// Window parent for the searchBox
	private Window win;

	/**
	 * The static call method.
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 * @return the module as DIV.
	 */
	public static Div show(int modulHeight) {
		return new DashboardYoutubeVideoCtrl(modulHeight);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param modulHeight
	 *            The height of this dashboard module
	 */
	private DashboardYoutubeVideoCtrl(int modulHeight) {
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
		win = new Window();
		win.setBorder("none");
		win.setSclass("OT-DashboardWindow");
		win.setParent(this);

		youTubePanel = new Panel();
		youTubePanel.setBorder("normal");
		youTubePanel.setClosable(false);
		youTubePanel.setParent(win);
		Caption cap = new Caption();
		cap.setImage(iconPath);
		cap.setLabel(title);
		cap.setStyle("padding: 0px;");
		cap.setParent(youTubePanel);
		Panelchildren plc = new Panelchildren();
		plc.setParent(youTubePanel);

		// Buttons Toolbar/Timer
		Div div = new Div();
		div.setStyle("padding: 0px");
		div.setParent(cap);
		Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setWidth("100%");
		hbox.setParent(div);
		Toolbar toolbarRight = new Toolbar();
		toolbarRight.setStyle("float:right; border-style: none;");
		toolbarRight.setParent(hbox);

		Hbox hboxSameButtonsHeight = new Hbox();
		hboxSameButtonsHeight.setPack("center");
		hboxSameButtonsHeight.setParent(toolbarRight);

		Button btnRefresh = new Button();
		btnRefresh.setId("btnSelectYoutubeSong");
		btnRefresh.setSclass("oT_ButtonForPanelWithIcon");
		btnRefresh.setImage("/images/icons/play-music_16x16.png");
		btnRefresh.setTooltiptext(Labels.getLabel("btnSelectYoutubeSong.tooltiptext"));
		btnRefresh.addEventListener("onClick", new BtnClickListener());
		btnRefresh.setParent(hboxSameButtonsHeight);

		// body
		Borderlayout bl = new Borderlayout();
		bl.setHeight(getModulHeight() + "px");
		bl.setParent(plc);
		Center ct = new Center();
		ct.setSclass("FDCenterNoBorder");
		ct.setStyle("background-color: white");
		ct.setFlex(true);
		ct.setParent(bl);

		iFrame = new org.zkoss.zul.Iframe();
		iFrame.setHeight("200px");
		iFrame.setWidth("100%");
		iFrame.setParent(ct);

		doReadData();

	}

	/**
	 * Reads the data.
	 */
	private void doReadData() {

		// select a random song from the table by first starting
		YoutubeLinkService service = (YoutubeLinkService) SpringUtil.getBean("youtubeLinkService");

		YoutubeLink youtubeLink = service.getRandomYoutubeLink();

		if (youtubeLink != null) {
			// set the title
			youTubePanel.setTooltiptext(youtubeLink.getInterpret() + "\n" + " - " + youtubeLink.getTitle() + " - ");
			// clear all old stuff
			iFrame.getChildren().clear();
			// set the URL
			iFrame.setSrc(youtubeLink.getUrl());
		}

	}

	/**
	 * Inner onBtnClick Listener class.<br>
	 * 
	 * @author sGerth
	 */
	private final class BtnClickListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {

			// check which button is pressed
			if (event.getTarget().getId().equalsIgnoreCase("btnSelectYoutubeSong")) {

				// select a youtubeLink from the list.
				YoutubeLink youtubeLink = YoutubeLinkSelectListBox.show(win);

				if (youtubeLink != null) {
					// set the title
					youTubePanel.setTooltiptext(youtubeLink.getInterpret() + "\n" + " - " + youtubeLink.getTitle() + " - ");
					// clear all old stuff
					iFrame.getChildren().clear();
					// set the URL
					iFrame.setSrc(youtubeLink.getUrl());
				}

			}
		}
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

	public void setiFrame(Iframe iFrame) {
		this.iFrame = iFrame;
	}

	public Iframe getiFrame() {
		return iFrame;
	}

	/**
	 * This class creates a modal window as a dialog in which the user <br>
	 * can search and select a youtubeLink object. By onClosing this box
	 * <b>returns</b> an object or null. <br>
	 * The object can returned by selecting and clicking the OK button or by
	 * DoubleClicking on an item from the list.<br>
	 * <br>
	 * 
	 * <pre>
	 * call: YoutubeLink youtubeLink = YoutubeLinkSelectListBox.show(parentComponent);
	 * </pre>
	 * 
	 * @author bbruhns
	 * @author sgerth
	 */
	public static class YoutubeLinkSelectListBox extends Window implements Serializable {

		private static final long serialVersionUID = 1L;
		private static final Logger logger = Logger.getLogger(YoutubeLinkSelectListBox.class);

		// the windows title
		private String _title = Labels.getLabel("btnSelectYoutubeSong.tooltiptext");
		// 1. Listheader
		private String _listHeader1 = Labels.getLabel("dashboard.youtube.interpret");
		// 2. Listheader
		private String _listHeader2 = Labels.getLabel("dashboard.youtube.songtitle");
		// the windows height
		private int _height = 420;
		// the windows width
		private int _width = 550;

		// Listbox
		private Listbox listbox;
		// the model for the listbox
		private ListModelList listModelList;
		// Paging
		private Paging paging;
		// PageSize
		private int pageSize = 20;

		// the returned bean object
		private YoutubeLink youtubeLink = null;

		// The service from which we get the data
		private YoutubeLinkService youtubeLinkService;

		/**
		 * The Call method.
		 * 
		 * @param parent
		 *            The parent component
		 * @return a BeanObject from the listBox or null.
		 */
		public static YoutubeLink show(Component parent) {
			return new YoutubeLinkSelectListBox(parent).getYoutubeLink();
		}

		/**
		 * Private Constructor. So it can only be created with the static show()
		 * method.<br>
		 * 
		 * @param parent
		 */
		private YoutubeLinkSelectListBox(Component parent) {
			super();

			setParent(parent);

			createBox();
		}

		/**
		 * Creates the components, sets the model and show the window as modal.<br>
		 */
		private void createBox() {

			// Window
			this.setWidth(String.valueOf(_width) + "px");
			this.setHeight(String.valueOf(_height) + "px");
			this.setTitle(_title);
			this.setVisible(true);
			this.setClosable(true);

			// Borderlayout
			Borderlayout bl = new Borderlayout();
			bl.setHeight("100%");
			bl.setWidth("100%");
			bl.setParent(this);

			Center center = new Center();
			center.setFlex(true);
			center.setParent(bl);

			South south = new South();
			south.setHeight("52px");
			south.setParent(bl);

			// Listbox
			listbox = new Listbox();
			listbox.setStyle("border: none;");
			listbox.setHeight("100%");
			listbox.setVisible(true);
			listbox.setParent(center);
			// listbox.setParent(vbox);
			listbox.setItemRenderer(new SearchBoxItemRenderer());

			// 1. Listheader
			Listhead listhead = new Listhead();
			listhead.setParent(listbox);
			Listheader listheader;
			listheader = new Listheader();
			listheader.setSclass("FDListBoxHeader1");
			listheader.setParent(listhead);
			listheader.setLabel(_listHeader1);
			listheader.setWidth("55%");
			// 2. Listheader
			listheader = new Listheader();
			listheader.setSclass("FDListBoxHeader1");
			listheader.setParent(listhead);
			listheader.setLabel(_listHeader2);
			listheader.setWidth("45%");

			Vbox vbox = new Vbox();
			vbox.setParent(south);
			vbox.setHflex("1");
			vbox.setVflex("1");

			// Paging
			paging = new Paging();
			paging.setDetailed(true);
			paging.setPageSize(getPageSize());
			paging.setParent(vbox);
			paging.addEventListener("onPaging", new OnPagingEventListener());

			// Button
			Button btnOK = new Button();
			btnOK.setLabel("OK");
			btnOK.addEventListener("onClick", new OnCloseListener());
			btnOK.setParent(vbox);

			// set the Model by filling with DB data
			// listbox.setModel(new
			// ListModelList(getYoutubeLinkService().getAllYoutubeLinks()));

			// clear old stuff

			/**
			 * init the model.<br>
			 * The ResultObject is a helper class that holds the generic list
			 * and the totalRecord count as int value.
			 */
			ResultObject ro = getYoutubeLinkService().getAllYoutubeLinks(0, getPageSize());
			List<YoutubeLink> resultList = (List<YoutubeLink>) ro.getList();
			paging.setTotalSize(ro.getTotalCount());

			// set the model
			setListModelList(new ListModelList(resultList));
			this.listbox.setModel(getListModelList());

			try {
				doModal();
			} catch (SuspendNotAllowedException e) {
				logger.fatal("", e);
				this.detach();
			} catch (InterruptedException e) {
				logger.fatal("", e);
				this.detach();
			}
		}

		/**
		 * Inner ListItemRenderer class.<br>
		 */
		final class SearchBoxItemRenderer implements ListitemRenderer {

			@Override
			public void render(Listitem item, Object data) throws Exception {

				YoutubeLink youtubeLink = (YoutubeLink) data;

				Listcell lc;

				lc = new Listcell(youtubeLink.getInterpret());
				lc.setParent(item);
				lc = new Listcell(youtubeLink.getTitle());
				lc.setParent(item);

				item.setAttribute("data", data);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");
			}
		}

		/**
		 * If a DoubleClick appears on a listItem. <br>
		 * This method is forwarded in the renderer.<br>
		 * 
		 * @param event
		 */
		public void onDoubleClicked(Event event) {

			if (listbox.getSelectedItem() != null) {
				Listitem li = listbox.getSelectedItem();
				YoutubeLink youtubeLink = (YoutubeLink) li.getAttribute("data");

				setYoutubeLink(youtubeLink);
				this.onClose();
			}
		}

		/**
		 * Inner OnCloseListener class.<br>
		 */
		final class OnCloseListener implements EventListener {
			@Override
			public void onEvent(Event event) throws Exception {

				if (listbox.getSelectedItem() != null) {
					Listitem li = listbox.getSelectedItem();
					YoutubeLink youtubeLink = (YoutubeLink) li.getAttribute("data");

					setYoutubeLink(youtubeLink);
				}
				onClose();
			}
		}

		/**
		 * Refreshes the list by calling the DAO methode with the modified
		 * search object. <br>
		 * 
		 * @param so
		 *            SearchObject, holds the entity and properties to search. <br>
		 * @param start
		 *            Row to start. <br>
		 */
		@SuppressWarnings("unchecked")
		void refreshModel(int start) {

			// clear old data
			getListModelList().clear();

			// init the model
			ResultObject ro = getYoutubeLinkService().getAllYoutubeLinks(start, getPageSize());
			List<YoutubeLink> resultList = (List<YoutubeLink>) ro.getList();
			this.paging.setTotalSize(ro.getTotalCount());

			// set the model
			setListModelList(new ListModelList(resultList));
			this.listbox.setModel(getListModelList());
		}

		/**
		 * "onPaging" EventListener for the paging component. <br>
		 * <br>
		 * Calculates the next page by currentPage and pageSize values. <br>
		 * Calls the method for refreshing the data with the new rowStart and
		 * pageSize. <br>
		 */
		public final class OnPagingEventListener implements EventListener {
			@Override
			public void onEvent(Event event) throws Exception {

				PagingEvent pe = (PagingEvent) event;
				int pageNo = pe.getActivePage();
				int start = pageNo * getPageSize();
				// refresh the list
				refreshModel(start);
			}
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++ //
		// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
		// +++++++++++++++++++++++++++++++++++++++++++++++++ //

		public YoutubeLink getYoutubeLink() {
			return youtubeLink;
		}

		public void setYoutubeLink(YoutubeLink youtubeLink) {
			this.youtubeLink = youtubeLink;
		}

		public YoutubeLinkService getYoutubeLinkService() {
			if (youtubeLinkService == null) {
				youtubeLinkService = (YoutubeLinkService) SpringUtil.getBean("youtubeLinkService");
			}
			return youtubeLinkService;
		}

		public void setYoutubeLinkService(YoutubeLinkService youtubeLinkService) {
			this.youtubeLinkService = youtubeLinkService;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getPageSize() {
			return this.pageSize;
		}

		public void setListModelList(ListModelList listModelList) {
			this.listModelList = listModelList;
		}

		public ListModelList getListModelList() {
			return this.listModelList;
		}
	}
	// +++ END inner class YoutubeLinkSelectListBox +++ //

}
