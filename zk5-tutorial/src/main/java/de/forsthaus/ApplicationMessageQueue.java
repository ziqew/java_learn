/**
 * Copyright (C) 2010 - 2012 Forsthaus IT Consulting GbR.
 * 
 * This file is part of openTruuls™. http://www.opentruuls.org/
 *
 * openTruuls™ community edition is free software: 
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 * If you need a commercial license please write us under info@opentruuls.org
 */
package de.forsthaus;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.SerializableEventListener;

import fi.jawsy.jawwa.zk.gritter.Gritter;

/**
 * Application Wide MessageQueue Listener.<br>
 * <br>
 * This class can be firstly started in an zk event.
 * 
 * reachable with:<br>
 * EventQueues.lookup("ApplicationEventQueue", EventQueues.APPLICATION,
 * true).publish(new Event("APPLICATION_NOTIFICATION", null, map));
 * 
 * called from IndexCtrl.java<br>
 * 
 * @author Stephan Gerth
 */
public class ApplicationMessageQueue implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient final static Logger logger = Logger.getLogger(ApplicationMessageQueue.class);

	/**
	 * Constructor.
	 */
	public ApplicationMessageQueue() {
		createApplicationMessageQueue();
	}

	/**
	 * Creates the application scoped message queue.
	 */
	private void createApplicationMessageQueue() {

		if (!EventQueues.exists("ApplicationEventQueue", EventQueues.APPLICATION)) {

			// autoCreate EventQueue
			EventQueue eq = EventQueues.lookup("ApplicationEventQueue", EventQueues.APPLICATION, true);

			// listen
			eq.subscribe(new SerializableEventListener() {
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public void onEvent(Event event) throws Exception {
					System.out.println("ApplicationEventQueue onEvent");

					if (event.getName().equals("APPLICATION_NOTIFICATION")) {
						Map<String, Object> map = (Map<String, Object>) event.getData();
						showNotification(map);
					}
				}
			});
		} else {
			// Get the EventQueue
			EventQueue eq = EventQueues.lookup("ApplicationEventQueue", EventQueues.APPLICATION, false);

			// NEW listen
			eq.subscribe(new SerializableEventListener() {
				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public void onEvent(Event event) throws Exception {

					if (event.getName().equals("APPLICATION_NOTIFICATION")) {
						Map<String, Object> map = (Map<String, Object>) event.getData();
						showNotification(map);
					}
				}
			});
		}

	}

	/**
	 * Shows the notification. <br>
	 * params: <br>
	 * - title (String)| the title for the notification.<br>
	 * - message (String) | the message for showing.<br>
	 * - image (String) | an image (60x60px optional)<br>
	 * - autoClosing (boolean) | true closes the notification after delay time.<br>
	 * - delayTime (int) | the time for showing (1000 = 1 sec; default = 6000).<br>
	 * <br>
	 * 
	 * @param map
	 */
	private void showNotification(Map<String, Object> map) {
		// init
		String title = "title is missing";
		String message = "msg is missing";
		String image = null;
		boolean autoClosing = false;
		int delayTime = new Integer(6000);

		if (map.containsKey("title"))
			title = (String) map.get("title");
		if (map.containsKey("message"))
			message = (String) map.get("message");
		if (map.containsKey("image"))
			image = (String) map.get("image");
		if (map.containsKey("autoClosing"))
			autoClosing = (Boolean) map.get("autoClosing");
		if (map.containsKey("delayTime"))
			delayTime = (Integer) map.get("delayTime");

		// show notification
		if (image != null)
			Gritter.notification().withTitle(title).withText(message).withSticky(autoClosing).withTime(delayTime).withSclass("gritter-red").withImage(image).show();
		else
			Gritter.notification().withTitle(title).withText(message).withSticky(autoClosing).withTime(delayTime).withSclass("gritter-red").show();

	}
}
