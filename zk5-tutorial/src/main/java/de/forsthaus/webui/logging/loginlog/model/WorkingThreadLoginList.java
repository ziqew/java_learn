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
package de.forsthaus.webui.logging.loginlog.model;

import org.apache.log4j.Logger;
import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import de.forsthaus.backend.service.LoginLoggingService;

/**
 * Worker thread for the server push sample.
 * 
 * @changes 06/03/2010 sge We don't use this code further, because in our spring
 *          managed session we have problems because this WorkerThread cannot
 *          applied to a session.<br>
 *          So we changed the mechanism to work with a timer.<br>
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class WorkingThreadLoginList extends Thread {

	private static Logger logger = Logger.getLogger(WorkingThreadLoginList.class);

	private final Desktop _desktop;
	private final Listbox _listBox;
	private final LoginLoggingService _service;

	private int refreshTime = 4000; // 4 seconds
	private boolean _ceased;

	private int i = 0;

	/**
	 * Constructor. <br>
	 * 
	 * @param listBox
	 * @param service
	 *            ServiceDAO
	 */
	public WorkingThreadLoginList(Listbox listBox, LoginLoggingService service) {
		_desktop = listBox.getDesktop();
		_listBox = listBox;
		_service = service;
	}

	public void run() {

		if (!_desktop.isServerPushEnabled()) {
			_desktop.enableServerPush(true);
		}

		/**
		 * We must check if the window is already seen or if the user has
		 * changed the page/close the browser and don't stops the thread.
		 */
		if (_listBox == null) {
			logger.debug("The window for showing the list is no longer exist! ");
			this.interrupt();
		}

		try {

			while (!_ceased) {

				Executions.activate(_desktop);

				try {
					i = i + 1;

					if (i % 2 == 0) {
						updateList(); // do something
					} else {
						updateList2(); // do something others
					}
				} finally {
					Executions.deactivate(_desktop);
				}
				Threads.sleep(refreshTime); // update each xx seconds
			}

		} catch (DesktopUnavailableException e) {
			logger.debug(e);
			Executions.deactivate(_desktop);
			this.setDone();

		} catch (InterruptedException e) {
			logger.debug(e);
			this.setDone();
		} finally {
			if (_desktop.isServerPushEnabled()) {
				_desktop.enableServerPush(false);
				Executions.deactivate(_desktop);
			}
		}

	}

	public void updateList() {
		_listBox.setModel(new ListModelList(_service.getAllLogsServerPushForSuccess()));
	}

	public void updateList2() {
		_listBox.setModel(new ListModelList(_service.getAllLogsServerPushForFailed()));
	}

	public void setDone() {
		_ceased = true;
	}

}
