package de.forsthaus.example;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class PiggyBackMessageDemo extends Div {

	private static final long serialVersionUID = 1L;

	static volatile String message = null;

	public PiggyBackMessageDemo() {
		addEventListener(Events.ON_PIGGYBACK, new EventListener() {
			public void onEvent(Event evt) throws Exception {
				if (message != null && !message.equals(getAttribute("message"))) {
					setAttribute("message", message);
					Messagebox.show(message);
				}
			}
		});

		final Textbox messageBox = new Textbox();
		Button sendMessage = new Button("Send message");
		sendMessage.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				message = messageBox.getText();
			}
		});

		Button readMessage = new Button("Read Message");
		readMessage.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event evt) throws Exception {
				// this is just an example of user interacting with the
				// application
			}
		});

		appendChild(messageBox);
		appendChild(sendMessage);
		appendChild(readMessage);
	}
}