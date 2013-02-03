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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.policy.model.UserImpl;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can input some text. By onClosing with <RETURN> or Button <send> this
 * InputConfirmBox can return the message as a String value if not empty. <br>
 * In this case the returnValue is the same as the inputValue.<br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class InputMessageTextBox extends Window {

	private static final long serialVersionUID = 8109634704496621100L;
	private static final Logger logger = Logger.getLogger(InputMessageTextBox.class);

	private final Textbox textbox;
	private String msg = "";
	private String userName;

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @param anQuestion
	 *            The question that's to be confirmed.
	 * @return String from the input textbox.
	 */
	public static String show(Component parent) {
		return new InputMessageTextBox(parent).getMsg();
	}

	/**
	 * private constructor. So it can only be created with the static show()
	 * method.
	 * 
	 * @param parent
	 * @param anQuestion
	 */
	private InputMessageTextBox(Component parent) {
		super();

		textbox = new Textbox();

		setParent(parent);

		try {
			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		} catch (Exception e) {
			e.printStackTrace();
		}

		createBox();
	}

	private void createBox() {

		setWidth("350px");
		setHeight("150px");
		setTitle(Labels.getLabel("message.Information.PleaseInsertText"));
		setId("confBox");
		setVisible(true);
		setClosable(true);
		addEventListener("onOK", new OnCloseListener());

		// Hbox hbox = new Hbox();
		// hbox.setWidth("100%");
		// hbox.setParent(this);
		// Checkbox cb = new Checkbox();
		// cb.setLabel(Labels.getLabel("common.All"));
		// cb.setChecked(true);

		Separator sp = new Separator();
		sp.setParent(this);

		textbox.setWidth("98%");
		textbox.setHeight("80px");
		textbox.setMultiline(true);
		textbox.setRows(5);
		textbox.setParent(this);

		Separator sp2 = new Separator();
		sp2.setBar(true);
		sp2.setParent(this);

		Button btnSend = new Button();
		btnSend.setLabel(Labels.getLabel("common.Send"));
		btnSend.setParent(this);
		btnSend.addEventListener("onClick", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {

				// Check if empty, than do not send
				if (StringUtils.isEmpty(StringUtils.trim(textbox.getText()))) {
					onClose();
					return;
				}

				msg = msg + ZksampleDateFormat.getDateTimeLongFormater().format(new Date()) + " / " + Labels.getLabel("common.Message.From") + " " + userName + ":" + "\n";
				msg = msg + textbox.getText();
				msg = msg + "\n" + "_____________________________________________________" + "\n";

				onClose();
			}
		});

		try {
			doModal();
		} catch (SuspendNotAllowedException e) {
			logger.fatal("", e);
		} catch (InterruptedException e) {
			logger.fatal("", e);
		}
	}

	final class OnCloseListener implements EventListener {
		@Override
		public void onEvent(Event event) throws Exception {
			onClose();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
