package de.forsthaus.example;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/** Controller class for test_tab_sequenceModal.zul page */

public class ControllerTestWinModal extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 1. All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by the
	 * GenericForwardComposer. Means you can acces them directly by their ID. <br>
	 * 2. You can call their events bei naming conventions. i.e.<br>
	 * EventName|Seperator|ComponentId <br>
	 * onClick$saveBtn(Event event){your code here}
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window subEditWin; // autowired
	protected Textbox movieNameTxtBox; // autowired
	protected Button saveEditBtn; // autowired
	protected Button closeSubWinBtn; // autowired

	private String movieTitle;

	/** Constructor */
	public ControllerTestWinModal() {
	}// end constructor

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		// Read back the overhanded param from the map.
		if (this.arg.containsKey("mySelectedItem")) {
			setMovieTitle((String) this.arg.get("mySelectedItem"));
		} else {
			setMovieTitle(null);
		}
	}

	// ======================Event Handlers ====================

	public void onCreate$subEditWin(Event event) throws SuspendNotAllowedException, InterruptedException {

		movieNameTxtBox.setValue(getMovieTitle()); // set the value
		movieNameTxtBox.setFocus(true); // set the focus

		subEditWin.doModal(); // show window in modal-mode
	}

	/** when you click the Save Record button on the Edit Sub-Window */
	public void onClick$saveEditBtn(Event event) throws InterruptedException {
		// Some action
	}// end method

	/** when you click the Close Window button on the Edit Sub-Window */
	public void onClick$closeSubWinBtn(Event event) throws InterruptedException {
		subEditWin.onClose();
	}// end method

	// ===================== Setter / Getter ====================

	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}

	public String getMovieTitle() {
		return movieTitle;
	}

}// end class
