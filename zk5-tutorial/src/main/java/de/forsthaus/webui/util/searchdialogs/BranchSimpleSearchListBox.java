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
package de.forsthaus.webui.util.searchdialogs;

import java.io.Serializable;

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
import org.zkoss.zul.Center;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.Branche;
import de.forsthaus.backend.service.BrancheService;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can search and select a branch object. By onClosing this box <b>returns</b>
 * an object or null. <br>
 * The object can returned by selecting and clicking the OK button or by
 * DoubleClicking on an item from the list.<br>
 * <br>
 * This is a basic skeleton which can be extended for paging or additionally
 * textboxes for inserting searchparameters.<br>
 * <br>
 * 
 * <pre>
 * call: Branch branch = BranchSimpleSearchListBox.show(parentComponent);
 * </pre>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class BranchSimpleSearchListBox extends Window implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BranchSimpleSearchListBox.class);

	private Listbox listbox;
	// the windows title
	private String _title = Labels.getLabel("message.Information.SimpleSearch") + " / " + Labels.getLabel("common.Branch");
	// 1. Listheader
	private String _listHeader1 = Labels.getLabel("common.Description");
	// the windows height
	private int _height = 400;
	// the windows width
	private int _width = 300;

	// the returned bean object
	private Branche branche = null;

	// The service from which we get the data
	private BrancheService brancheService;

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Branche show(Component parent) {
		return new BranchSimpleSearchListBox(parent).getBranche();
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private BranchSimpleSearchListBox(Component parent) {
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
		south.setHeight("26px");
		south.setParent(bl);

		// Button
		Button btnOK = new Button();
		btnOK.setLabel("OK");
		btnOK.addEventListener("onClick", new OnCloseListener());
		btnOK.setParent(south);

		// Listbox
		listbox = new Listbox();
		listbox.setStyle("border: none;");
		listbox.setHeight("100%");
		listbox.setVisible(true);
		listbox.setParent(center);
		listbox.setItemRenderer(new SearchBoxItemRenderer());

		Listhead listhead = new Listhead();
		listhead.setParent(listbox);
		Listheader listheader = new Listheader();
		listheader.setSclass("FDListBoxHeader1");
		listheader.setParent(listhead);
		listheader.setLabel(_listHeader1);

		// Model
		listbox.setModel(new ListModelList(getBrancheService().getAllBranches()));

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

			Branche branche = (Branche) data;

			Listcell lc = new Listcell(branche.getBraBezeichnung());
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
			Branche branche = (Branche) li.getAttribute("data");

			setBranche(branche);
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
				Branche branche = (Branche) li.getAttribute("data");

				setBranche(branche);
			}
			onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public Branche getBranche() {
		return branche;
	}

	private void setBranche(Branche branche) {
		this.branche = branche;
	}

	public BrancheService getBrancheService() {
		if (brancheService == null) {
			brancheService = (BrancheService) SpringUtil.getBean("brancheService");
		}
		return brancheService;
	}

	public void setBrancheService(BrancheService brancheService) {
		this.brancheService = brancheService;
	}

}
