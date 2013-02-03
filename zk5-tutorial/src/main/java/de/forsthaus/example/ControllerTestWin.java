package de.forsthaus.example;

import java.util.HashMap;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/** Controller class for test_tab_sequence.zul page */

public class ControllerTestWin extends GenericForwardComposer {

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
	protected Window testWin; // autowired
	protected Listbox detailLstBox; // autowired
	protected Button editBtn; // autowired

	/** Constructor */
	public ControllerTestWin() {
	}// end constructor

	// ======================Event Handlers===================

	/**
	 * when you click the Edit Record button
	 * 
	 * @throws InterruptedException
	 */
	public void onClick$editBtn(Event event) throws InterruptedException {

		if (detailLstBox.getSelectedItem() != null) {

			String str = (String) detailLstBox.getSelectedItem().getValue();

			// Map that holds params that we need in the new created Window
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("mySelectedItem", str);

			// create the Window and overhanded a map with params
			Executions.createComponents("/test/test_tab_sequenceModal.zul", testWin, paramMap);
		} else {
			Messagebox.show("Please select a row first.");
		}
	}// end method

	/** when you click the Save Record button on the Edit Sub-Window */
	public void onClick$saveEditBtn(Event event) throws InterruptedException {
		// Some action
	}// end method

}// end class
