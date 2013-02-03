package de.forsthaus.webui.util.test;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

public class WPanel extends Groupbox {
	private static final long serialVersionUID = -2836650146728077421L;

	public WPanel() {
		createPanel();
	}

	private void createPanel() {
		final Hbox box = new Hbox();

		this.addEventListener(Events.ON_CLICK, this.eventHrsOnChange);

		final Label label = new Label("This Product have no description.");
		box.appendChild(label);
		this.appendChild(box);
	}

	private final EventListener eventHrsOnChange = new EventListener() {
		@Override
		public void onEvent(Event evt) {
			try {
				Messagebox.show("TEST");
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	};
}