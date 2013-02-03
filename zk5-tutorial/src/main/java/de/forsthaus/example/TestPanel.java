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
package de.forsthaus.example;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;

/**
 * Test Panel.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class TestPanel extends Panel {

	private static final long serialVersionUID = 5034368329257021793L;

	public TestPanel() {
		super();
		createPanel();
	}

	private void createPanel() {

		this.setTitle("Hello World!!");
		this.setWidth("200px");
		this.setVisible(true);

		Panelchildren pc = new Panelchildren();
		pc.setParent(this);

		Label l = new Label();
		l.setValue("You are using ...");
		l.setParent(pc);

		Button btn = new Button();
		btn.addEventListener("onClick", new EventListener() {

			public void onEvent(Event event) throws Exception {
				Messagebox.show("I'm pressed");
			}
		});

		btn.setLabel("say hello");
		btn.setParent(pc);

	}

}
