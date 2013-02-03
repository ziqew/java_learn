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
package de.forsthaus.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.trg.search.Filter;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.model.Customer;
import de.forsthaus.backend.model.Office;
import de.forsthaus.backend.model.SecLoginlog;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.backend.service.CustomerService;
import de.forsthaus.backend.service.IpToCountryService;
import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.backend.service.OfficeService;
import de.forsthaus.backend.service.OrderService;
import de.forsthaus.backend.util.HibernateSearchObject;
import de.forsthaus.gui.service.GuiLoginLoggingService;
import de.forsthaus.services.report.service.ReportService;
import de.forsthaus.util.ZkossComponentTreeUtil;
import de.forsthaus.webui.customer.model.CustomerListModelItemRenderer;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.MyThemeProvider;
import de.forsthaus.webui.util.pagging.PagedListWrapper;

/**
 * The test controller for the /WEB-INF/test.zul file.
 * 
 * 
 * @author bbruhns
 * @author sgerth
 */
public class TestCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 8237296705533772050L;
	private static final Logger logger = Logger.getLogger(TestCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends BaseCtrl' class which extends Window and implements
	 * AfterCompose.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window window_TestCtrl; // autowired
	protected Button btn_Test2; // autowired
	protected Button btn_ChangeTheme; // autowired
	protected Button btn_CountLoginsPerHour; // autowired
	protected Button btn_fillIp2CountryOnceForAppUpdate; // autowired
	protected Button btn_updateIp2CountryFromLookUpHost; // autowired
	protected Button btn_Ip2CountryImport; // autowired
	protected Button btn_createWindow; // autowired
	protected Button btn_CompileReport; // autowired

	// listBox
	protected Paging pagingBranch; // autowired
	protected Listbox listBoxBranch; // autowired
	protected Listheader listheader_Branch_Description; // autowired
	protected Listheader listheader_Branch_No; // autowired

	private transient final FieldComparator fcBraNr_Asc = new FieldComparator("braNr", true);
	private transient final FieldComparator fcBraNr_Desc = new FieldComparator("braNr", false);
	private transient final FieldComparator fcBraBezeichnung_Asc = new FieldComparator("braBezeichnung", true);
	private transient final FieldComparator fcBraBezeichnung_Desc = new FieldComparator("braBezeichnung", false);

	protected Div div_DateBox;
	protected Datebox DateBox_Sample;
	protected Button Btn_ResetDatebox;
	protected Button btn_javaListbox;
	protected Panelchildren panelChildJavaListbox;

	protected Button btn_getRemoteAddress;
	protected Button btn_getRemoteHost;
	protected Button btn_getRemoteUser;
	protected Button btn_getLocalAddress;
	protected Button btn_getLocalName;

	protected Label label_InsertCustomer;
	protected Listbox listBoxCustomer;
	protected Paging pagingKunde;
	protected Listheader listheader_CustNo;
	protected Listheader listheader_CustName1;
	protected Listheader listheader_CustMatchcode;
	protected Listheader listheader_CustName2;
	protected Listheader listheader_CustCity;

	private final FieldComparator fcKunMatchcode_Asc = new FieldComparator("kunMatchcode", true);
	private final FieldComparator fcKunMatchcode_Desc = new FieldComparator("kunMatchcode", false);
	private final FieldComparator fcKunName1_Asc = new FieldComparator("kunName1", true);
	private final FieldComparator fcKunName1_Desc = new FieldComparator("kunName1", false);
	private final FieldComparator fcKunName2_Asc = new FieldComparator("kunName2", true);
	private final FieldComparator fcKunName2_Desc = new FieldComparator("kunName2", false);
	private final FieldComparator fcKunOrt_Asc = new FieldComparator("kunOrt", true);
	private final FieldComparator fcKunOrt_Desc = new FieldComparator("kunOrt", false);
	protected Button btnEditCustomerListbox;

	private transient CustomerService customerService;
	private transient BrancheService brancheService;
	private transient OfficeService officeService;
	private transient LoginLoggingService loginLoggingService;
	private transient GuiLoginLoggingService guiLoginLoggingService;
	private transient IpToCountryService ipToCountryService;
	private transient HibernateSearchObject<SecLoginlog> hsoLoginLog;
	private transient ReportService reportService;

	private transient OrderService orderService;

	private transient PagedListWrapper<Customer> pagedListWrapperCustomer;
	private transient PagedListWrapper<Branche> pagedListWrapperBranche;

	/**
	 * Constructor.<br>
	 */
	public TestCtrl() {
		super();
	}

	public void onClick$btn_javaListbox(Event event) throws InterruptedException {
		logger.info(event.getName());

		final Listbox listbox = new Listbox();
		listbox.setWidth("300px");
		listbox.setHeight("300px");
		listbox.setVisible(true);

		final Listhead lHead = new Listhead();
		lHead.setParent(listbox);
		final Listheader lHeader1 = new Listheader();
		lHeader1.setWidth("30%");
		lHeader1.setLabel("Column 1");
		lHeader1.setParent(lHead);
		lHeader1.setVisible(true);
		final Listheader lHeader2 = new Listheader();
		lHeader2.setWidth("30%");
		lHeader2.setLabel("Column 2");
		lHeader2.setParent(lHead);
		final Listheader lHeader3 = new Listheader();
		lHeader3.setWidth("40%");
		lHeader3.setLabel("Column 3");
		lHeader3.setParent(lHead);

		// set the parent where should hold the listbox.
		// ZK do the rendering
		listbox.setParent(this.panelChildJavaListbox);
	}

	public void onClick$Btn_ResetDatebox(Event event) throws InterruptedException {
		logger.info(event.getName());
		this.DateBox_Sample.setValue(null);

		System.out.println(ZkossComponentTreeUtil.getZulTree(this.window_TestCtrl));

		final TestPanel tp1 = new TestPanel();
		tp1.setParent(this.div_DateBox);
		final TestPanel tp2 = new TestPanel();
		tp2.setParent(this.div_DateBox);

	}

	public void onClick$btn_ChangeTheme(Event event) throws InterruptedException {
		final Execution exe = (Execution) Executions.getCurrent().getNativeRequest();

		MyThemeProvider.setSkinCookie(exe, "silvergray");
	}

	@Secured({ "testSecure" })
	public void onClick$btn_Test2(Event event) throws InterruptedException {
		logger.info(event.getName());

		try {
			if (Messagebox.CANCEL == Messagebox.show("Question is pressed. Are you sure?", "Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)) {
				System.out.println("Messagebox.CANCEL selected!");
				return;
			}

			System.out.println("Messagebox.OK selected!");
			return;
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void onClick$btn_getRemoteAddress(Event event) throws InterruptedException {
		final String str = Executions.getCurrent().getRemoteAddr();
		Messagebox.show("Remote Adress: " + str);
	}

	public void onClick$btn_getRemoteHost(Event event) throws InterruptedException {
		final String str = Executions.getCurrent().getRemoteHost();
		Messagebox.show("Remote Host: " + str);
	}

	public void onClick$btn_getRemoteUser(Event event) throws InterruptedException {
		final String str = Executions.getCurrent().getRemoteUser();
		Messagebox.show("Remote User: " + str);
	}

	public void onClick$btn_getLocalAddress(Event event) throws InterruptedException {
		final String str = Executions.getCurrent().getLocalAddr();
		Messagebox.show("Client Local Address: " + str);
	}

	public void onClick$btn_getLocalName(Event event) throws InterruptedException {
		final String str = Executions.getCurrent().getLocalName();
		Messagebox.show("Client Local Name: " + str);
	}

	public void onClick$BtnSerializeFC(Event event) throws InterruptedException {

		FieldComparator fcOld;
		FieldComparator fcNew;

		fcOld = new FieldComparator("TestColumn", false);

		// Serialize the original class object
		try {
			final FileOutputStream fo = new FileOutputStream("cde.tmp");
			final ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(fcOld);
			so.flush();
			so.close();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		// Deserialize in to new class object
		try {
			final FileInputStream fi = new FileInputStream("cde.tmp");
			final ObjectInputStream si = new ObjectInputStream(fi);
			fcNew = (FieldComparator) si.readObject();
			System.out.println(fcNew.getOrderBy());
			si.close();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		final String longString1 = "Hello. I'm a long string\n\n Hello i'm the second line.\n hjdgf hgjhdgsfhsd jhgjd sfjgj gfdsfg\n\n kjdsds fjk jdsh fjdhfdjsh djsfh jkhjdsf jds jds f ";
		final String longString2 = "Hello. I'm a long string\n\n Hello i'm the second line.\n hjdgf hgjhdgsfhsd jhgjd sfjgj gfdsfg ooiji ojre iorjioj girirjgijr griojgiorjg iorjgir ";
		final String longString3 = "Hello. I'm a long string\n\n Hello i'm the second line.\n hjdgf hgjhdgsfhsd jhgjd sfjgj gfdsfg rok reok kre grigoirejg eopijsj jgioe gjiojg rei re";
		final String longString4 = "Hello. I'm a long string\n\n Hello i'm the second line.\n hjdgf hgjhdgsfhsd jhgjd sfjgj gfdsfg rpokg orkeopkg ok rkropk gpor oprek grekopg kropkpor ";
		final String longString5 = "Hello. I'm a long string\n\n Hello i'm the second line.\n hjdgf hgjhdgsfhsd jhgjd sfjgj gfdsfg röplg reo ropekpo rekerop ok orek oprek porkeop re ";
		final String longString6 = "Hello. I'm a long string\n\n Hello i'm the second line.\n hjdgf hgjhdgsfhsd jhgjd sfjgj gfdsfg pork oprkk opre opkrepok oprek kopre oprekpo rkeop rke ";
		final String message = longString1 + longString2 + longString3 + longString4 + longString5 + longString6;
		final String title = Labels.getLabel("message.Information");
		MultiLineMessageBox.doSetTemplate();
		MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);

	}

	public void onClick$BtnSerializeTB(Event event) throws InterruptedException {

		Textbox fcOld;
		Textbox fcNew;

		fcOld = new Textbox("Test Textbox");

		// Serialize the original class object
		try {
			final FileOutputStream fo = new FileOutputStream("cde.tmp");
			final ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(fcOld);
			so.flush();
			so.close();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		// Deserialize in to new class object
		try {
			final FileInputStream fi = new FileInputStream("cde.tmp");
			final ObjectInputStream si = new ObjectInputStream(fi);
			fcNew = (Textbox) si.readObject();
			System.out.println(fcNew.getValue());
			si.close();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void onClick$test(Event event) throws InterruptedException {

	}

	public void onCreate$window_TestCtrl(Event event) throws Exception {
		// ++ create the searchObject ++//
		final HibernateSearchObject<Branche> so = new HibernateSearchObject<Branche>(Branche.class);
		// init sorting
		so.addSort("braBezeichnung", false);

		// set the paging params
		// pagingBranch.setTotalSize(getBrancheService().getAlleBranche().size());
		this.pagingBranch.setDetailed(true);

		this.listheader_Branch_No.setSortAscending(this.fcBraNr_Asc);
		this.listheader_Branch_No.setSortDescending(this.fcBraNr_Desc);
		this.listheader_Branch_Description.setSortAscending(this.fcBraBezeichnung_Asc);
		this.listheader_Branch_Description.setSortDescending(this.fcBraBezeichnung_Desc);

		this.listBoxBranch.setItemRenderer(new ListitemRenderer() {
			@Override
			public void render(Listitem item, Object data) throws Exception {
				final Branche branche = (Branche) data;
				Listcell lc;
				lc = new Listcell(String.valueOf(branche.getBraBezeichnung()));
				lc.setStyle("text-align: left; padding-left: 5px;");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(branche.getId()));
				lc.setStyle("text-align: left; padding-left: 5px;");
				lc.setParent(item);
			}
		});

		this.pagedListWrapperBranche.init(so, this.listBoxBranch, this.pagingBranch);

		// ++ Customer ++//
		// ++ create the searchObject and init sorting ++//
		final HibernateSearchObject<Customer> so2 = new HibernateSearchObject<Customer>(Customer.class);
		so2.addFilterEqual("kunOrt", "Freiburg");
		so2.addSort("kunName1", false);

		// set the paging params
		this.pagingKunde.setDetailed(true);

		this.listheader_CustMatchcode.setSortAscending(this.fcKunMatchcode_Asc);
		this.listheader_CustMatchcode.setSortDescending(this.fcKunMatchcode_Desc);
		this.listheader_CustName1.setSortAscending(this.fcKunName1_Asc);
		this.listheader_CustName1.setSortDescending(this.fcKunName1_Desc);
		this.listheader_CustName2.setSortAscending(this.fcKunName2_Asc);
		this.listheader_CustName2.setSortDescending(this.fcKunName2_Desc);
		this.listheader_CustCity.setSortAscending(this.fcKunOrt_Asc);
		this.listheader_CustCity.setSortDescending(this.fcKunOrt_Desc);
		this.listBoxCustomer.setItemRenderer(new CustomerListModelItemRenderer());

		this.pagedListWrapperCustomer.init(so2, this.listBoxCustomer, this.pagingKunde);
	}

	public void onClick$button_insertCustomers(Event event) throws InterruptedException {

		final Branche branche = getBrancheService().getBrancheById(1000);
		final Office office = getOfficeService().getOfficeByID(Long.valueOf(1));

		final int countRecords = 10000;

		final RandomDataEngine randomDataEngine = new RandomDataEngine();

		for (int j = 0; j < countRecords; j++) {
			final Customer customer = getCustomerService().getNewCustomer();

			customer.setKunName1(randomDataEngine.getRandomManFirstname());
			customer.setKunName2(randomDataEngine.getRandomLastname());
			customer.setKunMatchcode(customer.getKunName2().toUpperCase());
			customer.setKunOrt(randomDataEngine.getRandomCity());
			customer.setBranche(branche);
			customer.setOffice(office);
			customer.setKunMahnsperre(Boolean.FALSE);

			getCustomerService().saveOrUpdate(customer);
		}
	}

	public void onClick$button_deleteCustomers(Event event) throws InterruptedException {
		logger.debug("Letzte KundenID : " + getCustomerService().getMaxCustomerId());

		while (getCustomerService().getMaxCustomerId() > 49999) {

			getCustomerService().testDeleteCustomersOver50000();
		}

	}

	public void onClick$btn_CountLoginsPerHour(Event event) {
		this.hsoLoginLog = new HibernateSearchObject<SecLoginlog>(SecLoginlog.class);
		this.hsoLoginLog.addFilter(new Filter());

		// neu
		final List<SecLoginlog> list = getLoginLoggingService().getLoginsPerHour(new Date());
		System.out.println("count records : " + list.size());
		for (final SecLoginlog secLoginlog : list) {
			System.out.println(secLoginlog.getLglIp());
		}

	}

	public void onClick$btn_fillIp2CountryOnceForAppUpdate(Event event) {
		getGuiLoginLoggingService().fillIp2CountryOnceForAppUpdate();
	}

	public void onClick$btn_updateIp2CountryFromLookUpHost(Event event) {

		try {
			final String message = Labels.getLabel("message.Information.OutOfOrder");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(message, title, MultiLineMessageBox.OK, "INFORMATION", true);
			return;

		} catch (final Exception e) {
			// TODO: handle exception
		}

		getGuiLoginLoggingService().updateFromHostLookUpMain();
	}

	public void onClick$btn_Ip2CountryImport(Event event) {
		// getIpToCountryService().updateAll();
	}

	public void onClick$btn_createWindow(Event event) throws SuspendNotAllowedException, InterruptedException {
		final String width = "800px";
		final String height = "300px";
		final String uri = "/WEB-INF/pages/welcome.zul";
		final Window window = (Window) Executions.createComponents(uri, (Component) getController(), null);
		window.setWidth(width);
		window.setHeight(height);
		window.doModal();
	}

	public void onClick$btn_CompileReport(Event event) {

		getReportService().compileReport("/de/forsthaus/webui/reports/order/Test_Report_subreportAuftrag.jrxml");

		// // Get the real path for the report
		// String repSrc =
		// Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/reports/order/Test_Report.jrxml");
		// String subDir =
		// Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/reports/order")
		// +
		// "/";
		//
		// // preparing parameters
		// HashMap<String, Object> repParams = new HashMap<String, Object>();
		// repParams.put("Title", "Sample Order Report");
		// repParams.put("SUBREPORT_DIR", subDir);
		//
		// Order anOrder = getOrderService().getOrderById(40);
		// getReportService().printAuftragsPositionen(anOrder, repParams);

		// Textbox tb1 = new Textbox();
		// tb1.setValue("Hallo1");
		// Datebox dtb = new Datebox();
		// dtb.setValue(new Date());
		// Executions.sendRedirect("/WEB-INF/pages/branch/branchList.zul?var1="
		// + tb1.getValue());
		// Executions.sendRedirect("http://www.zkoss.org?var1=" +
		// tb1.getValue());
		// Executions.sendRedirect("http://www.zkoss.org?var1=" + tb1.getValue()
		// + "&var2=" + dtb.getValue());
		// Executions.getCurrent().sendRedirect("/WEB-INF/pages/branch/branchList.zul?var1="
		// + tb1.getValue());
		// System.out.println("Onnnnnnnnnnnnnnklji jfivjofj ovijfdiovfdvfd");

	}

	public void onClick$btnEditCustomerListbox(Event event) {
		final List<Listitem> lstArr = this.listBoxCustomer.getItems();
		logger.debug("Count items :" + this.listBoxCustomer.getItemCount());
		// for (Listitem lstItem : lstArr)
		for (final Object item : this.listBoxCustomer.getItems()) {
			logger.debug("item :" + item);
			if (item instanceof Listitem) {
				final Listitem lstItem = (Listitem) item;
				for (final Object cell : lstItem.getChildren()) {
					logger.debug("cell :" + cell);
					// CHILDREN COUNT is ALWAYS 1
					if (cell instanceof Listcell) {
						final Listcell listcell = (Listcell) cell;

						logger.debug("cell :" + listcell.getLabel());
						for (final Object innercell : listcell.getChildren()) {
							// NEVER GET HERE
							if (innercell instanceof Checkbox) {
								logger.debug("InnerCell = Checkbox");
								((Checkbox) innercell).setDisabled(false);
							}
						}
					}
				}
			}
		}
	}

	public void onSelect$listBoxCustomer(Event event) throws Exception {

		logger.info(event.getTarget().getClass().getName());

		final Set<Listitem> li = this.listBoxCustomer.getSelectedItems();

		for (final Listitem listitem : li) {
			// li.setCheckable(false);
			listitem.setStyle("background-color:#f3d973");
		}

	}

	public void onCheckmark$listBoxCustomer(Event event) throws Exception {

		logger.info(event.getTarget().getClass().getName());

		final Listitem li = this.listBoxCustomer.getSelectedItem();

		// li.setCheckable(false);
		li.setStyle("color: black; background-color:#f3d973");
		this.listBoxCustomer.invalidate();

	}

	// ++++++++++ START: Test for the WrongValueException
	// +++ Forum thread: http://zkoss.org/forum/listComment/11663
	private Textbox userNameTest;
	private Textbox passwordTest;

	public void onClick$btnLoginTest(Event event) {
		System.out.println(event.getName());

		this.userNameTest.getValue();
		this.passwordTest.getValue();
		Clients.closeErrorBox(this.userNameTest);

		if (this.userNameTest.getValue().equalsIgnoreCase("test") && this.passwordTest.getValue().equalsIgnoreCase("test")) {

			System.out.println("&&");
			this.userNameTest.getValue();
			this.userNameTest.invalidate();
			this.userNameTest.focus();

		} else {
			throw new WrongValueException(this.userNameTest, "false userName or password. Please retry.");
		}
	}

	public void onFocus$userNameTest(Event event) {
		System.out.println("onFocus: Textbox userName");
		Clients.closeErrorBox(this.userNameTest);
	}

	public void onFocus$passwordTest(Event event) {
		System.out.println("onFocus: Textbox passWord");

		Clients.closeErrorBox(this.userNameTest);
		// Component[] comps = { userNameTest, passwordTest };
		// Clients.closeErrorBox(comps);
	}

	// ++++++++++ END: Test for the WrongValueException

	// +++++++++++++++++ Getters/Setters+++++++++++++++++++++++

	public void setOfficeService(OfficeService officeService) {
		this.officeService = officeService;
	}

	public OfficeService getOfficeService() {
		if (this.officeService == null) {
			this.officeService = (OfficeService) SpringUtil.getBean("officeService");
			setOfficeService(this.officeService);
		}
		return this.officeService;
	}

	private void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	public BrancheService getBrancheService() {
		if (this.brancheService == null) {
			this.brancheService = (BrancheService) SpringUtil.getBean("brancheService");
			setBrancheService(this.brancheService);
		}
		return this.brancheService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		if (this.customerService == null) {
			this.customerService = (CustomerService) SpringUtil.getBean("customerService");
			setCustomerService(this.customerService);
		}
		return this.customerService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

	public LoginLoggingService getLoginLoggingService() {
		if (this.loginLoggingService == null) {
			this.loginLoggingService = (LoginLoggingService) SpringUtil.getBean("loginLoggingService");
			setLoginLoggingService(this.loginLoggingService);
		}
		return this.loginLoggingService;
	}

	public void setGuiLoginLoggingService(GuiLoginLoggingService guiLoginLoggingService) {
		this.guiLoginLoggingService = guiLoginLoggingService;
	}

	public GuiLoginLoggingService getGuiLoginLoggingService() {
		if (this.guiLoginLoggingService == null) {
			this.guiLoginLoggingService = (GuiLoginLoggingService) SpringUtil.getBean("guiLoginLoggingService");
			setGuiLoginLoggingService(this.guiLoginLoggingService);
		}
		return this.guiLoginLoggingService;
	}

	public void setIpToCountryService(IpToCountryService ipToCountryService) {
		this.ipToCountryService = ipToCountryService;
	}

	public IpToCountryService getIpToCountryService() {
		if (this.ipToCountryService == null) {
			this.ipToCountryService = (IpToCountryService) SpringUtil.getBean("guiLoginLoggingService");
			setIpToCountryService(this.ipToCountryService);
		}
		return this.ipToCountryService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public OrderService getOrderService() {
		if (this.orderService == null) {
			this.orderService = (OrderService) SpringUtil.getBean("orderService");
			setOrderService(this.orderService);
		}

		return this.orderService;
	}

	public ReportService getReportService() {
		if (this.reportService == null) {
			this.reportService = (ReportService) SpringUtil.getBean("reportService");
			setReportService(this.reportService);
		}
		return this.reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public PagedListWrapper<Customer> getPagedListWrapperCustomer() {
		return this.pagedListWrapperCustomer;
	}

	public void setPagedListWrapperCustomer(PagedListWrapper<Customer> pagedListWrapperCustomer) {
		this.pagedListWrapperCustomer = pagedListWrapperCustomer;
	}

	public PagedListWrapper<Branche> getPagedListWrapperBranche() {
		return this.pagedListWrapperBranche;
	}

	public void setPagedListWrapperBranche(PagedListWrapper<Branche> pagedListWrapperBranche) {
		this.pagedListWrapperBranche = pagedListWrapperBranche;
	}

}

// test: Popup must set his parent to the zul-page
// Tabbox tabbox = (Tabbox)
// center.getFellow("divCenter").getFellow("tabBoxIndexCenter");
// Menupopup menupopup = new Menupopup();
// menupopup.appendChild(new Menuitem("A"));
// menupopup.appendChild(new Menuitem("B"));
// menupopup.setParent(getRoot());
// tabbox.setContext(menupopup);

