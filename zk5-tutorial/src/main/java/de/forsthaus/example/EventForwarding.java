package de.forsthaus.example;

import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;

public class EventForwarding extends GenericForwardComposer {

	private static final long serialVersionUID = -2954698143188923918L;

	private Label label; // autowired

	public void onCommonBlurMethod(ForwardEvent event) {

		String compID = event.getOrigin().getTarget().getId();

		if (compID == "txtb_1") {
			// Do your logic here
		} else if (compID == "txtb_2") {
			// Do your logic here
		} else if (compID == "txtb_2") {
			// Do your logic here
		}

		label.setValue("onBlur event on textbox with ID: " + compID);
	}
}