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
package de.forsthaus.webui.security.user;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.annotation.Secured;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.backend.model.Language;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.UserService;
import de.forsthaus.webui.security.user.model.LanguageListModelItemRenderer;
import de.forsthaus.webui.security.user.model.UserRolesListModelItemRenderer;
import de.forsthaus.webui.util.ButtonStatusCtrl;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.MultiLineMessageBox;
import de.forsthaus.webui.util.ZksampleMessageUtils;
import de.forsthaus.webui.util.constraint.NoEmptyAndEqualStringsConstraint;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/sec_user/userDialog.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 *          10/12/2009: sge changings in the saving routine.<br>
 *          11/07/2009: bbr changed to extending from GFCBaseCtrl<br>
 *          (GenericForwardComposer) for spring-managed creation.<br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class UserDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -546886879998950467L;
	private transient static final Logger logger = Logger.getLogger(UserDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window userDialogWindow; // autowired
	protected Tab tab_UserDialog_Details; // autowired

	// panel account details
	protected Textbox usrLoginname; // autowired
	protected Textbox usrPassword; // autowired
	protected Textbox usrPasswordRetype; // autowired
	protected Textbox usrFirstname; // autowired
	protected Textbox usrLastname; // autowired
	protected Textbox usrEmail; // autowired
	protected Listbox lbox_usrLocale; // autowired

	// panel status
	protected Checkbox usrEnabled; // autowired
	protected Checkbox usrAccountnonexpired; // autowired
	protected Checkbox usrCredentialsnonexpired; // autowired
	protected Checkbox usrAccountnonlocked; // autowired

	// panel security token, SORRY logic it's internally
	protected Textbox usrToken; // autowired

	// panel granted roles
	protected Listbox listBoxDetails_UserRoles; // autowired
	protected Listheader listheader_UserDialog_UserRoleId; // autowired
	protected Listheader listheader_UserDialog_UserRoleShortDescription; // autowired

	// overhanded vars per params
	private transient Listbox listBoxUser; // overhanded
	private transient SecUser user; // overhanded

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_usrLoginname;
	private transient String oldVar_usrPassword;
	private transient String oldVar_usrPasswordRetype;
	private transient String oldVar_usrFirstname;
	private transient String oldVar_usrLastname;
	private transient String oldVar_usrEmail;
	private transient Listitem oldVar_usrLangauge;
	private transient boolean oldVar_usrEnabled;
	private transient boolean oldVar_usrAccountnonexpired;
	private transient boolean oldVar_usrCredentialsnonexpired;
	private transient boolean oldVar_usrAccountnonlocked;
	private transient String oldVar_usrToken;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_UserDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowired
	protected Button btnEdit; // autowired
	protected Button btnDelete; // autowired
	protected Button btnSave; // autowired
	protected Button btnCancel;// autowired
	protected Button btnClose; // autowired

	// checkRights
	protected Button btnHelp; // autowired
	protected Panel panel_UserDialog_Status; // autowired
	protected Panel panel_UserDialog_SecurityToken; // autowired
	protected Tabpanel tabpanel_UserDialog_Details; // autowired

	// ServiceDAOs
	private transient UserService userService;

	/**
	 * default constructor.<br>
	 */
	public UserDialogCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected user object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$userDialogWindow(Event event) throws Exception {
		/* set comps cisible dependent of the users rights */
		doCheckRights();

		// create the Button Controller. Disable not used buttons during working
		btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), btnCtroller_ClassPrefix, true, btnNew, btnEdit, btnDelete, btnSave, btnCancel, btnClose);

		// get the params map that are overhanded by creation.
		Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("user")) {
			user = (SecUser) args.get("user");
			setUser(user);
		} else {
			setUser(null);
		}

		// we get the listBox Object for the users list. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete users here.
		if (args.containsKey("listBoxUser")) {
			listBoxUser = (Listbox) args.get("listBoxUser");
		} else {
			listBoxUser = null;
		}

		// +++++++++ DropDown ListBox
		// set listModel and itemRenderer for the dropdown listbox
		lbox_usrLocale.setModel(new ListModelList(getUserService().getAllLanguages()));
		lbox_usrLocale.setItemRenderer(new LanguageListModelItemRenderer());

		// if available, select the object
		ListModelList lml = (ListModelList) lbox_usrLocale.getModel();

		/**
		 * check if the user is new ( means: userID == Long.MIN_VALUE )<br>
		 * If new than it's not a transient hibernate object and an error occur
		 * by using this ID for a hibernate query<br>
		 */
		if (user.isNew()) {
			lbox_usrLocale.setSelectedIndex(-1);
		} else {
			// Set the ListModel and the itemRenderer.
			listBoxDetails_UserRoles.setModel(new ListModelList(getUserService().getRolesByUser(getUser())));
			listBoxDetails_UserRoles.setItemRenderer(new UserRolesListModelItemRenderer());

			if (!StringUtils.isEmpty(user.getUsrLocale())) {
				Language lang = getUserService().getLanguageByLocale(user.getUsrLocale());
				lbox_usrLocale.setSelectedIndex(lml.indexOf(lang));
			}
		}

		doShowDialog(getUser());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {

		final UserWorkspace workspace = getUserWorkspace();

		userDialogWindow.setVisible(workspace.isAllowed("userDialogWindow"));

		tab_UserDialog_Details.setVisible(workspace.isAllowed("tab_UserDialog_Details"));
		tabpanel_UserDialog_Details.setVisible(workspace.isAllowed("tab_UserDialog_Details"));

		btnHelp.setVisible(workspace.isAllowed("button_UserDialog_btnHelp"));
		btnNew.setVisible(workspace.isAllowed("button_UserDialog_btnNew"));
		btnEdit.setVisible(workspace.isAllowed("button_UserDialog_btnEdit"));
		btnDelete.setVisible(workspace.isAllowed("button_UserDialog_btnDelete"));
		btnSave.setVisible(workspace.isAllowed("button_UserDialog_btnSave"));
		btnClose.setVisible(workspace.isAllowed("button_UserDialog_btnClose"));

		panel_UserDialog_Status.setVisible(workspace.isAllowed("panel_UserDialog_Status"));
		panel_UserDialog_SecurityToken.setVisible(workspace.isAllowed("panel_UserDialog_SecurityToken"));
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$userDialogWindow(Event event) throws Exception {
		// logger.debug(event.toString());

		doClose(event);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		doSave();
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		// logger.debug(event.toString());

		doEdit();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		// logger.debug(event.toString());

		doNew();
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		doDelete();
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		// logger.debug(event.toString());

		doCancel();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		// logger.debug(event.toString());

		try {
			doClose(event);
		} catch (final Exception e) {
			// close anyway
			userDialogWindow.onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 */
	private void doClose(Event event) throws Exception {

		if (isDataChanged()) {

			// Show a confirm box
			String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true, new EventListener() {
				@Override
				public void onEvent(Event evt) {
					switch (((Integer) evt.getData()).intValue()) {
					case MultiLineMessageBox.YES:
						try {
							doSave();
						} catch (final InterruptedException e) {
							throw new RuntimeException(e);
						}
					case MultiLineMessageBox.NO:
						break; //
					}
				}
			}

			) == MultiLineMessageBox.YES) {
			}
		}
		userDialogWindow.onClose();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		doResetInitValues();
		doReadOnly();
		btnCtrl.setInitEdit();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param anUser
	 */
	public void doWriteBeanToComponents(SecUser anUser) {

		usrLoginname.setValue(anUser.getUsrLoginname());
		usrPassword.setValue(anUser.getUsrPassword());
		usrPasswordRetype.setValue(anUser.getUsrPassword());
		usrFirstname.setValue(anUser.getUsrFirstname());
		usrLastname.setValue(anUser.getUsrLastname());
		usrEmail.setValue(anUser.getUsrEmail());

		usrEnabled.setChecked(anUser.isUsrEnabled());
		usrAccountnonexpired.setChecked(anUser.isUsrAccountnonexpired());
		usrAccountnonlocked.setChecked(anUser.isUsrAccountnonlocked());
		usrCredentialsnonexpired.setChecked(anUser.isUsrCredentialsnonexpired());

		usrToken.setValue(anUser.getUsrToken());

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param anUser
	 */
	public void doWriteComponentsToBean(SecUser anUser) {

		anUser.setUsrLoginname(usrLoginname.getValue());
		anUser.setUsrPassword(usrPassword.getValue());
		anUser.setUsrFirstname(usrFirstname.getValue());
		anUser.setUsrLastname(usrLastname.getValue());
		anUser.setUsrEmail(usrEmail.getValue());

		if (usrEnabled.isChecked() == true) {
			anUser.setUsrEnabled(true);
		} else {
			anUser.setUsrEnabled(false);
		}

		if (usrAccountnonexpired.isChecked() == true) {
			anUser.setUsrAccountnonexpired(true);
		} else {
			anUser.setUsrAccountnonexpired(false);
		}

		if (usrAccountnonlocked.isChecked() == true) {
			anUser.setUsrAccountnonlocked(true);
		} else {
			anUser.setUsrAccountnonlocked(false);
		}

		if (usrCredentialsnonexpired.isChecked() == true) {
			anUser.setUsrCredentialsnonexpired(true);
		} else {
			anUser.setUsrCredentialsnonexpired(false);
		}

		anUser.setUsrToken(usrToken.getValue());

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param anUser
	 * @throws InterruptedException
	 */
	public void doShowDialog(SecUser anUser) throws InterruptedException {

		// if aUser == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (anUser == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			anUser = getUserService().getNewUser();
		}

		// set Readonly mode accordingly if the object is new or not.
		if (anUser.isNew()) {
			btnCtrl.setInitNew();
			doEdit();
		} else {
			btnCtrl.setInitEdit();
			doReadOnly();
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(anUser);

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			usrLoginname.setFocus(true);

			userDialogWindow.doModal(); // open the dialog in modal mode
		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		oldVar_usrLoginname = usrLoginname.getValue();
		oldVar_usrPassword = usrPassword.getValue();
		oldVar_usrPasswordRetype = usrPasswordRetype.getValue();
		oldVar_usrFirstname = usrFirstname.getValue();
		oldVar_usrLastname = usrLastname.getValue();
		oldVar_usrEmail = usrEmail.getValue();
		oldVar_usrLangauge = lbox_usrLocale.getSelectedItem();
		oldVar_usrEnabled = usrEnabled.isChecked();
		oldVar_usrAccountnonexpired = usrAccountnonexpired.isChecked();
		oldVar_usrCredentialsnonexpired = usrCredentialsnonexpired.isChecked();
		oldVar_usrAccountnonlocked = usrAccountnonlocked.isChecked();
		oldVar_usrToken = usrToken.getValue();
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		usrLoginname.setValue(oldVar_usrLoginname);
		usrPassword.setValue(oldVar_usrPassword);
		usrPasswordRetype.setValue(oldVar_usrPasswordRetype);
		usrFirstname.setValue(oldVar_usrFirstname);
		usrLastname.setValue(oldVar_usrLastname);
		usrEmail.setValue(oldVar_usrEmail);
		lbox_usrLocale.setSelectedItem(oldVar_usrLangauge);
		usrEnabled.setChecked(oldVar_usrEnabled);
		usrAccountnonexpired.setChecked(oldVar_usrAccountnonexpired);
		usrCredentialsnonexpired.setChecked(oldVar_usrCredentialsnonexpired);
		usrAccountnonlocked.setChecked(oldVar_usrAccountnonlocked);
		usrToken.setValue(oldVar_usrToken);
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		boolean changed = false;

		if (oldVar_usrLoginname != usrLoginname.getValue()) {
			changed = true;
		}
		if (oldVar_usrPassword != usrPassword.getValue()) {
			changed = true;
		}
		if (oldVar_usrPasswordRetype != usrPasswordRetype.getValue()) {
			changed = true;
		}
		if (oldVar_usrFirstname != usrFirstname.getValue()) {
			changed = true;
		}
		if (oldVar_usrLastname != usrLastname.getValue()) {
			changed = true;
		}
		if (oldVar_usrEmail != usrEmail.getValue()) {
			changed = true;
		}
		if (oldVar_usrLangauge != lbox_usrLocale.getSelectedItem()) {
			changed = true;
		}
		if (oldVar_usrEnabled != usrEnabled.isChecked()) {
			changed = true;
		}
		if (oldVar_usrAccountnonexpired != usrAccountnonexpired.isChecked()) {
			changed = true;
		}
		if (oldVar_usrCredentialsnonexpired != usrCredentialsnonexpired.isChecked()) {
			changed = true;
		}
		if (oldVar_usrAccountnonlocked != usrAccountnonlocked.isChecked()) {
			changed = true;
		}
		if (oldVar_usrToken != usrToken.getValue()) {
			changed = true;
		}

		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		setValidationOn(true);

		usrLoginname.setConstraint("NO EMPTY");
		usrPassword.setConstraint("NO EMPTY");
		usrPasswordRetype.setConstraint(new NoEmptyAndEqualStringsConstraint(this.usrPassword));
		usrFirstname.setConstraint("NO EMPTY");
		usrLastname.setConstraint("NO EMPTY");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {

		setValidationOn(false);

		usrLoginname.setConstraint("");
		usrPassword.setConstraint("");
		usrPasswordRetype.setConstraint("");
		usrFirstname.setConstraint("");
		usrLastname.setConstraint("");

		// TODO helper textbox for selectedItem ?????
		// rigType.getSelectedItem()) {

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		usrLoginname.setReadonly(false);
		usrPassword.setReadonly(false);
		usrPasswordRetype.setReadonly(false);
		usrFirstname.setReadonly(false);
		usrLastname.setReadonly(false);
		usrEmail.setReadonly(false);
		lbox_usrLocale.setDisabled(false);

		usrEnabled.setDisabled(false);
		usrAccountnonexpired.setDisabled(false);
		usrAccountnonlocked.setDisabled(false);
		usrCredentialsnonexpired.setDisabled(false);

		usrToken.setReadonly(false);

		btnCtrl.setBtnStatus_Edit();
		usrLoginname.focus();

		// remember the old vars
		doStoreInitValues();
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {

		usrLoginname.setReadonly(true);
		usrPassword.setReadonly(true);
		usrPasswordRetype.setReadonly(true);
		usrFirstname.setReadonly(true);
		usrLastname.setReadonly(true);
		usrEmail.setReadonly(true);
		lbox_usrLocale.setDisabled(true);

		usrEnabled.setDisabled(true);
		usrAccountnonexpired.setDisabled(true);
		usrAccountnonlocked.setDisabled(true);
		usrCredentialsnonexpired.setDisabled(true);

		usrToken.setReadonly(true);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {

		// temporarely disable the validation to allow the field's clearing
		doRemoveValidation();

		usrLoginname.setValue("");
		usrPassword.setValue("");
		usrPasswordRetype.setValue("");
		usrFirstname.setValue("");
		usrLastname.setValue("");
		usrEmail.setValue("");

		usrEnabled.setChecked(false);
		usrAccountnonexpired.setChecked(true);
		usrAccountnonlocked.setChecked(true);
		usrCredentialsnonexpired.setChecked(true);

		usrToken.setValue("");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a secUser object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {

		final SecUser anUser = getUser();

		// Show a confirm box
		String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + anUser.getUsrLoginname() + " | " + anUser.getUsrFirstname() + " ,"
				+ anUser.getUsrLastname();
		String title = Labels.getLabel("message.Deleting.Record");

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true, new EventListener() {
			@Override
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case MultiLineMessageBox.YES:
					deleteUser();
				case MultiLineMessageBox.NO:
					break; //
				}
			}

			private void deleteUser() {

				/**
				 * Prevent the deleting of the demo users
				 */
				try {
					if (anUser.getId() <= 14 && anUser.getId() >= 10) {
						ZksampleMessageUtils.doShowNotAllowedForDemoRecords();
						return;
					} else {

						// delete from database
						try {
							getUserService().delete(anUser);
						} catch (DataAccessException e) {
							ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());
						}

						// now synchronize the listBox
						final ListModelList lml = (ListModelList) listBoxUser.getListModel();

						// Check if the object is new or updated
						// -1 means that the obj is not in the list, so
						// it's
						// new..
						if (lml.indexOf(anUser) == -1) {
						} else {
							lml.remove(lml.indexOf(anUser));
						}
					}
				} catch (final Exception e) {
					// TODO: handle exception
				}

				userDialogWindow.onClose(); // close
			}
		}

		) == MultiLineMessageBox.YES) {
		}

	}

	/**
	 * Create a new secUser object. <br>
	 */
	private void doNew() {

		// remember the old vars
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// We don't create a new DomainObject() in the frontend.
		// We GET it from the backend.
		setUser(getUserService().getNewUser());

		// these comps needed to be init
		usrEnabled.setChecked(false);
		usrAccountnonexpired.setChecked(true);
		usrAccountnonlocked.setChecked(true);
		usrCredentialsnonexpired.setChecked(true);

		doClear(); // clear all commponents
		doEdit(); // edit mode

		btnCtrl.setBtnStatus_New();

	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	@Secured( { btnCtroller_ClassPrefix + "btnSave" })
	public void doSave() throws InterruptedException {
		System.out.println("doSave");

		final SecUser anUser = getUser();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!isValidationOn()) {
			doSetValidation();
		}

		// fill the object with the components data
		doWriteComponentsToBean(anUser);

		// validate password again
		usrPassword.getValue();
		usrPasswordRetype.getValue();

		/* if a language is selected get the object from the listbox */
		Listitem item = lbox_usrLocale.getSelectedItem();

		if (item != null) {
			ListModelList lml1 = (ListModelList) lbox_usrLocale.getListModel();
			Language lang = (Language) lml1.get(item.getIndex());
			anUser.setUsrLocale(lang.getLanLocale());
		}

		// save it to database
		try {
			getUserService().saveOrUpdate(anUser);
		} catch (DataAccessException e) {
			ZksampleMessageUtils.showErrorMessage(e.getMostSpecificCause().toString());

			// Reset to init values
			doResetInitValues();

			doReadOnly();
			btnCtrl.setBtnStatus_Save();
			return;
		}

		// now synchronize the listBox
		ListModelList lml = (ListModelList) listBoxUser.getListModel();

		// Check if the object is new or updated
		// -1 means that the obj is not in the list, so it's new.
		if (lml.indexOf(anUser) == -1) {
			lml.add(anUser);
		} else {
			lml.set(lml.indexOf(anUser), anUser);
		}

		doReadOnly();
		btnCtrl.setBtnStatus_Save();
		// init the old values vars new
		doStoreInitValues();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public SecUser getUser() {
		return this.user;
	}

	public void setUser(SecUser user) {
		this.user = user;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}