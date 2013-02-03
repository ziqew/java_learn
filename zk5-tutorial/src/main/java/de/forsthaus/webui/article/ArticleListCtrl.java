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
package de.forsthaus.webui.article;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Article;
import de.forsthaus.backend.service.ArticleService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/article/articleList.zul
 * file.<br>
 * <b>WORKS with the annotated databinding mechanism.</b><br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changings for clustering.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          03/09/2009: sge changed for allow repainting after resizing.<br>
 *          with the refresh button.<br>
 *          07/03/2010: sge modified for zk5.x with complete Annotated
 *          Databinding.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class ArticleListCtrl extends GFCBaseListCtrl<Article> implements Serializable {

	private static final long serialVersionUID = 2038742641853727975L;
	private static final Logger logger = Logger.getLogger(ArticleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowArticlesList; // autowired

	// listbox articles
	protected Borderlayout borderLayout_articleList; // autowired
	protected Paging paging_ArticleList; // autowired
	protected Listbox listBoxArticle; // autowired
	protected Listheader listheader_ArticleList_No; // autowired
	protected Listheader listheader_ArticleList_ShortDescr; // autowired
	protected Listheader listheader_ArticleList_SinglePrice; // autowired

	// textbox long description
	protected Textbox longBoxArt_LangBeschreibung; // autowired

	// count of rows in the listbox
	private int countRows;

	// NEEDED for ReUse in a SearchWindow
	private HibernateSearchObject<Article> searchObj;

	// Databinding
	private AnnotateDataBinder binder;
	private ArticleMainCtrl articleMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient ArticleService articleService;

	/**
	 * default constructor.<br>
	 */
	public ArticleListCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);

		/**
		 * 1. Get the overhanded MainController.<br>
		 * 2. Set this controller in the MainController.<br>
		 * 3. Check if a 'selectedObject' exists yet in the MainController.<br>
		 */
		if (arg.containsKey("ModuleMainController")) {
			setArticleMainCtrl((ArticleMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getArticleMainCtrl().setArticleListCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getArticleMainCtrl().getSelectedArticle() != null) {
				setSelectedArticle(getArticleMainCtrl().getSelectedArticle());
			} else
				setSelectedArticle(null);
		} else {
			setSelectedArticle(null);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$windowArticlesList(Event event) throws Exception {

		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		doFillListbox();

		binder.loadAll();
	}

	public void doFillListbox() {

		doFitSize();

		// set the paging params
		paging_ArticleList.setPageSize(getCountRows());
		paging_ArticleList.setDetailed(true);

		// not used listheaders must be declared like ->
		// lh.setSortAscending(""); lh.setSortDescending("")
		listheader_ArticleList_No.setSortAscending(new FieldComparator("artNr", true));
		listheader_ArticleList_No.setSortDescending(new FieldComparator("artNr", false));
		listheader_ArticleList_ShortDescr.setSortAscending(new FieldComparator("artKurzbezeichnung", true));
		listheader_ArticleList_ShortDescr.setSortDescending(new FieldComparator("artKurzbezeichnung", false));
		listheader_ArticleList_SinglePrice.setSortAscending(new FieldComparator("artPreis", true));
		listheader_ArticleList_SinglePrice.setSortDescending(new FieldComparator("artPreis", false));

		// ++ create the searchObject and init sorting ++//
		// get customers and only their latest address
		searchObj = new HibernateSearchObject<Article>(Article.class, getCountRows());
		searchObj.addSort("artNr", false);
		setSearchObj(this.searchObj);

		// Set the BindingListModel
		getPagedBindingListWrapper().init(searchObj, getListBoxArticle(), paging_ArticleList);
		BindingListModelList lml = (BindingListModelList) getListBoxArticle().getModel();
		setArticles(lml);

		// Now we would select and show the text of the first entry in the list.
		// We became not the first item FROM the listbox because it's NOT
		// RENDERED AT THIS TIME.
		// So we take the first entry from the MODEL (ListModelList) and set as
		// selected.
		// check if first time opened and init databinding for selectedBean
		if (getSelectedArticle() == null) {
			// init the bean with the first record in the List
			if (lml.getSize() > 0) {
				final int rowIndex = 0;
				// only for correct showing after Rendering. No effect as an
				// Event
				// yet.
				getListBoxArticle().setSelectedIndex(rowIndex);
				// get the first entry and cast them to the needed object
				setSelectedArticle((Article) lml.get(0));

				// call the onSelect Event for showing the objects data in the
				// statusBar
				Events.sendEvent(new Event("onSelect", getListBoxArticle(), getSelectedArticle()));
			}
		}
	}

	/**
	 * Selects the object in the listbox and change the tab.<br>
	 * Event is forwarded in the corresponding listbox.
	 */
	public void onDoubleClickedArticleItem(Event event) {
		// logger.debug(event.toString());

		Article anArticle = getSelectedArticle();

		if (anArticle != null) {
			setSelectedArticle(anArticle);
			setArticle(anArticle);

			// check first, if the tabs are created
			if (getArticleMainCtrl().getArticleDetailCtrl() == null) {
				Events.sendEvent(new Event("onSelect", getArticleMainCtrl().tabArticleDetail, null));
				// if we work with spring beanCreation than we must check a
				// little bit deeper, because the Controller are preCreated ?
			} else if (getArticleMainCtrl().getArticleDetailCtrl().getBinder() == null) {
				Events.sendEvent(new Event("onSelect", getArticleMainCtrl().tabArticleDetail, null));
			}

			Events.sendEvent(new Event("onSelect", getArticleMainCtrl().tabArticleDetail, anArticle));
		}
	}

	/**
	 * When a listItem in the corresponding listbox is selected.<br>
	 * Event is forwarded in the corresponding listbox.
	 * 
	 * @param event
	 */
	public void onSelect$listBoxArticle(Event event) {
		// logger.debug(event.toString());

		// selectedArticle is filled by annotated databinding mechanism
		Article anArticle = getSelectedArticle();

		if (anArticle == null) {
			return;
		}

		// check first, if the tabs are created
		if (getArticleMainCtrl().getArticleDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", getArticleMainCtrl().tabArticleDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getArticleMainCtrl().getArticleDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", getArticleMainCtrl().tabArticleDetail, null));
		}

		// INIT ALL RELATED Queries/OBJECTS/LISTS NEW
		getArticleMainCtrl().getArticleDetailCtrl().setSelectedArticle(anArticle);
		getArticleMainCtrl().getArticleDetailCtrl().setArticle(anArticle);

		// store the selected bean values as current
		getArticleMainCtrl().doStoreInitValues();

		// show the objects data in the statusBar
		final String str = Labels.getLabel("common.Article") + ": " + anArticle.getArtKurzbezeichnung();
		EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Recalculates the container size for this controller and resize them.
	 * 
	 * Calculate how many rows have been place in the listbox. Get the
	 * currentDesktopHeight from a hidden Intbox from the index.zul that are
	 * filled by onClientInfo() in the indexCtroller.
	 */
	public void doFitSize() {

		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 0;

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - specialSize - 152;
		setCountRows((int) Math.round(maxListBoxHeight / 18.4));
		borderLayout_articleList.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowArticlesList.invalidate();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Best Pratice Hint:<br>
	 * The setters/getters for the local annotated data binded Beans/Sets are
	 * administered in the module's mainController. Working in this way you have
	 * clean line to share this beans/sets with other controllers.
	 */
	/* Master BEANS */
	public Article getArticle() {
		// STORED IN THE module's MainController
		return getArticleMainCtrl().getSelectedArticle();
	}

	public void setArticle(Article article) {
		// STORED IN THE module's MainController
		getArticleMainCtrl().setSelectedArticle(article);
	}

	public void setSelectedArticle(Article selectedArticle) {
		// STORED IN THE module's MainController
		getArticleMainCtrl().setSelectedArticle(selectedArticle);
	}

	public Article getSelectedArticle() {
		// STORED IN THE module's MainController
		return getArticleMainCtrl().getSelectedArticle();
	}

	public void setArticles(BindingListModelList articles) {
		// STORED IN THE module's MainController
		getArticleMainCtrl().setArticles(articles);
	}

	public BindingListModelList getArticles() {
		// STORED IN THE module's MainController
		return getArticleMainCtrl().getArticles();
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	/* CONTROLLERS */
	public void setArticleMainCtrl(ArticleMainCtrl articleMainCtrl) {
		this.articleMainCtrl = articleMainCtrl;
	}

	public ArticleMainCtrl getArticleMainCtrl() {
		return this.articleMainCtrl;
	}

	/* SERVICES */
	public void setArticleService(ArticleService articleService) {
		this.articleService = articleService;
	}

	public ArticleService getArticleService() {
		return this.articleService;
	}

	/* COMPONENTS and OTHERS */
	public int getCountRows() {
		return this.countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public HibernateSearchObject<Article> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(HibernateSearchObject<Article> searchObj) {
		this.searchObj = searchObj;
	}

	public Listbox getListBoxArticle() {
		return this.listBoxArticle;
	}

	public void setListBoxArticle(Listbox listBoxArticle) {
		this.listBoxArticle = listBoxArticle;
	}

}
