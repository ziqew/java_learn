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
package de.forsthaus.util;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.ComponentSerializationListener;
import org.zkoss.zk.ui.util.EventInterceptor;

/**
 * Test class.
 * 
 * @author sgerth
 * 
 */
public class MySerializationListener implements EventListener, EventInterceptor, java.io.Serializable,
		ComponentSerializationListener {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(MySerializationListener.class);

	public MySerializationListener() {
	}

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		logger.info("onEvent 1. --> : " + event.getTarget().getId());
		logger.info("onEvent 2. --> : " + event.toString());

	}

	@Override
	public void didDeserialize(Component comp) {
		logger.info("didSerialize --> : " + comp.getId());
		logger.info("didSerialize --> : " + comp.toString());
	}

	// ComponentSerializationListener//
	@Override
	public void willSerialize(Component comp) {
		logger.info("willSerialize --> : " + comp.getId());
		logger.info("willSerialize --> : " + comp.toString());

	}

	@Override
	public void afterProcessEvent(Event event) {
		logger.info("onEvent --> : " + event.getTarget().getId());
		logger.info("onEvent --> : " + event.toString());

	}

	@SuppressWarnings("unchecked")
	@Override
	public Event beforePostEvent(Event event) {
		logger.info("beforePostEvent 1. --> : " + event.getTarget().getId());
		logger.info("beforePostEvent 2. --> : " + event.toString());
		logger.info("beforePostEvent 3. --> : " + event.getTarget().getDesktop().getSession().toString());

		final Map<String, ?> map = event.getTarget().getDesktop().getSession().getAttributes();

		int i = 1;
		for (final String str : map.keySet()) {
			logger.info("Object Nr.: " + i++ + " / " + str);
		}
		return null;
	}

	@Override
	public Event beforeProcessEvent(Event event) {
		logger.info("onEvent --> : " + event.getTarget().getId());
		logger.info("onEvent --> : " + event.toString());
		return null;
	}

	@Override
	public Event beforeSendEvent(Event event) {
		logger.info("onEvent --> : " + event.getTarget().getId());
		logger.info("onEvent --> : " + event.toString());
		return null;
	}

}
