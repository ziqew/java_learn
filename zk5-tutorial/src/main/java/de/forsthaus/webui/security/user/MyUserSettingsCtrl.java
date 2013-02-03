/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of openTruuls™. http://www.opentruuls.org/
 *
 * openTruuls™ community edition is free software: you can 
 * redistribute it and/or modify it under the terms of the 
 * openTruuls™ public License which is based on the Apache 
 * License 2.0 from the Apache Foundation. The only permission
 * for using openTruuls™ is that a working as a SaaS solution
 * in a mass hosting way is not allowed without approval from the 
 * name holder of openTruuls™.  
 * 
 * openTruuls™ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * openTruuls™ public License for more details.
 *
 * You should have received a copy of the openTruuls™ public License
 * along with openTruuls™.  If not, see <http://www.opentruuls.org/licenses/>.
 */
package de.forsthaus.webui.security.user;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.backend.util.ZksampleBeanUtils;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * EN: Controller for the <b>My User Settings</b>.<br>
 * For checking the re-typed password we use an own local bean Pwd with a
 * property 'retypePassword' that is filled at start with the users password.<br>
 * DE: Controller fuer das Modul <b>User Einstellungen</b>. <br>
 * Um das re-typed Passwort zu pruefen, verwenden wir eine eigene locale Klasse
 * Pwd deren property 'retypePassword' beim Start gefuellt wird.<br>
 * <br>
 * zul-file: /WEB-INF/pages/security/mySettings/mySettings.zul.<br>
 * <br>
 * 
 * 
 * @author Stephan Gerth
 */
