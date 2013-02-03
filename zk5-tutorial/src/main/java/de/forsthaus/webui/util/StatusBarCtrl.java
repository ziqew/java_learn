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
package de.forsthaus.webui.util;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Window;

/**
 * =======================================================================<br>
 * StatusBarController. <br>
 * =======================================================================<br>
 * Works with the EventQueues mechanism of zk. ALl needed components are created
 * in this class. In the zul-template declare only this controller with 'apply'
 * to a winStatusBar window component.<br>
 * 2
 * Declaration in the zul-file:<br>
 * 
 * <pre>
 * < borderlayout >
 *   . . .
 *    < !-- STATUS BAR AREA -- >
 *    < south id="south" border="none" margins="1,0,0,0"
 * 		height="20px" splittable="false" flex="true" >
 * 	      < div id="divSouth" >
 * 
 *          < !-- The StatusBar. Comps are created in the Controller -- >
 *          < window id="winStatusBar" apply="${statusBarCtrl}"
 *                   border="none" width="100%" height="100%" />
 * 
 *        < /div >
 *    < /south >
 *  < /borderlayout >
 * </pre>
 * 
 * call in java to actualize a columns label:
 * 
 * <pre>
 * EventQueues.lookup(&quot;userNameEventQueue&quot;, EventQueues.DESKTOP, true).publish(new Event(&quot;onChangeSelectedObject&quot;, null, &quot;new Value&quot;));
 * </pre>
 * 
 * Spring bean declaration:
 * 
 * <pre>
 * < !-- StatusBarController -->
 * < bean id="statusBarCtrl" class="de.forsthaus.webui.util.StatusBarCtrl"
 *    scope="prototype">
 * < /bean>
 * </pre>
 * 
 * since: zk 5.0.0
 * 
 * @author sgerth
 * 
 */
public class StatusBarCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(StatusBarCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window winStatusBar; // autowired

	// Used Columns
	private Column statusBarSelectedObject;
	private Column statusBarAppVersion;
	private Column statusBarTableSchema;

	// Localized labels for the columns
	private final String _labelSelectedObject = Labels.getLabel("common.SelectedSign") + " ";
	private final String _labelAppVersion = "";
	private final String _labelTableSchema = Labels.getLabel("common.TableSchema") + ": ";

	/**
	 * Default constructor.
	 */
	public StatusBarCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		// Listener for selected Record
		EventQueues.lookup("selectedObjectEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				StatusBarCtrl.this.statusBarSelectedObject.setLabel(StatusBarCtrl.this._labelSelectedObject + msg);
			}
		});

		// Listener for applicationVersion
		EventQueues.lookup("appVersionEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				StatusBarCtrl.this.statusBarAppVersion.setLabel(StatusBarCtrl.this._labelAppVersion + msg);
			}
		});

		// Listener for TableSchemaName
		EventQueues.lookup("tableSchemaEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();
				StatusBarCtrl.this.statusBarTableSchema.setLabel(StatusBarCtrl.this._labelTableSchema + msg);
			}
		});

	}

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 */
	public void onCreate$winStatusBar(Event event) {

		final Grid grid = new Grid();
		grid.setHeight("22px");
		grid.setStyle("padding: 0px;");
		grid.setParent(this.winStatusBar);

		final Columns columns = new Columns();
		columns.setSizable(false);
		columns.setParent(grid);

		this.statusBarSelectedObject = new Column();
		this.statusBarSelectedObject.setHeight("22px");
		this.statusBarSelectedObject.setLabel(this._labelSelectedObject);
		this.statusBarSelectedObject.setWidth("50%");
		this.statusBarSelectedObject.setStyle("background-color: #D6DCDE; color: blue;");
		this.statusBarSelectedObject.setParent(columns);

		this.statusBarAppVersion = new Column();
		this.statusBarAppVersion.setHeight("22px");
		this.statusBarAppVersion.setLabel(this._labelAppVersion);
		this.statusBarAppVersion.setWidth("35%");
		this.statusBarAppVersion.setStyle("background-color: #D6DCDE; color: #FF0000;");
		this.statusBarAppVersion.setParent(columns);

		this.statusBarTableSchema = new Column();
		this.statusBarTableSchema.setHeight("22px");
		this.statusBarTableSchema.setLabel(this._labelTableSchema);
		this.statusBarTableSchema.setWidth("15%");
		this.statusBarTableSchema.setStyle("background-color: #D6DCDE; color: blue;");
		this.statusBarTableSchema.setParent(columns);

	}
}
