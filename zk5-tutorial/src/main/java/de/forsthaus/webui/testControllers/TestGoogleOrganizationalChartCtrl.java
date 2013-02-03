package de.forsthaus.webui.testControllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * Controller for the Google OrganizationalCharts. <br>
 * Zul-file: /pages/test/google_OrganizationalChart.zul <br>
 * 
 * @author sge
 * 
 */
public class TestGoogleOrganizationalChartCtrl extends GFCBaseCtrl {

	private static final long serialVersionUID = 1L;

	protected Window windowTestOrgaChart; // autowired

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
		// this.binder = (AnnotateDataBinder)
		// event.getTarget().getAttribute("binder", true);
		// this.binder.loadAll();

		// doFitSize(event);

		windowTestOrgaChart.doModal();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

}
