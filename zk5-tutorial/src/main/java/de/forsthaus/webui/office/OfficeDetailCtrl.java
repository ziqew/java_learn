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
package de.forsthaus.webui.office;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Office;
import de.forsthaus.backend.service.OfficeService;
import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/office/officeList.zul
 * file.<br>
 * <b>WORKS with the annotated databinding mechanism.</b><br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changings for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 *          07/04/2010: sge modified for zk5.x with complete Annotated
 *          Databinding.<br>
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class OfficeDetailCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8352659530536077973L;
	private static final Logger logger = Logger.getLogger(OfficeDetailCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowOfficeDetail; // autowired

	protected Borderlayout borderlayout_OfficeDetail; // autowired

	protected Textbox txtb_filNr; // autowired
	protected Textbox txtb_filBezeichnung; // autowired
	protected Textbox txtb_filName1; // autowired
	protected Textbox txtb_filName2; // autowired
	protected Textbox txtb_filOrt; // autowired
	protected Button button_OfficeDialog_PrintOffice; // autowired

	// Databinding
	protected transient AnnotateDataBinder binder;
	private OfficeMainCtrl officeMainCtrl;

	// ServiceDAOs / Domain Classes
	private transient OfficeService officeService;

	/**
	 * default constructor.<br>
	 */
	public OfficeDetailCtrl() {
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
			setOfficeMainCtrl((OfficeMainCtrl) arg.get("ModuleMainController"));

			// SET THIS CONTROLLER TO THE module's Parent/MainController
			getOfficeMainCtrl().setOfficeDetailCtrl(this);

			// Get the selected object.
			// Check if this Controller if created on first time. If so,
			// than the selectedXXXBean should be null
			if (getOfficeMainCtrl().getSelectedOffice() != null) {
				setSelectedOffice(getOfficeMainCtrl().getSelectedOffice());
			} else
				setSelectedOffice(null);
		} else {
			setSelectedOffice(null);
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
	public void onCreate$windowOfficeDetail(Event event) throws Exception {
		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		binder.loadAll();

		doFitSize(event);
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
		borderlayout_OfficeDetail.setHeight(String.valueOf(maxListBoxHeight) + "px");

		windowOfficeDetail.invalidate();
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
		txtb_filNr.setReadonly(b);
		txtb_filBezeichnung.setReadonly(b);
		txtb_filName1.setReadonly(b);
		txtb_filName2.setReadonly(b);
		txtb_filOrt.setReadonly(b);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Best Pratice Hint:<br>
	 * The setters/getters for the local annotated data binded Beans/Sets are
	 * administered in the module's mainController. Working in this way you have
	 * clean line to share this beans/sets with other controllers.
	 */
	/* Master BEANS */
	public Office getOffice() {
		// STORED IN THE module's MainController
		return getOfficeMainCtrl().getSelectedOffice();
	}

	public void setOffice(Office anOffice) {
		// STORED IN THE module's MainController
		getOfficeMainCtrl().setSelectedOffice(anOffice);
	}

	public Office getSelectedOffice() {
		// STORED IN THE module's MainController
		return getOfficeMainCtrl().getSelectedOffice();
	}

	public void setSelectedOffice(Office selectedOffice) {
		// STORED IN THE module's MainController
		getOfficeMainCtrl().setSelectedOffice(selectedOffice);
	}

	public BindingListModelList getOffices() {
		// STORED IN THE module's MainController
		return getOfficeMainCtrl().getOffices();
	}

	public void setOffices(BindingListModelList offices) {
		// STORED IN THE module's MainController
		getOfficeMainCtrl().setOffices(offices);
	}

	public AnnotateDataBinder getBinder() {
		return this.binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	/* CONTROLLERS */
	public void setOfficeMainCtrl(OfficeMainCtrl officeMainCtrl) {
		this.officeMainCtrl = officeMainCtrl;
	}

	public OfficeMainCtrl getOfficeMainCtrl() {
		return this.officeMainCtrl;
	}

	/* SERVICES */
	public void setOfficeService(OfficeService officeService) {
		this.officeService = officeService;
	}

	public OfficeService getOfficeService() {
		return this.officeService;
	}

	/* COMPONENTS and OTHERS */

}