public class MyUserSettingsCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient static final Logger logger = Logger.getLogger(MyUserSettingsCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window windowMyUserSettings; // autowired

	protected Textbox txtb_User_Loginname; // autowired
	protected Textbox txtb_User_Password; // autowired
	protected Textbox txtb_User_Password_Retype; // autowired
	protected Textbox txtb_User_Lastname; // autowired
	protected Textbox txtb_User_Firstname; // autowired

	protected Textbox txtb_User_Email; // autowired

	// Button controller for the CRUD buttons
	private final String btnCtroller_ClassPrefix = "btn_MyUserSettings_";
	private ButtonStatusCtrl btnCtrlMyUserSettings;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire

	// ServiceDAOs / Domain Classes
	private UserService userService;

	// databinding
	protected transient AnnotateDataBinder binder;
	private SecUser selectedUser;
	// a copy from the selected Object. Used for cancel an action.
	private SecUser originalUser;

	// internal class for holding the retyped-password
	private Pwd pwd;

	/**
	 * default constructor.<br>
	 */
	public MyUserSettingsCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * Set an 'alias' for this composer name in the zul file for access.<br>
		 * Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		self.setAttribute("controller", this, false);

		// create the Button Controller. Disable not used buttons during working
		btnNew.detach();
		btnDelete.detach();
		btnCtrlMyUserSettings = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, null, btnEdit, null, btnSave, btnCancel, btnClose);
		btnCtrlMyUserSettings.setSecurityActive(false);

		// init the buttons on editMode
		btnCtrlMyUserSettings.setInitEdit();

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
	public void onCreate$windowMyUserSettings(Event event) throws Exception {
		// logger.debug("--> " + event.toString());

		binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);

		// Get the logged in user
		SecUser anUser = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecUser();

		if (anUser != null) {
			setUser(anUser);
			setSelectedUser(anUser);

			// set the retyped password in an special created bean for
			// databinding
			Pwd aPwd = new Pwd();
			aPwd.setRetypePassword(anUser.getUsrPassword());
			setPwd(aPwd);
		}

		binder.loadAll();

		windowMyUserSettings.doModal();

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
	 * When the "cancel" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCancel(Event event) throws InterruptedException {
		doCancel(event);
	}

	/**
	 * When the "close" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * 1. Cancel the current action.<br>
	 * 2. Reset the values to its origin.<br>
	 * 3. Set UI components back to readOnly/disable mode.<br>
	 * 4. Set the buttons in edit mode.<br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doCancel(Event event) throws InterruptedException {

		// check first, if the tabs are created
		if (getBinder() != null) {

			// reset to the original object
			doResetToInitValues();
			// additionally we must manually set the reTyped Password to its
			// original
			getPwd().setRetypePassword(getSelectedUser().getUsrPassword());

			getBinder().loadAll();

			// set editable Mode
			doReadOnlyMode(true);

			btnCtrlMyUserSettings.setInitEdit();
		}

		btnCtrlMyUserSettings.setInitEdit();
	}

	/**
	 * Saves all involved Beans to the DB.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doSave(Event event) throws InterruptedException {

		getBinder().saveAll();

		// check if Pasword equals ReTyped password
		if (!StringUtils.equals(getSelectedUser().getUsrPassword(), getPwd().getRetypePassword())) {
			throw new WrongValueException(txtb_User_Password_Retype, Labels.getLabel("message.error.RetypedPasswordMustBeSame"));
		}

		try {
			// save it
			getUserService().saveOrUpdate(getSelectedUser());
			// if saving is successfully than actualize the beans as origins.
			doStoreInitValues();

		} catch (Exception e) {
			ZksampleMessageUtils.showErrorMessage(e.getMessage());
		} finally {
			btnCtrlMyUserSettings.setInitEdit();
			doReadOnlyMode(true);
		}

		btnCtrlMyUserSettings.setInitEdit();
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
	 * Sets all UI-components to writable-mode. Sets the buttons to edit-Mode.
	 * Checks first, if the NEEDED TABS with its contents are created. If not,
	 * than create it by simulate an onSelect() with calling Events.sendEvent()
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterruptedException
	 */
	private void doEdit(Event event) throws InterruptedException {

		doStoreInitValues();

		doReadOnlyMode(false);

		btnCtrlMyUserSettings.setBtnStatus_Edit();
	}

	/**
	 * When the Tab/Window is closed.<br>
	 * We check if their are unsaved data by comparing the selectedBean's data
	 * with the originalBean's data.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doClose(Event event) throws InterruptedException {

		if (isDataChanged()) {

			// Show a confirm box
			final String msg = Labels.getLabel("message.Information.SaveModifiedDataYesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO | MultiLineMessageBox.CANCEL, MultiLineMessageBox.QUESTION, true, new EventListener() {
				@Override
				public void onEvent(Event evt) {
					switch (((Integer) evt.getData()).intValue()) {
					case MultiLineMessageBox.YES:
						try {
							doSave(evt);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						windowMyUserSettings.onClose();
					case MultiLineMessageBox.NO:
						windowMyUserSettings.onClose();
					case MultiLineMessageBox.CANCEL:
						break;
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}

			event.stopPropagation();

		} else {
			windowMyUserSettings.onClose();
		}

	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * the involved controllers doStoreInitValues() . <br>
	 * Works by comparing the properties of the beans.
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		boolean result = true;

		SecUser selObject = getSelectedUser();
		SecUser origObject = getOriginalUser();

		if (selObject == null || origObject == null) {
			result = false;
		} else {
			// invert boolean
			result = !org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals(selObject, origObject);
		}

		return result;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Set all components in readOnly/disabled modus.
	 * 
	 * true = all components are readOnly or disabled.<br>
	 * false = all components are accessable.<br>
	 * 
	 * @param b
	 */
	public void doReadOnlyMode(boolean b) {
		txtb_User_Loginname.setReadonly(b);
		txtb_User_Password.setReadonly(b);
		txtb_User_Password_Retype.setReadonly(b);

		txtb_User_Firstname.setReadonly(b);
		txtb_User_Lastname.setReadonly(b);

		txtb_User_Email.setReadonly(b);
	}

	/**
	 * Saves the selected object's current properties. We can get them back if a
	 * modification is canceled.
	 * 
	 * @throws InterruptedException
	 * 
	 * @see doResetToInitValues()
	 */
	public void doStoreInitValues() throws InterruptedException {

		if (getSelectedUser() != null) {

			try {
				setOriginalUser((SecUser) ZksampleBeanUtils.cloneBean(getSelectedUser()));
			} catch (IllegalAccessException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			} catch (InstantiationException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			} catch (InvocationTargetException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			} catch (NoSuchMethodException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			}
		}
	}

	/**
	 * Reset the selected object to its origin property values.
	 * 
	 * @throws InterruptedException
	 * 
	 * @see doStoreInitValues()
	 * 
	 */
	public void doResetToInitValues() throws InterruptedException {

		if (getOriginalUser() != null) {

			try {
				setSelectedUser((SecUser) ZksampleBeanUtils.cloneBean(getOriginalUser()));

				// TODO Bug in DataBinder??
				windowMyUserSettings.invalidate();

			} catch (IllegalAccessException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			} catch (InstantiationException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			} catch (InvocationTargetException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			} catch (NoSuchMethodException e) {
				ZksampleMessageUtils.showErrorMessage(e.getMessage());
			}
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setUser(SecUser anUser) {
		setSelectedUser(anUser);
	}

	public SecUser getUser() {
		return getSelectedUser();
	}

	public void setSelectedUser(SecUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	public SecUser getSelectedUser() {
		return selectedUser;
	}

	public void setOriginalUser(SecUser originalUser) {
		this.originalUser = originalUser;
	}

	public SecUser getOriginalUser() {
		return originalUser;
	}

	public AnnotateDataBinder getBinder() {
		return binder;
	}

	public void setBinder(AnnotateDataBinder binder) {
		this.binder = binder;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	/**
	 * EN: Internal Class for holding the re-typed password for checking against
	 * the users original password.<br>
	 * DE: Interne Klasse die das re-typed Passwort haelt, das gegen des Users
	 * Passwort geprueft wird.<br>
	 * 
	 * @author sge
	 * 
	 */
	final public class Pwd implements Serializable {

		private static final long serialVersionUID = 1L;

		private String retypePassword;

		public void setRetypePassword(String retypePassword) {
			this.retypePassword = retypePassword;
		}

		public String getRetypePassword() {
			return retypePassword;
		}
	}

	public Pwd getPwd() {
		return pwd;
	}

	public void setPwd(Pwd pwd) {
		this.pwd = pwd;
	}

}
