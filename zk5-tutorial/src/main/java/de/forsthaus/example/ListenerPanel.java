package de.forsthaus.example;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Textbox;

public class ListenerPanel extends Panel implements AfterCompose {

	private static final long serialVersionUID = 1L;

	private Textbox tbName; // auto-wired
	private Button btnOK; // auto-wired

	@Override
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);

	}

	public void onClick$btnOK(Event event) throws InterruptedException {
		System.out.println("You pressed OK.");
	}

	public void onBlur$tbName(Event event) throws InterruptedException {

		if (Messagebox.show("Please confirm", "", Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION, new EventListener() {
			@Override
			public void onEvent(Event evt) {
				switch (((Integer) evt.getData()).intValue()) {
				case Messagebox.OK:
					System.out.println("Confirmed");
					break;
				case Messagebox.CANCEL:
					System.out.println("Not confirmed");
					break;
				default:
					System.out.println("Something went wrong");
				}
			}
		}

		) == Messagebox.YES) {
		}

	}

	// public void onBlur$tbName(Event event) throws InterruptedException {
	//
	// int result = Messagebox.show("Please confirm", "", Messagebox.OK |
	// Messagebox.CANCEL, Messagebox.INFORMATION);
	//
	//       
	// switch (result) {
	// case Messagebox.OK:
	// System.out.println("Confirmed");
	// break;
	// case Messagebox.CANCEL:
	// System.out.println("Not confirmed");
	// break;
	// default:
	// System.out.println("Something went wrong");
	// }
	//
	// }
}