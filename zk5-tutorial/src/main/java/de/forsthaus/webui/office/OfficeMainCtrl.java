package de.forsthaus.webui.office;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Office;
import de.forsthaus.backend.service.OfficeService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.webui.office.report.OfficeSimpleDJReport;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleCommonUtils;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Main controller for the office main module.<br>
 * <br>
 * zul-file: /WEB-INF/pages/office/officeMain.zul.<br>
 * <b>WORKS with the annotated databinding mechanism.</b><br>
 * This class creates the Tabs + TabPanels. The components/data to all tabs are
 * created on demand on first time selecting the tab.<br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @changes 07/04/2010: sge modified for zk5.x with complete Annotated
 *          Databinding.<br>
 * 
 *          Managed tabs:<br>
 *          - OfficeListCtrl = Office List / Filialen Liste<br>
 *          - OfficeDetailCtrl = Office Details / Filiale-Details<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class OfficeMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(OfficeMainCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowOfficeMain; // autowired

	// Tabs
	protected Tabbox tabbox_OfficeMain; // autowired
	protected Tab tabOfficeList; // autowired
	protected Tab tabOfficeDetail; // autowired
	protected Tabpanel tabPanelOfficeList; // autowired
	protected Tabpanel tabPanelOfficeDetail; // autowired

	// filter components
	protected Checkbox checkbox_OfficeList_ShowAll; // autowired
	protected Textbox txtb_Office_No; // aurowired
	protected Button button_OfficeList_SearchNo; // aurowired
	protected Textbox txtb_Office_Name; // aurowired
	protected Button button_OfficeList_SearchName; // aurowired
	protected Textbox txtb_Office_City; // aurowired
	protected Button button_OfficeList_SearchCity; // aurowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "button_OfficeMain_";
	private ButtonStatusCtrl btnCtrlOffice;
	protected Button btnNew; // autowired
	protected Button btnEdit; // autowired
	protected Button btnDelete; // autowired
	protected Button btnSave; // autowired
	protected Button btnCancel; // autowired

	protected Button btnFirst; // autowire
	protected Button btnPrevious; // autowire
	protected Button btnNext; // autowire
	protected Button btnLast; // autowire

	protected Button btnPrint; // autowire

	protected Button btnHelp;

	// Tab-Controllers for getting the binders
	private OfficeListCtrl officeListCtrl;
	private OfficeDetailCtrl officeDetailCtrl;

	// Databinding
	private Office selectedOffice;
	private BindingListModelList offices;

	// ServiceDAOs / Domain Classes
	private OfficeService officeService;

	// always a copy from the bean before modifying. Used for reseting
	private Office originalOffice;

	/**
	 * default constructor.<br>
	 */
	public OfficeMainCtrl() {
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
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$windowOfficeMain(Event event) throws Exception {
		windowOfficeMain.setContentStyle("padding:0px;");

		// create the Button Controller. Disable not used buttons during working
		btnCtrlOffice = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnPrint, btnFirst, btnPrevious, btnNext, btnLast, btnNew, btnEdit, btnDelete, btnSave,
				btnCancel, null);

		doCheckRights();

		/**
		 * Initiate the first loading by selecting the customerList tab and
		 * create the components from the zul-file.
		 */
		tabOfficeList.setSelected(true);

		if (tabPanelOfficeList != null) {
			ZksampleCommonUtils.createTabPanelContent(tabPanelOfficeList, this, "ModuleMainController", "/WEB-INF/pages/office/officeList.zul");
		}

		// init the buttons for editMode
		btnCtrlOffice.setInitEdit();
	}

	/**
	 * When the tab 'tabOfficeList' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabOfficeList(Event event) throws IOException {
		logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelOfficeList.getFirstChild() != null) {
			tabOfficeList.setSelected(true);

			return;
		}

		if (tabPanelOfficeList != null) {
			ZksampleCommonUtils.createTabPanelContent(tabPanelOfficeList, this, "ModuleMainController", "/WEB-INF/pages/office/officeList.zul");
		}

	}

	/**
	 * When the tab 'tabPanelOfficeDetail' is selected.<br>
	 * Loads the zul-file into the tab.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onSelect$tabOfficeDetail(Event event) throws IOException {
		// logger.debug(event.toString());

		// Check if the tabpanel is already loaded
		if (tabPanelOfficeDetail.getFirstChild() != null) {
			tabOfficeDetail.setSelected(true);

			// refresh the Binding mechanism
			getOfficeDetailCtrl().setOffice(getSelectedOffice());
			getOfficeDetailCtrl().getBinder().loadAll();
			return;
		}

		if (tabPanelOfficeDetail != null) {
			ZksampleCommonUtils.createTabPanelContent(tabPanelOfficeDetail, this, "ModuleMainController", "/WEB-INF/pages/office/officeDetail.zul");
		}
	}

	/**
	 * when the "print offices list" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException {
		final Window win = (Window) Path.getComponent("/outerIndexWindow");
		new OfficeSimpleDJReport(win);
	}

	/**
	 * when the checkBox 'Show All' for filtering is checked. <br>
	 * 
	 * @param event
	 */
	public void onCheck$checkbox_OfficeList_ShowAll(Event event) {
		// logger.debug(event.toString());

		// empty the text search boxes
		txtb_Office_No.setValue(""); // clear
		txtb_Office_Name.setValue(""); // clear
		txtb_Office_City.setValue(""); // clear

		// ++ create the searchObject and init sorting ++//
		HibernateSearchObject<Office> soOffice = new HibernateSearchObject<Office>(Office.class, getOfficeListCtrl().getCountRows());
		soOffice.addSort("filName1", false);

		// Change the BindingListModel.
		if (getOfficeListCtrl().getBinder() != null) {
			getOfficeListCtrl().getPagedBindingListWrapper().setSearchObject(soOffice);

			// get the current Tab for later checking if we must change it
			Tab currentTab = tabbox_OfficeMain.getSelectedTab();

			// check if the tab is one of the Detail tabs. If so do not
			// change the selection of it
			if (!currentTab.equals(tabOfficeList)) {
				tabOfficeList.setSelected(true);
			} else {
				currentTab.setSelected(true);
			}
		}

	}

	/**
	 * Filter the office list with 'like office number'. <br>
	 */
	public void onClick$button_OfficeList_SearchNo(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Office_No.getValue().isEmpty()) {
			checkbox_OfficeList_ShowAll.setChecked(false); // unCheck
			txtb_Office_Name.setValue(""); // clear
			txtb_Office_City.setValue(""); // clear

			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Office> soOffice = new HibernateSearchObject<Office>(Office.class, getOfficeListCtrl().getCountRows());
			soOffice.addFilter(new Filter("filNr", "%" + txtb_Office_No.getValue() + "%", Filter.OP_ILIKE));
			soOffice.addSort("filNr", false);

			// Change the BindingListModel.
			if (getOfficeListCtrl().getBinder() != null) {
				getOfficeListCtrl().getPagedBindingListWrapper().setSearchObject(soOffice);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_OfficeMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabOfficeList)) {
					tabOfficeList.setSelected(true);
				} else {
					currentTab.setSelected(true);
				}
			}
		}
	}

	/**
	 * Filter the office list with 'like office name'. <br>
	 */
	public void onClick$button_OfficeList_SearchName(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Office_Name.getValue().isEmpty()) {
			checkbox_OfficeList_ShowAll.setChecked(false); // unCheck
			txtb_Office_City.setValue(""); // clear
			txtb_Office_No.setValue(""); // clear

			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Office> soOffice = new HibernateSearchObject<Office>(Office.class, getOfficeListCtrl().getCountRows());
			soOffice.addFilter(new Filter("filName1", "%" + txtb_Office_Name.getValue() + "%", Filter.OP_ILIKE));
			soOffice.addSort("filName1", false);

			// Change the BindingListModel.
			if (getOfficeListCtrl().getBinder() != null) {
				getOfficeListCtrl().getPagedBindingListWrapper().setSearchObject(soOffice);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_OfficeMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabOfficeList)) {
					tabOfficeList.setSelected(true);
				} else {
					currentTab.setSelected(true);
				}
			}
		}
	}

	/**
	 * Filter the office list with 'like office city'. <br>
	 */
	public void onClick$button_OfficeList_SearchCity(Event event) throws Exception {
		// logger.debug(event.toString());

		// if not empty
		if (!txtb_Office_City.getValue().isEmpty()) {
			checkbox_OfficeList_ShowAll.setChecked(false); // unCheck
			txtb_Office_Name.setValue(""); // clear
			txtb_Office_No.setValue(""); // clear

			// ++ create the searchObject and init sorting ++//
			HibernateSearchObject<Office> soOffice = new HibernateSearchObject<Office>(Office.class, getOfficeListCtrl().getCountRows());
			soOffice.addFilter(new Filter("filOrt", "%" + txtb_Office_City.getValue() + "%", Filter.OP_ILIKE));
			soOffice.addSort("filOrt", false);

			// Change the BindingListModel.
			if (getOfficeListCtrl().getBinder() != null) {
				getOfficeListCtrl().getPagedBindingListWrapper().setSearchObject(soOffice);

				// get the current Tab for later checking if we must change it
				Tab currentTab = tabbox_OfficeMain.getSelectedTab();

				// check if the tab is one of the Detail tabs. If so do not
				// change the selection of it
				if (!currentTab.equals(tabOfficeList)) {
					tabOfficeList.setSelected(true);
				} else {
					currentTab.setSelected(true);
				}
			}

		}
	}

	/**
	 * When the "help" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		doHelp(event);
	}

	/**
	 * When the "new" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnNew(Event event) throws InterruptedException {
		doNew(event);
	}

	/**
	 * When the "save" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		doSave(event);
	}

	/**
	 * When the "cancel" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnEdit(Event event) throws InterruptedException {
		doEdit(event);
	}

	/**
	 * When the "delete" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		doDelete(event);
	}

	/**
	 * When the "cancel" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCancel(Event event) throws InterruptedException {
		doCancel(event);
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		doResizeSelectedTab(event);
	}

	/**
	 * when the "go first record" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnFirst(Event event) throws InterruptedException {
		doSkip(event);
	}

	/**
	 * when the "go previous record" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPrevious(Event event) throws InterruptedException {
		doSkip(event);
	}

	/**
	 * when the "go next record" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnNext(Event event) throws InterruptedException {
		doSkip(event);
	}

	/**
	 * when the "go last record" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnLast(Event event) throws InterruptedException {
		doSkip(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * 1. Cancel the current action.<br>
	 * 2. Reset the values to its origin.<br>
	 * 3. Set UI components back to readonly/disable mode.<br>
	 * 4. Set the buttons in edit mode.<br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doCancel(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		// reset to the original object
		doResetToInitValues();

		// check first, if the tabs are created
		if (getOfficeDetailCtrl().getBinder() != null) {

			// refresh all dataBinder related controllers/components
			getOfficeDetailCtrl().getBinder().loadAll();

			// set editable Mode
			getOfficeDetailCtrl().doReadOnlyMode(true);

			btnCtrlOffice.setInitEdit();
		}
	}

	/**
	 * Sets all UI-components to writable-mode. Sets the buttons to edit-Mode.
	 * Checks first, if the NEEDED TABS with its contents are created. If not,
	 * than create it by simulate an onSelect() with calling Events.sendEvent()
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doEdit(Event event) {
		// logger.debug(event.toString());

		// get the current Tab for later checking if we must change it
		Tab currentTab = tabbox_OfficeMain.getSelectedTab();

		// check first, if the tabs are created, if not than create it
		if (getOfficeDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabOfficeDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getOfficeDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabOfficeDetail, null));
		}

		// check if the tab is one of the Detail tabs. If so do not change the
		// selection of it
		if (!currentTab.equals(tabOfficeDetail)) {
			tabOfficeDetail.setSelected(true);
		} else {
			currentTab.setSelected(true);
		}

		// remember the old vars
		doStoreInitValues();

		btnCtrlOffice.setBtnStatus_Edit();

		getOfficeDetailCtrl().doReadOnlyMode(false);

		// refresh the UI, because we can click the EditBtn from every tab.
		getOfficeDetailCtrl().getBinder().loadAll();

		// set focus
		getOfficeDetailCtrl().txtb_filNr.focus();
	}

	/**
	 * Deletes the selected Bean from the DB.
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	private void doDelete(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		// check first, if the tabs are created, if not than create them
		if (getOfficeDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabOfficeDetail, null));
		}

		// check first, if the tabs are created
		if (getOfficeDetailCtrl().getBinder() == null) {
			return;
		}

		final Office anOffice = getSelectedOffice();
		if (anOffice != null) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anOffice.getFilBezeichnung();
			final String title = Labels.getLabel("message.Deleting.Record");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, true, new EventListener() {
				@Override
				public void onEvent(Event evt) {
					switch (((Integer) evt.getData()).intValue()) {
					case MultiLineMessageBox.YES:
						try {
							deleteBean();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break; //
					case MultiLineMessageBox.NO:
						break; //
					}
				}

				private void deleteBean() throws InterruptedException {

					/**
					 * Do not allow to modify the demo offices
					 */
					if (getOfficeDetailCtrl().getOffice().getId() <= 2) {
						try {
							ZksampleMessageUtils.doShowNotAllowedForDemoRecords();
							return;
						} catch (final InterruptedException e) {
							throw new RuntimeException(e);
						}

					} else {
						try {
							getOfficeService().delete(anOffice);
						} catch (DataAccessException e) {
							ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
						}
					}
				}

			}

			) == MultiLineMessageBox.YES) {
			}

		}

		btnCtrlOffice.setInitEdit();

		setSelectedOffice(null);
		// refresh the list
		getOfficeListCtrl().doFillListbox();

		// refresh all dataBinder related controllers
		getOfficeDetailCtrl().getBinder().loadAll();
	}

	/**
	 * Saves all involved Beans to the DB.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doSave(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		// save all components data in the several tabs to the bean
		getOfficeDetailCtrl().getBinder().saveAll();

		try {

			/**
			 * Do not allow to modify the demo offices
			 */
			if (getOfficeDetailCtrl().getOffice().getId() <= 2) {
				ZksampleMessageUtils.doShowNotAllowedForDemoRecords();

				doResetToInitValues();
				getOfficeDetailCtrl().getBinder().loadAll();
				return;
			}

			// save it to database
			getOfficeService().saveOrUpdate(getOfficeDetailCtrl().getOffice());
			// if saving is successfully than actualize the beans as
			// origins.
			doStoreInitValues();
			// refresh the list
			getOfficeListCtrl().doFillListbox();
			// later refresh StatusBar
			Events.postEvent("onSelect", getOfficeListCtrl().getListBoxOffice(), getSelectedOffice());

			// show the objects data in the statusBar
			String str = getSelectedOffice().getFilBezeichnung();
			EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).publish(new Event("onChangeSelectedObject", null, str));

		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetToInitValues();

			return;

		} finally {
			btnCtrlOffice.setInitEdit();
			getOfficeDetailCtrl().doReadOnlyMode(true);
		}
	}

	/**
	 * Sets all UI-components to writable-mode. Stores the current Beans as
	 * originBeans and get new Objects from the backend.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doNew(Event event) {
		// logger.debug(event.toString());

		// check first, if the tabs are created
		if (getOfficeDetailCtrl() == null) {
			Events.sendEvent(new Event("onSelect", tabOfficeDetail, null));
			// if we work with spring beanCreation than we must check a little
			// bit deeper, because the Controller are preCreated ?
		} else if (getOfficeDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event("onSelect", tabOfficeDetail, null));
		}

		// remember the current object
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		final Office anOffice = getOfficeService().getNewOffice();

		// set the beans in the related databinded controllers
		getOfficeDetailCtrl().setOffice(anOffice);
		getOfficeDetailCtrl().setSelectedOffice(anOffice);

		// Refresh the binding mechanism
		getOfficeDetailCtrl().setSelectedOffice(getSelectedOffice());
		getOfficeDetailCtrl().getBinder().loadAll();

		// set editable Mode
		getOfficeDetailCtrl().doReadOnlyMode(false);

		// set the ButtonStatus to New-Mode
		btnCtrlOffice.setInitNew();

		tabOfficeDetail.setSelected(true);
		// set focus
		getOfficeDetailCtrl().txtb_filNr.focus();

	}

	/**
	 * Skip/Leaf through the models data according the navigation buttons and
	 * selected the according row in the listbox.
	 * 
	 * @param event
	 */
	private void doSkip(Event event) {

		// get the model and the current selected record
		BindingListModelList blml = (BindingListModelList) getOfficeListCtrl().getListBoxOffice().getModel();

		// check if data exists
		if (blml == null || blml.size() < 1)
			return;

		int index = blml.indexOf(getSelectedOffice());

		/**
		 * Check, if all tabs with data binded components are created So we work
		 * with spring BeanCreation we must check a little bit deeper, because
		 * the Controller are preCreated ? After that, go back to the
		 * current/selected tab.
		 */
		Tab currentTab = tabbox_OfficeMain.getSelectedTab();

		if (getOfficeDetailCtrl().getBinder() == null) {
			Events.sendEvent(new Event(Events.ON_SELECT, tabOfficeDetail, null));
		}

		// go back to selected tab
		currentTab.setSelected(true);

		// Check which button is clicked and calculate the rowIndex
		if (((ForwardEvent) event).getOrigin().getTarget() == btnNext) {
			if (index < (blml.size() - 1)) {
				index = index + 1;
			}
		} else if (((ForwardEvent) event).getOrigin().getTarget() == btnPrevious) {
			if (index > 0) {
				index = index - 1;
			}
		} else if (((ForwardEvent) event).getOrigin().getTarget() == btnFirst) {
			if (index != 0) {
				index = 0;
			}
		} else if (((ForwardEvent) event).getOrigin().getTarget() == btnLast) {
			if (index != blml.size()) {
				index = (blml.size() - 1);
			}
		}

		getOfficeListCtrl().getListBoxOffice().setSelectedIndex(index);
		setSelectedOffice((Office) blml.get(index));

		// call onSelect() for showing the objects data in the statusBar
		Events.sendEvent(new Event(Events.ON_SELECT, getOfficeListCtrl().getListBoxOffice(), getSelectedOffice()));

		// refresh master-detail MASTERS data
		getOfficeDetailCtrl().getBinder().loadAll();

		// EXTRA: if we have a longtext field under the listbox, so we must
		// refresh
		// this binded component too
		// getArticleListCtrl().getBinder().loadComponent(getArticleListCtrl().longBoxArt_LangBeschreibung);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Resizes the container from the selected Tab.
	 * 
	 * @param event
	 */
	private void doResizeSelectedTab(Event event) {
		// logger.debug(event.toString());

		if (tabbox_OfficeMain.getSelectedTab() == tabOfficeDetail) {
			getOfficeDetailCtrl().doFitSize(event);
		} else if (tabbox_OfficeMain.getSelectedTab() == tabOfficeList) {
			// resize and fill Listbox new
			getOfficeListCtrl().doFillListbox();
		}
	}

	/**
	 * Opens the help screen for the current module.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doHelp(Event event) throws InterruptedException {

		ZksampleMessageUtils.doShowNotImplementedMessage();

		// we stop the propagation of the event, because zk will call ALL events
		// with the same name in the namespace and 'btnHelp' is a standard
		// button in this application and can often appears.
		// Events.getRealOrigin((ForwardEvent) event).stopPropagation();
		event.stopPropagation();
	}

	/**
	 * Saves the selected object's current properties. We can get them back if a
	 * modification is canceled.
	 * 
	 * @see doResetToInitValues()
	 */
	public void doStoreInitValues() {

		if (getSelectedOffice() != null) {

			try {
				setOriginalOffice((Office) ZksampleBeanUtils.cloneBean(getSelectedOffice()));
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (final InstantiationException e) {
				throw new RuntimeException(e);
			} catch (final InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (final NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Reset the selected object to its origin property values.
	 * 
	 * @see doStoreInitValues()
	 * 
	 */
	public void doResetToInitValues() {

		if (getOriginalOffice() != null) {

			try {
				setSelectedOffice((Office) ZksampleBeanUtils.cloneBean(getOriginalOffice()));
				// TODO Bug in DataBinder??
				windowOfficeMain.invalidate();

			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (final InstantiationException e) {
				throw new RuntimeException(e);
			} catch (final InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (final NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are getting from the spring framework users
	 * grantedAuthority(). Remember! A right is only a string. <br>
	 */
	// TODO move it to zul
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		button_OfficeList_SearchNo.setVisible(workspace.isAllowed("button_OfficeList_SearchNo"));
		button_OfficeList_SearchName.setVisible(workspace.isAllowed("button_OfficeList_SearchName"));
		button_OfficeList_SearchCity.setVisible(workspace.isAllowed("button_OfficeList_SearchCity"));

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/* Master BEANS */
	public void setOriginalOffice(Office originalOffice) {
		this.originalOffice = originalOffice;
	}

	public Office getOriginalOffice() {
		return this.originalOffice;
	}

	public void setSelectedOffice(Office selectedOffice) {
		this.selectedOffice = selectedOffice;
	}

	public Office getSelectedOffice() {
		return this.selectedOffice;
	}

	public void setOffices(BindingListModelList offices) {
		this.offices = offices;
	}

	public BindingListModelList getOffices() {
		return this.offices;
	}

	/* CONTROLLERS */
	public void setOfficeListCtrl(OfficeListCtrl officeListCtrl) {
		this.officeListCtrl = officeListCtrl;
	}

	public OfficeListCtrl getOfficeListCtrl() {
		return this.officeListCtrl;
	}

	public void setOfficeDetailCtrl(OfficeDetailCtrl officeDetailCtrl) {
		this.officeDetailCtrl = officeDetailCtrl;
	}

	public OfficeDetailCtrl getOfficeDetailCtrl() {
		return this.officeDetailCtrl;
	}

	/* SERVICES */
	public OfficeService getOfficeService() {
		return this.officeService;
	}

	public void setOfficeService(OfficeService officeService) {
		this.officeService = officeService;
	}

	/* COMPONENTS and OTHERS */
}
