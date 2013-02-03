package de.forsthaus.webui.branch;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.service.BrancheService;
import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Controller for the branch modul <b>BranchDetail<b>.<br>
 * <br>
 * <b>WORKS with the annotated databinding mechanism.</b><br>
 * zul-file: /WEB-INF/pages/branch/branchDetail.zul.<br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          03/09/2009: sge changed for allow repainting after resizing.<br>
 *          with the refresh button.<br>
 *          07/04/2010: sge modified for zk5.x with complete Annotated
 *          Databinding.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class BranchDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BranchDetailCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowBranchDetail; // autowired
	protected Borderlayout borderLayout_branchDetail; // autowired

	protected Textbox txtb_BranchText; // autowired

	// Databinding
	protected transient AnnotateDataBinder binder;
	private BranchMainCtrl branchMainCtrl;

	// ServiceDAOs / Domain Classes
	private BrancheService brancheService;

	/**
	 * default constructor.<br>
	 */
	public BranchDetailCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * Set an 'alias' for this composer name to access it in the zul-file.<br>
		 * Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);

		/**
		 * 1. Get the overhanded MainController.<br>
		 * 2. Set this controller in the MainController.<br>
		 * 3. Check if a 'selectedObject' exists yet in the MainController.<br>
		 */
		if (this.arg.containsKey("ModuleMainController")) {
			setBranchMainCtrl((BranchMainCtrl) this.arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getBranchMainCtrl().setBranchDetailCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getBranchMainCtrl().getSelectedBranche() != null) {
				setSelectedBranche(getBranchMainCtrl().getSelectedBranche());
			} else
				setSelectedBranche(null);
		} else {
			setSelectedBranche(null);
		}

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
	public void onCreate$windowBranchDetail(Event event) throws Exception {
		this.binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		this.binder.loadAll();

		doFitSize(event);

		// Testing for JAVA BUG !!!
		// http://www.heise.de/security/meldung/Oracle-warnt-vor-Java-Schwachstelle-1185967.html
		// System.out.println("HotorNot:");
		// double d = Double.parseDouble("2.2250738585072012e-308");
		// System.out.println("Value: " + d);
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
	public void doFitSize(Event event) {

		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - 152;
		this.borderLayout_branchDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		this.windowBranchDetail.invalidate();
	}

	/**
	 * Set all components in readOnly/disabled modus.
	 * 
	 * true = all components are readOnly or disabled.<br>
	 * false = all components are accessable.<br>
	 * 
	 * @param b
	 */
	public void doReadOnlyMode(boolean b) {
		this.txtb_BranchText.setReadonly(b);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Best Pratice Hint:<br>
	 * The setters/getters for the local annotated data binded Beans/Sets are
	 * administered in the module's mainController. Working in this way you have
	 * a clean line to share this beans/sets with other controllers.
	 */
	/* Master BEANS */
	public Branche getBranche() {
		// STORED IN THE module's MainController
		return getBranchMainCtrl().getSelectedBranche();
	}

	public void setBranche(Branche branche) {
		// STORED IN THE module's MainController
		getBranchMainCtrl().setSelectedBranche(branche);
	}

	public Branche getSelectedBranche() {
		// STORED IN THE module's MainController
		return getBranchMainCtrl().getSelectedBranche();
	}

	public void setSelectedBranche(Branche selectedBranche) {
		// STORED IN THE module's MainController
		getBranchMainCtrl().setSelectedBranche(selectedBranche);
	}

	public BindingListModelList getBranches() {
		// STORED IN THE module's MainController
		return getBranchMainCtrl().getBranches();
	}

	public void setBranches(BindingListModelList branches) {
		// STORED IN THE module's MainController
		getBranchMainCtrl().setBranches(branches);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setBranchMainCtrl(BranchMainCtrl branchMainCtrl) {
		this.branchMainCtrl = branchMainCtrl;
	}

	public BranchMainCtrl getBranchMainCtrl() {
		return this.branchMainCtrl;
	}

	/* SERVICES */
	public void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

	public BrancheService getBrancheService() {
		return this.brancheService;
	}

	/* COMPONENTS and OTHERS */

}
